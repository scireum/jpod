/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.pdf.content;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class enumerating the operators valid in a content stream. PDF 1.4 complete
 * operator summary (App A)
 */
public class CSOperators {
    // list of well known operators
    static public final CSOperator CSO_BMC = CSOperator.create("BMC"); //$NON-NLS-1$

    static public final CSOperator CSO_EMC = CSOperator.create("EMC"); //$NON-NLS-1$

    static public final CSOperator CSO_BT = CSOperator.create("BT"); //$NON-NLS-1$

    static public final CSOperator CSO_ET = CSOperator.create("ET"); //$NON-NLS-1$

    static public final CSOperator CSO_Tf = CSOperator.create("Tf"); //$NON-NLS-1$

    static public final CSOperator CSO_Tstar = CSOperator.create("T*"); //$NON-NLS-1$

    static public final CSOperator CSO_W = CSOperator.create("W"); //$NON-NLS-1$

    static public final CSOperator CSO_h = CSOperator.create("h"); //$NON-NLS-1$

    static public final CSOperator CSO_bstar = CSOperator.create("b*"); //$NON-NLS-1$

    static public final CSOperator CSO_b = CSOperator.create("b"); //$NON-NLS-1$

    static public final CSOperator CSO_s = CSOperator.create("s"); //$NON-NLS-1$

    static public final CSOperator CSO_y = CSOperator.create("y"); //$NON-NLS-1$

    static public final CSOperator CSO_v = CSOperator.create("v"); //$NON-NLS-1$

    static public final CSOperator CSO_c = CSOperator.create("c"); //$NON-NLS-1$

    static public final CSOperator CSO_Wstar = CSOperator.create("W*"); //$NON-NLS-1$

    static public final CSOperator CSO_fstar = CSOperator.create("f*"); //$NON-NLS-1$

    static public final CSOperator CSO_Bstar = CSOperator.create("B*"); //$NON-NLS-1$

    static public final CSOperator CSO_f = CSOperator.create("f"); //$NON-NLS-1$

    static public final CSOperator CSO_F = CSOperator.create("F"); //$NON-NLS-1$

    static public final CSOperator CSO_B = CSOperator.create("B"); //$NON-NLS-1$

    static public final CSOperator CSO_l = CSOperator.create("l"); //$NON-NLS-1$

    static public final CSOperator CSO_TD = CSOperator.create("TD"); //$NON-NLS-1$

    static public final CSOperator CSO_Td = CSOperator.create("Td"); //$NON-NLS-1$

    static public final CSOperator CSO_m = CSOperator.create("m"); //$NON-NLS-1$

    static public final CSOperator CSO_n = CSOperator.create("n"); //$NON-NLS-1$

    static public final CSOperator CSO_re = CSOperator.create("re"); //$NON-NLS-1$

    static public final CSOperator CSO_Q = CSOperator.create("Q"); //$NON-NLS-1$

    static public final CSOperator CSO_q = CSOperator.create("q"); //$NON-NLS-1$

    static public final CSOperator CSO_Tc = CSOperator.create("Tc"); //$NON-NLS-1$

    static public final CSOperator CSO_i = CSOperator.create("i"); //$NON-NLS-1$

    static public final CSOperator CSO_g = CSOperator.create("g"); //$NON-NLS-1$

    static public final CSOperator CSO_G = CSOperator.create("G"); //$NON-NLS-1$

    static public final CSOperator CSO_Tz = CSOperator.create("Tz"); //$NON-NLS-1$

    static public final CSOperator CSO_TL = CSOperator.create("TL"); //$NON-NLS-1$

    static public final CSOperator CSO_J = CSOperator.create("J"); //$NON-NLS-1$

    static public final CSOperator CSO_d = CSOperator.create("d"); //$NON-NLS-1$

    static public final CSOperator CSO_j = CSOperator.create("j"); //$NON-NLS-1$

    static public final CSOperator CSO_w = CSOperator.create("w"); //$NON-NLS-1$

    static public final CSOperator CSO_M = CSOperator.create("M"); //$NON-NLS-1$

    static public final CSOperator CSO_rg = CSOperator.create("rg"); //$NON-NLS-1$

    static public final CSOperator CSO_RG = CSOperator.create("RG"); //$NON-NLS-1$

    static public final CSOperator CSO_Tm = CSOperator.create("Tm"); //$NON-NLS-1$

