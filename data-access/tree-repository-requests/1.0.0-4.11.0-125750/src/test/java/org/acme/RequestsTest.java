/**
 * 
 */
package org.acme;

import static org.junit.Assert.*;

import javax.xml.namespace.QName;

import org.gcube.data.tr.requests.AbstractRequest;
import org.gcube.data.tr.requests.BindSource;
import org.gcube.data.tr.requests.Mode;
import org.gcube.data.tr.requests.RequestBinder;
import org.junit.Test;

/**
 * @author Fabio Simeoni
 *
 */
public class RequestsTest {

	@Test
	public void bindSourceRequestSerialisesCorrectly() throws Exception {
		
		BindSource  r = new BindSource("foo");
		r.setDescription("test");
		r.addTypes(new QName("test1"),new QName("http://acme.org","test2"));
		
		RequestBinder binder = new RequestBinder();
		AbstractRequest parsed = binder.bind(binder.bind(r),BindSource.class);
		
		assertEquals(r,parsed);
	}
	
	@Test
	public void reconfigureSourceRequestSerialisesCorrectly() throws Exception {

		BindSource r = new BindSource("foo",Mode.READABLE);
		
		RequestBinder binder = new RequestBinder();
		AbstractRequest parsed = binder.bind(binder.bind(r),BindSource.class);
		
		assertEquals(r,parsed);
	}
}
