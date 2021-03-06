/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.nio.ch;

import java.io.*;
import java.net.*;
import java.nio.channels.*;


// Make a server-socket channel look like a server socket.
//
// The methods in this class are defined in exactly the same order as in
// java.net.ServerSocket so as to simplify tracking future changes to that
// class.
//

public class ServerSocketAdaptor			// package-private
    extends ServerSocket
{

    // The channel being adapted
    private final ServerSocketChannelImpl ssc;

    // Option adaptor object, created on demand
    private volatile OptionAdaptor opts = null;

    // Timeout "option" value for accepts
    private volatile int timeout = 0;

    public static ServerSocket create(ServerSocketChannelImpl ssc) {
	try {
	    return new ServerSocketAdaptor(ssc);
	} catch (IOException x) {
	    throw new Error(x);
	}
    }

    // ## super will create a useless impl
    private ServerSocketAdaptor(ServerSocketChannelImpl ssc)
	throws IOException
    {
	this.ssc = ssc;
    }


    public void bind(SocketAddress local) throws IOException {
	bind(local, 50);
    }

    public void bind(SocketAddress local, int backlog) throws IOException {
	if (local == null)
	    local = new InetSocketAddress(0);
	try {
	    ssc.bind(local, backlog);
	} catch (Exception x) {
	    Net.translateException(x);
	}
    }

    public InetAddress getInetAddress() {
	if (!ssc.isBound())
	    return null;
	return Net.asInetSocketAddress(ssc.localAddress()).getAddress();
    }

    public int getLocalPort() {
	if (!ssc.isBound())
	    return -1;
	return Net.asInetSocketAddress(ssc.localAddress()).getPort();
    }


    public Socket accept() throws IOException {
	synchronized (ssc.blockingLock()) {
	    if (!ssc.isBound())
		throw new IllegalBlockingModeException();
	    try {
		if (timeout == 0) {
		    SocketChannel sc = ssc.accept();
		    if (sc == null && !ssc.isBlocking())
			throw new IllegalBlockingModeException();
		    return sc.socket();
		}

		// Implement timeout with a selector
		SelectionKey sk = null;
		Selector sel = null;
		ssc.configureBlocking(false);
		try {
		    SocketChannel sc;
		    if ((sc = ssc.accept()) != null)
			return sc.socket();
		    sel = Util.getTemporarySelector(ssc);
		    sk = ssc.register(sel, SelectionKey.OP_ACCEPT);
		    long to = timeout;
		    for (;;) {
                        if (!ssc.isOpen()) 
			    throw new ClosedChannelException();
			long st = System.currentTimeMillis();
			int ns = sel.select(to);
			if (ns > 0 &&
			    sk.isAcceptable() && ((sc = ssc.accept()) != null))
			    return sc.socket();
			sel.selectedKeys().remove(sk);
			to -= System.currentTimeMillis() - st;
			if (to <= 0)
			    throw new SocketTimeoutException();
		    }
		} finally {
		    if (sk != null)
			sk.cancel();
		    if (ssc.isOpen())
			ssc.configureBlocking(true);
		    if (sel != null)
                        Util.releaseTemporarySelector(sel);
		}

	    } catch (Exception x) {
		Net.translateException(x);
		assert false;
		return null;		// Never happens
	    }
	}
    }

    public void close() throws IOException {
	try {
	    ssc.close();
	} catch (Exception x) {
	    Net.translateException(x);
	}
    }

    public ServerSocketChannel getChannel() {
	return ssc;
    }

    public boolean isBound() {
	return ssc.isBound();
    }

    public boolean isClosed() {
	return !ssc.isOpen();
    }

    public void setSoTimeout(int timeout) throws SocketException {
	this.timeout = timeout;
    }

    public int getSoTimeout() throws SocketException {
	return timeout;
    }

    private OptionAdaptor opts() {
	if (opts == null)
	    opts = new OptionAdaptor(ssc);
	return opts;
    }

    public void setReuseAddress(boolean on) throws SocketException {
	opts().setReuseAddress(on);
    }

    public boolean getReuseAddress() throws SocketException {
	return opts().getReuseAddress();
    }

    public String toString() {
	if (!isBound())
	    return "ServerSocket[unbound]";
	return "ServerSocket[addr=" + getInetAddress() + 
	    //		",port=" + getPort() + 
		",localport=" + getLocalPort()  + "]";
    }

    public void setReceiveBufferSize(int size) throws SocketException {
	opts().setReceiveBufferSize(size);
    }

    public int getReceiveBufferSize() throws SocketException {
	return opts().getReceiveBufferSize();
    }

}
