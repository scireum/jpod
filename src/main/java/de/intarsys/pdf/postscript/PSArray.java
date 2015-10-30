package de.intarsys.pdf.postscript;

import java.util.ArrayList;
import java.util.List;

public class PSArray extends PSObject {

    private final List<PSObject> objects = new ArrayList<PSObject>();

    @Override
    public void accept(Handler handler) throws ParseException {
        for (PSObject object : objects) {
            object.accept(handler);
        }
    }

    public void add(PSObject psObject) {
        objects.add(psObject);
    }
}