    static public final CSOperator CSO_Tr = CSOperator.create("Tr"); //$NON-NLS-1$

    static public final CSOperator CSO_Ts = CSOperator.create("Ts"); //$NON-NLS-1$

    static public final CSOperator CSO_Tw = CSOperator.create("Tw"); //$NON-NLS-1$

    static public final CSOperator CSO_Tj = CSOperator.create("Tj"); //$NON-NLS-1$

    static public final CSOperator CSO_S = CSOperator.create("S"); //$NON-NLS-1$

    static public final CSOperator CSO_cm = CSOperator.create("cm"); //$NON-NLS-1$

    static public final CSOperator CSO_gs = CSOperator.create("gs"); //$NON-NLS-1$

    static public final CSOperator CSO_CS = CSOperator.create("CS"); //$NON-NLS-1$

    static public final CSOperator CSO_cs = CSOperator.create("cs"); //$NON-NLS-1$

    static public final CSOperator CSO_Do = CSOperator.create("Do"); //$NON-NLS-1$

    static public final CSOperator CSO_TJ = CSOperator.create("TJ"); //$NON-NLS-1$

    static public final CSOperator CSO_Quote = CSOperator.create("'"); //$NON-NLS-1$

    static public final CSOperator CSO_DoubleQuote = CSOperator.create("\""); //$NON-NLS-1$

    static public final CSOperator CSO_BDC = CSOperator.create("BDC"); //$NON-NLS-1$

    static public final CSOperator CSO_BI = CSOperator.create("BI"); //$NON-NLS-1$

    static public final CSOperator CSO_BX = CSOperator.create("BX"); //$NON-NLS-1$

    static public final CSOperator CSO_d0 = CSOperator.create("d0"); //$NON-NLS-1$

    static public final CSOperator CSO_d1 = CSOperator.create("d1"); //$NON-NLS-1$

    static public final CSOperator CSO_DP = CSOperator.create("DP"); //$NON-NLS-1$

    static public final CSOperator CSO_EI = CSOperator.create("EI"); //$NON-NLS-1$

    static public final CSOperator CSO_EX = CSOperator.create("EX"); //$NON-NLS-1$

    static public final CSOperator CSO_ID = CSOperator.create("ID"); //$NON-NLS-1$

    static public final CSOperator CSO_K = CSOperator.create("K"); //$NON-NLS-1$

    static public final CSOperator CSO_k = CSOperator.create("k"); //$NON-NLS-1$

    static public final CSOperator CSO_MP = CSOperator.create("MP"); //$NON-NLS-1$

    static public final CSOperator CSO_ri = CSOperator.create("ri"); //$NON-NLS-1$

    static public final CSOperator CSO_SC = CSOperator.create("SC"); //$NON-NLS-1$

    static public final CSOperator CSO_sc = CSOperator.create("sc"); //$NON-NLS-1$

    static public final CSOperator CSO_SCN = CSOperator.create("SCN"); //$NON-NLS-1$

    static public final CSOperator CSO_scn = CSOperator.create("scn"); //$NON-NLS-1$

    static public final CSOperator CSO_sh = CSOperator.create("sh"); //$NON-NLS-1$

