/**
 * DriveTrainEncoder.java
 *
 * Created: 28.12.2005 17:04:22
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.ui.Messages;
import de.mutantenzoo.gcu.units.UnitSystem;

/**
 * @author MKlemm
 *
 */
public class DriveTrainEncoder {

	public static void encode(Node parent, DriveTrain model) {
		Element element = parent.getOwnerDocument().createElement("DriveTrain");
		element.setAttribute("name", model.getName());
		element.setAttribute("uuid", model.getUUID().toString());
		
		if(model.getUnitSystem().equals(UnitSystem.METRIC)) {
			element.setAttribute("unitSystem","METRIC");
		} else {
			element.setAttribute("unitSystem","IMPERIAL");
		}
		element.setAttribute("maxOKDragAngle", Double.toString(model.getMaxOKDragAngle().getValue()));
		element.setAttribute("maxGoodDragAngle", Double.toString(model.getMaxGoodDragAngle().getValue()));
		element.setAttribute("rearCenter", Double.toString(model.getRearCenter().getValue()));
		element.setAttribute("wheelCirc", Double.toString(model.getWheelCirc().getValue()));
		element.setAttribute("cadence", Double.toString(model.getCadence().getValue()));
		PartSetEncoder.encode(element, model.getChainwheels(), "Chainwheels");
		PartSetEncoder.encode(element, model.getSprockets(), "Sprockets");
		parent.appendChild(element);
	}

	public static void encode(OutputStream out, DriveTrain model) throws TransformerException, ParserConfigurationException {
		ArrayList<DriveTrain> models = new ArrayList<DriveTrain>(1);
		models.add(model);
		encode(out, models);
	}
	
	public static void encode(OutputStream out, Collection<DriveTrain> models) throws TransformerException, ParserConfigurationException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty( OutputKeys.INDENT ,"yes");
		transformer.setOutputProperty( OutputKeys.METHOD ,"xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(new DOMSource(createDocument(models)), new StreamResult(out));
		
	}

	public static Document createDocument(Collection<DriveTrain> models) throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document doc = documentBuilder.newDocument();
		Element rootElement = doc.createElement("DriveTrains");
		doc.appendChild(rootElement);
		for(DriveTrain model : models) {
			encode(rootElement, model);
		}
		return doc;
	}
	
	public static DriveTrain[] decode(InputStream is) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document doc = documentBuilder.parse(is);

		Element rootElement = doc.getDocumentElement();
		if(rootElement.getNodeName().equals("DriveTrain")) {
			return new DriveTrain[] {decode(rootElement)};
		} else if(rootElement.getNodeName().equals("DriveTrains")) {
			NodeList elements = rootElement.getElementsByTagName("DriveTrain");
			DriveTrain[] results = new DriveTrain[elements.getLength()];
			for(int n=0; n<elements.getLength(); n++) {
				results[n] = decode((Element)elements.item(n));
			}
			return results;
		} else {
			throw new IOException(Messages.getString("Error.FileFormat"));
		}
	}
	
	public static DriveTrain decode(Element element) {
		DriveTrain model = new DriveTrain();
		if(element.hasAttribute("name")) {
			model.setName(element.getAttribute("name"));
		}

		if(element.hasAttribute("uuid")) {
			UUID uuid = UUID.fromString(element.getAttribute("uuid"));
			model.setUUID(uuid);
		}
		if(element.getAttribute("unitSystem").equals("METRIC")) {
			model.setUnitSystem(UnitSystem.METRIC);
		} else {
			model.setUnitSystem(UnitSystem.IMPERIAL);
		}
		model.getMaxOKDragAngle().setValue(Double.parseDouble(element.getAttribute("maxOKDragAngle")));
		model.getMaxGoodDragAngle().setValue(Double.parseDouble(element.getAttribute("maxGoodDragAngle")));
		model.getRearCenter().setValue(Double.parseDouble(element.getAttribute("rearCenter")));
		model.getWheelCirc().setValue(Double.parseDouble(element.getAttribute("wheelCirc")));
		model.getCadence().setValue(Double.parseDouble(element.getAttribute("cadence")));
		PartSetEncoder.decode(element, model.getChainwheels(), "Chainwheels");
		PartSetEncoder.decode(element, model.getSprockets(), "Sprockets");
		return model;
	}
}
