package org.n52.wps.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.data.binding.bbox.BoundingBoxData;
import org.n52.wps.io.data.binding.complex.FileDataBinding;
import org.n52.wps.server.AbstractSelfDescribingAlgorithm;
import org.n52.wps.server.ExceptionReport;

//org.n52.wps.demo.GPDemo

public class GPDemoSimple extends AbstractSelfDescribingAlgorithm {

	@Override
	public Class<?> getInputDataType(String identifier) {
		if (identifier.equalsIgnoreCase("CHECK")) {
			return org.n52.wps.io.data.binding.literal.LiteralStringBinding.class;
		}
		return null;
	}

	@Override
	public Class<?> getOutputDataType(String identifier) {
		if (identifier.equalsIgnoreCase("polygons")) {
			return org.n52.wps.io.data.binding.literal.LiteralStringBinding.class;
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
		
//		FileDataBinding binder = new FileDataBinding(new File("‪C:\\Users\\GP\\Desktop\\CoelacanthVelin.jpg"));
			
		HashMap<String, IData> result = new HashMap<String, IData>();
		result.put("polygons", new org.n52.wps.io.data.binding.literal.LiteralStringBinding("hello world"));
		return result;
	}

	@Override
	public List<String> getInputIdentifiers() {
		List<String> list = new ArrayList<String>();
		list.add("CHECK");
		return list;

	}

	@Override
	public List<String> getOutputIdentifiers() {
		List<String> list = new ArrayList<String>();
		list.add("polygons");
		return list;
	}

}
