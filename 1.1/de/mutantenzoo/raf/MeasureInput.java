/**
 * 
 */
package de.mutantenzoo.raf;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * @author MKlemm
 *
 */
public class MeasureInput extends ContentPanel implements View<Quantifiable>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JTextField textField = new JTextField(4);
	private JLabel unitLabel = new JLabel();
	private JLabel label;
	private Color backgroundColor;
	
	private Quantifiable model = null;
	
	/**
	 * 
	 */
	public MeasureInput(String text, Quantifiable model) {
		this.label = new JLabel(text, JLabel.RIGHT);
		setModel(model);
	}

	public MeasureInput(String text) {
		this.label = new JLabel(text, JLabel.RIGHT);
	}

	public void createIn(JPanel panel) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;
		gbc.weighty = 1.0;
		panel.add(label, gbc);
		
		gbc.anchor = GridBagConstraints.WEST;
		textField.setMinimumSize(textField.getPreferredSize());
		panel.add(textField, gbc);
		backgroundColor = textField.getBackground();
		textField.addFocusListener(new TextFieldListener());

		gbc.weightx = 0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel.add(unitLabel, gbc);
	}

	private class TextFieldListener implements FocusListener {

		public void focusGained(FocusEvent e) {
			// Not handled
			
		}

		public void focusLost(FocusEvent e) {
			if(model != null) {
				try {
					String text = textField.getText().trim();
					if(!model.getStringValue().equals(text)) {
						model.setStringValue(text);
						textField.setBackground(backgroundColor);
						fireContentChanged();
					}
				} catch(ParseException nfx) {
					textField.setBackground(Color.RED);
				}
			}
		}
		
	}
	
	public void setText(String text) {
		label.setText(text);
	}
	
	public void setModel(Quantifiable model) {
		this.model = model;
	}
	
	/**
	 * @return Returns the textField.
	 */
	public JTextField getTextField() {
		return textField;
	}

	/**
	 * @return Returns the unitLabel.
	 */
	public JLabel getUnitLabel() {
		return unitLabel;
	}
	
	public void update() {
		if( model != null) {
			textField.setBackground(backgroundColor);
			textField.setText(model.getStringValue());
			unitLabel.setText(model.getUnit());
		}
	}

	public Quantifiable getModel() {
		return model;
	}

}
