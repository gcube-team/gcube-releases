package density;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.dataanalysis.ecoengine.utils.Transformations;

public class Maxent {

	public String samplesFilePath;
	public String environmentalLayersLocation;
	public String outputDirectory;
	public int maxIterations;
	public double defaultPrevalence;
	public int noDataValue;
	public List<String> envVariables = new ArrayList<String>();

	public static Params getDefaultParameters() {
		Params p = new Params();
		p.setResponsecurves(true);
		p.setPictures(true);
		p.setJackknife(false);
		p.setOutputformat("Logistic");
		p.setOutputfiletype("asc");
		p.setRandomseed(false);
		p.setLogscale(true);
		p.setWarnings(true);
		p.setTooltips(false);
		p.setAskoverwrite(false);
		p.setSkipifexists(false);
		p.setRemoveduplicates(true);
		p.setWriteclampgrid(false);
		p.setWritemess(false);
		p.setRandomtestpoints(0);
		p.setBetamultiplier(1);
		p.setMaximumbackground(10000);
		p.setReplicates(1);
		p.setReplicatetype("Crossvalidate");
		p.setPerspeciesresults(false);
		p.setWritebackgroundpredictions(false);
		p.setBiasisbayesianprior(false);
		p.setResponsecurvesexponent(false);
		p.setLinear(true);
		p.setQuadratic(true);
		p.setProduct(true);
		p.setThreshold(true);
		p.setHinge(true);
		p.setPolyhedral(true);
		p.setAddsamplestobackground(true);
		p.setAddallsamplestobackground(false);
		p.setAutorun(false);
		p.setAutofeature(true);
		p.setDosqrtcat(false);
		p.setWriteplotdata(false);
		p.setFadebyclamping(false);
		p.setExtrapolate(true);
		p.setVisible(false);
		p.setGivemaxaucestimate(true);
		p.setDoclamp(true);
		p.setOutputgrids(true);
		p.setPlots(true);
		p.setAppendtoresultsfile(false);
		p.setParallelupdatefrequency(30);
		p.setMaximumiterations(1000);
		p.setConvergencethreshold(0.00001);
		p.setAdjustsampleradius(0);
		p.setThreads(2);
		p.setLq2lqptthreshold(80);
		p.setL2lqthreshold(10);
		p.setHingethreshold(15);
		p.setBeta_threshold(-1);
		p.setBeta_categorical(-1);
		p.setBeta_lqp(-1);
		p.setBeta_hinge(-1);
		p.setBiastype(0);

		p.setLogfile("maxent.log");
		p.setScientificpattern("#.#####E0");
		p.setCache(false);
		p.setCachefeatures(false);
		p.setDefaultprevalence(0.5);

		p.setVerbose(true);
		p.setAllowpartialdata(false);
		p.setPrefixes(true);
		p.setPrintversion(false);
		p.setNodata(-9999);
		p.setNceas(false);
		p.setMinclamping(false);
		p.setManualreplicates(false);

		p.setSamplesfile("CarcharodonPoints.csv");
		p.setEnvironmentallayers("./");
		p.setOutputdirectory("./maxentout/");

		return p;
	}

	public void executeMaxEnt() {
		
		File outDir = new File(outputDirectory);
		if (!outDir.exists())
			outDir.mkdir();
		
		Params p = getDefaultParameters();

		p.setSamplesfile(samplesFilePath);
		p.setEnvironmentallayers(environmentalLayersLocation);
		p.setOutputdirectory(outputDirectory);
		p.setMaximumiterations(maxIterations);
		p.setDefaultprevalence(defaultPrevalence);
		p.setNodata(noDataValue);

		Utils.applyStaticParams(p);
		p.setSelections();
		Runner runner = new Runner(p);
		runner.start();
		runner.end();
	}

	public Maxent(String samplesFilePath, String environmentalLayersLocation, String outputDirectory, int maxIterations, double defaultPrevalence, int noDataValue) {
		this.samplesFilePath = samplesFilePath;
		this.environmentalLayersLocation = environmentalLayersLocation;
		this.outputDirectory = outputDirectory;
		this.maxIterations = maxIterations;
		this.defaultPrevalence = defaultPrevalence;
		this.noDataValue = noDataValue;

		File layersLocation = new File(environmentalLayersLocation);
		File[] list = layersLocation.listFiles();
		for (File f : list) {
			if (f.getName().endsWith(".asc")) {
				envVariables.add(f.getName());
			}
		}
	}

