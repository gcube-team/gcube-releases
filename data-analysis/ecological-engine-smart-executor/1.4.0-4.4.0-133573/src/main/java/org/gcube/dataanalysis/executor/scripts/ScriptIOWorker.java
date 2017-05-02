package org.gcube.dataanalysis.executor.scripts;

public class ScriptIOWorker {
	
	
		public static String getString(String input){
			if (input!=null)
				return input.replace("##", ".");
			else 
				return input;
		}
		
		public static String toInputString(String output){
			if (output!=null)
				return output.replace(".", "##");
			else 
				return output;
		}
}
