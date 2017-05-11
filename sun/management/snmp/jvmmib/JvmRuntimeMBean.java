/*
 * %W% %E%
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.management.snmp.jvmmib;

//
// Generated by mibgen version 5.0 (06/02/03) when compiling JVM-MANAGEMENT-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.jmx.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "JvmRuntime" MBean.
 */
public interface JvmRuntimeMBean {

    /**
     * Getter for the "JvmRTBootClassPathSupport" variable.
     */
    public EnumJvmRTBootClassPathSupport getJvmRTBootClassPathSupport() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTManagementSpecVersion" variable.
     */
    public String getJvmRTManagementSpecVersion() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTSpecVersion" variable.
     */
    public String getJvmRTSpecVersion() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTSpecVendor" variable.
     */
    public String getJvmRTSpecVendor() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTSpecName" variable.
     */
    public String getJvmRTSpecName() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTVMVersion" variable.
     */
    public String getJvmRTVMVersion() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTVMVendor" variable.
     */
    public String getJvmRTVMVendor() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTStartTimeMs" variable.
     */
    public Long getJvmRTStartTimeMs() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTUptimeMs" variable.
     */
    public Long getJvmRTUptimeMs() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTVMName" variable.
     */
    public String getJvmRTVMName() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTName" variable.
     */
    public String getJvmRTName() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTInputArgsCount" variable.
     */
    public Integer getJvmRTInputArgsCount() throws SnmpStatusException;

}