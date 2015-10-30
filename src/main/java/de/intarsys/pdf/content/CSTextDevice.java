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

/**
 * A common superclass for "text only" devices.
 */
abstract public class CSTextDevice extends CSBasicDevice {

    @Override
    public void pathClipEvenOdd() {
        // ignore
    }

    @Override
    public void pathClipNonZero() {
        // ignore
    }

    @Override
    public void pathClose() {
        // ignore
    }

    @Override
    public void pathCloseFillStrokeEvenOdd() {
        // ignore
    }

    @Override
    public void pathCloseFillStrokeNonZero() {
        // ignore
    }

    @Override
    public void pathCloseStroke() {
        // ignore
    }

    @Override
    public void pathEnd() {
        // ignore
    }

    @Override
    public void pathFillEvenOdd() {
        // ignore
    }

    @Override
    public void pathFillNonZero() {
        // ignore
    }

    @Override
    public void pathFillStrokeEvenOdd() {
        // ignore
    }

    @Override
    public void pathFillStrokeNonZero() {
        // ignore
    }

    @Override
    public void pathStroke() {
        // ignore
    }

    @Override
    public void penCurveToC(float x1, float y1, float x2, float y2, float x3, float y3) {
        // ignore
    }

    @Override
    public void penCurveToV(float x2, float y2, float x3, float y3) {
        // ignore
    }

    @Override
    public void penCurveToY(float x1, float y1, float x3, float y3) {
        // ignore
    }

    @Override
    public void penLineTo(float x, float y) {
        // ignore
    }

    @Override
    public void penMoveTo(float x, float y) {
        // ignore
    }

    @Override
    public void penRectangle(float x, float y, float w, float h) {
        // ignore
    }
}
