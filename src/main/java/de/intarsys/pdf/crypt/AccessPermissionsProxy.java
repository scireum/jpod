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

    @Override
    public boolean mayAssemble() {
        return getDelegate().mayAssemble();
    }

    @Override
    public boolean mayCopy() {
        return getDelegate().mayCopy();
    }

    @Override
    public boolean mayExtract() {
        return getDelegate().mayExtract();
    }

    @Override
    public boolean mayFillForm() {
        return getDelegate().mayFillForm();
    }

    @Override
    public boolean mayModify() {
        return getDelegate().mayModify();
    }

    @Override
    public boolean mayModifyAnnotation() {
        return getDelegate().mayModifyAnnotation();
    }

    @Override
    public boolean mayPrint() {
        return getDelegate().mayPrint();
    }

    @Override
    public boolean mayPrintHighQuality() {
        return getDelegate().mayPrintHighQuality();
    }
}
