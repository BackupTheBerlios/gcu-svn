/**
 * DriveTrainCSVWriter.java
 *
 * Created: 30.12.2005 13:34:55
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

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Collection;

import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.Gear;

/**
 * Utility class to export one or
 * more DriveTrains as a CSV file.
 * @author MKlemm
 *
 */
public class DriveTrainCSVWriter {

	/**
	 * The format of numbers in the CSV file, 
	 * makes numbers formatted in a locale-specific
	 * way, because MS-Excel and friends expect it like this...
	 */
	public static DecimalFormat format = new DecimalFormat("###0.##########");
	
	/**
	 * Writes a drivetrain out into an
	 * output stream, formatted as CSV data.
	 * @param out Output stream for CSV data.
	 * @param model The DriveTrain to export.
	 */
	public static void writeCSV(PrintStream out, DriveTrain model) {
		int count = model.getGearCount();
		for(int n=0; n<count; n++) {
			Gear g = model.getGear(n);
			out.println(
					model.getName()+";"+
					g.getChainwheel().getSize()+";"+
					g.getSprocket().getSize()+";"+
					format.format(g.getTrans())+";"+
					g.getDevelopment().getStringValue()+";"+
					g.getSpeed().getStringValue()+";"+
					format.format(g.getDragAngle())
					);
		}
	}
	
	/**
	 * Writes a collection of DriveTrains
	 * as CSV data into an output stream.
	 * @param out Stream to write the data to
	 * @param models Colletion of DriveTrain objects to export.
	 */
	public static void writeCSV(PrintStream out, Collection<DriveTrain> models) {
		for(DriveTrain model : models) {
			writeCSV(out, model);
		}
	}

}
