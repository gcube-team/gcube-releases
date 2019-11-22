package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.NetCDFValueReader;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReader;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderBoolean;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderByte;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderChar;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderDouble;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderFloat;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderInt;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderLong;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderShort;
import org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior.ValueReaderString;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.exception.ServiceException;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.AttributeData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.DimensionData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFDetailData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFId;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.RangeData;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.VariableData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.Array;
import ucar.ma2.Range;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NetCDFResource {
	
	private static Logger logger = LoggerFactory.getLogger(NetCDFId.class);

	private URL publicLink;
	private Path ncFile = null;
	private NetcdfFile netcdfFile;
	private NetCDFId netCDFId = null;

	/*
	 * { System.loadLibrary("gdaljni"); }
	 */


	public NetCDFResource(URL publicLink) throws ServiceException {
		this.publicLink = publicLink;
		retrievePublicLink();
		this.netCDFId = new NetCDFId(ncFile.toAbsolutePath().toString());

	}

	public NetCDFResource(NetCDFId netCDFId) throws ServiceException {
		this.netCDFId = netCDFId;
		ncFile = Paths.get(netCDFId.getId());

	}

	public NetCDFId getNetCDFId() {
		return netCDFId;
	}

	private void retrievePublicLink() throws ServiceException {
		try {
			ncFile = Files.createTempFile("NetCDFFile_", ".nc");

			logger.debug("NetCDF Temp file: " + ncFile.toAbsolutePath().toString());

			URLConnection uc = publicLink.openConnection();
			try (InputStream is = uc.getInputStream()) {
				BufferedInputStream bis = new BufferedInputStream(is);
				OutputStream os = Files.newOutputStream(ncFile, StandardOpenOption.WRITE);
				BufferedOutputStream bos = new BufferedOutputStream(os);
				int data;
				while ((data = bis.read()) != -1) {
					bos.write(data);
				}
			}

		} catch (IOException e) {
			logger.error("Error retriving publicLink: " + publicLink);
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException("Error retrieving publicLink: " + e.getLocalizedMessage(), e);
		}
	}

	/*
	 * public void gdalExplore() { stringBuilder.append("GDAL: " +
	 * gdal.VersionInfo()); gdal.AllRegister(); stringBuilder.append("FILE: " +
	 * filename);
	 * 
	 * Dataset dataset = gdal.Open(filename);//
	 * stringBuilder.append("Projection: " + dataset.GetProjection());
	 * stringBuilder.append("ProjectionRef: " + dataset.GetProjectionRef());
	 * 
	 * Vector<?> vector = dataset.GetMetadata_List(); for (Object i : vector) {
	 * stringBuilder.append("MetaData: " + i); }
	 * 
	 * int numBands = dataset.getRasterCount();
	 * stringBuilder.append("NumBands: " + numBands); }
	 */

	private void openNetcdfFile() throws ServiceException {
		try {
			netcdfFile = NetcdfFile.open(ncFile.toAbsolutePath().toString());
			return;
		} catch (Throwable e) {
			logger.error("Error in NetcdfFile open: " + ncFile.toAbsolutePath().toString());
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException("Error in NetcdfFile open", e);
		}

	}

	public void close() throws ServiceException {
		try {
			deleteCNFile();
		} catch (Throwable e) {
			logger.error(e.getLocalizedMessage(), e);
			throw new ServiceException("Error in close: " + e.getLocalizedMessage(), e);
		}

	}

	private void closeNetcdfFile() throws ServiceException {
		try {
			logger.debug("Close NetcdfFile");
			if (netcdfFile != null) {
				netcdfFile.close();
			}
			return;
		} catch (Throwable e) {
			logger.error("Error in netcdfFile close: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}

	}

	private void deleteCNFile() throws ServiceException {
		try {
			if (ncFile != null) {
				logger.debug("Delete ncFile: " + ncFile.toAbsolutePath().toString());
				Files.delete(ncFile);
			}
		} catch (Throwable e) {
			logger.error("Error deleting ncFile: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage(), e);
		}
	}

	public NetCDFData exploreNetCDF() throws ServiceException {
		openNetcdfFile();
		NetCDFData netCDFData = process();
		closeNetcdfFile();
		return netCDFData;
	}

	private NetCDFData process() {
		NetCDFDetailData netCDFDetailData = processDetail();
		ArrayList<DimensionData> dimensions = processDimensions();
		ArrayList<VariableData> variables = processVariables();
		NetCDFData netCDFData = new NetCDFData(netCDFId, netCDFDetailData, dimensions, variables);
		return netCDFData;

	}

	private NetCDFDetailData processDetail() {

		// String info = netcdfFile.getDetailInfo();
		String typeId = netcdfFile.getFileTypeId();
		String typeDescription = netcdfFile.getFileTypeDescription();
		String typeVersion = netcdfFile.getFileTypeVersion();
		// String title = netcdfFile.getTitle();

		List<Attribute> globalAttrs = netcdfFile.getGlobalAttributes();
		ArrayList<AttributeData> attributeDataList=readAttributes(globalAttrs);

		NetCDFDetailData netCDFDetailData = new NetCDFDetailData(typeId, typeDescription, typeVersion, attributeDataList);
		return netCDFDetailData;

	}

	private ArrayList<AttributeData> readAttributes(List<Attribute> attributes) {
		ArrayList<AttributeData> attributeDataList = new ArrayList<>();

		for (int i = 0; i < attributes.size(); i++) {
			Attribute attr = attributes.get(i);
			String values = new String();
			if (attr.isArray()) {
				Array vals = attr.getValues();
				values = vals.toString();
			} else {
				if (attr.isString()) {
					values = attr.getStringValue();
				} else {
					Number num = attr.getNumericValue();
					switch (attr.getDataType()) {
					case BYTE:
						values = String.valueOf(num.byteValue());
						break;
					case SHORT:
						values = String.valueOf(num.shortValue());
						break;
					case INT:
						values = String.valueOf(num.intValue());
						break;
					case FLOAT:
						values = String.valueOf(num.floatValue());
						break;
					case DOUBLE:
						values = String.valueOf(num.doubleValue());
						break;
					case LONG:
						values = String.valueOf(num.longValue());
						break;
					default:
						break;

					}
				}
			}
			AttributeData attributeData=new AttributeData(i, attr.getFullName(), attr.getDataType().name(), values);
			attributeDataList.add(attributeData);
		}
		
		return attributeDataList;
	}

	private ArrayList<VariableData> processVariables() {
		ArrayList<VariableData> varsData = new ArrayList<>();
		List<Variable> variables = netcdfFile.getVariables();
		for (int i = 0; i < variables.size(); i++) {
			Variable variable = variables.get(i);
			ArrayList<DimensionData> dimsData = new ArrayList<>();
			List<Dimension> dims = variable.getDimensions();
			for (int j = 0; j < dims.size(); j++) {
				Dimension dim = dims.get(j);
				DimensionData dimData = new DimensionData(j, dim.getFullName(), dim.getLength(), dim.isUnlimited(),
						dim.isVariableLength(), dim.isShared());
				dimsData.add(dimData);
			}

			List<Attribute> attributes = variable.getAttributes();
			ArrayList<AttributeData> attrsData = readAttributes(attributes);
			
			List<Range> ranges=variable.getRanges();
			ArrayList<RangeData> rangesData=new ArrayList<>();
			
			for(int k=0;k<ranges.size();k++){
				Range range=ranges.get(k);
				RangeData rangeData=new RangeData(k,range.length(),range.first(),range.stride(),range.getName() );
				rangesData.add(rangeData);
			}
			
			VariableData variableData = new VariableData(i, variable.getFullName(), variable.getUnitsString(),
					variable.getDataType().name(), variable.getDimensionsString(), variable.getRank(),
					variable.isCoordinateVariable(), variable.isScalar(), variable.isImmutable(),
					variable.isUnlimited(), variable.isUnsigned(), variable.isVariableLength(),
					variable.isMemberOfStructure(), dimsData, attrsData,rangesData);

			varsData.add(variableData);

		}
		return varsData;
	}

	private ArrayList<DimensionData> processDimensions() {
		ArrayList<DimensionData> dimsData = new ArrayList<>();
		List<Dimension> dims = netcdfFile.getDimensions();
		for (int i = 0; i < dims.size(); i++) {
			Dimension dim = dims.get(i);
			DimensionData dimData = new DimensionData(i, dim.getFullName(), dim.getLength(), dim.isUnlimited(),
					dim.isVariableLength(), dim.isShared());
			dimsData.add(dimData);

		}
		return dimsData;

	}

	public NetCDFValues readDataVariable(VariableData variableData, boolean sample, int limit) throws ServiceException {
		openNetcdfFile();
		NetCDFValues netCDFValues = readDataInVariable(variableData, sample, limit);
		closeNetcdfFile();
		return netCDFValues;

	}

	private NetCDFValues readDataInVariable(VariableData variableData, boolean sample, int limit)
			throws ServiceException {
		List<Variable> vars = netcdfFile.getVariables();
		Variable foundVar = null;
		for (Variable var : vars) {
			if (var.getFullName().compareTo(variableData.getFullName()) == 0) {
				foundVar = var;
				break;
			}
		}
		if (foundVar == null) {
			throw new ServiceException("Variable " + variableData.getFullName() + "not found!");
		}

		return elaborateVariable(foundVar, sample, limit);
	}

	private NetCDFValues elaborateVariable(Variable variable, boolean sample, int limit) {
		ValueReader valueReader = new ValueReader();
		switch (variable.getDataType()) {
		case BOOLEAN:
			valueReader = new ValueReaderBoolean();
			break;
		case BYTE:
			valueReader = new ValueReaderByte();
			break;
		case CHAR:
			valueReader = new ValueReaderChar();
			break;
		case DOUBLE:
			valueReader = new ValueReaderDouble();
			break;
		case ENUM1:
			break;
		case ENUM2:
			break;
		case ENUM4:
			break;
		case FLOAT:
			valueReader = new ValueReaderFloat();
			break;
		case INT:
			valueReader = new ValueReaderInt();
			break;
		case LONG:
			valueReader = new ValueReaderLong();
			break;
		case OBJECT:
			break;
		case OPAQUE:
			break;
		case SEQUENCE:
			break;
		case SHORT:
			valueReader = new ValueReaderShort();
			break;
		case STRING:
			valueReader = new ValueReaderString();
			break;
		case STRUCTURE:
			break;
		default:
			break;

		}

		NetCDFValues netCDFValues = null;
		NetCDFValueReader netCDFValueReader = new NetCDFValueReader();
		if (sample) {
			netCDFValues = netCDFValueReader.sample(variable, limit, valueReader);
		} else {
			netCDFValues = netCDFValueReader.apply(variable, valueReader);
		}

		return netCDFValues;

	}

}
