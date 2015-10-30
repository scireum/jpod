package de.intarsys.pdf.crypt;

public abstract class AccessPermissionsProxy implements IAccessPermissions {

    private IAccessPermissions delegate;

    protected AccessPermissionsProxy(IAccessPermissions delegate) {
        super();
        this.delegate = delegate;
    }

    public IAccessPermissions getDelegate() {
        return delegate;
    }

    public boolean mayAssemble() {
        return getDelegate().mayAssemble();
    }

    public boolean mayCopy() {
        return getDelegate().mayCopy();
    }

    public boolean mayExtract() {
        return getDelegate().mayExtract();
    }

    public boolean mayFillForm() {
        return getDelegate().mayFillForm();
    }

    public boolean mayModify() {
        return getDelegate().mayModify();
    }

    public boolean mayModifyAnnotation() {
        return getDelegate().mayModifyAnnotation();
    }

    public boolean mayPrint() {
        return getDelegate().mayPrint();
    }

    public boolean mayPrintHighQuality() {
        return getDelegate().mayPrintHighQuality();
    }
}
