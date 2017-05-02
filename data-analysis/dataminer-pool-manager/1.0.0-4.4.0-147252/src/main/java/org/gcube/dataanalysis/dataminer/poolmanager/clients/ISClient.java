package org.gcube.dataanalysis.dataminer.poolmanager.clients;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Domain;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.comparator.HostComparator;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import au.com.bytecode.opencsv.CSVReader;

public class ISClient {


	public Host getDataminer(String hostname) {
		Host h = new Host();
		boolean remote2 = true;

		if (!remote2) {
			h.setName("dataminer1-devnext.d4science.org");
			return h;
		} else {
			//SimpleQuery query = queryFor(ServiceEndpoint.class);
			//query.addCondition("$resource/Profile/RunTime/HostedOn/text() eq '" + hostname + "'");
			//DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			//List<ServiceEndpoint> resources = client.submit(query);
			//ServiceEndpoint a = resources.get(0);
			//h.setName(a.profile().runtime().hostedOn());
			h.setName(hostname);
		}
		return h;
	}

	// return the HProxy hostname in the VRE
	public static String getHProxy() {
		Host h = new Host();
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq 'DataMiner'");
		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> resources = client.submit(query);
		h.setName(resources.get(0).profile().runtime().hostedOn());
		return h.getName();
	}

	// return the Cluster hostname from the IS
	// to develop
	// public Cluster getCluster(){
	// Cluster cl = new Cluster();
	// String HProxy = this.getHProxy();
	// SimpleQuery query = queryFor(ServiceEndpoint.class);
	// query.addCondition("$resource/Profile/Platform/Name/text() eq
	// 'DataMiner'");
	// DiscoveryClient<ServiceEndpoint> client =
	// clientFor(ServiceEndpoint.class);
	// List<ServiceEndpoint> resources = client.submit(query);
	// cl.setName(resources.get(0).profile().runtime().hostedOn());
	// return null;
	//
	// }

	// return the dataminer hostnames from the IS
	// to develop
	// public List<Host> getDM(){
	// Cluster cl = new Cluster();
	// String HProxy = this.getHProxy();
	// SimpleQuery query = queryFor(ServiceEndpoint.class);
	// query.addCondition("$resource/Profile/Platform/Name/text() eq
	// 'DataMiner'");
	// DiscoveryClient<ServiceEndpoint> client =
	// clientFor(ServiceEndpoint.class);
	// List<ServiceEndpoint> resources = client.submit(query);
	// cl.setName(resources.get(0).profile().runtime().hostedOn());
	// return null;
	//
	// }

	/**
	 * Return the list of hosts (dataminers) in a given VRE
	 * 
	 * @param vreName
	 * @return
	 */
	public Collection<Host> listDataminersInVRE() {

		boolean remote = false;

		if (!remote) {
			Collection<Host> out = new Vector<>();
			Host h = new Host();
			// h.setName("bb-dataminer.res.eng.it");
			// h.setName("vm101.ui.savba.sk");
			h.setName("dataminer1-devnext.d4science.org");
			out.add(h);
			return out;
		} else {

			SimpleQuery query = queryFor(ServiceEndpoint.class);

			// old version
			// query.addCondition("$resource/Profile/Category/text() eq
			// 'DataAnalysis'")
			// .addCondition("$resource/Profile/Name/text() eq 'DataMiner'");

			query.addCondition("$resource/Profile/Platform/Name/text() eq 'DataMiner'");

			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

			List<ServiceEndpoint> resources = client.submit(query);

			Collection<Host> out = new Vector<>();
			for (ServiceEndpoint r : resources) {
				Host h = new Host();
				h.setName(r.profile().runtime().hostedOn());
				out.add(h);
			}
			return out;
		}
	}

	public static void main(String[] args) throws IOException, SVNException {
		ISClient a = new ISClient();
		ScopeProvider.instance.set("/gcube/devNext/NextNext");
		// System.out.println(a.getHProxy());
		// System.out.println(a.MapCluster());
		// System.out.println(a.listDataMinersByCluster());
		// System.out.println(a.listDataMinersByCluster());

		// List<Dependency> list = new LinkedList<Dependency>();
		// Dependency aa = new Dependency();
		// aa.setName("testnunzio");
		// aa.setType("cran:");
		// list.add(aa);

		// a.checkSVNdep();
		System.out.println(a.getDataminer("dataminer1-d-d4s.d4science.org").getDomain());
		// System.out.println(a.listDataminersInVRE());
	}
}
