package org.gcube.data.tml.stubs;

import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
import static org.gcube.data.tm.utils.Utils.*;
import static org.gcube.data.tml.Constants.*;
import static org.gcube.data.trees.data.Nodes.*;
import static org.gcube.data.trees.patterns.Patterns.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;

import org.gcube.common.mycontainer.Deployment;
import org.gcube.common.mycontainer.Gar;
import org.gcube.common.mycontainer.MyContainer;
import org.gcube.common.mycontainer.MyContainerTestRunner;
import org.gcube.common.mycontainer.Scope;
import org.gcube.data.tm.services.TReaderService;
import org.gcube.data.tm.stubs.AnyHolder;
import org.gcube.data.tm.stubs.GetByIDParams;
import org.gcube.data.tm.stubs.GetByIDsParams;
import org.gcube.data.tm.stubs.GetParams;
import org.gcube.data.tm.stubs.UnknownPathFault;
import org.gcube.data.tm.stubs.UnknownTreeFault;
import org.gcube.data.tm.stubs.UnsupportedOperationFault;
import org.gcube.data.tm.stubs.UnsupportedRequestFault;
import org.gcube.data.tml.exceptions.UnknownPathException;
import org.gcube.data.tml.exceptions.UnknownTreeException;
import org.gcube.data.tml.proxies.Path;
import org.gcube.data.tml.stubs.TReaderStub;
import org.gcube.data.tml.stubs.Types.LookupRequest;
import org.gcube.data.tml.stubs.Types.LookupStreamRequest;
import org.gcube.data.tml.stubs.Types.NodeHolder;
import org.gcube.data.tml.stubs.Types.QueryRequest;
import org.gcube.data.trees.data.Node;
import org.gcube.data.trees.data.Tree;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(MyContainerTestRunner.class) @Scope("/gcube/devsec")
public class ReaderTest {

	@Deployment
	static Gar gar = new Gar(new File("src/test/resources/tree-manager-service.gar"));

	@Named(readerWSDDName)
	static URI address;

	@Inject
	static MyContainer container;
	
	static TReaderStub readerStub;

	@BeforeClass
	public static void setup() {

		//setProxy("localhost", 8081); // decomment after on-the-wire analysis

		readerStub = stubFor(reader).at(address);

	}

	@Test
	public void lookup() throws Exception {

		final Tree output = t(e("a", 3));

		// mock service implementation
		TReaderService mock = new TReaderService() {

			public AnyHolder getByID(GetByIDParams params) {

				assertEquals("id", params.getRootID());

				try {
					assertEquals(tree(), toPattern(params.getPattern()));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				return toAnyHolder(output);
			}
		};

		// install mock
		container.setEndpoint(readerWSDDName, mock);

		LookupRequest holder = new LookupRequest("id", tree());

		NodeHolder treeHolder = readerStub.lookup(holder);

		assertEquals(output, treeHolder.asTree());
	}

	@Test
	public void lookupErrors() throws Exception {

		// mock service implementation
		TReaderService mock = mock(TReaderService.class);

		when(mock.getByID(any(GetByIDParams.class))).thenThrow(new UnsupportedOperationFault(),
				new UnsupportedRequestFault(), new UnknownTreeFault());
		
		// install mock
		container.setEndpoint(readerWSDDName, mock);

		LookupRequest request = new LookupRequest("id", tree());

		try {
			readerStub.lookup(request);
			fail();
		} 
		catch (org.gcube.data.tml.stubs.Types.UnsupportedOperationFault e) {

		}

		try {
			readerStub.lookup(request);
			fail();
		} catch (org.gcube.data.tml.stubs.Types.UnsupportedRequestFault e) {

		}

		try {
			readerStub.lookup(request);
			fail();
		} catch (UnknownTreeException e) {

		}

	}

	@Test
	public void lookupStream() throws Exception {
		
		// mock service implementation
		TReaderService mock = new TReaderService() {

			public String getByIDs(GetByIDsParams params) {

				assertEquals("locator", params.getLocator());

				try {
					assertEquals(tree(), toPattern(params.getPattern()));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				return "locator";
			}
		};
		// install mock
		container.setEndpoint(readerWSDDName, mock);

		LookupStreamRequest request = new LookupStreamRequest("locator", tree());
		
		String output = readerStub.lookupStream(request);
		
		assertEquals("locator",output);

	}
	
	@Test
	public void query() throws Exception {
		
		// mock service implementation
		TReaderService mock = new TReaderService() {

			public String get(GetParams params) {

				try {
					assertEquals(tree(), toPattern(params.getPattern()));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				return "locator";
			}
		};
		// install mock
		container.setEndpoint(readerWSDDName, mock);

		 QueryRequest request = new QueryRequest(tree());
		
		String output = readerStub.query(request);
		
		assertEquals("locator",output);

	}
	
	@Test
	public void lookupNode() throws Exception {
		
		final String[] ids = new String[]{"1","2"};
		
		final Node node = n(e("a", 3));
		
		// mock service implementation
		TReaderService mock = new TReaderService() {

			public AnyHolder getNode(org.gcube.data.tm.stubs.Path params) {

				assertArrayEquals(ids,params.getId());

				try {
					return toHolder(node);
				}
				catch(Exception e) {
					throw new RuntimeException(e);
				}
 			}
		};
		// install mock
		container.setEndpoint(readerWSDDName, mock);

		
		Path path = new Path(ids);
		
		NodeHolder output = readerStub.lookupNode(path);
		
		assertEquals(node,output.asNode());

	}
	
	@Test
	public void lookupNodeError() throws Exception {
		
		final String[] ids = new String[]{"1","2"};
		
		// mock service implementation
		TReaderService mock = mock(TReaderService.class);
		
		when(mock.getNode(any(org.gcube.data.tm.stubs.Path.class))).thenThrow(new UnknownPathFault());
		
		// install mock
		container.setEndpoint(readerWSDDName, mock);

		try {
			readerStub.lookupNode(new Path(ids));
			fail();
		}
		catch(UnknownPathException e){}
		

	}
}