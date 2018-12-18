package org.gcube.common.dbinterface.types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Cast {
	
	private static final Logger logger = LoggerFactory.getLogger(Cast.class);
	
	@SuppressWarnings("rawtypes")
	public static Object apply(Class javaClass, Type destination, Object value) throws Exception{
		if (!value.getClass().isAssignableFrom(javaClass)) throw new Exception("value and origin Type are not compatible");
		Type origin=Type.getTypeByJavaClass(javaClass);
		Object toReturn=null;
		try{
			switch (destination.getType()) {
			case INTEGER:
				toReturn= origin.toInteger(value);
				break;
			case STRING: case TEXT:
				toReturn= origin.toString(value);
				break;
			case DATE:
				toReturn= origin.toDate(value, destination.getFormat());
				break;
			case TIME:
				toReturn= origin.toTime(value, destination.getFormat());
				break;
			/*case FLOAT:
				toReturn= origin.toFloat(value);
				break;*/
			case FLOAT:
				toReturn= origin.toReal(value);
				break;
			case LONG:
				toReturn= origin.toLong(value);
				break;
			case TIMESTAMP:
				toReturn= origin.toTimestamp(value);
				break;
			case BOOLEAN:
				toReturn= origin.toBoolean(value);
				break;
			default:
				break;
			}
		}catch (Exception e) {
			logger.warn("error casting value",e);
			return null;
		}
		return toReturn;
	}

	
}
