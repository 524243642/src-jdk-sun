/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.io;

import sun.nio.cs.ISO_8859_5;

/**
 * Tables and data to convert Unicode to ISO8859_5
 *
 * @author  ConverterGenerator tool
 * @version >= JDK1.1.6
 */

public class CharToByteISO8859_5 extends CharToByteSingleByte {

    private final static ISO_8859_5 nioCoder = new ISO_8859_5();

    public String getCharacterEncoding() {
        return "ISO8859_5";
    }

    public CharToByteISO8859_5() {
        super.mask1 = 0xFF00;
        super.mask2 = 0x00FF;
        super.shift = 8;
        super.index1 = nioCoder.getEncoderIndex1();
        super.index2 = nioCoder.getEncoderIndex2();
    }
}
