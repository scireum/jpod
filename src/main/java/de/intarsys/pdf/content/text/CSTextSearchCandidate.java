/*
 * intarsys consulting gmbh
 * all rights reserved
 *
 */
package de.intarsys.pdf.content.text;

import java.awt.geom.Rectangle2D;

/**
 * A single potential search hit.
 */
public class CSTextSearchCandidate {

    private final CSTextSearchHit hit = new CSTextSearchHit();

    private final CSTextSearcher searcher;

    private int index = 0;

    private char[] suffix = new char[CSTextSearcher.FLOATING_CONTEXT_LENGTH];

    private int suffixPos = 0;

    public CSTextSearchCandidate(CSTextSearcher searcher) {
        super();
        this.searcher = searcher;
    }

    public boolean accept(char c, Rectangle2D charRect) {
        char currentChar = searcher.basicGetSearchString().charAt(index);
        char checkChar = c;
        if (searcher.isIgnoreCase()) {
            checkChar = Character.toLowerCase(c);
        }
        if (currentChar == checkChar) {
            hit.add(c, charRect);
            index++;
            return true;
        }
        return false;
    }

    public boolean acceptSuffix(char c) {
        suffix[suffixPos++] = c;
        if (suffixPos >= CSTextSearcher.FLOATING_CONTEXT_LENGTH) {
            flush();
            return false;
        }
        return true;
    }

    public void flush() {
        hit.setSuffix(new String(suffix, 0, suffixPos));
    }

    public CSTextSearchHit getHit() {
        return hit;
    }

    public boolean isComplete() {
        return index >= searcher.basicGetSearchString().length();
    }

    public void setPrefix(String prefix) {
        hit.setPrefix(prefix);
    }

    public void setSuffix(String suffix) {
        hit.setSuffix(suffix);
    }
}
