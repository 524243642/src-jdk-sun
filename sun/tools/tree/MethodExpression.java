/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.tools.tree;

import sun.tools.java.*;
import sun.tools.asm.Assembler;
import java.io.PrintStream;
import java.util.Hashtable;

/**
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public
class MethodExpression extends NaryExpression {
    Identifier id;
    ClassDefinition clazz;   // The class in which the called method is defined
    MemberDefinition field;
    Expression implementation;

    private boolean isSuper;  // Set if qualified by 'super' or '<class>.super'.

    /**
     * constructor
     */
    public MethodExpression(long where, Expression right, Identifier id, Expression args[]) {
	super(METHOD, where, Type.tError, right, args);
	this.id = id;
    }
    public MethodExpression(long where, Expression right, MemberDefinition field, Expression args[]) {
	super(METHOD, where, field.getType().getReturnType(), right, args);
	this.id = field.getName();
	this.field = field;
	this.clazz = field.getClassDefinition();
    }

    // This is a hack used only within certain access methods generated by 
    // 'SourceClass.getAccessMember'.  It allows an 'invokespecial' instruction
    // to be forced even though 'super' does not appear within the call.
    // Such access methods are needed for access to protected methods when using
    // the qualified '<class>.super.<method>(...)' notation.
    public MethodExpression(long where, Expression right,
			    MemberDefinition field, Expression args[], boolean forceSuper) {
	this(where, right, field, args);
	this.isSuper = forceSuper;
    }
    
    public Expression getImplementation() {
	if (implementation != null)
	    return implementation;
	return this;
    }

    /**
     * Check expression type
     */
    public Vset checkValue(Environment env, Context ctx, Vset vset, Hashtable exp) {
	ClassDeclaration c = null;
	boolean isArray = false;
	boolean staticRef = false;

	// Access method to use if required.
	MemberDefinition implMethod = null;

	ClassDefinition ctxClass = ctx.field.getClassDefinition();

	// When calling a constructor, we may need to add an
	// additional argument to transmit the outer instance link.
	Expression args[] = this.args;
	if (id.equals(idInit)){
	    ClassDefinition conCls = ctxClass;
	    try {
		Expression conOuter = null;
		if (right instanceof SuperExpression) {
		    // outer.super(...)
		    conCls = conCls.getSuperClass().getClassDefinition(env);
		    conOuter = ((SuperExpression)right).outerArg;
		} else if (right instanceof ThisExpression) {
		    // outer.this(...)
		    conOuter = ((ThisExpression)right).outerArg;
		}
		args = NewInstanceExpression.
		    insertOuterLink(env, ctx, where, conCls, conOuter, args);
	    } catch (ClassNotFound ee) {
		// the same error is handled elsewhere
	    }
	}

	Type argTypes[] = new Type[args.length];

	// The effective accessing class, for access checking.
	// This is normally the immediately enclosing class.
	ClassDefinition sourceClass = ctxClass;

	try {
	    if (right == null) {
		staticRef = ctx.field.isStatic();
		// Find the first outer scope that mentions the method.
		ClassDefinition cdef = ctxClass;
		MemberDefinition m = null;
		for (; cdef != null; cdef = cdef.getOuterClass()) {
		    m = cdef.findAnyMethod(env, id);
		    if (m != null) {
			break;
		    }
		}
		if (m == null) {
		    // this is the scope for error diagnosis
		    c = ctx.field.getClassDeclaration();
		} else {
		    // found the innermost scope in which m occurs
		    c = cdef.getClassDeclaration();

		    // Maybe an inherited method hides an apparent method.
		    // Keep looking at enclosing scopes to find out.
		    if (m.getClassDefinition() != cdef) {
			ClassDefinition cdef2 = cdef;
			while ((cdef2 = cdef2.getOuterClass()) != null) {
			    MemberDefinition m2 = cdef2.findAnyMethod(env, id);
			    if (m2 != null && m2.getClassDefinition() == cdef2) {
				env.error(where, "inherited.hides.method",
					  id, cdef.getClassDeclaration(),
					  cdef2.getClassDeclaration());
				break;
			    }
			}
		    }
		}
	    } else {
		if (id.equals(idInit)) {
		    int thisN = ctx.getThisNumber();
		    if (!ctx.field.isConstructor()) {
			env.error(where, "invalid.constr.invoke");
			return vset.addVar(thisN);
		    }
 		    // As a consequence of the DA/DU rules in the JLS (draft of
 		    // forthcoming 2e), all variables are both definitely assigned
 		    // and definitely unassigned in unreachable code.  Normally, this
 		    // correctly suppresses DA/DU-related errors in such code.
 		    // The use of the DA status of the 'this' variable for the extra
 		    // check below on correct constructor usage, however, does not quite
 		    // fit into this DA/DU scheme.  The current representation of
 		    // Vsets for unreachable dead-ends, does not allow 'clearVar' 
 		    // to work, as the DA/DU bits (all on) are implicitly represented
 		    // by the fact that the Vset is a dead-end.  The DA/DU status
 		    // of the 'this' variable is supposed to be temporarily
 		    // cleared at the beginning of a constructor and during the
 		    // checking of constructor arguments (see below in this method).
 		    // Since 'clearVar' has no effect on dead-ends, we may
 		    // find the 'this' variable in an erroneously definitely-assigned state.
 		    // As a workaround, we suppress the following error message when
 		    // the Vset is a dead-end, i.e., when we are in unreachable code.
 		    // Unfortunately, the special-case treatment of reachability for
 		    // if-then and if-then-else allows unreachable code in some circumstances,
 		    // thus it is possible that no error message will be emitted at all.
 		    // While this behavior is strictly incorrect (thus we call this a
 		    // workaround), the problematic code is indeed unreachable and will
 		    // not be executed.  In fact, it will be entirely omitted from the
 		    // translated program, and can cause no harm at runtime.  A correct
 		    // solution would require modifying the representation of the DA/DU
 		    // analysis to use finite Vsets only, restricting the universe
 		    // of variables about which assertions are made (even in unreachable
 		    // code) to variables that are actually in scope. Alternatively, the
 		    // Vset extension and the dead-end marker (currently a reserved value
 		    // of the extension) could be represented orthogonally.  In either case,
 		    // 'clearVar' could then be made to work on (non-canonical) dead ends.
 		    // See file 'Vset.java'.
 		    if (!vset.isReallyDeadEnd() && vset.testVar(thisN)) {
			env.error(where, "constr.invoke.not.first");
			return vset;
		    }
		    vset = vset.addVar(thisN);
		    if (right instanceof SuperExpression) {
			// supers require this specific kind of checking
			vset = right.checkAmbigName(env, ctx, vset, exp, this);
		    } else {
			vset = right.checkValue(env, ctx, vset, exp);
		    }
		} else {
		    vset = right.checkAmbigName(env, ctx, vset, exp, this);
		    if (right.type == Type.tPackage) {
			FieldExpression.reportFailedPackagePrefix(env, right);
			return vset;
		    }
		    if (right instanceof TypeExpression) {
			staticRef = true;
		    }
		}
		if (right.type.isType(TC_CLASS)) {
		    c = env.getClassDeclaration(right.type);
		} else if (right.type.isType(TC_ARRAY)) {
		    isArray = true;
		    c = env.getClassDeclaration(Type.tObject);
		} else {
		    if (!right.type.isType(TC_ERROR)) {
			env.error(where, "invalid.method.invoke", right.type);
		    }
		    return vset;
		}

		// Normally, the effective accessing class is the innermost
		// class surrounding the current method call, but, for calls
		// of the form '<class>.super.<method>(...)', it is <class>.
		// This allows access to protected members of a superclass
		// from within a class nested within one of its subclasses.
		// Otherwise, for example, the call below to 'matchMethod'
		// may fail due to the rules for visibility of inaccessible
		// members.  For consistency, we treat qualified 'this' in
		// the same manner, as error diagnostics will be affected.
		// QUERY: Are there subtle unexplored language issues here?
		if (right instanceof FieldExpression) {
		    Identifier id = ((FieldExpression)right).id;
		    if (id == idThis) {
			sourceClass = ((FieldExpression)right).clazz;
		    } else if (id == idSuper) {
			isSuper = true;
			sourceClass = ((FieldExpression)right).clazz;
		    }
		} else if (right instanceof SuperExpression) {
		    isSuper = true;
		}

		// Fix for 4158650.  When we extend a protected inner
		// class in a different package, we may not have access
		// to the type of our superclass.  Allow the call to
		// the superclass constructor from within our constructor
		// Note that this check does not apply to constructor
		// calls in new instance expressions -- those are part
		// of NewInstanceExpression#check().
		if (id != idInit) {
		    // Required by JLS 6.6.1.  Fixes 4143715.
		    // (See also 4094658.)
		    if (!FieldExpression.isTypeAccessible(where, env,
							  right.type,
							  sourceClass)) {
			ClassDeclaration cdecl =
			    sourceClass.getClassDeclaration();
			if (staticRef) {
			    env.error(where, "no.type.access",
				      id, right.type.toString(), cdecl);
			} else {
			    env.error(where, "cant.access.member.type",
				      id, right.type.toString(), cdecl);
			}
		    }
		}
	    }

	    // Compose a list of argument types
	    boolean hasErrors = false;

	    // "this" is not defined during argument checking
	    if (id.equals(idInit)) {
		vset = vset.clearVar(ctx.getThisNumber());
	    }

	    for (int i = 0 ; i < args.length ; i++) {
		vset = args[i].checkValue(env, ctx, vset, exp);
		argTypes[i] = args[i].type;
		hasErrors = hasErrors || argTypes[i].isType(TC_ERROR);
	    }

	    // "this" is defined after the constructor invocation
	    if (id.equals(idInit)) {
		vset = vset.addVar(ctx.getThisNumber());
	    }

	    // Check if there are any type errors in the arguments
	    if (hasErrors) {
		return vset;
	    }

	    // Get the method field, given the argument types
	    clazz = c.getClassDefinition(env);

	    if (field == null) {

		field = clazz.matchMethod(env, sourceClass, id, argTypes);
		
		if (field == null) {
		    if (id.equals(idInit)) {
			if (diagnoseMismatch(env, args, argTypes))
			    return vset;
			String sig = clazz.getName().getName().toString();
			sig = Type.tMethod(Type.tError, argTypes).typeString(sig, false, false);
			env.error(where, "unmatched.constr", sig, c);
			return vset;
		    }
		    String sig = id.toString();
		    sig = Type.tMethod(Type.tError, argTypes).typeString(sig, false, false);
		    if (clazz.findAnyMethod(env, id) == null) {
			if (ctx.getField(env, id) != null) {
			    env.error(where, "invalid.method", id, c);
			} else {
			    env.error(where, "undef.meth", sig, c);
			}
		    } else if (diagnoseMismatch(env, args, argTypes)) {
		    } else {
			env.error(where, "unmatched.meth", sig, c);
		    }
		    return vset;
		}

	    }

	    type = field.getType().getReturnType();

	    // Make sure that static references are allowed
	    if (staticRef && !field.isStatic()) {
		env.error(where, "no.static.meth.access",
			  field, field.getClassDeclaration());
		return vset;
	    }

	    if (field.isProtected() 
		&& !(right == null) 
		&& !(right instanceof SuperExpression
		     // Extension of JLS 6.6.2 for qualified 'super'.
		     || (right instanceof FieldExpression &&
			 ((FieldExpression)right).id == idSuper))
		&& !sourceClass.protectedAccess(env, field, right.type)) {
		env.error(where, "invalid.protected.method.use",
			  field.getName(), field.getClassDeclaration(),
			  right.type);
		return vset;
	    }

	    // In <class>.super.<method>(), we cannot simply evaluate
	    // <class>.super to an object reference (as we would for
	    // <class>.super.<field>) and then perform an 'invokespecial'.
	    // An 'invokespecial' must be performed from within (a subclass of)
	    // the class in which the target method is located.
	    if (right instanceof FieldExpression &&
		((FieldExpression)right).id == idSuper) {
		if (!field.isPrivate()) {
		    // The private case is handled below.
		    // Use an access method unless the effective accessing class
		    // (the class qualifying the 'super') is the same as the
		    // immediately enclosing class, i.e., the qualification was
		    // unnecessary.
		    if (sourceClass != ctxClass) {
			implMethod = sourceClass.getAccessMember(env, ctx, field, true);
		    }
		}
	    }
	    
	    // Access method for private field if not in the same class.
	    if (implMethod == null && field.isPrivate()) {
		ClassDefinition cdef = field.getClassDefinition();
		if (cdef != ctxClass) {
		    implMethod = cdef.getAccessMember(env, ctx, field, false);
		}
	    }
	    
	    // Make sure that we are not invoking an abstract method
	    if (field.isAbstract() && (right != null) && (right.op == SUPER)) {
		env.error(where, "invoke.abstract", field, field.getClassDeclaration());
		return vset;
	    }

	    if (field.reportDeprecated(env)) {
		if (field.isConstructor()) {
		    env.error(where, "warn.constr.is.deprecated", field);
		} else {
		    env.error(where, "warn.meth.is.deprecated",
			      field, field.getClassDefinition());
		}
	    }

	    // Check for recursive constructor
	    if (field.isConstructor() && ctx.field.equals(field)) {
		env.error(where, "recursive.constr", field);
	    }

	    // When a package-private class defines public or protected
	    // members, those members may sometimes be accessed from
	    // outside of the package in public subclasses.  In these
	    // cases, we need to massage the method call to refer to
	    // to an accessible subclass rather than the package-private
	    // parent class.  Part of fix for 4135692.
		
	    // Find out if the class which contains this method
	    // call has access to the class which declares the
	    // public or protected method referent.
	    // We don't perform this translation on constructor calls.
	    if (sourceClass == ctxClass) {
		ClassDefinition declarer = field.getClassDefinition();
		if (!field.isConstructor() &&
		    declarer.isPackagePrivate() &&
		    !declarer.getName().getQualifier()
		    .equals(sourceClass.getName().getQualifier())) {
		    
		    //System.out.println("The access of member " +
		    //		   field + " declared in class " +
		    //		   declarer +
		    //		   " is not allowed by the VM from class  " +
		    //		   accessor +
		    //		   ".  Replacing with an access of class " +
		    //		   clazz);
		    
		    // We cannot make this access at the VM level.
		    // Construct a member which will stand for this
		    // method in clazz and set `field' to refer to it.
		    field =
			MemberDefinition.makeProxyMember(field, clazz, env);
		}
	    }
	    
	    sourceClass.addDependency(field.getClassDeclaration());
	    if (sourceClass != ctxClass) {
		ctxClass.addDependency(field.getClassDeclaration());
	    }

	} catch (ClassNotFound ee) {
	    env.error(where, "class.not.found", ee.name, ctx.field);
	    return vset;

	} catch (AmbiguousMember ee) {
	    env.error(where, "ambig.field", id, ee.field1, ee.field2);
	    return vset;
	}

	// Make sure it is qualified
	if ((right == null) && !field.isStatic()) {
	    right = ctx.findOuterLink(env, where, field);
	    vset = right.checkValue(env, ctx, vset, exp);
	}

	// Cast arguments
	argTypes = field.getType().getArgumentTypes();
	for (int i = 0 ; i < args.length ; i++) {
	    args[i] = convert(env, ctx, argTypes[i], args[i]);
	}

	if (field.isConstructor()) {
	    MemberDefinition m = field;
	    if (implMethod != null) {
		m = implMethod;
	    }
	    int nargs = args.length;
	    Expression[] newargs = args;
	    if (nargs > this.args.length) {
		// Argument was added above.
		// Maintain the model for hidden outer args in outer.super(...):
		Expression rightI;
		if (right instanceof SuperExpression) {
		    rightI = new SuperExpression(right.where, ctx);
		    ((SuperExpression)right).outerArg = args[0];
		} else if (right instanceof ThisExpression) {
		    rightI = new ThisExpression(right.where, ctx);
		} else {
		    throw new CompilerError("this.init");
		}
		if (implMethod != null) {
		    // Need dummy argument for access method.
		    // Dummy argument follows outer instance link.
		    // Leave 'this.args' equal to 'newargs' but
		    // without the outer instance link.
		    newargs = new Expression[nargs+1];
		    this.args = new Expression[nargs];
		    newargs[0] = args[0]; // outer instance
		    this.args[0] = newargs[1] = new NullExpression(where); // dummy argument
		    for (int i = 1 ; i < nargs ; i++) {
			this.args[i] = newargs[i+1] = args[i];
		    }
		} else {
		    // Strip outer instance link from 'this.args'.
		    // ASSERT(this.arg.length == nargs-1);
		    for (int i = 1 ; i < nargs ; i++) {
			this.args[i-1] = args[i];
		    }
		}
		implementation = new MethodExpression(where, rightI, m, newargs);
		implementation.type = type; // Is this needed?
	    } else {
		// No argument was added.
		if (implMethod != null) {
		    // Need dummy argument for access method.
		    // Dummy argument is first, as there is no outer instance link.
		    newargs = new Expression[nargs+1];
		    newargs[0] = new NullExpression(where);
		    for (int i = 0 ; i < nargs ; i++) {
			newargs[i+1] = args[i];
		    }
		}
		implementation = new MethodExpression(where, right, m, newargs);
	    }
	} else {
	    // Have ordinary method.
	    // Argument should have been added only for a constructor.
	    if (args.length > this.args.length) {
		throw new CompilerError("method arg");
	    }
	    if (implMethod != null) {
		//System.out.println("Calling " + field + " via " + implMethod);
		Expression oldargs[] = this.args;
		if (field.isStatic()) {
		    Expression call = new MethodExpression(where, null, implMethod, oldargs);
		    implementation = new CommaExpression(where, right, call);
		} else {
		    // Access method needs an explicit 'this' pointer.
		    int nargs = oldargs.length;
		    Expression newargs[] = new Expression[nargs+1];
		    newargs[0] = right;
		    for (int i = 0; i < nargs; i++) {
			newargs[i+1] = oldargs[i];
		    }
		    implementation = new MethodExpression(where, null, implMethod, newargs);
		}
	    }
	}

	// Follow super() by variable initializations
	if (ctx.field.isConstructor() &&
	    field.isConstructor() && (right != null) && (right.op == SUPER)) {
	    Expression e = makeVarInits(env, ctx);
	    if (e != null) {
		if (implementation == null)
		    implementation = (Expression)this.clone();
		implementation = new CommaExpression(where, implementation, e);
	    }
	}

	// Throw the declared exceptions.
	ClassDeclaration exceptions[] = field.getExceptions(env);
	if (isArray && (field.getName() == idClone) &&
	       (field.getType().getArgumentTypes().length == 0)) {
	    /* Arrays pretend that they have "public Object clone()" that doesn't
	     * throw anything, according to the language spec.
	     */
	    exceptions = new ClassDeclaration[0];
	    /* See if there's a bogus catch for it, to issue a warning. */
	    for (Context p = ctx; p != null; p = p.prev) {
		if (p.node != null && p.node.op == TRY) {
		    ((TryStatement) p.node).arrayCloneWhere = where;
		}
	    }
	}
	for (int i = 0 ; i < exceptions.length ; i++) {
	    if (exp.get(exceptions[i]) == null) {
		exp.put(exceptions[i], this);
	    }
	}

	// Mark all blank finals as definitely assigned following 'this(...)'.
	// Correctness follows inductively from the requirement that all blank finals
	// be definitely assigned at the completion of every constructor.
	if (ctx.field.isConstructor() &&
	    field.isConstructor() && (right != null) && (right.op == THIS)) {
	    ClassDefinition cls = field.getClassDefinition();
	    for (MemberDefinition f = cls.getFirstMember() ; f != null ; f = f.getNextMember()) {
		if (f.isVariable() && f.isBlankFinal() && !f.isStatic()) {
		    // Static variables should also be considered defined as well, but this
		    // is handled in 'SourceClass.checkMembers', and we should not interfere.
		    vset = vset.addVar(ctx.getFieldNumber(f));
		}
	    }
	}

	return vset;
    }

    /**
     * Check void expression
     */
    public Vset check(Environment env, Context ctx, Vset vset, Hashtable exp) {
	return checkValue(env, ctx, vset, exp);
    }

    /**
     * We're about to report a "unmatched method" error.
     * Try to issue a better diagnostic by comparing the actual argument types
     * with the method (or methods) available.
     * In particular, if there is an argument which fails to match <em>any</em>
     * method, we report a type mismatch error against that particular argument.
     * The diagnostic will report a target type taken from one of the methods.
     * <p>
     * Return false if we couldn't think of anything smart to say.
     */
    boolean diagnoseMismatch(Environment env, Expression args[],
			     Type argTypes[]) throws ClassNotFound {
	Type margType[] = new Type[1];
	boolean saidSomething = false;
	int start = 0;
	while (start < argTypes.length) {
	    int code = clazz.diagnoseMismatch(env, id, argTypes, start, margType);
	    String opName = (id.equals(idInit)) ? "constructor" : opNames[op];
	    if (code == -2) {
		env.error(where, "wrong.number.args", opName);
		saidSomething = true;
	    }
	    if (code < 0)  break;
	    int i = code >> 2;
	    boolean castOK = (code & 2) != 0;
	    boolean ambig = (code & 1) != 0;
	    Type targetType = margType[0];

	    // At least one argument is offensive to all overloadings.
	    // targetType is one of the argument types it does not match.
	    String ttype = ""+targetType;

	    // The message might be slightly misleading, if there are other
	    // argument types that also would match.  Hint at this:
	    //if (ambig)  ttype = "{"+ttype+";...}";

	    if (castOK)
		env.error(args[i].where, "explicit.cast.needed", opName, argTypes[i], ttype);
	    else
		env.error(args[i].where, "incompatible.type", opName, argTypes[i], ttype);
	    saidSomething = true;
	    start = i+1;	// look for other bad arguments, too
	}
	return saidSomething;
    }

    /**
     * Inline
     */
    static final int MAXINLINECOST = Statement.MAXINLINECOST;

    private
    Expression inlineMethod(Environment env, Context ctx, Statement s, boolean valNeeded) {
	if (env.dump()) {
	    System.out.println("INLINE METHOD " + field + " in " + ctx.field);
	}
	LocalMember v[] = LocalMember.copyArguments(ctx, field);
	Statement body[] = new Statement[v.length + 2];

	int n = 0;
	if (field.isStatic()) {
	    body[0] = new ExpressionStatement(where, right);
	} else {
	    if ((right != null) && (right.op == SUPER)) {
		right = new ThisExpression(right.where, ctx);
	    }
	    body[0] = new VarDeclarationStatement(where, v[n++], right);
	}
	for (int i = 0 ; i < args.length ; i++) {
	    body[i + 1] = new VarDeclarationStatement(where, v[n++], args[i]);
	}
	//System.out.print("BEFORE:"); s.print(System.out); System.out.println();
	// Note: If !valNeeded, then all returns in the body of the method
	// change to void returns.
	body[body.length - 1] = (s != null) ? s.copyInline(ctx, valNeeded) : null;
	//System.out.print("COPY:"); body[body.length - 1].print(System.out); System.out.println();
	LocalMember.doneWithArguments(ctx, v);

	// Make sure the type matches what the return statements are returning.
	Type type = valNeeded ? this.type : Type.tVoid;
	Expression e = new InlineMethodExpression(where, type, field, new CompoundStatement(where, body));
	return valNeeded ? e.inlineValue(env, ctx) : e.inline(env, ctx);
    }

    public Expression inline(Environment env, Context ctx) {
	if (implementation != null)
	    return implementation.inline(env, ctx);
	try {
	    if (right != null) {
		right = field.isStatic() ? right.inline(env, ctx) : right.inlineValue(env, ctx);
	    }
	    for (int i = 0 ; i < args.length ; i++) {
		args[i] = args[i].inlineValue(env, ctx);
	    }

	    // ctxClass is the current class trying to inline this method
	    ClassDefinition ctxClass = ctx.field.getClassDefinition();

	    Expression e = this;
	    if (env.opt() && field.isInlineable(env, clazz.isFinal()) &&
		
                // Don't inline if a qualified non-static method: the call
		// itself might throw NullPointerException as a side effect
		((right == null) || (right.op==THIS) || field.isStatic()) &&

		// We only allow the inlining if the current class can access
		// the field, the field's class, and right's declared type.
                ctxClass.permitInlinedAccess(env, 
                              field.getClassDeclaration()) &&
	        ctxClass.permitInlinedAccess(env, field) &&
                (right==null || ctxClass.permitInlinedAccess(env, 
                              env.getClassDeclaration(right.type)))  &&

		((id == null) || !id.equals(idInit)) && 
		(!ctx.field.isInitializer()) && ctx.field.isMethod() &&
		(ctx.getInlineMemberContext(field) == null)) {
		Statement s = (Statement)field.getValue(env);
		if ((s == null) ||
		    (s.costInline(MAXINLINECOST, env, ctx) < MAXINLINECOST))  {
		    e = inlineMethod(env, ctx, s, false);
		}
	    }
	    return e;

	} catch (ClassNotFound e) {
	    throw new CompilerError(e);
	}
    }

    public Expression inlineValue(Environment env, Context ctx) {
	if (implementation != null)
	    return implementation.inlineValue(env, ctx);
	try {
	    if (right != null) {
		right = field.isStatic() ? right.inline(env, ctx) : right.inlineValue(env, ctx);
	    }
	    if (field.getName().equals(idInit)) {
		ClassDefinition refc = field.getClassDefinition();
		UplevelReference r = refc.getReferencesFrozen();
		if (r != null) {
		    r.willCodeArguments(env, ctx);
		}
	    }
	    for (int i = 0 ; i < args.length ; i++) {
		args[i] = args[i].inlineValue(env, ctx);
	    }
    
	    // ctxClass is the current class trying to inline this method
	    ClassDefinition ctxClass = ctx.field.getClassDefinition();

	    if (env.opt() && field.isInlineable(env, clazz.isFinal()) &&
		
                // Don't inline if a qualified non-static method: the call
		// itself might throw NullPointerException as a side effect
		((right == null) || (right.op==THIS) || field.isStatic()) &&

		// We only allow the inlining if the current class can access
		// the field, the field's class, and right's declared type.
                ctxClass.permitInlinedAccess(env, 
                              field.getClassDeclaration()) &&
	        ctxClass.permitInlinedAccess(env, field) &&
                (right==null || ctxClass.permitInlinedAccess(env, 
                              env.getClassDeclaration(right.type)))  &&

		(!ctx.field.isInitializer()) && ctx.field.isMethod() &&
		(ctx.getInlineMemberContext(field) == null)) {
		Statement s = (Statement)field.getValue(env);
		if ((s == null) ||
		    (s.costInline(MAXINLINECOST, env, ctx) < MAXINLINECOST))  {
		    return inlineMethod(env, ctx, s, true);
		}
	    }
	    return this;
	} catch (ClassNotFound e) {
	    throw new CompilerError(e);
	}
    }

    public Expression copyInline(Context ctx) {
	if (implementation != null)
	    return implementation.copyInline(ctx);
	return super.copyInline(ctx);
    }

    public int costInline(int thresh, Environment env, Context ctx) {
	if (implementation != null)
	    return implementation.costInline(thresh, env, ctx);

	// for now, don't allow calls to super() to be inlined.  We may fix
	// this later
	if ((right != null) && (right.op == SUPER)) {
	    return thresh;
	}
	return super.costInline(thresh, env, ctx);
    }

    /*
     * Grab all instance initializer code from the class definition,
     * and return as one bolus.  Note that we are assuming the
     * the relevant fields have already been checked.
     * (See the pre-pass in SourceClass.checkMembers which ensures this.)
     */
    private Expression makeVarInits(Environment env, Context ctx) {
	// insert instance initializers
	ClassDefinition clazz = ctx.field.getClassDefinition();
	Expression e = null;
	for (MemberDefinition f = clazz.getFirstMember() ; f != null ; f = f.getNextMember()) {
	    if ((f.isVariable() || f.isInitializer()) && !f.isStatic()) {
		try {
		    f.check(env);
		} catch (ClassNotFound ee) {
		    env.error(f.getWhere(), "class.not.found", ee.name,
			      f.getClassDefinition());
		}
		Expression val = null;
		if (f.isUplevelValue()) {
		    if (f != clazz.findOuterMember()) {
			// it's too early to accumulate these
			continue;
		    }
		    IdentifierExpression arg =
			new IdentifierExpression(where, f.getName());
		    if (!arg.bind(env, ctx)) {
			throw new CompilerError("bind "+arg.id);
		    }
		    val = arg;
		} else if (f.isInitializer()) {
		    Statement s = (Statement)f.getValue();
		    val = new InlineMethodExpression(where, Type.tVoid, f, s);
		} else {
		    val = (Expression)f.getValue();
		}
		// append all initializers to "e":
		// This section used to check for variables which were
		// initialized to their default values and elide such
		// initialization.  This is specifically disallowed by
		// JLS 12.5 numeral 4, which requires a textual ordering
		// on the execution of initializers.
		if ((val != null)) { //  && !val.equals(0)) {
		    long p = f.getWhere();
		    val = val.copyInline(ctx);
		    Expression init = val;
		    if (f.isVariable()) {
			Expression v = new ThisExpression(p, ctx);
		    v = new FieldExpression(p, v, f);
		    init = new AssignExpression(p, v, val);
		    }
		    e = (e == null) ? init : new CommaExpression(p, e, init);
		}
	    }
	}
	return e;
    }

    /**
     * Code
     */
    public void codeValue(Environment env, Context ctx, Assembler asm) {
	if (implementation != null)
	    throw new CompilerError("codeValue");
	int i = 0;		// argument index
	if (field.isStatic()) {
	    if (right != null) {
		right.code(env, ctx, asm);
	    }
	} else if (right == null) {
	    asm.add(where, opc_aload, new Integer(0));
	} else if (right.op == SUPER) {
	    // 'super.<method>(...)', 'super(...)', or '<expr>.super(...)'
	    /*****
	    isSuper = true;
	    *****/
	    right.codeValue(env, ctx, asm);
	    if (idInit.equals(id)) {
		// 'super(...)' or '<expr>.super(...)' only
		ClassDefinition refc = field.getClassDefinition();
		UplevelReference r = refc.getReferencesFrozen();
		if (r != null) {
		    // When calling a constructor for a class with
		    // embedded uplevel references, add extra arguments.
		    if (r.isClientOuterField()) {
			// the extra arguments are inserted after this one
			args[i++].codeValue(env, ctx, asm);
		    }
		    r.codeArguments(env, ctx, asm, where, field);
		}
	    }
	} else {
	    right.codeValue(env, ctx, asm);
	    /*****
	    if (right.op == FIELD &&
		((FieldExpression)right).id == idSuper) {
		// '<class>.super.<method>(...)'
		isSuper = true;
	    }
	    *****/
	}

	for ( ; i < args.length ; i++) {
	    args[i].codeValue(env, ctx, asm);
	}

	if (field.isStatic()) {
	    asm.add(where, opc_invokestatic, field);
	} else if (field.isConstructor() || field.isPrivate() || isSuper) {
	    asm.add(where, opc_invokespecial, field);
	} else if (field.getClassDefinition().isInterface()) {
	    asm.add(where, opc_invokeinterface, field);
	} else {
	    asm.add(where, opc_invokevirtual, field);
	}

	if (right != null && right.op == SUPER && idInit.equals(id)) {
	    // 'super(...)' or '<expr>.super(...)'
	    ClassDefinition refc = ctx.field.getClassDefinition();
	    UplevelReference r = refc.getReferencesFrozen();
	    if (r != null) {
		// After calling a superclass constructor in a class with
		// embedded uplevel references, initialize uplevel fields.
		r.codeInitialization(env, ctx, asm, where, field);
	    }
	}
    }

    /**
     * Check if the first thing is a constructor invocation
     */
    public Expression firstConstructor() {
	return id.equals(idInit) ? this : null;
    }

    /**
     * Print
     */
    public void print(PrintStream out) {
	out.print("(" + opNames[op]);
	if (right != null) {
	    out.print(" ");
	    right.print(out);
	}
	out.print(" " + ((id == null) ? idInit : id));
	for (int i = 0 ; i < args.length ; i++) {
	    out.print(" ");
	    if (args[i] != null) {
		args[i].print(out);
	    } else {
		out.print("<null>");
	    }
	}
	out.print(")");
	if (implementation != null) {
	    out.print("/IMPL=");
	    implementation.print(out);
	}
    }
}
