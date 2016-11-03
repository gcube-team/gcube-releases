package org.gcube.portlets.admin.vredeployment.server;

import static org.gcube.vremanagement.vremodel.cl.plugin.AbstractPlugin.factory;
import static org.gcube.vremanagement.vremodel.cl.plugin.AbstractPlugin.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.framework.vremanagement.vremanagement.impl.VREGeneratorEvo;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.vredeployment.client.VREDeploymentService;
import org.gcube.portlets.admin.vredeployment.shared.VREDefinitionBean;
import org.gcube.vremanagement.vremodel.cl.proxy.Manager;
import org.gcube.vremanagement.vremodel.cl.stubs.types.FunctionalityItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.Report;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ResourceDescriptionItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.ResourceItem;
import org.gcube.vremanagement.vremodel.cl.stubs.types.VREDescription;
import org.gcube.vremanagement.vremodeler.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class VREManagerServiceImpl extends RemoteServiceServlet implements VREDeploymentService {

	private static final Logger _log = LoggerFactory.getLogger(VREManagerServiceImpl.class);

	private static final String APPROVE_MODE = "approve";
	private static final String EDIT_MODE = "edit";
	private static final String REEDIT_TYPE_ATTRIBUTE = "reeditType";
	private static final String VRE_GENERATOR_ATTRIBUTE = "VREGenerator";
	private static final String VIEW_MODE_ATTRIBUTE = "viewMode";

	private static final String HARD_CODED_VO_NAME = "/gcube/devsec";


	@Override
	public ArrayList<VREDefinitionBean> getVREDefinitions() {
		ASLSession aslSession = getASLSession();
		ArrayList<VREDefinitionBean> toReturn = new ArrayList<VREDefinitionBean>();
		System.out.println("getAllVREs");
		VREGeneratorEvo evo = new VREGeneratorEvo(aslSession);
		List<Report> vres = evo.getAllVREs(aslSession);
		
		if (vres == null || vres.isEmpty()) {
			return  toReturn;
		}
		else {
			for (Report vre: vres) {
				if (vre.name().equals("notCompletedVirtualResearchEnv.")) {
					_log.debug("removing fake vre" +vre.name());
					doRemove(vre.id());
				}
				else {
					VREDescription vreDetail = null;
					try {
						Manager modelPortType = manager().at(factory().build().getEPRbyId(vre.id())).build();
						vreDetail = modelPortType.getDescription();
					} catch (Exception e) {
						e.printStackTrace();
					}
					Date start = null;
					Date end = null;
					if (vreDetail != null) {
						start = vreDetail.startTime().getTime();
						end = vreDetail.endTime().getTime();
					}
					toReturn.add(new VREDefinitionBean(vre.id(), vre.name(), vre.description(), "", "", start, end, vre.state(), ""));
					_log.debug(vre.name() + " id=" + vre.id());
				}
			}
		}
		return toReturn;
	}

	/**
	 * 
	 * @return
	 */
	private ASLSession getASLSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		String username = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);

		if (username == null) {
			username = "massimiliano.assante";
			SessionManager.getInstance().getASLSession(session.getId(), username).setScope(HARD_CODED_VO_NAME);
			//SessionManager.getInstance().getASLSession(session.getId(), username).setAttribute(REEDIT_TYPE_ATTRIBUTE, APPROVE_MODE);
		}

		return SessionManager.getInstance().getASLSession(session.getId(), username);
	}

	/**
	 * approve the vre and set in session necessar vars for deployer
	 */
	public boolean doApprove(String epr) {
		System.out.println("---   approve    ---");
		epr = unCodeERP(epr);
		_log.debug("VRE Instance to approve at epr: " + epr);

		ASLSession d4ScienceSession = getASLSession();
		_log.debug("doApprove id: " + epr + " Scope: " + d4ScienceSession.getScopeName());

		d4ScienceSession.setAttribute(VRE_GENERATOR_ATTRIBUTE, epr);
	
		d4ScienceSession.setAttribute(REEDIT_TYPE_ATTRIBUTE, APPROVE_MODE);
		_log.debug("REEDIT_TYPE_ATTRIBUTE SET: " + APPROVE_MODE);
		setDeployingStatusOff();

		d4ScienceSession.setAttribute(VIEW_MODE_ATTRIBUTE, new Boolean(false));
		return true;
	}

	/**
	 * 
	 */
	private static final String DEPLOYING = "DEPLOYING";
	private void setDeployingStatusOff() {
		_log.debug("---  setDeployingStatusOff   ---");

		getASLSession().setAttribute(DEPLOYING, null);
	}


	private String unCodeERP(String epr){
		epr = epr.replace("&gt;", ">");
		epr = epr.replace("&lt;", "<");
		epr = epr.replace("&quot;", "\"");
		return epr;
	}

	@Override
	public boolean doRemove(String epr) {
		ASLSession aslSession = getASLSession();
		VREGeneratorEvo evo = new VREGeneratorEvo(aslSession);
		evo.removeVRE(aslSession, epr);
		return true;
	}
	
	@Override
	public boolean doUndeploy(String epr) {
		ASLSession aslSession = getASLSession();
		VREGeneratorEvo evo = new VREGeneratorEvo(aslSession);
		evo.undeployVRE(epr);
		return true;
	}

	@Override
	public boolean doEdit(String epr) {
		epr = unCodeERP(epr);
		_log.debug("VRE Instance to edit at epr: " + epr);
		ASLSession session = getASLSession();
		session.setAttribute(VRE_GENERATOR_ATTRIBUTE, epr);
		session.setAttribute(REEDIT_TYPE_ATTRIBUTE, EDIT_MODE);
		session.setAttribute(VIEW_MODE_ATTRIBUTE, new Boolean(false));
		return true;
	}

	@Override
	public String doViewDetails(String epr) {
		String toReturn = "";
		epr = unCodeERP(epr);
		_log.debug("VRE Instance to edit at epr: " + epr);
		try {
			ASLSession session = getASLSession();
			VREGeneratorEvo vreGenerator = new VREGeneratorEvo(session,epr);
			toReturn = getHTMLDescription(vreGenerator);
		} catch (RemoteException e) {
			e.printStackTrace();
			return "Could not retrieve VRE info, cause: " + e.getMessage();
		}
		return toReturn;
	}

	@Override
	public boolean doViewReport(String epr) {
		_log.debug("---   view REPORT    ---");
		_log.debug("VRE Instance to view at encoded epr: " + epr);
		ASLSession session = getASLSession();
		session.setAttribute(VRE_GENERATOR_ATTRIBUTE, epr);
		getASLSession().setAttribute(DEPLOYING, "ON");
		return true;
	}

	/**
	 * Antonio Method for displaying info
	 * @param evo
	 * @return
	 * @throws RemoteException 
	 */
	private String getHTMLDescription(VREGeneratorEvo evo) throws RemoteException {
		String toReturn = "";

		VREDescription vre = evo.getVREModel();
		//STYLE=\"font-size:18px;\"
		String vreDescription = "<div STYLE=\"font-size:18px;\" align=\"center\" ><h1> Summary </h1></div>";
		vreDescription+="<div><b>Name: </b>" + vre.name() +"</div>";
		vreDescription+="<div><b>VRE Designer: </b>" + vre.designer() +"</div>";
		vreDescription+="<div><b>VRE Manager: </b>" + vre.manager() +"</div>";
		SimpleDateFormat fmt = new SimpleDateFormat("EEE, MMM d, ''yy");   
		vreDescription+="<div ><b>From: </b>" 
				+ fmt.format(vre.startTime().getTime())
				+ " <b> To: </b>"
				+ fmt.format(vre.endTime().getTime())
				+ "</div>";

		vreDescription+= "<br />";
		vreDescription+="<div><b>Description: </b>" + vre.description() +"</div>";
		vreDescription += "<br />";


		vreDescription += "<div style=\"font-size:14px;\" align=\"center\" >Functionalities</div>";
		vreDescription += "<table>";

		try {
			List<FunctionalityItem> list = evo.getFunctionality();
			if (list == null) {
				_log.warn("FunctionalityList NULL");
			} else {
				for (FunctionalityItem fi :list){
					vreDescription += "<tr><td><span style=\"font-weight: bold; color: #333; font-size: 12px;\">" + fi.name() + "</span></td></tr>";
					if (fi.children() != null && !fi.children().isEmpty()) {
						List<FunctionalityItem> children = fi.children();
						for (int i = 0; i < children.size(); i++) {
							if (children.get(i).selected()) {
								vreDescription += "<tr><td><span class=\"funcadd\">" + children.get(i).name() + "</span></td></tr>";
								if ( children.get(i).selectableResourcesDescription()!=null) 
									for (ResourceDescriptionItem category:  children.get(i).selectableResourcesDescription()) 
										if (category.resources()!=null) 
											for (ResourceItem resource : category.resources()) 
												if (resource.selected()) 
													vreDescription += "<tr><td><span class=\"extres\" >" + resource.name() + "</span></td></tr>";											
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		vreDescription += "</table>";
		toReturn += vreDescription;
		return toReturn;
	}
	
	/**
	 * 
	 * @return the html representation of the report
	 */
	@Override
	public String getHTMLReport(String epr) {
		epr = unCodeERP(epr);
		_log.info("--- getHTMLReport VRE  ---");

		ASLSession session = getASLSession();
		VREGeneratorEvo vreGenerator = new VREGeneratorEvo(session,epr);

		String  report = null;

		try {
			report = Utils.toXML(vreGenerator.checkVREStatus());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		String startDirectory = this.getServletContext().getRealPath("");
		String xslFileLocation = startDirectory + "/styles/report.xsl";

		String transformed = "";
		transformed = transformToHtml(report, xslFileLocation);
	

		return transformed;	
	}

	/**
	 * 
	 * @param profile
	 * @param xslFile
	 * @return
	 */
	private String transformToHtml(String profile, String xslFile){


		File stylesheet  =  new File(xslFile);

		TransformerFactory tFactory = TransformerFactory.newInstance();

		StreamSource stylesource = new StreamSource(stylesheet);

		Transformer transformer = null;
		try {
			transformer = tFactory.newTransformer(stylesource);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return "";
		}


		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return "";
		}


		StringReader reader = new StringReader(profile);
		InputSource inputSource = new InputSource(reader);

		_log.debug("***** --- Reading **** ");
		try {
			document = builder.parse(inputSource);
		} catch (SAXException e) {
			_log.error("***** --- ERROR PARSING REPORT SAXException--- **** ");
			_log.error("CHECK THIS: \n" + profile);

			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}


		DOMSource source = new DOMSource(document);

		ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(resultStream);

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			return "";
		}

		return resultStream.toString();
	}

	@Override
	public boolean postPone(String vreId) {
		_log.debug("VRE Instance to postpone id: " + vreId);
		try {
			ASLSession session = getASLSession();
			VREGeneratorEvo evo = new VREGeneratorEvo(session, vreId);
			VREDescription vre = evo.getVREModel();
			Calendar endTime = vre.endTime();
			endTime.set(Calendar.MONTH, (endTime.get(Calendar.MONTH)+6));
			vre.endTime(endTime);
			evo.setVREModel(vre.name(), vre.description(), vre.designer(), vre.manager(), vre.startTime().getTimeInMillis(), endTime.getTimeInMillis());
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}
	}

	
}
