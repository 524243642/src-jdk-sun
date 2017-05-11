/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.swing;

/**
 * This interface is used only for tagging keys for client properties
 * for {@code JComponent} set by UI which needs to be cleared on L&F
 * change.
 *  
 * All such keys are removed from client properties in {@code
 * JComponent.setUI()} method after uninstalling old UI and before
 * intalling the new one.
 */

public interface UIClientPropertyKey {
}