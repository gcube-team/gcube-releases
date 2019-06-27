package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.server.thread;

import static org.gcube.resources.discovery.icclient.ICFactory.client;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.portlets.widgets.ckandatapublisherwidget.server.threads.WritePostCatalogueManagerThread;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.Query;
import org.gcube.resources.discovery.client.queries.impl.QueryBox;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;


/**
 * This thread is scheduled to update the resource related to org.gcube.datacatalogue.ProductCatalogue
 * {org.gcube.portlets.widgets.ckandatapublisherwidget.server.threads}
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class UpdateItemCatalogueResource extends Thread{

	private String currentScope;
	private String cleanUrl;
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UpdateItemCatalogueResource.class);


	/**
	 * @param currentScope
	 * @param cleanUrl
	 */
	public UpdateItemCatalogueResource(String currentScope, String cleanUrl) {
		super();
		this.currentScope = currentScope;
		this.cleanUrl = cleanUrl;
	}

	@Override
	public void run() {

		try{

			if(cleanUrl == null || cleanUrl.isEmpty() || currentScope==null || currentScope.isEmpty()){
				logger.warn("One or more arguments {}{} is wrong. Exiting", currentScope, cleanUrl);
				return;
			}

			ScopeBean scope =  new ScopeBean(currentScope);
			if(!scope.is(Type.VRE)){
				logger.warn("{} is not a VRE scope", currentScope);
				return;
			}

			// set the scope of the root infrastructure
			String rootInfrastructure = getRootScope();
			ScopeProvider.instance.set("/"+rootInfrastructure);

			// check if the resource is present
			Query q = new QueryBox("for $profile in collection('/db/Profiles/GenericResource')//Resource " +
					"where $profile/Profile/SecondaryType/string() eq 'ApplicationProfile' and  $profile/Profile/Body/AppId/string() " +
					" eq '" + WritePostCatalogueManagerThread.APPLICATION_ID_CATALOGUE_MANAGER + "'" +
					"return $profile");

			DiscoveryClient<String> client = client();
			List<String> appProfile = client.submit(q);

			if (appProfile == null || appProfile.size() == 0) 
				throw new Exception("this applicationProfile is not registered in the infrastructure");
			else{

				String elem = appProfile.get(0);
				DocumentBuilder docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Node node = docBuilder.parse(new InputSource(new StringReader(elem))).getDocumentElement();
				XPathHelper helper = new XPathHelper(node);

				// look for the scope
				List<String> currValue = null;
				currValue = helper.evaluate(String.format("/Resource/Profile/Body/EndPoint/Scope/text()[.='%s']", scope));
				logger.debug("Result is " + currValue);

				if (currValue == null || currValue.isEmpty()) {
					logger.info("Adding the following url " + cleanUrl);

					String endpoint2Add = "<EndPoint><Scope>"+currentScope+"</Scope><URL>"+cleanUrl+"</URL></EndPoint>";
					GenericResource toUpdate = clientFor(GenericResource.class).submit(q).get(0);
					try {
						docBuilder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Element body = toUpdate.profile().body();
						Node fragmentNode = docBuilder.parse(new InputSource(new StringReader(endpoint2Add))).getDocumentElement();
						fragmentNode = body.getOwnerDocument().importNode(fragmentNode, true);
						body.appendChild(fragmentNode);
					} catch (Exception e) {
						logger.error("Error while updating the generic resource", e);
					}
					RegistryPublisher rp = RegistryPublisherFactory.create();
					rp.update(toUpdate);
					logger.info("Resource updated!");
				}
			}
		}catch(Exception e){
			logger.error("Failed to execute this check", e);
		}finally{
			ScopeProvider.instance.reset();
		}

	}

	private String getRootScope() throws Exception{

		if(currentScope == null || currentScope.isEmpty())
			throw new Exception("Scope was not specified");

		return currentScope.split("/")[1];

	}
}
