package org.gcube.data.analysis.tabulardata.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.InternalSecurityException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.HistoryData;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.RuleDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TemplateDescription;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications.Notification;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.resources.ResourceDescriptor;
import org.gcube.data.analysis.tabulardata.exceptions.NoSuchObjectException;
import org.gcube.data.analysis.tabulardata.metadata.Identifiable;
import org.gcube.data.analysis.tabulardata.metadata.StorableHistoryStep;
import org.gcube.data.analysis.tabulardata.metadata.StorableRule;
import org.gcube.data.analysis.tabulardata.metadata.StorableTemplate;
import org.gcube.data.analysis.tabulardata.metadata.notification.StorableNotification;
import org.gcube.data.analysis.tabulardata.metadata.resources.StorableResource;
import org.gcube.data.analysis.tabulardata.metadata.tabularresource.StorableTabularResource;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.OperationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;

public class Util {

	public static List<HistoryData> toHistoryDataList(List<StorableHistoryStep> steps){
		List<HistoryData> historyDataList = new ArrayList<HistoryData>();
		for(StorableHistoryStep step : steps)
			historyDataList.add(toHistoryData(step));
		return historyDataList;
	}
	
	public static HistoryData toHistoryData(StorableHistoryStep step){
		return new HistoryData(step.getId(),step.getOperationDescription(), step.getTableId()==null?null:new TableId(step.getTableId()), step.getDate());
	}
	
	public static OperationExecution toOperationExecution(OperationInvocation invocation){
		OperationExecution opExec = new OperationExecution(invocation.getOperationDescriptor().getOperationId().getValue(), invocation.getParameterInstances());
		if (invocation.getTargetColumnId()!=null)
			opExec.setColumnId(invocation.getTargetColumnId().getValue());
		return opExec;
	}
	
	public static OperationDefinition toOperationDefinition(OperationDescriptor descriptor){
		return new OperationDefinition(descriptor.getOperationId().getValue(), descriptor.getName(), descriptor.getDescription(), descriptor.getParameters() );
	}
	
	public static TabularResource toTabularResource(StorableTabularResource sTr){
		TabularResource tabularResource = new TabularResource(sTr.getId(), sTr.getTabularResourceType(), sTr.getName(), sTr.getOwner(), sTr.getCreationDate(), sTr.getTableType(), sTr.getSharedWith(), sTr.getProperties(), toHistoryDataList(sTr.getHistorySteps()), sTr.isValid(), sTr.isLocked()); 
		tabularResource.finalize(sTr.isFinalized());
		return tabularResource;
	}

	public static ResourceDescriptor toResourceDescriptor(StorableResource sr){
		ResourceDescriptor resource = new ResourceDescriptor(sr.getId(), sr.getName(), sr.getDescription(), sr.getCreationDate(), sr.getCreatorId(), sr.getResource(), sr.getType()); 
		return resource;
	}
	
	public static List<Notification> toNotificationList(
			List<StorableNotification> resultList) {
		if (resultList.isEmpty()) return Collections.emptyList();
		List<Notification> notifications = new ArrayList<Notification>();
		for (StorableNotification notification : resultList)
			notifications.add(toNotification(notification));
		return notifications;
	}
	
	public static Notification toNotification(StorableNotification result){
		return new Notification(result.getAffectedObject(), result.getUpdateEvent(),
				result.getNotificationObject().getHumanReadableDescription(),result.getDate());
	}
	
	public static TemplateDescription toTemplateDescription(StorableTemplate sTemplate){
		return new TemplateDescription(sTemplate.getId(), sTemplate.getOwner(), sTemplate.getName(), sTemplate.getDescription(), sTemplate.getAgency(), sTemplate.getCreationDate(),  sTemplate.getTemplate(), sTemplate.getSharedWith()); 
	}
	
	public static RuleDescription toRuleDescription(StorableRule sRule){
		return new RuleDescription(sRule.getId(), sRule.getName(), sRule.getDescription(), sRule.getCreationDate(), sRule.getRule().getExpressionWithPlaceholder(), sRule.getRuleScope(), sRule.getOwner(), sRule.getRuleType(), sRule.getSharedWith());
	}
	
	public static List<ResourceDescriptor> toResourceDescriptorList(List<StorableResource> resources){
		if (resources.isEmpty()) return Collections.emptyList();
		List<ResourceDescriptor> descriptors = new ArrayList<>();
		for (StorableResource sr: resources)
			descriptors.add(toResourceDescriptor(sr));
		return descriptors;
	}
	
	public static <T, R extends Identifiable> R getOwnerhipAuthorizedObject(T id, Class<R> objectClass, EntityManager entityManager) throws NoSuchObjectException, InternalSecurityException{
		
		String caller = AuthorizationProvider.instance.get().getClient().getId();
		R sTr = entityManager.find(objectClass, id);
		if (sTr==null || !sTr.getScopes().contains(ScopeProvider.instance.get())) throw new NoSuchObjectException();
		if (!sTr.getOwner().equals(caller)) 
			throw new InternalSecurityException(caller+" is not owner of "+objectClass.getName()+" with id "+id.toString());
		return sTr;
	}

	public static <T, R extends Identifiable> R getUserAuthorizedObject(
			T id, Class<R> objectClass, EntityManager entityManager) throws NoSuchObjectException, InternalSecurityException{
		String caller = AuthorizationProvider.instance.get().getClient().getId();
		R sTr = entityManager.find(objectClass, id);
		if (sTr==null || !sTr.getScopes().contains(ScopeProvider.instance.get())) throw new NoSuchObjectException();
		if (!sTr.getOwner().equals(caller) && !sTr.getSharedWith().contains(String.format("u(%s)", caller)) &&
				!sTr.getSharedWith().contains(String.format("g(%s)", caller)))
			throw new InternalSecurityException(caller+" is not authorized to use "+objectClass.getName()+" with id "+id.toString());
		return sTr;
	}
		
	
}
