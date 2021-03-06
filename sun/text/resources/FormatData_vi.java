/*
 * %W% %E%
 */

/*
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 *******************************************************************************
 * (C) Copyright IBM Corp. 1996-2003 - All Rights Reserved                     *
 *                                                                             *
 * The original version of this source code and documentation is copyrighted   *
 * and owned by IBM, These materials are provided under terms of a License     *
 * Agreement between IBM and Sun. This technology is protected by multiple     *
 * US and International patents. This notice and attribution to IBM may not    *
 * to removed.                                                                 *
 *******************************************************************************
 *
 * This locale data is based on the ICU's Vietnamese locale data (rev. 1.38)
 * found at:
 *
 * http://oss.software.ibm.com/cvs/icu/icu/source/data/locales/vi.txt?rev=1.38
 */

package sun.text.resources;

import java.util.ListResourceBundle;

public class FormatData_vi extends ListResourceBundle {
    /**
     * Overrides ListResourceBundle
     */
    protected final Object[][] getContents() {
        return new Object[][] {
            { "MonthNames",
                new String[] {
                    "th\u00e1ng m\u1ed9t", // january
                    "th\u00e1ng hai", // february
                    "th\u00e1ng ba", // march
                    "th\u00e1ng t\u01b0", // april
                    "th\u00e1ng n\u0103m", // may
                    "th\u00e1ng s\u00e1u", // june
                    "th\u00e1ng b\u1ea3y", // july
                    "th\u00e1ng t\u00e1m", // august
                    "th\u00e1ng ch\u00edn", // september
                    "th\u00e1ng m\u01b0\u1eddi", // october
                    "th\u00e1ng m\u01b0\u1eddi m\u1ed9t", // november
                    "th\u00e1ng m\u01b0\u1eddi hai", // december
                    "" // month 13 if applicable
                }
            },
            { "MonthAbbreviations",
                new String[] {
                    "thg 1", // abb january
                    "thg 2", // abb february
                    "thg 3", // abb march
                    "thg 4", // abb april
                    "thg 5", // abb may
                    "thg 6", // abb june
                    "thg 7", // abb july
                    "thg 8", // abb august
                    "thg 9", // abb september
                    "thg 10", // abb october
                    "thg 11", // abb november
                    "thg 12", // abb december
                    "" // abb month 13 if applicable
                }
            },
            { "DayNames",
                new String[] {
                    "Ch\u1ee7 nh\u1eadt", // Sunday
                    "Th\u1ee9 hai", // Monday
                    "Th\u1ee9 ba",  // Tuesday
                    "Th\u1ee9 t\u01b0", // Wednesday
                    "Th\u1ee9 n\u0103m", // Thursday
                    "Th\u1ee9 s\u00e1u", // Friday
                    "Th\u1ee9 b\u1ea3y" // Saturday
                }
            },
            { "DayAbbreviations",
                new String[] {
                    "CN", // abb Sunday
                    "Th 2", // abb Monday
                    "Th 3", // abb Tuesday
                    "Th 4", // abb Wednesday
                    "Th 5", // abb Thursday
                    "Th 6", // abb Friday
                    "Th 7" // abb Saturday
                }
            },
            { "AmPmMarkers",
                new String[] {
                    "SA", // am marker
                    "CH" // pm marker
                }
            },
            { "Eras",
                new String[] { // era strings
                    "tr. CN",
                    "sau CN"
                }
            },
            { "NumberElements",
                new String[] {
                    ",", // decimal separator
                    ".", // group (thousands) separator
                    ";", // list separator
                    "%", // percent sign
                    "0", // native 0 digit
                    "#", // pattern digit
                    "-", // minus sign
                    "E", // exponential
                    "\u2030", // per mille
                    "\u221e", // infinity
                    "\ufffd" // NaN
                }
            },
            { "DateTimePatterns",
                new String[] {
                    "HH:mm:ss z", // full time pattern
                    "HH:mm:ss z", // long time pattern
                    "HH:mm:ss", // medium time pattern
                    "HH:mm", // short time pattern
                    "EEEE, 'ng\u00E0y' dd MMMM 'n\u0103m' yyyy", // full date pattern
                    "'Ng\u00E0y' dd 'th\u00E1ng' M 'n\u0103m' yyyy", // long date pattern
                    "dd-MM-yyyy", // medium date pattern
                    "dd/MM/yyyy", // short date pattern
                    "{0} {1}" // date-time pattern
                }
            },
        };
    }
}