	public String getSpeciesName() throws Exception{
		Map<String, String> value = getOutputValues("Species");
		String species = value.get("Species");
		return species;
	}
	
	public String getResult()  throws Exception{
		String species = getSpeciesName();
		File f = new File(outputDirectory, species + ".asc");
		if (f.exists()) {
			return f.getAbsolutePath();
		}
		return null;
	}

	public String getWorldPlot()  throws Exception{
		
		File f = new File(outputDirectory, "plots/"+getSpeciesName()+".png");
		if (f.exists()) {
			return f.getAbsolutePath();
		}
		return null;
	}

	public String getOmissionPlot()  throws Exception{
		File f = new File(outputDirectory, "plots/"+getSpeciesName()+"_omission.png");
		if (f.exists()) {
			return f.getAbsolutePath();
		}
		return null;
	}

	public String getROCPlot()  throws Exception{
		File f = new File(outputDirectory, "plots/"+getSpeciesName()+"_roc.png");
		if (f.exists()) {
			return f.getAbsolutePath();
		}
		return null;
	}

	public Map<String, String> getVariablesContributions()  throws Exception{
		return getOutputValues(" contribution");
	}

	public Map<String, String> getVariablesPermutationsImportance()  throws Exception{
		return getOutputValues(" permutation importance");
	}

	public double getPrevalence()  throws Exception{
		return Double.parseDouble(getOutputValues(" (average of logistic output over background sites)").values().iterator().next());
	}

	public double getBestThr()  throws Exception{
		return Double.parseDouble(getOutputValues(" training omission, predicted area and threshold value logistic threshold").values().iterator().next());
	}

	public String getWarnings() {

		File f = new File(outputDirectory, "maxent.log");
		if (f.exists()) {
			StringBuffer buffer = new StringBuffer();
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line = br.readLine();
				while(line!=null) {
					if (line.startsWith("Warning:"))
						buffer.append(line+"\n");
					
					line = br.readLine();
				}
				br.close();
				return buffer.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	private Map<String, String> getOutputValues(String adjective) throws Exception{

		File f = new File(outputDirectory, "maxentResults.csv");
		if (f.exists()) {
			Map<String, String> contributions = new LinkedHashMap<String, String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String headers = br.readLine();
				String values = br.readLine();
				List<String> heads = Transformations.parseCVSString(headers, ",");
				List<String> vals = Transformations.parseCVSString(values,",");
				int i = 0;
				for (String head : heads) {
					if (head.contains(adjective)) {
						int idx = head.indexOf(" ");
						String var = head;
						if (idx>-1)
							var = head.substring(0,idx);
						
						contributions.put(var, vals.get(i));
					}
					i++;
				}
				br.close();
				return contributions;
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("No occurrence records in the selected bounding box");
			}
		}
		return null;
	}

	
	private void delFiles(String dir){
		System.gc();
		File f = new File(dir);
		File[] list = f.listFiles();
		if (list != null) {
			for (File l : list) {
				if (l.isFile()){
					for (int j=0;j<3;j++)
						l.delete();
				}
			}
		}
	}
	
	private void delDir(String dir){
		System.gc();
		File f = new File(dir);
		f.delete();
	}

	public void clean() throws Exception {
		delFiles(new File(outputDirectory, "plots").getAbsolutePath());
		delDir(new File(outputDirectory, "plots").getAbsolutePath());
		delFiles(outputDirectory);
		delDir(outputDirectory);
	}

	public static void main(String[] args) throws Exception {
		Maxent me = new Maxent("./maxenttestfolder/occurrence_species_id0045886b_2a7c_4ede_afc4_3157c694b893_occ.csv", "./maxenttestfolder/", "./maxenttestfolder/output/", 10000, 0.5, -9999);
		me.executeMaxEnt();
		
		System.out.println("Result: "+me.getResult());
		System.out.println("ROC plot: "+me.getROCPlot());
		System.out.println("World plot: "+me.getWorldPlot());
		System.out.println("Best Threshold: "+me.getBestThr());
		System.out.println("Prevalence: "+me.getPrevalence());
		System.out.println("Variables Contribution: "+me.getVariablesContributions());
		System.out.println("Variables Permutations: "+me.getVariablesPermutationsImportance());
		System.out.println("Omission/Commission Plot: "+me.getOmissionPlot());
		System.out.println("Warnings: "+me.getWarnings());
		
//		me.clean();
	}

}
