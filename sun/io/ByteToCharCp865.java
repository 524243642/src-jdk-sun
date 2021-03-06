/*
 * %W%	%E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package sun.io;

import sun.nio.cs.ext.IBM865;

/**
 * A table to convert to Cp865 to Unicode
 *
 * @author  ConverterGenerator tool
 * @version >= JDK1.1.6
 */

public class ByteToCharCp865 extends ByteToCharSingleByte {

    private final static IBM865 nioCoder = new IBM865();

    public String getCharacterEncoding() {
        return "Cp865";
    }

    public ByteToCharCp865() {
        super.byteToCharTable = nioCoder.getDecoderSingleByteMappings();
    }
}
