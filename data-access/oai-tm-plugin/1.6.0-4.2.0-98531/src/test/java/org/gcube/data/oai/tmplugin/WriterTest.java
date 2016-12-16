//package org.gcube.data.oai.tmplugin;
//
//import static org.gcube.common.clients.stubs.jaxws.StubFactory.*;
//import static org.gcube.data.tm.utils.Utils.*;
//import static org.gcube.data.tml.Constants.*;
//import static org.gcube.data.trees.data.Nodes.*;
//import static org.junit.Assert.*;
//
//import java.io.File;
//import java.net.URI;
//
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import org.gcube.common.mycontainer.Deployment;
//import org.gcube.common.mycontainer.Gar;
//import org.gcube.common.mycontainer.MyContainer;
//import org.gcube.common.mycontainer.MyContainerTestRunner;
//import org.gcube.common.mycontainer.Scope;
//import org.gcube.data.tm.services.TWriterService;
//import org.gcube.data.tm.stubs.AnyHolder;
//import org.gcube.data.tml.stubs.TWriterStub;
//import org.gcube.data.tml.stubs.Types.NodeHolder;
//import org.gcube.data.trees.data.Tree;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//@RunWith(MyContainerTestRunner.class) @Scope("/gcube/devsec")
//public class WriterTest {
//
//	@Deployment
//	static Gar gar = new Gar(new File("src/test/resources/tree-manager-service.gar"));
//
//	@Named(writerWSDDName)
//	static URI address;
//
//	@Inject
//	static MyContainer container;
//	
//	static TWriterStub writerStub;
//
//	@BeforeClass
//	public static void setup() {
//
//		//setProxy("localhost", 8081); // decomment after on-the-wire analysis
//
//		writerStub = stubFor(writer).at(address);
//
//	}
//
//	@Test
//	public void add() throws Exception {
//
//		final Tree t = t(e("a", 3));
//
//		
//		// mock service implementation
//		TWriterService mock = new TWriterService() {
//
//			public AnyHolder add(AnyHolder params) {
//
//				try {
//					assertEquals(t,toTree(params));
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//
//				return toAnyHolder(t);
//			}
//		};
//
//		// install mock
//		container.setEndpoint(writerWSDDName, mock);
//
//		NodeHolder treeHolder = writerStub.add(new NodeHolder(t));
//
//		assertEquals(t, treeHolder.asTree());
//	}
//
//}
