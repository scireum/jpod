package de.intarsys.pdf.postscript;

public class PSName extends PSObject {

	public final String name;

	public PSName(String name) {
		super();
		this.name = name;
	}

	@Override
	public void accept(Handler handler) throws ParseException {
		handler.processIdentifier(name);
	}
}
