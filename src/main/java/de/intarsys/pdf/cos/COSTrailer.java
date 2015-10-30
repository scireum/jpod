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
package de.intarsys.pdf.cos;

import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;
import de.intarsys.tools.string.StringTools;

import java.security.MessageDigest;
import java.util.Iterator;

/**
 * The document trailer.
 */
public class COSTrailer extends COSBasedObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends COSBasedObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new COSTrailer(object);
        }
    }

    /**
     * The well known attribute names
     */
    public static final COSName DK_Info = COSName.constant("Info"); //$NON-NLS-1$

    public static final COSName DK_Prev = COSName.constant("Prev"); //$NON-NLS-1$

    public static final COSName DK_Root = COSName.constant("Root"); //$NON-NLS-1$

    public static final COSName DK_Size = COSName.constant("Size"); //$NON-NLS-1$

    public static final COSName DK_Encrypt = COSName.constant("Encrypt"); //$NON-NLS-1$

    public static final COSName DK_ID = COSName.constant("ID"); //$NON-NLS-1$

    public static final COSName DK_XRefStm = COSName.constant("XRefStm"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * The cached catalog object
     */
    private COSCatalog cachedCatalog;

    protected COSTrailer(COSObject object) {
        super(object);
    }

    /**
     * The dynamic file id part.
     */
    public COSString cosGetDynamicFileID() {
        COSArray fileID = cosGetField(DK_ID).asArray();
        if ((fileID == null) || (fileID.size() < 2)) {
            return null;
        } else {
            return (COSString) fileID.get(1);
        }
    }

    /**
     * The /Encrypt field of the trailer.
     *
     * @return The /Encrypt field of the trailer.
     */
    public COSDictionary cosGetEncryption() {
        return cosGetField(DK_Encrypt).asDictionary();
    }

    /**
     * The current file id or null
     */
    public COSArray cosGetFileID() {
        return cosGetField(DK_ID).asArray();
    }

    /**
     * The /ID field of the trailer.
     *
     * @return The /ID field of the trailer.
     */
    public COSArray cosGetID() {
        return cosGetField(DK_ID).asArray();
    }

    /**
     * The permanent file id part.
     */
    public COSString cosGetPermanentFileID() {
        COSArray fileID = cosGetField(DK_ID).asArray();
        if ((fileID == null) || (fileID.isEmpty())) {
            return null;
        } else {
            return (COSString) fileID.get(0);
        }
    }

    /**
     * Set the /Encrypt field of the trailer.
     *
     * @param encryption The new encryption dictionary
     */
    public void cosSetEncryption(COSDictionary encryption) {
        cosSetField(DK_Encrypt, encryption);
    }

    /**
     * Create a random id for use in the PDF file id. The document is not
     * changed. <br>
     * <p>
     * {@code
     * - include time
     * - file location
     * - size
     * - document information dictionary
     * }
     *
     * @return a byte array with the created ID
     */
    public byte[] createFileID() {
        try {
            COSDocument cosDoc = cosGetDoc();
            if (cosDoc == null) {
                return null;
            }
            ILocator locator = cosDoc.getLocator();
            if (locator == null) {
                return null;
            }
            IRandomAccess ra = cosDoc.stGetDoc().getRandomAccess();
            if (ra == null) {
                ra = new RandomAccessByteArray(StringTools.toByteArray("DummyValue")); //$NON-NLS-1$
            }
            MessageDigest digest = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
            long time = System.currentTimeMillis();
            digest.update(String.valueOf(time).getBytes());
            digest.update(locator.getFullName().getBytes());
            // this is the previous length! - but should not matter
            digest.update(String.valueOf(ra.getLength()).getBytes());
            COSInfoDict infoDict = getInfoDict();
            if (infoDict != null) {
                for (Iterator it = infoDict.cosGetDict().iterator(); it.hasNext(); ) {
                    COSObject object = (COSObject) it.next();
                    String tempString = object.stringValue();
                    if (tempString != null) {
                        digest.update(tempString.getBytes());
                    }
                }
            }
            return digest.digest();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * The {@link COSInfoDict} containing metadata.
     *
     * @return The {@link COSInfoDict} containing metadata.
     */
    public COSInfoDict getInfoDict() {
        return (COSInfoDict) COSInfoDict.META.createFromCos(cosGetField(DK_Info).asDictionary());
    }

    /**
     * @return Offset of previous trailer dict or -1 if none exists
     */
    public int getPrev() {
        return getFieldInt(DK_Prev, -1);
    }

    /**
     * Get the root object (the catalog) for the document.
     *
     * @return The root object (the catalog) for the document.
     */
    public COSCatalog getRoot() {
        if (cachedCatalog == null) {
            cachedCatalog = (COSCatalog) COSCatalog.META.createFromCos(cosGetField(DK_Root));
        }
        return cachedCatalog;
    }

    /**
     * @return Total number of indirect objects in the document
     */
    public int getSize() {
        return getFieldInt(DK_Size, -1);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBasedObject#initializeFromScratch()
     */
    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        setRoot((COSCatalog) COSCatalog.META.createNew());
        setInfoDict((COSInfoDict) COSInfoDict.META.createNew());
    }

    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedCatalog = null;
    }

    /**
     * Set the info dictionary containing metadata.
     *
     * @param infoDict The info dictionary containing metadata.
     */
    public void setInfoDict(COSInfoDict infoDict) {
        setFieldObject(DK_Info, infoDict);
    }

    /**
     * Set the catalog.
     *
     * @param root The document catalog
     */
    public void setRoot(COSCatalog root) {
        setFieldObject(DK_Root, root);
    }

    /**
     * Generates a unique file ID array (10.3).
     */
    public void updateFileID() {
        updateFileID(null, null);
    }

    /**
     * Update the file id. id1 and id2 are optional default values for the new
     * file id.
     */
    public void updateFileID(COSString id1, COSString id2) {
        COSArray fileID = cosGetField(DK_ID).asArray();
        if (fileID == null) {
            fileID = COSArray.create();
            cosSetField(DK_ID, fileID);
        }
        if (fileID.isEmpty()) {
            if (id1 == null) {
                byte[] id = createFileID();
                id1 = COSString.create(id);
            }
            fileID.add(id1);
            if (id2 == null) {
                id2 = id1;
            }
            fileID.add(id2);
        } else if (fileID.size() == 1) {
            if (id1 != null) {
                fileID.set(0, id1);
            }
            if (id2 == null) {
                byte[] id = createFileID();
                id2 = COSString.create(id);
            }
            fileID.add(id2);
        } else if (fileID.size() == 2) {
            if (id1 != null) {
                fileID.set(0, id1);
            }
            if (id2 == null) {
                byte[] id = createFileID();
                id2 = COSString.create(id);
            }
            fileID.set(1, id2);
        }
    }
}
