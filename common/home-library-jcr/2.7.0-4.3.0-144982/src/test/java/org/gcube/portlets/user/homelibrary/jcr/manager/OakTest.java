package org.gcube.portlets.user.homelibrary.jcr.manager;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.ValueFormatException;

import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.plugins.document.DocumentMK;
import org.apache.jackrabbit.oak.plugins.document.DocumentNodeStore;
import org.apache.jackrabbit.oak.security.SecurityProviderImpl;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.SecurityProvider;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.apache.jackrabbit.oak.spi.security.user.UserConstants;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
//import com.mongodb.DB;
//import com.mongodb.MongoClient;



public class OakTest {
//	private static final String ADMIN_USER = "admin";
//	private static Repository rep = null;  
//	private static DocumentNodeStore ns = null;
//	private static DB db;
//	private static Logger logger = LoggerFactory.getLogger(OakTest.class);
//	
//	public static void main(String[] args) throws ValueFormatException, PathNotFoundException, RepositoryException, UnknownHostException, InternalErrorException {
//
//		logger.info("Getting Mongo DB....");
//
//		try {
//			if (db==null){
//				db = new MongoClient("ws-repo-mongo-d.d4science.org", 27017).getDB("jackrabbit-next");
//				System.out.println("Get mongo db " + db.getName());
//			}
//			if (ns==null){
//				ns = new DocumentMK.Builder().setMongoDB(db).getNodeStore();
//				System.out.println("Cluster ID " + ns.getClusterId());
//			}
//			if (rep==null){
//				rep = new Jcr(new org.apache.jackrabbit.oak.Oak(ns)).with(getSecurityProvider()).createRepository();
//				System.out.println("Get OAK repository ");
//			}
//			
//			Session session = rep
//					.login(new SimpleCredentials(ADMIN_USER, ADMIN_USER.toCharArray()));	
//			System.out.println(session.getUserID());
//			
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	
//	private static SecurityProvider getSecurityProvider() {
//		Map<String, Object> userParams = new HashMap<String, Object>();
//
//		userParams.put(UserConstants.PARAM_ADMIN_ID, ADMIN_USER);
//		userParams.put(UserConstants.PARAM_OMIT_ADMIN_PW, false);
//
//		ConfigurationParameters securityParams = ConfigurationParameters
//				.of(ImmutableMap.of(UserConfiguration.NAME, ConfigurationParameters.of(userParams)));
//		SecurityProviderImpl securityProvider = new SecurityProviderImpl(securityParams);
//		return securityProvider;
//	}
}