    static public final List CSO_All = Arrays.asList(new CSOperator[]{CSO_b,
                                                                      CSO_B,
                                                                      CSO_BDC,
                                                                      CSO_BI,
                                                                      CSO_BMC,
                                                                      CSO_bstar,
                                                                      CSO_Bstar,
                                                                      CSO_BT,
                                                                      CSO_BX,
                                                                      CSO_c,
                                                                      CSO_cm,
                                                                      CSO_cs,
                                                                      CSO_CS,
                                                                      CSO_d,
                                                                      CSO_d0,
                                                                      CSO_d1,
                                                                      CSO_Do,
                                                                      CSO_DoubleQuote,
                                                                      CSO_DP,
                                                                      CSO_EI,
                                                                      CSO_EMC,
                                                                      CSO_ET,
                                                                      CSO_EX,
                                                                      CSO_f,
                                                                      CSO_F,
                                                                      CSO_fstar,
                                                                      CSO_g,
                                                                      CSO_G,
                                                                      CSO_gs,
                                                                      CSO_h,
                                                                      CSO_i,
                                                                      CSO_ID,
                                                                      CSO_j,
                                                                      CSO_J,
                                                                      CSO_k,
                                                                      CSO_K,
                                                                      CSO_l,
                                                                      CSO_m,
                                                                      CSO_M,
                                                                      CSO_MP,
                                                                      CSO_n,
                                                                      CSO_q,
                                                                      CSO_Q,
                                                                      CSO_Quote,
                                                                      CSO_re,
                                                                      CSO_rg,
                                                                      CSO_RG,
                                                                      CSO_ri,
                                                                      CSO_s,
                                                                      CSO_S,
                                                                      CSO_sc,
                                                                      CSO_scn,
                                                                      CSO_SC,
                                                                      CSO_SCN,
                                                                      CSO_sh,
                                                                      CSO_Tc,
                                                                      CSO_Td,
                                                                      CSO_TD,
                                                                      CSO_Tf,
                                                                      CSO_Tj,
                                                                      CSO_TJ,
                                                                      CSO_TL,
                                                                      CSO_Tm,
                                                                      CSO_Tr,
                                                                      CSO_Ts,
                                                                      CSO_Tstar,
                                                                      CSO_Tw,
                                                                      CSO_Tz,
                                                                      CSO_v,
                                                                      CSO_w,
                                                                      CSO_W,
                                                                      CSO_Wstar,
                                                                      CSO_y});

    static private final Map descriptions = new HashMap();

