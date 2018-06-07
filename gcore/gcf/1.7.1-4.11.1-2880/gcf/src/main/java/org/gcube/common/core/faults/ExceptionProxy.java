/**
 * 
 */
package org.gcube.common.core.faults;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author fabio
 *
 */
@XmlRootElement(namespace="http://gcube-system.org",name="stacktrace")
public class ExceptionProxy {

	
	static {
		try {
			context=JAXBContext.newInstance(ExceptionProxy.class);
		}
		catch(Throwable t) {
			throw new AssertionError(t);
		}
	}

	
	@XmlType(name="e")
	static class StackTraceElementProxy {
		
		@XmlAttribute public String cn;
		@XmlAttribute public String mn;
		@XmlAttribute public String fn;
		@XmlAttribute public int ln;
		
	}

	private static JAXBContext context;
	private static final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

	
	public static ExceptionProxy newInstance(Element e) throws Exception {
		return (ExceptionProxy) context.createUnmarshaller().unmarshal(e);
	}
	
	public static ExceptionProxy newInstance(Throwable t) {
		
		ExceptionProxy p = new ExceptionProxy();
		p.msg=t.getMessage();
		p.name=t.getClass().getCanonicalName();
		
		for (StackTraceElement e : t.getStackTrace()) {
			StackTraceElementProxy ep = new StackTraceElementProxy();
			ep.cn= e.getClassName();
			ep.mn=e.getMethodName();
			ep.fn=e.getFileName();
			ep.ln=e.getLineNumber();
			p.el.add(ep);
		}
		
		if (t.getCause()!=null) 
			p.c= newInstance(t.getCause());
		
		return p;
		
	}
	
		
	@XmlAttribute public String name;
	@XmlAttribute public String msg;
	@XmlElement public List<StackTraceElementProxy> el = new ArrayList<StackTraceElementProxy>();
	@XmlElement public ExceptionProxy c;
	

	public Throwable toThrowable() {
		String msg = "remote cause: ("+(this.msg==null?name:this.msg)+")";
		Throwable t = c==null? new Throwable(msg):
					                 new Throwable(msg,c.toThrowable());
		
		List<StackTraceElement> elements = new ArrayList<StackTraceElement>();
		
		for (StackTraceElementProxy ep : el)
			elements.add(new StackTraceElement(ep.cn, ep.mn, ep.fn, ep.ln));
		t.setStackTrace(elements.toArray(new StackTraceElement[0]));
		
	
		return t;
	}
	
	
	public Element toElement() throws Exception {
		Document d = domFactory.newDocumentBuilder().newDocument();
		context.createMarshaller().marshal(this,d);
		return d.getDocumentElement();
	}
	

}

