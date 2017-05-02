package org.gcube.application.aquamaps.aquamapsservice.impl.publishing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.JobManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSetUtils {
	final static Logger logger= LoggerFactory.getLogger(FileSetUtils.class);
	


//	private static final String GENERATED_IMAGES=System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/";
	
	public static final String getTempMapsFolder(){
		String tempFolderPath=System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid/maps/tmp_maps/";
		File f=new File(tempFolderPath);
		if(!f.exists())f.mkdirs();
		return tempFolderPath;
	}
	
	
	private static final Map<String,String> imageFileAndName= new HashMap<String, String>();

	static {
		imageFileAndName.put("_map_pic.jpg", "Earth");
		imageFileAndName.put("_afr.jpg", "Continent View : Africa");
		imageFileAndName.put("_asia.jpg", "Continent View : Asia");
		imageFileAndName.put("_aus.jpg", "Continent View : Australia");
		imageFileAndName.put("_eur.jpg", "Continent View : Europa");
		imageFileAndName.put("_nAm.jpg", "Continent View : North America");
		imageFileAndName.put("_sAm.jpg", "Continent View : South America");
		imageFileAndName.put("_xmapAtlan.jpg", "Ocean View : Atlantic");
		imageFileAndName.put("_xmapI.jpg", "Ocean View : Indian");
		imageFileAndName.put("_xmapN.jpg", "Pole View : Artic");
		imageFileAndName.put("_xmapNAtlan.jpg", "Ocean View : North Atlantic");
		imageFileAndName.put("_xmapP.jpg", "Ocean View : Pacific");
		imageFileAndName.put("_xmapS.jpg", "Pole View : Antarctic");
		imageFileAndName.put("_xmapSAtlan.jpg", "Ocean View : South Atlantic");		
	}
	
	
	
	
	public static Map<String,String> generateFileMap(String csqString,String objectTitle)throws Exception{
		int generationResponse=generateImages(csqString);
		if(generationResponse==0){
			return getToPublishList(objectTitle);
		}else throw new Exception("Perl Execution returned "+generationResponse);
	}
	
	public static List<String> getTempFiles(String objectTitle){
		objectTitle=objectTitle.replace(" ", "_");
		ArrayList<String> toReturn=new ArrayList<String>();
		String tmpBasePath=getTempMapsFolder();
		toReturn.add(tmpBasePath+objectTitle+File.separator);
		toReturn.add(tmpBasePath+"csq_map127.0.0.1_"+objectTitle+"_map_pic.jpg");
		return toReturn;
	}
	
	
	
	/**
	 * 
	 * @param objectName
	 * @param csq_str
	 * @param header
	 * @param header_map
	 * @param dirName
	 * @return
	 * @throws Exception
	 */
	static String createClusteringFile(int objId, int jobId, StringBuilder[]csq_str,String header) throws Exception{
		header=header.replace(" ", "_");
		String to_out = "color=FFFF84 fill=Y color2=FFDE6B fill2=Y color3=FFAD6B fill3=Y color4=FF6B6B fill4=Y color5=DE4242 fill5=Y "+
		((csq_str[0].toString().compareTo("")!=0)?" csq="+csq_str[0].toString():" csq=0000:000:0")+
		((csq_str[1].toString().compareTo("")!=0)?" csq2="+csq_str[1].toString():"")+
		((csq_str[2].toString().compareTo("")!=0)?" csq3="+csq_str[2].toString():"")+
		((csq_str[3].toString().compareTo("")!=0)?" csq4="+csq_str[3].toString():"")+
		((csq_str[4].toString().compareTo("")!=0)?" csq5="+csq_str[4].toString():"")+
		" header="+header+" enlarge=7200 title="+header+
		" dilate=N cSub popup=Y landmask=1 filedesc=map_pic legend=  mapsize=large";
		
		String fileName=objId+"_clustering";
		try {
			File d=new File(ServiceContext.getContext().getFolderPath(FOLDERS.CLUSTERS)+File.separator+jobId);
			JobManager.addToDeleteTempFolder(jobId, d.getAbsolutePath());
			d.mkdirs();
			File f=new File(d,fileName);
			f.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(f));
			out.write(to_out);
			out.close();
			String toReturn=f.getAbsolutePath();
			logger.debug("Clustering string saved into "+toReturn);
			return toReturn;
		} catch(Exception e){
				logger.error("Unable to write clutering file ",e);
				throw  e;
		}
	}
	
	static Map<String,String> getToPublishList(String header) throws Exception{
		header=header.replace(" ", "_");
		String tempFolderBasePath=getTempMapsFolder();
		Map<String,String> toReturn=new HashMap<String, String>();
		String basePath=tempFolderBasePath+header+File.separator;
		logger.trace("Checking generated images...");
		logger.trace("base path : "+basePath);
		logger.trace("header is "+header);
		File f1 = new File(tempFolderBasePath+"csq_map127.0.0.1_"+header+"_map_pic.jpg");
		logger.trace("Checking file "+f1.getAbsolutePath());
		if (f1.exists())
			toReturn.put("Earth",f1.getAbsolutePath());			

		for(String suffix:imageFileAndName.keySet()){
			File f2 = new File(basePath+header+suffix);
			logger.trace("Checking file "+f2.getAbsolutePath());
			if (f2.exists())
				toReturn.put(imageFileAndName.get(suffix), f2.getAbsolutePath());
		}

		if(ServiceContext.getContext().getPropertyAsBoolean(PropertiesConstants.ENABLE_SCRIPT_LOGGING))
		for(Entry<String,String> entry:toReturn.entrySet())
			logger.trace("Found "+entry.getKey()+" @ "+entry.getValue());
		return toReturn;
	}
	
	static int  generateImages(String file) throws Exception{
		BufferedReader  input=null;
		try{
			//ensure perl file existance
		logger.trace("Checking perl...");
		String perlFileLocation=System.getenv("GLOBUS_LOCATION")+File.separator+"c-squaresOnGrid"+
		File.separator+"bin"+File.separator+"cs_mapMod.pl";
		File perlFile=new File(perlFileLocation);
		if(perlFile.exists()){
			//ensure temp folder existance
			getTempMapsFolder();			
			
			logger.trace("Checking file existance... : "+file);
			File clusteringFile=new File(file);
			if(clusteringFile.exists()&&clusteringFile.canRead()){
				Runtime rt  = Runtime.getRuntime();
				String cmdLine[] = { "/usr/bin/perl", "-w", perlFileLocation ,file};
				Process p = rt.exec(cmdLine);
				input = new BufferedReader (new InputStreamReader (p.getInputStream())); 
				String line = null;
				while ((line = input.readLine())!=null){
					if(ServiceContext.getContext().getPropertyAsBoolean(PropertiesConstants.ENABLE_SCRIPT_LOGGING))
					logger.trace(line);
				}

				try {
					p.waitFor();
				} catch (InterruptedException e) {
					logger.trace("Perl process exited");
				}
				int value=p.exitValue();
				logger.trace("Exit value is "+value);
				p.destroy();
				return value;
			}else throw new Exception("No access to clustering file "+file);
		}else throw new Exception("Perl File "+perlFileLocation+" NOT FOUND, unable to proceed");
		}catch(Exception e){throw e;}
		finally{
			if(input!=null) input.close();
		}

		 
	}
	
	/**
	 *  Clusterize rs in N stringBuilders compliant with CsquareCode convention
	 * @param rs 			
	 * @param maxIndex				the column index to retrieve max value 
	 * @param toClusterIndex 		the column index of CsquareCodes
	 * @param probabilityIndex		the column index of probability
	 * @return
	 * @throws SQLException
	 */
	public static StringBuilder[] clusterize(ResultSet rs,int maxIndex,int toClusterIndex,int probabilityIndex,boolean bioDiversity) throws SQLException{
		StringBuilder csq_str1 = new StringBuilder();
		StringBuilder csq_str2 = new StringBuilder();
		StringBuilder csq_str3 = new StringBuilder();
		StringBuilder csq_str4 = new StringBuilder();
		StringBuilder csq_str5 = new StringBuilder();
		if(rs.first()){	
			double max=0;
			double r1=0;
			double r2=0;
			double r3=0;
			double r4=0;
			if(bioDiversity){
				max=rs.getDouble(maxIndex);
				r1 = Math.round(Math.pow(10,(Math.log10(max)/5)));
				r2 = Math.round(Math.pow(10,(2*Math.log10(max)/5)));
				r3 = Math.round(Math.pow(10,(3*Math.log10(max)/5)));
				r4 = Math.round(Math.pow(10,(4*Math.log10(max)/5)));}
			else{								
				r1= 0.2;
				r2= 0.4;
				r3= 0.6;
				r4= 0.8;
			}
			logger.debug("Clustering by "+r1+" , "+r2+" , "+r3+" , "+r4);


			do{
				double currentValue=rs.getDouble(probabilityIndex);
				String toAppendCode=rs.getString(toClusterIndex);
				if((currentValue>0)&&(currentValue<=r1)){
					if(csq_str1.length()>1){
						csq_str1.append("|"+toAppendCode);
					}else{
						csq_str1.append(toAppendCode);
					}
				}else{
					if((currentValue>r1)&&(currentValue<=r2)){
						if(csq_str2.length()>1){
							csq_str2.append("|"+toAppendCode);
						}else{
							csq_str2.append(toAppendCode);
						}
					}else{
						if((currentValue>r2)&&(currentValue<=r3)){
							if(csq_str3.length()>1){
								csq_str3.append("|"+toAppendCode);
							}else{
								csq_str3.append(toAppendCode);
							}
						}else{
							if((currentValue>r3)&&(currentValue<=r4)){
								if(csq_str4.length()>1){
									csq_str4.append("|"+toAppendCode);
								}else{
									csq_str4.append(toAppendCode);
								}
							}else{
								if(currentValue>r4){
									if(csq_str5.length()>1){
										csq_str5.append("|"+toAppendCode);
									}else{
										csq_str5.append(toAppendCode);
									}
								}
							}
						}
					}
				}
			}while(rs.next());
		}else return null;
		logger.trace("Clustering complete : cluster 1 size "+csq_str1.length()+"cluster 2 size "+csq_str2.length()+
				"cluster 3 size "+csq_str3.length()+"cluster 4 size "+csq_str4.length()+"cluster 5 size "+csq_str5.length());
		return new StringBuilder[]{csq_str1,csq_str2,csq_str3,csq_str4,csq_str5};
	}
	
}
