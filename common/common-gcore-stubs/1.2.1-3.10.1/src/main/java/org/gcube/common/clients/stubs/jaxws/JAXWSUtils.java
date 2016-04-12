package org.gcube.common.clients.stubs.jaxws;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * Library-wide utilities.
 * 
 * @author Fabio Simeoni
 * 
 */
public class JAXWSUtils {

	@XmlRootElement
	public static class Empty{}
	
	public static final Empty empty = new Empty(); 
	
	/**
	 * Returns the remote cause of a {@link SOAPFaultException} as a {@link Throwable}.
	 * 
	 * @param e the exception
	 * @return a {@link Throwable} deserialised from the stacktrace found in the {@link SOAPFault} inside the exception,
	 *         or the input exception itself if the stacktrace cannot be found.
	 */
	public static Throwable remoteCause(SOAPFaultException e) {

		// if we cannt do better, we throw this
		Throwable throwable = e;

		SOAPFault faultBean = e.getFault();

		// if a serialised stacktrace is available, parse it and convert it into a throwable
		Iterator<?> details = faultBean.getDetail().getDetailEntries();
		while (details.hasNext()) {

			DetailEntry detail = (DetailEntry) details.next();
			String ns = detail.getNamespaceURI();
			String local = detail.getLocalName();

			boolean match = ns != null && ns.equals("http://gcube-system.org") && local != null
					&& local.equals("stacktrace");

			if (match)
				try {
					ExceptionProxy proxy = ExceptionProxy.newInstance(detail);
					throwable = proxy.toThrowable();
				} catch (Throwable t) {
					throwable = new Exception("could not parse remote fault", t);
				}
		}

		return throwable;
	}
	
	
	static void notNull(String message,Object o) {
		if (o==null)
			throw new IllegalArgumentException(o+" cannot be null");
	}

}
