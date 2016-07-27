/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine.exl;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * {@link MapperWrapper} that generare short class names and in lower case.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public class HavingMapper extends MapperWrapper {

	public HavingMapper(Mapper wrapped) {
		super(wrapped);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public String serializedClass(Class type) {
		return type.getSimpleName().toLowerCase();
	}

	

}
