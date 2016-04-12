/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.server.news;

import static org.gcube.resources.discovery.icclient.ICFactory.client;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.applicationsupportlayer.social.ApplicationNewsManager;
import org.gcube.applicationsupportlayer.social.NewsManager;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resources;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.proxies.StatisticalManagerFactory;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMAlgorithm;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMGroupedAlgorithms;
import org.gcube.data.analysis.statisticalmanager.stubs.types.SMListGroupedAlgorithms;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.informationsystem.publisher.ScopedPublisher;
import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.bean.OperatorCategory;
import org.gcube.portlets.user.statisticalmanager.server.DescriptionRepository;
import org.gcube.portlets.user.statisticalmanager.server.util.SessionUtil;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class OperatorsNewsProducer {

	private static final String GR_SECONDARY_TYPE = "StatisticalManager";
	private static final String GR_NAME = "Operators";

	protected Logger logger = Logger.getLogger(OperatorsNewsProducer.class);
	protected String scope;

	/**
	 * @param scope
	 */
	public OperatorsNewsProducer(String scope) {
		this.scope = scope;
	}

	public void checkOperatorsForFeed() throws Exception
	{
		logger.trace("checkOperatorsForFeed scope: "+scope);
		
		Map<String, Operator> currentOperators = getCurrentOperators();
		List<String> previousNotifiedOperators =  getGenericResource();		
		List<Operator> newOperators = calculateNewOperators(previousNotifiedOperators, currentOperators);

		logger.trace("curentOperators.size "+currentOperators.size()+" previousNotifiedOperators: "+previousNotifiedOperators.size()+" newOperators: "+newOperators.size());
		
		boolean feedNews = newOperators.size()!=0;
		if (feedNews) {

			//TODO NEWS!
			logger.trace("Operators:");
			for (Operator operator:newOperators) logger.trace(operator.getName());

			ASLSession fakeSession = SessionManager.getInstance().getASLSession("1", "fake.session");
			fakeSession.setScope(scope.toString());

			NewsManager anm = new ApplicationNewsManager(fakeSession, Constants.APPLICATION_ID);

			for (Operator operator:newOperators) {
				String news = getNewsText(operator);
				anm.shareApplicationUpdate(news);
			}

			saveNotifiedOperators(currentOperators);
		}
	}

	protected String getNewsText(Operator operator)
	{
		StringBuilder text = new StringBuilder("The operator ");
		text.append(operator.getName());
		text.append(" has been added: ");
		text.append(operator.getDescription());
		return text.toString();
	}

	protected List<Operator> calculateNewOperators(List<String> previousNotifiedOperators, Map<String, Operator> currentOperators)
	{
		List<Operator> newOperators = new ArrayList<Operator>();

		for (Operator op: currentOperators.values()) if (!previousNotifiedOperators.contains(op.getName())) newOperators.add(op);

		return newOperators;
	}

	

	protected List<String>  getGenericResource() throws Exception
	{

		String queryString = "for $resource in collection('/db/Profiles/GenericResource')"
				+ "//Resource where ($resource/Profile/SecondaryType eq '"
				+ GR_SECONDARY_TYPE
				+ "')"
				+ " and ($resource/Scopes/Scope eq '"
				+ ScopeProvider.instance.get()
				+ "') " 
				+ " and ( $resource/Profile/Name eq '"
				+ GR_NAME
				+ "')" +		
				" return $resource/Profile/Body/operators/text()";
		logger.debug(queryString);
		Query q = new QueryBox(queryString);

		DiscoveryClient<String> client = client();
		List<String> operators = new ArrayList<String>();

		operators.addAll(client.submit(q));

		List<String> result= new ArrayList<String>();
		for(String s : operators)
		{
			result.addAll(extractOperatorsFromXml(s));
		}
		return result;
	}

	/**
	 * @param xml
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	protected List<String> extractOperatorsFromXml(String xml) throws SAXException, IOException, ParserConfigurationException {
		List<String> operators = new ArrayList<String>();

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);
		NodeList nodes = doc.getElementsByTagName("operator");

		// iterate the employees
		for (int i = 0; i < nodes.getLength(); i++) {
			Element operator = (Element) nodes.item(i);
			String operatorId = getCharacterDataFromElement(operator);
			operators.add(operatorId);
		}

		return operators;
	}


	protected String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

	public Map<String, Operator> getCurrentOperators() {
		Map<String, Operator> operators = new HashMap<String, Operator>();

		try{
System.out.println("getCurrentOperators");
		StatisticalManagerFactory factory = SessionUtil.getFactory(scope.toString());
		System.out.println("fac");

		String classificationName = Constants.userClassificationName;


		// get and print algorithms
		SMListGroupedAlgorithms groups = (classificationName.equals(Constants.userClassificationName) ? factory.getAlgorithmsUser() : factory.getAlgorithms());
		System.out.println("trup");

		// get list categories
		for(SMGroupedAlgorithms group: groups.thelist()) {
			OperatorCategory category = new OperatorCategory(group.category(), "", "");
			OperatorCategory catSearch = DescriptionRepository.getOperatorCategory(category);
			if (catSearch!=null) category = catSearch.clone();

			for(SMAlgorithm algorithm : group.thelist()) {
				String operatorId = algorithm.name();
				Operator operator = new Operator(operatorId, "", "", category);
				Operator opSearch = DescriptionRepository.getOperator(operator);
				if (opSearch!=null) {
					operator = opSearch.clone();
					operator.setCategory(category);
				}
				String algDescr = algorithm.description();
				if (algDescr!=null) {
					operator.setDescription(algDescr);
					operator.setBriefDescription(algDescr);
				}
				category.addOperator(operator);


				operators.put(operator.getName(), operator);
			}
		}
		System.out.println("operator found are :"+operators.size());
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}

		return operators;
	}
	
	
	
	public void saveNotifiedOperators(Map<String, Operator> operators) throws Exception
	{
		StringBuilder xml = new StringBuilder();
		xml.append("<Resource version=\"0.4.x\">");
		xml.append("<Type>GenericResource</Type>");
		xml.append("<Profile>");
		xml.append("<SecondaryType>");
		xml.append(GR_SECONDARY_TYPE);
		xml.append("</SecondaryType>");
		xml.append("<Name>");
		xml.append(GR_NAME);
		xml.append("</Name>");
		xml.append("<Body>");
		xml.append("<operators>");
		for (Operator operator: operators.values()) xml.append("<operator>"+operator.getName()+"</operator>");
		xml.append("</operators>");
		xml.append("</Body>");
		xml.append("</Profile>");
		xml.append("</Resource>");

		StringReader reader = new StringReader(xml.toString());

		GenericResource resource = (GenericResource) Resources.unmarshal(
				GenericResource.class, reader);
		ScopedPublisher sp = RegistryPublisherFactory
				.scopedPublisher();
		List<String>scopes= new ArrayList<String>();
		scopes.add(scope);
		sp.create(resource, scopes);

	
	}

	
	
	
	
	
	public static void main(String[] args) throws Exception
	{
		/*GHNContext ctx = GHNContext.getContext();
		String rootScope = (String) ctx.getProperty(GHNContext.INFRASTRUCTURE_NAME, true);*/
		OperatorsNewsProducer feeder = new OperatorsNewsProducer("/gcube/devNext/NextNext");
		feeder.checkOperatorsForFeed();
	}

}
