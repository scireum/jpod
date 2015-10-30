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
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * Function implementation based on samples.
 */
public class PDSampledFunction extends PDFunction {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDFunction.MetaClass {
		protected MetaClass(Class<?> instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDSampledFunction(object);
		}

		@Override
		protected COSObject doCreateCOSObject() {
			return COSStream.create(null);
		}
	}

	public static final COSName DK_BitsPerSample = COSName
			.constant("BitsPerSample"); //$NON-NLS-1$

	public static final COSName DK_Decode = COSName.constant("Decode"); //$NON-NLS-1$

	public static final COSName DK_Encode = COSName.constant("Encode"); //$NON-NLS-1$

	public static final COSName DK_Order = COSName.constant("Order"); //$NON-NLS-1$

	public static final COSName DK_Size = COSName.constant("Size"); //$NON-NLS-1$

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private byte[] samples;

	/**
	 * PDSampledFunction constructor.
	 * 
	 * @param object
	 *            the COS object to base this COS based PD object on
	 */
	protected PDSampledFunction(COSObject object) {
		super(object);
	}

	/**
	 * Return the Decode value array.
	 * 
	 * @return the Decode value array
	 */
	public COSArray cosGetDecode() {
		return cosGetDict().get(DK_Decode).asArray();
	}

	@Override
	public COSDictionary cosGetDict() {
		return cosGetStream().getDict();
	}

	/**
	 * Return the Encode value array
	 * 
	 * @return the Encode value array
	 */
	public COSArray cosGetEncode() {
		return cosGetDict().get(DK_Encode).asArray();
	}

	/**
	 * Evaluate the function for each of the input values in turn. The output
	 * values are stored in an array and returned.
	 * 
	 * @param input
	 *            input values to evaluate
	 * 
	 * @return an array of output values
	 */
	@Override
	public float[] evaluate(float[] input) {
		float[] intermediate;
		float[] output;

		intermediate = new float[input.length];
		prepareInput(input, intermediate);
		output = stepInterpolateOutput(intermediate, 0, 0, 0);
		prepareOutput(output);

		return output;
	}

	/**
	 * Return the BitsPerSample value.
	 * 
	 * @return the BitsPerSample value
	 */
	public int getBitsPerSample() {
		return cosGetDict().get(DK_BitsPerSample).asNumber().intValue();
	}

	/**
	 * Return the max value of the Decode array for the given dimension.
	 * 
	 * @param dimension
	 *            the dimension to get the max value for
	 * 
	 * @return the max value of the Decode array for the given dimension
	 */
	public float getDecodeMax(int dimension) {
		COSArray decode = cosGetDecode();
		if (decode == null) {
			return getRangeMax(dimension);
		}
		return decode.get((dimension * 2) + 1).asNumber().floatValue();
	}

	/**
	 * Return the min value of the Decode array for the given dimension.
	 * 
	 * @param dimension
	 *            the dimension to get the min value for
	 * 
	 * @return the min value of the Decode array for the given dimension
	 */
	public float getDecodeMin(int dimension) {
		COSArray decode = cosGetDecode();
		if (decode == null) {
			return getRangeMin(dimension);
		}
		return cosGetDecode().get(dimension * 2).asNumber().floatValue();
	}

	/**
	 * Return the max value of the Encode array for the given dimension.
	 * 
	 * @param dimension
	 *            the dimension to get the max value for
	 * 
	 * @return the max value of the Encode array for the given dimension
	 */
	public float getEncodeMax(int dimension) {
		COSArray encode = cosGetEncode();
		if (encode == null) {
			return getSize(dimension) - 1;
		}
		return cosGetEncode().get((dimension * 2) + 1).asNumber().floatValue();
	}

	/**
	 * Return the min value of the Encode array for the given dimension.
	 * 
	 * @param dimension
	 *            the dimension to get the min value for
	 * 
	 * @return the min value of the Encode array for the given dimension
	 */
	public float getEncodeMin(int dimension) {
		COSArray encode = cosGetEncode();
		if (encode == null) {
			return 0;
		}
		return cosGetEncode().get(dimension * 2).asNumber().floatValue();
	}

	/**
	 * Return the number of output values for one input value.
	 * 
	 * @return the number of output values for one input value
	 */
	@Override
	public int getOutputSize() {
		return getRange().size() / 2;
	}

