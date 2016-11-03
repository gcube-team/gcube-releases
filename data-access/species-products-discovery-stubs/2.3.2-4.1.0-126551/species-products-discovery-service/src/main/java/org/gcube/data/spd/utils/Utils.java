package org.gcube.data.spd.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.gcube.common.core.faults.ExceptionProxy;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.model.Condition;


public class Utils {

	public static <E extends GCUBEFault> E newFault(E fault, Throwable cause) {
		
		fault.setFaultMessage(cause.getMessage());
		
		//pretty pointless output
		fault.removeFaultDetail(new QName("http://xml.apache.org/axis/","stackTrace"));
		
		//adds whole stacktrace as single detail element
		StringWriter w = new StringWriter();
		cause.printStackTrace(new PrintWriter(w));
		fault.addFaultDetailString(w.toString());
		
		try {
			fault.addFaultDetail(ExceptionProxy.newInstance(cause).toElement());
		}
		catch(Exception e) {}
		
		return fault;
		
	}
		
	public static String getPropsAsString(Condition[] conditions){
		StringBuilder props =new StringBuilder(); 
		Arrays.sort(conditions);
		for (Condition cond: conditions){
			switch (cond.getType()) {
			case COORDINATE:
					Coordinate coord = (Coordinate)cond.getValue();
					props.append("lat="+coord.getLatitude());
					props.append("long="+coord.getLongitude());
					props.append("op="+cond.getOp().name());
				break;
			case DATE:
				Calendar cal = (Calendar)cond.getValue();
				props.append("date="+cal.getTimeInMillis());
				props.append("op="+cond.getOp().name());
				break;	
			default:
				break;
			}
		}
		return props.toString();
	}
	
	public static File createErrorFile(Iterator<String> errors) throws Exception{
		int entries =0;
		File file = File.createTempFile("errors", "txt");
		FileWriter writer= new FileWriter(file);
		while(errors.hasNext()){
			writer.write(errors.next()+"\n");
			entries++;
		}
		writer.close();
		if (entries==0){
			file.delete();
			return null;
		}else return file;
	}
}
