package org.gcube.dataanalysis.seadatanet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DivaHTTPClient {

	//public static String WEB_HTTP="http://gher-diva.phys.ulg.ac.be/web-vis/Python/web";
	public static String WEB_HTTP="http://ec.oceanbrowser.net/emodnet/Python/web";
	public static String DOWN_FILE_NC="/download?fieldname=";

	public static String postFile(File file) throws Exception {
		String crlf = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		HttpURLConnection httpUrlConnection = null;
		try {

			URL url = new URL(WEB_HTTP+"/upload");
			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setDoOutput(true);

			httpUrlConnection.setRequestMethod("POST");
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");

			httpUrlConnection.setRequestProperty(
					"Content-Type", "multipart/form-data;boundary=" + boundary);

			DataOutputStream request = new DataOutputStream(
					httpUrlConnection.getOutputStream());

			request.writeBytes("--" + boundary + crlf);
			request.writeBytes("Content-Disposition: form-data; name=\"data" + "\";filename=\"" + 
					file.getName() + "\"" + crlf);
			request.writeBytes(crlf);

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String lineString = reader.readLine();
			while (lineString!=null) {
				request.writeBytes(lineString+"\n");
				lineString = reader.readLine();
			}


			reader.close();
			request.writeBytes(crlf);
			request.writeBytes(twoHyphens + boundary + 
					twoHyphens + crlf);
			request.flush();
			request.close();
			InputStream responseStream = new 
					BufferedInputStream(httpUrlConnection.getInputStream());

			BufferedReader responseStreamReader = 
					new BufferedReader(new InputStreamReader(responseStream));

			String line = "";
			StringBuilder stringBuilder = new StringBuilder();

			while ((line = responseStreamReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}
			responseStreamReader.close();

			String response = stringBuilder.toString();
			
			responseStream.close();
			httpUrlConnection.disconnect();
			
			return response;

		} catch (IOException e) {
			throw new Exception(e);
		} finally {
			if (httpUrlConnection != null)
				httpUrlConnection.disconnect();
		}
	}
	/**
	 * sessionid
		    id from previous upload 
		len
		    correlation length (arc degrees) 
		stn
		    signal to noise ratio (non-dimensional) 
		x0,x1,dx
		    first and last longitude values and resolution of the grid 
		y0,y1,dy
		    idem for latitude 
		level
		    depth level 
	 */
	public static DivaAnalysisGetResponse getAnalysis (String id,double len,double stn,double x0,double x1, double dx,double y0,double y1,double dy,double level) throws Exception {
		HttpURLConnection httpUrlConnection = null;
		
		try {
			String get_url=WEB_HTTP+"/make_analysis?";
			get_url+="sessionid="+id+"&";
			get_url+="len="+len+"&";
			get_url+="stn="+stn+"&";
			get_url+="x0="+x0+"&";
			get_url+="x1="+x1+"&";
			get_url+="dx="+dx+"&";
			get_url+="y0="+y0+"&";
			get_url+="y1="+y1+"&";
			get_url+="dy="+dy+"&";
			get_url+="level="+level+"";
			//System.out.println("GET URL="+get_url);
			URL url = new URL(get_url);

			httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setDoOutput(true);

			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
			httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
			InputStream responseStream = new 
					BufferedInputStream(httpUrlConnection.getInputStream());

			BufferedReader responseStreamReader = 
					new BufferedReader(new InputStreamReader(responseStream));

			String line = "";
			StringBuilder stringBuilder = new StringBuilder();
		
			while ((line = responseStreamReader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}

			responseStreamReader.close();
			
			
			String response = stringBuilder.toString();
			
			//System.out.println("RESPONSE STRING: "+response);
			AnalysisLogger.getLogger().debug("Response from DIVA Analysis: \n"+response);
			
			Document doc = loadXMLFromString(response);
			Node idNode = doc.getElementsByTagName("result").item(0);
			String identifier = idNode.getAttributes().item(0).getNodeValue();
			Double vmin = Double.parseDouble(doc.getElementsByTagName("stat").item(0).getTextContent());
			Double vmax = Double.parseDouble(doc.getElementsByTagName("stat").item(1).getTextContent());
			Integer stat_obs_count_used = Integer.parseInt(doc.getElementsByTagName("stat").item(2).getTextContent());
			Double stat_posteriori_stn = Double.parseDouble(doc.getElementsByTagName("stat").item(3).getTextContent());
			
			responseStream.close();
			httpUrlConnection.disconnect();
			
			return new DivaAnalysisGetResponse(identifier, vmin, vmax, stat_obs_count_used, stat_posteriori_stn);

		} catch (IOException e) {
			throw new Exception(e);
		} finally {
			if (httpUrlConnection != null)
				httpUrlConnection.disconnect();
		}
		

	}


	public static DivaFilePostResponse uploadFile(File file) throws Exception, IOException, ParserConfigurationException, SAXException
	{
		String result_xml = null;
		try {
			result_xml = postFile(file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (result_xml == null) {
			throw new SAXException("Error in HTTP response no response element found!");
		}


		//Create Document object by XMLResponse
		Document doc = loadXMLFromString(result_xml);
		//Print XML Response
		AnalysisLogger.getLogger().debug("DIVA XML Response: \n"+result_xml);
		

		Node root = doc.getDocumentElement();
		if(root== null) throw new SAXException("Error in HTTP response no root element found!");
		Integer obs_count= null;
		try{
			obs_count=Integer.parseInt(root.getChildNodes().item(1).getTextContent());
		}catch(NullPointerException pe ){
			throw new Exception("Error in uploading file to DIVA - Possibly non uniformly filled table!");
		}
		Double obs_x0=Double.parseDouble(root.getChildNodes().item(3).getTextContent());
		Double obs_x1=Double.parseDouble(root.getChildNodes().item(5).getTextContent());
		Double obs_y0=Double.parseDouble(root.getChildNodes().item(7).getTextContent());
		Double obs_y1=Double.parseDouble(root.getChildNodes().item(9).getTextContent());
		Double obs_v0=Double.parseDouble(root.getChildNodes().item(11).getTextContent());
		Double obs_v1=Double.parseDouble(root.getChildNodes().item(13).getTextContent());

		String sessionid=root.getChildNodes().item(15).getTextContent();

		return new DivaFilePostResponse(obs_x0, obs_x1, obs_y0, obs_y1, obs_v0, obs_v1, obs_count, sessionid);

	}

	public static void downloadFileDiva(String fileid, String destinationFile) throws Exception{
		try {
			String fileurl = DivaHTTPClient.WEB_HTTP + DivaHTTPClient.DOWN_FILE_NC + fileid;
			URL smpFile = new URL(fileurl);
			URLConnection uc = (URLConnection) smpFile.openConnection();
			InputStream is = uc.getInputStream();
			AnalysisLogger.getLogger().debug("Retrieving DIVA file from " + fileurl + " to :" + destinationFile);
			inputStreamToFile(is, destinationFile);
			is.close();
		} catch (Exception e) {
			throw e;
		}
	}

	public static void inputStreamToFile(InputStream is, String path) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(new File(path));
		byte buf[] = new byte[1024];
		int len = 0;
		while ((len = is.read(buf)) > 0)
			out.write(buf, 0, len);
		out.close();
	}


	public static void main(String[] args) throws Exception {
		AnalysisLogger.setLogger("cfg/"+AlgorithmConfiguration.defaultLoggerFile);
		DivaFilePostResponse response=uploadFile(new File("resources"+File.separator+"files"+File.separator+"temperature_argo.txt"));
		
		//System.out.println(getAnalysis(response.getSessionid(),5,4,76,78,1,35,78,1,2));
		System.out.println(""+response);
		DivaAnalysisGetResponse respAnalysis = getAnalysis(response.getSessionid(),10, 0.8, -180, 180, 3, -80, 80, 3, 1);
		System.out.println("Response: "+respAnalysis.toString());
		
		//Downloader downFileNC = new Downloader();
		//System.out.println(System.getProperty("user.dir")+File.separator+"temp.nc");
		File f=new File(System.getProperty("user.dir")+File.separator+"temp.nc");
		
		if(!f.exists()) f.createNewFile();
		downloadFileDiva(WEB_HTTP+DOWN_FILE_NC+
		respAnalysis.getIdentifier(),System.getProperty("user.dir")+File.separator+"temp.nc");
		
	}


	//PARSER XML
	private static Document loadXMLFromString(String xml) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

}
