package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace.DataProvenance;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mapping.dataspace.StoredData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputsManager {

	private static Logger LOGGER = LoggerFactory.getLogger(OutputsManager.class);
	
	private AlgorithmConfiguration config;

	private List<File> generatedFiles = new ArrayList<File>();
	private List<String> generatedTables = new ArrayList<String>();
	private IClient storageclient;
	private String computationsession;
	private List<StoredData> provenanceData = new ArrayList<StoredData>();
	
	public List<StoredData> getProvenanceData() {
		return provenanceData;
	}

	public List<File> getGeneratedData() {
		return generatedFiles;
	}
	
	public List<File> getGeneratedFiles() {
		return generatedFiles;
	}

	public List<String> getGeneratedTables() {
		return generatedTables;
	}

	public OutputsManager(AlgorithmConfiguration config,String computationsession) {
		this.config = config;
		this.computationsession=computationsession;
	}

	public LinkedHashMap<String, Object> createOutput(StatisticalType prioroutput, StatisticalType posterioroutput) throws Exception {

		LinkedHashMap<String, Object> outputs = new LinkedHashMap<String, Object>();

		LOGGER.debug("Converting prior output into WPS output");
		StatisticalTypeToWPSType converter = new StatisticalTypeToWPSType();
		converter.convert2WPSType(prioroutput, false, config);
		generatedFiles.addAll(converter.getGeneratedFiles());
		generatedTables.addAll(converter.getGeneratedTables());
		LinkedHashMap<String, IOWPSInformation> priorOutput = converter.outputSet;

		LOGGER.debug("Converting posterior output into WPS output");
		StatisticalTypeToWPSType postconverter = new StatisticalTypeToWPSType();
		postconverter.convert2WPSType(posterioroutput, false, config);
		generatedFiles.addAll(postconverter.getGeneratedFiles());
		LOGGER.debug("Generated Files "+generatedFiles);
		generatedTables.addAll(postconverter.getGeneratedTables());
		LOGGER.debug("Generated Tables "+generatedFiles);
		
		LinkedHashMap<String, IOWPSInformation> postOutput = postconverter.outputSet;

		LinkedHashMap<String, IOWPSInformation> ndoutput = new LinkedHashMap<String, IOWPSInformation>();
		// merging a priori and a posteriori output
		LOGGER.debug("Merging prior and posterior output");
		if (ConfigurationManager.useStorage())
			prepareForStoring();

		for (String okey : postOutput.keySet()) {
			LOGGER.debug("Assigning output: " + okey + " to the expected output");
			IOWPSInformation postInfo = postOutput.get(okey);
			// search for the best prior matching the output
			IOWPSInformation info = priorOutput.get(okey);
			if (info == null) {
				// if the output was not defined a priori occupy a suitable slot
				// if not yet occupied
				for (String priorPName : priorOutput.keySet()) {
					// check if the slot for this output had been yet occupied
					if (outputs.get(priorPName) == null && priorPName.startsWith(postInfo.getClassname())) {
						okey = priorPName;
						info = priorOutput.get(priorPName);
						break;
					}
				}

			}

			// this check filters out the containers of sub elements
			if (postInfo != null && postInfo.getContent() != null) {
				if (ConfigurationManager.useStorage()) {
					if (postInfo.getLocalMachineContent() != null) {
						// return the url from storage manager
						String storageurl = uploadFileOnStorage(postInfo.getLocalMachineContent(), postInfo.getMimetype());
						postInfo.setContent(storageurl);
					}
				}
				/*
				else if (postInfo.getLocalMachineContent() != null) {
					String url = "<wps:Reference mimeType=\""+postInfo.getMimetype()+"\" xlink:href=\""+postInfo.getContent()+"\" method=\"GET\"/>";					
					LOGGER.debug("Reference URL: " + url);
					outputs.put(okey, url);
				}
				else*/ 
				if (info != null) {
					LOGGER.debug("Found a corresponding output: " + okey);
					outputs.put(okey, postInfo.getContent());
					//add link to the file also among the non deterministic output
					if (postInfo.getLocalMachineContent() != null) {
						ndoutput.put(okey, postInfo);
					}
				} else {
					LOGGER.debug("Output was not expected: " + okey);
					ndoutput.put(okey, postInfo);
				}
				saveProvenanceData(postInfo);
			}

			System.gc();
		}
		
		XmlObject ndxml = generateNonDeterministicOutput(ndoutput);
		outputs.put("non_deterministic_output", ndxml);
		
		//safety check for declared output, i.e. a priori output
		for (String pkey:priorOutput.keySet()){
			if (outputs.get(pkey)==null){
				LOGGER.debug("Safety check: adding empty string for " + pkey+ " of type "+priorOutput.get(pkey).getClassname());
				outputs.put(pkey, "");
			}
		}
		LOGGER.debug("OutputsManager outputs "+outputs);
		return outputs;
	}

	
	private void saveProvenanceData(IOWPSInformation info){
		String name = info.getName();
		String id = info.getName();
		DataProvenance provenance = DataProvenance.COMPUTED;
		String creationDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(System.currentTimeMillis());
		String operator = config.getAgent();
		String computationId = computationsession;
		String type = info.getMimetype();
/*		if (info.getLocalMachineContent() != null) {
			type = StoredType.DATA;
		}
		*/
		String payload = info.getContent();
		
		StoredData data = new StoredData(name, info.getAbstractStr(),id, provenance, creationDate, operator, computationId, type,payload,config.getGcubeScope());
		
		provenanceData.add(data);
	}
	
	private void prepareForStoring() {
		LOGGER.debug("Preparing storage client");
		//String scope = config.getGcubeScope();
		//ScopeProvider.instance.set(scope);
		String serviceClass = "WPS";
		String serviceName = "wps.synch";
		String owner = config.getParam(ConfigurationManager.serviceUserNameParameterVariable);
		storageclient = new StorageClient(serviceClass, serviceName, owner, AccessType.SHARED, MemoryType.VOLATILE).getClient();
		LOGGER.debug("Storage client ready");
	}
	
	
	private String uploadFileOnStorage(String localfile, String mimetype) throws Exception {
		LOGGER.debug("Storing->Start uploading on storage the following file: " + localfile);
		File localFile = new File(localfile);
		String remotef = "/wps_synch_output/" +config.getAgent()+"/"+computationsession+"/"+ localFile.getName();
		storageclient.put(true).LFile(localfile).RFile(remotef);
		
		String url = storageclient.getHttpsUrl().RFile(remotef);
		
		/*
		if (config.getGcubeScope().startsWith("/gcube"))
			url = "http://data-d.d4science.org/uri-resolver/smp?smp-uri=" + url + "&fileName=" + localFile.getName() + "&contentType=" + mimetype;
		else
			url = "http://data.d4science.org/uri-resolver/smp?smp-uri=" + url+ "&fileName=" + localFile.getName() + "&contentType=" + mimetype;
			*/
		LOGGER.info("Storing->Uploading finished - URL: " + url);
		return url;

	}
	
	public String cleanTagString(String tag) {
		return tag.replace(" ", "_").replaceAll("[\\]\\[!\"#$%&'()*+,\\./:;<=>?@\\^`{|}~-]", "");
	}

	public XmlObject generateNonDeterministicOutputPlain(LinkedHashMap<String, IOWPSInformation> ndoutput) throws Exception {
		String XMLString = "<gml:featureMember xmlns:gml=\"http://www.opengis.net/gml\" xmlns:d4science=\"http://www.d4science.org\">\n" + "	<d4science:output fid=\"outputcollection\">\n";
		for (String key : ndoutput.keySet()) {
			IOWPSInformation info = ndoutput.get(key);
			String payload = info.getContent();
			String mimetype = info.getMimetype();
			XMLString += "		<d4science:k_" + cleanTagString(key) + ">" + "			<d4science:Data><![CDATA[" + payload + "]]></d4science:Data>\n" + "			<d4science:Description><![CDATA[" + (info.getAbstractStr() != null ? info.getAbstractStr() : "") + "]]></d4science:Description>\n" + "			<d4science:MimeType>" + mimetype + "</d4science:MimeType>\n" + "		</d4science:k_" + cleanTagString(key) + ">\n";
		}
		XMLString += "	</d4science:output>\n" + "</gml:featureMember>\n";

		LOGGER.debug("Non deterministic output: " + XMLString);

		XmlObject xmlData = XmlObject.Factory.newInstance();
		ByteArrayInputStream xstream = new ByteArrayInputStream(XMLString.getBytes());
		xmlData = XmlObject.Factory.parse(xstream);
		LOGGER.debug("Output has been correctly parsed");

		return xmlData;
	}

	public XmlObject generateNonDeterministicOutputCollection(LinkedHashMap<String, IOWPSInformation> ndoutput) throws Exception {
		String XMLString = "<ogr:FeatureCollection xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ogr.maptools.org/ result_8751.xsd\" xmlns:ogr=\"http://ogr.maptools.org/\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:d4science=\"http://www.d4science.org\">" +
				"\n<gml:featureMember>\n" + " <ogr:Result fid=\"F0\">\n" +
						"	<d4science:output fid=\"outputcollection\">\n";
		
		for (String key : ndoutput.keySet()) {
			IOWPSInformation info = ndoutput.get(key);
			String payload = info.getContent();
			String mimetype = info.getMimetype();
			XMLString += "		<d4science:k_" + cleanTagString(key) + ">" + "			<d4science:Data><![CDATA[" + payload + "]]></d4science:Data>\n" + "			<d4science:Description><![CDATA[" + (info.getAbstractStr() != null ? info.getAbstractStr() : "") + "]]></d4science:Description>\n" + "			<d4science:MimeType>" + mimetype + "</d4science:MimeType>\n" + "		</d4science:k_" + cleanTagString(key) + ">\n";
		}
		XMLString += "	</d4science:output>\n" + "  </ogr:Result>\n</gml:featureMember>\n</ogr:FeatureCollection>";

		LOGGER.debug("Non deterministic output: " + XMLString);

		XmlObject xmlData = XmlObject.Factory.newInstance();
		ByteArrayInputStream xstream = new ByteArrayInputStream(XMLString.getBytes());
		xmlData = XmlObject.Factory.parse(xstream);
		LOGGER.debug("Output has been correctly parsed");

		return xmlData;
	}
	
	public XmlObject generateNonDeterministicOutput(LinkedHashMap<String, IOWPSInformation> ndoutput) throws Exception {
		
		if (ndoutput.size()==0)
			return null;
		
		String XMLString = "<ogr:FeatureCollection xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://ogr.maptools.org/ result_8751.xsd\" xmlns:ogr=\"http://ogr.maptools.org/\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:d4science=\"http://www.d4science.org\">" +
				"\n<gml:featureMember>\n";
		int count = 0;
		for (String key : ndoutput.keySet()) {
			IOWPSInformation info = ndoutput.get(key);
			String payload = info.getContent();
			String mimetype = info.getMimetype();
			String abstractStr = info.getAbstractStr();
			
			LOGGER.debug("IOWPS Information [name="+info.getName()+", abstr="+info.getAbstractStr()+", content="+info.getContent()+", def="+info.getDefaultVal()+"]");
			
			if ((abstractStr==null || abstractStr.trim().length()==0) && (payload!= null && payload.trim().length()>0))
				abstractStr = info.getName();
			else if (abstractStr == null)
				abstractStr = "";
			
			//geospatialized
//			XMLString += "		<ogr:Result fid=\"F" + count+ "\">" + "<ogr:geometryProperty><gml:Point><gml:coordinates>0,0</gml:coordinates></gml:Point></ogr:geometryProperty>"+ "			<d4science:Data><![CDATA[" + payload + "]]></d4science:Data>\n" + "			<d4science:Description><![CDATA[" + (info.getAbstractStr() != null ? info.getAbstractStr() : "") + "]]></d4science:Description>\n" + "			<d4science:MimeType>" + mimetype + "</d4science:MimeType>\n" + "		</ogr:Result>\n";
			XMLString += "		<ogr:Result fid=\"F" + count+ "\">" + "			<d4science:Data><![CDATA[" + payload + "]]></d4science:Data>\n" + "			<d4science:Description><![CDATA[" + abstractStr + "]]></d4science:Description>\n" + "			<d4science:MimeType>" + mimetype + "</d4science:MimeType>\n" + "		</ogr:Result>\n";
			count++;
		}
		XMLString += "	</gml:featureMember>\n</ogr:FeatureCollection>";
		
		
		
		LOGGER.debug("Non deterministic output: " + XMLString);

		XmlObject xmlData = XmlObject.Factory.newInstance();
		ByteArrayInputStream xstream = new ByteArrayInputStream(XMLString.getBytes());
		xmlData = XmlObject.Factory.parse(xstream);
		LOGGER.debug("Output has been correctly parsed");

		return xmlData;
	}
	
	public void shutdown(){
		try{
		storageclient.close();
		}catch(Exception e){
			
		}
	}
}
