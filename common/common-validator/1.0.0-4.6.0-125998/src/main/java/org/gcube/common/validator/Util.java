package org.gcube.common.validator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

	public static List<Field> getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) 
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        return fields;
    }
	
}
