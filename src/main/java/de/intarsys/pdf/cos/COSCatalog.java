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

/**
 * The document catalog object of the PDF document.
 */
public class COSCatalog extends COSBasedObject {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends COSBasedObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new COSCatalog(object);
        }
    }

    public static final COSName DK_AcroForm = COSName.constant("AcroForm"); //$NON-NLS-1$

    public static final COSName DK_Collection = COSName.constant("Collection"); //$NON-NLS-1$

    static public final COSName DK_Dests = COSName.constant("Dests"); //$NON-NLS-1$

    static public final COSName DK_EmbeddedFiles = COSName.constant("EmbeddedFiles"); //$NON-NLS-1$

    static public final COSName DK_JavaScript = COSName.constant("JavaScript"); //$NON-NLS-1$

    public static final COSName DK_FDF = COSName.constant("FDF"); //$NON-NLS-1$

    static public final COSName DK_Names = COSName.constant("Names"); //$NON-NLS-1$

    static public final COSName DK_MarkInfo = COSName.constant("MarkInfo"); //$NON-NLS-1$

    static public final COSName DK_OpenAction = COSName.constant("OpenAction"); //$NON-NLS-1$

    static public final COSName DK_Outlines = COSName.constant("Outlines"); //$NON-NLS-1$

    public static final COSName DK_Sig = COSName.constant("Sig"); //$NON-NLS-1$

    public static final COSName DK_ViewerPreferences = COSName.constant("ViewerPreferences"); //$NON-NLS-1$

    public static final COSName DK_PageLabels = COSName.constant("PageLabels"); //$NON-NLS-1$

    public static final COSName DK_PageLayout = COSName.constant("PageLayout"); //$NON-NLS-1$

    public static final COSName DK_Threads = COSName.constant("Threads"); //$NON-NLS-1$

    public static final COSName DK_AA = COSName.constant("AA"); //$NON-NLS-1$

    public static final COSName DK_AF = COSName.constant("AF"); //$NON-NLS-1$

    public static final COSName DK_Lang = COSName.constant("Lang"); //$NON-NLS-1$

    public static final COSName DK_SpiderInfo = COSName.constant("SpiderInfo"); //$NON-NLS-1$

    public static final COSName DK_StructTreeRoot = COSName.constant("StructTreeRoot"); //$NON-NLS-1$

    public static final COSName DK_Type = COSName.constant("Type"); //$NON-NLS-1$

    public static final COSName DK_Metadata = COSName.constant("Metadata"); //$NON-NLS-1$

    public static final COSName DK_OutputIntents = COSName.constant("OutputIntents"); //$NON-NLS-1$

    static public final COSName DK_PageMode = COSName.constant("PageMode"); //$NON-NLS-1$

    public static final COSName DK_Pages = COSName.constant("Pages"); //$NON-NLS-1$

    public static final COSName DK_PieceInfo = COSName.constant("PieceInfo"); //$NON-NLS-1$

    static public final COSName CN_Type_Catalog = COSName.constant("Catalog"); //$NON-NLS-1$

    public static final COSName DK_URI = COSName.constant("URI"); //$NON-NLS-1$

    static public final COSName DK_Version = COSName.constant("Version"); //$NON-NLS-1$

    public static final COSName DK_OCProperties = COSName.constant("OCProperties"); //$NON-NLS-1$

    /**
     * Well known attribute names
     */
    static public final COSName CN_Version_1_4 = COSName.constant("1.4"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    protected COSCatalog(COSObject object) {
        super(object);
    }

    /**
     * The /AF field in the document catalog.
     *
     * @return The /AF field in the document catalog.
     */
    public COSArray cosGetAF() {
        return cosGetField(DK_AF).asArray();
    }

    /**
     * The /Collection field in the document catalog.
     *
     * @return The /Collection field in the document catalog.
     */
    public COSDictionary cosGetCollection() {
        return cosGetField(DK_Collection).asDictionary();
    }

    /**
     * The /Dests field in the document catalog.
     *
     * @return The /Dests field in the document catalog.
     */
    public COSDictionary cosGetDests() {
        return cosGetField(DK_Dests).asDictionary();
    }

    /**
     * The /FDF field in the document catalog.
     *
     * @return The /FDF field in the document catalog.
     */
    public COSDictionary cosGetFDF() {
        return cosGetField(DK_FDF).asDictionary();
    }

    /**
     * The /Names field in the document catalog.
     *
     * @return The /Names field in the document catalog.
     */
    public COSDictionary cosGetNames() {
        return cosGetField(DK_Names).asDictionary();
    }

    /**
     * The object defining the open action for the document.
     *
     * @return COSDictionary or COSArray or COSNull if no entry defined.
     */
    public COSObject cosGetOpenAction() {
        return cosGetField(DK_OpenAction);
    }

    /**
     * The /Outlines field in the document catalog.
     *
     * @return The /Outlines field in the document catalog.
     */
    public COSDictionary cosGetOutline() {
        return cosGetField(DK_Outlines).asDictionary();
    }

    /**
     * The /Sig field in the document catalog.
     *
     * @return The /Sig field in the document catalog.
     */
    public COSDictionary cosGetSig() {
        return cosGetField(DK_Sig).asDictionary();
    }

    /**
     * Set the /Collection field in the document catalog.
     */
    public COSDictionary cosSetCollection(COSDictionary pCollection) {
        return cosSetField(DK_Collection, pCollection).asDictionary();
    }

    /**
     * Set the /Dests field in the document catalog.
     */
    public COSDictionary cosSetDests(COSDictionary pDests) {
        return cosSetField(DK_Dests, pDests).asDictionary();
    }

    /**
     * Set the /FDF field in the document catalog.
     */
    public COSDictionary cosSetFDF(COSDictionary fdfDict) {
        return cosSetField(DK_FDF, fdfDict).asDictionary();
    }

    /**
     * Set the /Names field in the document catalog.
     */
    public COSDictionary cosSetNames(COSDictionary pNames) {
        return cosSetField(DK_Names, pNames).asDictionary();
    }

    /**
     * Set the /Outlines field in the document catalog.
     */
    public COSDictionary cosSetOutline(COSDictionary dict) {
        return cosSetField(DK_Outlines, dict).asDictionary();
    }

    /**
     * Set the /Sig field in the document catalog.
     */
    public COSDictionary cosSetSig(COSDictionary sigDict) {
        return cosSetField(DK_Sig, sigDict).asDictionary();
    }

    /**
     * The /PageMode field in the document catalog.
     *
     * @return The /PageMode field in the document catalog.
     */
    public String getPageMode() {
        return getFieldString(DK_PageMode, "UseNone"); //$NON-NLS-1$
    }

    /**
     * The /Version field in the document catalog.
     *
     * @return The /Version field in the document catalog.
     */
    public String getVersion() {
        return getFieldString(DK_Version, cosGetDoc().stGetDoc().getVersion());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBasedObject#initializeFromScratch()
     */
    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        cosSetField(DK_Type, CN_Type_Catalog.copyShallow());
        cosSetField(DK_Version, CN_Version_1_4.copyShallow());
    }

    /**
     * Set the /PageMode field in the document catalog.
     */
    public void setPageMode(String value) {
        setFieldName(DK_PageMode, value);
    }

    /**
     * Set the /Version field in the document catalog.
     */
    public void setVersion(String value) {
        setFieldName(DK_Version, value);
    }
}
