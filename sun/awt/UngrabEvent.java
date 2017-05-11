/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.awt;

import java.awt.AWTEvent;
import java.awt.Component;

/**
 * Sent when one of the following events occur on the grabbed window: <ul>
 * <li> it looses focus, but not to one of the owned windows
 * <li> mouse click on the outside area happens (except for one of the owned windows)
 * <li> switch to another application or desktop happens
 * <li> click in the non-client area of the owning window or this window happens
 * </ul>
 *
 * <p>Notice that this event is not generated on mouse click inside of the window area.
 * <p>To listen for this event, install AWTEventListener with {@value sun.awt.SunToolkit#GRAB_EVENT_MASK}
 */
public class UngrabEvent extends AWTEvent {
    public UngrabEvent(Component source) {
        super(source, 0xffff);
    }

    public String toString() {
        return "sun.awt.UngrabEvent[" + getSource() + "]";
    }
}
