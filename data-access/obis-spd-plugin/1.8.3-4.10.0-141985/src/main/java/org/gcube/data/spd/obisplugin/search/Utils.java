package org.gcube.data.spd.obisplugin.search;

import static org.gcube.data.spd.obisplugin.search.query.QueryCondition.cond;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.products.DataSet;
import org.gcube.data.spd.obisplugin.search.query.QueryCondition;

public class Utils {

	protected static List<QueryCondition> elaborateConditions(Condition[] properties) throws Exception{
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		List<Condition> coordinateConditions = new ArrayList<Condition>();
		for (Condition prop: properties){
			switch (prop.getType()) {
			case COORDINATE:
				coordinateConditions.add(prop);
				break;
			case DATE:
				Calendar date = (Calendar) prop.getValue();
				queryConditions.addAll(getDateCondition(date, prop));
				break;		
			}
		}
		if (coordinateConditions.size()>0)
			queryConditions.add(getCoordinateConditions(coordinateConditions));

		return queryConditions;
	}

	public static ProductKey elaborateProductsKey(String id) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		String[] splitString = id.split("\\|\\|");
		queryConditions.add(cond("resourceid", splitString[0]));
		queryConditions.add(cond("obisid", splitString[1]));
		if (splitString.length>2)
			for (int i = 2; i<splitString.length; i++){
				String[] equalSplit = splitString[i].split("=");
				queryConditions.add(cond(equalSplit[0], equalSplit[1].replaceAll(" ", "%20")));
			}
		return new ProductKey(queryConditions);
	}

	protected static String createProductsKey(String dataResourceKey, String taxonKey, List<QueryCondition> queryConditions) {
		StringBuilder conditionTransformer = new StringBuilder();
		for (QueryCondition cond : queryConditions)
			conditionTransformer.append("||").append(cond.getKey()).append("=").append(cond.getValue());
		return dataResourceKey+"||"+taxonKey+conditionTransformer.toString();
	}

	public static QueryCondition getCoordinateConditions(List<Condition> coordinateConditions){
		double lowerLong =-180, lowerLat= -90, upperLat= 90, upperLong=180;
		double latitude, longitude;
		for (Condition cond :coordinateConditions){
			switch (cond.getOp()) {
			case EQ:
				break;
			case GT:
				latitude = ((Coordinate)cond.getValue()).getLatitude()+0.01;
				longitude = ((Coordinate)cond.getValue()).getLongitude()+0.01;
				if (latitude>lowerLat) lowerLat = latitude;
				if (longitude>lowerLong) lowerLong = longitude;
				break;
			case GE:
				latitude = ((Coordinate)cond.getValue()).getLatitude();
				longitude = ((Coordinate)cond.getValue()).getLongitude();
				if (latitude>lowerLat) lowerLat = latitude;
				if (longitude>lowerLong) lowerLong = longitude;
				break;
			case LT:
				latitude = ((Coordinate)cond.getValue()).getLatitude()-0.01;
				longitude = ((Coordinate)cond.getValue()).getLongitude()-0.01;
				if (latitude<upperLat) upperLat = latitude;
				if (longitude>upperLong) upperLong = longitude;
				break;
			case LE:
				latitude = ((Coordinate)cond.getValue()).getLatitude();
				longitude = ((Coordinate)cond.getValue()).getLongitude();
				if (latitude<upperLat) upperLat = latitude;
				if (longitude>upperLong) upperLong = longitude;
				break;
			default:
				
				break;
			}
		}
		
		return cond("geometry", String.format("POLYGON((%1$f %2$f,%3$f %4$f,%5$f %6$f,%1$f %2$f))"
				,lowerLat, lowerLong, upperLat, lowerLong, upperLat, upperLong, lowerLat, upperLong).replaceAll(" ", "%20"));
		
	}

	public static List<QueryCondition> getDateCondition(Calendar date, Condition prop){
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();  
		DateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd");
		Calendar newDate = date;
		switch (prop.getOp()) {
		case EQ:
			conditions.add(cond("eventDate",dateFormat.format(date.getTime())));
			break;
		case GT:
			newDate.add(Calendar.DAY_OF_MONTH, 1);
			conditions.add(cond("startdate",dateFormat.format(newDate.getTime())));
			break;
		case GE:
			conditions.add(cond("startdate",dateFormat.format(date.getTime())));
			break;
		case LT:
			newDate.add(Calendar.DAY_OF_MONTH, -1);
			conditions.add(cond("enddate",dateFormat.format(newDate.getTime())));
			break;
		case LE:
			conditions.add(cond("enddate",dateFormat.format(date.getTime())));
			break;
		default:
			break;
		}
		return conditions;
	}

	protected static String getDataSetAsString(DataSet dataset){
		return dataset.getId();
	}
	
}
