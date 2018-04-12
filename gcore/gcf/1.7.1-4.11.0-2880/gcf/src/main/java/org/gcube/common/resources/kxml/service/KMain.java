package org.gcube.common.resources.kxml.service;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.service.MainPackage;
import org.gcube.common.core.resources.service.PortType;
import org.gcube.common.core.resources.service.PortType.Function;
import org.gcube.common.resources.kxml.utils.KAny;
import org.gcube.common.resources.kxml.utils.KStringList;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * @author Andrea Manzi (ISTI-CNR)
 */
public class KMain {
	
	public static MainPackage load(KXmlParser parser) throws Exception {
		MainPackage main = new MainPackage();
		if (parser.getAttributeValue(NS, "deployable")!=null) main.setDeployable(Boolean.valueOf(parser.getAttributeValue(NS, "deployable")));
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					KPackage.load(main, parser);
					if (parser.getName().equals("GARArchive")) main.setGarArchive(parser.nextText().trim());
					if (parser.getName().equals("ServiceEquivalenceFunctions")) main.getServiceEquivalenceFunctions().add(KFunction.load(parser)) ;
					if (parser.getName().equals("PortType")) main.getPorttypes().add(KPortType.load(parser)) ;
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Main")) break loop;
					break;
				case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Main");
			}
		}
		return main;
	}
	public static void store(MainPackage component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
			serializer.startTag(NS,"Main");
			if (component.isDeployable()) serializer.attribute(NS, "deployable", component.isDeployable()+"");
			KPackage.store(component,serializer);
			if (component.getGarArchive()!=null) serializer.startTag(NS,"GARArchive").text(component.getGarArchive().trim()).endTag(NS, "GARArchive");
			if (component.getServiceEquivalenceFunctions().size()!=0){ serializer.startTag(NS,"ServiceEquivalenceFunctions"); for (Function fun : component.getServiceEquivalenceFunctions()) KFunction.store(fun, serializer);serializer.endTag(NS, "ServiceEquivalenceFunctions");}
			if (component.getPorttypes().size()!=0) for (PortType entry : component.getPorttypes()) KPortType.store(entry, serializer);
		serializer.endTag(NS,"Main");
	}
	
	
	public static class KFunction {

		public static Function load(KXmlParser parser) throws Exception {
			Function fun = new Function();
			loop: while (true) {
				int tokenType = parser.next();
				switch (tokenType){			
					case KXmlParser.START_TAG : 
						String tag = parser.getName(); //remember position and name of tag
						if (tag.equals("Name")) fun.setName(parser.nextText());
						if (tag.equals("FormalParameters")) fun.setFormalParameters(KStringList.load("FormalParameters",parser));
						if (tag.equals("Body")) fun.setBody(KAny.load("Body",parser));
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Function")) break loop;
						break;
					case KXmlParser.END_DOCUMENT : throw new Exception("Parsing failed at Function");
				
				}
			}
			return fun; 
		}
		
		public static void store(Function component, KXmlSerializer serializer) throws Exception {
			serializer.startTag(NS, "Function");
			serializer.startTag(NS, "Name").text(component.getName()).endTag(NS, "Name");
			if (component.getFormalParameters().size()!=0) KStringList.store("FormalParameters","Name", component.getFormalParameters(), serializer);
			KAny.store("Body",component.getBody(), serializer);
			serializer.endTag(NS, "Function");
		}
	}

	
}
