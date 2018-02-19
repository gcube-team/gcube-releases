package org.gcube.dataanalysis.ecoengine.utils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class LMEEnum extends DynamicEnum {

	public enum LMEEnumType {

	};

	public Field[] getFields() {
		Field[] fields = LMEEnumType.class.getDeclaredFields();
		return fields;
	}

	public static void main(String[] args) {
		LMEEnum en = new LMEEnum();
		
		en.addEnum(LMEEnumType.class, "CIAO");
		en.addEnum(LMEEnumType.class, "TEST");
		en.addEnum(LMEEnumType.class, "MIAO *_$");

		System.out.println(Arrays.deepToString(LMEEnumType.values()));
/*
		DynamicEnum den = new DynamicEnum();
		
		den.addEnum(DynamicEnum.DynamicEnumE.class, "y");
		den.addEnum(DynamicEnum.DynamicEnumE.class, "r");
		den.addEnum(DynamicEnum.DynamicEnumE.class, "t");

		System.out.println(Arrays.deepToString(DynamicEnum.DynamicEnumE.values()));
*/
		
	}

}
