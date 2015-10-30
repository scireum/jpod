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
package de.intarsys.pdf.tools.kernel;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.LocatorFactory;
import de.intarsys.tools.locator.URLLocator;
import de.intarsys.tools.system.SystemTools;

public class PDFFileTools {

	protected static ILocator getRoot(ILocator locator) {
		ILocator root = locator;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}

	public static ILocator resolveLocator(String osIndependentPath,
			ILocator baseLocator) throws IOException {
		if (osIndependentPath.contains("://")) {
			// special case: url
			return new URLLocator(new URL(osIndependentPath));
		}
		String result = toOSPath(osIndependentPath);
		if (baseLocator == null) {
			return LocatorFactory.get().createLocator(result);
		}
		if (result.startsWith("\\") && !result.startsWith("\\\\")) {
			// windows absolute without drive
			return getRoot(baseLocator).getChild(result);
		} else {
			File resultFile = new File(result);
			if (resultFile.isAbsolute()) {
				return LocatorFactory.get().createLocator(result);
			} else {
				return baseLocator.getChild(result);
			}
		}
	}

	public static String toOSIndependentPath(String osPath) {
		// todo mac support
		if (SystemTools.isWindows()) {
			return windowsToOSIndependentPath(osPath);
		} else {
			return osPath;
		}
	}

	public static String toOSPath(String osIndependentPath) {
		// todo mac support
		if (SystemTools.isWindows()) {
			return toWindowsPath(osIndependentPath);
		} else {
			return osIndependentPath;
		}
	}

	protected static String toWindowsPath(String osIndependentPath) {
		if (File.separatorChar == '/') {
			return osIndependentPath;
		}
		if (osIndependentPath.startsWith("///")) {
			// unc
			return "\\\\" + osIndependentPath.substring(3).replace("/", "\\");
		} else if (osIndependentPath.startsWith("//")) {
			// absolute without drive
			return "\\" + osIndependentPath.substring(2).replace("/", "\\");
		} else if (osIndependentPath.startsWith("/")) {
			// absolute with drive
			String windows = osIndependentPath.substring(1).replace("/", "\\");
			int index = windows.indexOf("\\");
			if (index < 0) {
				index = windows.length();
			}
			File drive = new File(windows.substring(0, index) + ":");
			if (drive.isDirectory()) {
				windows = windows.substring(0, index) + ":"
						+ windows.substring(index);
			} else {
				// fallback for Acrobat 5 compatibility
				windows = "\\\\" + windows;
			}
			return windows;
		} else {
			// relative path
			return osIndependentPath.replace("/", "\\");
		}
	}

	protected static String windowsToOSIndependentPath(String osPath) {
		String osIndependentPath = osPath.replaceAll("\\\\", "/");
		int index = osIndependentPath.indexOf(":");
		if ((index >= 0) && (index < osIndependentPath.indexOf("/"))) {
			osIndependentPath = "/" + osIndependentPath.substring(0, index)
					+ osIndependentPath.substring(index + 1);
		}
		return osIndependentPath;
	}

}
