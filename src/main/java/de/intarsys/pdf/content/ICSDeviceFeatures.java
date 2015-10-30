package de.intarsys.pdf.content;

import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.pd.PDColorSpace;
import de.intarsys.pdf.pd.PDExtGState;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDPattern;
import de.intarsys.pdf.pd.PDShading;
import de.intarsys.pdf.pd.PDXObject;

/**
 * The {@link CSDeviceBasedInterpreter} may do some unnecessary work when
 * interfacing to an {@link ICSDevice}, for example when he realizes images
 * where the device finally only handles shapes or text.
 * <p>
 * This optional interface can signal to the interpreter that it is not
 * interested in some of the arguments in the {@link ICSDevice} callback. Only
 * the arguments are replaced with {@code null}, the callback itself is
 * performed to support lightweight implementations that for example only rely
 * on the object name.
 * <p>
 * This declarative interface is designed to fine tune the
 * {@link CSDeviceBasedInterpreter} where either a PDF object lookup or other
 * complex object construction is involved. Where this tuning is not enough you
 * may be better off subclassing {@link CSInterpreter} directly.
 */
public interface ICSDeviceFeatures {

    /**
     * {@code true} if this device supports (needs) {@link PDColorSpace}
     * objects to be reported in its callbacks.
     *
     * @return {@code true} if this device supports (needs)
     * {@link PDColorSpace} objects to be reported in its callbacks.
     */
    boolean supportsColorSpace();

    /**
     * {@code true} if this device supports (needs) {@link PDExtGState}
     * objects to be reported in its callbacks.
     *
     * @return {@code true} if this device supports (needs)
     * {@link PDExtGState} objects to be reported in its callbacks.
     */
    boolean supportsExtendedState();

    /**
     * {@code true} if this device supports (needs) {@link PDFont} objects
     * to be reported in its callbacks.
     *
     * @return {@code true} if this device supports (needs) {@link PDFont}
     * objects to be reported in its callbacks.
     */
    boolean supportsFont();

    /**
     * {@code true} if this device supports (needs) {@link PDImage} objects
     * to be reported in its "inlineImage" callbacks.
     *
     * @return {@code true} if this device supports (needs) {@link PDImage}
     * objects to be reported in its callbacks.
     */
    boolean supportsInlineImage();

    /**
     * {@code true} if this device supports (needs) {@link PDPattern}
     * objects to be reported in its callbacks.
     *
     * @return {@code true} if this device supports (needs)
     * {@link PDPattern} objects to be reported in its callbacks.
     */
    boolean supportsPattern();

    /**
     * {@code true} if this device supports (needs) properties to be
     * reported in its callbacks.
     *
     * @return {@code true} if this device supports (needs) properties to
     * be reported in its callbacks.
     */
    boolean supportsProperties();

    /**
     * {@code true} if this device supports (needs) {@link PDShading}
     * objects to be reported in its callbacks.
     *
     * @return {@code true} if this device supports (needs)
     * {@link PDShading} objects to be reported in its callbacks.
     */
    boolean supportsShading();

    /**
     * {@code true} if this device supports (needs) {@link PDXObject}
     * objects to be reported in its callbacks.
     *
     * @return {@code true} if this device supports (needs)
     * {@link PDXObject} objects to be reported in its callbacks.
     */
    boolean supportsXObject();
}
