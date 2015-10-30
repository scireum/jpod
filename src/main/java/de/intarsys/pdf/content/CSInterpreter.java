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

import de.intarsys.pdf.pd.PDColorSpace;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDResources;
import de.intarsys.tools.string.StringTools;

import java.util.Map;

/**
 * An abstrct superclass for implementing an interpreter for PDF content
 * streams.
 */
abstract public class CSInterpreter implements ICSInterpreter {

    protected static final int PageLevel = 0;

    protected static final int TextObject = 1;

    protected static final int ShadingObject = 2;

    protected static final int ExternalObject = 3;

    protected static final int InLineImageObject = 4;

    protected static final int ClippingObject = 5;

    protected static final int PathObject = 6;

    /**
     * The options for the rendering process
     */
    private Map options;

    private ICSExceptionHandler exceptionHandler;

    protected CSInterpreterFrame frame;

    private boolean interruptible;

    public CSInterpreter(Map paramOptions) {
        super();
        options = paramOptions;
    }

    private boolean checkInterrupt() {
        return isInterruptible() && Thread.currentThread().isInterrupted();
    }

    protected CSInterpreterFrame createFrame() {
        return new CSInterpreterFrame();
    }

    protected void decCompatibilitySectionDepth() {
        frame.compatibilitySectionDepth--;
    }

