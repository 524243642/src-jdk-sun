/*
 * %W% %E%
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.management.counter.perf;

import sun.management.counter.*;

/**
 * A snapshot of the perf counter for serialization.
 */
class ByteArrayCounterSnapshot extends AbstractCounter
       implements ByteArrayCounter {

    byte[] value;

    // package private
    ByteArrayCounterSnapshot(String name, Units u, Variability v, int flags, 
                             int vectorLength, byte[] value) {
        super(name, u, v, flags, vectorLength);
        this.value = value;
    }
    
    public Object getValue() {
        return value;
    }
    
    public byte[] byteArrayValue() {
        return value;
    }

    public byte byteAt(int index) {
        return value[index];
    }
}
