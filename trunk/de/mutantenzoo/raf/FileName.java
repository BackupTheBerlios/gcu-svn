/**
 * FileName.java
 *
 * Created: 18.01.2006 08:52:16
 *
 * $Id$
 *
 * Copyright 2005 MKlemm
 *
 * This file is part of GCU.
 *
 * GCU is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GCU; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package de.mutantenzoo.raf;

import java.io.File;

/**
 * @author MKlemm
 *
 */
public class FileName {

	private FileName() {
		// cannot instantiate
	}
	
	public static final String strip(String fileName) {
		int dotPos = fileName.lastIndexOf('.');
		if(dotPos > -1) {
			return fileName.substring(0, dotPos);
		} else {
			return fileName;
		}
	}
	
	public static final String extend(String fileName, String ext) {
		int dotPos = fileName.lastIndexOf('.');
		if(dotPos > -1) {
			return fileName;
		} else {
			return fileName + ext;
		}
	}

	public static final String extendReplace(String fileName, String ext) {
		if(fileName.toLowerCase().endsWith(ext.toLowerCase())) {
			return fileName;
		} else {
			int dotPos = fileName.lastIndexOf('.');
			if(dotPos > -1) {
				return extend(strip(fileName), ext);
			} else {
				return fileName + ext;
			}
		}
	}
	
	public static final File strip(File file) {
		return new File(strip(file.getPath()));
	}
	
	public static final File extend(File file, String ext) {
		return new File(extend(file.getPath(), ext));
	}
	
	public static final File extendReplace(File file, String ext) {
		return new File(extendReplace(file.getPath(), ext));
	}
}
