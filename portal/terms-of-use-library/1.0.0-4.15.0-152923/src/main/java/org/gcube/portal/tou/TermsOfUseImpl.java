package org.gcube.portal.tou;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.portal.tou.exceptions.ToUNotFoundException;
import org.gcube.portal.tou.model.ToU;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleServiceUtil;

/**
 * Implementation of the TermsOfUse Interface.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class TermsOfUseImpl implements TermsOfUse {

	private static final Logger logger = LoggerFactory.getLogger(TermsOfUseImpl.class);
	private static final String TOU_KEY_GROUP_CUSTOM_FIELD = "ToU"; // a long value
	private static final String TOU_KEY_USER_CUSTOM_FIELD = "ToUser"; // a group of text values with accepted tous of kind "context||touId"
	private static final String SCOPE_TOU_ID_SEPARATOR = "|";
	private static final String TITLE_XML_FIELD = "Title";
	private static final String CONTENT_XML_FIELD = "static-content";
	private static final String DEFAULT_LONG_VALUE = "0";

	public boolean hasAcceptedToU(String userid, long groupId) throws NumberFormatException, UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault {
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		return acceptedToUs(um, um.getUserByUsername(userid)).containsKey(gm.getInfrastructureScope(groupId));
	}

	public Long hasAcceptedToUVersion(String userid,  long groupId) throws NumberFormatException, UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault {
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		return acceptedToUs(um, um.getUserByUsername(userid)).get(gm.getInfrastructureScope(groupId));
	}

	public void setAcceptedToU(String userid, long groupId) throws ToUNotFoundException, PortalException, SystemException, UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault{
		UserManager um = new LiferayUserManager();
		GroupManager gm = new LiferayGroupManager();
		setTouAsRead(um, um.getUserByUsername(userid), gm.getInfrastructureScope(groupId), String.valueOf(getToUGroup(groupId).getId()));
	}

	public ToU getToUGroup(long groupId) throws ToUNotFoundException, GroupRetrievalFault, UserManagementSystemException, PortalException, SystemException{

		logger.info("Retrieving ToU for group " + groupId);
		GroupManager gm = new LiferayGroupManager();
		Serializable customAttributeLongValue = gm.readCustomAttr(groupId, TOU_KEY_GROUP_CUSTOM_FIELD);
		if(customAttributeLongValue == null || String.valueOf(customAttributeLongValue).equals(DEFAULT_LONG_VALUE))
			throw new ToUNotFoundException();

		logger.info("Group Custom Attribute looks like " + customAttributeLongValue);
		JournalArticle article = JournalArticleServiceUtil.getArticle(groupId, String.valueOf(customAttributeLongValue));

		logger.debug("Content is " + article.getContent() != null? article.getContent().substring(0, 20) : "");
		logger.debug("Title is " + article.getTitle());
		logger.debug("id is " + customAttributeLongValue);
		logger.debug("Version is " + article.getVersion());

		ToU tou = new ToU(escapedXmlContent(article.getTitle(), TITLE_XML_FIELD), escapedXmlContent(article.getContent(), CONTENT_XML_FIELD), Long.valueOf(String.valueOf(customAttributeLongValue)), article.getVersion());
		logger.debug("ToU is " + tou);
		return tou;
	}

	/**
	 * Parse the web document from liferay
	 * @param xml
	 * @param elemText
	 * @return
	 */
	private static String escapedXmlContent(String xml, String elemText){
		DocumentBuilder builder;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
			NodeList nList = doc.getElementsByTagName(elemText);
			return nList.item(0).getTextContent();
		} catch (Exception e){
			logger.error("Error while retrieving ToU", e);
		}
		return null;
	}

	/**
	 * Retrieve the accepted ToUs
	 * @param um
	 * @param gCubeUser
	 * @return
	 * @throws NumberFormatException
	 * @throws UserRetrievalFault
	 */
	private static Map<String, Long> acceptedToUs(UserManager um, GCubeUser gCubeUser) throws NumberFormatException, UserRetrievalFault{
		Map<String, Long> toReturn = new HashMap<String, Long>();
		if (um.readCustomAttr(gCubeUser.getUserId(), TOU_KEY_USER_CUSTOM_FIELD) != null && um.readCustomAttr(gCubeUser.getUserId(), TOU_KEY_USER_CUSTOM_FIELD).toString().compareTo("") != 0) {
			String[] values = (String[]) um.readCustomAttr(gCubeUser.getUserId(), TOU_KEY_USER_CUSTOM_FIELD);
			if (values != null && values.length > 0) {
				for (int i = 0; i < values.length; i++) {
					String[] splits = values[i].split("\\"+SCOPE_TOU_ID_SEPARATOR);
					toReturn.put(splits[0], Long.valueOf(splits[1]));
				}					
			}
		}
		logger.debug("List of accepted ToUs for user " + gCubeUser.getUsername() +  " is " + toReturn);
		return toReturn;
	}

	/**
	 * Set the read ToUs for a user
	 * @param um
	 * @param gCubeUser
	 * @param context
	 * @param touIdaccepted
	 * @throws UserRetrievalFault
	 */
	private static void setTouAsRead(UserManager um, GCubeUser gCubeUser, String context, String touIdaccepted) throws UserRetrievalFault {
		Map<String, Long> theInstances = acceptedToUs(um, gCubeUser);
		theInstances.put(context, Long.valueOf(touIdaccepted));
		String[] theValues = new String[theInstances.size()];
		int i = 0;
		for (String touPerScope : theInstances.keySet()) {
			String toPut = touPerScope+SCOPE_TOU_ID_SEPARATOR+theInstances.get(touPerScope); //-> /gcube/devNext/NextNext|touIdaccepted
			theValues[i] = toPut;
			i++;
		}
		//overwrite the values
		logger.debug("List of accepted ToUs for user " + gCubeUser.getUsername() +  " is " + theValues + ". Going to set them");
		um.saveCustomAttr(gCubeUser.getUserId(), TOU_KEY_USER_CUSTOM_FIELD, theValues);
	}

}
