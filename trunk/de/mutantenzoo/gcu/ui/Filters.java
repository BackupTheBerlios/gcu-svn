package de.mutantenzoo.gcu.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

class Filters {

	public static final FileFilter BINARY = new BinaryFileFilter();
	public static final FileFilter XML = new XMLFileFilter();
	public static final FileFilter CSV = new CSVFileFilter();
	public static final FileFilter HTML = new HTMLFileFilter();
	public static final FileFilter PNG = new PNGFileFilter();
	
	private Filters() {
		// not instatiatable
	}

	static final class BinaryFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".rrp") || file.isDirectory(); //$NON-NLS-1$
		}

		@Override
		public String getDescription() {
			return Messages.getString("Main.18"); //$NON-NLS-1$
		}
	}
	
	static final class XMLFileFilter extends FileFilter {
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
	
	/**
	 * @author MKlemm
	 *
	 */
	static class HTMLFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".htm") //$NON-NLS-1$
			|| file.getName().toLowerCase().endsWith(".html") //$NON-NLS-1$
			|| file.isDirectory(); 
		}

		@Override
		public String getDescription() {
			return Messages.getString("HTMLFileFormat"); //$NON-NLS-1$
		}
	}

	/**
	 * @author MKlemm
	 *
	 */
	static class CSVFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".csv") //$NON-NLS-1$
			|| file.isDirectory(); 
		}

		@Override
		public String getDescription() {
			return Messages.getString("CSVFileFormat"); //$NON-NLS-1$
		}
	}
	
	/**
	 * @author MKlemm
	 *
	 */
	static class PNGFileFilter extends FileFilter {
		@Override
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".png") //$NON-NLS-1$
			|| file.isDirectory(); 
		}

		@Override
		public String getDescription() {
			return Messages.getString("PNGFileFormat"); //$NON-NLS-1$
		}

	}



}
