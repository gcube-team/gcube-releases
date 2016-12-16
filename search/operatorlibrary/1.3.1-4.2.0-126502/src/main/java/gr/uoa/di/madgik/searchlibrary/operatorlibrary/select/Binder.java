package gr.uoa.di.madgik.searchlibrary.operatorlibrary.select;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Given some bindings as an input, replaces all references within a logical
 * expression string that match a particular pattern.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class Binder {
	/**
	 * The logical expression
	 */
	private String expr;
	
	/**
	 * References that match the given pattern
	 */
	private Set<String> refs;
	
	private Matcher m;

	/**
	 * Default pattern to be used of the form [(\d+)] (e.g. [0])
	 */
	private static final String DEFAULTPATTERN = "\\[(\\d+)\\]";
	
	/**
	 * Constructor
	 * @param expr The expression to be evaluated.
	 */
	public Binder(String expr) {
		this(expr, DEFAULTPATTERN);
	}

	/**
	 * Constructor
	 * @param expr The expression to be evaluated.
	 * @param pattern The pattern to be matched (e.g. [(\d+)])
	 */
	public Binder(String expr, String pattern) {
		this.expr = expr;

		// Matches pattern that is not surrounded between quotes
		m = Pattern.compile(pattern + "(?=([^\"']*[\"'][^\"']*[\"'])*[^\"']*$)").matcher(expr);

		refs = new HashSet<String>();
		while (m.find()) {
			refs.add(m.group(1));
		}
	}

	/**
	 * @return A set of references that must be substituted.
	 */
	public Set<String> getSubstitutions() {
		return refs;
	}

	/**
	 * @param bindings The bindings between the references and the actual values that will take their place.
	 * @return The string after substitutions.
	 */
	public String substitute(Map<String, String> bindings) {
		m.reset(expr);

		String fin = expr;
		while (m.find()) {
			String ref = m.group(1);

			String repl = bindings.get(ref);
			if (!repl.matches("\\d+"))
				repl = '"' + repl + '"';
			
			fin = m.replaceFirst(repl);

			m.reset(fin);
		}
		return fin;
	}

//	public static void main(String[] args) throws IOException {
//		String expr = "[0] == '[666]' && [2] > 0 || [5] != \"[666]\"";
//		Binder eval = new Binder(expr);
//
//		System.out.println(expr);
//		System.out.println(eval.getSubstitutions());
//
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("0", "666");
//		map.put("2", "1");
//		map.put("5", "xa");
//
//		System.out.println(map);
//
//		String logical = eval.substitute(map);
//		System.out.println(logical);
//		
//		try {
//			System.out.println(Evaluator.jsEvaluator(logical));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
