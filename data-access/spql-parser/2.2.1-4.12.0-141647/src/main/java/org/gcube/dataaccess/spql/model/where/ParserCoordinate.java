/**
 * 
 */
package org.gcube.dataaccess.spql.model.where;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ParserCoordinate extends AbstractParsableValue<Coordinate> {

	protected String latitudeText;
	protected String longitudeText;

	public ParserCoordinate(String latitude, String longitude) {
		this.latitudeText = latitude;
		this.longitudeText = longitude;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTextValue() {
		StringBuilder textValue = new StringBuilder();
		textValue.append(latitudeText);
		textValue.append(" , ");
		textValue.append(longitudeText);
		return textValue.toString();
	}

	@Override
	public void parse() {
		double latitude = Double.parseDouble(latitudeText);
		double longitude = Double.parseDouble(longitudeText);
		Coordinate value = new Coordinate(latitude, longitude);
		setValue(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ParserCoordinate [latitude=");
		builder.append(latitudeText);
		builder.append(", longitude=");
		builder.append(longitudeText);
		builder.append("]");
		return builder.toString();
	}

}
