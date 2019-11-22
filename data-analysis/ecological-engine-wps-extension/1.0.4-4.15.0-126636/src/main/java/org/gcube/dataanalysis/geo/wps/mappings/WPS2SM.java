package org.gcube.dataanalysis.geo.wps.mappings;

import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import net.opengis.wps.x100.LiteralOutputType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import net.opengis.wps.x100.SupportedComplexDataType;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.StatisticalType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.n52.wps.io.data.GenericFileDataConstants;

public class WPS2SM {
	// name: abstract, max megabytes, uom, min elements, max elements, default value

	public static StatisticalType manageBoundingBoxInformation(String Abstract, int minOcc, int maxOcc, int rangeOccs, String title, String crs) {
		StatisticalType converted = null;
		Abstract = "Bounding Box Input in OGC 06-121r3 spec. E.g. 102,46,103,47,urn:ogc:def:crs:EPSG:4328 "+ Abstract;
		if (crs!=null && crs.length()>0)
			Abstract+=" Supported CRS "+crs;
		
		Abstract = buildParameterDescription(Abstract, null, null, minOcc, maxOcc, null);
		if ((maxOcc == 1)||(maxOcc<0)||(maxOcc == 0))
			converted = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, title, Abstract, " ",true);
		else
			converted = new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, title, Abstract, true);

		return converted;
	}

	public static StatisticalType manageLiteral(String Abstract, int minOcc, int maxOcc, int rangeOccs, String defaultValue, String title, String uoms, DomainMetadataType type) {
		StatisticalType converted = null;
		AnalysisLogger.getLogger().debug("WPS type:" + type.getStringValue());
		String guessedType = guessWPSLiteralType(type);
		AnalysisLogger.getLogger().debug("Guessed type: " + guessedType);
		
		// rebuild Abstract
		if ((defaultValue==null || defaultValue.trim().length()==0))
			defaultValue=guessDefaultValue(guessedType);
		
		AnalysisLogger.getLogger().debug("Guessed default value: " + defaultValue);
		
		Abstract = buildParameterDescription(Abstract, null, uoms, minOcc, maxOcc, defaultValue);
		if ((maxOcc == 1)||(maxOcc<0)||(maxOcc == 0))
			converted = new PrimitiveType(guessedType, null, guessPrimitiveType(guessedType), title, Abstract, defaultValue,true);
		else
			converted = new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, title, Abstract, true);
		return converted;
	}

	public static StatisticalType manageComplexData(String maxMegaBytes, String Abstract, int minOcc, int maxOcc, int rangeOccs, String title, ComplexDataDescriptionType type) {
		StatisticalType converted = null;
		String mimeType = null;
		String schema = null;
		String encoding = null;
//		GenericFileDataConstants.MIME_TYPE_TEXT_XML
		mimeType = type.getMimeType();
		schema = type.getSchema();
		encoding = type.getEncoding();

		AnalysisLogger.getLogger().debug("MimeType: " + mimeType);
		AnalysisLogger.getLogger().debug("Schema: " + schema);
		AnalysisLogger.getLogger().debug("Encoding: " + encoding);

		// rebuild Abstract
		Abstract = buildParameterDescription(Abstract, maxMegaBytes, null, minOcc, maxOcc, null);
		if ((maxOcc == 1)||(maxOcc<0)||(maxOcc == 0))
			converted = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, title, Abstract," ",true);
		else
			converted = new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, title, Abstract, true);

		return converted;
	}

	public static StatisticalType convert2SMType(InputDescriptionType wpsType) {

		String id = wpsType.getIdentifier().getStringValue();
		String Abstract = wpsType.getAbstract()!=null?wpsType.getAbstract().getStringValue():"";
		int minOcc = wpsType.getMinOccurs().intValue();
		int maxOcc = wpsType.getMaxOccurs().intValue();
		int rangeOccs = maxOcc - minOcc;
		if (rangeOccs == 0)
			rangeOccs = 1;

		// default
		StatisticalType converted = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, id, Abstract," ",true);
		if (rangeOccs > 1)
			converted = new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, id, Abstract, true);

		// Bounding Boxes
		if (wpsType.isSetBoundingBoxData()){
			AnalysisLogger.getLogger().debug("Conversion to SM Type->" + id+" is a Bounding Box Input");
			converted = manageBoundingBoxInformation(Abstract, minOcc, maxOcc, rangeOccs, id, wpsType.getBoundingBoxData().getDefault().getCRS());
		}
		// Literals
		else if (wpsType.isSetLiteralData()) {
			AnalysisLogger.getLogger().debug("Conversion to SM Type->" + id+" is a Literal Input");
			LiteralInputType literal = wpsType.getLiteralData();
			String uoms = literal.getUOMs() == null ? "" : literal.getUOMs().getDefault().getUOM().getStringValue();
			String defaultValue = literal.getDefaultValue();
			converted = manageLiteral(Abstract, minOcc, maxOcc, rangeOccs, defaultValue, id, uoms, literal.getDataType());
		} else if (wpsType.isSetComplexData()) {
			AnalysisLogger.getLogger().debug("Conversion to SM Type->" + id+" is a Complex Input");
			SupportedComplexDataInputType complex = wpsType.getComplexData();
			String maxMegaBytes = complex.getMaximumMegabytes()!=null?complex.getMaximumMegabytes().toString():"1";
			AnalysisLogger.getLogger().debug("Max Megabytes: " + maxMegaBytes);
			converted = manageComplexData(maxMegaBytes, Abstract, minOcc, maxOcc, rangeOccs, id, complex.getDefault().getFormat());
		}

		AnalysisLogger.getLogger().debug("Conversion to SM Type->Abstract:" + Abstract);
		AnalysisLogger.getLogger().debug("Conversion to SM Type->Name:" + id);
		AnalysisLogger.getLogger().debug("Conversion to SM Type->Number of Inputs to Manage:" + rangeOccs);
		
		return converted;
	}

	public static StatisticalType convert2SMType(OutputDescriptionType wpsType) {

		String id = wpsType.getIdentifier().getStringValue();
		String Abstract = wpsType.getAbstract()!=null?wpsType.getAbstract().getStringValue():"";
		
		// default
		StatisticalType converted = new PrimitiveType(String.class.getName(), null, PrimitiveTypes.STRING, id, Abstract," ",true);

		AnalysisLogger.getLogger().debug("Conversion to SM Type->Output id:" + id);
		AnalysisLogger.getLogger().debug("Conversion to SM Type->Abstract:" + Abstract);
		

		// Bounding Boxes
		if (wpsType.isSetBoundingBoxOutput()){
			AnalysisLogger.getLogger().debug("Bounding Box Output");
			converted = manageBoundingBoxInformation(Abstract, -1, -1, -1, id,"");
		}
		// Literals
		else if (wpsType.isSetLiteralOutput()) {
			AnalysisLogger.getLogger().debug("Literal Output");
			LiteralOutputType literal = wpsType.getLiteralOutput();
			String uoms = literal.getUOMs() == null ? "" : literal.getUOMs().toString();
			converted = manageLiteral(Abstract, -1, -1, -1, "", id, uoms, literal.getDataType());
		} else if (wpsType.isSetComplexOutput()) {
			AnalysisLogger.getLogger().debug("Complex Output");
			SupportedComplexDataType complex = wpsType.getComplexOutput();
			converted = manageComplexData("", Abstract, -1, -1, -1, id, complex.getDefault().getFormat());
		}

		return converted;
	}
	
	// name: abstract, max megabytes, uom, min elements, max elements, default value
	public static String buildParameterDescription(String Abstract, String maxMegabytes, String UoM, int minElements, int maxElements, String defaultValue) {

		String description = Abstract;

		String innerDescription = "";

		if (maxMegabytes != null && maxMegabytes.trim().length() > 0) {
			innerDescription += "Max MB Size:" + maxMegabytes.trim() + "; ";
		}
		if (UoM != null && UoM.trim().length() > 0) {
			innerDescription += "Unit of Measure:" + UoM.trim() + "; ";
		}
		if (minElements>0) {
			innerDescription += "Min N. of Entries:" + minElements+ "; ";
		}
		if (maxElements >0) {
			innerDescription += "Max N. of Entries:" + maxElements+ "; ";
		}
		if (defaultValue != null && defaultValue.trim().length() > 0) {
			innerDescription += "default:" + defaultValue.trim() + "; ";
		}

		if (innerDescription.length()>0)
			description += " [" + innerDescription.substring(0,innerDescription.lastIndexOf(";")).trim() + "]";

		return description;

	}

	public static String guessWPSLiteralType(DomainMetadataType type) {

		if (type == null || type.getStringValue() == null)
			return String.class.getName();
		else {
			String typeS = type.getReference().trim();
			if (typeS.length() == 0)
				return String.class.getName();
			else if (typeS.contains("float") || typeS.contains("double") || typeS.contains("decimal"))
				return Double.class.getName();
			else if (typeS.contains("int"))
				return Integer.class.getName();
			else if (typeS.contains("long"))
				return Long.class.getName();
			else if (typeS.contains("short"))
				return Short.class.getName();
		}

		return String.class.getName();
	}

	public static String guessDefaultValue(String type) {
		if (type.equals(String.class.getName()))
			return " ";
		else
			return "0";
	}
	
	public static PrimitiveTypes guessPrimitiveType(String type) {
		if (type.equals(String.class.getName()))
			return PrimitiveTypes.STRING;
		else
			return PrimitiveTypes.NUMBER;
	}
	
}
