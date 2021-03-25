/*
 * Made with all the love in the world
 * by scireum in Remshalden, Germany
 *
 * Copyright by scireum GmbH
 * http://www.scireum.de - info@scireum.de
 */

package de.intarsys.pdf.encoding;

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.font.CMap;
import de.intarsys.pdf.font.NamedCMap;

public class Test {

    public static void main(String[] args) {
        Test test = new Test();
        try {
            test.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() throws Exception {
        CMap map = NamedCMap.loadCMap(COSName.constant("Adobe-Japan1-7"));
        map.getChars(52);
        System.out.println(map);
    }
}
