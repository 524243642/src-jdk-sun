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
 * This interface is used for representing the remote management interface for the "JvmClassLoading" MBean.
 */
public interface JvmClassLoadingMBean {

    /**
     * Getter for the "JvmClassesVerboseLevel" variable.
     */
    public EnumJvmClassesVerboseLevel getJvmClassesVerboseLevel() throws SnmpStatusException;

    /**
     * Setter for the "JvmClassesVerboseLevel" variable.
     */
    public void setJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel x) throws SnmpStatusException;

    /**
     * Checker for the "JvmClassesVerboseLevel" variable.
     */
    public void checkJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel x) throws SnmpStatusException;

    /**
     * Getter for the "JvmClassesUnloadedCount" variable.
     */
    public Long getJvmClassesUnloadedCount() throws SnmpStatusException;

    /**
     * Getter for the "JvmClassesTotalLoadedCount" variable.
     */
    public Long getJvmClassesTotalLoadedCount() throws SnmpStatusException;

    /**
     * Getter for the "JvmClassesLoadedCount" variable.
     */
    public Long getJvmClassesLoadedCount() throws SnmpStatusException;

}
