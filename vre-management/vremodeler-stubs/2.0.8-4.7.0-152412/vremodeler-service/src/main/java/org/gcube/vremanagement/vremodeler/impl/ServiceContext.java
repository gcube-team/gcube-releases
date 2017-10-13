package org.gcube.vremanagement.vremodeler.impl;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.informationsystem.notifier.ISNotifier.GCUBENotificationTopic;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.scope.GCUBEScope.Type;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.consumers.CollectionConsumer;
import org.gcube.vremanagement.vremodeler.consumers.GHNConsumer;
import org.gcube.vremanagement.vremodeler.consumers.GenericResourceConsumer;
import org.gcube.vremanagement.vremodeler.consumers.RunningInstanceConsumer;
import org.gcube.vremanagement.vremodeler.consumers.RuntimeResourceConsumer;
import org.gcube.vremanagement.vremodeler.db.IStoDBUtil;
import org.gcube.vremanagement.vremodeler.impl.util.ExpiredVREFinderTask;
import org.gcube.vremanagement.vremodeler.impl.util.ServicePair;
import org.gcube.vremanagement.vremodeler.resources.handlers.CollectionHandler;
import org.gcube.vremanagement.vremodeler.resources.handlers.FunctionalityHandler;
import org.gcube.vremanagement.vremodeler.resources.handlers.GHNHandler;
import org.gcube.vremanagement.vremodeler.resources.handlers.GenericResourceHandler;
import org.gcube.vremanagement.vremodeler.resources.handlers.RuntimeResourceHandler;

public class ServiceContext extends GCUBEServiceContext{

	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();

	private static List<GCUBENotificationTopic> topicToRemove= new ArrayList<ISNotifier.GCUBENotificationTopic>();

	/** Returns cached instance */
	public static ServiceContext getContext() {return cache;}

	/** Prevents accidental creation of more instances */
	private ServiceContext(){};

	/** {@inheritDoc} */
	protected String getJNDIName() {return "gcube/vremanagement/vremodeler";}

	private ArrayList<String> secondaryTypeGenericResourceRequired= new ArrayList<String>();

	private ArrayList<ServicePair> baseServiceForGhn= new ArrayList<ServicePair>();

	protected void onReady() throws Exception{
		try{
			logger.info("ready event invoked on " + this.getName());
			this.intializeDB();
			for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()){
				if (scope.getType()== Type.VO)	ExpiredVREFinderTask.get(scope.toString());
			}

		}catch (Exception e){
			logger.error("error initializing VREModeler",e);
			this.setStatus(Status.FAILED);
			throw e;
		}
	}

	protected void intializeDB() throws Exception{
		ArrayList<ServicePair> baseServiceGhn= new ArrayList<ServicePair>();
		for (String gen:((String)this.getProperty("BaseRisForSelectableGHN", true)).split(";")){
			String[] serviceString=gen.split(",");
			logger.trace("base service: "+serviceString[0]+","+ serviceString[1]);
			baseServiceGhn.add(new ServicePair(serviceString[0], serviceString[1]));
		}
		this.setBaseServiceForGhn(baseServiceGhn);
		try{   

			ISNotifier notifier= GHNContext.getImplementation(ISNotifier.class);
			for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()){
				ScopeProvider.instance.set(scope.toString());
				if (scope.getType() != Type.VO) continue;

				IStoDBUtil.initDB(scope.toString());
				new GHNHandler().initialize();
				new CollectionHandler().initialize();	

				//skipping the initialize FunctionalityHandler
				FunctionalityHandler functionalityHandler= new FunctionalityHandler();
				functionalityHandler.initialize();
				new GenericResourceHandler().initialize();
				//new ServiceHandler().initialize();
				new RuntimeResourceHandler().initialize();
				logger.debug("Service initialized!!");

				//GHNNotification	
				ArrayList<GCUBENotificationTopic> qnameList= new ArrayList<GCUBENotificationTopic>();
				qnameList.add(GHNConsumer.ghnTopic);
				notifier.registerToISNotification(new GHNConsumer(scope),qnameList,  this, scope); 
				//RINotification
				qnameList= new ArrayList<GCUBENotificationTopic>();
				qnameList.add(RunningInstanceConsumer.riTopic);
				notifier.registerToISNotification(new RunningInstanceConsumer(scope), qnameList, this, scope);
				//CollectionNotification
				qnameList= new ArrayList<GCUBENotificationTopic>();
				qnameList.add(CollectionConsumer.collectionTopic);
				notifier.registerToISNotification(new CollectionConsumer(scope), qnameList, this, scope);
				//FunctionalityResource
				qnameList= new ArrayList<GCUBENotificationTopic>();
				qnameList.add(GenericResourceConsumer.functionalityTopic);
				notifier.registerToISNotification(new GenericResourceConsumer(scope,functionalityHandler.getFunctionalityResourceId()), qnameList, this, scope);
				//ServiceResource
				/*qnameList= new ArrayList<GCUBENotificationTopic>();
			qnameList.add(ServiceConsumer.serviceTopic);
			notifier.registerToISNotification(new ServiceConsumer(scope), qnameList, this, scope);*/
				//RuntimeResources
				qnameList= new ArrayList<GCUBENotificationTopic>();
				qnameList.add(RuntimeResourceConsumer.runtimeResourceTopic);
				notifier.registerToISNotification(new RuntimeResourceConsumer(scope), qnameList, this, scope);


				logger.debug("consumers registered");
			}
		}finally{
			ScopeProvider.instance.reset();
		}

		//saving topic for removing
		topicToRemove.add(GHNConsumer.ghnTopic);
		topicToRemove.add(RunningInstanceConsumer.riTopic);
		topicToRemove.add(CollectionConsumer.collectionTopic);
		topicToRemove.add(GenericResourceConsumer.functionalityTopic);
		//topicToRemove.add(ServiceConsumer.serviceTopic);
		topicToRemove.add(RuntimeResourceConsumer.runtimeResourceTopic);
	}

	public void onShutdown() throws Exception{
		ISNotifier notifier= GHNContext.getImplementation(ISNotifier.class);
		GCUBEScope enteringScope = ServiceContext.getContext().getScope();

		for (GCUBEScope scope : ServiceContext.getContext().getInstance().getScopes().values()){
			if (scope.getType()== Type.VO)	ExpiredVREFinderTask.get(scope.toString()).cancel();

			ServiceContext.getContext().setScope(scope);
			notifier.unregisterFromISNotification(this, topicToRemove, scope);

			//if (!scope.isInfrastructure()) DBInterface.close();
		}
		ServiceContext.getContext().setScope(enteringScope);
	}

	public ArrayList<String> getSecondaryTypeGenericResourceRequired() {
		return secondaryTypeGenericResourceRequired;
	}

	public void setSecondaryTypeGenericResourceRequired(
			String[] secondaryTypeGenericResourceRequired) {
		Collections.addAll(this.secondaryTypeGenericResourceRequired,secondaryTypeGenericResourceRequired);
	}

	public ArrayList<ServicePair> getBaseServiceForGhn() {
		return baseServiceForGhn;
	}

	public void setBaseServiceForGhn(ArrayList<ServicePair> baseServiceForGhn) {
		this.baseServiceForGhn = baseServiceForGhn;
	}
}
