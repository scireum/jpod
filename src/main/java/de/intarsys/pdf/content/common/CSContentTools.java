package de.intarsys.pdf.content.common;

import java.awt.geom.Rectangle2D;

import de.intarsys.pdf.cds.CDSMatrix;
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSDeviceBasedInterpreter;
import de.intarsys.pdf.content.CSError;
import de.intarsys.pdf.content.CSException;
import de.intarsys.pdf.content.CSWarning;
import de.intarsys.pdf.content.ICSExceptionHandler;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.pd.PDForm;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDResources;

/**
 * A collection of tools to work with page content.
 */
public class CSContentTools {

	private static final ICSExceptionHandler ignoreExceptionHandler = new ICSExceptionHandler() {

		public void error(CSError error) throws CSException {
			// ignore
		}

		public void warning(CSWarning warning) throws CSException {
			// ignore
		}

	};

	/**
	 * Get the rectangle containing all graphics artifacts on the page (stemming
	 * from the content stream).
	 * 
	 * @param page
	 *            The page whose content is evaluated.
	 * @param border
	 *            A border width to be added to the clipping rectangle.
	 * 
	 * @return Get the rectangle containing all graphics artifacts on the page
	 *         (stemming from the content stream).
	 */
	public static Rectangle2D getBoundingBoxClipped(PDPage page, double border) {
		CSBoundingBoxCollector bbCollector = new CSBoundingBoxCollector();
		CSDeviceBasedInterpreter interpreter = new CSDeviceBasedInterpreter(
				null, bbCollector);
		interpreter.setExceptionHandler(ignoreExceptionHandler);
		CSContent content = page.getContentStream();
		interpreter.process(content, page.getResources());
		if (bbCollector.getBoundingBox() != null) {
			return bbCollector.getBoundingBox();
		} else {
			return page.getCropBox().toNormalizedRectangle();
		}
	}

	/**
	 * Get a {@link PDForm} containing all graphics artifacts on the page
	 * (stemming from the content stream).
	 * 
	 * @param page
	 *            The page whose content is evaluated.
	 * @param border
	 *            A border width to be added to the clipping rectangle.
	 * 
	 * @return Get a {@link PDForm} containing all graphics artifacts on the
	 *         page (stemming from the content stream).
	 */
	public static PDForm getFormClipped(PDPage page, double border) {
		PDForm form = (PDForm) PDForm.META.createNew();
		CSContent content = page.getContentStream();
		if (content == null) {
			form.setBytes(new byte[0]);
		} else {
			form.setBytes(content.toByteArray());
		}
		if (page.getResources() != null) {
			COSObject cosResources = page.getResources().cosGetObject()
					.copyDeep();
			PDResources resources = (PDResources) PDResources.META
					.createFromCos(cosResources);
			form.setResources(resources);
		}
		Rectangle2D rect = CSContentTools.getBoundingBoxClipped(page, border);
		CDSRectangle bbox = new CDSRectangle(rect);
		form.setBoundingBox(bbox);
		CDSMatrix matrix = new CDSMatrix();
		matrix.translate(-bbox.getLowerLeftX(), -bbox.getLowerLeftY());
		// int rotation = PDFGeometryTools.normalizeRotation(page.getRotate());
		// if (rotation == 0) {
		// } else if (rotation == 90) {
		// matrix.rotate(Math.toRadians(-90));
		// } else if (rotation == 180) {
		// matrix.rotate(Math.toRadians(-180));
		// } else if (rotation == 270) {
		// matrix.rotate(Math.toRadians(-270));
		// } else {
		// }
		form.setMatrix(matrix);
		return form;
	}
}