	/**
	 * Return the sample from the sample stream for the given bit position.
	 * 
	 * @param bitPos
	 *            the bit position to get the sample for
	 * 
	 * @return the sample from the sample stream for the given bit position
	 */
	protected int getSample(int bitPos) {
		int bytePos = bitPos >> 3;
		int bitShift = (7 - bitPos) & 7;
		int result = 0;
		for (int i = 0; i < getBitsPerSample(); i++) {
			result = result << 1;
			int sample = (getSamples()[bytePos] >> bitShift) & 1;
			result = result + sample;
			if (bitShift == 0) {
				bytePos++;
				bitShift = 7;
			} else {
				bitShift--;
			}
		}
		return result;
	}

	/**
	 * Return the decoded contents of the sample stream.
	 * 
	 * @return the decoded contents of the sample stream
	 */
	protected byte[] getSamples() {
		if (samples == null) {
			samples = cosGetStream().getDecodedBytes();
		}
		return samples;
	}

	/**
	 * Return the Size value array.
	 * 
	 * @return the Size value array
	 */
	public COSArray getSize() {
		return cosGetDict().get(DK_Size).asArray();
	}

	/**
	 * Return the size value for the given dimension.
	 * 
	 * @param dimension
	 *            the dimension to get the size value for
	 * 
	 * @return the size value for the given dimension
	 */
	public int getSize(int dimension) {
		return getSize().get(dimension).asNumber().intValue();
	}

	protected float interpolate(float x, float xmin, float xmax, float ymin,
			float ymax) {
		return ymin + ((x - xmin) * (ymax - ymin) / (xmax - xmin));
	}

	protected float[] lookup(int sampleIndex) {
		int bitPos;
		float[] output;

		bitPos = getBitsPerSample() * getOutputSize() * sampleIndex;
		output = new float[getOutputSize()];
		for (int index = 0; index < output.length; index++, bitPos = bitPos
				+ getBitsPerSample()) {
			output[index] = getSample(bitPos);
		}
		return output;
	}

	protected void prepareInput(float[] values, float[] intermediate) {
		for (int i = 0; i < values.length; i++) {
			intermediate[i] = clip(values[i], getDomainMin(i), getDomainMax(i));
			intermediate[i] = interpolate(intermediate[i], getDomainMin(i),
					getDomainMax(i), getEncodeMin(i), getEncodeMax(i));
			intermediate[i] = clip(intermediate[i], 0, getSize(i) - 1);
		}
	}

	protected void prepareOutput(float[] values) {
		for (int i = 0; i < values.length; i++) {
			values[i] = interpolate(values[i], 0f, (float) Math.pow(2,
					getBitsPerSample()), getDecodeMin(i), getDecodeMax(i));
			values[i] = clip(values[i], getRangeMin(i), getRangeMax(i));
		}
	}

	protected float[] stepInterpolateOutput(float[] input, int lowBase,
			int highBase, int step) {
		int dimension;
		int offset;
		float[] lowSample;
		float[] highSample;
		float[] result;

		dimension = input.length - step - 1;
		offset = 1;
		for (int index = 0; index < dimension; index++) {
			offset = offset * getSize(index);
		}
		if (dimension > -1) {
			int lowOffset;
			int highOffset;

			lowOffset = offset * (int) Math.floor(input[dimension]);
			highOffset = offset * (int) Math.ceil(input[dimension]);
			lowSample = stepInterpolateOutput(input, lowBase + lowOffset,
					lowBase + highOffset, step + 1);
			if (step == 0) {
				return lowSample;
			}
			highSample = stepInterpolateOutput(input, highBase + lowOffset,
					highBase + highOffset, step + 1);
		} else {
			lowSample = lookup(lowBase);
			if (highBase == lowBase) {
				highSample = lowSample;
			} else {
				highSample = lookup(highBase);
			}
		}

		result = new float[getOutputSize()];
		for (int index = 0; index < result.length; index++) {
			float low;
			float high;
			float fract;

			low = lowSample[index];
			high = highSample[index];
			fract = (float) (input[step - 1] - Math.floor(input[step - 1]));
			result[index] = low + (fract * (high - low));
		}
		return result;
	}
}
