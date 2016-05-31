package org.gcube.dataanalysis.fin.taxamatch;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.taxamatch.fin.func_Taxamatch;

public class TaxaMatchTransducer extends StandardLocalExternalAlgorithm {

	public static enum operators {
		EQUAL, NOT_EQUAL, CONTAINS, BEGINS_WITH, ENDS_WITH
	};

	static String GenusOperator = "ComparisonOperatorforGenus";
	static String SpeciesOperator = "ComparisonOperatorforSpecies";
	static String Genus = "Genus";
	static String Species = "Species";
	static String databaseParameterName = "FishBase";
	static String userParameterName = "user";
	static String passwordParameterName = "password";
	static String urlParameterName = "FishBase";

	@Override
	public String getDescription() {
		return "An algorithm for Taxa Matching with respect to the Fishbase database";
	}

	@Override
	public void init() throws Exception {

	}

	@Override
	protected void process() throws Exception {

		String genus = getInputParameter(Genus);
		String species = getInputParameter(Species);
		String genusOperator = getInputParameter(GenusOperator);
		String speciesOperator = getInputParameter(SpeciesOperator);
/*
		String databaseIP = "biodiversity.db.i-marine.research-infrastructures.eu";
		String databaseUser = "postgres";
		String databasePwd = "0b1s@d4sc13nc3";
		String databaseName = "fishbase";
*/
		String databaseJdbc = getInputParameter(urlParameterName).replace("//", "");
		int separator = databaseJdbc.lastIndexOf("/");
		if (separator<0){
			log("Bad database URL: "+databaseJdbc);
			addOutputString("Number of Matches", "0");
			return;
		}
		
		String databaseIP = databaseJdbc.substring(0,separator);
		String databaseName = databaseJdbc.substring(separator+1);
		String databaseUser = getInputParameter(userParameterName);
		String databasePwd = getInputParameter(passwordParameterName);
		log("Database Parameters to use: "+databaseIP+" "+databaseName+" "+databaseUser+" "+databasePwd);

		log("Computing matching for " + genus + " " + species);
		log("With operators: " + genusOperator + " " + speciesOperator);
		if ((genus == null) || (species == null)) {
			log("Void input");
			addOutputString("Number of Matches", "0");
		} else {
			func_Taxamatch func = new func_Taxamatch();
			AnalysisLogger.getLogger().trace("TaxaMatcher Initialized");
			String[] matches = func.func_Taxamatch(genus, species, genusOperator, speciesOperator, databaseIP, databaseUser, databasePwd, databaseName);

			if ((matches == null) || (matches.length == 0)) {
				log("No match");
				addOutputString("Number of Matches", "0");
			} else {
				log("Found " + matches[0] + " matches");
				addOutputString("Number of Matches", matches[0]);
				String[] speciesn = matches[1].split("\n");
				if (Integer.parseInt(matches[0]) > 0) {
					for (int i = 0; i < speciesn.length; i++) {
						addOutputString("Match " + (i + 1), speciesn[i].trim());
					}
				}
			}
		}
		log(outputParameters);
	}

	@Override
	public void shutdown() {

	}

	@Override
	protected void setInputParameters() {
		addStringInput(Genus, "Genus of the species", "Gadus");
		addStringInput(Species, "Species", "morhua");
		addEnumerateInput(operators.values(), GenusOperator, "Comparison Operator for Genus", "" + operators.EQUAL);
		addEnumerateInput(operators.values(), SpeciesOperator, "Comparison Operator for Species", "" + operators.EQUAL);
		addRemoteDatabaseInput(databaseParameterName,urlParameterName,userParameterName,passwordParameterName,"driver","dialect");
	}

}
