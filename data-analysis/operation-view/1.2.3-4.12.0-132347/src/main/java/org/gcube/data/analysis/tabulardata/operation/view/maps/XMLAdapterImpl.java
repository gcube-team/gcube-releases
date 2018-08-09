package org.gcube.data.analysis.tabulardata.operation.view.maps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.spatial.data.geonetwork.configuration.XMLAdapter;

public class XMLAdapterImpl implements XMLAdapter{

	private static final String NODE_NAME="gmd:MD_TopicCategoryCode";
	
	@Override
	public String adaptXML(String arg0) {	
		String startElementRegexp="(?s)<"+NODE_NAME+"[^>]*>";		
//		Pattern pattern = Pattern.compile(startElementRegexp);
//		Matcher matcher=pattern.matcher(arg0);
		String[] splitted=arg0.split(startElementRegexp);
		if(splitted.length>1){
			String value=splitted[1];
			value=value.substring(0, value.indexOf("<"));
			return arg0.replaceAll("(?s)<"+NODE_NAME+"[^>]*>.*?</"+NODE_NAME+">",
					String.format("<%1$s>%2$s</%1$s>", NODE_NAME,value));
		}else return arg0;
	}
	
}
