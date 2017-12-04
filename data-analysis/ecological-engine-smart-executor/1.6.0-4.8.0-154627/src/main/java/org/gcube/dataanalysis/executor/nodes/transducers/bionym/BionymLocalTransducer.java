package org.gcube.dataanalysis.executor.nodes.transducers.bionym;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.configuration.INFRASTRUCTURE;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.interfaces.Transducerer;
import org.gcube.dataanalysis.ecoengine.utils.ResourceFactory;
import org.gcube.dataanalysis.executor.generators.D4ScienceDistributedProcessing;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.MatcherOutput;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.SingleEntry;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts.YasmeenMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.workflows.BiOnymWF;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.Matcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;
import org.gcube.dataanalysis.executor.scripts.OSCommand;


public class BionymLocalTransducer implements Transducerer {
	// BionymFlexibleWorkflowTransducer bionymwf = new BionymFlexibleWorkflowTransducer();
	public float status = 0f;
	AlgorithmConfiguration config = null;
	public static String speciesName = "SpeciesAuthorName";
	public LinkedHashMap<String, String> outputmap = new LinkedHashMap<String, String>();

	@Override
	public List<StatisticalType> getInputParameters() {
		List<StatisticalType> types = new BionymFlexibleWorkflowTransducer().getInputParameters();
		PrimitiveType p1 = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, speciesName, "The scientific name of the species, possibly with authorship", "Gadus morhua (Linnaeus, 1758)");
		types.remove(0);
		types.remove(0);
		types.remove(0);
		types.remove(0);

		types.add(0, p1);

