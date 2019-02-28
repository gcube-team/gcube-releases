package org.gcube.social_networking.socialutillibrary;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.pickitem.shared.ItemBean;
import org.gcube.socialnetworking.socialtoken.SanitizedURL;
import org.gcube.socialnetworking.socialtoken.SocialMessageParser;
import org.gcube.socialnetworking.socialtoken.URLToken;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageParserTest {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageParserTest.class);
	
	public static final String TEST_11 = "Dear all, this is a test to ignore, to select a week for the upcoming 194th #Tcom event, "
			+ "hosted by #Apple in #Cupertino, please use this #Doodle: http://Doodle.com/poll/not-existing-poll \n\n"
			+ "We're closing the poll next Thursday 16th March.";
	
	public static final String TEST_12 = "Just because I am so happy to have the SPARQL-endpoint available, \n"
			+ "sharing some sample SPARQL queries: \n\n" + "* Classes & usage counts: \n"
			+ "https://virtuoso.parthenos.d4science.org/sparql?default-graph-uri=&query=%09SELECT+%3Fp+%28COUNT%28%3Fp%29+as+%3FpCount%29++%0D%0A%09%09%09%09%09WHERE+%7B%5B%5D+%3Fp+%5B%5D%7D%0D%0A%09%09%09%09%09GROUP+BY+%3Fp&format=text%2Fhtml&timeout=0&debug=on\n"
			+ "\n" + "* properties and usage counts: \n"
			+ "https://virtuoso.parthenos.d4science.org/sparql?default-graph-uri=&query=SELECT+%3Ftype+%28COUNT%28%3Ftype%29+as+%3FtypeCount%29++%0D%0A%09%09%09%09%09WHERE+%7B%5B%5D+a+%3Ftype%7D%0D%0A%09%09%09%09%09GROUP+BY+%3Ftype&format=text%2Fhtml&timeout=0&debug=on\n";
	
	public static final String TEST_13 = "Dear members,\n"
			+ "The item 'Webinar on Ontology Management using VOCBENCH in the context of AGINFRAPLUS Project' has been just published by Leonardo Candela .\n"
			+ "You can find it here: http://data.d4science.org/ctlg/AGINFRAplus/webinar_on_ontology_management_using_vocbench_in_the_context_of_aginfraplus_project \n"
			+ "#AGINFRAPLUS #VOCBENCH";
	
	public static final String TEST_LUCA_1 = "Dear members,\n"
			+ "The item 'just a test with time fields' has been just published by Francesco Mangiacrapa.\n"
			+ "You can find it here: http://data-d.d4science.org/ctlg/NextNext/just_a_test_with_time_fields\n"
			+ "#Text_mining #Field_1 #B3";
	
	public static final String TEST_LUCA_2 = "Francesco Mangiacrapa prova &lt;a href=\"#\"&gt;Francesco Mangiacrapa&lt;/a&gt;";
	
	public static final String TEST_LUCA_3 = "test &nbsp";
	
	public static final String TEST_LUCA_4 = "test &&lt;nbsp &lt;";
	
	
	public static final String TEST_LUCA_5 = "Accedete a questo link che vi porta ad un post su linkedin.  \n" + 
			"https://www.linkedin.com/feed/update/urn:li:activity:6488779074213801984/\n" + 
			"I numeri riportati sono veri ed è motivo di orgoglio per tutti noi aver contribuito alla realizzazione della d4s infra che ha questo utilizzo via i diversi gateway. ";
	
	public static final String TEST_LUCA_6 = "(www.google.it)";
	public static final String TEST_LUCA_7 = "Hello (https://doodle.com/poll/not-existing-poll)";
	
	@Test
	public void test() {
		String message = "Prova #Pippo Pollo http://google) <a href=\"/test\">Luca</a>   https://www.linkedin.com/feed/update/urn:li:activity:6488779074213801984 :)  ";
		
		SocialMessageParser messageParser = new SocialMessageParser(message);
		logger.debug(messageParser.getParsedMessage());
		
		messageParser = new SocialMessageParser(TEST_11);
		logger.debug(messageParser.getParsedMessage());
		
		messageParser = new SocialMessageParser(TEST_12);
		logger.debug(messageParser.getParsedMessage());
		
		messageParser = new SocialMessageParser(TEST_13);
		logger.debug(messageParser.getParsedMessage());
		
		List<ItemBean> mentionedUsers = new ArrayList<>();
		ItemBean itemBean = new ItemBean("21150", "francesco.mangiacrapa", "Francesco Mangiacrapa", "");
		itemBean.setItemGroup(false);
		mentionedUsers.add(itemBean);
		messageParser = new SocialMessageParser(TEST_LUCA_1);
		logger.debug(messageParser.getParsedMessage(mentionedUsers, "/group/nextnext"));
		
		messageParser = new SocialMessageParser(TEST_LUCA_2);
		logger.debug(messageParser.getParsedMessage(mentionedUsers, "/group/nextnext"));
	}
	
	@Test
	public void anotherTest() {
		SocialMessageParser messageParser = new SocialMessageParser(TEST_LUCA_4);
		logger.debug(messageParser.getParsedMessage());
		messageParser = new SocialMessageParser(TEST_LUCA_5);
		logger.debug(messageParser.getParsedMessage());
		
	}
	
	@Test
	public void urlWithParentesisTest() {
		SocialMessageParser messageParser = new SocialMessageParser(TEST_LUCA_6);
		logger.debug(messageParser.getParsedMessage());
		messageParser = new SocialMessageParser(TEST_LUCA_7);
		logger.debug(messageParser.getParsedMessage());
		
	}
	
	@Test(expected=MalformedURLException.class)
	public void auxTest() throws MalformedURLException {
		new SanitizedURL(null);		
	}
	
	protected String findFirstLink(String message) {
		try {
			SocialMessageParser messageParser = new SocialMessageParser(message);
			List<URLToken> urlTokens = messageParser.getURLTokens();
			return urlTokens.get(0).getExtractedURL().toString();
		}catch (Exception e) {
			return null;
		}
	}
	
	@Test
	public void testTest() {
		 String text = "Hello (https://doodle.com/poll/not-existing-poll)";
		 logger.debug(findFirstLink(text));
		 
		 text = "post \"a text with #hashtag);\"";
		 SocialMessageParser messageParser = new SocialMessageParser(text);
		 logger.debug(messageParser.getParsedMessage());
	}
}
