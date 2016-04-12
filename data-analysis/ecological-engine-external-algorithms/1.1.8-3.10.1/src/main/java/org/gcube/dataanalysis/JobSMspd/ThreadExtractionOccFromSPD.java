package org.gcube.dataanalysis.JobSMspd;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.streams.Stream;

public class ThreadExtractionOccFromSPD implements Runnable {
	private ArrayList<String> chunk;
	private ArrayList<ArrayList<String>> informations;
	private ArrayList<String> errors;
	BufferedWriter out;

	private String dataProvider;
	private String dataProviderUnfold;
	private String dataProviderExpand;
	String scope;
	private File tempFile;

	public ThreadExtractionOccFromSPD(ArrayList<String> chunk,
			String dataProvider, String dataProviderExpand,
			String dataProviderUnfold, String scope) {
		this.chunk = chunk;
		this.dataProvider = dataProvider;
		this.dataProviderExpand = dataProviderExpand;
		this.dataProviderUnfold = dataProviderUnfold;
		errors = new ArrayList<String>();
		this.scope = scope;

	}

	public void run() {
		AnalysisLogger.getLogger().debug("SCOPE  " + scope);
		try {
			tempFile = File.createTempFile("chunk"
					+ Thread.currentThread().getId(), ".csv");
			out = new BufferedWriter(new FileWriter(tempFile, false));
		} catch (Exception e) {
			AnalysisLogger.getLogger().error(
					"Error in the chunk file creation: " + e);
		}
		ScopeProvider.instance.set(scope);
		Manager manager = null;
		try {
			manager = manager().build();
			for (String species : chunk) {
				if (species != null) {
					String query = new String();
					query = createQueryParameter(species);
					AnalysisLogger.getLogger().debug("QUERY *******: " + query);
					Stream<ResultElement> stream;
					try {
						stream = manager.search(query);
						int i = 0;
						while (stream.hasNext()) {
							i++;
							OccurrencePoint ti = (OccurrencePoint) stream
									.next();
							ArrayList<String> array = crateRowTable(ti);
							

							insertInTheFile(array);
							array = null;
							System.gc();

						}
						if (i == 0) {
							errors.add(species + " not found.");
						}
					} catch (Exception e) {
						errors.add("Exception on " + species + " :"
								+ e.getMessage());
						e.printStackTrace();
					}
				}
			}
			out.close();
		} catch (Throwable e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug(
					"An error occurred: " + e.getMessage());
		}

	}

	private String createQueryParameter(String species) {
		String query = "SEARCH BY SN '" + species + "'";
		String where = new String();
		String expand = new String();
		String unfold = new String();
		if (dataProvider.equals("ALL"))
			where = "";
		else
			where = " IN " + dataProvider;

		if (dataProviderUnfold.equals("NO OPTION"))
			unfold = "";
		else
			unfold = " UNFOLD WITH " + dataProviderUnfold;

		query = query + unfold;
		AnalysisLogger.getLogger().debug("expand is : " + dataProviderExpand);
		if (dataProviderExpand.equals("ALL")) {
			expand = " EXPAND";
		} else {
			AnalysisLogger.getLogger().debug("inside else ");
			if (dataProviderExpand.equals("NO OPTION"))
				expand = "";
			else
				expand = " EXPAND WITH " + dataProviderExpand;
		}
		query = query + expand;
		query = query + where;
		query = query + " RETURN occurrence";
		return query;
	}

	private ArrayList<String> crateRowTable(OccurrencePoint occurrence) {
		ArrayList<String> infoOcc = new ArrayList<String>();
		try{infoOcc.add(occurrence.getInstitutionCode().replace(",", " "));}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getCollectionCode().replace(",", " "));}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getCatalogueNumber().replace(",", " "));}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getDataSet().getName());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getDataSet().getDataProvider().getName());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getProvider());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getScientificNameAuthorship());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getIdentifiedBy());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getCredits());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getRecordedBy());}catch(Exception e){infoOcc.add("");}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		if (occurrence.getEventDate() == null
				|| sdf.format(occurrence.getEventDate().getTime()).length() == 0)
			infoOcc.add("");
		else
			try{infoOcc.add(sdf.format(occurrence.getEventDate().getTime()));}catch(Exception e){infoOcc.add("");}

		if (occurrence.getModified() == null
				|| sdf.format(occurrence.getModified().getTime()).length() == 0)
			infoOcc.add("");
		else
			try{infoOcc.add(sdf.format(occurrence.getModified().getTime()));}catch(Exception e){infoOcc.add("");}

		try{infoOcc.add(occurrence.getScientificName());}catch(Exception e){infoOcc.add("");}

		try{infoOcc.add(occurrence.getKingdom());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getFamily());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getLocality());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getCountry());}catch(Exception e){infoOcc.add("");}

		try{infoOcc.add(occurrence.getCitation());}catch(Exception e){infoOcc.add("");}

		try{infoOcc.add(Double.toString(occurrence.getDecimalLatitude()));}catch(Exception e){infoOcc.add("");}

		try{infoOcc.add(Double.toString(occurrence.getDecimalLongitude()));}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getCoordinateUncertaintyInMeters());}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(Double.toString(occurrence.getMaxDepth()));}catch(Exception e){infoOcc.add("");}

		try{infoOcc.add(Double.toString(occurrence.getMinDepth()));}catch(Exception e){infoOcc.add("");}
		try{infoOcc.add(occurrence.getBasisOfRecord().toString());}catch(Exception e){infoOcc.add("");}

		return infoOcc;

	}

	private void insertInTheFile(ArrayList<String> array) throws Exception {
		// AnalysisLogger.getLogger().debug("INSIDE insertInTheFile");

		// String query = "insert into " + outputtable + st + " values (";
		String writeString = new String();
		int i = 0;

		for (String s : array) {
			if (i == array.size() - 1) {
				if (s == null)
					writeString = writeString + " ";
				else {

					writeString = writeString + s.replace(",", " ");
				}
			} else if (s == null)
			{
				writeString = writeString + " ,";
				//index of timestamp value, is needed void field
				if(i==10||i==11)
					writeString = writeString + ",";

			}
			else {

				writeString = writeString + s.replace(",", " ") + ",";
			}

			i++;

		}
		// AnalysisLogger.getLogger().debug("record is "+writeString);
		write(writeString);
		out.newLine();

	}

	private void write(String writeSt) {
		try {
			out.write(writeSt);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public File getInfo() {
		return tempFile;
	}

	public ArrayList<String> getErrors() {
		return errors;
	}
}
