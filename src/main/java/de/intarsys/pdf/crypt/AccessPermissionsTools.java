package de.intarsys.pdf.crypt;

import java.util.Iterator;
import java.util.List;

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDSignature;
import de.intarsys.pdf.pd.PDSignatureReference;
import de.intarsys.pdf.pd.PDTransformMethod;
import de.intarsys.pdf.pd.PDTransformMethodDocMDP;

public class AccessPermissionsTools {

	public static IAccessPermissions createPermissions(PDDocument doc) {
		IAccessPermissions delegate = doc.cosGetDoc().getAccessPermissions();
		COSDictionary perms = doc.cosGetPermissionsDict();
		if (perms != null) {
			COSObject cosDocMDPSig = perms.get(PDTransformMethod.DK_DocMDP);
			if (!cosDocMDPSig.isNull()) {
				PDSignature sig = (PDSignature) PDSignature.META
						.createFromCos(cosDocMDPSig);
				List references = sig.getSignatureReferences();
				for (Iterator i = references.iterator(); i.hasNext();) {
					PDSignatureReference reference = (PDSignatureReference) i
							.next();
					if (reference.getTransformMethod() == PDTransformMethodDocMDP.SINGLETON) {
						return new AccessPermissionsDocMDP(delegate, reference
								.getTransformParams());
					}
				}
			}
		}
		return delegate;
	}

}
