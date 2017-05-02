package org.gcube.data.spd.specieslink;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Condition.Operator;
import org.gcube.data.spd.model.Coordinate;

public class Utils {

	public static String credits;

	//create filters
	static String elaborateProps(Condition[] properties) throws Exception{
		StringBuilder props =new StringBuilder(); 
		for (Condition prop: properties){

			String operator = getOperator(prop.getOp());

			switch (prop.getType()) {

			case COORDINATE:
				Coordinate coord = (Coordinate)prop.getValue();

				double latitude = coord.getLatitude();
				double longitude = coord.getLongitude();
				props.append("%20and%20http://rs.tdwg.org/dwc/geospatial/DecimalLatitude%20");
				props.append(operator);
				props.append("%20%22");
				props.append(latitude);
				props.append("%22");

				props.append("%20and%20http://rs.tdwg.org/dwc/geospatial/DecimalLongitude%20");
				props.append(operator);
				props.append("%20%22");
				props.append(longitude);
				props.append("%22");

				break;  

			case DATE:
				Calendar calendarDate = (Calendar) prop.getValue();
				DateFormat dfFrom = new  SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");			
				props.append("%20and%20http://rs.tdwg.org/dwc/terms/eventTime%20");
				props.append(operator);
				props.append("%20%22");
				props.append(dfFrom.format(calendarDate.getTime()));
				props.append("%22");

				break;

			default:
				break;
			}
		}
		return props.toString();
	}

	private static String getOperator(Operator op) {

		if (op.equals(Operator.EQ)){
			return ("equals");
		}else if (op.equals(Operator.GE)){
			return ("greaterThanOrEquals");
		}else if (op.equals(Operator.GT)){
			return ("greaterThan");
		}else if (op.equals(Operator.LE)){
			return ("lessThanOrEquals");
		}else if (op.equals(Operator.LT)){
			return ("lessThan");
		}
		return null;

	}

	public static String credits() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String credits = SpeciesLinkPlugin.credits.replace("XDATEX", format.format(now.getTime()));
		return credits;
	}

	public static String citation() {
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String citation = SpeciesLinkPlugin.citation.replace("XDATEX", format.format(now.getTime()));
		return citation;
	}
}
