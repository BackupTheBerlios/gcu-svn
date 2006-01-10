/**
 * 
 */
package de.mutantenzoo.raf;

import java.text.ParseException;

/**
 * @author MKlemm
 *
 */
public interface Quantifiable {
	double getValue();
	void setValue(double value);
	
	String getStringValue();
	void setStringValue(String value) throws ParseException;
	
	String getUnit();
}
