/*
 * %W%	%E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package sun.io;

import sun.nio.cs.IBM855;

/**
 * A table to convert to Cp855 to Unicode
 *
 * @author  ConverterGenerator tool
 * @version >= JDK1.1.6
 */

public class ByteToCharCp855 extends ByteToCharSingleByte {

    private final static IBM855 nioCoder = new IBM855();

    public String getCharacterEncoding() {
        return "Cp855";
    }

    public ByteToCharCp855() {
        super.byteToCharTable = nioCoder.getDecoderSingleByteMappings();
    }
}
