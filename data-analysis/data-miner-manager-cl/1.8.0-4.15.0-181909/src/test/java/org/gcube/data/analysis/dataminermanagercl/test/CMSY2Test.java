package org.gcube.data.analysis.dataminermanagercl.test;

import java.util.ArrayList;
import java.util.List;

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
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.FileParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.data.analysis.dataminermanagercl.shared.process.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class CMSY2Test extends TestCase {
	private static Logger logger = LoggerFactory.getLogger(CMSY2Test.class);

	public void testExecute() {

		if (Constants.TEST_ENABLE) {
			logger.debug("Test CMSY 2");
			try {
				DataMinerService dataMinerService = new DataMinerService();
				SClient sClient = dataMinerService.getClient();
				Operator operator = sClient
						.getOperatorById(OperatorId.CMSY2.toString());

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

		FileParameter catchFile = new FileParameter();
		catchFile.setName("catch_file");
		catchFile
				.setValue("http://data.d4science.org/R3BDYS91WkRBaVlUQ29LNWFERGs5TTA5U3o3Rmp5R2hHbWJQNStIS0N6Yz0");
		
		ObjectParameter region = new ObjectParameter();
		region.setName("Region");
		region.setValue("Mediterranean");
		
		ObjectParameter subRegion = new ObjectParameter();
		subRegion.setName("Subregion");
		subRegion.setValue("Adriatic Sea");
		
		ObjectParameter stock = new ObjectParameter();
		stock.setName("Stock");
		stock.setValue("Athe_boy_AD");

		ObjectParameter group = new ObjectParameter();
		group.setName("Group");
		group.setValue("Plankton feeders");

		ObjectParameter name = new ObjectParameter();
		name.setName("Name");
		name.setValue("Sand smelt in Adriatic Sea");

		ObjectParameter englishName = new ObjectParameter();
		englishName.setName("EnglishName");
		englishName.setValue("Big scale sand smelt");

		ObjectParameter scientificName = new ObjectParameter();
		scientificName.setName("ScientificName");
		scientificName.setValue("Atherina boyeri");

		ObjectParameter source = new ObjectParameter();
		source.setName("Source");
		source.setValue("-");

		ObjectParameter minOfYear = new ObjectParameter();
		minOfYear.setName("MinOfYear");
		minOfYear.setValue("1970");

		ObjectParameter maxOfYear = new ObjectParameter();
		maxOfYear.setName("MaxOfYear");
		maxOfYear.setValue("2014");
		
		ObjectParameter startYear = new ObjectParameter();
		startYear.setName("StartYear");
		startYear.setValue("1970");

		ObjectParameter endYear = new ObjectParameter();
		endYear.setName("EndYear");
		endYear.setValue("2014");
		
		ObjectParameter flim = new ObjectParameter();
		flim.setName("Flim");
		flim.setValue("NA");
		
		ObjectParameter fpa = new ObjectParameter();
		fpa.setName("Fpa");
		fpa.setValue("NA");
		
		ObjectParameter blim = new ObjectParameter();
		blim.setName("Blim");
		blim.setValue("NA");
		
		ObjectParameter bpa = new ObjectParameter();
		bpa.setName("Bpa");
		bpa.setValue("NA");
		
		ObjectParameter bmsy = new ObjectParameter();
		bmsy.setName("Bmsy");
		bmsy.setValue("NA");
		
		ObjectParameter fmsy = new ObjectParameter();
		fmsy.setName("FMSY");
		fmsy.setValue("NA");
		
		ObjectParameter msy = new ObjectParameter();
		msy.setName("MSY");
		msy.setValue("NA");
		
		ObjectParameter msybtrigger = new ObjectParameter();
		msybtrigger.setName("MSYBtrigger");
		msybtrigger.setValue("NA");
		
		ObjectParameter b40 = new ObjectParameter();
		b40.setName("B40");
		b40.setValue("NA");
		
		ObjectParameter m = new ObjectParameter();
		m.setName("M");
		m.setValue("NA");
		
		ObjectParameter fofl = new ObjectParameter();
		fofl.setName("Fofl");
		fofl.setValue("NA");
		
		ObjectParameter last_F = new ObjectParameter();
		last_F.setName("last_F");
		last_F.setValue("NA");
		
		EnumParameter resilience=new EnumParameter();
		resilience.setName("Resilience");
		resilience.setValue("Medium");

		ObjectParameter rLow = new ObjectParameter();
		rLow.setName("r.low");
		rLow.setValue("NA");
		
		ObjectParameter rHi = new ObjectParameter();
		rHi.setName("r.hi");
		rHi.setValue("NA");
		
		ObjectParameter stbLow = new ObjectParameter();
		stbLow.setName("stb.low");
		stbLow.setValue("0.2");
		
		ObjectParameter stbHi = new ObjectParameter();
		stbHi.setName("stb.hi");
		stbHi.setValue("0.6");
		
		ObjectParameter intYr = new ObjectParameter();
		intYr.setName("int.yr");
		intYr.setValue("NA");
		
		ObjectParameter intbLow = new ObjectParameter();
		intbLow.setName("intb.low");
		intbLow.setValue("NA");
		
		ObjectParameter intbHi = new ObjectParameter();
		intbHi.setName("intb.hi");
		intbHi.setValue("NA");
		
		ObjectParameter endbLow = new ObjectParameter();
		endbLow.setName("endb.low");
		endbLow.setValue("0.01");
		
		ObjectParameter endbHi = new ObjectParameter();
		endbHi.setName("endb.hi");
		endbHi.setValue("0.4");
		
		ObjectParameter qStart = new ObjectParameter();
		qStart.setName("q.start");
		qStart.setValue("NA");
		
		ObjectParameter qEnd = new ObjectParameter();
		qEnd.setName("q.end");
		qEnd.setValue("NA");
		
		EnumParameter btype=new EnumParameter();
		btype.setName("btype");
		btype.setValue("None");
		
		ObjectParameter forceCmsy = new ObjectParameter();
		forceCmsy.setName("force.cmsy");
		forceCmsy.setValue("false");
		
		ObjectParameter comment = new ObjectParameter();
		comment.setName("Comment");
		comment.setValue("landings");
		
		
		List<Parameter> parameters = new ArrayList<>();
		parameters.add(catchFile);
		parameters.add(region);
		parameters.add(subRegion);
		parameters.add(stock);
		parameters.add(group);
		parameters.add(name);
		parameters.add(englishName);
		parameters.add(scientificName);
		parameters.add(source);
		parameters.add(minOfYear);
		parameters.add(maxOfYear);
		parameters.add(startYear);
		parameters.add(endYear);
		parameters.add(flim);
		parameters.add(fpa);
		parameters.add(blim);
		parameters.add(bpa);
		parameters.add(bmsy);
		parameters.add(fmsy);
		parameters.add(msy);
		parameters.add(msybtrigger);
		parameters.add(b40);
		parameters.add(m);
		parameters.add(fofl);
		parameters.add(last_F);
		parameters.add(resilience);
		parameters.add(rLow);
		parameters.add(rHi);
		parameters.add(stbLow);
		parameters.add(stbHi);
		parameters.add(intYr);
		parameters.add(intbLow);
		parameters.add(intbHi);
		parameters.add(endbLow);
		parameters.add(endbHi);
		parameters.add(qStart);
		parameters.add(qEnd);
		parameters.add(btype);
		parameters.add(forceCmsy);
		parameters.add(comment);
	
		logger.debug("Parameters set: " + parameters);
		operator.setOperatorParameters(parameters);

	}

}
