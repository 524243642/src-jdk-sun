/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.tools.jps;

import java.util.*;
import java.net.*;
import sun.jvmstat.monitor.*;

/**
 * Application to provide a listing of monitorable java processes.
 *
 * @author Brian Doherty
 * @version %I%, %G%
 * @since 1.5
 */
public class Jps {

    private static Arguments arguments;

    public static void main(String[] args) {
        try {
            arguments = new Arguments(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            Arguments.printUsage(System.err);
            return;
        }

        if (arguments.isHelp()) {
            Arguments.printUsage(System.out);
            System.exit(0);
        }

        try {
            HostIdentifier hostId = arguments.hostId();
            MonitoredHost monitoredHost =
                    MonitoredHost.getMonitoredHost(hostId);

            // get the set active JVMs on the specified host.
            Set jvms = monitoredHost.activeVms();

            for (Iterator j = jvms.iterator(); j.hasNext(); /* empty */ ) {
                StringBuilder output = new StringBuilder();
                Throwable lastError = null;

                int lvmid = ((Integer)j.next()).intValue();

                output.append(String.valueOf(lvmid));

                if (arguments.isQuiet()) {
                    System.out.println(output);
                    continue;
                }

                MonitoredVm vm = null;
                String vmidString = "//" + lvmid + "?mode=r";

                try {
                    VmIdentifier id = new VmIdentifier(vmidString);
                    vm = monitoredHost.getMonitoredVm(id, 0);
                } catch (URISyntaxException e) {
                    // unexpected as vmidString is based on a validated hostid
                    lastError = e;
                    assert false;
                } catch (Exception e) {
                    lastError = e;
                } finally {
                    if (vm == null) {
                        /*
                         * we ignore most exceptions, as there are race
                         * conditions where a JVM in 'jvms' may terminate
                         * before we get a chance to list its information.
                         * Other errors, such as access and I/O exceptions
                         * should stop us from iterating over the complete set.
                         */
                        output.append(" -- process information unavailable");
                        if (arguments.isDebug()) {
                            if ((lastError != null)
                                    && (lastError.getMessage() != null)) {
                                output.append("\n\t");
                                output.append(lastError.getMessage());
                            }
                        }
                        System.out.println(output);
                        if (arguments.printStackTrace()) {
                            lastError.printStackTrace();
                        }
                        continue;
                    }
                }

                output.append(" ");
                output.append(MonitoredVmUtil.mainClass(vm,
                        arguments.showLongPaths()));

                if (arguments.showMainArgs()) {
                    String mainArgs = MonitoredVmUtil.mainArgs(vm);
                    if (mainArgs != null && mainArgs.length() > 0) {
                        output.append(" ").append(mainArgs);
                    }
                }
                if (arguments.showVmArgs()) {
                    String jvmArgs = MonitoredVmUtil.jvmArgs(vm);
                    if (jvmArgs != null && jvmArgs.length() > 0) {
                      output.append(" ").append(jvmArgs);
                    }
                }
                if (arguments.showVmFlags()) {
                    String jvmFlags = MonitoredVmUtil.jvmFlags(vm);
                    if (jvmFlags != null && jvmFlags.length() > 0) {
                        output.append(" ").append(jvmFlags);
                    }
                }

                System.out.println(output);
          
                monitoredHost.detach(vm);
            }
        } catch (MonitorException e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            } else {
                Throwable cause = e.getCause();
                if ((cause != null) && (cause.getMessage() != null)) {
                    System.err.println(cause.getMessage());
                } else {
                    e.printStackTrace();
                }
            }
        }
    }
}
