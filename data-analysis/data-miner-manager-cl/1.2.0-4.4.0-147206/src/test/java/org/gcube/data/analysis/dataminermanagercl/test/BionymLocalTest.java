package org.gcube.data.analysis.dataminermanagercl.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.gcube.data.analysis.dataminermanagercl.server.DataMinerService;
import org.gcube.data.analysis.dataminermanagercl.server.dmservice.SClient;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitor;
import org.gcube.data.analysis.dataminermanagercl.server.monitor.DMMonitorListener;
import org.gcube.data.analysis.dataminermanagercl.shared.Constants;
import org.gcube.data.analysis.dataminermanagercl.shared.data.OutputData;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationId;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.MapResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.EnumParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class BionymLocalTest extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(BionymLocalTest.class);

	public void testExecute() {

		if (Constants.TEST_ENABLE) {
			logger.debug("Test BIONYM_LOCAL");
			try {
				DataMinerService dataMinerService = new DataMinerService();
				SClient sClient = dataMinerService.getClient();
				Operator operator = sClient
						.getOperatorById(OperatorId.BIONYM_LOCAL.toString());

				if (operator == null) {
					logger.error("Operator not found");
				} else {
					logger.debug("Operator Name: " + operator.getName()
							+ " (" + operator.getId() + ")");
					logger.debug("Operator: " + operator);
					List<Parameter> parameters = sClient
							.getInputParameters(operator);
					logger.debug("Parameters: " + parameters);
					for (Parameter parameter : parameters) {
						logger.debug("Parameter:[Name=" + parameter.getName()
								+ ", Typology=" + parameter.getTypology() + "]");
					}
					
					createRequest(operator);
					logger.debug("Start Computation");
					ComputationId computationId = sClient
							.startComputation(operator);
					logger.debug("Started ComputationId: " + computationId);
					monitoringComputation(computationId, sClient);
				
				}

				assertTrue("Success", true);

			} catch (Throwable e) {
				logger.error(e.getLocalizedMessage());
				e.printStackTrace();
				assertTrue("Error", false);
			}

		} else {
			assertTrue("Success", true);
		}
	}

	private void monitoringComputation(final ComputationId computationId,
			final SClient sClient) {

		DMMonitorListener listener = new DMMonitorListener() {

			@Override
			public void running(double percentage) {
				logger.debug("Operation Running: " + percentage);

			}

			@Override
			public void failed(String message, Exception exception) {
				logger.error("Operation Failed");
				logger.error(message, exception);

			}

			@Override
			public void complete(double percentage) {
				logger.debug("Operation Completed");
				logger.debug("Perc: " + percentage);
				retrieveOutput(computationId, sClient);

			}

			@Override
			public void cancelled() {
				logger.debug("Operation Cancelled");
			}

			@Override
			public void accepted() {
				logger.debug("Operation Accepted");

			}
		};

		DMMonitor dmMonitor = new DMMonitor(computationId, sClient);
		dmMonitor.add(listener);
		dmMonitor.start();

	}

	private void retrieveOutput(ComputationId computationId, SClient sClient) {
		try {
			OutputData output = sClient
					.getOutputDataByComputationId(computationId);
			logger.debug("Output: " + output);
			Resource resource = output.getResource();
			if (resource.isMap()) {
				MapResource mapResource = (MapResource) resource;
				for (String key : mapResource.getMap().keySet()) {
					logger.debug("Entry: " + key + " = "
							+ mapResource.getMap().get(key));
				}
			}

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
		}

	}

	/**
	 *
	 * @param operator
	 */
	private void createRequest(Operator operator) {
		logger.debug("Create Request");

		ObjectParameter speciesAuthorName = new ObjectParameter();
		speciesAuthorName.setName("SpeciesAuthorName");
		speciesAuthorName.setValue("Gadus morhua (Linnaeus, 1758)");

		EnumParameter taxaAuthorityFile=new EnumParameter();
		taxaAuthorityFile.setName("Taxa_Authority_File");
		taxaAuthorityFile.setValue("FISHBASE");

		EnumParameter parserName=new EnumParameter();
		parserName.setName("Parser_Name");
		parserName.setValue("SIMPLE");
		
		ObjectParameter activatePreparsingProcessing = new ObjectParameter();
		activatePreparsingProcessing.setName("Activate_Preparsing_Processing");
		activatePreparsingProcessing.setValue("true");
		

		ObjectParameter useStemmedGenusAndSpecies=new ObjectParameter();
		useStemmedGenusAndSpecies.setName("Use_Stemmed_Genus_and_Species");
		useStemmedGenusAndSpecies.setValue("false");
		
		EnumParameter accuracyVsSpeed=new EnumParameter();
		accuracyVsSpeed.setName("Accuracy_vs_Speed");
		accuracyVsSpeed.setValue("MAX_ACCURACY");
		
		EnumParameter matcher1=new EnumParameter();
		matcher1.setName("Matcher_1");
		matcher1.setValue("GSAy");
		
		ObjectParameter threshold1 = new ObjectParameter();
		threshold1.setName("Threshold_1");
		threshold1.setValue("0.6");

		ObjectParameter maxResults1 = new ObjectParameter();
		maxResults1.setName("MaxResults_1");
		maxResults1.setValue("10");
		
		EnumParameter matcher2=new EnumParameter();
		matcher2.setName("Matcher_2");
		matcher2.setValue("FUZZYMATCH");
		
		ObjectParameter threshold2 = new ObjectParameter();
		threshold2.setName("Threshold_2");
		threshold2.setValue("0.6");
		
		ObjectParameter maxResults2 = new ObjectParameter();
		maxResults2.setName("MaxResults_2");
		maxResults2.setValue("10");
		
		EnumParameter matcher3=new EnumParameter();
		matcher3.setName("Matcher_3");
		matcher3.setValue("LEVENSHTEIN");
		
		ObjectParameter threshold3 = new ObjectParameter();
		threshold3.setName("Threshold_3");
		threshold3.setValue("0.4");

		ObjectParameter maxResults3 = new ObjectParameter();
		maxResults3.setName("MaxResults_3");
		maxResults3.setValue("5");
		
		EnumParameter matcher4=new EnumParameter();
		matcher4.setName("Matcher_4");
		matcher4.setValue("TRIGRAM");
		
		ObjectParameter threshold4 = new ObjectParameter();
		threshold4.setName("Threshold_4");
		threshold4.setValue("0.4");

		ObjectParameter maxResults4 = new ObjectParameter();
		maxResults4.setName("MaxResults_4");
		maxResults4.setValue("5");
		
		EnumParameter matcher5=new EnumParameter();
		matcher5.setName("Matcher_5");
		matcher5.setValue("NONE");
		
		ObjectParameter threshold5 = new ObjectParameter();
		threshold5.setName("Threshold_5");
		threshold5.setValue("0.2");

		ObjectParameter maxResults5 = new ObjectParameter();
		maxResults5.setName("MaxResults_5");
		maxResults5.setValue("0");
		
		
		List<Parameter> parameters = new ArrayList<>();
		parameters.add(speciesAuthorName);
		parameters.add(taxaAuthorityFile);
		parameters.add(parserName);
		parameters.add(activatePreparsingProcessing);
		parameters.add(useStemmedGenusAndSpecies);		
		parameters.add(accuracyVsSpeed);
		parameters.add(matcher1);
		parameters.add(threshold1);
		parameters.add(maxResults1);
		parameters.add(matcher2);
		parameters.add(threshold2);
		parameters.add(maxResults2);
		parameters.add(matcher3);
		parameters.add(threshold3);
		parameters.add(maxResults3);
		parameters.add(matcher4);
		parameters.add(threshold4);
		parameters.add(maxResults4);
		parameters.add(matcher5);
		parameters.add(threshold5);
		parameters.add(maxResults5);

	
		logger.debug("Parameters set: " + parameters);
		operator.setOperatorParameters(parameters);

	}

}
