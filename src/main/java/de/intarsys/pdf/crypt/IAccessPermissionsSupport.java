package de.intarsys.pdf.crypt;

public interface IAccessPermissionsSupport {

	/**
	 * The access permissions active for the document.
	 * <p>
	 * The return value of this method not only depends on the permissions
	 * encoded by the {@link ISecurityHandler}, but also on the outcome of the
	 * authentication.
	 * <p>
	 * A negative authentication should result in no permissions, a positive
	 * authentication should result either in the encoded permissions for a user
	 * authentication or in all permissions for a successful owner
	 * authentication.
	 * 
	 * @return The access permissions active for the document.
	 */
	public IAccessPermissions getAccessPermissions();

}
