package org.gcube.application.aquamaps.images;

import java.sql.SQLException;
import java.util.List;

import net.sf.csv4j.CSVLineProcessor;

import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.File;
import org.gcube.application.aquamaps.images.model.SpeciesInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;

public class SpeciesCSVLineProcessor implements CSVLineProcessor{

	private static final Logger logger = LoggerFactory.getLogger(SpeciesCSVLineProcessor.class);
	
	long count=0;
	int genusIndex=0;
	int speciesIndex=0;
	int picnameIndex=0;
	int speciesIdIndex=0;

	private Dao<SpeciesInfo,String> speciesDao;
	
	public SpeciesCSVLineProcessor() {
		speciesDao=Common.get().getSpeciesDao();
	}
	
	
	@Override
	public boolean continueProcessing() {
		return true;
	}

	@Override
	public void processDataLine(int arg0, List<String> arg1) {
		try {
		String picname=arg1.get(picnameIndex);
		String species=arg1.get(speciesIndex);
		String genus=arg1.get(genusIndex);
		String speciesId=arg1.get(speciesIdIndex);
		
		
		SpeciesInfo info=new SpeciesInfo(speciesId,picname,new String(genus+species).toLowerCase());
//		
//		
//		
//		
//		
//		info.setScientificName(new String(genus+species).toLowerCase());
//		info.setPic(picname);
//		try{
//			for(AquaMap map:pub.getMapsBySpecies(new String[]{genus+"_"+species}, false, false, null)){
//				if(map.getMapType().equals(ObjectType.SpeciesDistribution)){
//					if(map.getResource().getSearchId()==suitableID) info.setSuitableURI(getEarthURI(map.getFiles()));
//					else if(map.getResource().getSearchId()==suitable2050ID) info.setSuitable2050URI(getEarthURI(map.getFiles()));
//					else if(map.getResource().getSearchId()==nativeID) info.setNativeURI(getEarthURI(map.getFiles()));
//					else if(map.getResource().getSearchId()==native2050ID) info.setNative2050URI(getEarthURI(map.getFiles()));
//				}
//			}
//		} catch (Exception e) {
//			logger.error("Unable to contact publisher "+e.getMessage());
//			logger.debug("Exception was ",e);
//		}
//		
////		System.out.println(info);
		speciesDao.createOrUpdate(info);
			
		
			count++;
		} catch (SQLException e) {
			logger.error("Unable to insert / update "+e.getMessage());
			logger.debug("Exception was ",e);
		}
	}

	@Override
	public void processHeaderLine(int arg0, List<String> arg1) {
		for(int i=0;i<arg1.size();i++){
			String column=arg1.get(i);
			if(column.equals("genus")) genusIndex=i;
			else if(column.equals("species")) speciesIndex=i;
			else if(column.equals("picname")) picnameIndex=i;
			else if(column.equals("speciesid")) speciesIdIndex=i;
		}		
	}

	public long getCount() {
		return count;
	}
	
	
	
	private static final String getEarthURI(List<File> files){
		for(File f:files){
			if(f.getName().equals("Earth")) return f.getUuri();
		}
		return "";
	}
	
}
