/**
 * 
 */
package de.mutantenzoo.raf;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;


/**
 * @author MKlemm
 *
 */
public class Measure implements Quantifiable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double value;
	private DecimalFormat format;
	private String unit;
	private transient boolean changed = false;
	
	/**
	 * 
	 */
	public Measure(double value, DecimalFormat format, String unit) {
		setUnit(format, unit);
		setValue(value);
	}

	public Measure(double value) {
		setValue(value);
	}

	/**
	 * @return Returns the format.
	 */
	public DecimalFormat getFormat() {
		return format;
	}

	/**
	 * @param format The format to set.
	 */
	public void setFormat(DecimalFormat format) {
		this.format = format;
		this.changed = true;
	}

	/**
	 * @return Returns the unit.
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit The unit to set.
	 */
	public void setUnit(String unit) {
		this.unit = unit;
		this.changed = true;
	}

	/**
	 * @return Returns the value.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(double value) {
		this.value = value;
		this.changed = true;
	}

	public void setStringValue(String stringValue) throws ParseException {
		setValue(format.parse(stringValue).doubleValue());
	}
	
	public String getStringValue() {
		return format.format(value);
	}
	
	public void setUnit(DecimalFormat format, String unit) {
		this.format = format;
		this.unit = unit;
		this.changed = true;
	}
	
	@Override
	public String toString() {
		return getStringValue()+getUnit();
	}

	/**
	 * @return Returns the changed.
	 */
	public boolean isChanged() {
		return changed;
	}

	/**
	 * @param changed The changed to set.
	 */
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public void reset() {
		this.changed = false;
	}
	
}
