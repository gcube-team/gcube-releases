package org.gcube.dataanalysis.executor.nodes.transducers.bionym.abstracts;

import java.io.File;
import java.security.Permission;
import java.util.HashMap;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.implementations.matchers.LevensteinMatcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.interfaces.Matcher;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenFileTools;
import org.gcube.dataanalysis.executor.nodes.transducers.bionym.utils.YasmeenGlobalParameters;

public abstract class YasmeenMatcher implements Matcher {

	public static String matcherLib = "YASMEEN-matcher-1.2.0.1.jar";
	protected String sandBoxFolder = "./";
	public double threshold = 0.4;
	public int maxResults = 10;
	public HashMap<String, String> parameters;

	HashMap<String, String> urlsmap = createReferenceMap();
	static HashMap<String, String> performancemap = createPerformanceMap();

	protected static HashMap<String, String> createReferenceMap() {
		HashMap<String, String> urlsMaps = new HashMap<String, String>();
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.ASFIS.name(), "http://goo.gl/qfeTp7");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.FISHBASE.name(), "http://goo.gl/FcnUc0");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS.name(), "http://goo.gl/AUcUXt");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS_ANIMALIA.name(), "http://goo.gl/mW1fvb");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS_CNIDARIA.name(), "http://goo.gl/N6vCq8");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS_ECHINODERMATA.name(), "http://goo.gl/2qQThI");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS_PLATYHELMINTHES.name(), "http://goo.gl/RLmCn2");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.COL_FULL.name(), "http://goo.gl/fJ9feg");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.COL_CHORDATA.name(), "http://goo.gl/11upZC");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.COL_MAMMALIA.name(), "http://goo.gl/J4fga6");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.IRMNG_ACTINOPTERYGII.name(), "http://goo.gl/Z8eRly");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.WORMS_ANIMALIA.name(), "http://goo.gl/XRMWgr");
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.WORMS_PISCES.name(), "http://goo.gl/OdPRiA");

		return urlsMaps;
	}

	protected HashMap<String, String> createReferenceMapFile(String localFolder) {
		HashMap<String, String> urlsMaps = new HashMap<String, String>();
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.ASFIS.name(), new File(localFolder, "ASFIS_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.FISHBASE.name(), new File(localFolder, "FISHBASE_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS.name(), new File(localFolder, "OBIS_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS_ANIMALIA.name(), new File(localFolder, "OBIS_Animalia_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS_CNIDARIA.name(), new File(localFolder, "OBIS_Cnidaria_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS_ECHINODERMATA.name(), new File(localFolder, "OBIS_Echinodermata_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.OBIS_PLATYHELMINTHES.name(), new File(localFolder, "OBIS_Platyhelminthes_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.COL_FULL.name(), new File(localFolder, "COL_FULL_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.COL_CHORDATA.name(), new File(localFolder, "COL_Chordata_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.COL_MAMMALIA.name(), new File(localFolder, "COL_Mammalia_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.IRMNG_ACTINOPTERYGII.name(), new File(localFolder, "IRMNG_Actinopterygii_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.WORMS_ANIMALIA.name(), new File(localFolder, "WORMS_Animalia_taxa.taf").getAbsolutePath());
		urlsMaps.put(YasmeenGlobalParameters.BuiltinDataSources.WORMS_PISCES.name(), new File(localFolder, "WoRMS_Pisces_taxa.taf").getAbsolutePath());

		return urlsMaps;
	}

	protected static HashMap<String, String> createPerformanceMap() {
		HashMap<String, String> performanceMap = new HashMap<String, String>();
		performanceMap.put(YasmeenGlobalParameters.Performance.MAX_ACCURACY.name(), "");
		performanceMap.put(YasmeenGlobalParameters.Performance.LOW_SPEED.name(), "-dpt 10");
		performanceMap.put(YasmeenGlobalParameters.Performance.MEDIUM_SPEED.name(), "-dpt 6");
		performanceMap.put(YasmeenGlobalParameters.Performance.HIGH_SPEED.name(), "-dpt 2");
		performanceMap.put(YasmeenGlobalParameters.Performance.MAX_SPEED.name(), "-dpt 0");
		return performanceMap;
	}

	protected abstract String getMatchlets();

	protected abstract String getLexicalDistancesWeights();

	protected abstract String getStemming();

	public YasmeenMatcher(String sandboxfolder, double threshold, int maxResults, HashMap<String, String> parameters) {
		init(sandboxfolder, threshold, maxResults, parameters);
	}

	@Override
	public void init(String sandboxfolder, double threshold, int maxResults, HashMap<String, String> parameters) {
		this.sandBoxFolder = sandboxfolder;
		this.parameters = parameters;
		this.threshold = threshold;
		this.maxResults = maxResults;
	}

	@Override
	public MatcherOutput match(MatcherInput inputName) throws Exception {

		String inputFileName = parameters.get(YasmeenGlobalParameters.parserOutputFileParam);
		String uuid = ("" + UUID.randomUUID()).replace("-", "");
		String outputFileName = "matcherOutput" + uuid + ".csv";
		File outfile = new File(sandBoxFolder, outputFileName);
		if (outfile.exists()) {
			outfile.delete();
		}

		// String outputFileName = parameters.get(YasmeenGlobalParameters.parserOutputFileParam);
		String taxaAuthorityFile = parameters.get(YasmeenGlobalParameters.taxaAuthorityFileParam);
		String performanceMap = parameters.get(YasmeenGlobalParameters.performanceParam);

		String performanceCommand = performancemap.get(performanceMap);
		if (performanceCommand == null)
			performanceCommand = "";

		boolean stemmit = Boolean.parseBoolean(parameters.get(YasmeenGlobalParameters.useStemmedGenusAndSpecies));
		String normalizeMatchlets = "";
		String standardMatchlets = "";
		if (stemmit)
			normalizeMatchlets = getStemming();
		else
			standardMatchlets = getMatchlets();

		/* ENABLE LOCAL FILES USAGE */
		/*
		 * String localFilesFolder = parameters.get(YasmeenGlobalParameters.staticFilesFolderParam); if (localFilesFolder==null){ localFilesFolder=""; urlsmap=createReferenceMap(); } else urlsmap=createReferenceMapFile(localFilesFolder);
		 */
		// java -jar YASMEEN-matcher-1.1.1.jar -inFile output.csv -refData ASFIS@https://dl.dropboxusercontent.com/u/12809149/ASFIS_taxa.taf.gz -outFile matchout.csv -man -may -mSn -mt -mc 10 -ps -pt 1.5x -xml -xslTemplate csv -law 90:30:60
		// YasmeenFileTools.writeYasmeenMatcherInput(sandBoxFolder, inputFileName, parser, inputName);

		// org.fao.fi.comet.domain.species.tools.process.matching.cli.MatchingEngine

		String localFilesFolder = parameters.get(YasmeenGlobalParameters.staticFilesFolderParam);
		if ((localFilesFolder != null) && (localFilesFolder.length() > 0) 
				&& (taxaAuthorityFile.equals(YasmeenGlobalParameters.BuiltinDataSources.FISHBASE.name())) 
				&& (this instanceof LevensteinMatcher) 
				&& new File(createReferenceMapFile(localFilesFolder).get(taxaAuthorityFile)).exists()) {
				RealTimeMatcher rm = new RealTimeMatcher();
				HashMap<String, String> filesMap = createReferenceMapFile(localFilesFolder);
				rm.match(filesMap.get(taxaAuthorityFile), taxaAuthorityFile, new File(sandBoxFolder, inputFileName).getAbsolutePath(), outfile.getAbsolutePath(), threshold, maxResults);
		} 
		else {
			String execution = "java -Xmx512m -Xmx1024m -jar " + new File(sandBoxFolder, matcherLib).getAbsolutePath() + " -inFile " + new File(sandBoxFolder, inputFileName).getAbsolutePath() + " -refData " + taxaAuthorityFile + "@" + urlsmap.get(taxaAuthorityFile) + " -outFile " + outfile.getAbsolutePath() + " " + normalizeMatchlets + " -mc " + maxResults + " -mst " + threshold + " -pt 1x -xml -xslTemplate csv" + " " + getLexicalDistancesWeights() + " " + standardMatchlets + " " + performanceCommand;
			AnalysisLogger.getLogger().debug(execution);
			YasmeenFileTools.callYasmeen(execution.trim());
		}
		MatcherOutput mo = YasmeenFileTools.getYasmeenMatcherOutput(sandBoxFolder, outputFileName);
		outfile.delete();
		return mo;
	}

	private static class NoExitSecurityManager extends SecurityManager {
		@Override
		public void checkPermission(Permission perm) {
			// allow anything.
		}

		@Override
		public void checkPermission(Permission perm, Object context) {
			// allow anything.
		}

		@Override
		public void checkExit(int status) {
			super.checkExit(status);
			throw new RuntimeException("exit status is "+status);
		}
	}
}
