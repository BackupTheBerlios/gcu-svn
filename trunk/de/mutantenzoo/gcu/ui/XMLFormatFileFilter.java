/**
 * XMLFormatFileFilter.java
 *
 * Created: 10.01.2006 15:13:47
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
package de.mutantenzoo.gcu.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

final class XMLFormatFileFilter extends FileFilter {
	@Override
	public boolean accept(File file) {
		return file.getName().toLowerCase().endsWith(".xml") //$NON-NLS-1$
		|| file.getName().toLowerCase().endsWith(".txt") //$NON-NLS-1$
		|| file.isDirectory(); 
	}

	@Override
	public String getDescription() {
		return Messages.getString("XMLFileFormat"); //$NON-NLS-1$
	}
}