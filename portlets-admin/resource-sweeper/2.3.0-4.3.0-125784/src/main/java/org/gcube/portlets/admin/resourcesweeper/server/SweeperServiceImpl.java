package org.gcube.portlets.admin.resourcesweeper.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.portlets.admin.resourcesweeper.client.async.SweeperService;
import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;
import org.gcube.resourcemanagement.support.server.sweeper.Sweeper;
import org.gcube.resourcemanagement.support.shared.util.SweeperActions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SweeperServiceImpl extends RemoteServiceServlet implements	SweeperService {

	private static final Logger _log = LoggerFactory.getLogger(SweeperServiceImpl.class);
	/**
	 * 
	 */
	@Override
	public List<String> getSweepElems(String scope, SweeperActions action) {
		Sweeper sweeper = new Sweeper();
		try {
			switch (action) {
			case GET_GHN_MOVE_TO_UNREACHABLE: {
				List<String> ghnList = sweeper.getExpiredGHNs(new ScopeBean(scope));
				return filterExpiredGHNs(ghnList);
			}
			case GET_GHN_DELETE:
				return sweeper.getDeadGHNs(new ScopeBean(scope));
			case GET_RI_DELETE:
				return sweeper.getOrphanRI(new ScopeBean(scope));
			default:
				return null;
			}
		} catch (Exception e) {
			_log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Boolean applySweep(String scope, List<ModelData> selectedElems) {
		Sweeper sweeper = new Sweeper();
		System.out.println("Applying sweep");
		for (ModelData selectedModel : selectedElems) {
			_log.trace(((Object) selectedModel.get("ID")).toString() + " :: " +((Object) selectedModel.get("Actions")).toString());
		}
		try {
			return sweeper.applySweep(ScopeManager.getScope(scope), selectedElems);
		} catch (Exception e) {
			_log.error(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * self explaining
	 * @param allGHNs
	 * @return the filtered ghn
	 * @throws Exception
	 */
	private ArrayList<String> filterExpiredGHNs(List<String> allGHNs) throws Exception {

		ArrayList<String> expiredGHNsToReturn = new ArrayList<String>();

		ArrayList<String> expiredGHNs = new ArrayList<String>();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		DefaultHandler handler = getParserHandler(expiredGHNs);

		for (String ghn : allGHNs) {
			saxParser.parse(new InputSource(new StringReader(ghn)), handler);
		}

		for (String ghn : allGHNs) {
			for (String expiredGHNid : expiredGHNs) 
				if (ghn.contains(expiredGHNid.split(",")[0])) {
					ghn = ghn.replaceAll("</Resource>", "");
					ghn += "<UpdateMinutesElapsed>" + expiredGHNid.split(",")[1] + "</UpdateMinutesElapsed></Resource>";
					expiredGHNsToReturn.add(ghn);
					break;
				}
		}

		return expiredGHNsToReturn;
	}
	private DefaultHandler getParserHandler(final ArrayList<String> expiredGHNs) throws Exception	{
		DefaultHandler handler = new DefaultHandler() {

			int liveMaxGHNMinutes = Integer.parseInt(Sweeper.LIVE_GHN_MAX_MINUTES);
			boolean isID = false;
			boolean isLastUpdate = false;
			String tempID = "";

			Calendar lastUpTimeDate ; 

			public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
				if (qName.equalsIgnoreCase("ID")) {
					isID = true;
				}
				if (qName.equalsIgnoreCase("LastUpdate")) {
					isLastUpdate = true;
				}
			}
			public void characters(char ch[], int start, int length) throws SAXException {

				if (isID) {
					tempID =  new String(ch, start, length);
					isID = false;
				}

				if (isLastUpdate) {
					String lastUpTime = new String(ch, start, length);
					lastUpTimeDate = DatatypeConverter.parseDateTime(lastUpTime);

					Calendar now = Calendar.getInstance();
					now.setTime(new Date());

					long diffInMinutes = (now.getTimeInMillis() - lastUpTimeDate.getTimeInMillis()) / (1000 * 60);

					if (diffInMinutes > liveMaxGHNMinutes) {
						expiredGHNs.add(tempID+","+diffInMinutes);
					}					
					tempID = "";
					isLastUpdate = false;
				}
			}
		};
		return handler;
	}
	/**
	 * 
	 * @param xml
	 * @param xslt
	 * @return the html string
	 * @throws Exception
	 */
	public static String XML2HTML(final String xml, final String xslt)
			throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();

		InputStream stream = new FileInputStream(xslt);
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		StringBuilder retval = new StringBuilder();
		String currLine = null;

		while ((currLine = in.readLine()) != null) {
			// a comment
			if (currLine.trim().length() > 0 && currLine.trim().startsWith("#")) {
				continue;
			}
			if (currLine.trim().length() == 0) { continue; }
			retval.append(currLine + System.getProperty("line.separator"));
		}
		in.close();


		StreamSource source = new StreamSource(new ByteArrayInputStream(retval.toString().getBytes()));
		Templates compiledXSLT = tf.newTemplates(source);
		Transformer t = compiledXSLT.newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "true");
		StringWriter w = new StringWriter();
		t.transform(new StreamSource(new StringReader(xml)), new StreamResult(w));
		return w.toString();
	}


}
