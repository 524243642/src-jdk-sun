/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */



package sun.tools.javap;

import java.util.*;
import java.io.*;

/**
 * Entry point for javap, class file disassembler.
 *
 * @author  Sucheta Dambalkar (Adopted code from old javap)
 */
public class Main{
    
    private Vector classList = new Vector();
    private PrintWriter out;
    JavapEnvironment env = new JavapEnvironment();
    private static boolean errorOccurred = false;
    private static final String progname = "javap";
    
    
    public Main(PrintWriter out){
	this.out = out;
    }
    
    public static void main(String argv[]) {
	entry(argv);
	if (errorOccurred) {
	    System.exit(1);
	}
    }
    
    
    /**
     * Entry point for tool if you don't want System.exit() called.
     */
    public static void entry(String argv[]) {
	PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
	try {
	    
	    Main jpmain = new Main(out);
	    jpmain.perform(argv);
	    
	} finally {
	    out.close();
	}
    }
    
    /**
     * Process the arguments and perform the desired action
     */
    private void perform(String argv[]) {
	if (parseArguments(argv)) {
	    displayResults();
	    
	}
    }
    
    private void error(String msg) {
	errorOccurred = true;
	System.err.println(msg);
	System.err.flush();
    }
    
    /**
     * Print usage information
     */
    private void usage() {
	java.io.PrintStream out = System.out;
        out.println("Usage: " + progname + " <options> <classes>...");
        out.println();
        out.println("where options include:");
        out.println("   -c                        Disassemble the code");
        out.println("   -classpath <pathlist>     Specify where to find user class files");
        out.println("   -extdirs <dirs>           Override location of installed extensions");
        out.println("   -help                     Print this usage message");
        out.println("   -J<flag>                  Pass <flag> directly to the runtime system");
        out.println("   -l                        Print line number and local variable tables");
	out.println("   -public                   Show only public classes and members");
	out.println("   -protected                Show protected/public classes and members");
	out.println("   -package                  Show package/protected/public classes");
	out.println("                             and members (default)");
	out.println("   -private                  Show all classes and members");
        out.println("   -s                        Print internal type signatures");
        out.println("   -bootclasspath <pathlist> Override location of class files loaded");
        out.println("                             by the bootstrap class loader");
        out.println("   -verbose                  Print stack size, number of locals and args for methods");
        out.println("                             If verifying, print reasons for failure");
        out.println();
    }
    
    /**
     * Parse the command line arguments. 
     * Set flags, construct the class list and create environment.
     */
    private boolean parseArguments(String argv[]) {
	for (int i = 0 ; i < argv.length ; i++) {
	    String arg = argv[i];
	    if (arg.startsWith("-")) {
		if (arg.equals("-Xold")) {
		    String[] oldjpargs = new String[(argv.length)-1];
		    int l = 0;
		    for(int k = 0; k < argv.length; k++){
			if(!(argv[k].equals("-Xold"))){
			    oldjpargs[l] = argv[k];
			    l++;
			}
		    }
		    sun.tools.javap.oldjavap.JavaP.main(oldjpargs);
		    
		} else if (arg.equals("-l")) {
		    env.showLineAndLocal = true;
		} else if (arg.equals("-private") || arg.equals("-p")) {
		    env.showAccess = env.PRIVATE;
		} else if (arg.equals("-package")) {
		    env.showAccess = env.PACKAGE;
		} else if (arg.equals("-protected")) {
		    env.showAccess = env.PROTECTED;
		} else if (arg.equals("-public")) {
		    env.showAccess = env.PUBLIC;
		} else if (arg.equals("-c")) {
		    env.showDisassembled = true;
		} else if (arg.equals("-s")) {
		    env.showInternalSigs = true;
		} else if (arg.equals("-verbose"))  {
		    env.showVerbose = true;
		} else if (arg.equals("-v")) {
		    env.showVerbose = true;
		} else if (arg.equals("-h")) {
                    error("-h is no longer available - use the 'javah' program");
                    return false;
		} else if (arg.equals("-verify")) {
		    error("-verify is no longer available - use 'java -verify'");
                    return false;
		} else if (arg.equals("-verify-verbose")) {
		    error("-verify is no longer available - use 'java -verify'");
                    return false;
		} else if (arg.equals("-help")) {
                    usage();
                    return false;
		} else if (arg.equals("-classpath")) {
		    if ((i + 1) < argv.length) {
			env.classPathString = argv[++i];
		    } else {
			error("-classpath requires argument");
			usage();
			return false;
		    }
		} else if (arg.equals("-bootclasspath")) {
		    if ((i + 1) < argv.length) {
			env.bootClassPathString = argv[++i];
		    } else {
			error("-bootclasspath requires argument");
			usage();
			return false;
		    }
		} else if (arg.equals("-extdirs")) {
		    if ((i + 1) < argv.length) {
			env.extDirsString = argv[++i];
		    } else {
			error("-extdirs requires argument");
			usage();
			return false;
		    }
		} else if (arg.equals("-all")) {
		    env.showallAttr = true;
		} else {
		    error("invalid flag: " + arg);
		    usage();
		    return false;
		}
	    } else {
		classList.addElement(arg);
		env.nothingToDo = false;
	    }
	}
	if (env.nothingToDo) {
	    System.out.println("No classes were specified on the command line.  Try -help.");
	    errorOccurred = true;
	    return false;
	}
	return true;
    }
    
    /**
     * Display results
     */
    private void displayResults() {
	for (int i = 0; i < classList.size() ; i++ ) {
	    String Name = (String)classList.elementAt(i);
	    InputStream classin = env.getFileInputStream(Name);
	    
	    try {
		JavapPrinter printer = new JavapPrinter(classin, out, env);
		printer.print();		// actual do display
		
	    } catch (IllegalArgumentException exc) {
		error(exc.getMessage());
	    }
	}
    }
}


	


    
   

   
