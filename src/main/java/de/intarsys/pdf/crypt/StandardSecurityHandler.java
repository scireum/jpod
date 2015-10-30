/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.pdf.crypt;

import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObjectKey;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.st.STDocument;

/**
 * The standard security handler as specified in the PDF reference.
 * <p>
 * Be aware that there is no internal check for permissions. The reason is
 * simply that on the API level there's nothing that really keeps you from
 * manipulating a PDF document.
 * <p>
 * On one hand, changing simply the security relevant parameters won't work as
 * the crypt key is cached. It is necessary to create and associate a new
 * security handler. Here we could check and abandon the request if no owner
 * permissions are set. But this is no real problem, as you could read the
 * complete document with user permissions and as such simply copy the root.
 * <p>
 * So we didn't even care as this would pollute the implementation while
 * providing only superficial benefits.
 */
abstract public class StandardSecurityHandler extends AbstractSecurityHandler
		implements IAccessPermissionsSupport {

	/**
	 * The default value for the access permission flags.
	 * 
	 * <p>
	 * Everything is allowed, only the reserved flags are zero.
	 * </p>
	 */
	public static final int DEFAULT_ACCESS_PERMISSIONS = 0xFFFFFFFC;

	public static final COSName DK_EncryptMetadata = COSName
			.constant("EncryptMetadata"); //$NON-NLS-1$

	public static final COSName DK_O = COSName.constant("O"); //$NON-NLS-1$

	public static final COSName DK_P = COSName.constant("P"); //$NON-NLS-1$

	public static final COSName DK_R = COSName.constant("R"); //$NON-NLS-1$
	public static final COSName DK_U = COSName.constant("U"); //$NON-NLS-1$

	/** The padding sequence as defined in the spec. */
	protected static byte[] PADDING = new byte[] { (byte) 0x28, (byte) 0xBF,
			(byte) 0x4E, (byte) 0x5E, (byte) 0x4E, (byte) 0x75, (byte) 0x8A,
			(byte) 0x41, (byte) 0x64, (byte) 0x00, (byte) 0x4E, (byte) 0x56,
			(byte) 0xFF, (byte) 0xFA, (byte) 0x01, (byte) 0x08, (byte) 0x2E,
			(byte) 0x2E, (byte) 0x00, (byte) 0xB6, (byte) 0xD0, (byte) 0x68,
			(byte) 0x3E, (byte) 0x80, (byte) 0x2F, (byte) 0x0C, (byte) 0xA9,
			(byte) 0xFE, (byte) 0x64, (byte) 0x53, (byte) 0x69, (byte) 0x7A };

	/** A dummy padding sequence for the revision 3 variant. */
	protected static byte[] USER_R3_PADDING = new byte[] { (byte) 0x28,
			(byte) 0xBF, (byte) 0x4E, (byte) 0x5E, (byte) 0x4E, (byte) 0x75,
			(byte) 0x8A, (byte) 0x41, (byte) 0x64, (byte) 0x00, (byte) 0x28,
			(byte) 0xBF, (byte) 0x4E, (byte) 0x5E, (byte) 0x4E, (byte) 0x4E };

	/**
	 * The access permissions currently active
	 */
	private IAccessPermissions accessPermissions = AccessPermissionsFull.get();

	private IAuthenticationHandler authenticationHandler;

	/** The key that was computed for the security handler upon authentication. */
	private byte[] cryptKey;

	private byte[] owner;

	private byte[] user;

	public StandardSecurityHandler() {
		super();
	}

	public void apply() throws COSSecurityException {
		//
		byte[] oPwd = createOwnerPassword(owner, user);
		COSString o = COSString.create(oPwd);
		getEncryption().cosSetField(StandardSecurityHandler.DK_O, o);
		byte[] uPwd = createUserPassword(user);
		COSString u = COSString.create(uPwd);
		getEncryption().cosSetField(StandardSecurityHandler.DK_U, u);
		// create new crypt key
		setCryptKey(createCryptKey(getUser()));
	}

	@Override
	public void attach(STDocument doc) {
		super.attach(doc);
		if (doc == null) {
			return;
		}
		COSEncryption encryption = getEncryption();
		encryption.cosSetField(COSEncryption.DK_Filter,
				StandardSecurityHandlerFactory.CN_Standard);
		encryption.cosSetField(StandardSecurityHandler.DK_R,
				COSInteger.create(getRevision()));
		// apply default permissions
		getEncryption().setFieldInt(StandardSecurityHandler.DK_P,
				DEFAULT_ACCESS_PERMISSIONS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.encryption.ISecurityHandler#authenticate()
	 */
	final public void authenticate() throws COSSecurityException {
		// reset permissions
		setActiveAccessPermissions(AccessPermissionsNone.get());
		if (authenticationHandler == null) {
			authenticationHandler = AuthenticationHandlerFactory.get()
					.createAuthenticationHandler(this);
		}
		authenticationHandler.authenticate(this);
	}

	abstract public boolean authenticateOwner(byte[] owner)
			throws COSSecurityException;

	abstract public boolean authenticateUser(byte[] user)
			throws COSSecurityException;

	public int basicGetPermissionFlags() {
		return getEncryption().getFieldInt(StandardSecurityHandler.DK_P,
				DEFAULT_ACCESS_PERMISSIONS);
	}

	public void basicSetPermissionFlags(int newValue)
			throws COSSecurityException {
		if (getEncryption().cosGetDoc() == null) {
			throw new COSSecurityException("document missing");
		}
		getEncryption().setFieldInt(StandardSecurityHandler.DK_P, newValue);
	}

	abstract protected IAccessPermissions createAccessPermissions();

	abstract protected byte[] createCryptKey(byte[] password)
			throws COSSecurityException;

	abstract protected byte[] createOwnerPassword(byte[] owner, byte[] user)
			throws COSSecurityException;

	abstract protected byte[] createUserPassword(byte[] user)
			throws COSSecurityException;

	public byte[] decrypt(COSObjectKey key, byte[] bytes)
			throws COSSecurityException {
		throw new COSSecurityException("pluggable encryption not supported"); //$NON-NLS-1$
	}

	@Override
	public void detach(STDocument doc) throws COSSecurityException {
		if (doc == null) {
			return;
		}
		COSEncryption encryption = getEncryption();
		encryption.cosRemoveField(COSEncryption.DK_Filter);
		encryption.cosRemoveField(StandardSecurityHandler.DK_R);
		encryption.cosRemoveField(StandardSecurityHandler.DK_P);
		super.detach(doc);
	}

	public byte[] encrypt(COSObjectKey key, byte[] bytes)
			throws COSSecurityException {
		throw new COSSecurityException("pluggable encryption not supported"); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.encryption.ISecurityHandler#getAccessPermissions()
	 */
	final public IAccessPermissions getAccessPermissions() {
		return accessPermissions;
	}

	public IAuthenticationHandler getAuthenticationHandler() {
		return authenticationHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.encryption.ISecurityHandler#getCryptKey()
	 */
	public byte[] getCryptKey() {
		return cryptKey;
	}

	protected byte[] getO() {
		COSString o = getEncryption().cosGetField(DK_O).asString();
		if (o != null) {
			return o.byteValue();
		}
		return null;
	}

	protected byte[] getOwner() {
		return owner;
	}

	protected byte[] getPBytes() {
		int pint = basicGetPermissionFlags();
		byte[] result = new byte[4];
		result[0] = (byte) (pint & 0xff);
		pint = pint >> 8;
		result[1] = (byte) (pint & 0xff);
		pint = pint >> 8;
		result[2] = (byte) (pint & 0xff);
		pint = pint >> 8;
		result[3] = (byte) (pint & 0xff);
		return result;
	}

	protected byte[] getPermanentFileID() throws COSSecurityException {
		STDocument stDoc = stGetDoc();
		if (stDoc == null) {
			throw new COSSecurityException("document missing");
		}
		// force creation of file id
		if (stDoc.getTrailer().cosGetPermanentFileID() == null) {
			stDoc.getTrailer().updateFileID();
		}
		COSString permanentId = stDoc.getTrailer().cosGetPermanentFileID();
		if (permanentId != null) {
			return permanentId.byteValue();
		}
		return null;
	}

	public PermissionFlags getPermissionFlags() {
		return new PermissionFlags(this);
	}

	abstract public int getRevision();

	protected byte[] getU() {
		COSString u = getEncryption().cosGetField(DK_U).asString();
		if (u == null) {
			return null;
		}
		return u.byteValue();
	}

	protected byte[] getUser() {
		return user;
	}

	@Override
	public void initialize(STDocument doc) {
		super.initialize(doc);
		setActiveAccessPermissions(AccessPermissionsNone.get());
	}

	public boolean isEncryptMetadata() {
		return getEncryption().getFieldBoolean(DK_EncryptMetadata, true);
	}

	protected byte[] prepareBytes(byte[] bytes) {
		byte[] padded = new byte[32];
		if (bytes == null) {
			System.arraycopy(PADDING, 0, padded, 0, 32);
		} else {
			if (bytes.length > 32) {
				System.arraycopy(bytes, 0, padded, 0, 32);
			} else {
				System.arraycopy(bytes, 0, padded, 0, bytes.length);
				System.arraycopy(PADDING, 0, padded, bytes.length,
						32 - bytes.length);
			}
		}
		return padded;
	}

	protected void setActiveAccessPermissions(
			IAccessPermissions accessPermissions) {
		this.accessPermissions = accessPermissions;
	}

	public void setAuthenticationHandler(
			IAuthenticationHandler authenticationHandler) {
		this.authenticationHandler = authenticationHandler;
	}

	protected void setCryptKey(byte[] key) throws COSSecurityException {
		this.cryptKey = key;
	}

	public void setEncryptMetadata(boolean value) {
		getEncryption().setFieldBoolean(DK_EncryptMetadata, value);
	}

	protected void setOwner(byte[] owner) {
		this.owner = owner;
	}

	/**
	 * Set new owner password for the {@link ISecurityHandler}.
	 * 
	 * @param pOwner
	 *            The new owner password.
	 * @throws COSSecurityException
	 */
	public void setOwnerPassword(byte[] pOwner) throws COSSecurityException {
		if (getEncryption().cosGetDoc() == null) {
			throw new COSSecurityException("document missing");
		}
		owner = pOwner;
	}

	protected void setUser(byte[] user) {
		this.user = user;
	}

	/**
	 * Set new user password for the {@link ISecurityHandler}.
	 * 
	 * @param pUser
	 *            The new user password
	 * @throws COSSecurityException
	 */
	public void setUserPassword(byte[] pUser) throws COSSecurityException {
		if (getEncryption().cosGetDoc() == null) {
			throw new COSSecurityException("document missing");
		}
		user = pUser;
	}

}
