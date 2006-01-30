package de.mutantenzoo.gcu.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import de.mutantenzoo.raf.FileName;

class SuffixFileFilter extends FileFilter {

	public static final SuffixFileFilter BINARY = new SuffixFileFilter(".rrp");
	public static final SuffixFileFilter XML = new SuffixFileFilter(".txt", ".rpx", ".xml");
	public static final SuffixFileFilter CSV = new SuffixFileFilter(".csv");
	public static final SuffixFileFilter HTML = new SuffixFileFilter(".html", ".htm", ".xhtml", ".xhm", ".xml");
	public static final SuffixFileFilter PNG = new SuffixFileFilter(".png");
	
	private String description;
	private String canonicalSuffix;
	private String[] suffices;
	
	private SuffixFileFilter(String canonicalSuffix) {
		this(canonicalSuffix, new String[0]);
	}
	
	private SuffixFileFilter(String canonicalSuffix, String ... suffices) {
		this.canonicalSuffix = canonicalSuffix;
		this.suffices = suffices;
		this.description = Messages.getString(canonicalSuffix) + " (" + listExtensions() + ")";
	}

	private String listExtensions() {
		StringBuffer sb = new StringBuffer("*"+canonicalSuffix);
		for(String suffix : suffices) {
			sb.append(", *");
			sb.append(suffix);
		}
		return sb.toString();
	}
	
	public String makeAcceptable(String fileName) {
		if(!accept(fileName)) {
			return FileName.extend(fileName, canonicalSuffix);
		} else {
			return fileName;
		}
	}

	public File makeAcceptable(File file) {
		if(!accept(file)) {
			return FileName.extend(file, canonicalSuffix);
		} else {
			return file;
		}
	}
	
	@Override
	public boolean accept(File file) {
		String fileName = file.getName().toLowerCase();
		if(file.isDirectory()) {
			return true;
		} else {
			return accept(fileName);
		}
	}
	
	public boolean accept(String fileName) {
		if(fileName.endsWith(canonicalSuffix)) {
			return true;
		} else {
			for(String suffix : suffices) {
				if( fileName.endsWith(suffix)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public String getDescription() {
		return description;
	}

	

}
