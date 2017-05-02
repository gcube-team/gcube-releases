package org.gcube.rest.commons.helpers;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


public class JSHelper {

	static ScriptEngineManager mgr = new ScriptEngineManager();
	static ScriptEngine engine = mgr.getEngineByName("JavaScript");
	
	public static Boolean evaluate(String eval) throws ScriptException {
		Boolean result = (Boolean) engine.eval(eval);

		return result;
	}
}
