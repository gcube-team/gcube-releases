package gr.cite.geoanalytics.functions.output.object;

import java.util.ArrayList;
import java.util.List;

public class Helper {
	
	
	
	
	public static void main(String [] args){
		
		List<ExtradataField> l = new ArrayList<ExtradataField>();
		l.add(new ExtradataField("aaa", new Double(5.332)));
		l.add(new ExtradataField("bbb", new Integer(8)));
		l.add(new ExtradataField("ccc", new Boolean(true)));
		
		String xml = formExtradataField(l.toArray(new ExtradataField[l.size()]));
		System.out.println(xml);
		
	}
	
	public static String formExtradataField(ExtradataField ... fields){
		StringBuilder sb = new StringBuilder();
		sb.append("<extraData>");
		for(ExtradataField field : fields)
			sb.append(field);
		sb.append("</extraData>");
		return sb.toString();
	}
	

	
	
}
