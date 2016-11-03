package org.n52.wps.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.opengis.ows.x11.impl.MimeTypeImpl;

import org.apache.commons.io.IOUtils;
import org.n52.wps.io.data.GenericFileData;
import org.n52.wps.io.data.GenericFileDataConstants;
import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.complex.FileDataBinding;
import org.n52.wps.io.data.binding.complex.GenericFileDataBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;

//org.n52.wps.demo.GPDemoFile

public class CopyOfGPDemoFile extends AbstractSelfDescribingAlgorithm {

	@Override
	public Class<?> getInputDataType(String identifier) {
		if (identifier.equalsIgnoreCase("CHECK")) {
			return org.n52.wps.io.data.binding.literal.LiteralStringBinding.class;
		}
		if (identifier.equalsIgnoreCase("FFF")) {
			return GenericFileDataBinding.class;
		}
		return null;
	}

	@Override
	public Class<?> getOutputDataType(String identifier) {
		if (identifier.equalsIgnoreCase("polygons")) {
			return org.n52.wps.io.data.binding.literal.LiteralStringBinding.class;
		}
		if (identifier.equalsIgnoreCase("file")) {
			return GenericFileDataBinding.class;
		}

		return null;
	}

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputData)
			throws ExceptionReport {
		if (inputData == null || !inputData.containsKey("CHECK")) {
			throw new RuntimeException(
					"Error while allocating input parameters");
		}
		List<IData> dataList = inputData.get("CHECK");
		if (dataList == null || dataList.size() != 1) {
			throw new RuntimeException(
					"Error while allocating input parameters");
		}
		IData data = inputData.get("FFF").get(0);

		if(data instanceof FileDataBinding){
			System.out.println("We got a standard File! ");
			FileDataBinding fileDataBinding = (FileDataBinding)data;
			File file = fileDataBinding.getPayload();
			System.out.println("Here ! "+file.getAbsolutePath());
		}
		GenericFileDataBinding  fileDataBinding = null;
		if(data instanceof GenericFileDataBinding){
			fileDataBinding = (GenericFileDataBinding)data;
			File file = fileDataBinding.getPayload().getBaseFile(false);
			InputStream is = fileDataBinding.getPayload().getDataStream();
			System.out.println("We got a Generic File! "+file.getAbsolutePath());
			StringWriter writer = new StringWriter();
			try {
				IOUtils.copy(is, writer, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
			String theString = writer.toString();
			System.out.println("Content! "+theString);
		}
		
		GenericFileDataBinding binder=null;
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
				binder = new GenericFileDataBinding(new GenericFileData(of, GenericFileDataConstants.MIME_TYPE_PLAIN_TEXT));
				System.out.println("File mime: "+binder.getPayload().getMimeType());
				System.out.println("File extension: "+binder.getPayload().getFileExtension());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		FileDataBinding binder = new FileDataBinding(new File("c:\\Users\\GP\\Desktop\\CoelacanthVelin.jpg"));
			
		HashMap<String, IData> result = new HashMap<String, IData>();
		result.put("polygons", new org.n52.wps.io.data.binding.literal.LiteralStringBinding("hello world"));
		result.put("file", binder);
		return result;
	}

	@Override
	public List<String> getInputIdentifiers() {
		List<String> list = new ArrayList<String>();
		list.add("CHECK");
		list.add("FFF");
		return list;

	}

	@Override
	public List<String> getOutputIdentifiers() {
		List<String> list = new ArrayList<String>();
		list.add("polygons");list.add("file");
		return list;
	}

}
