package org.n52.wps.demo;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlObject;
import org.gcube.contentmanagement.lexicalmatcher.utils.FileTools;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings.CsvFileDataBinding;
import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.algorithm.annotation.ComplexDataInput;
import org.n52.wps.algorithm.annotation.ComplexDataOutput;
import org.n52.wps.algorithm.annotation.Execute;
import org.n52.wps.algorithm.annotation.LiteralDataInput;
import org.n52.wps.algorithm.annotation.LiteralDataOutput;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.io.data.binding.complex.GenericXMLDataBinding;
import org.n52.wps.io.data.binding.literal.LiteralStringBinding;
import org.n52.wps.server.AbstractAnnotatedAlgorithm;

//org.n52.wps.demo.GPDemoFile
@Algorithm(statusSupported=false, title="title of the A.", abstrakt="hello worlds", identifier="org.n52.wps.demo.GPDemoFile", version = "1.1.0")
public class TestIO extends AbstractAnnotatedAlgorithm {

	private String data;
	private GenericFileData file;
	private XmlObject outfile;
	private GenericFileData outfile2;
	private String lout;

	@LiteralDataInput(identifier = "CHECK", binding = LiteralStringBinding.class)
    public void setLData(String data) {
        this.data = data;
    }
	
	@ComplexDataInput(identifier = "FFF", binding = GenericFileDataBinding.class)
	public void setCDataType(GenericFileData file) {
		this.file=file;
	}

	@ComplexDataOutput(identifier = "file", binding = GenericXMLDataBinding.class)
	public XmlObject getFile() {
        return outfile;
    }
	
	@LiteralDataOutput(identifier = "lout", binding = LiteralStringBinding.class)
	public String getLiteral() {
        return lout;
    }
	
	@Execute
	public void run() {

		if(file!=null){
			File f = file.getBaseFile(false);
			
			System.out.println("We got a Generic File! "+f.getAbsolutePath());
			try{
			String fileLink = FileTools.loadString(f.getAbsolutePath(), "UTF-8");
			URL url = new URL(fileLink);   
		    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();   
		    InputStream is = new BufferedInputStream(urlConnection.getInputStream());     
		        
//			InputStream is = new FileInputStream(new File(fileLink));
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			String theString = writer.toString();
			System.out.println("Input Content: "+theString);
			is.close();
			urlConnection.disconnect();
			}catch(Exception e){
				e.printStackTrace();
			}
			
				lout = "OK";
		}
		
//		PngFileDataBinding binder=null;
		
//			binder = new GenericFileDataBinding(new GenericFileData(new File("c:\\Users\\GP\\Desktop\\CoelacanthVelin.jpg"), GenericFileDataConstants.MIME_TYPE_IMAGE_JPEG));
			File of = new File("C:\\Users\\GP\\Desktop\\WorkFolder\\WPS\\WPS.txt");
			System.out.println("File Exists: "+of.exists());
			/*
			if (fileDataBinding!=null)
				binder= fileDataBinding;
			else
			*/
			{
//				binder = new PngFileDataBinding(new GenericFileData(of, "image/png"));
//				System.out.println("File mime: "+binder.getPayload().getMimeType());
//				System.out.println("File extension: "+binder.getPayload().getFileExtension());
//				outfile=new GenericFileData(of, "image/png");
				//String inputXMLString = "<testElement>testStringValue</testElement>";
				String out1Name= "generatedimage";
				String out1Payload= "generated image";
				String collectionname = "This is a test image"+" (code:"+UUID.randomUUID()+")";
				String inputXMLString = "<gml:featureMember xmlns:gml=\"http://www.opengis.net/gml\" xmlns:d4science=\"http://www.d4science.org\">\n" +
						"	<d4science:outputcollection fid=\""+collectionname+"\">\n" +
						"		<d4science:"+out1Name+">"+out1Payload+"</d4science:"+out1Name+">\n"+
						"	</d4science:outputcollection>\n" +
						"</gml:featureMember>\n";
				
				System.out.println("XML Produced : \n"+inputXMLString);
				
				XmlObject xmlData = XmlObject.Factory.newInstance();
				ByteArrayInputStream xstream = new ByteArrayInputStream(inputXMLString.getBytes());
				
				try {
					xmlData = XmlObject.Factory.parse(xstream);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Error in parsing xml: "+e.getLocalizedMessage());
				}	
				outfile = xmlData;
				
				//outfile=new GenericFileData(of, GenericFileDataConstants.MIME_TYPE_PLAIN_TEXT);
			}
		
		
	}

}
