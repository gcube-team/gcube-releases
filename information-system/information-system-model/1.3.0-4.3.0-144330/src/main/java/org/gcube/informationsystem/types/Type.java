package org.gcube.informationsystem.types;
/*
 *
 *  *  Copyright 2014 Orient Technologies LTD (info(at)orientechnologies.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientechnologies.com
 *
 */

import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.gcube.informationsystem.model.embedded.Embedded;


/**
 * @author Lucio Lelii (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 * Create a mapping with OrientDB Types 
 * https://orientdb.gitbooks.io/orientdb-manual/content/orientdb.wiki/Types.html
 * and gCube IS Types
 * https://wiki.gcube-system.org/gcube/Facet_Based_Resource_Model#Property
 * The code is copied from OrientDB source Code 
 * https://github.com/orientechnologies/orientdb/blob/master/core/src/main/java/com/orientechnologies/orient/core/metadata/schema/OType.java 
 * and adapted for gCube purpose.  
 */
public  class Type{

	/**
	 * Generic representation of a type.<br>
	 * allowAssignmentFrom accepts any class, but Array.class means that the type accepts generic Arrays.
	 * 
	 * @author Luca Garulli
	 * 
	 */
	public enum OType {
		BOOLEAN("Boolean", 0),

		INTEGER("Integer", 1),

		SHORT("Short", 2),

		LONG("Long", 3),

		FLOAT("Float", 4),

		DOUBLE("Double", 5),

		DATETIME("Datetime", 6),

		STRING("String", 7),
		
		BYNARY("Bynary", 8),
		
		EMBEDDED("Embedded", 9),
		
		EMBEDDEDLIST("Embedded List", 10),
		
		EMBEDDEDSET("Embedded Set", 11),

		EMBEDDEDMAP("Embedded Map", 12),
		
		BYTE("Byte", 17),
		
		BINARY("Binary", 8);

		private String stringValue;
		private int intValue;

		OType(String stringValue,  int intValue){
			this.stringValue = stringValue;
			this.intValue = intValue;
		}

		protected String getStringValue() {
			return stringValue;
		}

		protected int getIntValue() {
			return intValue;
		}
				
	}

	protected static final Map<Class<?>, OType> TYPES_BY_CLASS = new HashMap<Class<?>, OType>();

	static{

		// This is made by hand because not all types should be add.
		TYPES_BY_CLASS.put(Boolean.TYPE, OType.BOOLEAN);
		TYPES_BY_CLASS.put(Boolean.class, OType.BOOLEAN);
		
		TYPES_BY_CLASS.put(Integer.TYPE, OType.INTEGER);
		TYPES_BY_CLASS.put(Integer.class, OType.INTEGER);
		TYPES_BY_CLASS.put(BigInteger.class, OType.INTEGER);
		
		TYPES_BY_CLASS.put(Short.TYPE, OType.SHORT);
		TYPES_BY_CLASS.put(Short.class, OType.SHORT);
		
		TYPES_BY_CLASS.put(Long.TYPE, OType.LONG);
		TYPES_BY_CLASS.put(Long.class, OType.LONG);
		
		TYPES_BY_CLASS.put(Float.TYPE, OType.FLOAT);
		TYPES_BY_CLASS.put(Float.class, OType.FLOAT);
		
		TYPES_BY_CLASS.put(Double.TYPE, OType.DOUBLE);
		TYPES_BY_CLASS.put(Double.class, OType.DOUBLE);
		
		TYPES_BY_CLASS.put(Date.class, OType.DATETIME);
		TYPES_BY_CLASS.put(Calendar.class, OType.DATETIME);
		
		TYPES_BY_CLASS.put(String.class, OType.STRING);
		TYPES_BY_CLASS.put(Character.class, OType.STRING);
		TYPES_BY_CLASS.put(Character.TYPE, OType.STRING);
		
		TYPES_BY_CLASS.put(Embedded.class, OType.EMBEDDED);
		
		TYPES_BY_CLASS.put(List.class, OType.EMBEDDEDLIST);
		
		TYPES_BY_CLASS.put(Set.class, OType.EMBEDDEDSET);
		
		TYPES_BY_CLASS.put(Map.class, OType.EMBEDDEDMAP);
		
		TYPES_BY_CLASS.put(Byte.TYPE, OType.BYTE);
		TYPES_BY_CLASS.put(Byte.class, OType.BYTE);
		
		TYPES_BY_CLASS.put(byte[].class, OType.BYNARY);
		TYPES_BY_CLASS.put(Byte[].class, OType.BYNARY);
		
		
		TYPES_BY_CLASS.put(Enum.class, OType.STRING);
		TYPES_BY_CLASS.put(URI.class, OType.STRING);
		TYPES_BY_CLASS.put(URL.class, OType.STRING);
		TYPES_BY_CLASS.put(UUID.class, OType.STRING);
		
	}

	/**
	 * Return the correspondent type by checking the "assignability" of the 
	 * class received as parameter.
	 * 
	 * @param iClass Class to check
	 * @return OType instance if found, otherwise null
	 */
	public static OType getTypeByClass(final Class<?> iClass) {
		if (iClass == null) {
			return null;
		}
		
	    OType type = TYPES_BY_CLASS.get(iClass);
	    if (type != null) {
	      return type;
	    }
	    
	    
	    if(Enum.class.isAssignableFrom(iClass)){
	    	type = TYPES_BY_CLASS.get(Enum.class);
	    }
		
	    return type;
	  }

	  
}

