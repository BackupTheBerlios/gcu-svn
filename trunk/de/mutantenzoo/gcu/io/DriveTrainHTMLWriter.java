/**
 * DriveTrainHTMLWriter.java
 *
 * Created: 30.12.2005 11:32:01
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

import de.mutantenzoo.gcu.ui.Messages;
import de.mutantenzoo.gcu.ui.TableAdapter;

/**
 * @author MKlemm
 *
 */
public class DriveTrainHTMLWriter {

	public static void writeHTML(PrintStream out, TableAdapter model) {
		out.println("<html>");
		out.println("  <head>");
		out.println("    <title>"+model.getModel().getFile().getName()+"</title>");
		out.println("    <style>table {border-collapse: collapse;} td {text-align: left; border: solid 1px black;} th {text-align: center; border: solid 1px black; background-color: #F0F0FF;}</style>");
		out.println("  </head>");
		out.println("  <body>");
		out.println("    <h1>"+model.getModel().getFile().getName()+"</h1>");
		out.println("    <p>"+Messages.getString("Cadence")+": "+model.getModel().getCadence().getStringValue()+"</p>");
		out.println("    <table>");
		out.print("      <tr>");
		for(int n=0; n<model.getColumnCount(); n++) {
			if(n == model.getColumnCount()-1) {
				out.print("<th width=\"100%\">");
			} else {
				out.print("<th>");
			}
			out.print(model.getColumnName(n));
			out.print("</th>");
		}
		out.println("</tr>");	
		for(int row=0; row<model.getRowCount(); row++) {
			out.print("      <tr>");
			for(int col=0; col<model.getColumnCount(); col++) {
				out.print("<td>");
				out.print(model.getValueAt(row, col));
				out.print("</td>");
			}
			out.println("</tr>");
		}
		out.println("    </table>");
		out.println("  </body>");
		out.println("</html>");
	}

}
