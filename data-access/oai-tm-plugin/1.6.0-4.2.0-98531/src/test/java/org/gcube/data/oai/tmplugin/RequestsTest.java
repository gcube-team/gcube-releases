/**
 * 
 */
package org.gcube.data.oai.tmplugin;

import static org.gcube.data.oai.tmplugin.TestUtils.*;
import static org.junit.Assert.*;

import org.gcube.data.oai.tmplugin.requests.Request;
import org.gcube.data.oai.tmplugin.requests.RequestBinder;
import org.gcube.data.oai.tmplugin.requests.WrapRepositoryRequest;
import org.gcube.data.oai.tmplugin.requests.WrapSetsRequest;
import org.junit.Test;

/**
 * @author Fabio Simeoni
 *
 */
public class RequestsTest {

	@Test
	public void wrapSetRequestBindsAndUnbinds() throws Exception {

			
			Request  r = new WrapSetsRequest(repourl);
			r.addSets("set1","set2","set3");
			
			RequestBinder binder = new RequestBinder();
			Request parsed = binder.bind(binder.bind(r),Request.class);
			
			assertEquals(r,parsed);
		}
		
		@Test
		public void wrapRepositoryRequestBindsAndUnbinds() throws Exception {

			Request r = new WrapRepositoryRequest(repourl);
			r.addSets("set1","set2","set3");

			
			RequestBinder binder = new RequestBinder();
			Request parsed = binder.bind(binder.bind(r),WrapRepositoryRequest.class);
			
			assertEquals(r,parsed);
			
			
			parsed = binder.bind(binder.bind(r),WrapRepositoryRequest.class);
			
			assertEquals(r,parsed);
		}
}
