package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi"
 *
 */
public class ValueMap {
	private static Logger logger = LoggerFactory
			.getLogger(ValueMap.class);

	public ValueMap(){
		
	}
	
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public ArrayList<Map<String,Object>> genValueMap(TRId trId, ArrayList<ColumnData> columns, HashMap<String,String> fieldsMap)
			throws TDGWTServiceException {
		ArrayList<Map<String,Object>> composit=new ArrayList<Map<String,Object>>();
		
		if(fieldsMap==null){
			return composit;
		}
		
		TableId tableId;
		if(trId.isViewTable()){
			tableId=new TableId(new Long(trId.getReferenceTargetTableId()));
		} else {
			tableId=new TableId(new Long(trId.getTableId()));
		}
		Map<String,String> parametersValue=fieldsMap;
		Set<String> keys=parametersValue.keySet();
		Iterator<String> iterator=keys.iterator();
		String key;
		while(iterator.hasNext()){
			key=iterator.next();
			String value=parametersValue.get(key);
			for(ColumnData col:columns){
				if(col.getColumnId().compareTo(key)==0){
					if (col.getTypeCode().compareTo(
							ColumnTypeCode.DIMENSION.toString()) == 0
							|| col.getTypeCode().compareTo(
									ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
						if(value==null){
							logger.error("Error, null value for on dimension or timedimension column is not valid");
							continue;
						}
						TDInteger tdDim=new TDInteger(new Integer(value));
						ColumnLocalId columnId=new ColumnLocalId(key);
						ColumnReference colRef=new ColumnReference(tableId, columnId);
						Map<String,Object> valueMap=new HashMap<String,Object>();
						valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_FIELD,colRef);
						valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_TOSETVALUE,tdDim);
						composit.add(valueMap);
						
					} else {
						if (col.getDataTypeName().compareTo(
								ColumnDataType.Boolean.toString()) == 0) {
							if(value==null){
								continue;
							}
							TDBoolean tdBoolean=new TDBoolean(new Boolean(value));
							ColumnLocalId columnId=new ColumnLocalId(key);
							ColumnReference colRef=new ColumnReference(tableId, columnId);
							Map<String,Object> valueMap=new HashMap<String,Object>();
							valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_FIELD,colRef);
							valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_TOSETVALUE,tdBoolean);
							composit.add(valueMap);
						} else {
							if (col.getDataTypeName().compareTo(
									ColumnDataType.Date.toString()) == 0) {
								Date d;
								try{
									d=sdf.parse(value);
								}catch(Exception e){
									continue;
								}
								TDDate tdDate=new TDDate(d);
								ColumnLocalId columnId=new ColumnLocalId(key);
								ColumnReference colRef=new ColumnReference(tableId, columnId);
								Map<String,Object> valueMap=new HashMap<String,Object>();
								valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_FIELD,colRef);
								valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_TOSETVALUE,tdDate);
								composit.add(valueMap);
							} else {
								if (col.getDataTypeName().compareTo(
										ColumnDataType.Text.toString()) == 0) {
									if(value==null){
										continue;
									}
									TDText tdText=new TDText(value);
									
									ColumnLocalId columnId=new ColumnLocalId(key);
									ColumnReference colRef=new ColumnReference(tableId, columnId);
									Map<String,Object> valueMap=new HashMap<String,Object>();
									valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_FIELD,colRef);
									valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_TOSETVALUE,tdText);
									composit.add(valueMap);
								} else {
									if (col.getDataTypeName().compareTo(
											ColumnDataType.Geometry.toString()) == 0) {
										TDGeometry tdGeometry;
										if(TDGeometry.validateGeometry(value)){
											tdGeometry=new TDGeometry(value);
										}  else {
											continue;
											
										}	
										ColumnLocalId columnId=new ColumnLocalId(key);
										ColumnReference colRef=new ColumnReference(tableId, columnId);
										Map<String,Object> valueMap=new HashMap<String,Object>();
										valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_FIELD,colRef);
										valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_TOSETVALUE,tdGeometry);
										composit.add(valueMap);
									} else {
										if (col.getDataTypeName().compareTo(
												ColumnDataType.Integer.toString()) == 0) {
											if(value==null){
												continue;
											}
											Integer integ;
											try{
											integ=new Integer(value);
											} catch(NumberFormatException e){
												continue;
											}
											TDInteger tdInteger=new TDInteger(integ);
											ColumnLocalId columnId=new ColumnLocalId(key);
											ColumnReference colRef=new ColumnReference(tableId, columnId);
											Map<String,Object> valueMap=new HashMap<String,Object>();
											valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_FIELD,colRef);
											valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_TOSETVALUE,tdInteger);
											composit.add(valueMap);
										} else {
											if (col.getDataTypeName().compareTo(
													ColumnDataType.Numeric.toString()) == 0) {
												if(value==null){
													continue;
												}
												Double numeric;
												try{
												numeric=new Double(value);
												} catch(NumberFormatException e){
													continue;
												}
												TDNumeric tdNumeric=new TDNumeric(numeric);
												ColumnLocalId columnId=new ColumnLocalId(key);
												ColumnReference colRef=new ColumnReference(tableId, columnId);
												Map<String,Object> valueMap=new HashMap<String,Object>();
												valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_FIELD,colRef);
												valueMap.put(Constants.PARAMETER_ADD_ROW_COMPOSITE_TOSETVALUE,tdNumeric);
												composit.add(valueMap);
											} else {
												
											}
										}
									}
								}
							}
						}
					
					}
					break;
				}
			}
		}
		
		return composit;
	}
	
	
	
	
	
	
}
