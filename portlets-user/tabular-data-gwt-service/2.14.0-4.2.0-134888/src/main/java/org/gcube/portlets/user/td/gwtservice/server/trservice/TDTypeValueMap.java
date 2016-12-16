package org.gcube.portlets.user.td.gwtservice.server.trservice;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDGeometry;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTServiceException;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TDTypeValueMap {
	protected static Logger logger = LoggerFactory
			.getLogger(TDTypeValueMap.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static TDTypeValue map(String dataTypeName, String value)
			throws TDGWTServiceException {
		TDTypeValue tdTypeValue = null;
		try {
			switch (dataTypeName) {
			case "Boolean":
				Boolean b = new Boolean(value);
				tdTypeValue = new TDBoolean(b);
				break;
			case "Date":
				Date d;
				try{
					d=sdf.parse(value);
				}catch(Exception e){
					throw new TDGWTServiceException(e.getLocalizedMessage());
				}
				tdTypeValue=new TDDate(d);
				break;
			case "Geometry":
				if(TDGeometry.validateGeometry(value)){
					tdTypeValue = new TDGeometry(value);
				}  else {
					throw new TDGWTServiceException("The value "+ value+ " is not a valid geometry");
				}	
				break;
			case "Integer":
				Integer in = new Integer(value);
				tdTypeValue = new TDInteger(in);
				break;
			case "Numeric":
				Double db = new Double(value);
				tdTypeValue = new TDNumeric(db);
				break;
			case "Text":
				if(value==null){
					value=new String();
				}
				tdTypeValue = new TDText(value);
				break;
			default:
				break;
			}

		} catch (Throwable e) {
			logger.debug("Error in TDTypeValueMap: "+e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error in TDTypeValueMap: "
					+ e.getLocalizedMessage());
		}
		return tdTypeValue;
	}
	
	
	public static TDTypeValue map(ColumnDataType dataType, String value)
			throws TDGWTServiceException {
		TDTypeValue tdTypeValue = null;
		try {
			switch (dataType) {
			case Boolean:
				Boolean b = new Boolean(value);
				tdTypeValue = new TDBoolean(b);
				break;
			case Date:
				Date d;
				try{
					d=sdf.parse(value);
				}catch(Exception e){
					throw new TDGWTServiceException(e.getLocalizedMessage());
				}
				tdTypeValue=new TDDate(d);
				break;
			case Geometry:
				if(TDGeometry.validateGeometry(value)){
					tdTypeValue = new TDGeometry(value);
				}  else {
					throw new TDGWTServiceException("The value "+ value+ " is not a valid geometry");
				}	
				break;
			case Integer:
				Integer in = new Integer(value);
				tdTypeValue = new TDInteger(in);
				break;
			case Numeric:
				Double db = new Double(value);
				tdTypeValue = new TDNumeric(db);
				break;
			case Text:
				if(value==null){
					value="";
				}
				tdTypeValue = new TDText(value);
				break;
			default:
				break;
			}

		} catch (Throwable e) {
			logger.debug("Error in TDTypeValueMap: "+e.getLocalizedMessage());
			e.printStackTrace();
			throw new TDGWTServiceException("Error in TDTypeValueMap: "
					+ e.getLocalizedMessage());
		}
		return tdTypeValue;
	}
	
}
