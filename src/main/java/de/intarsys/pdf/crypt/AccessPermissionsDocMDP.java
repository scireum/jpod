package de.intarsys.pdf.crypt;

import de.intarsys.pdf.pd.PDTransformParams;

public class AccessPermissionsDocMDP extends AccessPermissionsProxy {

	private PDTransformParams parameters;

	public AccessPermissionsDocMDP(IAccessPermissions permissions,
			PDTransformParams parameters) {
		super(permissions);
		this.parameters = parameters;
	}

	public PDTransformParams getParameters() {
		return parameters;
	}

	protected int getPermissions() {
		return getParameters().getPermissions();
	}

	@Override
	public boolean mayAssemble() {
		switch (getPermissions()) {
		case 0:
			return super.mayAssemble();
		default:
			return false;
		}
	}

	@Override
	public boolean mayFillForm() {
		switch (getPermissions()) {
		case 1:
			return false;
		default:
			return super.mayFillForm();
		}
	}

	@Override
	public boolean mayModify() {
		switch (getPermissions()) {
		case 0:
			return super.mayModify();
		default:
			return false;
		}
	}

	@Override
	public boolean mayModifyAnnotation() {
		switch (getPermissions()) {
		case 0:
			return false;
		case 1:
			return false;
		case 2:
			return false;
		default:
			return super.mayModifyAnnotation();
		}
	}

}
