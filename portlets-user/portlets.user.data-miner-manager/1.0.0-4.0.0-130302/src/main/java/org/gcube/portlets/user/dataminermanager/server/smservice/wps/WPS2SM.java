package org.gcube.portlets.user.dataminermanager.server.smservice.wps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.opengis.ows.x11.AllowedValuesDocument.AllowedValues;
import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.ows.x11.ValueType;
import net.opengis.wps.x100.ComplexDataDescriptionType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.LiteralInputType;
import net.opengis.wps.x100.LiteralOutputType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.SupportedComplexDataInputType;
import net.opengis.wps.x100.SupportedComplexDataType;

import org.gcube.portlets.user.dataminermanager.shared.exception.ServiceException;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ColumnListParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ColumnParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.EnumParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.FileParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ListParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.ObjectParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.Parameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.TabularListParameter;
import org.gcube.portlets.user.dataminermanager.shared.parameters.TabularParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WPS2SM {
	private final static String SEPARATOR = "|";

	/*
	 * public enum PrimitiveTypes { STRING, NUMBER, ENUMERATED, CONSTANT,
	 * RANDOM, FILE, MAP, BOOLEAN, IMAGES
	 * 
	 * }
	 */

	private static Logger logger = LoggerFactory.getLogger(WPS2SM.class);

	/**
	 * 
	 * @param title
	 * @param minOcc
	 * @param maxOcc
	 * @param rangeOccs
	 * @param id
	 * @param crs
	 * @return
	 */
	public static Parameter manageBoundingBoxInformation(String title,
			int minOcc, int maxOcc, int rangeOccs, String id, String crs) {
		Parameter converted = null;
		title = "Bounding Box Input in OGC 06-121r3 spec. E.g. 102,46,103,47,urn:ogc:def:crs:EPSG:4328 "
				+ title;
		if (crs != null && crs.length() > 0)
			title += " Supported CRS " + crs;

		title = buildParameterDescription(title, null, null, minOcc, maxOcc,
				null);
		if ((maxOcc == 1) || (maxOcc < 0) || (maxOcc == 0))
			converted = new ObjectParameter(id, title, String.class.getName(),
					" ");

		else
			converted = new ListParameter(id, title, String.class.getName(),
					SEPARATOR);

		return converted;
	}

	/**
	 * 
	 * @param title
	 * @param minOcc
	 * @param maxOcc
	 * @param rangeOccs
	 * @param defaultValue
	 * @param id
	 * @param uoms
	 * @param type
	 * @return
	 */
	public static Parameter manageLiteral(String title, int minOcc, int maxOcc,
			int rangeOccs, String defaultValue, String id, String uoms,
			DomainMetadataType type) {
		Parameter converted = null;
		logger.debug("WPS type:" + type.getStringValue());
		String guessedType = guessWPSLiteralType(type);
		logger.debug("Guessed type: " + guessedType);

		// rebuild title
		if ((defaultValue == null || defaultValue.trim().length() == 0))
			defaultValue = guessDefaultValue(guessedType);

		logger.debug("Guessed default value: " + defaultValue);

		title = buildParameterDescription(title, null, uoms, minOcc, maxOcc,
				defaultValue);
		if ((maxOcc == 1) || (maxOcc < 0) || (maxOcc == 0)) {
			if (title != null && !title.isEmpty()) {
				if (title.contains("[a sequence of names of columns from ")) {
					Pattern pattern = Pattern
							.compile("a sequence of names of columns from (\\w+) separated by (\\p{ASCII})");
					Matcher matcher = pattern.matcher(title);
					logger.debug("Machter title: " + title);
					logger.debug("Machter find: " + matcher.find());
					logger.debug("Machter group: " + matcher.group());
					logger.debug("Machter start: " + matcher.start());
					logger.debug("Machter end: " + matcher.end());
					logger.debug("Machter Group Count: " + matcher.groupCount());
					String referredTabularParameterName = matcher.group(1);
					logger.debug("Matcher referredTabularParameterName: "
							+ referredTabularParameterName);
					String separator = matcher.group(2);
					logger.debug("Matcher separator: " + separator);

					converted = new ColumnListParameter(id, title,
							referredTabularParameterName, separator);
				} else {
					if (title.contains("[the name of a column from ")) {
						Pattern pattern = Pattern
								.compile("the name of a column from (\\w+)");
						Matcher matcher = pattern.matcher(title);
						logger.debug("Machter title: " + title);
						logger.debug("Machter find: " + matcher.find());
						logger.debug("Machter group: " + matcher.group());
						logger.debug("Machter start: " + matcher.start());
						logger.debug("Machter end: " + matcher.end());
						logger.debug("Machter Group Count: "
								+ matcher.groupCount());
						String referredTabularParameterName = matcher.group(1);
						logger.debug("Matcher referredTabularParameterName: "
								+ referredTabularParameterName);

						converted = new ColumnParameter(id, title,
								referredTabularParameterName, defaultValue);
					} else {
						if (title
								.contains("[a sequence of values separated by ")) {
							Pattern pattern = Pattern
									.compile("a sequence of values separated by (\\p{ASCII})");
							Matcher matcher = pattern.matcher(title);
							logger.debug("Machter title: " + title);
							logger.debug("Machter find: " + matcher.find());
							logger.debug("Machter group: " + matcher.group());
							logger.debug("Machter start: " + matcher.start());
							logger.debug("Machter end: " + matcher.end());
							logger.debug("Machter Group Count: "
									+ matcher.groupCount());

							String separator = matcher.group(1);
							logger.debug("Matcher separator: " + separator);

							converted = new ListParameter(id, title,
									guessedType, separator);
						} else {
							converted = new ObjectParameter(id, title,
									guessPrimitiveType(guessedType),
									defaultValue);

						}
					}
				}

			} else {
				converted = new ObjectParameter(id, title,
						guessPrimitiveType(guessedType), defaultValue);
			}

		} else
			converted = new ListParameter(id, title, String.class.getName(),
					SEPARATOR);
		return converted;
	}

	/**
	 * 
	 * @param maxMegaBytes
	 * @param title
	 * @param minOcc
	 * @param maxOcc
	 * @param rangeOccs
	 * @param id
	 * @param defaultType
	 * @return
	 */
	public static Parameter manageComplexData(String maxMegaBytes,
			String title, int minOcc, int maxOcc, int rangeOccs, String id,
			ComplexDataDescriptionType defaultType,
			ComplexDataDescriptionType[] supportedTypes) {
		Parameter converted = null;
		String mimeType = null;
		String schema = null;
		String encoding = null;
		ArrayList<String> supportedMimeTypes = new ArrayList<String>();

		// GenericFileDataConstants.MIME_TYPE_TEXT_XML
		mimeType = defaultType.getMimeType();
		schema = defaultType.getSchema();
		encoding = defaultType.getEncoding();

		logger.debug("Default MimeType: " + mimeType);
		logger.debug("Default Schema: " + schema);
		logger.debug("Default Encoding: " + encoding);

		for (ComplexDataDescriptionType supported : supportedTypes) {
			supportedMimeTypes.add(supported.getMimeType());
		}
		// rebuild title
		title = buildParameterDescription(title, maxMegaBytes, null, minOcc,
				maxOcc, null);
		if ((maxOcc == 1) || (maxOcc < 0) || (maxOcc == 0)) {
			if (title != null && !title.isEmpty()) {
				if (title.contains("[a http link to a table")) {
					converted = new TabularParameter(id, title, " ",
							new ArrayList<String>(), mimeType,
							supportedMimeTypes);
				} else {
					if (title.contains("[a http link to a file")) {
						converted = new FileParameter(id, title, mimeType,
								supportedMimeTypes);
					} else {
						if (title.contains("[a sequence of http links")) {
							Pattern pattern = Pattern
									.compile("\\[a sequence of http links separated by (\\p{ASCII}) , each indicating a table");

							Matcher matcher = pattern.matcher(title);
							boolean match = false;
							if (match = matcher.find()) {
								logger.debug("Machter title: " + title);
								logger.debug("Machter find: " + match);
								logger.debug("Machter group: "
										+ matcher.group());
								logger.debug("Machter start: "
										+ matcher.start());
								logger.debug("Machter end: " + matcher.end());
								logger.debug("Machter Group Count: "
										+ matcher.groupCount());
								String separator = matcher.group(1);
								logger.debug("Matcher separator: " + separator);
								converted = new TabularListParameter(id, title,
										separator, mimeType, supportedMimeTypes);
							} else {
								converted = new FileParameter(id, title,
										mimeType, supportedMimeTypes);
							}
						} else {
							converted = new FileParameter(id, title, mimeType,
									supportedMimeTypes);
						}
					}
				}
			} else {
				converted = new FileParameter(id, title, mimeType,
						supportedMimeTypes);
			}
		} else {
			converted = new FileParameter(id, title, mimeType,
					supportedMimeTypes);
		}
		return converted;
	}

	public static Parameter convert2SMType(InputDescriptionType wpsType)
			throws ServiceException {
		try {

			String id = wpsType.getIdentifier().getStringValue();

			String title = wpsType.getTitle() != null ? wpsType.getTitle()
					.getStringValue() : "";
			int minOcc = wpsType.getMinOccurs().intValue();
			int maxOcc = wpsType.getMaxOccurs().intValue();
			int rangeOccs = maxOcc - minOcc;
			if (rangeOccs == 0)
				rangeOccs = 1;

			// default
			Parameter converted = new ObjectParameter(id, title,
					String.class.getName(), " ");
			if (rangeOccs > 1)
				converted = new ListParameter(id, title,
						String.class.getName(), SEPARATOR);

			// Bounding Boxes
			if (wpsType.isSetBoundingBoxData()) {
				logger.debug("Conversion to SM Type->" + id
						+ " is a Bounding Box Input");
				converted = manageBoundingBoxInformation(title, minOcc, maxOcc,
						rangeOccs, id, wpsType.getBoundingBoxData()
								.getDefault().getCRS());
			}
			// Literals
			else if (wpsType.isSetLiteralData()) {
				logger.debug("Conversion to SM Type->" + id
						+ " is a Literal Input");
				LiteralInputType literal = wpsType.getLiteralData();
				String uoms = literal.getUOMs() == null ? "" : literal
						.getUOMs().getDefault().getUOM().getStringValue();
				String defaultValue = literal.getDefaultValue();
				converted = manageLiteral(title, minOcc, maxOcc, rangeOccs,
						defaultValue, id, uoms, literal.getDataType());
				AllowedValues allowedValues = literal.getAllowedValues();
				if (allowedValues != null) {
					ValueType[] values = allowedValues.getValueArray();
					logger.debug("ValueType[]:" + Arrays.toString(values));
					List<String> enumValues = new ArrayList<>();

					for (ValueType v : values) {
						enumValues.add(v.getStringValue());
					}
					if (values.length > 1) {
						ObjectParameter conv = (ObjectParameter) converted;
						converted = new EnumParameter(conv.getName(),
								conv.getDescription(), enumValues,
								conv.getDefaultValue());
					}
				}
			} else if (wpsType.isSetComplexData()) {
				logger.debug("Conversion to SM Type->" + id
						+ " is a Complex Input");
				SupportedComplexDataInputType complex = wpsType
						.getComplexData();
				String maxMegaBytes = complex.getMaximumMegabytes() != null ? complex
						.getMaximumMegabytes().toString() : "1";
				logger.debug("Max Megabytes: " + maxMegaBytes);
				converted = manageComplexData(maxMegaBytes, title, minOcc,
						maxOcc, rangeOccs, id,
						complex.getDefault().getFormat(), complex
								.getSupported().getFormatArray());

			}
			
			logger.debug("Conversion to SM Type->Name=" + id);
			logger.debug("Conversion to SM Type->Title=" + title);
			logger.debug("Conversion to SM Type->Number of Inputs to Manage="
					+ rangeOccs);

			return converted;

		} catch (Throwable e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	public static Parameter convert2SMType(OutputDescriptionType wpsType) {

		String id = wpsType.getIdentifier().getStringValue();
		String title = wpsType.getTitle() != null ? wpsType.getTitle()
				.getStringValue() : "";

		// default
		Parameter converted = new ObjectParameter(id, title,
				String.class.getName(), " ");

		logger.debug("Conversion to SM Type->Output id:" + id);
		logger.debug("Conversion to SM Type->Title:" + title);

		// Bounding Boxes
		if (wpsType.isSetBoundingBoxOutput()) {
			logger.debug("Bounding Box Output");
			converted = manageBoundingBoxInformation(title, -1, -1, -1, id, "");
		}
		// Literals
		else if (wpsType.isSetLiteralOutput()) {
			logger.debug("Literal Output");
			LiteralOutputType literal = wpsType.getLiteralOutput();
			String uoms = literal.getUOMs() == null ? "" : literal.getUOMs()
					.toString();
			converted = manageLiteral(title, -1, -1, -1, "", id, uoms,
					literal.getDataType());
		} else if (wpsType.isSetComplexOutput()) {
			logger.debug("Complex Output");
			SupportedComplexDataType complex = wpsType.getComplexOutput();
			converted = manageComplexData("", title, -1, -1, -1, id, complex
					.getDefault().getFormat(), complex.getSupported()
					.getFormatArray());
		}

		return converted;
	}

	/**
	 * 
	 * @param title
	 * @param maxMegabytes
	 * @param UoM
	 * @param minElements
	 * @param maxElements
	 * @param defaultValue
	 * @return
	 */
	public static String buildParameterDescription(String title,
			String maxMegabytes, String UoM, int minElements, int maxElements,
			String defaultValue) {

		String description = title;
		/*
		String innerDescription = "";

		if (maxMegabytes != null && maxMegabytes.trim().length() > 0) {
			innerDescription += "Max MB Size:" + maxMegabytes.trim() + "; ";
		}
		if (UoM != null && UoM.trim().length() > 0) {
			innerDescription += "Unit of Measure:" + UoM.trim() + "; ";
		}
		if (minElements > 0) {
			innerDescription += "Min N. of Entries:" + minElements + "; ";
		}
		if (maxElements > 0) {
			innerDescription += "Max N. of Entries:" + maxElements + "; ";
		}
		if (defaultValue != null && defaultValue.trim().length() > 0) {
			innerDescription += "default:" + defaultValue.trim() + "; ";
		}

		if (innerDescription.length() > 0)
			description += " ["
					+ innerDescription.substring(0,
							innerDescription.lastIndexOf(";")).trim() + "]";
		*/
		return description;

	}

	public static String guessWPSLiteralType(DomainMetadataType type) {

		if (type == null || type.getStringValue() == null)
			return String.class.getName();
		else {
			String typeS = type.getReference().trim();
			if (typeS.length() == 0)
				return String.class.getName();
			else if (typeS.contains("float") || typeS.contains("double")
					|| typeS.contains("decimal"))
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

	public static String guessPrimitiveType(String type) {
		if (type.equals(Integer.class.getName())) {
			return Integer.class.getName();
		} else if (type.equals(String.class.getName())) {
			return String.class.getName();
		} else if (type.equals(Boolean.class.getName())) {
			return Boolean.class.getName();
		} else if (type.equals(Double.class.getName())) {
			return Double.class.getName();
		} else if (type.equals(Float.class.getName())) {
			return Float.class.getName();
		} else
			return null;

	}

}
