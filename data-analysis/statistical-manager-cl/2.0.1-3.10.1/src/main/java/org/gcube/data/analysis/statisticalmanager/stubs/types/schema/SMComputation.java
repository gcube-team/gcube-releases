package org.gcube.data.analysis.statisticalmanager.stubs.types.schema;

import static org.gcube.data.analysis.statisticalmanager.stubs.SMConstants.TYPES_NAMESPACE;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(namespace = TYPES_NAMESPACE)
public class SMComputation extends SMOperation {
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String title;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String algorithm;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String category;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private List<SMEntry> parameters;
	@XmlElement(namespace = TYPES_NAMESPACE)
	private String infrastructure;
	
	 public SMComputation() {
		 super();
		 if(parameters==null)
			 this.parameters=new ArrayList<SMEntry>();
	    }

	    public SMComputation(
	          String algorithm,
	           String category,
	           String infrastructure,
	           List<SMEntry> parameters,
	           String title) {
	           this.title = title;
	           this.algorithm = algorithm;
	           this.category = category;
	           if(parameters!=null)
	           this.parameters = parameters;
	           this.infrastructure = infrastructure;
	    }

	
    public String title() {
        return title;
    }

   
    
    public void title(String value) {
        this.title = value;
    }

   
    public String algorithm() {
        return algorithm;
    }

   
    public void algorithm(String value) {
        this.algorithm = value;
    }

 
    public String category() {
        return category;
    }

   
    public void category(String value) {
        this.category = value;
    }

   
    public List<SMEntry> parameters() {
        
        return this.parameters;
    }
    
    public void parameters(List<SMEntry> parameters) {
    	if(parameters!=null)
        this.parameters=parameters;
    }

  
    public String infrastructure() {
        return infrastructure;
    }

 
    public void infrastructure(String value) {
        this.infrastructure = value;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMComputation [title=");
		builder.append(title);
		builder.append(", algorithm=");
		builder.append(algorithm);
		builder.append(", category=");
		builder.append(category);
		builder.append(", parameters=");
		builder.append(parameters);
		builder.append(", infrastructure=");
		builder.append(infrastructure);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}
