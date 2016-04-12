package org.gcube.datatransformation.datatransformationlibrary.datahandlers.impl.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.impl.LocalFileDataElement;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.tmpfilemanagement.TempFileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

/**
 * Encapsulates a {@link DataElement}.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class RSDataElementUtil {
	private static final String ROOT = "RSDataElement";
	private static final String OID = "oid";
	private static final String CONTENTTYPE = "contenttype";
	private static final String MIMETYPE = "mimetype";
	private static final String PARAMETERS = "parameters";
	private static final String ATTRIBUTES = "attributes";
	private static String subdir = null;
	
	
	public static String dataElementMetadataToXML(DataElement de) {
		StringBuilder metadata = new StringBuilder();
		metadata.append("<" + ROOT + " ");
		metadata.append(OID + "=\"" + XMLUtils.DoReplaceSpecialCharachters(de.getId()) + "\" ");
		metadata.deleteCharAt(metadata.length() - 1);
		metadata.append(">");
		metadata.append("<" + CONTENTTYPE + " ");
		metadata.append(MIMETYPE + "=\"" + XMLUtils.DoReplaceSpecialCharachters(de.getContentType().getMimeType()) + "\" ");
		metadata.deleteCharAt(metadata.length() - 1);
		metadata.append(">");
		if (de.getContentType().getContentTypeParameters() != null && !de.getContentType().getContentTypeParameters().isEmpty()) {
			metadata.append("<" + PARAMETERS + " ");
			for (Parameter param : de.getContentType().getContentTypeParameters()) {
				metadata.append(XMLUtils.DoReplaceSpecialCharachters(param.getName()) + "=\"" + XMLUtils.DoReplaceSpecialCharachters(param.getValue()) + "\" ");
			}
			metadata.deleteCharAt(metadata.length() - 1);
			metadata.append("/>");
		}
		metadata.append("</" + CONTENTTYPE + ">");
		if (!de.getAllAttributes().isEmpty()) {
			metadata.append("<" + ATTRIBUTES + " ");
			for (String attrName : de.getAllAttributes().keySet()) {
				metadata.append(XMLUtils.DoReplaceSpecialCharachters(attrName) + "=\"" + XMLUtils.DoReplaceSpecialCharachters(de.getAttributeValue(attrName))
						+ "\" ");
			}
			metadata.deleteCharAt(metadata.length() - 1);
			metadata.append("/>");
		}
		metadata.append("</" + ROOT + ">");

		return metadata.toString();
	}

	public static File dataElementContentToFile(DataElement de) {
		try {
			if (subdir == null) {
				subdir = TempFileManager.genarateTempSubDir();
				new File(subdir);
			}
			
			String tempFile = TempFileManager.generateTempFileName(subdir);

			File f = new File(tempFile);
			InputStream inputStream = de.getContent();
			OutputStream out = null;
			out = new FileOutputStream(f);

			byte buf[] = new byte[4096];
			int len;
			while ((len = inputStream.read(buf)) > 0)
				out.write(buf, 0, len);
			out.close();

			inputStream.close();
			
			return f;
		} catch (Exception e) {
			return null;
		}
	}

	public static DataElement dataElementFromRS(String metadata, File file) throws Exception {
		LocalFileDataElement de = new LocalFileDataElement();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document xmlDoc = db.parse(new InputSource(new StringReader(metadata)));

		Element xmlDocEl = (Element) xmlDoc.getElementsByTagName(ROOT).item(0);
		de.setId(XMLUtils.UndoReplaceSpecialCharachters(xmlDocEl.getAttribute(OID)));

		xmlDocEl = (Element) xmlDocEl.getElementsByTagName(CONTENTTYPE).item(0);
		ContentType ct = new ContentType();
		ct.setMimeType(XMLUtils.UndoReplaceSpecialCharachters(xmlDocEl.getAttribute(MIMETYPE)));

		xmlDocEl = (Element) xmlDocEl.getElementsByTagName(PARAMETERS).item(0);
		if (xmlDocEl != null) {
			List<Parameter> pars = new ArrayList<Parameter>();
			NamedNodeMap parmap = xmlDocEl.getAttributes();

			for (int i = 0; i < parmap.getLength(); i++) {
				String par = parmap.item(i).getNodeName();
				pars.add(new Parameter(par, XMLUtils.UndoReplaceSpecialCharachters(xmlDocEl.getAttribute(par))));
			}
			ct.setContentTypeParameters(pars);
		}
		de.setContentType(ct);

		xmlDocEl = (Element) xmlDoc.getElementsByTagName(ROOT).item(0);
		xmlDocEl = (Element) xmlDocEl.getElementsByTagName(ATTRIBUTES).item(0);

		if (xmlDocEl != null) {
			NamedNodeMap attrmap = xmlDocEl.getAttributes();

			for (int i = 0; i < attrmap.getLength(); i++) {
				String attr = attrmap.item(i).getNodeName();
				de.setAttribute(attr, XMLUtils.UndoReplaceSpecialCharachters(xmlDocEl.getAttribute(attr)));
			}
		}

		de.setContent(file);

		return de;
	}

	public static String stringFromInputStream(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		in.close();
		return out.toString();
	}
}
