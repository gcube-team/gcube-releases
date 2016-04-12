package org.gcube.dataanalysis.JobSMspd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class MapDwCA {
 
	static Logger logger = LoggerFactory.getLogger(MapDwCA.class);
 
	private BufferedWriter vernacularFile;
	private File tempFolder;
	private List<File> fileList = new ArrayList<File>();
	private String archiveZip = "archive-tax.zip";
	private String directory;
 
	public MapDwCA(String directory) {
		super();
		this.directory=directory;
	}
 
 
	public synchronized File createDwCA(Iterator<TaxonomyItem> taxa) throws Exception{
		createMetaXml();
		createMetadata();
		createHeaders();
		createTaxaTxt(taxa);
		getAllFiles(tempFolder);
		return writeZipFile(tempFolder);
	}
 
	/**
	 * Create file meta.xml
	 */
	private void createMetaXml(){
 
		try {
			BufferedWriter bw = null;
			BufferedReader br = null;
			//tempFolder = File.createTempFile("DwCA-folder", "" );
			tempFolder = new File(directory+"DwCA-folder");
			//tempFolder.delete();
			tempFolder.mkdir();
			File output = new File(tempFolder + "/meta.xml") ;
 
			bw = new BufferedWriter(new FileWriter(output));
			br = new BufferedReader(new InputStreamReader(MapDwCA.class.getResourceAsStream("/org/gcube/data/spd/dwca/meta.xml")));
			String line;
 
			while ((line = br.readLine()) != null) {
				bw.write(line);
				bw.write('\n');
 
			}
			bw.close();
			br.close();
		} catch (IOException e) {
			logger.error("IO Error", e);
		}
	}
 
 
	/**
	 * Create headers in taxa.txt and vernacular.txt
	 */
	private void createHeaders(){
 
		try {
 
			BufferedWriter file = new BufferedWriter(new FileWriter(tempFolder + "/" + "taxa.txt", true));
			vernacularFile = new BufferedWriter(new FileWriter(tempFolder + "/" + "VernacularName.txt", true));
 
			//header
			file.write("taxonID\t");
			file.write("acceptedNameUsageID\t");
			file.write("parentNameUsageID\t");
			file.write("scientificName\t");
			file.write("scientificNameAuthorship\t");
			file.write("nameAccordingTo\t");
			file.write("kingdom\t");
			file.write("phylum\t");
			file.write("class\t");
			file.write("order\t");
			file.write("family\t");
			file.write("genus\t");
			file.write("subgenus\t");
			file.write("specificEpithet\t");
			file.write("infraspecificEpithet\t");
			file.write("verbatimTaxonRank\t");
			file.write("taxonRank\t");
			file.write("taxonomicStatus\t");
			file.write("modified\t");
			file.write("bibliographicCitation\t");
			file.write("taxonRemarks\t");
			file.write("scientificNameID\n");
			file.close();
 
 
			//header VernacularName.txt
			vernacularFile.write("taxonID\t");
			vernacularFile.write("vernacularName\t");
			vernacularFile.write("language\t");
			vernacularFile.write("locality\n");
			vernacularFile.close();
 
		} catch (IOException e) {			
			logger.error("IO Error", e);
		}
 
	}
 
 
	/**
	 * Write taxa.txt
	 */
	public void createTaxaTxt(Iterator<TaxonomyItem> taxaReader){
 
		while (taxaReader.hasNext()) {
			TaxonomyItem item = taxaReader.next();
			//logger.trace(item.toString());
			writeLine(item);
		}
 
	}
 
 
	private void internalWriter(TaxonomyItem taxonomyItem, BufferedWriter file ) throws IOException{
		String[] name = taxonomyItem.getScientificName().split(" ");
 
		// Get elemen
		TaxonomyItem tax = taxonomyItem.getParent();
 
		Hashtable<String, String> hashTaxa = new Hashtable<String,String>();
		//create hashtable with taxonomy keys
		if (tax !=null)
			getTax(tax, hashTaxa);
 
 
		//taxonID
		file.write(taxonomyItem.getId());
		file.write("\t");
 
		//acceptedNameUsageID
		if (taxonomyItem.getStatus()==null){
			logger.trace("the status is null for "+taxonomyItem.getId());
		}if (taxonomyItem.getStatus().getRefId() != null){
			String id = taxonomyItem.getStatus().getRefId();
			file.write(id);
		}	
 
		file.write("\t");
 
		//parentNameUsageID
		if (tax !=null)
			file.write(tax.getId());
		file.write("\t");
 
		//scientificName
		/*if (taxonomyItem.getCitation() != null)
			file.write(taxonomyItem.getScientificName() + " " + taxonomyItem.getCitation());
		else*/
		file.write(taxonomyItem.getScientificName());
 
		file.write("\t");
 
		//scientificNameAuthorship
		if (taxonomyItem.getScientificNameAuthorship()!= null)
			file.write(taxonomyItem.getScientificNameAuthorship());
		file.write("\t");
 
		if (taxonomyItem.getCitation()!= null)
			file.write(taxonomyItem.getCitation());
		file.write("\t");		
 
		//kingdom
		String kingdom = (String)hashTaxa.get("kingdom");
		if (kingdom != null)
			file.write(kingdom);
		file.write("\t");
 
		//phylum
		String phylum = (String) hashTaxa.get("phylum");
		if (phylum != null)
			file.write(phylum);
		file.write("\t");
 
		//class
		String claz = (String)hashTaxa.get("class");
		if (claz != null)
			file.write(claz);
		file.write("\t");
 
		//order
		String order = (String)hashTaxa.get("order");
		if (order != null)
			file.write(order);
		file.write("\t");
 
		//family
		String  family = (String)hashTaxa.get("family");
		if (family != null)
			file.write(family);
		file.write("\t");
 
		//genus
		String genus = (String)hashTaxa.get("genus");
		if (genus != null)
			file.write(genus);
		file.write("\t");
 
		//subgenus
		String subgenus = (String)hashTaxa.get("subgenus");
		if (subgenus != null)
			file.write(subgenus);
		file.write("\t");
 
		//specificEpithet
		if (name.length>1)
			file.write(name[1]);
		file.write("\t");
 
		//infraspecificEpithet
		if (name.length>2){
			file.write(name[name.length-1]);
		}
		file.write("\t");
 
		//verbatimTaxonRank
		if (name.length>2){
			file.write(name[name.length-2]);
		}
		file.write("\t");
 
		//taxonRank
		if (taxonomyItem.getRank()!= null)
			file.write(taxonomyItem.getRank().toLowerCase());
		file.write("\t");
 
		//taxonomicStatus (accepted, synonym, unkonwn)
		file.write(taxonomyItem.getStatus().getStatus().toString().toLowerCase());
		file.write("\t");
 
		//modified
		if (taxonomyItem.getModified() !=null){
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = taxonomyItem.getModified().getTime();
			String s = sdf.format(date);
			file.write(s);
		}
		file.write("\t");
 
		//source
		if (taxonomyItem.getCredits() != null)
		file.write(taxonomyItem.getCredits());
		file.write("\t");
 
		//taxonRemarks
		if (taxonomyItem.getStatus().getStatusAsString() != null)
		file.write(taxonomyItem.getStatus().getStatusAsString());
 
		file.write("\t");
 
		if (taxonomyItem.getLsid() != null)
			file.write(taxonomyItem.getLsid());
		file.write("\n");
 
 
 
		//write varnacular names
		if (taxonomyItem.getCommonNames()!= null){
			createVernacularTxt(taxonomyItem.getId(), taxonomyItem.getCommonNames());
		}
	}
 
	/**
	 * Insert line in taxa.txt
	 */
	private void writeLine(TaxonomyItem taxonomyItem){
 
		BufferedWriter bufferedWriter =null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(tempFolder + "/" + "taxa.txt", true));
			internalWriter(taxonomyItem, bufferedWriter);		
 
 
 
		} catch (IOException e) {
			logger.error("IO Error", e);
		}finally{
			try {
				if (bufferedWriter!=null)
					bufferedWriter.close();
			} catch (IOException e) {
				logger.error("error closing bufferedWriter",e);
			}
		}
 
	}
 
	/**
	 * Write VernacularName.txt
	 */
	private void createVernacularTxt(String id, List<CommonName> list){
 
		try {
			vernacularFile = new BufferedWriter(new FileWriter(tempFolder + "/" + "VernacularName.txt", true));
			for (CommonName vernacular : list) {
				//				logger.trace("Vernacular name: " + vernacular.getName());
 
				//taxonID
				vernacularFile.write(id);
				vernacularFile.write("\t");
 
				//vernacularName
				vernacularFile.write(vernacular.getName());
				vernacularFile.write("\t");
 
				//language
				if (vernacular.getLanguage()!= null)
					vernacularFile.write(vernacular.getLanguage());				
				vernacularFile.write("\t");			
 
				//locality
				if (vernacular.getLocality()!= null)
					vernacularFile.write(vernacular.getLocality());
 
				vernacularFile.write("\n");	
 
 
			}
			vernacularFile.close();
		} catch (IOException e) {			
			logger.error("IO Error", e);
		}
 
	}
 
	/**
	 * Create hashtable with taxonomy keys
	 */
	private void getTax(TaxonomyItem tax, Hashtable<String, String> taxa){
		if(tax!=null)
			if(tax.getRank()!=null && tax.getScientificName()!=null)
		taxa.put((tax.getRank()).toLowerCase(), tax.getScientificName());
			else
			{
				AnalysisLogger.getLogger().debug("in DWA generator, tax rank or SN are null");
			}
		else
		{
			AnalysisLogger.getLogger().debug("tax is null");
		}
		//writeLine(tax);
		//		logger.trace("insert parent " + tax.getId() + " " + tax.getScientificName());
		if (tax.getParent()!=null)
			getTax(tax.getParent(), taxa);
 
 
	}
 
 
	/**
	 * List files in directory
	 */
	private void getAllFiles(File dir) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				fileList.add(file);
				if (file.isDirectory()) {
					logger.trace("directory:" + file.getCanonicalPath());
					getAllFiles(file);
				} else {
					logger.trace("     file:" + file.getCanonicalPath());
				}
			}
		} catch (IOException e) {
			logger.error("error creating files",e);
		}
	}
 
	/**
	 * Create zip file
	 */
	private File writeZipFile(File directoryToZip) throws Exception {
 
		File zipFile = new File(directoryToZip + "/" + archiveZip);
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipOutputStream zos = new ZipOutputStream(fos);
 
		for (File file : fileList) {
			if (!file.isDirectory()) { // we only zip files, not directories
				addToZip(directoryToZip, file, zos);
			}
		}
		zos.close();
		fos.close();
		return zipFile;
 
	}
 
 
	/**
	 * Add files to zip
	 */
	private void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws FileNotFoundException,
	IOException {
 
		FileInputStream fis = new FileInputStream(file);
 
		// we want the zipEntry's path to be a relative path that is relative
		// to the directory being zipped, so chop off the rest of the path
		String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
				file.getCanonicalPath().length());
		logger.trace("Writing '" + zipFilePath + "' to zip file");
		ZipEntry zipEntry = new ZipEntry(zipFilePath);
		zos.putNextEntry(zipEntry);
 
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
 
		zos.closeEntry();
		fis.close();
 
	}
 
	/**
	 * Create file em.xml
	 */
	public void createMetadata() throws IOException {
 
		Calendar now = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
 
		File output = new File(tempFolder + "/eml.xml") ;
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(output));
		} catch (IOException e) {
			logger.error("IO Error", e);
		}
 
		BufferedReader br = new BufferedReader(new InputStreamReader(MapDwCA.class.getResourceAsStream("/org/gcube/data/spd/dwca/eml.xml")));
		String line;
		while ((line = br.readLine()) != null) {
			bw.write(line.replace("<pubDate></pubDate>", "<pubDate>" + format.format(now.getTime()) + "</pubDate>"));
			bw.write('\n');
 
		}
		bw.close();
		br.close();
 
	}
 
}
 