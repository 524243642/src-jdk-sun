/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * %W% %E%
 */

package sun.nio.cs.ext;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharacterCodingException;
import sun.nio.cs.HistoricallyNamedCharset;

public class IBM943C extends Charset implements HistoricallyNamedCharset
{

    public IBM943C() {
	super("x-IBM943C", ExtendedCharsets.aliasesFor("x-IBM943C"));
    }

    public String historicalName() {
	return "Cp943C";
    }

    public boolean contains(Charset cs) {
	return ((cs.name().equals("US-ASCII"))
		|| (cs instanceof IBM943C));
    }

    public CharsetDecoder newDecoder() {
	return new Decoder(this);
    }

    public CharsetEncoder newEncoder() {
	return new Encoder(this);
    }

    private static class Decoder extends IBM943.Decoder {
        protected static final String singleByteToChar;

	static {
	  String indexs = "";
	  for (char c = '\0'; c < '\u0080'; ++c) indexs += c;
	      singleByteToChar = indexs +
				 IBM943.Decoder.singleByteToChar.substring(indexs.length());
	}

	public Decoder(Charset cs) {
	    super(cs, singleByteToChar);
	}
    }

    private static class Encoder extends IBM943.Encoder {

	protected static final short index1[];
	protected static final String index2a;
	protected static final int shift = 6;

	static {
	    String indexs = "";
	    for (char c = '\0'; c < '\u0080'; ++c) indexs += c;
		index2a = IBM943.Encoder.index2a + indexs;

	    int o = IBM943.Encoder.index2a.length() + 15000;
	    index1 = new short[IBM943.Encoder.index1.length];
	    System.arraycopy(IBM943.Encoder.index1,
			     0,
			     index1,
			     0,
			     IBM943.Encoder.index1.length);

	    for (int i = 0; i * (1<<shift) < 128; ++i) {
		index1[i] = (short)(o + i * (1<<shift));
	    }
	}

	public Encoder(Charset cs) {
	    super(cs, index1, index2a);
	}
    }
}