    protected PDDocument getDoc() {
        if (frame.doc == null) {
            if (getResources() != null) {
                frame.doc = getResources().getDoc();
            }
        }
        return frame.doc;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSInterpreter#getExceptionHandler()
     */
    public ICSExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSInterpreter#getOptions()
     */
    public Map getOptions() {
        return options;
    }

    protected PDResources getResources() {
        return frame.resources;
    }

    protected void handleError(CSError error) throws CSException {
        if (exceptionHandler != null) {
            exceptionHandler.error(error);
        } else {
            throw error;
        }
    }

    protected void handleWarning(CSWarning warning) throws CSException {
        if (exceptionHandler != null) {
            exceptionHandler.warning(warning);
        } else {
            // it is just a warning...
        }
    }

    protected void incCompatibilitySectionDepth() {
        frame.compatibilitySectionDepth++;
    }

    protected boolean isCompatibilitySection() {
        return frame.compatibilitySectionDepth > 0;
    }

    public boolean isInterruptible() {
        return interruptible;
    }

    protected void notSupported(CSOperation operation) throws CSException {
        if (isCompatibilitySection()) {
            return;
        }
        handleWarning(new CSNotSupported("operation " //$NON-NLS-1$
                                         + StringTools.safeString(operation) + " not supported")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSInterpreter#process(de.intarsys.pdf.content
     * .CSContent, de.intarsys.pdf.pd.PDResources)
     */
    public void process(CSContent pContent, PDResources pResources) throws CSException {
        if (pContent == null) {
            return;
        }
        CSInterpreterFrame oldFrame = frame;
        try {
            frame = createFrame();
            frame.resources = pResources;
            if (frame.resources == null && oldFrame != null) {
                // this is not exactly what the spec said..
                // currently we need this for use with Type3 fonts without
                // resources.
                frame.resources = oldFrame.resources;
            }
            // cache default colorspaces now
            if (frame.resources != null) {
                try {
                    frame.defaultCMYK = frame.resources.getColorSpaceResource(PDColorSpace.CN_CS_DefaultCMYK);
                } catch (Exception ex) {
                    getExceptionHandler().warning(new CSWarning(ex));
                }
                try {
                    frame.defaultRGB = frame.resources.getColorSpaceResource(PDColorSpace.CN_CS_DefaultRGB);
                } catch (Exception ex) {
                    getExceptionHandler().warning(new CSWarning(ex));
                }
                try {
                    frame.defaultGray = frame.resources.getColorSpaceResource(PDColorSpace.CN_CS_DefaultGray);
                } catch (Exception ex) {
                    getExceptionHandler().warning(new CSWarning(ex));
                }
            } else {
                frame.defaultCMYK = null;
                frame.defaultRGB = null;
                frame.defaultGray = null;
            }
            int len = pContent.size();
            for (int i = 0; i < len; i++) {
                if (checkInterrupt()) {
                    break;
                }
                CSOperation operation = pContent.getOperation(i);
                try {
                    process(operation);
                } catch (CSError e) {
                    handleError(e);
                } catch (CSWarning w) {
                    handleWarning(w);
                } catch (RuntimeException e) {
                    handleError(new CSError("unexpected exception", e)); //$NON-NLS-1$
                }
            }
        } finally {
            frame = oldFrame;
        }
    }

    protected void process(CSOperation operation) throws CSException {
        byte[] token = operation.getOperatorToken();
        switch (token[0]) {
            case 'q':
                render_q(operation);
                break;
            case 'Q':
                render_Q(operation);
                break;
            case 'T':
                switch (token[1]) {
                    case 'j':
                        render_Tj(operation);
                        break;
                    case 'J':
                        render_TJ(operation);
                        break;
                    case 'f':
                        render_Tf(operation);
                        break;
                    case 'd':
                        render_Td(operation);
                        break;
                    case 'L':
                        render_TL(operation);
                        break;
                    case 'D':
                        render_TD(operation);
                        break;
                    case 'c':
                        render_Tc(operation);
                        break;
                    case 'm':
                        render_Tm(operation);
                        break;
                    case 'r':
                        render_Tr(operation);
                        break;
                    case 's':
                        render_Ts(operation);
                        break;
                    case 'w':
                        render_Tw(operation);
                        break;
                    case 'z':
                        render_Tz(operation);
                        break;
                    case '*':
                        render_Tstar(operation);
                        break;
                }
                break;
            case 'n':
                render_n(operation);
                break;
            case 's':
                if (token.length == 1) {
                    render_s(operation);
                } else {
                    switch (token[1]) {
                        case 'c':
                            if (token.length == 2) {
                                render_sc(operation);
                            } else {
                                render_scn(operation);
                            }
                            break;
                        case 'h':
                            render_sh(operation);
                            break;
                    }
                }
                break;
            case 'g':
                if (token.length == 1) {
                    render_g(operation);
                } else {
                    render_gs(operation);
                }
                break;
            case 'r':
                switch (token[1]) {
                    case 'e':
                        render_re(operation);
                        break;
                    case 'g':
                        render_rg(operation);
                        break;
                    case 'i':
                        render_ri(operation);
                        break;
                }
                break;
            case 'R':
                render_RG(operation);
                break;
            case 'm':
                render_m(operation);
                break;
            case 'l':
                render_l(operation);
                break;
            case 'f':
                if (token.length == 1) {
                    render_f(operation);
                } else {
                    render_fstar(operation);
                }
                break;
            case 'B':
                if (token.length == 1) {
                    render_B(operation);
                } else {
                    switch (token[1]) {
                        case '*':
                            render_Bstar(operation);
                            break;
                        case 'T':
                            render_BT(operation);
                            break;
                        case 'M':
                            render_BMC(operation);
                            break;
                        case 'D':
                            render_BDC(operation);
                            break;
                        case 'X':
                            render_BX(operation);
                            break;
                    }
                }
                break;
            case 'b':
                if (token.length == 1) {
                    render_b(operation);
                } else {
                    render_bstar(operation);
                }
                break;
            case 'S':
                if (token.length == 1) {
                    render_S(operation);
                } else {
                    if (token.length == 2) {
                        render_SC(operation);
                    } else {
                        render_SCN(operation);
                    }
                }
                break;
            case 'h':
                render_h(operation);
                break;
            case 'W':
                if (token.length == 1) {
                    render_W(operation);
                } else {
                    render_Wstar(operation);
                }
                break;
            case 'c':
                if (token.length == 1) {
                    render_c(operation);
                } else {
                    switch (token[1]) {
                        case 'm':
                            render_cm(operation);
                            break;
                        case 's':
                            render_cs(operation);
                            break;
                    }
                }
                break;
            case 'E':
                switch (token[1]) {
                    case 'T':
                        render_ET(operation);
                        break;
                    case 'M':
                        render_EMC(operation);
                        break;
                    case 'I':
                        render_EI(operation);
                        break;
                    case 'X':
                        render_EX(operation);
                        break;
                }
                break;
            case 'x':
                switch (token[1]) {
                    case 'j':
                        render_Tj(operation);
                        break;
                    case 'J':
                        render_TJ(operation);
                        break;
                }
                break;
            case 'G':
                render_G(operation);
                break;
            case '\'':
                render_Quote(operation);
                break;
            case '"':
                render_DoubleQuote(operation);
                break;
            case 'C':
                render_CS(operation);
                break;
            case 'd':
                if (token.length == 1) {
                    render_d(operation);
                } else {
                    switch (token[1]) {
                        case '0':
                            render_d0(operation);
                            break;
                        case '1':
                            render_d1(operation);
                            break;
                    }
                }
                break;
            case 'D':
                switch (token[1]) {
                    case 'o':
                        render_Do(operation);
                        break;
                    case 'P':
                        render_DP(operation);
                        break;
                }
                break;
            case 'F':
                render_F(operation);
                break;
            case 'i':
                render_i(operation);
                break;
            case 'j':
                render_j(operation);
                break;
            case 'J':
                render_J(operation);
                break;
            case 'K':
                render_K(operation);
                break;
            case 'k':
                render_k(operation);
                break;
            case 'M':
                if (token.length == 1) {
                    render_M(operation);
                } else {
                    render_MP(operation);
                }
                break;
            case 'v':
                render_v(operation);
                break;
            case 'w':
                render_w(operation);
                break;
            case 'y':
                render_y(operation);
                break;
            default:
                notSupported(operation);
                break;
        }
    }

    protected void render_b(CSOperation operation) throws CSException {
        // close, fill and stroke path using nonzero winding rule
        notSupported(operation);
    }

    protected void render_B(CSOperation operation) throws CSException {
        // fill and stroke path using nonzero winding rule
        notSupported(operation);
    }

    protected void render_BDC(CSOperation operation) throws CSException {
        // begin marked content sequence with property
        notSupported(operation);
    }

    protected void render_BMC(CSOperation operation) throws CSException {
        // begin marked content sequence
        notSupported(operation);
    }

    protected void render_bstar(CSOperation operation) throws CSException {
        // close, fill and stroke path using even/odd rule
        notSupported(operation);
    }

    protected void render_Bstar(CSOperation operation) throws CSException {
        // fill and stroke path using even/odd rule
        notSupported(operation);
    }

    protected void render_BT(CSOperation operation) throws CSException {
        // begin text
        notSupported(operation);
    }

    protected void render_BX(CSOperation operation) throws CSException {
        // begin compatibility section
        incCompatibilitySectionDepth();
    }

    protected void render_c(CSOperation operation) throws CSException {
        // append curved segment
        notSupported(operation);
    }

    protected void render_cm(CSOperation operation) throws CSException {
        // concatenate matrix
        notSupported(operation);
    }

    protected void render_cs(CSOperation operation) throws CSException {
        // set color space for non stroking
        notSupported(operation);
    }

    protected void render_CS(CSOperation operation) throws CSException {
        // set color space for stroking
        notSupported(operation);
    }

    protected void render_d(CSOperation operation) throws CSException {
        // set line dash pattern
        notSupported(operation);
    }

    protected void render_d0(CSOperation operation) throws CSException {
        // set glyph width in type 3
        notSupported(operation);
    }

    protected void render_d1(CSOperation operation) throws CSException {
        // set glyph width and bounding box
        notSupported(operation);
    }

    protected void render_Do(CSOperation operation) throws CSException {
        // invoke XObject
        notSupported(operation);
    }

    protected void render_DoubleQuote(CSOperation operation) throws CSException {
        // set word and character spacing, move to next line, show text
        notSupported(operation);
    }

    protected void render_DP(CSOperation operation) throws CSException {
        // define marked content with property
        notSupported(operation);
    }

    protected void render_EI(CSOperation operation) throws CSException {
        // end inline image
        notSupported(operation);
    }

    protected void render_EMC(CSOperation operation) throws CSException {
        // end marked content
        notSupported(operation);
    }

    protected void render_ET(CSOperation operation) throws CSException {
        // end text
        notSupported(operation);
    }

    protected void render_EX(CSOperation operation) throws CSException {
        // end compatibility
        decCompatibilitySectionDepth();
    }

    protected void render_f(CSOperation operation) throws CSException {
        // fill path using nonzero winding rule
        notSupported(operation);
    }

    protected void render_F(CSOperation operation) throws CSException {
        // fill path using nonzero winding rule (obsolete)
        notSupported(operation);
    }

    protected void render_fstar(CSOperation operation) throws CSException {
        // fill path using even/odd rule
        notSupported(operation);
    }

    protected void render_g(CSOperation operation) throws CSException {
        // gray level for non stroking operations
        notSupported(operation);
    }

    protected void render_G(CSOperation operation) throws CSException {
        // gray level for stroking operations
        notSupported(operation);
    }

    protected void render_gs(CSOperation operation) throws CSException {
        // set parameters from graphics state parameters
        notSupported(operation);
    }

    protected void render_h(CSOperation operation) throws CSException {
        // close subpath, line to start
        notSupported(operation);
    }

    protected void render_i(CSOperation operation) throws CSException {
        // set flatness tolerance
        notSupported(operation);
    }

    protected void render_j(CSOperation operation) throws CSException {
        // set line joins style
        notSupported(operation);
    }

    protected void render_J(CSOperation operation) throws CSException {
        // set line cap style
        notSupported(operation);
    }

    protected void render_k(CSOperation operation) throws CSException {
        // set CMYK color for non stroking
        notSupported(operation);
    }

    protected void render_K(CSOperation operation) throws CSException {
        // set CMYK color for stroking
        notSupported(operation);
    }

    protected void render_l(CSOperation operation) throws CSException {
        // append line to path
        notSupported(operation);
    }

    protected void render_m(CSOperation operation) throws CSException {
        // move current point
        notSupported(operation);
    }

    protected void render_M(CSOperation operation) throws CSException {
        // set miter limit
        notSupported(operation);
    }

    protected void render_MP(CSOperation operation) throws CSException {
        // define marked content point
        notSupported(operation);
    }

    protected void render_n(CSOperation operation) throws CSException {
        // end path without filling or stroking
        notSupported(operation);
    }

    protected void render_q(CSOperation operation) throws CSException {
        // save graphics state
        notSupported(operation);
    }

    protected void render_Q(CSOperation operation) throws CSException {
        // restore graphics state
        notSupported(operation);
    }

    protected void render_Quote(CSOperation operation) throws CSException {
        // move to next line and show text
        notSupported(operation);
    }

    protected void render_re(CSOperation operation) throws CSException {
        // append rectangle to path
        notSupported(operation);
    }

    protected void render_rg(CSOperation operation) throws CSException {
        // set RGB color for non stroking
        notSupported(operation);
    }

    protected void render_RG(CSOperation operation) throws CSException {
        // set RGB color for stroking
        notSupported(operation);
    }

    protected void render_ri(CSOperation operation) throws CSException {
        // set color rendering intent
        notSupported(operation);
    }

    protected void render_s(CSOperation operation) throws CSException {
        // close and stroke path
        notSupported(operation);
    }

    protected void render_S(CSOperation operation) throws CSException {
        // stroke path
        notSupported(operation);
    }

    protected void render_sc(CSOperation operation) throws CSException {
        // set color for non stroking
        notSupported(operation);
    }

    protected void render_SC(CSOperation operation) throws CSException {
        // set color for stroking
        notSupported(operation);
    }

    protected void render_scn(CSOperation operation) throws CSException {
        // set color for non stroking (ICCBased, special color spaces)
        notSupported(operation);
    }

    protected void render_SCN(CSOperation operation) throws CSException {
        // set color for stroking (ICCBased, special color spaces)
        notSupported(operation);
    }

    protected void render_sh(CSOperation operation) throws CSException {
        // paint area defined by shading pattern
        notSupported(operation);
    }

    protected void render_Tc(CSOperation operation) throws CSException {
        // set character spacing
        notSupported(operation);
    }

    protected void render_Td(CSOperation operation) throws CSException {
        // move text position
        notSupported(operation);
    }

    protected void render_TD(CSOperation operation) throws CSException {
        // move text position and set leading
        notSupported(operation);
    }

    protected void render_Tf(CSOperation operation) throws CSException {
        // set text font and size
        notSupported(operation);
    }

    protected void render_Tj(CSOperation operation) throws CSException {
        // show text
        notSupported(operation);
    }

    protected void render_TJ(CSOperation operation) throws CSException {
        // show text, allowing individual positioning
        notSupported(operation);
    }

    protected void render_TL(CSOperation operation) throws CSException {
        // set text leading
        notSupported(operation);
    }

    protected void render_Tm(CSOperation operation) throws CSException {
        // set text matrix and text line matrix
        notSupported(operation);
    }

    protected void render_Tr(CSOperation operation) throws CSException {
        // set text rendering mode
        notSupported(operation);
    }

    protected void render_Ts(CSOperation operation) throws CSException {
        // set text rise
        notSupported(operation);
    }

    protected void render_Tstar(CSOperation operation) throws CSException {
        // move to start of next line
        notSupported(operation);
    }

    protected void render_Tw(CSOperation operation) throws CSException {
        // set word spacing
        notSupported(operation);
    }

    protected void render_Tz(CSOperation operation) throws CSException {
        // set horizontal text scaling
        notSupported(operation);
    }

    protected void render_v(CSOperation operation) throws CSException {
        // append curved segment to path (initial point replicated)
        notSupported(operation);
    }

    protected void render_w(CSOperation operation) throws CSException {
        // set line width
        notSupported(operation);
    }

    protected void render_W(CSOperation operation) throws CSException {
        // request clipping path using nonzero winding rule
        notSupported(operation);
    }

    protected void render_Wstar(CSOperation operation) throws CSException {
        // request clipping path using even/odd winding rule
        notSupported(operation);
    }

    protected void render_y(CSOperation operation) throws CSException {
        // append curved segment to path (final point replicated)
        notSupported(operation);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSInterpreter#setExceptionHandler(de.intarsys
     * .pdf.content.ICSExceptionHandler)
     */
    public void setExceptionHandler(ICSExceptionHandler errorHandler) {
        this.exceptionHandler = errorHandler;
    }

    public void setInterruptible(boolean interruptible) {
        this.interruptible = interruptible;
    }
}
