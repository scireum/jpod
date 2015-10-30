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
package de.intarsys.pdf.pd;

/**
 * The flags of an annotation.
 * <p>
 * The flags are bits of an integer.<br>
 * The following bits are defined (more may exist).
 * </p>
 * <ul>
 * <li>0: default
 * <li>1: Invisible
 * <li>2: Hidden
 * <li>3: Print
 * <li>4: NoZoom
 * <li>5: NoRotate
 * <li>6: NoView
 * <li>7: ReadOnly
 * <li>8: Locked
 * <li>9: ToggleNoView
 * <li>10: LockedContents
 * </ul>
 */
public class AnnotationFlags extends AbstractBitFlags {
    final static public int Bit_Invisible = 1; // Bit position 1

    final static public int Bit_Hidden = 1 << 1; // Bit position 2

    final static public int Bit_Print = 1 << 2; // Bit position 3

    final static public int Bit_NoZoom = 1 << 3;

    final static public int Bit_NoRotate = 1 << 4;

    final static public int Bit_NoView = 1 << 5;

    final static public int Bit_ReadOnly = 1 << 6;

    final static public int Bit_Locked = 1 << 7;

    final static public int Bit_ToggleNoView = 1 << 8;

    final static public int Bit_LockedContents = 1 << 9;

    private PDAnnotation annotation;

    public AnnotationFlags(int value) {
        super(value);
    }

    public AnnotationFlags(PDAnnotation annotation) {
        super(annotation, null);
        this.annotation = annotation;
    }

    protected PDAnnotation getAnnotation() {
        return annotation;
    }

    @Override
    protected int getValueInObject() {
        return getAnnotation().basicGetFlags();
    }

    public boolean isHidden() {
        return isSetAnd(Bit_Hidden);
    }

    public boolean isInvisible() {
        return isSetAnd(Bit_Invisible);
    }

    public boolean isLocked() {
        return isSetAnd(Bit_Locked);
    }

    public boolean isLockedContents() {
        return isSetAnd(Bit_LockedContents);
    }

    public boolean isNoRotate() {
        return isSetAnd(Bit_NoRotate);
    }

    public boolean isNoView() {
        return isSetAnd(Bit_NoView);
    }

    public boolean isNoZoom() {
        return isSetAnd(Bit_NoZoom);
    }

    public boolean isPrint() {
        return isSetAnd(Bit_Print);
    }

    public boolean isReadOnly() {
        return isSetAnd(Bit_ReadOnly);
    }

    public boolean isToggleNoView() {
        return isSetAnd(Bit_ToggleNoView);
    }

    public void setHidden(boolean f) {
        set(Bit_Hidden, f);
    }

    public void setInvisible(boolean f) {
        set(Bit_Invisible, f);
    }

    public void setLocked(boolean f) {
        set(Bit_Locked, f);
    }

    public void setLockedContents(boolean f) {
        set(Bit_LockedContents, f);
    }

    public void setNoRotate(boolean f) {
        set(Bit_NoRotate, f);
    }

    public void setNoView(boolean f) {
        set(Bit_NoView, f);
    }

    public void setNoZoom(boolean f) {
        set(Bit_NoZoom, f);
    }

    public void setPrint(boolean f) {
        set(Bit_Print, f);
    }

    public void setReadOnly(boolean f) {
        set(Bit_ReadOnly, f);
    }

    public void setToggleNoView(boolean f) {
        set(Bit_ToggleNoView, f);
    }

    @Override
    protected void setValueInObject(int newValue) {
        getAnnotation().basicSetFlags(newValue);
    }
}
