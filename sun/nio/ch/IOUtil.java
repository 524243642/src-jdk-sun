/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * File-descriptor based I/O utilities that are shared by NIO classes.
 */

class IOUtil {

    private IOUtil() { }                // No instantiation

    static int write(FileDescriptor fd, ByteBuffer src, long position,
                     NativeDispatcher nd, Object lock)
        throws IOException
    {
        if (src instanceof DirectBuffer)
            return writeFromNativeBuffer(fd, src, position, nd, lock);

        // Substitute a native buffer
        int pos = src.position();
        int lim = src.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);
        ByteBuffer bb = null;
        try {
            bb = Util.getTemporaryDirectBuffer(rem);
            bb.put(src);
            bb.flip();
            // Do not update src until we see how many bytes were written
            src.position(pos);

            int n = writeFromNativeBuffer(fd, bb, position, nd, lock);
            if (n > 0) {
                // now update src
                src.position(pos + n);
            }
            return n;
        } finally {
            Util.offerFirstTemporaryDirectBuffer(bb);
        }
    }

    private static int writeFromNativeBuffer(FileDescriptor fd, ByteBuffer bb,
                                           long position, NativeDispatcher nd,
                                             Object lock)
        throws IOException
    {
        int pos = bb.position();
        int lim = bb.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);

        int written = 0;
        if (rem == 0)
            return 0;
        if (position != -1) {
            written = nd.pwrite(fd,
                                ((DirectBuffer)bb).address() + pos,
                                rem, position, lock);
        } else {
            written = nd.write(fd, ((DirectBuffer)bb).address() + pos, rem);
        }
        if (written > 0)
            bb.position(pos + written);
        return written;
    }

    static long write(FileDescriptor fd, ByteBuffer[] bufs, NativeDispatcher nd)
        throws IOException
    {
        return write(fd, bufs, 0, bufs.length, nd);
    }

    static long write(FileDescriptor fd, ByteBuffer[] bufs, int offset, int length,
                      NativeDispatcher nd)
        throws IOException
    {
        IOVecWrapper vec = IOVecWrapper.get(length);

        boolean completed = false;
        int iov_len = 0;
        try {

            // Iterate over buffers to populate native iovec array.
            int count = offset + length;
            for (int i=offset; i<count; i++) {
                ByteBuffer buf = bufs[i];
                int pos = buf.position();
                int lim = buf.limit();
                assert (pos <= lim);
                int rem = (pos <= lim ? lim - pos : 0);
                if (rem > 0) {
                    vec.setBuffer(iov_len, buf, pos, rem);

                    // allocate shadow buffer to ensure I/O is done with direct buffer
                    if (!(buf instanceof DirectBuffer)) {
                        ByteBuffer shadow = Util.getTemporaryDirectBuffer(rem);
                        shadow.put(buf);
                        shadow.flip();
                        vec.setShadow(iov_len, shadow);
                        buf.position(pos);  // temporarily restore position in user buffer
                        buf = shadow;
                        pos = shadow.position();
                    }

                    vec.putBase(iov_len, ((DirectBuffer)buf).address() + pos);
                    vec.putLen(iov_len, rem);
                    iov_len++;
                }
            }
            if (iov_len == 0)
                return 0L;

            long bytesWritten = nd.writev(fd, vec.address, iov_len);

            // Notify the buffers how many bytes were taken
            long left = bytesWritten;
            for (int j=0; j<iov_len; j++) {
                if (left > 0) {
                    ByteBuffer buf = vec.getBuffer(j);
                    int pos = vec.getPosition(j);
                    int rem = vec.getRemaining(j);
                    int n = (left > rem) ? rem : (int)left;
                    buf.position(pos + n);
                    left -= n;
                }
                // return shadow buffers to buffer pool
                ByteBuffer shadow = vec.getShadow(j);
                if (shadow != null)
                    Util.offerLastTemporaryDirectBuffer(shadow);
                vec.clearRefs(j);
            }

            completed = true;
            return bytesWritten;

        } finally {
            // if an error occurred then clear refs to buffers and return any shadow
            // buffers to cache
            if (!completed) {
                for (int j=0; j<iov_len; j++) {
                    ByteBuffer shadow = vec.getShadow(j);
                    if (shadow != null)
                        Util.offerLastTemporaryDirectBuffer(shadow);
                    vec.clearRefs(j);
                }
            }
        }
    }

    static int read(FileDescriptor fd, ByteBuffer dst, long position,
                    NativeDispatcher nd, Object lock)
        throws IOException
    {
        if (dst.isReadOnly())
            throw new IllegalArgumentException("Read-only buffer");
        if (dst instanceof DirectBuffer)
            return readIntoNativeBuffer(fd, dst, position, nd, lock);

        // Substitute a native buffer
        ByteBuffer bb = null;
        try {
            bb = Util.getTemporaryDirectBuffer(dst.remaining());
            int n = readIntoNativeBuffer(fd, bb, position, nd, lock);
            bb.flip();
            if (n > 0)
                dst.put(bb);
            return n;
        } finally {
            Util.offerFirstTemporaryDirectBuffer(bb);
        }
    }

    private static int readIntoNativeBuffer(FileDescriptor fd, ByteBuffer bb,
                                            long position, NativeDispatcher nd,
                                            Object lock)
        throws IOException
    {
        int pos = bb.position();
        int lim = bb.limit();
        assert (pos <= lim);
        int rem = (pos <= lim ? lim - pos : 0);

        if (rem == 0)
            return 0;
        int n = 0;
        if (position != -1) {
            n = nd.pread(fd, ((DirectBuffer)bb).address() + pos,
                         rem, position, lock);
        } else {
            n = nd.read(fd, ((DirectBuffer)bb).address() + pos, rem);
        }
        if (n > 0)
            bb.position(pos + n);
        return n;
    }

    static long read(FileDescriptor fd, ByteBuffer[] bufs, NativeDispatcher nd)
        throws IOException
    {
        return read(fd, bufs, 0, bufs.length, nd);
    }

    static long read(FileDescriptor fd, ByteBuffer[] bufs, int offset, int length,
                     NativeDispatcher nd)
        throws IOException
    {
        IOVecWrapper vec = IOVecWrapper.get(length);

        boolean completed = false;
        int iov_len = 0;
        try {

            // Iterate over buffers to populate native iovec array.
            int count = offset + length;
            for (int i=offset; i<count; i++) {
                ByteBuffer buf = bufs[i];
                if (buf.isReadOnly())
                    throw new IllegalArgumentException("Read-only buffer");
                int pos = buf.position();
                int lim = buf.limit();
                assert (pos <= lim);
                int rem = (pos <= lim ? lim - pos : 0);

                if (rem > 0) {
                    vec.setBuffer(iov_len, buf, pos, rem);

                    // allocate shadow buffer to ensure I/O is done with direct buffer
                    if (!(buf instanceof DirectBuffer)) {
                        ByteBuffer shadow = Util.getTemporaryDirectBuffer(rem);
                        vec.setShadow(iov_len, shadow);
                        buf = shadow;
                        pos = shadow.position();
                    }

                    vec.putBase(iov_len, ((DirectBuffer)buf).address() + pos);
                    vec.putLen(iov_len, rem);
                    iov_len++;
                }
            }
            if (iov_len == 0)
                return 0L;

            long bytesRead = nd.readv(fd, vec.address, iov_len);

            // Notify the buffers how many bytes were read
            long left = bytesRead;
            for (int j=0; j<iov_len; j++) {
                ByteBuffer shadow = vec.getShadow(j);
                if (left > 0) {
                    ByteBuffer buf = vec.getBuffer(j);
                    int rem = vec.getRemaining(j);
                    int n = (left > rem) ? rem : (int)left;
                    if (shadow == null) {
                        int pos = vec.getPosition(j);
                        buf.position(pos + n);
                    } else {
                        shadow.limit(shadow.position() + n);
                        buf.put(shadow);
                    }
                    left -= n;
                }
                if (shadow != null)
                    Util.offerLastTemporaryDirectBuffer(shadow);
                vec.clearRefs(j);
            }

            completed = true;
            return bytesRead;

        } finally {
            // if an error occurred then clear refs to buffers and return any shadow
            // buffers to cache
            if (!completed) {
                for (int j=0; j<iov_len; j++) {
                    ByteBuffer shadow = vec.getShadow(j);
                    if (shadow != null)
                        Util.offerLastTemporaryDirectBuffer(shadow);
                    vec.clearRefs(j);
                }
            }
        }
    }

    static FileDescriptor newFD(int i) {
        FileDescriptor fd = new FileDescriptor();
        setfdVal(fd, i);
        return fd;
    }

    static native boolean randomBytes(byte[] someBytes);

    static native void initPipe(int[] fda, boolean blocking);

    static native boolean drain(int fd) throws IOException;

    static native void configureBlocking(FileDescriptor fd, boolean blocking)
        throws IOException;

    static native int fdVal(FileDescriptor fd);

    static native void setfdVal(FileDescriptor fd, int value);

    static native void initIDs();

    static {
        // Note that IOUtil.initIDs is called from within Util.load.
        Util.load();
    }

}
