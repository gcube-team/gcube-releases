package org.gcube.data.spd.model.binding;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultItem;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Bindings {

	private static final Logger logger = LoggerFactory.getLogger(Bindings.class);
	
	private  static JAXBContext context ;
	
	static{
		try {
			context = JAXBContext.newInstance(OccurrencePoint.class, TaxonomyItem.class, ResultItem.class, PointInfo.class);
		} catch (JAXBException e) {
			logger.error("error preparing the binding ",e);
		}
	}
		
	public static <T> String toXml(T obj) throws JAXBException{
		StringWriter sw = new StringWriter();
		if (obj==null)
			throw new JAXBException("null value passed");
		context.createMarshaller().marshal(obj, sw);
		return sw.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String payload) throws JAXBException {
		return (T) context.createUnmarshaller().unmarshal(new StringReader(payload));
	}
	
}
