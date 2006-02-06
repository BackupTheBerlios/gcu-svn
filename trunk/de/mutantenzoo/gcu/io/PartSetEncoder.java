/**
 * PartSetEncoder.java
 *
 * Created: 28.12.2005 16:54:38
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
import org.w3c.dom.NodeList;

import de.mutantenzoo.gcu.model.Part;
import de.mutantenzoo.gcu.model.PartSet;

/**
 * Utility class to serialize a PartSet object
 * to an XML element.
 * @author MKlemm
 *
 */
public class PartSetEncoder {
	
	/**
	 * Serializes a PartSet object to an XML element
	 * with a given name.
	 * @param parent The parent element to add the PartSet to.
	 * @param model The PartSet to serialize.
	 * @param elementName The name of the new XML element.
	 */
	public static void encode(Element parent, PartSet model, String elementName) {
		Element element = parent.getOwnerDocument().createElement(elementName);
		element.setAttribute("chainline", Double.toString(model.getChainline().getValue()));
		element.setAttribute("partPitch",Double.toString(model.getPartPitch().getValue()));
		for(Part part : model) {
			PartEncoder.encode(element, part);
		}
		parent.appendChild(element);
	}

	/**
	 * De-serializes a PartSet object from an XML element.
	 * @param parent The element containing the parts.
	 * @param model The PartSet to add the de-serialized objects to.
	 * @param elementName The name of the XML elements to read.
	 */
	public static void decode(Element parent, PartSet model, String elementName) {
		Element element = (Element)parent.getElementsByTagName(elementName).item(0);
		model.getChainline().setValue(Double.parseDouble(element.getAttribute("chainline")));
		model.getPartPitch().setValue(Double.parseDouble(element.getAttribute("partPitch")));
		NodeList partElements = element.getElementsByTagName("Part");
		
		for(int n=0; n<partElements.getLength(); n++) {
			Element partElement = (Element)partElements.item(n);
			model.createPart(Integer.parseInt(partElement.getAttribute("size")));
		}
	}

}
