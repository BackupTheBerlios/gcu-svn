/**
 * PartEncoder.java
 *
 * Created: 28.12.2005 16:46:58
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
package de.mutantenzoo.gcu.io;

import org.w3c.dom.Element;

import de.mutantenzoo.gcu.model.Part;

/**
 * @author MKlemm
 *
 */
public class PartEncoder {
	public static void encode(Element parent, Part model) {
		Element element = parent.getOwnerDocument().createElement("Part");
		element.setAttribute("size", Integer.toString(model.getSize()));
		parent.appendChild(element);
	}

}
