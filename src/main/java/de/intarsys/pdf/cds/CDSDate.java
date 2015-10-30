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
package de.intarsys.pdf.cds;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.intarsys.pdf.cos.COSString;

/**
 * The implementation for a date string based on a
 * {@link de.intarsys.pdf.cos.COSString}.
 * 
 * <p>
 * The string follows the format defined in [PDF], chapter 3.8.2.
 * </p>
 */
public class CDSDate extends CDSBase {
	public static final String DATE_FORMAT = "'D':yyyyMMddHHmmss"; //$NON-NLS-1$

	// YYYY MM DD HH mm SS O HH ' mm '
	public static final Pattern DatePattern = Pattern
			.compile("D:(\\d{4})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\d{2})(\\+|-|Z|z)?(\\d{2})?\\'?(\\d{2})?.*"); //$NON-NLS-1$

	private static final DateFormat PDF_DATE_FORMAT = new SimpleDateFormat(
			DATE_FORMAT);

	private static final DateFormat dateFormat = DateFormat
			.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

	/**
	 * Create a {@link CDSDate} from a {@link COSString}
	 * 
	 * @param string
	 *            The base string.
	 * @return Create a {@link CDSDate} from a {@link COSString}
	 */
	static public CDSDate createFromCOS(COSString string) {
		if (string == null) {
			return null;
		}
		return new CDSDate(string);
	}

	/**
	 * Format a {@link CDSDate} using the default format.
	 * 
	 * @param cdsDate
	 *            The {@link CDSDate} to be formatted.
	 * @return A formatted {@link String} representation.
	 */
	public static String format(CDSDate cdsDate) {
		synchronized (dateFormat) {
			return format(dateFormat, cdsDate);
		}
	}

	/**
	 * Format a {@link CDSDate} using <code>format</code>.
	 * 
	 * @param format
	 *            The format to be used for formatting
	 * @param cdsDate
	 *            The date to be formatted
	 * @return A formatted {@link String} representation of <code>cdsDate</code>
	 */
	public static String format(Format format, CDSDate cdsDate) {
		String strDate = ""; //$NON-NLS-1$
		if (cdsDate != null) {
			try {
				strDate = format.format(cdsDate.toDate());
			} catch (ParseException e) {
				strDate = cdsDate.toString();
			}
		}
		return strDate;
	}

	/**
	 * Create a {@link Date} using the default format.
	 * 
	 * @param string
	 *            The date string.
	 * @return The parsed {@link Date}
	 * @throws ParseException
	 */
	public static Date toDate(String string) throws ParseException {
		synchronized (PDF_DATE_FORMAT) {
			return PDF_DATE_FORMAT.parse(string);
		}
	}

	/**
	 * Create a {@link Date} using the default format using the timezone.
	 * 
	 * @param string
	 *            The date string.
	 * @return The parsed {@link Date}
	 * @throws ParseException
	 */
	public static Date toDateWithZone(String string) throws ParseException {
		Matcher m = DatePattern.matcher(string);
		if (m.matches()) {
			int year = Integer.valueOf(m.group(1)).intValue();
			int month = Integer.valueOf(m.group(2)).intValue() - 1;
			int day = Integer.valueOf(m.group(3)).intValue();
			int hour = Integer.valueOf(m.group(4)).intValue();
			int min = Integer.valueOf(m.group(5)).intValue();
			int sec = Integer.valueOf(m.group(6)).intValue();
			int hourOffset = 0;
			int minOffset = 0;

			if (m.group(9) != null) {
				minOffset = Integer.valueOf(m.group(9)).intValue();
			}
			if (m.group(8) != null) {
				hourOffset = Integer.valueOf(m.group(8)).intValue();
			}
			if (m.group(7) != null) {
				String o = m.group(7);
				if ("z".equals(o.toLowerCase())) { //$NON-NLS-1$
					hourOffset = 0;
					minOffset = 0;
				} else if ("-".equals(o)) { //$NON-NLS-1$
					hourOffset *= -1;
					minOffset *= -1;
				}
				hour -= hourOffset;
				min -= minOffset;
			}

			// add local time zone
			int offset = TimeZone.getDefault().getOffset(
					System.currentTimeMillis());

			// determine if the requested date was in DST, if so add offset
			boolean nowDST = TimeZone.getDefault().inDaylightTime(new Date());
			GregorianCalendar testDate = new GregorianCalendar(year, month, day);
			boolean testDST = TimeZone.getDefault().inDaylightTime(
					testDate.getTime());
			if (nowDST) {
				if (!testDST) {
					offset = offset - TimeZone.getDefault().getDSTSavings();
				}
			} else {
				if (testDST) {
					offset = offset + TimeZone.getDefault().getDSTSavings();
				}
			}
			offset /= 60 * 1000;
			hourOffset = Math.abs(offset / 60);
			minOffset = offset % 60;
			if (offset < 0) {
				hour -= hourOffset;
				min -= minOffset;
			} else {
				hour += hourOffset;
				min += minOffset;
			}
			GregorianCalendar greg = new GregorianCalendar(year, month, day,
					hour, min, sec);
			return greg.getTime();
		}
		throw new ParseException("can't parse date string '" + string + "'", 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static String toString(Date date) {
		synchronized (PDF_DATE_FORMAT) {
			return PDF_DATE_FORMAT.format(date);
		}
	}

	public static String toStringWithZone(Date date) {
		return toStringWithZone(date, TimeZone.getDefault());
	}

	public static String toStringWithZone(Date date, TimeZone timeZone) {
		StringBuilder result = new StringBuilder();
		result.append(toString(date));
		int offset = timeZone.getOffset(date.getTime());
		if (offset < 0) {
			result.append("-"); //$NON-NLS-1$
		} else if (offset > 0) {
			result.append("+"); //$NON-NLS-1$
		} else {
			result.append("Z"); //$NON-NLS-1$
		}
		int hours = Math.abs(offset / (60 * 60 * 1000));
		if (hours / 10 < 1) {
			result.append("0"); //$NON-NLS-1$
		}
		result.append(hours);
		// TODO 1 @kkr day light saving time
		result.append("'00'"); //$NON-NLS-1$
		return result.toString();
	}

	/**
	 * Create a new date object with the current system date set
	 */
	public CDSDate() {
		super(COSString.create(toStringWithZone(new Date())));
	}

	/**
	 * Create a new date object with the date defined in
	 * <code>newDateString</code>.
	 * 
	 * @param newDateString
	 *            The string representation of the new CDSDate.
	 */
	protected CDSDate(COSString newDateString) {
		super(newDateString);
	}

	/**
	 * The {@link String} representation of this.
	 * 
	 * @return The {@link String} representation of this.
	 */
	public String stringValue() {
		return ((COSString) cosGetObject()).stringValue();
	}

	/**
	 * The {@link Date} represented by this.
	 * 
	 * @return The {@link Date} represented by this.
	 * @throws ParseException
	 */
	public Date toDate() throws ParseException {
		return toDateWithZone(stringValue());
	}

	/**
	 * A formatted {@link String} representation of this.
	 * 
	 * @return A formatted {@link String} representation of this.
	 */
	public String toFormattedString() {
		return format(this);
	}
}