    static {
        descriptions.put(CSO_b, "close, fill and stroke path using nonzero winding rule"); //$NON-NLS-1$
        descriptions.put(CSO_B, "fill and stroke path using nonzero winding rule"); //$NON-NLS-1$
        descriptions.put(CSO_bstar, "close, fill and stroke path using even-odd rule"); //$NON-NLS-1$
        descriptions.put(CSO_Bstar, "fill and stroke path using even-odd rule"); //$NON-NLS-1$
        descriptions.put(CSO_BDC, "begin marked-content with property list"); //$NON-NLS-1$
        descriptions.put(CSO_BI, "begin inline image"); //$NON-NLS-1$
        descriptions.put(CSO_BMC, "begin marked content"); //$NON-NLS-1$
        descriptions.put(CSO_BT, "begin text"); //$NON-NLS-1$
        descriptions.put(CSO_BX, "gebin compatibility section"); //$NON-NLS-1$
        descriptions.put(CSO_c, "append curved segment (three control points)"); //$NON-NLS-1$
        descriptions.put(CSO_cm, "concatenate matrix to current transformation matrix"); //$NON-NLS-1$
        descriptions.put(CSO_CS, "set color space for stroking"); //$NON-NLS-1$
        descriptions.put(CSO_cs, "set color space for non stroking"); //$NON-NLS-1$
        descriptions.put(CSO_d, "set line dash pattern"); //$NON-NLS-1$
        descriptions.put(CSO_d0, "set glyph width in type 3 font"); //$NON-NLS-1$
        descriptions.put(CSO_d1, "set glyph width and bounding box in type 3 font"); //$NON-NLS-1$
        descriptions.put(CSO_Do, "invoke named XObject"); //$NON-NLS-1$
        descriptions.put(CSO_DP, "define marked content point with property list"); //$NON-NLS-1$
        descriptions.put(CSO_EI, "end inline image"); //$NON-NLS-1$
        descriptions.put(CSO_EMC, "end marked content sequence"); //$NON-NLS-1$
        descriptions.put(CSO_ET, "end text object"); //$NON-NLS-1$
        descriptions.put(CSO_EX, "end compatibility section"); //$NON-NLS-1$
        descriptions.put(CSO_f, "fill path using nonzero winding rule"); //$NON-NLS-1$
        descriptions.put(CSO_F, "fill path using nonzero winding rule"); //$NON-NLS-1$
        descriptions.put(CSO_fstar, "fill path using even-odd rule"); //$NON-NLS-1$
        descriptions.put(CSO_G, "set gray level for stroking"); //$NON-NLS-1$
        descriptions.put(CSO_g, "set gray level for nonstroking"); //$NON-NLS-1$
        descriptions.put(CSO_gs, "set parameters from graphics state parameter dict"); //$NON-NLS-1$
        descriptions.put(CSO_h, "close subpath"); //$NON-NLS-1$
        descriptions.put(CSO_i, "set flatness tolerance"); //$NON-NLS-1$
        descriptions.put(CSO_ID, "bgein inline image data"); //$NON-NLS-1$
        descriptions.put(CSO_j, "set line join style"); //$NON-NLS-1$
        descriptions.put(CSO_J, "set line cap style"); //$NON-NLS-1$
        descriptions.put(CSO_K, "set CMYK color for stroking"); //$NON-NLS-1$
        descriptions.put(CSO_k, "set CMYK color for non stroking"); //$NON-NLS-1$
        descriptions.put(CSO_l, "append straight line segment to path"); //$NON-NLS-1$
        descriptions.put(CSO_m, "begin new subpath"); //$NON-NLS-1$
        descriptions.put(CSO_M, "set miter limit"); //$NON-NLS-1$
        descriptions.put(CSO_MP, "define marked content point"); //$NON-NLS-1$
        descriptions.put(CSO_n, "end path without filling or stroking"); //$NON-NLS-1$
        descriptions.put(CSO_q, "save graphics state"); //$NON-NLS-1$
        descriptions.put(CSO_Q, "restore graphics state"); //$NON-NLS-1$
        descriptions.put(CSO_re, "append rectangle to path"); //$NON-NLS-1$
        descriptions.put(CSO_RG, "set RGB color for stroking operations"); //$NON-NLS-1$
        descriptions.put(CSO_rg, "set RGB color for nonstroking operations"); //$NON-NLS-1$
        descriptions.put(CSO_ri, "set color rendering intent"); //$NON-NLS-1$
        descriptions.put(CSO_s, "close and stroke path"); //$NON-NLS-1$
        descriptions.put(CSO_S, "stroke path"); //$NON-NLS-1$
        descriptions.put(CSO_SC, "set color for stroking operations"); //$NON-NLS-1$
        descriptions.put(CSO_sc, "set color for nonstroking operations"); //$NON-NLS-1$
        descriptions.put(CSO_SCN,
                         "set color for stroking operations (ICCBased and special color spaces)"); //$NON-NLS-1$
        descriptions.put(CSO_scn,
                         "set color for nonstroking operations (ICCBased and special color spaces)"); //$NON-NLS-1$
        descriptions.put(CSO_sh, "paint area defined by shading pattern"); //$NON-NLS-1$
        descriptions.put(CSO_Tstar, "move to start of next line"); //$NON-NLS-1$
        descriptions.put(CSO_Tc, "set character spacing"); //$NON-NLS-1$
        descriptions.put(CSO_Td, "move text position"); //$NON-NLS-1$
        descriptions.put(CSO_TD, "move text position and set leading"); //$NON-NLS-1$
        descriptions.put(CSO_Tf, "set text font and size"); //$NON-NLS-1$
        descriptions.put(CSO_Tj, "show text"); //$NON-NLS-1$
        descriptions.put(CSO_TJ, "show text, allowing individual glyph positioning"); //$NON-NLS-1$
        descriptions.put(CSO_TL, "set text leading"); //$NON-NLS-1$
        descriptions.put(CSO_Tm, "set text matrix and text line matrix"); //$NON-NLS-1$
        descriptions.put(CSO_Tr, "set text rendering mode"); //$NON-NLS-1$
        descriptions.put(CSO_Ts, "set text rise"); //$NON-NLS-1$
        descriptions.put(CSO_Tw, "set word spacing"); //$NON-NLS-1$
        descriptions.put(CSO_Tz, "set horizontal text spacing"); //$NON-NLS-1$
        descriptions.put(CSO_v, "append curved segment to path (initial point replicated)"); //$NON-NLS-1$
        descriptions.put(CSO_w, "set line width"); //$NON-NLS-1$
        descriptions.put(CSO_W, "set clipping path using nonzero winding rule"); //$NON-NLS-1$
        descriptions.put(CSO_Wstar, "set clipping path using even-odd rule"); //$NON-NLS-1$
        descriptions.put(CSO_y, "append curved segment to path (final point rpelicated)"); //$NON-NLS-1$
        descriptions.put(CSO_Quote, "move to next line and show text"); //$NON-NLS-1$
        descriptions.put(CSO_DoubleQuote, "set word and character spacing, move to next line, show text"); //$NON-NLS-1$
    }

    /**
     * Enumeration class
     */
    private CSOperators() {
        //
    }

    public static String getDescription(CSOperator operator) {
        String result = (String) descriptions.get(operator);
        if (result == null) {
            result = "<unkown>"; //$NON-NLS-1$
        }
        return result;
    }
}
