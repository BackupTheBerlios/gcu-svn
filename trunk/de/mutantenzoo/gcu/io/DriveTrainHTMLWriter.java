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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.io.File;

import de.mutantenzoo.gcu.ui.DriveTrainComparisonView;
import de.mutantenzoo.gcu.ui.Messages;
import de.mutantenzoo.gcu.ui.TableAdapter;
import de.mutantenzoo.raf.FileName;

/**
 * Utility class to export DriveTrain and
 * comparison view data as an HTML document.
 * @author MKlemm
 *
 */
public class DriveTrainHTMLWriter {

	/**
	 * Writes an HTML representation of
	 * the drive train drawing and the
	 * table view to an output stream.
	 * @param out The stream to write the HTML to.
	 * @param model The TableAdapter that contains the DriveTrain data to write.
	 * @param imageURL The URL of the embedded image showing the drive train drawing.
	 */
	public static void writeHTML(PrintStream out, TableAdapter model, String imageURL) {
		out.println("<html>");
		out.println("  <head>");
		out.println("    <title>"+model.getModel().getName()+"</title>");
		out.println("    <style>table {border-collapse: collapse;} td {text-align: left; border: solid 1px black;} th {text-align: center; border: solid 1px black; background-color: #F0F0FF;}</style>");
		out.println("  </head>");
		out.println("  <body>");
		out.println("    <h1>"+model.getModel().getName()+"</h1>");
		out.println("    <p>"+Messages.getString("Cadence")+": "+model.getModel().getCadence().getStringValue()+"</p>");
		out.print("    <img align=\"left\" src=\""+imageURL+"\" alt=\""+Messages.getString("Drawing")+"\" />");
		out.println("<table>");
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

	/**
	 * Writes an HTML representation of the
	 * compariso view to a file.
	 * @param htmlFile The file to write the HTML to.
	 * @param view The DriveTrainComparisonView to export.
	 * @throws IOException if writing to the file fails.
	 */
	public static void writeComparisonHTML(File htmlFile, DriveTrainComparisonView view) throws IOException {
		String path = htmlFile.getPath();
		String title = FileName.strip(htmlFile.getName());
		PrintStream out = new PrintStream(new FileOutputStream(path));
		DriveTrainPNGWriter png = new DriveTrainPNGWriter(view.getRowHeader().getViewSize().width, view.getRowHeader().getViewSize().height + view.getColumnHeader().getViewSize().height);
		png.getGraphics().translate(0, view.getColumnHeader().getViewSize().getHeight());
		view.getRowHeader().getView().paint(png.getGraphics());
		FileOutputStream pngOut = new FileOutputStream(FileName.strip(path)+"_leftImage.png");
		png.save(pngOut);
		pngOut.close();
		png = new DriveTrainPNGWriter(view.getViewport().getViewSize().width, view.getRowHeader().getViewSize().height + view.getColumnHeader().getViewSize().height);
		view.getColumnHeader().getView().paint(png.getGraphics());
		png.getGraphics().translate(0, view.getColumnHeader().getViewSize().height);
		view.getViewport().getView().paint(png.getGraphics());
		pngOut = new FileOutputStream(FileName.strip(path)+"_rightImage.png");
		png.save(pngOut);
		pngOut.close();
		out.println("<html>");
		out.println("  <head>");
		out.println("    <title>"+title+"</title>");
		out.println("    <style>table {border-collapse: collapse; width: 100%;} td {text-align: left; border: solid 0px black; margin: 0 0 0 0;} th {text-align: center; border: solid 1px black; background-color: #F0F0FF;}</style>");
		out.println("  </head>");
		out.println("  <body>");
		out.println("    <h1>"+title+"</h1>");
		out.println("    <table><tr><td valign=\"top\"><img src=\""+title+"_leftImage.png\" /></td><td valign=\"top\" width=\"100%\"><div style=\"overflow: auto\"><img src=\""+title+"_rightImage.png\" /></div></td></tr></table>");
		out.println("  </body>");
		out.println("</html>");
		out.close();
	}

}
