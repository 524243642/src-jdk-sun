/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package sun.io;

import sun.nio.cs.ext.IBM964;

/**
* Tables and data to convert Unicode to Cp964
*
* @author Malcolm Ayres, assisted by UniMap program
*/
public class CharToByteCp964
	extends CharToByteEUC

{
	private final static IBM964 nioCoder = new IBM964();

	// Return the character set id
	public String getCharacterEncoding()
	{
		return "Cp964";
	}

	public int getMaxBytesPerChar() { 
		return 4;
	}

	public CharToByteCp964()
	{
		super();
		super.mask1 = 0xFFC0;
		super.mask2 = 0x003F;
		super.shift = 6;
		super.index1 = nioCoder.getEncoderIndex1();
		super.index2 = nioCoder.getEncoderIndex2();
		super.index2a = nioCoder.getEncoderIndex2a();
		super.index2b = nioCoder.getEncoderIndex2b();
		super.index2c = nioCoder.getEncoderIndex2c();
	}
}