		return types;
	}

	@Override
	public String getResourceLoad() {
		ResourceFactory resourceManager = new ResourceFactory();
		return resourceManager.getResourceLoad(1);
	}

	public String getResources() {
		if ((status > 0) && (status < 100))
			return ResourceFactory.getResources(100f);
		else
			return ResourceFactory.getResources(0f);
	}

	@Override
	public float getStatus() {
		return status;
	}

	@Override
	public INFRASTRUCTURE getInfrastructure() {
		return INFRASTRUCTURE.LOCAL;
	}

	@Override
	public StatisticalType getOutput() {
		PrimitiveType p = new PrimitiveType(Map.class.getName(), PrimitiveType.stringMap2StatisticalMap(outputmap), PrimitiveTypes.MAP, "RetrievedTaxaNames", "Retrieved Taxa Names");
		return p;
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	public void setConfiguration(AlgorithmConfiguration config) {
		this.config = config;
	}

	@Override
	public void shutdown() {
	}

	@Override
	public String getDescription() {
		return "A fast version of the algorithm implementing BiOnym, a flexible workflow approach to taxon name matching. The workflow allows to activate several taxa names matching algorithms and to get the list of possible transcriptions for a list of input raw species names with possible authorship indication.";
	}

	public void compute() throws Exception {
		status = 10f;
		String uuid = ("" + UUID.randomUUID()).replace("-", "");
		String inputParserFile = "inputParser" + uuid + ".txt";
		String outputParserFile = "outputParser" + uuid + ".txt";
		String sandboxFolder = "";
		try {
			String parser = config.getParam(YasmeenGlobalParameters.parserNameParam);
			String accuracyvsspeed = config.getParam(YasmeenGlobalParameters.performanceParam);
			String reference = config.getParam(YasmeenGlobalParameters.taxaAuthorityFileParam);
			String doPreprocess = config.getParam(YasmeenGlobalParameters.activatePreParsingProcessing);
			String usestemming = config.getParam(YasmeenGlobalParameters.useStemmedGenusAndSpecies);
			String overallMaxResults = config.getParam(YasmeenGlobalParameters.overallMaxResults);
			String inputSpecies = config.getParam(speciesName);
			sandboxFolder = new File(config.getPersistencePath(), D4ScienceDistributedProcessing.defaultContainerFolder).getAbsolutePath();
			
			AnalysisLogger.getLogger().debug("BiOnymLocal-> Species Name: " + inputSpecies);
			AnalysisLogger.getLogger().debug("BiOnymLocal-> Parser to use: " + parser);
			AnalysisLogger.getLogger().debug("BiOnymLocal-> Accuracy vs Speed: " + accuracyvsspeed);
			AnalysisLogger.getLogger().debug("BiOnymLocal-> Reference Dataset: " + reference);
			AnalysisLogger.getLogger().debug("BiOnymLocal-> Do Preprocessing: " + doPreprocess);
			AnalysisLogger.getLogger().debug("BiOnymLocal-> Use Stemming:" + usestemming);
			AnalysisLogger.getLogger().debug("BiOnymLocal-> Overall MaxResults:" + overallMaxResults);

			// prepare the WF
			HashMap<String, String> globalparameters = new HashMap<String, String>();

			globalparameters.put(YasmeenGlobalParameters.parserInputFileParam, inputParserFile);
			globalparameters.put(YasmeenGlobalParameters.parserOutputFileParam, outputParserFile);
			globalparameters.put(YasmeenGlobalParameters.activatePreParsingProcessing, doPreprocess);
			globalparameters.put(YasmeenGlobalParameters.parserNameParam, parser);
			globalparameters.put(YasmeenGlobalParameters.performanceParam, accuracyvsspeed);
			globalparameters.put(YasmeenGlobalParameters.taxaAuthorityFileParam, reference);
			globalparameters.put(YasmeenGlobalParameters.useStemmedGenusAndSpecies, usestemming);
			globalparameters.put(YasmeenGlobalParameters.staticFilesFolderParam, config.getConfigPath());
			// retrieve the list of names to process
			inputSpecies = inputSpecies.replaceAll("^'", "").replaceAll("'$", "");
			List<String> rawnamesFiltered = new ArrayList<String>();
			rawnamesFiltered.add(inputSpecies);

			// prepare the environment
			try {
				OSCommand.ExecuteGetLine("chmod +x *", null);
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("BiOnymLocal-> WARNING: could not change the permissions");
			}

			int overallMR = 10;
			if (overallMaxResults != null)
				overallMR = Integer.parseInt(overallMaxResults);

			AnalysisLogger.getLogger().debug("BiOnymLocal-> Executing WF");
			BiOnymWF bionym = new BiOnymWF(sandboxFolder, overallMR, globalparameters);
			// rebuild the matchers
			List<Matcher> matchers = new BionymFlexibleWorkflowTransducer().buildMatcherList(config, sandboxFolder, globalparameters);
			if (matchers != null)
				bionym.resetMatchers(matchers);
			
			AnalysisLogger.getLogger().debug("BiOnymLocal-> WorkFlow: ");
			int mcounter = 1;
			for (Matcher matcher:matchers){
				AnalysisLogger.getLogger().debug("BiOnymLocal-> "+mcounter+": "+matcher);
				mcounter++;
			}
			status = 20f;
			MatcherOutput output = bionym.executeChainedWorkflow(rawnamesFiltered);
			AnalysisLogger.getLogger().debug("BiOnymLocal-> Workflow Executed");

			status = 70f;
			int nEntries = output.getEntriesNumber();
			outputmap.put("HEADER", BionymFlexibleWorkflowTransducer.headers.toString());

			for (int i = 0; i < nEntries; i++) {
				SingleEntry se = output.getEntry(i);
				// "SOURCE_DATA,TARGET_DATA_SCIENTIFIC_NAME,TARGET_DATA_AUTHORITY,MATCHING_SCORE,TARGET_DATA_SOURCE,TARGET_DATA_ID";
				String[] srow = new String[6];
				srow[0] = "\""+se.originalName+"\"";
				srow[1] = "\""+se.targetScientificName+"\"";
				srow[2] = "\""+se.targetAuthor+"\"";
				srow[3] = "\"" + se.matchingScore+"\"";
				srow[4] = "\""+reference+"\"";
				srow[5] = "\""+se.targetID+"\"";

				outputmap.put("" + (i + 1), Arrays.toString(srow));
			}
			if (nEntries == 0)
				AnalysisLogger.getLogger().debug("BiOnymLocal-> Warning no output found!");

			AnalysisLogger.getLogger().debug("BiOnymLocal-> map of outputs:\n" + outputmap);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {

			new File(sandboxFolder,inputParserFile).delete();
			new File(sandboxFolder,outputParserFile).delete();

			AnalysisLogger.getLogger().debug("BiOnymLocal-> deleting auxiliary files");
			AnalysisLogger.getLogger().debug("BiOnymLocal-> shutting down");
			status = 100f;
		}
	}

}
