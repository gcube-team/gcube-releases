package org.gcube.execution.workfloworchestrationlayerservice.wrappers;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.workflow.adaptor.WorkflowJDLAdaptor;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AdaptorJDLResources;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.AttachedResourceType;
import gr.uoa.di.madgik.workflow.adaptor.utils.jdl.AttachedJDLResource.ResourceType;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.AccessInfo;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.JDLConfig;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.JDLParams;
import gr.uoa.di.madgik.workflow.client.library.beans.Types.JDLResource;
import gr.uoa.di.madgik.workflow.client.library.proxies.WorkflowEngineCLProxyI;
import gr.uoa.di.madgik.workflow.client.library.proxies.WorkflowEngineDSL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.execution.workfloworchestrationlayerservice.utils.FileInfo;
import org.gcube.execution.workfloworchestrationlayerservice.utils.FileInfo.LocationType;
import org.gcube.execution.workfloworchestrationlayerservice.utils.WorkflowOrchestrationLayer;
import org.gcube.execution.workfloworchestrationlayerservice.utils.WorkflowOrchestrationLayer.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDLAdaptor extends AdaptorBase {

	private static Logger logger = LoggerFactory.getLogger(JDLAdaptor.class);

	private static String pattern = "http://(.+):([0-9]+).*";
	private static Pattern r = Pattern.compile(pattern);

	
	public static HashMap<String, String> ParseResourceFile(String file) throws Exception {
		File f = new File(file);
		if (!f.exists() || !f.isFile()) {
			logger.error("Specified resource file (" + file + ") not found");
			throw new Exception("Specified resource file (" + file + ") not found");
		}
		BufferedReader r = new BufferedReader(new FileReader(f));
		HashMap<String, String> res = new HashMap<String, String>();
		while (true) {
			String line = r.readLine();
			if (line == null)
				break;
			String parts[] = line.trim().split("\\s#\\s");
			if (parts.length != 2)
				continue;
			res.put(parts[0].trim(), parts[1].trim());
		}
		return res;
	}

	public static FileInfo ParseGlobalOutputStoreMode(String file) throws Exception {
		File f = new File(file);
		if (!f.exists() || !f.isFile())
			throw new Exception("Specified resource file (" + file + ") not found");
		FileInfo nfo = null;
		BufferedReader r = new BufferedReader(new FileReader(f));
		while (true) {
			String line = r.readLine();
			if (line == null)
				break;
			String parts[] = line.trim().split("\\s#\\s");
			if (parts.length != 2 && parts.length != 3 && parts.length != 5)
				continue;
			if (parts[0].trim().equalsIgnoreCase("outputStoreMode")) {
				nfo = new FileInfo();
				if (parts.length == 2 && parts[1].trim().equals(LocationType.ss.toString()))
					nfo.TypeOfLocation = LocationType.ss;
				else {
					nfo.TypeOfLocation = FileInfo.LocationType.valueOf(parts[1].trim());
					if (nfo.TypeOfLocation.equals(LocationType.url)) {
						if (parts.length == 3) {
							nfo.Value = AdaptorBase.StripUrlPort(AdaptorBase.StripUrlUserInfo(parts[2].trim()));
							nfo.AccessInfo = AdaptorBase.ParseUrlAccessInfo(parts[2].trim());
						} else {
							nfo.Value = AdaptorBase.StripUrlPort(parts[2].trim());
							nfo.AccessInfo = AdaptorBase.ParseUrlAccessInfo(parts[2].trim());
							nfo.AccessInfo.userId = parts[3].trim();
							nfo.AccessInfo.password = parts[4].trim();
						}
					}
				}
			}
		}
		if (nfo == null) {
			nfo = new FileInfo();
			nfo.TypeOfLocation = LocationType.ss;
		}
		return nfo;
	}

	public static HashMap<String, FileInfo> ParseInData(String file) throws Exception {
		File f = new File(file);
		if (!f.exists() || !f.isFile())
			throw new Exception("Specified resource file (" + file + ") not found");
		HashMap<String, FileInfo> resource = new HashMap<String, FileInfo>();
		BufferedReader r = new BufferedReader(new FileReader(f));
		while (true) {
			String line = r.readLine();
			if (line == null)
				break;
			String parts[] = line.trim().split("\\s#\\s");
			if (parts.length != 4)
				continue;
			if (!parts[0].trim().equalsIgnoreCase("inData"))
				continue;
			FileInfo nfo = new FileInfo();
			nfo.TypeOfLocation = FileInfo.LocationType.valueOf(parts[2].trim());
			nfo.Value = parts[3].trim();
			if (parts[0].trim().equalsIgnoreCase("inData"))
				resource.put(parts[1].trim(), nfo);
		}
		return resource;
	}

	public static HashMap<String, FileInfo> ParseOutData(String file, FileInfo globalOutputStoreMode) throws Exception {
		File f = new File(file);
		if (!f.exists() || !f.isFile())
			throw new Exception("Specified resource file (" + file + ") not found");
		HashMap<String, FileInfo> resource = new HashMap<String, FileInfo>();
		BufferedReader r = new BufferedReader(new FileReader(f));
		while (true) {
			String line = r.readLine();
			if (line == null)
				break;
			String parts[] = line.trim().split("\\s#\\s");
			if (parts.length < 2)
				continue;
			if (!parts[0].trim().equalsIgnoreCase("outData"))
				continue;
			FileInfo nfo = null;
			if (parts.length == 2 && globalOutputStoreMode != null) {
				nfo = new FileInfo();
				nfo.TypeOfLocation = globalOutputStoreMode.TypeOfLocation;
				nfo.Value = globalOutputStoreMode.Value;
			}
			if (parts.length == 3 && parts[2].equals(LocationType.ss.toString())) {
				nfo = new FileInfo();
				nfo.TypeOfLocation = LocationType.ss;
			} else if (parts.length > 3) {
				nfo = new FileInfo();
				nfo.TypeOfLocation = FileInfo.LocationType.valueOf(parts[2].trim());
			}
			if (nfo.TypeOfLocation.equals(LocationType.url)) {
				if (parts.length != 4 && parts.length != 6)
					continue;
				if (parts.length == 4) {
					nfo.Value = AdaptorBase.StripUrlPort(AdaptorBase.StripUrlUserInfo(parts[3].trim()));
					nfo.AccessInfo = AdaptorBase.ParseUrlAccessInfo(parts[3].trim());
				} else {
					nfo.Value = AdaptorBase.StripUrlPort(parts[3].trim());
					nfo.AccessInfo = AdaptorBase.ParseUrlAccessInfo(parts[3].trim());
					nfo.AccessInfo.userId = parts[4].trim();
					nfo.AccessInfo.password = parts[5].trim();
				}
			}
			if (parts[0].trim().equalsIgnoreCase("outData"))
				resource.put(parts[1].trim(), nfo);
		}
		List<String> defaultInfo = new ArrayList<String>();
		for (Map.Entry<String, FileInfo> outData : resource.entrySet()) {
			if (outData.getValue() == null)
				defaultInfo.add(outData.getKey());
		}
		for (String d : defaultInfo) {
			FileInfo nfo = new FileInfo();
			nfo.TypeOfLocation = LocationType.ss;
			resource.put(d, nfo);
		}

		return resource;
	}

	public static void createPlan(String[] args) throws Exception {
		if (args.length < 4) {
			JDLAdaptor.PrintHelp();
			return;
		} else {
			JDLAdaptor.Init();
		}

		HashMap<String, String> resources = JDLAdaptor.ParseResourceFile(args[2]);
		FileInfo globalOutputStoreMode = JDLAdaptor.ParseGlobalOutputStoreMode(args[2]);
		HashMap<String, FileInfo> inDataResources = JDLAdaptor.ParseInData(args[2]);
		HashMap<String, FileInfo> outDataResources = JDLAdaptor.ParseOutData(args[2], globalOutputStoreMode);

		AdaptorJDLResources attachedResources = new AdaptorJDLResources();
		for (Map.Entry<String, String> res : resources.entrySet()) {
			if (res.getKey().equals("jdl"))
				continue;
			else if (res.getKey().equals("chokeProgressEvents"))
				continue;
			else if (res.getKey().equals("chokePerformanceEvents"))
				continue;
			else if (res.getKey().equals("storePlans"))
				continue;
			else if (res.getKey().equals("scope"))
				continue;
		}

		if (!resources.containsKey("jdl"))
			throw new Exception("no jdl attribute specified");
		if (!resources.containsKey("chokeProgressEvents"))
			throw new Exception("no chokeProgressEvents attribute specified");
		if (!resources.containsKey("chokePerformanceEvents"))
			throw new Exception("no chokePerformanceEvents attribute specified");
		if (!resources.containsKey("storePlans"))
			throw new Exception("no chokePerformanceEvents attribute specified");

		for (Map.Entry<String, FileInfo> res : inDataResources.entrySet()) {
			AttachedResourceType type = null;
			switch (res.getValue().TypeOfLocation) {
			case local: {
				type = AttachedResourceType.LocalFile;
				break;
			}
			case ss: {
				type = AttachedResourceType.CMSReference;
				break;
			}
			case url: {
				type = AttachedResourceType.Reference;
				break;
			}
			}
			attachedResources.Resources.add(new AttachedJDLResource(res.getKey(), ResourceType.InData, res.getValue().Value, type));
		}

		for (Map.Entry<String, FileInfo> res : outDataResources.entrySet()) {
			AttachedResourceType type = null;
			switch (res.getValue().TypeOfLocation) {
			case ss: {
				type = AttachedResourceType.CMSReference;
				break;
			}
			case url: {
				type = AttachedResourceType.Reference;
				break;
			}
			}
			attachedResources.Resources.add(new AttachedJDLResource(res.getKey(), ResourceType.OutData, res.getValue().Value, type));
		}

		WorkflowJDLAdaptor adaptor = new WorkflowJDLAdaptor();
		if (resources.containsKey("scope"))
			adaptor.ConstructEnvironmentHints(resources.get("scope"));
		adaptor.SetAdaptorResources(attachedResources);
		adaptor.SetJDL(new File(resources.get("jdl")));
		adaptor.CreatePlan();
		adaptor.GetCreatedPlan().Config.ChokeProgressReporting = Boolean.parseBoolean(resources.get("chokeProgressEvents"));
		adaptor.GetCreatedPlan().Config.ChokePerformanceReporting = Boolean.parseBoolean(resources.get("chokePerformanceEvents"));

		if (Boolean.parseBoolean(resources.get("storePlans"))) {
			File tmp = File.createTempFile(UUID.randomUUID().toString(), ".test.jdl.adaptor.original.plan.xml");
			XMLUtils.Serialize(tmp.toString(), adaptor.GetCreatedPlan().Serialize());
			logger.info("Initial plan is stored at " + tmp.toString());
		}
	}

	private static void PrintHelp() {
		StringBuilder buf = new StringBuilder();
		buf.append("Usage:\n");
		buf.append("Two arguments are needed\n");
		buf.append("1) the path of the resource file. The syntax of the resource file is the following:\n");
		buf.append("\tscope : <the scope to use>\n");
		buf.append("\tjdl : <path to the jdl file>\n");
		buf.append("\tchokeProgressEvents : <true | false> (depending on whether you want to omit progress reporting)\n");
		buf.append("\tchokePerformanceEvents : <true | false> (depending on whether you want to omit performance reporting)\n");
		buf.append("\tstorePlans : <true | false> (depending on whether you want the plan created and the final one to be stored for inspection)\n");
		buf.append("\t<name of resource as mentioned in jdl> : <local | ss | url depending on where to access the payload from> : <the path / id / url to retrieve the paylaod from>\n");
		buf.append("\t<name of resource as mentioned in jdl> : <local | ss | url depending on where to access the payload from> : <the paath / id / url to retrieve the paylaod from>\n");
		buf.append("\t[...]");
		//buf.append("2) the path of the output file that will contain the execution identifier\n");
		logger.info(buf.toString());
	}

	public String execute(String[] args) {
		{
			if (args.length != 1) {
				JDLAdaptor.PrintHelp();
				return null;
			}
			logger.info("resources file used : " + args[0]);

			HashMap<String, String> resources = null;
			HashMap<String, FileInfo> inDataResources = null;
			HashMap<String, FileInfo> outDataResources;
			try {
				resources = JDLAdaptor.ParseResourceFile(args[0]);
				FileInfo globalOutputStoreMode = JDLAdaptor.ParseGlobalOutputStoreMode(args[0]);
				inDataResources = JDLAdaptor.ParseInData(args[0]);
				outDataResources = JDLAdaptor.ParseOutData(args[0], globalOutputStoreMode);
			} catch (Exception e) {
				logger.error("Exception", e);
				return null;
			}

			JDLParams params = new JDLParams();

			JDLConfig conf = new JDLConfig();
			if (resources.containsKey("chokePerformanceEvents"))
				conf.chokePerformanceEvents = Boolean.parseBoolean(resources.get("chokePerformanceEvents"));
			else
				conf.chokePerformanceEvents = false;
			if (resources.containsKey("chokeProgressEvents"))
				conf.chokeProgressEvents = Boolean.parseBoolean(resources.get("chokeProgressEvents"));
			else
				conf.chokeProgressEvents = false;
			if (resources.containsKey("queueSupport"))
				conf.queueSupport = Boolean.parseBoolean(resources.get("queueSupport"));
			else
				conf.queueSupport = false;
			if (resources.containsKey("utilization"))
				conf.utilization = Float.parseFloat(resources.get("utilization"));
			else
				conf.utilization = 0.1f;
			if (resources.containsKey("passedBy"))
				conf.passedBy = Integer.parseInt(resources.get("passedBy"));
			else
				conf.passedBy = 1;
			params.config = conf;
			try {
				params.jdlDescription = AdaptorBase.GetStringFilePayload(resources.get("jdl"));
			} catch (IOException e) {
				logger.error("Exception", e);
			}

			List<JDLResource> resourceslst = new ArrayList<JDLResource>();
			for (Map.Entry<String, FileInfo> res : inDataResources.entrySet()) {
				JDLResource r = new JDLResource();
				r.resourceType = "InData";
				r.resourceKey = res.getKey();
				switch (res.getValue().TypeOfLocation) {
				case local: {
					try {
						r.inMessageBytePayload = AdaptorBase.GetByteFilePayload(res.getValue().Value);
					} catch (IOException e) {
						logger.error("Exception", e);
						return null;
					}
					r.resourceAccess = "InMessageBytes";
					break;
				}
				case ss: {
					r.resourceReference = res.getValue().Value;
					r.resourceAccess = "CMSReference";
					break;
				}
				case url: {
					r.resourceReference = res.getValue().Value;
					r.resourceAccess = "Reference";
					break;
				}
				}
				resourceslst.add(r);
			}

			for (Map.Entry<String, FileInfo> res : outDataResources.entrySet()) {
				JDLResource r = new JDLResource();
				r.resourceKey = res.getKey();
				r.resourceType = "OutData";
				switch (res.getValue().TypeOfLocation) {
				case ss: {
					r.resourceReference = res.getValue().Value;
					r.resourceAccess = "CMSReference";
					break;
				}
				case url: {
					r.resourceReference = res.getValue().Value;
					r.resourceAccess = "Reference";
					AccessInfo ai = new AccessInfo();
					ai.password = res.getValue().AccessInfo.password;
					ai.port = new Integer(res.getValue().AccessInfo.port).toString();
					ai.userId = res.getValue().AccessInfo.userId;
					r.resourceAccessInfo = ai;
					break;
				}
				}
				resourceslst.add(r);
			}
			params.jdlResources = resourceslst;
			try {
				String scope = resources.get("scope");
				ScopeProvider.instance.set(scope);
				logger.info("Locating Workflow Engine");
				String endpoint = WorkflowOrchestrationLayer.getEndpoint(scope, Type.PE2NG);
				Matcher m = r.matcher(endpoint);
				String domain = null, port = null;
				if (m.find()) {
					domain = m.group(1);
					port = m.group(2);
				} else {
					logger.error("Failed to parse the endpoint");
					return null;
				}
				logger.info("Selected Workflow Engine " + endpoint+" "+domain+" "+port);
				WorkflowEngineCLProxyI proxy = WorkflowEngineDSL.getWorkflowEngineProxyBuilder().at(domain,Integer.parseInt(port)).build();
				logger.info("At " + new Date().toString() + " Submiting execution");
				String executionID = proxy.adaptJDL(params);
				logger.info("At " + new Date().toString() + " Execution ID : " + executionID);
				return executionID;
			} catch (Exception e) {
				logger.error("Exception", e);
				e.printStackTrace();
			}
		}
		return null;
	}
}