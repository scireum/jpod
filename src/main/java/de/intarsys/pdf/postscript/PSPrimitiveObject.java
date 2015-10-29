package de.intarsys.pdf.postscript;

public class PSPrimitiveObject extends PSObject {

	public final Object object;

	public PSPrimitiveObject(Object object) {
		super();
		this.object = object;
	}

	@Override
	public void accept(Handler handler) {
		handler.processLiteral(object);
	}
}
