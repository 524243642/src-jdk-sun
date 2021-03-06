/*
 * %W% %E%
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.management.counter;

/**
 * Interface for a performance counter wrapping a
 * <code>long</code> basic type.
 *
 * @author   Brian Doherty
 * @version  %I%, %G%
 */
public interface LongCounter extends Counter {

    /**
     * Get the value of this Long performance counter
     */
    public long longValue();
}
