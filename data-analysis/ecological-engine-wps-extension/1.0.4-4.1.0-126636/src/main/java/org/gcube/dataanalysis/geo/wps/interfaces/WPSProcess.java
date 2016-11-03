package org.gcube.dataanalysis.geo.wps.interfaces;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.opengis.wps.x100.ComplexDataType;
import net.opengis.wps.x100.ExecuteResponseDocument.ExecuteResponse.ProcessOutputs;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDataType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataInputType;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube.dataanalysis.geo.wps.client.WPSClient;
import org.gcube.dataanalysis.geo.wps.factory.DynamicWPSTransducerer;

public class WPSProcess implements Transducerer {

	public String wpsurl;
	public String processid;
	public String title;
	public String processAbstract;
	private List<StatisticalType> inputTypes;
	private LinkedHashMap<String, StatisticalType> outputTypes;
	private InputDescriptionType[] wpsInputs;
	private OutputDescriptionType[] wpsOutputs;
	private ProcessDescriptionType processDescription;
	private WPSClient currentProcess;
	
	public static Map<String,WPSClient> inputsCache;
	
	protected ResourceFactory resourceManager;
	private AlgorithmConfiguration config;
	float status = 0;

	public WPSProcess(String wpsurl, String processid) {
		this.wpsurl = wpsurl;
		this.processid = processid;
	}

	public void compute() throws Exception {
		status = 0;
		try {
			// setup the inputs
			org.n52.wps.client.ExecuteRequestBuilder executeBuilder = new org.n52.wps.client.ExecuteRequestBuilder(processDescription);
			// for each input
			for (InputDescriptionType input : wpsInputs) {
				// retrieve the input from the sm config
				String value = config.getParam(input.getIdentifier().getStringValue());
				
				if (value != null && value.trim().length()>0) {
					String [] values = value.split(AlgorithmConfiguration.getListSeparator());
					if (values.length==0){
						values = new String[1];
						values[0] = value;
					}
					// fulfill an input obj for the execution
					if (input.isSetLiteralData()) {
						AnalysisLogger.getLogger().debug("Configuring Literal: " + input.getIdentifier().getStringValue() + " to: " + value);
						for (String v:values){
							executeBuilder.addLiteralData(input.getIdentifier().getStringValue(), v);
						}
					} else if (input.isSetBoundingBoxData()) {
						AnalysisLogger.getLogger().debug("Configuring Bounding Box: " + input.getIdentifier().getStringValue() + " to: " + value);
						AnalysisLogger.getLogger().debug(input);
						for (String v:values){
							WPSClient.addBoundingBoxInput(executeBuilder, input.getIdentifier().getStringValue(), v);
						}
					} else {
						AnalysisLogger.getLogger().debug("Configuring Complex: " + input.getIdentifier().getStringValue() + " to: " + value);
						SupportedComplexDataInputType complex = input.getComplexData();
						for (String v:values){
							executeBuilder.addComplexDataReference(input.getIdentifier().getStringValue(), v, complex.getDefault().getFormat().getSchema(), complex.getDefault().getFormat().getEncoding(), complex.getDefault().getFormat().getMimeType());
						}
					}
				}
			}
			// Submit the execution
			WPSClient client = new WPSClient(wpsurl);
			currentProcess=client;
			AnalysisLogger.getLogger().debug("Starting Process");
			ProcessOutputs outs = client.executeProcess(executeBuilder, processDescription);
			AnalysisLogger.getLogger().debug("Process Executed");
			// retrieve the output objs
			if (outs == null)
				throw new Exception("Error during the execution of the WPS process: returned an empty document");
			else {
				OutputDataType[] outputData = outs.getOutputArray();

				for (OutputDataType out : outputData) {
					String outputID = out.getIdentifier().getStringValue();
					String value = "";
					if (out.getData().isSetLiteralData()) {
						value = out.getData().getLiteralData().getStringValue();
						StatisticalType stype = outputTypes.get(outputID);
						if (stype != null){
							((PrimitiveType) stype).setContent(value);
							AnalysisLogger.getLogger().debug("Assigning value: " + value + " to output named: " + outputID);
						}
					} else if (out.getData().isSetComplexData()) {
						if (out.getReference() != null) {
							value = out.getReference().getHref();
							StatisticalType stype = outputTypes.get(outputID);
							
							if (stype != null){
								((PrimitiveType) stype).setContent(value);
								AnalysisLogger.getLogger().debug("Assigning value: " + value + " to output named: " + outputID);
							}
						}
						else
							//remove the element name, which is not useful
							outputTypes.remove(outputID);
						
						ComplexDataType cdt = out.getData().getComplexData();
						List<String> urls = WPSClient.retrieveURLsFromWPSResponse(cdt);
						
						int counter = 1;
						for (String url : urls) {
							AnalysisLogger.getLogger().debug("Adding OBJ:" + url);
							outputTypes.put("OUT_" + counter, new PrimitiveType(String.class.getName(), url, PrimitiveTypes.STRING, "OUT", "", ""));
							counter++;
						}
					} else {
						value = out.getData().getLiteralData().getStringValue();

					}
				}
			}

		} catch (Exception e) {
			String exceptiontext = WPSClient.getExceptionText(e.getLocalizedMessage());
			AnalysisLogger.getLogger().debug("WPSProcess->Returning Exception to the SM:" + exceptiontext);
			throw new Exception(exceptiontext);
		} finally {
			status = 100;
		}
	}

