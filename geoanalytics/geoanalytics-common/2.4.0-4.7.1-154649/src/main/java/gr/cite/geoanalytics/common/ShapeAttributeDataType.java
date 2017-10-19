package gr.cite.geoanalytics.common;


/**
 * 
 * TODO incorporate length info in order to take advantage of length restrictions inherent in data
 *
 */
public enum ShapeAttributeDataType
{
	INTEGER("int"),
	SHORT("short"),
	LONG("long"),
	FLOAT("float"),
	DOUBLE("double"),
	STRING("string"),
	LONGSTRING("string"),
	DATE("date");
	
	private String xmlType;
	
	private ShapeAttributeDataType(String xmlType) {
		this.xmlType = xmlType;
	}
	
	public String getXmlType() {
		return xmlType;
	}
}