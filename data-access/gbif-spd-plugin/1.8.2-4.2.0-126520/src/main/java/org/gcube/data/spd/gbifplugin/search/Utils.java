package org.gcube.data.spd.gbifplugin.search;

import static org.gcube.data.spd.gbifplugin.search.query.QueryCondition.cond;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.data.spd.gbifplugin.search.query.QueryCondition;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.products.DataProvider;
import org.gcube.data.spd.model.products.DataSet;

public class Utils {

	protected static List<QueryCondition> elaborateConditions(Condition[] properties) throws Exception{
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		for (Condition prop: properties){
			switch (prop.getType()) {
			case COORDINATE:
				Coordinate coord = (Coordinate)prop.getValue();
				queryConditions.addAll(getCoordinateConditions(coord, prop));
				break;
			case DATE:
				Calendar date = (Calendar) prop.getValue();
				queryConditions.addAll(getDateCondition(date, prop));
				break;		
			}
		}
		return queryConditions;
	}

	public static ProductKey elaborateProductsKey(String id) {
		List<QueryCondition> queryConditions = new ArrayList<QueryCondition>();
		String[] splitString = id.split("\\|\\|");
		
		DataSet dataset = getDataSetFromString(splitString[0]);
		queryConditions.add(cond("datasetKey", dataset.getId()));
		queryConditions.add(cond("taxonKey", splitString[1]));
		if (splitString.length>2)
			for (int i = 2; i<=splitString.length; i++){
				String[] equalSplit = splitString[i].split("=");
				queryConditions.add(cond(equalSplit[0], equalSplit[1]));
			}
		return new ProductKey(queryConditions, dataset);
	}

	protected static String createProductsKey(String dataResourceKey, String taxonKey, List<QueryCondition> queryConditions) {
		StringBuilder conditionTransformer = new StringBuilder();
		for (QueryCondition cond : queryConditions)
			conditionTransformer.append("||").append(cond.getKey()).append("=").append(cond.getValue());
		return dataResourceKey+"||"+taxonKey+conditionTransformer.toString();
	}
	
	public static List<QueryCondition> getCoordinateConditions(Coordinate coordinate, Condition prop){
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();  
		switch (prop.getOp()) {
		case EQ:
			conditions.add(cond("decimalLatitiude",coordinate.getLatitude()+""));
			conditions.add(cond("decimalLongitude",coordinate.getLongitude()+""));
			break;
		case GT:
			conditions.add(cond("decimalLatitiude",(coordinate.getLatitude()+0.01)+",90"));
			conditions.add(cond("decimalLongitude",(coordinate.getLongitude()+0.01)+",180"));
			break;
		case GE:
			conditions.add(cond("decimalLatitiude",coordinate.getLatitude()+",90"));
			conditions.add(cond("decimalLongitude",coordinate.getLongitude()+",180"));
			break;
		case LT:
			conditions.add(cond("decimalLatitiude","-90,"+(coordinate.getLatitude()+0.01)));
			conditions.add(cond("decimalLongitude","-180,"+(coordinate.getLongitude()+0.01)));
			break;
		case LE:
			conditions.add(cond("decimalLatitiude","-90,"+coordinate.getLatitude()));
			conditions.add(cond("decimalLongitude","-180,"+coordinate.getLongitude()));
			break;
		default:
			break;
		}
		return conditions;
	}

	public static List<QueryCondition> getDateCondition(Calendar date, Condition prop){
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();  
		DateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd");
		Calendar newDate = date;
		Calendar now = Calendar.getInstance();
		switch (prop.getOp()) {
		case EQ:
			conditions.add(cond("eventDate",dateFormat.format(date.getTime())));
			break;
		case GT:
			newDate.add(Calendar.DAY_OF_MONTH, -1);
			conditions.add(cond("eventDate",dateFormat.format(date.getTime())+","+dateFormat.format(now.getTime())));
			break;
		case GE:
			conditions.add(cond("eventDate",dateFormat.format(date.getTime())+","+dateFormat.format(now.getTime())));
			break;
		case LT:
			newDate.add(Calendar.DAY_OF_MONTH, 1);
			conditions.add(cond("eventDate","1000-01-01,"+dateFormat.format(now.getTime())));
			break;
		case LE:
			conditions.add(cond("eventDate","1000-01-01,"+dateFormat.format(now.getTime())));
			break;
		default:
			break;
		}
		return conditions;
	}
	
	protected static String getDataSetAsString(DataSet dataset){
		StringBuilder datasetAsString = new StringBuilder(
				dataset.getId())
				.append("^^").append(dataset.getName())
				.append("^^").append(dataset.getCitation())
				.append("^^").append(dataset.getDataProvider().getId())
				.append("^^").append(dataset.getDataProvider().getName());
		return datasetAsString.toString();
	}
	
	protected static DataSet getDataSetFromString(String datasetString){
		String[] splittedDataset = datasetString.split("\\^\\^");
		DataSet dataset = new DataSet(splittedDataset[0]);
		dataset.setName(splittedDataset[1]);
		dataset.setCitation(splittedDataset[2]);
		DataProvider dataProvider = new DataProvider(splittedDataset[3]);
		dataProvider.setName(splittedDataset[4]);
		dataset.setDataProvider(dataProvider);
		return dataset;
	}
}
