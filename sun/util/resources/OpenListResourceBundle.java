/*
 * %W% %E%
 */

/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package sun.util.resources;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import sun.util.ResourceBundleEnumeration;

/**
 * Subclass of <code>ResourceBundle</code> which mimics
 * <code>ListResourceBundle</code>, but provides more hooks
 * for specialized subclass behavior. For general description,
 * see {@link java.util.ListResourceBundle}.
 * <p>
 * This class leaves handleGetObject non-final, and
 * adds a method createMap which allows subclasses to
 * use specialized Map implementations.
 */
public abstract class OpenListResourceBundle extends ResourceBundle {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected OpenListResourceBundle() {
    }

    // Implements java.util.ResourceBundle.handleGetObject; inherits javadoc specification.
    public Object handleGetObject(String key) {
        if (key == null) {
            throw new NullPointerException();
        }

        loadLookupTablesIfNecessary();
        return lookup.get(key); // this class ignores locales
    }

    /**
     * Implementation of ResourceBundle.getKeys.
     */
    public Enumeration<String> getKeys() {
        ResourceBundle parent = this.parent;
        return new ResourceBundleEnumeration(handleGetKeys(),
                (parent != null) ? parent.getKeys() : null);
    }

    /**
     * Returns a set of keys provided in this resource bundle
     */
    public Set<String> handleGetKeys() {
        loadLookupTablesIfNecessary();

        return lookup.keySet();
    }
    
    /**
     * Returns the parent bundle
     */
    public OpenListResourceBundle getParent() {
        return (OpenListResourceBundle)parent;
    }
    
    /**
     * See ListResourceBundle class description.
     */
    abstract protected Object[][] getContents();

    /**
     * Load lookup tables if they haven't been loaded already.
     */
    void loadLookupTablesIfNecessary() {
        if (lookup == null) {
            loadLookup();
        }
    }
    
    /**
     * We lazily load the lookup hashtable.  This function does the
     * loading.
     */
    private synchronized void loadLookup() {
        if (lookup != null)
            return;

        Object[][] contents = getContents();
        Map temp = createMap(contents.length);
        for (int i = 0; i < contents.length; ++i) {
            // key must be non-null String, value must be non-null
            String key = (String) contents[i][0];
            Object value = contents[i][1];
            if (key == null || value == null) {
                throw new NullPointerException();
            }
            temp.put(key, value);
        }
        lookup = temp;
    }
    
    /**
     * Lets subclasses provide specialized Map implementations.
     * Default uses HashMap.
     */
    protected Map createMap(int size) {
        return new HashMap(size);
    }

    private Map lookup = null;
}
