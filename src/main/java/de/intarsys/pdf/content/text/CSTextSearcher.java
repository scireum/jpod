/*
 * intarsys consulting gmbh
 * all rights reserved
 *
 */
package de.intarsys.pdf.content.text;

import de.intarsys.pdf.content.ICSInterpreter;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDGlyphs;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple text search utility.
 */
public class CSTextSearcher extends CSCharacterParser {

    public static int FLOATING_CONTEXT_LENGTH = 28;

    private String basicSearchString;

    private ArrayList candidates = new ArrayList();

    private char[] floatingContext = new char[FLOATING_CONTEXT_LENGTH];

    private int floatingContextCurrent = 0;

    private ArrayList hits = new ArrayList();

    private boolean ignoreCase = true;

    private double maxDX = 5;

    private double maxDY = 5;

    private CSTextSearchCandidate nextCandidate;

    private ArrayList qualified = new ArrayList();

    private String searchString;

    protected String basicGetSearchString() {
        return basicSearchString;
    }

    protected void check(char foundChar, Rectangle2D charRect) {
        for (Iterator it = qualified.iterator(); it.hasNext(); ) {
            CSTextSearchCandidate candidate = (CSTextSearchCandidate) it.next();
            if (!candidate.acceptSuffix(foundChar)) {
                hits.add(candidate.getHit());
                it.remove();
            }
        }
        for (Iterator it = candidates.iterator(); it.hasNext(); ) {
            CSTextSearchCandidate candidate = (CSTextSearchCandidate) it.next();
            if (!candidate.accept(foundChar, charRect)) {
                it.remove();
            } else {
                if (candidate.isComplete()) {
                    qualified.add(candidate);
                    it.remove();
                }
            }
        }
        if (nextCandidate == null) {
            nextCandidate = new CSTextSearchCandidate(this);
        }
        if (nextCandidate.accept(foundChar, charRect)) {
            nextCandidate.setPrefix(getFloatingContextString());
            if (nextCandidate.isComplete()) {
                qualified.add(nextCandidate);
            } else {
                candidates.add(nextCandidate);
            }
            nextCandidate = null;
        }
        if (foundChar != 0) {
            floatingContext[floatingContextCurrent++] = foundChar;
            if (floatingContextCurrent == floatingContext.length) {
                floatingContextCurrent = 0;
            }
        }
    }

    protected String getFloatingContextString() {
        String a = new String(floatingContext, floatingContextCurrent, floatingContext.length - floatingContextCurrent);
        String b = new String(floatingContext, 0, floatingContextCurrent);
        return a + b;
    }

    public List getHits() {
        for (Iterator it = qualified.iterator(); it.hasNext(); ) {
            CSTextSearchCandidate candidate = (CSTextSearchCandidate) it.next();
            candidate.flush();
            hits.add(candidate.getHit());
            it.remove();
        }
        return hits;
    }

    public String getSearchString() {
        return searchString;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    @Override
    protected void onCharacterFound(PDGlyphs glyphs, Rectangle2D rect) {
        char[] chars = glyphs.getChars();
        if (chars == null) {
            chars = new char[]{' '};
        }

        double dX = lastStopX - lastStartX;
        double dY = lastStopY - lastStartY;
        if (Math.abs(dX) < maxDX) {
            if (Math.abs(dY) >= maxDY) {
                Rectangle2D spaceRect = new Rectangle2D.Float((int) lastStartX, (int) lastStartY, 0, 0);
                check(' ', spaceRect);
            }
        } else {
            Rectangle2D spaceRect = new Rectangle2D.Float((int) lastStartX, (int) lastStartY, 0, 0);
            check(' ', spaceRect);
        }
        for (int i = 0; i < chars.length; i++) {
            check(chars[i], rect);
        }
    }

    @Override
    public void open(ICSInterpreter pInterpreter) {
        super.open(pInterpreter);
        candidates.clear();
        hits.clear();
        nextCandidate = null;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        if (isIgnoreCase() && searchString != null) {
            basicSearchString = searchString.toLowerCase();
        } else {
            basicSearchString = searchString;
        }
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
        if (isIgnoreCase()) {
            basicSearchString = searchString.toLowerCase();
        } else {
            basicSearchString = searchString;
        }
    }

    @Override
    public void textSetFont(COSName name, PDFont font, float size) {
        super.textSetFont(name, font, size);
        AffineTransform tx;
        tx = (AffineTransform) getDeviceTransform().clone();
        tx.concatenate(textState.globalTransform);
        maxDX = textState.fontSize * 0.2 * tx.getScaleX();
        maxDY = textState.fontSize * 0.6 * tx.getScaleY();
    }

    @Override
    public void textSetTransform(float a, float b, float c, float d, float e, float f) {
        super.textSetTransform(a, b, c, d, e, f);
        AffineTransform tx;
        tx = (AffineTransform) getDeviceTransform().clone();
        tx.concatenate(textState.globalTransform);
        maxDX = textState.fontSize * 0.2 * tx.getScaleX();
        maxDY = textState.fontSize * 0.6 * tx.getScaleY();
    }
}
