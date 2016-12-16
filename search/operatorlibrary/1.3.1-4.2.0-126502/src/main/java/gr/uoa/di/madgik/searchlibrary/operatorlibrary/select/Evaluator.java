package gr.uoa.di.madgik.searchlibrary.operatorlibrary.select;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


/**
 * Evaluator for java script engine
 * 
 * @author john.gerbesiotis - DI NKUA
 *
 */
public class Evaluator {
	
	static boolean jsEvaluator(String expr) throws Exception {
		boolean result = false;
		try {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("JavaScript");

			result = (Boolean) engine.eval(expr);
		} catch (ScriptException e) {
			throw new Exception("Could not parse expression: " + expr, e);
		}
		return result;
	}
}
