package org.gcube.application.reporting.component;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.reporting.Property;
import org.gcube.application.reporting.component.interfaces.ReportComponent;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;

public abstract class AbstractComponent implements ReportComponent {
	private String id;
	private List<Property> properties;
	
	public AbstractComponent() {
		super();
		this.id = "-1";
		this.properties = new ArrayList<Property>();
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public List<Property> getProperties() {
		return properties;
	}
	@Override
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	/**
	 * Added for convenience's sake
	 * 
	 * @param properties a vararg set of properties
	 * @author Fabio Fiorellato
	 */
	public void setProperties(Property... properties) {
		this.properties = new ArrayList<Property>();
		
		if(properties != null)
			for(Property property : properties) 
				if(property != null)
					this.properties.add(property);
	}
	
	public void addProperty(Property toAdd) {
		if (this.properties == null)
			this.properties = new ArrayList<Property>();
		this.properties.add(toAdd);
	}
	
	protected List<Metadata> convertProperties() {
		List<Metadata> metadata = new ArrayList<Metadata>();
		for (Property p : properties) 
			metadata.add(new Metadata(p.getKey(), p.getValue()));
		return metadata;
	}
	
	@Override
	public abstract boolean hasChildren();

	@Override
	public abstract List<ReportComponent> getChildren();

	@Override
	public abstract String getStringValue();
	
	@Override
	public abstract BasicComponent getModelComponent();
}
