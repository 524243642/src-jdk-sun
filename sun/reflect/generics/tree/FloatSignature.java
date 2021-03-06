/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

/** AST that represents the type float. */
public class FloatSignature implements BaseType {
    private static FloatSignature singleton = new FloatSignature();

    private FloatSignature(){}

    public static FloatSignature make() {return singleton;}

    public void accept(TypeTreeVisitor<?> v){v.visitFloatSignature(this);}
}
