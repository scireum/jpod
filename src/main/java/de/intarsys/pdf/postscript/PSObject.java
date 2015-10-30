package de.intarsys.pdf.postscript;

public abstract class PSObject {

    public abstract void accept(Handler handler) throws ParseException;
}
