// 255,255,255 -> 090,140,173
// 255,255,156 -> 123,140,123
// 132,132,000 -> 066,082,049
// 008,008,008 -> 000,016,049
// 049,049,000 -> 024,041,049
// 206,255,255 -> -99,140,173

package com.ibm.was.graphics;

import java.awt.*;
import java.awt.image.*;

public class RolloverComposite implements Composite {

	private static RolloverComposite instance = null;
	
	private RolloverComposite() {
		// Only static singleton instantiation;
	}
	
	public static RolloverComposite getInstance() {
		if(instance == null) {
			instance = new RolloverComposite();
		}
		return instance;
	}

	public CompositeContext createContext(ColorModel srcColorModel,
			ColorModel dstColorModel, RenderingHints hints) {
		
		return new RolloverCompositeContext();

	}
	
	private static class RolloverCompositeContext implements CompositeContext {

		public void dispose() {
			// Do nothing
			
		}

		public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
			for (int y = 0; y < dstOut.getHeight(); y++) {
				for (int x = 0; x < dstOut.getWidth(); x++) {
					// Get the source pixels
					int[] srcPixels = new int[4];
					src.getPixel(x, y, srcPixels);
					// Ignore transparent pixels
					//if (srcPixels[3] != 0) {
						// Lighten each color by 1/2, and increasing the
						// blue
						srcPixels[0] = srcPixels[0] / 2;
						srcPixels[1] = srcPixels[1] / 2;
						srcPixels[2] = srcPixels[2] / 2 + 68;
						
						dstOut.setPixel(x, y, srcPixels);
					//}
				}
			}
		}
		
	}
}
