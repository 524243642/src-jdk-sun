/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.reflect.generics.tree;

import sun.reflect.generics.visitor.TypeTreeVisitor;

public class BottomSignature implements FieldTypeSignature {
    private static BottomSignature singleton = new BottomSignature();

    private BottomSignature(){}

    public static BottomSignature make() {return singleton;}

    public void accept(TypeTreeVisitor<?> v){v.visitBottomSignature(this);}
}
