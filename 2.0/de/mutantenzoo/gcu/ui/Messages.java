/**
 * Created: 13.12.2005 08:36:01
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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "de.mutantenzoo.gcu.ui.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
		// Cannot instantiate
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	public static MessageFormat getFormat(String key) {
		try {
			MessageFormat format = new MessageFormat(RESOURCE_BUNDLE.getString(key));
			return format;
		} catch (MissingResourceException e) {
			return null;
		}
	}

	public static String format(String key, Object ...objects) {
		try {
			return MessageFormat.format(getString(key), objects);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	public static String getString(String category, String key, String subKey) {
		try {
			return RESOURCE_BUNDLE.getString(category+"."+key+"."+subKey);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	public static String getString(String category, String key, String subKey, String defaultValue) {
		try {
			return RESOURCE_BUNDLE.getString(category+"."+key+"."+subKey);
		} catch (MissingResourceException e) {
			return defaultValue;
		}
	}
}
