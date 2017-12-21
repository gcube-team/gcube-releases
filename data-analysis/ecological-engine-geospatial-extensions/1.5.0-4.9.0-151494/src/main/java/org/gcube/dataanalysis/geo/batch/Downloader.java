package org.gcube.dataanalysis.geo.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
/**
 * Analyzes each row of a file and searches for links inside the file
 * In the end builds a wget file to be run
 * @author coro
 *
 */
public class Downloader {

	public static void main(String[] args) throws Exception {
		List<String> hfiles = getfiles("netcdf_data.html", "fileServer","http", ".nc");
		System.out.println(hfiles);
		System.out.println("Number of links:"+hfiles.size());
		List<String> files = enrichfiles(hfiles);
		System.out.println(files);
		buildwgetFile("wgetfiles.sh", hfiles, files);
	}
	
	public static void buildwgetFile(String filename, List<String> hfiles, List<String> files) throws Exception{
		int size = hfiles.size();
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
		for (int i=0;i<size;i++){
			bw.write(buildGetterString(hfiles.get(i), files.get(i))+System.getProperty("line.separator"));
		}
		
		bw.close();
	}
	
	public static List<String> enrichfiles(List<String> files) throws Exception{
		List<String> arrayfile = new ArrayList<String>();
		for (String fileh:files){
			String file = fileh.substring(fileh.lastIndexOf("/")+1);
			if (file.contains("temperature"))
				file = buildTopicString(file,"ENVIRONMENT", "OCEANS");
			else if (file.contains("salinity"))
				file = buildTopicString(file,"ENVIRONMENT", "OCEANS");
			else if (file.contains("oxygen"))
				file = buildTopicString(file,"ENVIRONMENT", "BIOTA");
			else if (file.contains("phosphate"))
				file = buildTopicString(file,"ENVIRONMENT", "BIOTA");
			else if (file.contains("nitrate"))
				file = buildTopicString(file,"ENVIRONMENT", "BIOTA");
			else if (file.contains("silicate"))
				file = buildTopicString(file,"ENVIRONMENT", "BIOTA");
			else 
				file = buildTopicString(file,"ENVIRONMENT", "OCEANS");
			arrayfile.add(file);
		}
		return arrayfile;
	}
	
	public static List<String> getfiles(String filename,String criterion,String initselection, String endselection) throws Exception{
		List<String> files = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		String line = br.readLine();
		while (line!=null){
			if (line.contains(criterion)){
				String cut = line.substring(line.indexOf(initselection),line.indexOf(endselection)+endselection.length());
				files.add(cut);
			}
			line = br.readLine();
		}
		br.close();
		return files;
	}
	public static String buildGetterString(String httpstring,String filename){
		return String.format("wget --output-document=%1$s %2$s",filename,httpstring);
	}
	
	public static String buildTopicString(String filename, String... topics){
		int idx = filename.indexOf(".n");
		String file = filename.substring(0,idx);
		String ext = filename.substring(idx);
		for (String topic:topics){
			file=file+"_"+topic;
		}
		return file+"_"+ext;
	}
	
	public static void downloadData(String endpoint, String file) throws Exception {
		// Send data
		String urlStr = endpoint;
		URL url = new URL(urlStr);
		URLConnection conn = url.openConnection();
		InputStreamReader isr = new InputStreamReader(conn.getInputStream());
		FileWriter fw = new FileWriter(new File(file));
		pipe(isr, fw);
		fw.close();
		isr.close();
	}

	private static void pipe(Reader reader, Writer writer) throws IOException {
		char[] buf = new char[1024];
		int read = 0;
		double bytes = 0;
		long i = 0;
		while ((read = reader.read(buf)) >= 0) {
			writer.write(buf, 0, read);
			bytes=(bytes+read);
			if (i%1000==0)
				System.out.println("B:"+bytes);
			i++;
		}
		
		writer.flush();
	}

}
