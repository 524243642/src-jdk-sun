/*
 * %W%	%E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.nio.cs;


import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import sun.nio.cs.StandardCharsets;
import sun.nio.cs.SingleByteDecoder;
import sun.nio.cs.SingleByteEncoder;
import sun.nio.cs.HistoricallyNamedCharset;

public class IBM737
    extends Charset
    implements HistoricallyNamedCharset
{

    public IBM737() {
	super("x-IBM737", StandardCharsets.aliases_IBM737);
    }

    public String historicalName() {
	return "Cp737";
    }

    public boolean contains(Charset cs) {
	return (cs instanceof IBM737);
    }

    public CharsetDecoder newDecoder() {
	return new Decoder(this);
    }

    public CharsetEncoder newEncoder() {
	return new Encoder(this);
    }


    /**
     * These accessors are temporarily supplied while sun.io
     * converters co-exist with the sun.nio.cs.{ext} charset coders
     * These facilitate sharing of conversion tables between the
     * two co-existing implementations. When sun.io converters
     * are made extinct these will be unncessary and should be removed
     */

    public String getDecoderSingleByteMappings() {
	return Decoder.byteToCharTable;

    }

    public short[] getEncoderIndex1() {
	return Encoder.index1;

    }
    public String getEncoderIndex2() {
	return Encoder.index2;

    }

    private static class Decoder extends SingleByteDecoder {
	    public Decoder(Charset cs) {
		super(cs, byteToCharTable);
	}

	private final static String byteToCharTable =

            "\u0391\u0392\u0393\u0394\u0395\u0396\u0397\u0398" + 	// 0x80 - 0x87
            "\u0399\u039A\u039B\u039C\u039D\u039E\u039F\u03A0" + 	// 0x88 - 0x8F
            "\u03A1\u03A3\u03A4\u03A5\u03A6\u03A7\u03A8\u03A9" + 	// 0x90 - 0x97
            "\u03B1\u03B2\u03B3\u03B4\u03B5\u03B6\u03B7\u03B8" + 	// 0x98 - 0x9F
            "\u03B9\u03BA\u03BB\u03BC\u03BD\u03BE\u03BF\u03C0" + 	// 0xA0 - 0xA7
            "\u03C1\u03C3\u03C2\u03C4\u03C5\u03C6\u03C7\u03C8" + 	// 0xA8 - 0xAF
            "\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556" + 	// 0xB0 - 0xB7
            "\u2555\u2563\u2551\u2557\u255D\u255C\u255B\u2510" + 	// 0xB8 - 0xBF
            "\u2514\u2534\u252C\u251C\u2500\u253C\u255E\u255F" + 	// 0xC0 - 0xC7
            "\u255A\u2554\u2569\u2566\u2560\u2550\u256C\u2567" + 	// 0xC8 - 0xCF
            "\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256B" + 	// 0xD0 - 0xD7
            "\u256A\u2518\u250C\u2588\u2584\u258C\u2590\u2580" + 	// 0xD8 - 0xDF
            "\u03C9\u03AC\u03AD\u03AE\u03CA\u03AF\u03CC\u03CD" + 	// 0xE0 - 0xE7
            "\u03CB\u03CE\u0386\u0388\u0389\u038A\u038C\u038E" + 	// 0xE8 - 0xEF
            "\u038F\u00B1\u2265\u2264\u03AA\u03AB\u00F7\u2248" + 	// 0xF0 - 0xF7
            "\u00B0\u2219\u00B7\u221A\u207F\u00B2\u25A0\u00A0" + 	// 0xF8 - 0xFF
            "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007" + 	// 0x00 - 0x07
            "\b\t\n\u000B\f\r\u000E\u000F" + 	// 0x08 - 0x0F
            "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017" + 	// 0x10 - 0x17
            "\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" + 	// 0x18 - 0x1F
            "\u0020\u0021\"\u0023\u0024\u0025\u0026\'" + 	// 0x20 - 0x27
            "\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" + 	// 0x28 - 0x2F
            "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" + 	// 0x30 - 0x37
            "\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" + 	// 0x38 - 0x3F
            "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047" + 	// 0x40 - 0x47
            "\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" + 	// 0x48 - 0x4F
            "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057" + 	// 0x50 - 0x57
            "\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" + 	// 0x58 - 0x5F
            "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067" + 	// 0x60 - 0x67
            "\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" + 	// 0x68 - 0x6F
            "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077" + 	// 0x70 - 0x77
            "\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u007F"; 	// 0x78 - 0x7F

    }

    private static class Encoder extends SingleByteEncoder {
	    public Encoder(Charset cs) {
		super(cs, index1, index2, 0xFF00, 0x00FF, 8);
	    }

	    private final static String index2 =


            "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007" + 
            "\b\t\n\u000B\f\r\u000E\u000F" + 
            "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017" + 
            "\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" + 
            "\u0020\u0021\"\u0023\u0024\u0025\u0026\'" + 
            "\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" + 
            "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" + 
            "\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" + 
            "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047" + 
            "\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" + 
            "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057" + 
            "\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" + 
            "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067" + 
            "\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" + 
            "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077" + 
            "\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u007F" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00FF\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00F8\u00F1\u00FD\u0000\u0000\u0000\u0000\u00FA" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u00F6" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00EA\u0000\u00EB\u00EC\u00ED\u0000\u00EE\u0000" + 
            "\u00EF\u00F0\u0000\u0080\u0081\u0082\u0083\u0084" + 
            "\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C" + 
            "\u008D\u008E\u008F\u0090\u0000\u0091\u0092\u0093" + 
            "\u0094\u0095\u0096\u0097\u00F4\u00F5\u00E1\u00E2" + 
            "\u00E3\u00E5\u0000\u0098\u0099\u009A\u009B\u009C" + 
            "\u009D\u009E\u009F\u00A0\u00A1\u00A2\u00A3\u00A4" + 
            "\u00A5\u00A6\u00A7\u00A8\u00AA\u00A9\u00AB\u00AC" + 
            "\u00AD\u00AE\u00AF\u00E0\u00E4\u00E8\u00E6\u00E7" + 
            "\u00E9\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00FC\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u00F9\u00FB\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00F7\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u00F3\u00F2\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00C4\u0000\u00B3\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u00DA\u0000\u0000\u0000" + 
            "\u00BF\u0000\u0000\u0000\u00C0\u0000\u0000\u0000" + 
            "\u00D9\u0000\u0000\u0000\u00C3\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u00B4\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u00C2\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u00C1\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u00C5\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00CD\u00BA\u00D5\u00D6\u00C9\u00B8\u00B7\u00BB" + 
            "\u00D4\u00D3\u00C8\u00BE\u00BD\u00BC\u00C6\u00C7" + 
            "\u00CC\u00B5\u00B6\u00B9\u00D1\u00D2\u00CB\u00CF" + 
            "\u00D0\u00CA\u00D8\u00D7\u00CE\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00DF\u0000\u0000\u0000\u00DC\u0000\u0000\u0000" + 
            "\u00DB\u0000\u0000\u0000\u00DD\u0000\u0000\u0000" + 
            "\u00DE\u00B0\u00B1\u00B2\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u00FE\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" + 
            "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000"; 

    private final static short index1[] = {
            0, 248, 248, 370, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            577, 248, 808, 248, 248, 1064, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 
            248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 248, 

	};
    }
}
