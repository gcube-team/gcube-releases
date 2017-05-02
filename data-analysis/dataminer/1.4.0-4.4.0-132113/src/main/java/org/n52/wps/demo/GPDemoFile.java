package org.n52.wps.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings.CsvFileDataBinding;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings.D4ScienceFileDataBinding;
import org.gcube.dataanalysis.wps.statisticalmanager.synchserver.bindings.GisLinkDataBinding;
import org.n52.wps.algorithm.annotation.*;
import org.n52.wps.io.data.*;
import org.n52.wps.io.data.binding.complex.*;
import org.n52.wps.io.data.binding.literal.*;
import org.n52.wps.server.*;

//org.n52.wps.demo.GPDemoFile
@Algorithm(statusSupported=false, title="title of the A.", abstrakt="hello worlds", identifier="org.n52.wps.demo.GPDemoFile", version = "1.1.0")
public class GPDemoFile extends AbstractAnnotatedAlgorithm {

	private String data;
	private GenericFileData file;
	private GenericFileData gis;
	private GenericFileData outfile;
	private String lout;
	private GenericFileData link;

	@LiteralDataInput(allowedValues= {"c","a"},defaultValue="1", identifier = "CHECK", binding = LiteralStringBinding.class)
    public void setLData(String data) {
        this.data = data;
    }
	
	@ComplexDataInput(abstrakt="", title="", maxOccurs=1, minOccurs=1, identifier = "FFF", binding = D4ScienceFileDataBinding.class)
	public void setCDataType(GenericFileData file) {
		this.file=file;
	}

	@ComplexDataInput(identifier = "GIS", binding = GisLinkDataBinding.class)
	public void setGisDataType(GenericFileData file) {
		this.gis=file;
	}
	
	@ComplexDataOutput(identifier = "file", binding = D4ScienceFileDataBinding.class)
	public GenericFileData getFile() {
        return outfile;
    }
	
	@LiteralDataOutput(identifier = "lout", binding = LiteralStringBinding.class)
	public String getLiteral() {
        return lout;
    }
	
	
	@ComplexDataOutput(identifier = "link", binding = GisLinkDataBinding.class)
	public GenericFileData getGisLink() {
        return link;
    }
	
	@Execute
	public void run() {

		if(file!=null){
			File f = file.getBaseFile(false);
			InputStream is = file.getDataStream();
			System.out.println("We got a Generic File! "+f.getAbsolutePath());
			StringWriter writer = new StringWriter();
			try {
				IOUtils.copy(is, writer, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
			String theString = writer.toString();
			System.out.println("Content: "+theString);
			lout = "OK";
		}
		
		if(gis!=null){
			File f = gis.getBaseFile(false);
			InputStream is = file.getDataStream();
			System.out.println("We got a GIS Link! "+f.getAbsolutePath());
			StringWriter writer = new StringWriter();
			try {
				IOUtils.copy(is, writer, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
			String theString = writer.toString();
			System.out.println("Gis Content: "+theString);
			
		}
		
//		PngFileDataBinding binder=null;
		try {
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
				outfile=new GenericFileData(of, "image/png");
				System.out.println("Generating GIS Link binding");
				//outfile=new GenericFileData(of, GenericFileDataConstants.MIME_TYPE_PLAIN_TEXT);
				link=new GenericFileData(of, "text/wfs");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
