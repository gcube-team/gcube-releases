package org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Envelope;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.EnvelopeFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.HspenFields;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class EnvelopeConverter implements Converter{

	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext arg2) {
		Envelope env=(Envelope) arg0;
		writer.startNode("Envelope");
		writer.addAttribute(HspenFields.faoareas+"", env.getFaoAreas());
		writer.addAttribute(HspenFields.pelagic+"", env.isPelagic()+"");
		writer.addAttribute(HspenFields.layer+"", env.isUseBottomSeaTempAndSalinity()+"");
		writer.startNode("BoundingBox");
		writer.setValue(env.getBoundingBox().toString());
		writer.endNode();
		for(EnvelopeFields f : EnvelopeFields.values()){
			writer.startNode(f+"");
			for(HspenFields paramName : env.getValueNames(f))
				writer.addAttribute(paramName+"", env.getValue(f, paramName)+"");
			writer.endNode();
		}
	}

	public Object unmarshal(HierarchicalStreamReader arg0,
			UnmarshallingContext arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean canConvert(Class arg0) {
		return arg0.equals(Envelope.class);
	}

}