	public void init() throws Exception {
		WPSClient wpsclient = null;
		if (inputsCache!=null)
			wpsclient = inputsCache.get(processid);
		else
			inputsCache=new HashMap<String, WPSClient>();
		if (wpsclient ==null)
		{
			// here we build the WPS process by means of the client
			try{
			wpsclient = new WPSClient(wpsurl);
			wpsclient.describeProcess(processid);
			inputsCache.put(processid, wpsclient);
			}catch(Exception e){
				AnalysisLogger.getLogger().debug("Error in getting process description with ID: "+processid);
			}
		}
		
		inputTypes = wpsclient.getCurrentInputStatisticalTypes();
		outputTypes = wpsclient.getCurrentOutputStatisticalTypes();
		wpsInputs = wpsclient.getCurrentInputs();
		wpsOutputs = wpsclient.getCurrentOutputs();
		title = wpsclient.getCurrentProcessTitle();
		String wpsServiceIndication = wpsurl.substring("http://".length());
		int idxcolumn =  wpsServiceIndication.indexOf(":");
		int idxslash =  wpsServiceIndication.indexOf("/");
		int idx = -1;
		if (idxcolumn>0 && idxslash>0) 
			idx = Math.min(idxslash, idxcolumn);
		else if (idxcolumn>0)
			idx = idxcolumn;
		else if (idxslash>0)
			idx = idxslash;
		else
			idx = Math.min(5, wpsServiceIndication.length());
		
		if (idx>0)
			wpsServiceIndication = wpsServiceIndication.substring(0,idx);
			
		processAbstract = wpsServiceIndication+": "+wpsclient.getCurrentProcessAbstract();
		AnalysisLogger.getLogger().debug("Process Abstract: "+processAbstract);
		processDescription = wpsclient.getProcessDescription();

		if ( DynamicWPSTransducerer.isTooMuchTime()){
			inputsCache=null;
			System.gc();
		}
		
			
	}

	public String getDescription() {
		return processAbstract;
	}

	public List<StatisticalType> getInputParameters() {
		return inputTypes;
	}

	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.EXTERNAL_WPS;
	}

	public StatisticalType getOutput() {
		// start from the declared outputs and fulfill the objects
		PrimitiveType output = new PrimitiveType(HashMap.class.getName(), outputTypes, PrimitiveTypes.MAP, "Results", "Results");
		return output;
	}

	public String getResourceLoad() {
		if (resourceManager == null)
			resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	public String getResources() {
		return ResourceFactory.getResources(100f);
	}

	public float getStatus() {
		if (status ==100f)
			return status;
		else if (currentProcess!=null)
			return Math.min(currentProcess.wpsstatus, 90f);
		else
			return status;
	}

	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}

	public void shutdown() {

	}

}
