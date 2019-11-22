package org.gcube.common.resources.kxml.runninginstance;

import static org.gcube.common.resources.kxml.KGCUBEResource.NS;

import java.util.HashMap;

import org.gcube.common.core.resources.runninginstance.ScopedAccounting;
import org.gcube.common.core.scope.GCUBEScope;

import org.kxml2.io.KXmlParser;
import org.kxml2.io.KXmlSerializer;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class KScopedAccounting {

	/**
	 * Deserializes a  ScopedAccounting map
	 * @param parser the parser 
	 * @return a scoped accounting map
	 * @throws Exception if the parsing fails
	 */
	public static HashMap<GCUBEScope,ScopedAccounting> load(KXmlParser parser) throws Exception {
		HashMap<GCUBEScope,ScopedAccounting> map = new HashMap<GCUBEScope,ScopedAccounting>();
		loop: while (true) {
			switch (parser.next()){			
				case KXmlParser.START_TAG :
					if (parser.getName().equals("ScopedAccounting")) {
						GCUBEScope scope = GCUBEScope.getScope(parser.getAttributeValue(NS,"scope"));
						ScopedAccounting scopedAccounting = new ScopedAccounting();
						scopedAccounting.setScope(scope);
						innerLoop: while (true) {
							switch (parser.next()){			
								case KXmlParser.START_TAG :
									if (parser.getName().equals("TotalINCalls")) scopedAccounting.setTotalINCalls(Long.valueOf(parser.nextText()));
									if (parser.getName().equals("AverageINCalls")) 
										scopedAccounting.getAverageCallsMap().put(Long.valueOf(parser.getAttributeValue(NS,"interval")),
												Double.valueOf(parser.getAttributeValue(NS,"average")));
									if (parser.getName().equals("AverageInvocationTime")) 
										scopedAccounting.getAverageTimeMap().put(Long.valueOf(parser.getAttributeValue(NS,"interval")),
											Double.valueOf(parser.getAttributeValue(NS,"average")));
									if (parser.getName().equals("TopCallerGHN")) { 
											scopedAccounting.setTopCallerGHNavgHourlyCalls(Double.valueOf(parser.getAttributeValue(NS,"avgHourlyCalls")));
											scopedAccounting.setTopCallerGHNavgDailyCalls(Double.valueOf(parser.getAttributeValue(NS,"avgDailyCalls")));
											scopedAccounting.setTopCallerGHNtotalCalls(Long.valueOf(parser.getAttributeValue(NS,"totalCalls")));
											innestloop: while (true) {
												switch (parser.next()){			
													case KXmlParser.START_TAG :
														if (parser.getName().equals("GHNName")) 
															scopedAccounting.setTopCallerGHN(parser.nextText());
														break;
													case KXmlParser.END_TAG:
														if (parser.getName().equals("TopCallerGHN")){
															break innestloop;
														}
														break;
												}
											}
									}
									break;
								case KXmlParser.END_TAG:
									if (parser.getName().equals("ScopedAccounting")){
										break innerLoop;
									}
									break;
							}
							
							}
						map.put(scope, scopedAccounting);
					}
					break;
				case KXmlParser.END_TAG:
					if (parser.getName().equals("Accounting")){
						break loop;
					}
					break;
				case KXmlParser.END_DOCUMENT :
					throw new Exception("Parsing failed at ScopedAccounting");
			}
		}
		return map; 
	}
	
	/**
	 * Serializes the ScopedAccounting component 
	 * @param component the ScopedAccounting component to serialize
	 * @param serializer the serializer 
	 * @throws Exception if the serialization fails
	 */
	public static void store(ScopedAccounting component, KXmlSerializer serializer) throws Exception {
		if (component==null) return;
		serializer.startTag(NS,"ScopedAccounting").attribute(NS, "scope", component.getScope().toString());
		if (component.getTotalINCalls() != null) serializer.startTag(NS,"TotalINCalls").text(component.getTotalINCalls().toString()).endTag(NS,"TotalINCalls");
		if (component.getAverageCallsMap() != null)
			for (Long interval: component.getAverageCallsMap().keySet()){
				 serializer.startTag(NS,"AverageINCalls").attribute(NS, "interval", interval.toString()).attribute(NS, "average",component.getAverageCallsMap().get(interval).toString()).endTag(NS, "AverageINCalls");
			}
		if (component.getAverageTimeMap() != null)
			for (Long interval: component.getAverageTimeMap().keySet()){
				 serializer.startTag(NS,"AverageInvocationTime").attribute(NS, "interval", interval.toString()).attribute(NS, "average",component.getAverageTimeMap().get(interval).toString()).endTag(NS, "AverageInvocationTime");
			}
		if (component.getTopCallerGHN()!= null && component.getTopCallerGHNavgDailyCalls() != null && component.getTopCallerGHNavgHourlyCalls() != null && 
				component.getTopCallerGHNtotalCalls() != null)
			serializer.startTag(NS,"TopCallerGHN").attribute(NS, "avgHourlyCalls", component.getTopCallerGHNavgHourlyCalls().toString()).
			attribute(NS, "avgDailyCalls",component.getTopCallerGHNavgDailyCalls().toString()).
			attribute(NS, "totalCalls",component.getTopCallerGHNtotalCalls().toString());
			serializer.startTag(NS,"GHNName").text(component.getTopCallerGHN()).endTag(NS,"GHNName").endTag(NS, "TopCallerGHN");
		serializer.endTag(NS,"ScopedAccounting");
		
	}
}
