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
package de.intarsys.pdf.font;

/**
 * A codespace range for a CMap.
 * <p>
 * <p>
 * todo 1 cmap implement correct byte check
 * </p>
 */
public class CMapRange {
    /**
     * The start index of the range
     */
    private final byte[] start;

    /**
     * The end index of the range
     */
    private final byte[] end;

    /**
     *
     */
    public CMapRange(byte[] start, byte[] end) {
        super();
        this.start = start;
        this.end = end;
    }

    public boolean checkPrefix(byte[] value, int count) {
        if (count <= start.length) {
            for (int i = 0; i < count; i++) {
                if (((value[i] & 0xff) < (start[i] & 0xff)) || ((value[i] & 0xff) > (end[i] & 0xff))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean checkRange(byte[] value, int count) {
        if (count == start.length) {
            for (int i = 0; i < count; i++) {
                if (((value[i] & 0xff) < (start[i] & 0xff)) || ((value[i] & 0xff) > (end[i] & 0xff))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public int getByteCount() {
        return start.length;
    }

    public byte[] getEnd() {
        return end;
    }

    public byte[] getStart() {
        return start;
    }
}
