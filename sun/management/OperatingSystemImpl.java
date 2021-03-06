/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.management;

import java.lang.management.OperatingSystemMXBean;
import sun.misc.Unsafe;

/**
 * Implementation class for the operating system.
 * Standard and committed hotspot-specific metrics if any.
 *
 * ManagementFactory.getOperatingSystemMXBean() returns an instance
 * of this class.
 */
public class OperatingSystemImpl implements OperatingSystemMXBean {

    private final VMManagement jvm;

    /**
     * Constructor of OperatingSystemImpl class.
     */
    protected OperatingSystemImpl(VMManagement vm) {
        this.jvm = vm;
    }

    public String getName() {
        return jvm.getOsName();
    }

    public String getArch() {
        return jvm.getOsArch();
    }

    public String getVersion() {
        return jvm.getOsVersion();
    }

    public int getAvailableProcessors() {
        return jvm.getAvailableProcessors();
    }

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private double[] loadavg = new double[1];
    public double getSystemLoadAverage() {
        if (unsafe.getLoadAverage(loadavg, 1) == 1) {
             return loadavg[0];
        } else {
             return -1.0;
        }
    }
}

