package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets.data;

import java.util.ArrayList;
import java.util.Collection;

import org.gcube.portlets.admin.fhn_manager_portlet.client.GUICommon;
import org.gcube.portlets.admin.fhn_manager_portlet.client.resources.ImageType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;

import com.google.gwt.user.client.ui.Image;

public class AdvancedGridConfiguration{
	
	public static final AdvancedGridConfiguration SERVICE_PROFILE=new AdvancedGridConfiguration("Service Profiles", ObjectType.SERVICE_PROFILE);
	public static final AdvancedGridConfiguration VM_PROVIDER=new AdvancedGridConfiguration("VM Providers", ObjectType.VM_PROVIDER);
	public static final AdvancedGridConfiguration VM_TEMPLATE=new AdvancedGridConfiguration("VM Templates", ObjectType.VM_TEMPLATES);
	public static final AdvancedGridConfiguration REMOTE_NODE=new AdvancedGridConfiguration("Remote Nodes", ObjectType.REMOTE_NODE);
	
	static{
		ArrayList<FieldDefinition> serviceProfileFields=new ArrayList<FieldDefinition>();
//		serviceProfileFields.add(new FieldDefinition(ServiceProfile.ID_FIELD, "ID", FieldDefinition.Type.String));
		serviceProfileFields.add(new FieldDefinition(ServiceProfile.DESCRIPTION_FIELD, "Description", FieldDefinition.Type.String));
		serviceProfileFields.add(new FieldDefinition(ServiceProfile.CREATION_FIELD, "Creation time", FieldDefinition.Type.Date));
		serviceProfileFields.add(new FieldDefinition(ServiceProfile.VERSION_FIELD, "Version", FieldDefinition.Type.String));		
		SERVICE_PROFILE.setFieldDefinitions(serviceProfileFields);
		SERVICE_PROFILE.setEnableRemove(false);		
		SERVICE_PROFILE.setEnableCreate(false);
		
		ArrayList<FieldDefinition> vmProviderFields=new ArrayList<FieldDefinition>();
//		vmProviderFields.add(new FieldDefinition(VMProvider.ID_FIELD, "ID", FieldDefinition.Type.String));
		vmProviderFields.add(new FieldDefinition(VMProvider.NAME_FIELD, "Name", FieldDefinition.Type.String));
		vmProviderFields.add(new FieldDefinition(VMProvider.URL_FIELD, "Endpoint", FieldDefinition.Type.String));		
		VM_PROVIDER.setFieldDefinitions(vmProviderFields);
		VM_PROVIDER.setEnableCreate(false);
		VM_PROVIDER.setEnableRemove(false);
		
		ArrayList<FieldDefinition> remoteNodeFields=new ArrayList<FieldDefinition>();
//		remoteNodeFields.add(new FieldDefinition(RemoteNode.ID_FIELD, "ID", FieldDefinition.Type.String));
		remoteNodeFields.add(new FieldDefinition(ServiceProfile.DESCRIPTION_FIELD, "Service", FieldDefinition.Type.String));
		remoteNodeFields.add(new FieldDefinition(ServiceProfile.VERSION_FIELD, "Version", FieldDefinition.Type.String));
		remoteNodeFields.add(new FieldDefinition(RemoteNode.HOST_FIELD, "Host", FieldDefinition.Type.String));
		remoteNodeFields.add(new FieldDefinition(VMProvider.NAME_FIELD, "Provider", FieldDefinition.Type.String));
		remoteNodeFields.add(new FieldDefinition(RemoteNode.STATUS, "Status", FieldDefinition.Type.String));
		remoteNodeFields.add(new FieldDefinition(RemoteNode.AVG_WORKLOAD, "Avg Workload", FieldDefinition.Type.Double));
		
		REMOTE_NODE.setFieldDefinitions(remoteNodeFields);
		
		ArrayList<FieldDefinition> vmTemplateFields=new ArrayList<FieldDefinition>();
//		vmTemplateFields.add(new FieldDefinition(VMTemplate.ID_FIELD, "ID", FieldDefinition.Type.String));
		vmTemplateFields.add(new FieldDefinition(VMTemplate.NAME_FIELD, "Name", FieldDefinition.Type.String));
		vmTemplateFields.add(new FieldDefinition(VMProvider.NAME_FIELD, "Provider", FieldDefinition.Type.String));
		vmTemplateFields.add(new FieldDefinition(VMTemplate.CORES_FIELD, "Cores", FieldDefinition.Type.Integer));
		vmTemplateFields.add(new FieldDefinition(VMTemplate.MEMORY_FIELD, "Memory", FieldDefinition.Type.Byte));		
		VM_TEMPLATE.setFieldDefinitions(vmTemplateFields);
		VM_TEMPLATE.setEnableCreate(false);
		VM_TEMPLATE.setEnableRemove(false);
		
	}
	
	public static AdvancedGridConfiguration byType(ObjectType type){
		switch(type){
		case REMOTE_NODE : return REMOTE_NODE;
		case SERVICE_PROFILE : return SERVICE_PROFILE;
		case VM_PROVIDER : return VM_PROVIDER;
		case VM_TEMPLATES : return VM_TEMPLATE;		
		}
		return null;
	}
	
	private String title;
	private boolean enableCreate=true;
	private boolean enableRemove=true;
	private ObjectType managedObjectType;
	private Collection<FieldDefinition> fieldDefinitions;
	/**
	 * @return the fieldDefinitions
	 */
	public Collection<FieldDefinition> getFieldDefinitions() {
		return fieldDefinitions;
	}
	/**
	 * @param fieldDefinitions the fieldDefinitions to set
	 */
	public void setFieldDefinitions(Collection<FieldDefinition> fieldDefinitions) {
		this.fieldDefinitions = fieldDefinitions;
	}
	private Image resourceIcon;
	
	public AdvancedGridConfiguration(String title, ObjectType managedObjectType) {
		super();
		this.title = title;
		this.managedObjectType = managedObjectType;
		resourceIcon=GUICommon.getResourceIcon(managedObjectType,ImageType.RESOURCE_ICON);
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the enableCreate
	 */
	public boolean isEnableCreate() {
		return enableCreate;
	}
	/**
	 * @param enableCreate the enableCreate to set
	 */
	public void setEnableCreate(boolean enableCreate) {
		this.enableCreate = enableCreate;
	}
	/**
	 * @return the enableRemove
	 */
	public boolean isEnableRemove() {
		return enableRemove;
	}
	/**
	 * @param enableRemove the enableRemove to set
	 */
	public void setEnableRemove(boolean enableRemove) {
		this.enableRemove = enableRemove;
	}
	
	
	public Image getResourceIcon() {
		return resourceIcon;
	}
	
	public void setResourceIcon(Image resourceIcon) {
		this.resourceIcon = resourceIcon;
	}
	
	public ObjectType getManagedObjectType() {
		return managedObjectType;
	}
	public void setManagedObjectType(ObjectType managedObjectType) {
		this.managedObjectType = managedObjectType;
	}
	
}