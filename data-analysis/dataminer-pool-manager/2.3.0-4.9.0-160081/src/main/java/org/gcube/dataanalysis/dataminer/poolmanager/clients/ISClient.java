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
import java.io.StringWriter;
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

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Algorithm;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Cluster;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Domain;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Host;
import org.gcube.dataanalysis.dataminer.poolmanager.datamodel.comparator.HostComparator;
import org.gcube.dataanalysis.dataminer.poolmanager.service.DataminerPoolManager;
import org.gcube.informationsystem.publisher.AdvancedScopedPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.informationsystem.publisher.exception.RegistryNotFoundException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(ISClient.class);


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

	
	public void updateAlg(Algorithm algo) {

		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();

		SimpleQuery query = queryFor(GenericResource.class);
		query.addCondition("$resource/Profile/Name/text() eq '" + algo.getName() + "'").setResult("$resource");
		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
		List<GenericResource> ds = client.submit(query);
		if (ds.isEmpty()) {
			return;
		}
		GenericResource a = ds.get(0);
		a.profile().newBody(this.getAlgoBody(algo));
		try {
			scopedPublisher.update(a);
		} catch (RegistryNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	private String getAlgoBody(Algorithm algo) {
		return "<category>" + algo.getCategory() + "</category>" + "\n" + "<clazz>" + algo.getClazz() + "</clazz>"
				+ "\n" + "<algorithmType>" + algo.getAlgorithmType() + "</algorithmType>" + "\n" + "<skipJava>"
				+ algo.getSkipJava() + "</skipJava>" + "\n" + "<packageURL>" + algo.getPackageURL() + "</packageURL>"
				+ "\n" + "<dependencies>" + algo.getDependencies() + "</dependencies>";
	}

	
	
//	public void addAlgToIs(Algorithm algo) {
//		GenericResource a = new GenericResource();
//		a.newProfile().name(algo.getName()).type("StatisticalManagerAlgorithm").description(algo.getDescription());
//		a.profile().newBody(this.getAlgoBody(algo));
//		try {
//			publishScopedResource(a, Arrays.asList(new String[] { ScopeProvider.instance.get() }));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	
	public void addAlgToIs(Algorithm algo, String token) {
		GenericResource a = new GenericResource();
		a.newProfile().name(algo.getName()).type("StatisticalManagerAlgorithm").description(algo.getDescription());
		a.profile().newBody(this.getAlgoBody(algo));
		try {
			SecurityTokenProvider.instance.set(token);
			publishScopedResource(a, Arrays.asList(new String[] { SecurityTokenProvider.instance.get() }));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void unPublishScopedResource(GenericResource resource) throws RegistryNotFoundException, Exception {
		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
		AdvancedScopedPublisher advancedScopedPublisher = new AdvancedScopedPublisher(scopedPublisher);
		String id = resource.id();
		LOGGER.debug("Trying to remove {} with ID {} from {}", resource.getClass().getSimpleName(), id,
				ScopeProvider.instance.get());
		// scopedPublisher.remove(resource, scopes);
		advancedScopedPublisher.forceRemove(resource);
		LOGGER.debug("{} with ID {} removed successfully", resource.getClass().getSimpleName(), id);
	}

	public void publishScopedResource(GenericResource a, List<String> scopes)
			throws RegistryNotFoundException, Exception {
		StringWriter stringWriter = new StringWriter();
		Resources.marshal(a, stringWriter);

		ScopedPublisher scopedPublisher = RegistryPublisherFactory.scopedPublisher();
		try {
			System.out.println(scopes);
			System.out.println(stringWriter);
			scopedPublisher.create(a, scopes);
		} catch (RegistryNotFoundException e) {
			System.out.println(e);
			throw e;
		}
	}
	

//	public Set<Algorithm> getAlgoFromIs() {
//		// TODO Auto-generated method stub
//
//		Set<Algorithm> out = new HashSet<Algorithm>();
//		SimpleQuery query = queryFor(GenericResource.class);
//		query.addCondition("$resource/Profile/SecondaryType/text() eq 'StatisticalManagerAlgorithm'")
//				.setResult("$resource");
//		DiscoveryClient<GenericResource> client = clientFor(GenericResource.class);
//		List<GenericResource> ds = client.submit(query);
//		for (GenericResource a : ds) {
//			out.add(this.convertAlgo(a));
//		}
//		return out;
//	}

	
//	private Algorithm convertAlgo(GenericResource a) {
//		Algorithm out = new Algorithm();
//
//		// out.setId(a.profile().body().getElementsByTagName("id").item(0).getTextContent());
//		out.setAlgorithmType(a.profile().body().getElementsByTagName("algorithmType").item(0).getTextContent());
//		out.setCategory(a.profile().body().getElementsByTagName("category").item(0).getTextContent());
//		out.setClazz(a.profile().body().getElementsByTagName("clazz").item(0).getTextContent());
//		out.setName(a.profile().name());
//		out.setPackageURL(a.profile().body().getElementsByTagName("packageURL").item(0).getTextContent());
//		out.setSkipJava(a.profile().body().getElementsByTagName("skipJava").item(0).getTextContent());
//		out.setDescription(a.profile().description());
//
//		Set<org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency> deps = new HashSet<org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency>();
//		for (int i = 0; i < a.profile().body().getElementsByTagName("dependencies").getLength(); i++) {
//			org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency d1 = new org.gcube.dataanalysis.dataminer.poolmanager.datamodel.Dependency();
//			d1.setName(a.profile().body().getElementsByTagName("dependencies").item(i).getTextContent());
//			deps.add(d1);
//		}
//		out.setDependencies(deps);
//		return out;
//	}
	

	
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
		//System.out.println(a.getDataminer("dataminer1-d-d4s.d4science.org").getDomain());
		// System.out.println(a.listDataminersInVRE());
	}
}
