package org.gcube.elasticsearch;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.concurrent.Callable;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeExecutor implements Callable<Client> {

	private static final Logger logger = LoggerFactory.getLogger(NodeExecutor.class);
	
	private String clusterName;
	private Settings settings;
	private Node node;
	
	
	public NodeExecutor(String clusterName, Settings settings){
		this.clusterName = clusterName;
		this.settings = settings;
	}
	
	
	@Override
    public Client call() {
    	
    	node = nodeBuilder()
				.client(false)
				.clusterName(this.clusterName)
				.settings(settings)
				.node()
				.start();
    	
    	return node.client();
    	
    }
    

}
