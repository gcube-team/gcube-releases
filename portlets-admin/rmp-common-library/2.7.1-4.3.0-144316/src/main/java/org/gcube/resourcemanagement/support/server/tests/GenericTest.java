/****************************************************************************


 *  
 *  
 *  
 *  
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: GenericTest.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.tests;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.support.server.gcube.CacheManager;
import org.gcube.resourcemanagement.support.server.gcube.ISClientRequester;
import org.gcube.resourcemanagement.support.server.managers.resources.GHNManager;
import org.gcube.resourcemanagement.support.server.managers.resources.GenericResourceManager;
import org.gcube.resourcemanagement.support.server.managers.resources.ManagementUtils;
import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;
import org.gcube.resourcemanagement.support.server.types.AllowedResourceTypes;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;
import org.gcube.resourcemanagement.support.shared.types.datamodel.ResourceDescriptor;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class GenericTest {
	private static final String LOG_PREFIX = "[SW-SUPPORT-TEST]";

	public static String testCreation() {
		System.out.println("\n\n\n******************** TEST CREATION ***************");

		String resID = null;

		try {
			resID = GenericResourceManager.create(
					null,
					new ScopeBean("/gcube/devsec"),
					"RMP Test " + new Date(),
					"RMP Test Description",
					"Hello",
			"test");
			ServerConsole.trace(null, "Generic Resource Created with ID: " + resID);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		} finally {
			System.out.println("\n******************** TEST CREATION END ***************\n");
		}
		return resID;
	}

	public static void testScopeCopy(final String resID, final String fromScope, final String toScope) {
		System.out.println("\n\n\n******************** TEST SCOPE COPY ***************");
		try {
//			GenericResourceManager res = new GenericResourceManager(resID);
//			ServerConsole.trace(null,
//					res.addToExistingScope(new ScopeBean(fromScope), new ScopeBean(toScope))
//			);
			String[] ids = {resID};
			ManagementUtils.addToExistingScope(AllowedResourceTypes.GenericResource, ids, new ScopeBean(fromScope), new ScopeBean(toScope));
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		} finally {
			System.out.println("\n******************** TEST SCOPE COPY END ***************\n");
		}
	}

	public static void testResourceEdit(final String resID, final ScopeBean scope) {
		System.out.println("\n\n\n******************** TEST RESEDIT COPY ***************");
		try {
			GenericResourceManager res = new GenericResourceManager(resID);
			res.update(res.getName()+" Edited", "updated description", "<update>updated body</update>", "test2", scope);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		} finally {
			System.out.println("\n******************** TEST RESEDIT END ***************\n");
		}
	}

	public static void testModeGHN(final String resID, final String scope)
	throws Exception {
		ScopeBean queryScope = new ScopeBean(scope);
		GHNManager ghnManager = new GHNManager(resID);
		HostingNode res = (HostingNode) ghnManager.getResource(queryScope);
		res.profile().description().status("CERTIFIED");
		ScopeProvider.instance.set(queryScope.name());		
		ghnManager.getRegistryPublisher().update(res);
	}

	public static void testGHN() {
		System.out.println("\n\n\n******************** TEST GHN ***************");
		try {
			GHNManager ghn1 = new GHNManager("796f0680-3937-11e2-9d5f-ae6a92affb51", "pcd4science3.cern.ch");
			ghn1.addToExistingScope(new ScopeBean("/gcube"), new ScopeBean("/gcube/devsec"));
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		} finally {
			System.out.println("\n******************** TEST GHN END ***************\n");
		}
	}

	public static void testRemoveFromScope(final String resID, final ScopeBean scope) {
		System.out.println("\n\n\n******************** TEST RESOURCE REMOVEFROMSCOPE ***************");
		try {
			GenericResourceManager res = new GenericResourceManager(resID);
			res.removeFromScope(scope);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		} finally {
			System.out.println("\n******************** TEST RESOURCE REMOVEFROMSCOPE END ***************\n");
		}
	}
	

	public static void testDelete(final String resID, final ScopeBean scope) {
		System.out.println("\n\n\n******************** TEST RESOURCE DELETE ***************");
		try {
			GenericResourceManager res = new GenericResourceManager(resID);
			res.delete(scope);
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
		} finally {
			System.out.println("\n******************** TEST RESOURCE DELETE END ***************\n");
		}
	}

	private static void testTree(ScopeBean scope) throws Exception {

		CacheManager cm = new CacheManager();
		cm.setUseCache(false);
		HashMap<String, ArrayList<String>> results = ISClientRequester.getResourcesTree(cm, scope);
		for (String res : results.keySet()) {
			System.out.println(res);
			for (String sub : results.get(res)) {
				System.out.println(sub);
			}
		}
		
		List<String> descs = ISClientRequester.getResourcesByType(cm, scope, "GenericResource", "VRE");
		for (String resourceDescriptor : descs) {
			DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Node node = docBuilder.parse(new InputSource(new StringReader(resourceDescriptor))).getDocumentElement();
			XPathHelper helper = new XPathHelper(node);
			System.out.println(helper.evaluate("/Resource/Name/text()").get(0));
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(final String[] args) throws Exception {
		// The scopes must be initialized
		ScopeManager.setScopeConfigFile("test-suite" + File.separator + "scopes" + File.separator + "scopedata_admin.xml");
		
		ScopeProvider.instance.set("/gcube/devsec");
		
		testTree( new ScopeBean("/gcube/devsec"));
		
//		boolean deepTest = true;
//
//		// testGHN();
//		if (deepTest) {
//			String resID = testCreation();
//			testScopeCopy(resID, "/gcube/devsec", "/gcube/devsec/devVRE");
//////
//			System.out.println("\n\nWaiting for resource refresh 20secs.\n\n\n");
//			Thread.sleep(20000);
//
//
//			//testResourceEdit(resID, new ScopeBean("/gcube/devsec"));
			//testDelete(resID, new ScopeBean("/gcube/devsec"));
//
//			testRemoveFromScope(resID, new ScopeBean("/gcube/devsec/devVRE"));
//		} else {
//			testModeGHN("796f0680-3937-11e2-9d5f-ae6a92affb51", "/gcube/devsec/devVRE");
//		}
	}
}
