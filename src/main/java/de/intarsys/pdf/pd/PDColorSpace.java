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
package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * An abstract superclass for all color spaces.
 * 
 */
public abstract class PDColorSpace extends PDObject {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class paramInstanceClass) {
			super(paramInstanceClass);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.intarsys.pdf.cos.COSBasedObject.MetaClass#doCreateCOSBasedObject
		 * (de.intarsys.pdf.cos.COSObject)
		 */
		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			COSArray array;
			COSName type;

			if (object instanceof COSName) {
				return getNamed((COSName) object);
			}

			if (object instanceof COSArray) {
				array = (COSArray) object;
				type = array.get(0).asName();
				if (type == null) {
					throw new IllegalArgumentException(
							"ColorSpace array has no type");
				}
				if (type.equals(CN_CS_DeviceCMYK) || type.equals(CN_CS_CMYK)) {
					return PDCSDeviceCMYK.SINGLETON;
				}
				if (type.equals(CN_CS_DeviceRGB) || type.equals(CN_CS_RGB)) {
					return PDCSDeviceRGB.SINGLETON;
				}
				if (type.equals(CN_CS_DeviceGray) || type.equals(CN_CS_G)) {
					return PDCSDeviceGray.SINGLETON;
				}
			}
			return doCreateCOSBasedObjectBasic(object);
		}

		public COSBasedObject doCreateCOSBasedObjectBasic(COSObject object) {
			return null;
		}

		@Override
		protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
			// this here may come from the resource default colorspaces which
			// are not named arrays but single COSStreams
			if (object instanceof COSStream) {
				return PDCSICCBased.META;
			}
			if (object instanceof COSArray) {
				COSName type = ((COSArray) object).get(0).asName();
				if (type == null) {
					throw new IllegalArgumentException(
							"ColorSpace array has no type");
				}
				if (type.equals(CN_CS_CalGray)) {
					return PDCSCalGray.META;
				}
				if (type.equals(CN_CS_CalRGB)) {
					return PDCSCalRGB.META;
				}
				if (type.equals(CN_CS_Lab)) {
					return PDCSLab.META;
				}
				if (type.equals(CN_CS_ICCBased)) {
					return PDCSICCBased.META;
				}
				if (type.equals(CN_CS_Indexed) || type.equals(CN_CS_I)) {
					return PDCSIndexed.META;
				}
				if (type.equals(CN_CS_Pattern)) {
					return PDCSPattern.META;
				}
				if (type.equals(CN_CS_Separation)) {
					return PDCSSeparation.META;
				}
				if (type.equals(CN_CS_DeviceN)) {
					return PDCSDeviceN.META;
				}
			}
			return super.doDetermineClass(object);
		}

		@Override
		public Class getRootClass() {
			return PDColorSpace.class;
		}
	}

	public static final COSName CN_CS_CalGray = COSName.constant("CalGray");

	public static final COSName CN_CS_CalRGB = COSName.constant("CalRGB");

	public static final COSName CN_CS_CMYK = COSName.constant("CMYK");

	public static final COSName CN_CS_DeviceCMYK = COSName
			.constant("DeviceCMYK");

	public static final COSName CN_CS_DeviceGray = COSName
			.constant("DeviceGray");

	public static final COSName CN_CS_DeviceN = COSName.constant("DeviceN");

	public static final COSName CN_CS_DeviceRGB = COSName.constant("DeviceRGB");

	public static final COSName CN_CS_DefaultGray = COSName
			.constant("DefaultGray");

	public static final COSName CN_CS_DefaultCMYK = COSName
			.constant("DefaultCMYK");

	public static final COSName CN_CS_DefaultRGB = COSName
			.constant("DefaultRGB");

	public static final COSName CN_CS_G = COSName.constant("G");

	public static final COSName CN_CS_ICCBased = COSName.constant("ICCBased");

	public static final COSName CN_CS_Indexed = COSName.constant("Indexed");

	public static final COSName CN_CS_I = COSName.constant("I");

	public static final COSName CN_CS_Lab = COSName.constant("Lab");

	public static final COSName CN_CS_Pattern = COSName.constant("Pattern");

	public static final COSName CN_CS_RGB = COSName.constant("RGB");

	public static final COSName CN_CS_Separation = COSName
			.constant("Separation");

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static PDColorSpace getNamed(COSName name) {
		if (name.equals(CN_CS_Pattern)) {
			return new PDCSPattern(name);
		}
		return getSingleton(name);
	}

	/**
	 * return the singleton color space instance corresponding to the given name
	 * 
	 * @param name
	 *            must be one of the predefined color space names
	 * 
	 * @return return the singleton color space instance corresponding to the
	 *         given name
	 */
	public static PDColorSpace getSingleton(COSName name) {
		if (CN_CS_DeviceGray.equals(name)) {
			return PDCSDeviceGray.SINGLETON;
		} else if (CN_CS_G.equals(name)) {
			return PDCSDeviceGray.SINGLETON;
		} else if (CN_CS_DeviceRGB.equals(name)) {
			return PDCSDeviceRGB.SINGLETON;
		} else if (CN_CS_RGB.equals(name)) {
			return PDCSDeviceRGB.SINGLETON;
		} else if (CN_CS_DeviceCMYK.equals(name)) {
			return PDCSDeviceCMYK.SINGLETON;
		} else if (CN_CS_CMYK.equals(name)) {
			return PDCSDeviceCMYK.SINGLETON;
		} else {
			return null;
		}
	}

	protected PDColorSpace(COSObject object) {
		super(object);
	}

}
