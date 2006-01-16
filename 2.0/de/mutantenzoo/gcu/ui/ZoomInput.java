/**
 * ZoomInput.java
 *
 * Created: 13.12.2005 08:36:01
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
package de.mutantenzoo.gcu.ui;

import javax.swing.JSlider;

import de.mutantenzoo.gcu.model.DriveTrain;
import de.mutantenzoo.gcu.model.DriveTrainStyle;
import de.mutantenzoo.raf.ContentPanel;

/**
 * @author MKlemm
 *
 */
public class ZoomInput extends ContentPanel {
	
	/**
	 * Generated SUID
	 */
	private static final long serialVersionUID = 7501545759244293162L;
	private JSlider slider = null;
	private DriveTrain model;
	private DriveTrainStyle style;

	/**
	 * 
	 */
	public ZoomInput(DriveTrain model, DriveTrainStyle style) {
		super();
		this.model = model;
		this.style = style;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.add(getSlider(), null);
		
	}

	/**
	 * This method initializes slider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getSlider() {
		if (slider == null) {
			slider = new JSlider(50,200,100);
			slider.setMinorTickSpacing(10);
			slider.setMajorTickSpacing(50);
			slider.setPaintLabels(true);
			slider.setPaintTicks(true);
			slider.setToolTipText("Zoom");
			slider.setOrientation(JSlider.VERTICAL);
			slider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					style.setZoomFactor(slider.getValue() / 100.0);
					fireStyleChanged();
				}
			});
		}
		return slider;
	}

	/**
	 * Creates slider labels
	 */
	/*private void createCustomLabels() {
		Hashtable<Integer,JButton> labelTable = new Hashtable<Integer,JButton>(4);
		labelTable.put(50,new JButton("50%"));
		labelTable.put(100,new JButton("100%"));
		labelTable.put(150,new JButton("150%"));
		labelTable.put(200,new JButton("200%"));
		for(Map.Entry<Integer,JButton> pair : labelTable.entrySet()) {
			pair.getValue().addActionListener(new SliderPositionListener(pair));
		}
		slider.setLabelTable(labelTable);
	}
	
	private class SliderPositionListener implements ActionListener {
		private Map.Entry<Integer,JButton> pair;
		
		SliderPositionListener(Map.Entry<Integer,JButton> pair) {
			this.pair = pair;
		}
		
		public void actionPerformed(ActionEvent e) {
			slider.setValue(pair.getKey());
			fireContentChanged();
		}
	}*/

	/**
	 * @return Returns the model.
	 */
	public DriveTrain getModel() {
		return model;
	}

	/**
	 * @param model The model to set.
	 */
	public void setModel(DriveTrain model) {
		this.model = model;
	}

	/**
	 * @return Returns the style.
	 */
	public DriveTrainStyle getStyle() {
		return style;
	}

	/**
	 * @param style The style to set.
	 */
	public void setStyle(DriveTrainStyle style) {
		this.style = style;
	}

}
