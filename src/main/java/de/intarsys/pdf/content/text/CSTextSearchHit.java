/*
 * intarsys consulting gmbh
 * all rights reserved
 *
 */
package de.intarsys.pdf.content.text;

import java.awt.geom.Rectangle2D;

/**
 * A search hit.
 * 
 */
public class CSTextSearchHit {

	private StringBuilder buffer;

	private Rectangle2D rect;

	private String prefix;

	private String suffix;

	public CSTextSearchHit() {
		super();
		buffer = new StringBuilder();
	}

	public void add(char c, Rectangle2D charRect) {
		buffer.append(c);
		if (rect == null) {
			rect = (Rectangle2D) charRect.clone();
		} else {
			rect.add(charRect);
		}
	}

	public String getLabel() {
		return getTrimmedPrefix() + getText() + getSuffix() + "...";
	}

	public Rectangle2D getRect() {
		return rect;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getText() {
		return buffer.toString();
	}

	/**
	 * prefix is trimmed to one word or two words if the last word is shorter
	 * than 4 characters
	 * 
	 * @return
	 */
	private String getTrimmedPrefix() {
		int i = prefix.length() - 1;
		while (i > 0) {
			char c = prefix.charAt(i);
			if (Character.isWhitespace(c)) {
				if (prefix.length() - i >= 5) {
					i++;
					break;
				}
			}
			i--;
		}
		return prefix.substring(i);
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
}
