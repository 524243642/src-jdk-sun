/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 *   Class Printer for the 'javap' program
 */

package sun.tools.javap.oldjavap;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Vector;

import java.lang.reflect.Modifier;

import sun.tools.java.BinaryClass;
import sun.tools.java.BinaryConstantPool;
import sun.tools.java.BinaryMember;
import sun.tools.java.ClassDeclaration;
import sun.tools.java.Constants;
import sun.tools.java.Identifier;
import sun.tools.java.MemberDefinition;
import sun.tools.java.Type;

/**
 *  JavaP
 */
class JavaPClassPrinter implements Constants {

    private BinaryClass cdef;
    private JavaPEnvironment env;
    private PrintWriter output;
    private Vector codesVec;
    private int depth;

    /**
     * Constructor
     */
    JavaPClassPrinter(BinaryClass cdef, JavaPEnvironment env, int depth) {
	this.cdef = cdef;
	this.env = env;
	this.output = env.output;
        this.depth = depth;
    }

    JavaPClassPrinter(BinaryClass cdef, JavaPEnvironment env) {
	this(cdef, env, 0);
    }

    /**
     * Given a member definition, return the byte code information
     */
    JavaPBinaryCode codeFor(MemberDefinition fld) {
	byte attrData[] = ((BinaryMember)fld).getAttribute(env.idCode);
	if (attrData != null) {
	    BinaryConstantPool cpool = cdef.getConstants();
	    return new JavaPBinaryCode(attrData, cpool, env, fld);
	}
	return null;
    }

    /**
     * Innerclass for enumerating over method code for a class
     */
    private class Codes {

	private int index;

	Codes() {
	    if (codesVec == null) {
		codesVec = new Vector();
		for (MemberDefinition fld = cdef.getFirstMember();
				fld != null;
				fld = fld.getNextMember()) {
		    JavaPBinaryCode code = codeFor(fld);
		    if (code != null)
			codesVec.addElement(code);
		}
	    }
	    index = 0;
	}

	JavaPBinaryCode next() {
	    if (index < codesVec.size())
		return (JavaPBinaryCode)codesVec.elementAt(index++);
	    return null;
	}

    }

    /**
     * Print a description of the class (not members)
     */
    public void printClassHeader() {
        boolean isClass = !cdef.isInterface();
        int modbits = cdef.getModifiers() & ~M_SYNCHRONIZED;
        if (!isClass) {   // interfaces should not be marked abstract
            modbits &= ~M_ABSTRACT; 
        }
	String mods = Modifier.toString(modbits);
	if (mods.length() > 0)
	    output.print(mods + " ");
	if (isClass)	{   // interface is a modifier
	    output.print("class ");
        }
        output.print(cdef.getName() + " ");
        if (isClass && cdef.getSuperClass() != null) {
            output.print("extends " + cdef.getSuperClass().getName() + " ");
        }
	ClassDeclaration interfaces[] = cdef.getInterfaces();
	if (interfaces.length > 0) {
	    output.print(isClass? "implements " : "extends ");
	    for (int i = 0 ; i < interfaces.length ; i++) {
		if (i > 0) {
		    output.print(", ");
		}
		output.print(interfaces[i].getName());
	    }
            output.print(" ");
	}
	if (env.showBackwardCompatible && 
                         (cdef.getModifiers() & ACC_SUPER) != 0) {
	    output.println();
	    output.println("    /* ACC_SUPER bit set */");
	}
	if ((cdef.getModifiers() & ACC_SUPER) == 0) {
	    output.println();
	    output.println("    /* ACC_SUPER bit NOT set */");
	}
    }

    /**
     * Print a field definition
     */
    public void printMemberDefinition(MemberDefinition fld) {
	String mods = Modifier.toString(fld.getModifiers());
	if (mods.length() > 0)
	    output.print(mods + " ");
        output.print(convertMemberDefinition(fld));
	if (!env.showBackwardCompatible) {
            ClassDeclaration[] exp = fld.getExceptions(env);
            if (exp != null && exp.length > 0) {
                output.print(" throws ");
                for(int i=0; i<exp.length; i++){
                    if (i != 0) {
                        output.print(", ");
                    }
                    output.print(exp[i].getName());
                }
            } 
        }
        output.println(";");
    }

    /**
     * Convert any nested classes in the MemberDefinition
     */
    public String convertMemberDefinition(MemberDefinition fld) {
        if (fld.isConstructor()) {
            // Check for nested constructor (simulates most of toString)
            // (Uses getDefining because a constructor's name is stored
            //  as <init>)
            Identifier className = fld.getDefiningClassDeclaration().getName();
            StringBuffer buf = new StringBuffer();
            buf.append(Identifier.lookup(className.getQualifier(),
                                         className.getFlatName()));
            buf.append('(');
            Type argTypes[] = fld.getType().getArgumentTypes();
            for (int i = 0; i < argTypes.length; i++) {
              if (i > 0) {
                buf.append(',');
              }
              buf.append(argTypes[i].toString());
            }
            buf.append(')');
            return buf.toString();
	} else if (fld.isInitializer()) {
	    return "{}";
        } else {
            return fld.toString();
        }
    }

    /**
     * Print class level indent
     */
    private void printIndent(int cnt) {
        for (int i = cnt; i >= 0; --i) {
            output.print("    ");
        }
    }

    /**
     * Print the fields
     */
    public void printMembers() throws IOException {
	for (MemberDefinition fld = cdef.getFirstMember();
			fld != null;
			fld = fld.getNextMember()) {
	    if (env.showAccess.checkMember(fld)) {
		printIndent(depth);
		if (fld.isInnerClass()) {
		    BinaryClass innerCdef = (BinaryClass)fld.getInnerClass();
                    JavaPClassPrinter printer = 
                        new JavaPClassPrinter(innerCdef, env, depth+1);
                    printer.print();
		} else {
		    printMemberDefinition(fld);
		    if (env.showInternalSigs)
			output.println("\t/*   " + 
                                       fld.getType().getTypeSignature() + 
                                       "   */");
		    if (env.showVerbose) {
			JavaPBinaryCode code = codeFor(fld);
			if (code != null)
			    code.printVerboseHeader();
		    }
		}
            }
	}
    }

    /**
     * Print all requested information about one class
     */
    public void print() throws IOException {
	Object src = cdef.getSource();
	JavaPBinaryCode code;

	if (depth == 0) {   // only for outer class
            if (src == null) 
                output.print("No source");
            else {
                output.print("Compiled from ");
                output.println((String)src);
            }
        }
	printClassHeader();
	output.println("{");
	printMembers();

	if (env.showBackwardCompatible) {
	    if (env.showDisassembled) {
		for (Codes codes = new Codes(); (code = codes.next()) != null; ) {
		    code.printCodeSequence();
		    code.printExceptionTable();
		}
	    }
	    if (env.showLineAndLocal) {
		output.println();
		for (Codes codes = new Codes(); (code = codes.next()) != null; )
		    code.printLineNumberTable();
		output.println();
		for (Codes codes = new Codes(); (code = codes.next()) != null; )
		    code.printLocalVariableTable();
	    }
	    output.println();
            printIndent(depth-1);
	    output.println("}");
	} else {
            printIndent(depth-1);
	    output.println("}");
	    if (env.showDisassembled || env.showLineAndLocal) {
		for (Codes codes = new Codes(); (code = codes.next()) != null; ) {
		    if (env.showDisassembled) {
			code.printCodeSequence();
			code.printExceptionTable();
		    }
		    if (env.showLineAndLocal) {
			code.printLineNumberTable();
			code.printLocalVariableTable();
		    }
		}
	    }
	}
    }
}
