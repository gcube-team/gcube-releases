package org.gcube.informationsystem.resource_checker.beans;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.Software;

/**
 * A basic functionality bean with name, category and type. 
 * Resource Identifier will be discovered at run time. It is supposed that the same resource
 * is published in all contexts (so the identifier is the same).
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("rawtypes")
public class BasicFunctionalityBean {

	private String category;
	private String name;
	private Class type;
	private String resourceId;
	private OperationLevel opLevel = OperationLevel.ALERT_READD;
	private ContextLevel ctxLevel = ContextLevel.ALL;

	/**
	 * @param category
	 * @param name
	 * @param type
	 */
	public BasicFunctionalityBean(String category, String name, String type, String opLevel, String ctxLevel) {
		super();
		this.category = category;
		this.name = name;

		if(type == null)
			this.type = ServiceEndpoint.class;
		else{
			Type extractedType = Type.valueOf(type);

			switch(extractedType){
			case GCOREENDPOINT:
				this.type = GCoreEndpoint.class;
				break;
			case GENERIC:
				this.type = GenericResource.class;
				break;
			case NODE:
				this.type = HostingNode.class;
				break;
			case SOFTWARE:
				this.type = Software.class;
				break;
			case ENDPOINT:
			default:
				this.type = ServiceEndpoint.class;
			}
		}
		
		if(opLevel != null)
			this.opLevel = OperationLevel.valueOf(opLevel);
		
		if(ctxLevel != null)
			this.ctxLevel = ContextLevel.valueOf(ctxLevel);
		
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class getType() {
		return type;
	}
	public void Class(Class type) {
		this.type = type;
	}
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public OperationLevel getOpLevel() {
		return opLevel;
	}
	public void setOpLevel(OperationLevel opLevel) {
		this.opLevel = opLevel;
	}
	public ContextLevel getCtxLevel() {
		return ctxLevel;
	}
	public void setCtxLevel(ContextLevel ctxLevel) {
		this.ctxLevel = ctxLevel;
	}
	@Override
	public String toString() {
		return "BasicFunctionalityBean [category=" + category + ", name="
				+ name + ", type=" + type + ", resourceId=" + resourceId
				+ ", opLevel=" + opLevel + ", ctxLevel=" + ctxLevel + "]";
	}

}
