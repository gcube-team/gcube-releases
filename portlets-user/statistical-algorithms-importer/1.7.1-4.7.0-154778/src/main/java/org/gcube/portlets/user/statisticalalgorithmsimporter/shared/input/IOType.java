package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public enum IOType {
	INPUT("Input"),
	OUTPUT("Output");
	
	
	//private static IOTypeMessages msgs=GWT.create(IOTypeMessages.class);
	private String id;
	
	//private static List<String> inputTypeI18NList;

	/*static {
		ioTypeI18NList = new ArrayList<String>();
		for (IOType iotype : values()) {
			ioI18NList.add(msgs.ioType(iotype));
		}
	}*/

	private IOType(String id) {
		this.id = id;	
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return id;
	}
	
	public String getLabel(){
		//return msgs.ioType(this);
		return id;
	}
	
	
	public static List<IOType> asList() {
		List<IOType> list = Arrays.asList(values());
		return list;
	}

	/*public static List<String> asI18NList() {
		return ioTypeI18NList;

	}*/
	
}
