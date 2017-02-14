package org.gcube.datatransformation.datatransformationlibrary.model;

import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>Parameter</tt> class represents a content type parameter.
 * </p>
 */
public class Parameter {
//	TODO: Add isOptional
	private String name=null;
	private String value=null;
	private boolean isOptional=false;
	
	/**
	 * Instantiates a new parameter.
	 */
	public Parameter(){
		
	}
	/**
	 * Instantiates a new parameter by setting its name and value.
	 * @param name The name of the parameter.
	 * @param value The value of the parameter.
	 */
	public Parameter(String name, String value) {
		super();
		this.setName(name);
		this.setValue(value);
	}
	/**
	 * Returns the name of the parameter.
	 * @return The name of the parameter.
	 */
	public String getName() {
		return name;
	}
	/**
	 * Sets the name of the parameter.
	 * @param name The name of the parameter.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * Returns the value of the parameter.
	 * @return The value of the parameter.
	 */
	public String getValue() {
		return value.replaceAll("\\\\n", "\n");
	}
	/**
	 * Sets the value of the parameter.
	 * @param value The value of the parameter.
	 */
	public void setValue(String value) {
		this.value = value.replaceAll("\n", "\\\\n");
	}

//	public static void main(String[] args) {
//		System.out.println("\\n");
//		String name = "name";
//		String value = "va\nlue";
//		Parameter par = new Parameter(name, value);
//		System.out.println(name + " " + value);
//		System.out.println(par.value);
//		String getValue = par.getValue();
//		System.out.println(par.getName() + " " + getValue);
//
//	}
	//TODO: Check this...
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param obj The object to be checked.
	 * @return True if this and the checked parameter are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Parameter other = (Parameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equalsIgnoreCase(other.value))
			return false;
		return true;
	}

	/**
	 * Checks if two arrays of parameters are equal.
	 * @param params1 The first array.
	 * @param params2 The second array.
	 * @return True if the two arrays are equal.
	 */
	public static boolean equals(List<Parameter> params1, List<Parameter> params2){
		if(params1==null&&params2==null)
			return true;
		if(params1==null&&params2.size()==0)
			return true;
		if(params2==null&&params1.size()==0)
			return true;
		if(params1==null&&params2.size()>0)
			return false;
		if(params2==null&&params1.size()>0)
			return false;
		if((params1.size()==0)&&(params2.size()==0))
			return true;
		if(params1.size()!=params2.size())
			return false;
		for(Parameter param : params1){
			if(!parameterExist(param,params2))
				return false;
		}
		return true;
	}

	/**
	 * Checks if the <tt>paramtofind</tt> exists in <tt>inparameters</tt> array. 
	 * @param paramtofind The parameter to be found.
	 * @param inparameters The array in which the <tt>paramtofind</tt> is searched in.
	 * @return True if the <tt>paramtofind</tt> exists in <tt>inparameters</tt> array.
	 */
	public static boolean parameterExist(Parameter paramtofind, List<Parameter> inparameters){
		if(paramtofind==null || inparameters==null)
			return false;
		for(Parameter tmpparam:inparameters){
			if(tmpparam.equals(paramtofind))
				return true;
		}
		return false;
	}

	/**
	 * Checks if the parameter name exists in <tt>parametersToSearch</tt> array. 
	 * @param parameterName The parameter name to be found
	 * @param parametersToSearch The array in which the <tt>parameterName</tt> is searched in.
	 * @return True if the parameter name exists.
	 */
	public static boolean parameterNameExist(String parameterName, Parameter[] parametersToSearch){
		if(parameterName==null || parametersToSearch==null)
			return false;
		for(Parameter tmpparam:parametersToSearch){
			if(tmpparam.getName().equalsIgnoreCase(parameterName))
				return true;
		}
		return false;
	}
	/**
	 * Returns the isOptional value.
	 * @return the isOptional value.
	 */
	public boolean isOptional() {
		return isOptional;
	}
	/**
	 * Sets the isOptional value.
	 * @param isOptional the isOptional value.
	 */
	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 * @return The hash code of this parameter.
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (isOptional ? 1231 : 1237);
		result = PRIME * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
		result = PRIME * result + ((value == null) ? 0 : value.toLowerCase().hashCode());
		return result;
	}
	
	/**
	 * Returns the hash code of an array of parameters.
	 * @param parameters The parameters from which the hash code is created.
	 * @return the hash code of an array of parameters.
	 */
	public static int hashCode(List<Parameter> parameters){
		if(parameters==null || parameters.size()==0)
			return 1231;
		
		Parameter tmp;
		for(int i=0;i<parameters.size();i++){
			for(int j=0;j<parameters.size()-1;j++){
				if(parameters.get(j).name.toLowerCase().compareTo(parameters.get(j+1).name.toLowerCase())>0){
					tmp = parameters.get(j);
					parameters.set(j, parameters.get(j+1));
					parameters.set(j+1, tmp);
				}
			}
		}
		
		final int PRIME = 31;
		int result = 1;
		for(Parameter param: parameters){
			result = PRIME * result + param.hashCode(); 
		}
		return result;
	}
	
	protected void toDOM(Element parent){
		Document doc = parent.getOwnerDocument();
		Element param = doc.createElement(XMLDefinitions.ELEMENT_parameter);
		Attr pname = doc.createAttribute(XMLDefinitions.ATTRIBUTE_parameterName);
		pname.setNodeValue(this.getName());
		Attr pvalue = doc.createAttribute(XMLDefinitions.ATTRIBUTE_parameterValue);
		pvalue.setNodeValue(this.getValue());
		Attr isOptional = doc.createAttribute(XMLDefinitions.ATTRIBUTE_parameterIsOptional);
		isOptional.setTextContent(String.valueOf(this.isOptional()));
		param.setAttributeNode(pname);
		param.setAttributeNode(pvalue);
		param.setAttributeNode(isOptional);
		parent.appendChild(param);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 * @return A string representation of <tt>Parameter</tt>.
	 */
	@Override
	public String toString(){
		return this.getName()+"=\""+this.getValue()+"\"";
	}
}
