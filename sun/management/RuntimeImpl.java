/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.management;

import java.lang.management.RuntimeMXBean;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Properties;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.OpenDataException;

/**
 * Implementation class for the runtime subsystem.
 * Standard and committed hotspot-specific metrics if any.
 *
 * ManagementFactory.getRuntimeMXBean() returns an instance
 * of this class.
 */
class RuntimeImpl implements RuntimeMXBean {

    private final VMManagement jvm;
    private final long vmStartupTime;

    /**
     * Constructor of RuntimeImpl class.
     */
    RuntimeImpl(VMManagement vm) {
        this.jvm = vm;
        this.vmStartupTime = jvm.getStartupTime();
    }

    public String getName() {
        return jvm.getVmId();
    }

    public String getManagementSpecVersion() {
        return jvm.getManagementVersion();
    }

    public String getVmName() {
        return jvm.getVmName();
    }

    public String getVmVendor() {
        return jvm.getVmVendor();
    } 

    public String getVmVersion() {
        return jvm.getVmVersion();
    }

    public String getSpecName() {
        return jvm.getVmSpecName();
    }

    public String getSpecVendor() {
        return jvm.getVmSpecVendor();
    }

    public String getSpecVersion() {
        return jvm.getVmSpecVersion();
    }

    public String getClassPath() {
        return jvm.getClassPath();
    }

    public String getLibraryPath() {
        return jvm.getLibraryPath();
    }

    public String getBootClassPath() {
        if (!isBootClassPathSupported()) {
            throw new UnsupportedOperationException(
                "Boot class path mechanism is not supported");
        }
        ManagementFactory.checkMonitorAccess();
        return jvm.getBootClassPath();
    }

    public List<String> getInputArguments() {
        ManagementFactory.checkMonitorAccess();
        return jvm.getVmArguments();
    }

    public long getUptime() {       
        long current = System.currentTimeMillis();

        // TODO: If called from client side when we support
        // MBean proxy to read performance counters from shared memory, 
        // need to check if the monitored VM exitd.
        return (current - vmStartupTime);
    }

    public long getStartTime() {
        return vmStartupTime;
    }

    public boolean isBootClassPathSupported() {
        return jvm.isBootClassPathSupported();
    }

    public Map<String,String> getSystemProperties() {
        Properties sysProps = System.getProperties();
        Map<String,String> map = new HashMap();

        // Properties.entrySet() does not include the entries in
        // the default properties.  So use Properties.stringPropertyNames()
        // to get the list of property keys including the default ones.
        Set<String> keys = sysProps.stringPropertyNames();
        for (String k : keys) {
            String value = sysProps.getProperty(k);
            map.put(k, value);
        }

        return map;
    }
}

