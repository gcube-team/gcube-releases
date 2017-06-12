package org.gcube.informationsystem.resource_checker.utils;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.HostingNode;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.Resource.Type;
import org.gcube.common.resources.gcore.Software;

/**
 * A basic functionality bean with name, category and type
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@SuppressWarnings("rawtypes")
public class BasicFunctionalityBean {

	private String category;
	private String name;
	private Class type; // service endpoint only for now

	/**
	 * @param category
	 * @param name
	 * @param type
	 */
	public BasicFunctionalityBean(String category, String name, String type) {
		super();
		this.category = category;
		this.name = name;

		if(type == null)
			this.type = ServiceEndpoint.class;
		else{
			Type extractedType = Type.valueOf(type);

			switch(extractedType){
			case ENDPOINT:
				this.type = ServiceEndpoint.class;
				break;
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
			default:
				this.type = ServiceEndpoint.class;
			}
		}
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
	@Override
	public String toString() {
		return "BasicFunctionalityBean [category=" + category + ", name="
				+ name + ", type=" + type + "]";
	}

}
