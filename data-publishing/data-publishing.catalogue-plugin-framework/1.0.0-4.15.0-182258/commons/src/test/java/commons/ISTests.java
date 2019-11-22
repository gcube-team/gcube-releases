package commons;

import java.util.Map.Entry;

import org.gcube.data.publishing.gCatFeeder.utils.ISUtils;

public class ISTests {

	public static void main(String[] args) {
		TokenSetter.set("/gcube/devNext/NextNext");
		for(Entry<String,String> entry:ISUtils.loadConfiguration().entrySet())
			System.out.println("Param : [key : "+entry.getKey()+" , value : "+entry.getValue()+"]");
		
	}
	
	
}
