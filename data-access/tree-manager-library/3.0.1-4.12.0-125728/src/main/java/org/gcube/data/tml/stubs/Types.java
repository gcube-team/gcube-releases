package org.gcube.data.tml.stubs;

import static org.gcube.data.trees.io.XMLBindings.fromElement;
import static org.gcube.data.trees.io.XMLBindings.nodeFromElement;
import static org.gcube.data.trees.io.XMLBindings.toElement;

import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.parsers.*;
import javax.xml.ws.*;

import org.gcube.data.tml.proxies.Binding;
import org.gcube.data.trees.data.*;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.patterns.*;
import org.w3c.dom.*;

/**
 * Types used in the interfaces of service stubs.
 * 
 * @author Fabio Simeoni
 *
 */
@SuppressWarnings("serial")
public class Types {


	
	public static class BindingsHolder {
		@XmlElement
		public List<Binding> bindings;
	}
	
	public static class LookupRequest {
		
		public LookupRequest() {}
		
		public LookupRequest(String id,Pattern p) {
			this.rootID=id;
			this.pattern = new AnyPattern(p);
		}
		
		@XmlElement
		public String rootID;
		
		@XmlElementRef
		public AnyPattern pattern;
	}
	
	public static class LookupNodeRequest {
		
		public LookupNodeRequest() {}
		
		public LookupNodeRequest(String ...ids) {
			this.ids=ids;
		}
		
		@XmlElement(name="id")
		public String[] ids;
		
	}
	
	public static class LookupStreamRequest {
		
		public LookupStreamRequest() {}
		
		public LookupStreamRequest(String locator,Pattern p) {
			this.locator=locator;
			this.pattern = new AnyPattern(p);
		}
		
		@XmlElement
		public String locator;
		
		@XmlElementRef
		public AnyPattern pattern;
	}
	
	public static class QueryRequest {
		
		public QueryRequest() {}
		
		public QueryRequest(Pattern p) {
			this.pattern = new AnyPattern(p);
		}
		
		@XmlElementRef
		public AnyPattern pattern;
	}
	
	
	@XmlRootElement(name="pattern")
	public static class AnyPattern extends AnyWrapper{
		
		private static DocumentBuilder builder; 
		
		static {
			DocumentBuilderFactory.newInstance();
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public AnyPattern() {}
		
		public AnyPattern(Pattern p) {
			try {
				Document doc = builder.newDocument();
				Patterns.getMarshaller().marshal(p,doc);
				element = doc.getDocumentElement();
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@XmlRootElement
	public static class NodeHolder extends AnyWrapper{
		
		public NodeHolder() {}
		
		public NodeHolder(Tree tree) throws Exception {
			element = toElement(tree);
		}
		
		
		public Node asNode() throws Exception {
			return nodeFromElement(element);
		}
		
		public Tree asTree() throws Exception {
			return fromElement(element);
		}
	}
	
	
	public static class AnyWrapper {
		
		@XmlAnyElement
		public Element element; 
		
		public AnyWrapper() {}
		
		public AnyWrapper(Element e) {
			this.element=e;
		}
		
		@Override
		public String toString() {
			return "[element=" + element + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((element == null) ? 0 : element.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AnyWrapper other = (AnyWrapper) obj;
			if (element == null) {
				if (other.element != null)
					return false;
			} else if (!element.isEqualNode(other.element))
				return false;
			return true;
		}
	}
	
	
	// STUB FAULTS
	
	@WebFault(name="InvalidRequestFault")
	public static class InvalidRequestFault extends Exception {

		public InvalidRequestFault(String s) {
			super(s);
		}
	}
	
	
	@WebFault(name="UnsupportedOperationFault")
	public static class UnsupportedOperationFault extends Exception {

		public UnsupportedOperationFault(String s) {
			super(s);
		}
	}
	
	@WebFault(name="UnsupportedRequestFault")
	public static class UnsupportedRequestFault extends Exception {

		public UnsupportedRequestFault(String s) {
			super(s);
		}
	}
}
