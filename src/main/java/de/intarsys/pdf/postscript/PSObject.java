package de.intarsys.pdf.postscript;

abstract public class PSObject {

    abstract public void accept(Handler handler) throws ParseException;
}
