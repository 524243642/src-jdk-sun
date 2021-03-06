/*
 * %W% %E% * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package sun.nio.cs.ext;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import sun.nio.cs.HistoricallyNamedCharset;

public class MS932 extends Charset implements HistoricallyNamedCharset
{
    public MS932() {
	super("windows-31j", ExtendedCharsets.aliasesFor("windows-31j"));
    }

    public String historicalName() {
	return "MS932";
    }

    public boolean contains(Charset cs) {
	return ((cs.name().equals("US-ASCII"))
		|| (cs instanceof JIS_X_0201)
		|| (cs instanceof MS932));
    }

    public CharsetDecoder newDecoder() {
	return new Decoder(this);
    }

    public CharsetEncoder newEncoder() {
	return new Encoder(this);
    }

    private static class Decoder extends MS932DB.Decoder
	implements DelegatableDecoder {

	JIS_X_0201.Decoder jisDec0201;

	private Decoder(Charset cs) {
	    super(cs);
	    jisDec0201 = new JIS_X_0201.Decoder(cs);
	}

	protected char decodeSingle(int b) {
	    // If the high bits are all off, it's ASCII == Unicode
	    if ((b & 0xFF80) == 0) {
		return (char)b;
	    }
	    return jisDec0201.decode(b);
	}

	// Make some protected methods public for use by JISAutoDetect
	public CoderResult decodeLoop(ByteBuffer src, CharBuffer dst) {
 	    return super.decodeLoop(src, dst);
 	}
 	public void implReset() {
	    super.implReset();
	}
 	public CoderResult implFlush(CharBuffer out) {
 	    return super.implFlush(out);
 	}
    }

    private static class Encoder extends MS932DB.Encoder {

	private JIS_X_0201.Encoder jisEnc0201; 

	
	private Encoder(Charset cs) {
	    super(cs);
	    jisEnc0201 = new JIS_X_0201.Encoder(cs);
	}

	protected int encodeSingle(char inputChar) {

	    byte b;
	    // \u0000 - \u007F map straight through
	    if ((inputChar & 0xFF80) == 0) {
		return ((byte)inputChar);
	    }

	    if ((b = jisEnc0201.encode(inputChar)) == 0)
		return -1;
	    else
		return b;
	}
    }
}
