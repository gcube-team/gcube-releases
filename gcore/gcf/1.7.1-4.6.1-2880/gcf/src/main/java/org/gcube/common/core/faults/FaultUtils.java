package org.gcube.common.core.faults;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;

import javax.wsdl.Fault;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * Utilities to convert between remote faults and local exceptions whilst preserving stack traces.
 * 
 * @author Fabio Simeoni
 *
 */
public class FaultUtils {

	

	/**
	 * Returns a given fault after serialising into it an original cause.
	 * 
	 * 
	 * @param fault the fault
	 * @param localCause the cause
	 * @return the fault
	 * @param <E> the type of the fault
	 */
	public static <E extends GCUBEFault> E newFault(E fault, Throwable localCause) {
		
		fault.setFaultMessage(localCause.getMessage());

		// pretty pointless output
		fault.removeFaultDetail(new QName("http://xml.apache.org/axis/", "stackTrace"));

		// adds stacktrace of local cause as single detail element, for debugging purposes
		StringWriter w = new StringWriter();
		localCause.printStackTrace(new PrintWriter(w));
		fault.addFaultDetail(new QName("cause"),w.toString());

		//serialise stacktrace of local cause as single detail element, for client-side deserialisation
		try {
			fault.addFaultDetail(ExceptionProxy.newInstance(localCause).toElement());
		} 
		catch (Exception e) {
		}
		
		return fault;
	}
	
	/**
	 * Returns the remote cause of a {@link Fault} as a {@link Throwable}
	 * @param fault the fault
	 * @return the {@link Throwable}
	 */
	public static Throwable remoteCause(GCUBEFault fault) {
	
		//if we cannt do better, we 
		Throwable throwable = fault;
		
		//if a serialised stacktrace is available, parse it and convert it into a throwable
		for (Element d : fault.getFaultDetails()) {
			String ns = d.getNamespaceURI();
			String local = d.getLocalName();
			boolean match = 
					ns!=null && ns.equals("http://gcube-system.org") &&
					local!=null && local.equals("stacktrace");
					
			if (match)
				try {
					ExceptionProxy proxy = ExceptionProxy.newInstance(d);
				    throwable = proxy.toThrowable();
				}
				catch (Throwable t) {
					throwable = new RemoteException("could not parse remote fault", t);
				}
		}
		
		return throwable;
	}
}
