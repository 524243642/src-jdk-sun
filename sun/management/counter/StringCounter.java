/*
 * %W% %E%
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.management.counter;

/**
 * Interface for a performance counter wrapping a <code>String</code> object.
 *
 * @author   Brian Doherty
 * @version  %I%, %G%
 */
public interface StringCounter extends Counter {

    /**
     * Get a copy of the value of the StringCounter.
     */
    public String stringValue();
}
