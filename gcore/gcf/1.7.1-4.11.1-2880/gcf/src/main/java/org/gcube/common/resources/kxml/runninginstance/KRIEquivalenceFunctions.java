package org.gcube.common.resources.kxml.runninginstance;


import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import org.gcube.common.core.resources.runninginstance.RIEquivalenceFunction;
import org.gcube.common.core.resources.runninginstance.RIEquivalenceFunction.Function;
import org.gcube.common.core.resources.runninginstance.RIEquivalenceFunction.Function.ActualParameters;
import org.gcube.common.core.resources.runninginstance.RIEquivalenceFunction.Function.ActualParameters.Param;
import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * 
 * @author  Andrea Manzi (CNR)
 *
 */
public class KRIEquivalenceFunctions {
	public static RIEquivalenceFunction load(KXmlParser parser) throws Exception {
		
		RIEquivalenceFunction d = new RIEquivalenceFunction();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("Function")) d.getFunctions().add(KFunction.load(parser));
		
				break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("RIEquivalenceFunctions")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at RIEquivalenceFunctions");
			}
		}
		return d; 
		}
	public static void store(RIEquivalenceFunction component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		for (Function function : component.getFunctions()){
			KFunction.store(function,serializer);
		}
		
		
	}
	
	public static class KFunction {
		public static Function load(KXmlParser parser) throws Exception {
			Function d = new Function();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG :
						if (parser.getName().equals("Name")) d.setName(parser.nextText());
						if (parser.getName().equals("ActualParameters")) d.setActualParameters(KActualParameter.load(parser));
						
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Function")){
							break loop;
						}
						break;
					case KXmlParser.END_DOCUMENT :
						throw new Exception("Parsing failed at Function");
				}
			}
			return d; 
			}
		
		public static void store(Function component, KXmlSerializer serializer) throws Exception {
			if (component== null) return ;
			serializer.startTag(NS,"Function");
			if (component.getName()!= null) serializer.startTag(NS, "Name").text(component.getName()).endTag(NS, "Name");
			if (component.getActualParameters()!= null) KActualParameter.store(component.getActualParameters(),serializer);
			serializer.endTag(NS,"Function");
		}
	}
	
	public static class KActualParameter {
		public static ActualParameters load(KXmlParser parser) throws Exception {
			ActualParameters d = new ActualParameters();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG :
						if (parser.getName().equals("Param")) d.getParam().add(KParam.load(parser));
						
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("ActualParameters")){
							break loop;
						}
						break;
					case KXmlParser.END_DOCUMENT :
						throw new Exception("Parsing failed at Function");
				}
			}
			return d; 
			}
		
		public static void store(ActualParameters component, KXmlSerializer serializer) throws Exception {
			if (component== null) return ;
			serializer.startTag(NS,"ActualParameters");
			for (Param param : component.getParam()) 
				KParam.store(param,serializer);
			serializer.endTag(NS,"ActualParameters");
		}
	}
	
	public static class KParam {
		public static Param load(KXmlParser parser) throws Exception {
			Param d = new Param();
			loop: while (true) {
				switch (parser.next()){			
					case KXmlParser.START_TAG :
						if (parser.getName().equals("Name")) d.setName(parser.nextText());
						if (parser.getName().equals("Value")) d.getValue().add(parser.nextText());
					
					break;
					case KXmlParser.END_TAG:
						if (parser.getName().equals("Param")){
							break loop;
						}
						break;
					case KXmlParser.END_DOCUMENT :
						throw new Exception("Parsing failed at Param");
				}
			}
			return d; 
			}
		
		public static void store(Param component, KXmlSerializer serializer) throws Exception {
			if (component== null) return ;
			serializer.startTag(NS,"Param");
				if (component.getName()!= null) serializer.startTag(NS, "Name").text(component.getName()).endTag(NS, "Name");
				for( String value :component.getValue())
					serializer.startTag(NS, "Value").text(value).endTag(NS, "Value");
			serializer.endTag(NS,"Param");
		}
	}
	
}