package de.mutantenzoo.raf;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;

import com.ibm.was.graphics.RolloverIcon;

public class IconBundle {
	public static final int DEFAULT_LARGE_ICON_SIZE = 24;
	public static final int DEFAULT_SMALL_ICON_SIZE = 16;
	public static final String DEFAULT_RESOURCE_BASE = "/toolbarButtonGraphics/general";
	public static final String DEFAULT_FILE_SUFFIX = ".gif";

	private String resourceBase;
	private String fileSuffix;
	private int largeIconSize;
	private int smallIconSize;

	private String name;
	private ImageIcon largeIcon = null;
	private ImageIcon smallIcon = null;
	private ImageIcon smallRolloverIcon = null;
	private ImageIcon largeRolloverIcon = null;
	
	protected IconBundle(String name) {
		this(DEFAULT_RESOURCE_BASE,
				name,
				DEFAULT_FILE_SUFFIX,
				DEFAULT_LARGE_ICON_SIZE,
				DEFAULT_SMALL_ICON_SIZE);
	}

	/**
	 * @param resourceBase
	 * @param largeIconSize
	 * @param smallIconSize
	 */
	protected IconBundle(String resourceBase, String name, String fileSuffix, int largeIconSize, int smallIconSize) {
		this.resourceBase = resourceBase;
		this.name = name;
		this.fileSuffix = fileSuffix;
		this.largeIconSize = largeIconSize;
		this.smallIconSize = smallIconSize;
	}

	/**
	 * @return Returns the largeIcon.
	 */
	public ImageIcon getLargeIcon(Component component) {
		if(largeIcon == null) {
			largeIcon = loadIcon(component, largeIconSize);
		}
		return largeIcon;
	}

	/**
	 * @return Returns the largeRolloverIcon.
	 */
	public ImageIcon getLargeRolloverIcon(Component component) {
		if(largeRolloverIcon == null) {
			largeRolloverIcon = new RolloverIcon(component, getLargeIcon(component));
		}
		return largeRolloverIcon;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the smallIcon.
	 */
	public ImageIcon getSmallIcon(Component component) {
		if(smallIcon == null) {
			smallIcon = loadIcon(component, smallIconSize);
		}
		return smallIcon;
	}

	/**
	 * @return Returns the smallRolloverIcon.
	 */
	public ImageIcon getSmallRolloverIcon(Component component) {
		if(smallRolloverIcon == null) {
			smallRolloverIcon = new RolloverIcon(component, getSmallIcon(component));
		}
		return smallRolloverIcon;
	}

	
	
	/**
	 * Gets an Icon from an image resource.
	 * The image data is converted to the internal RGBA
	 * color space to allow for rollover blending effects
	 * @param size
	 * @return A new ImageIcon instance
	 */
	private ImageIcon loadIcon(Component component, int size) {
		String imageLocation = resourceBase+"/"+name+size+fileSuffix;
		URL imageURL = getClass().getResource(imageLocation); 
		Image srcImage = Toolkit.getDefaultToolkit().getImage(imageURL);
		ImageIcon icon = new ImageIcon();	
		
		MediaTracker mt = new MediaTracker(component);
		
		mt.addImage(srcImage, 1);
		
		try {
			mt.waitForID(1);
		} catch (InterruptedException ix) {
			// Not handled
		}
		
		BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
		
		img.getGraphics().drawImage(srcImage, 0, 0, size, size, null);

		icon.setImage(img);
		
		return icon;
	}

	
}
