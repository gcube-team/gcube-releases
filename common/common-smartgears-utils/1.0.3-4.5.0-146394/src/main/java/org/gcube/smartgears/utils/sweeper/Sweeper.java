package org.gcube.smartgears.utils.sweeper;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.resources.discovery.icclient.ICFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrea Manzi(CERN)
 * 
 * Implements the sweeping on the IS in case a container state is cleaned
 * 
 */
public class Sweeper {

	String ghn_state_path = "";

	Logger logger;
	
	String ghn_path;
	
	String id ;
	List<ContextBean> contextBeans = new ArrayList<ContextBean>();

	public Sweeper () throws Exception {

		logger  = LoggerFactory.getLogger(Sweeper.class);

		ghn_path = System.getenv("GHN_HOME");

		if (ghn_path == null ) {
			logger.error("GHN_HOME not defined");	
			throw new Exception ("GHN_HOME not defined");
		}

		ghn_state_path=ghn_path+File.separator+"state";
		
		deserializeState();
	}
	
	@SuppressWarnings("unchecked")
	private void deserializeState(){
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ghn_state_path+File.separator+"ghn.xml"))){
			id = (String)ois.readObject();
			List<String> tokens = (List<String>) ois.readObject();
			for (String token : tokens){
				AuthorizationEntry entry = authorizationService().get(token);
				contextBeans.add(new ContextBean(token, entry.getContext()));
			}
		}catch(Exception e){
			throw new RuntimeException("error loading persisted state",e);
		}	
	}
	
	
	public void forceDeleteHostingNode(){
		RegistryPublisher rp=RegistryPublisherFactory.create();
		try{
			DiscoveryClient<HostingNode> client = ICFactory.clientFor(HostingNode.class);
			SimpleQuery query = ICFactory.queryFor(HostingNode.class);
			query.addCondition("$resource/ID/text() = '"+id+"'");
			for (ContextBean contextBean : contextBeans){
				
				SecurityTokenProvider.instance.set(contextBean.getToken());
				ScopeProvider.instance.set(contextBean.getContext());
				List<HostingNode> nodes = client.submit(query);
				if (nodes.isEmpty()) continue;
				rp.remove(nodes.get(0));
			}


		}catch(Exception e){
			throw new RuntimeException("error removing hosting node resource",e);
		}

	}
	
	public void saveTokens(String fileName){
		File file = new File(ghn_path+File.separator+fileName);
						
		try {
			if (file.exists())
				file.delete();
			file.createNewFile();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
		try(FileWriter fw = new FileWriter(file)){
			for (ContextBean bean: contextBeans){
				fw.write("<token>"+bean.getToken()+"</token> <!--- "+bean.getContext()+" -->\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);	
		}
		
	}
	
}
