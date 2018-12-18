package org.gcube.data.access.storagehub;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConstraintChecker {

	public boolean isValidName(String name){
		//^ < > ? $  / \ ' "
		Pattern p = Pattern.compile("[^a-z0-9 _/-/?/$<>']", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(name);
		boolean b = m.find();
		return !b;
	}
	
}
