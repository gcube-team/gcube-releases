package org.gcube.datatransfer.agent.impl.utils;


import static org.gcube.data.trees.patterns.Patterns.*;

import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.axis.message.MessageElement;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.streams.Stream;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.gcube.data.trees.io.Bindings;
import org.gcube.data.trees.patterns.Pattern;
import org.gcube.data.trees.patterns.Patterns;
import org.gcube.datatransfer.agent.stubs.datatransferagent.AnyHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * 
 * @author Fabio Simeoni (FAO)
 * @author Andrea Manzi (CERN)
 */
public class Utils {
	
	
	public static GCUBELog logger= new GCUBELog(Utils.class);
	

	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	
	/**
	 * Converts a {@link AnyHolder} returned by the service into a {@link Pattern}.
	 * @param h the holder
	 * @return the pattern
	 * @throws Exception if the conversion fails
	 */
	public static Pattern getPattern(AnyHolder h) throws Exception {

		return h==null?null: (Pattern) getUnMarshaller().unmarshal(h.get_any()[0].getAsDOM());

	}


	public static AnyHolder toHolder(Pattern p) throws Exception {

		if (p==null) 
			return null;

		Document filterNode = factory.newDocumentBuilder().newDocument();
		Patterns.getMarshaller().marshal(p, filterNode);
		return toHolder(filterNode.getDocumentElement());

	}
	/* Converts an {@link Element} into a {@link AnyHolder} accepted by the service.
	 * @param e the element
	 * @return the holder
	 */
	public static AnyHolder toHolder(Element e) {

		return e==null?null:new AnyHolder(new MessageElement[]{new MessageElement(e)});

	}

	/**
	 * Transforms a {@link Node} into a {@link AnyHolder} accepted by the service.
	 * @param n the node
	 * @return the holder
	 * @throws Exception if the conversion fails
	 */
	public static AnyHolder toHolder(Node n) throws Exception {

		return n==null?null:toHolder(Bindings.nodeToElement(n));

	}
	
	public static String replaceUnderscore(String input) {
		return input.replaceAll("\\.", "_");
	}
	
	//returns true if an exception occured
	public static boolean consumeStream(Stream<Tree> stream) {
		try{			
			while(stream.hasNext()){
				Tree tmp = stream.next();
			}
			
		}catch(Exception e){
			logger.error("Utils - consumeStream - exception");
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	public static String getParameterFromWSResource(RPDocument resource,String field){
		try{
			List<String> list = resource.evaluate("//"+field);
			if(list==null)return "no_"+field;
			else if(list.size()==0)return "no_"+field;
			else{				
				String wholeName=list.get(0);
				//legger.debug(field+"="+wholeName);
				String name=wholeName.substring(0,wholeName.lastIndexOf("</"));
				name = name.substring(name.lastIndexOf(">")+1);
				return name;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			return "no_"+field;
		}
	}
}
	