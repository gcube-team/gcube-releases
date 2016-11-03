package org.gcube.datatransformation.datatransformationlibrary.model;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.dataelements.DataElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * <tt>ContentType</tt> is the class representing the format of the payload of a {@link DataElement}.
 * </p>
 */
public class ContentType {
	
	private String mimeType;
	private List<Parameter> contentTypeParameters = new ArrayList<Parameter>();
	
	private static Logger log = LoggerFactory.getLogger(ContentType.class);
	
	/**
	 * Simple constructor.
	 */
	public ContentType() {}
	
	/**
	 * Instantiates a <tt>ContentType</tt> by setting the mimetype and the content type parameters.
	 * 
	 * @param mimeType The mimetype of the <tt>ContentType</tt>.
	 * @param contentTypeParameters The parameters of the <tt>ContentType</tt>.
	 */
	public ContentType(String mimeType, List<Parameter> contentTypeParameters) {
		this.mimeType = mimeType;
		this.contentTypeParameters = contentTypeParameters;
	}
	
	/**
	 * Returns the content type parameters.
	 * 
	 * @return The content type parameters.
	 */
	public List<Parameter> getContentTypeParameters() {
		return contentTypeParameters;
	}
	
	/**
	 * Sets the content type parameters.
	 * @param contentTypeParameters The content type parameters.
	 */
	public void setContentTypeParameters(List<Parameter> contentTypeParameters) {
		this.contentTypeParameters = contentTypeParameters;
	}
	
	/**
	 * Sets the content type parameters.
	 * @param contentTypeParameters The content type parameters.
	 */
	public void addContentTypeParameters(Parameter ... contentTypeParameters) {
		if(this.contentTypeParameters==null){
			this.contentTypeParameters=new ArrayList<Parameter>();
		}
		if(contentTypeParameters==null)
			return;
		for(Parameter param: contentTypeParameters){
			this.contentTypeParameters.add(param);
		}
	}
	
	/**
	 * Returns the mimetype of the <tt>ContentType</tt>.
	 * @return The mimetype of the <tt>ContentType</tt>.
	 */
	public String getMimeType() {
		return mimeType;
	}
	
	/**
	 * Sets the mimetype of the <tt>ContentType</tt>.
	 * @param mimeType The mimetype of the <tt>ContentType</tt>.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	/**
	 * Creates this <tt>ContentType</tt> from a dom element representing it.
	 * @param contentTypeElement The dom element of the content type.
	 */
	public void fromDOM(Element contentTypeElement){
		Element mtelm = (Element)contentTypeElement.getElementsByTagName(XMLDefinitions.ELEMENT_mimetype).item(0);
		this.mimeType=mtelm.getTextContent();
		Element formatparameters;
		if((formatparameters=(Element)contentTypeElement.getElementsByTagName(XMLDefinitions.ELEMENT_contenttypeparameters).item(0))!=null){
			ArrayList <Parameter> formatParametersList = new ArrayList<Parameter>();
			Element parameter;int cnt=0;
			while((parameter=(Element)formatparameters.getElementsByTagName(XMLDefinitions.ELEMENT_parameter).item(cnt++))!=null){
				Parameter param = new Parameter();
				param.setName(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterName));
				param.setValue(parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterValue));
				String isOptional = parameter.getAttribute(XMLDefinitions.ATTRIBUTE_parameterIsOptional);
				if(isOptional==null || isOptional.equalsIgnoreCase("true"))
					param.setOptional(true);
				formatParametersList.add(param);
			}
			this.contentTypeParameters=formatParametersList;
		}
	}
	
	protected void fromXML(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			ByteArrayInputStream instream = new ByteArrayInputStream(xml.getBytes());
			builder = factory.newDocumentBuilder();
			Document contentFormatDoc = builder.parse(instream);
			instream.close();
			fromDOM(contentFormatDoc.getDocumentElement());
		} catch (Exception e) {
			log.error("Could not parse the Content Format: "+xml,e);
			throw new Exception("Could not parse the content format: "+xml);
		}
	}

	public void toDOM(Element parent){
		Document doc = parent.getOwnerDocument();
		Element contentFormatElm = doc.createElement(XMLDefinitions.ELEMENT_contenttype);
		
		Element mimetype = doc.createElement(XMLDefinitions.ELEMENT_mimetype);
		mimetype.setTextContent(this.mimeType);
		contentFormatElm.appendChild(mimetype);
		
		Element fmtparams = doc.createElement(XMLDefinitions.ELEMENT_contenttypeparameters);
		if(this.contentTypeParameters!=null && this.contentTypeParameters.size()>0){
			for(Parameter fparam: this.contentTypeParameters){
				fparam.toDOM(fmtparams);
			}
		}
		contentFormatElm.appendChild(fmtparams);
		parent.appendChild(contentFormatElm);
	}
	
	/* 
	 * Attention to correctly implement this method in order to efficiently use 
	 * ContentFormat's equals and in contains methods of ArrayLists and others...
	 */
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @param obj The object to be checked.
	 * @return True if this and the parameter object are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ContentType other = (ContentType) obj;
		if (!Parameter.equals(contentTypeParameters, other.contentTypeParameters))
			return false;
		if (mimeType == null) {
			if (other.mimeType != null)
				return false;
		} else if (!mimeType.equalsIgnoreCase(other.mimeType))	{
			// TODO application/xml and text/xml are considered the same
			if (mimeType.equalsIgnoreCase("text/xml") && other.mimeType.equalsIgnoreCase("application/xml"))
				return true;
			if (mimeType.equalsIgnoreCase("application/xml") && other.mimeType.equalsIgnoreCase("text/xml"))
				return true;
			return false;
		}
		return true;
	}
	
	/*
	 * This method implements the support consistency level only for the parameters
	 * The mimetype must be also checked externally...
	 * 
	 * TODO: Take into consideration the isOptional parameter!!!
	 * TODO: Think what to do with the '-'!!!
	 */
	/**
	 * This method implements the support consistency level only for the parameters.
	 * @param supported The supported parameters 
	 * @param ifsupported The parameters to be checked.
	 * @return True if the <tt>ifsupported</tt> parameters are supported by <tt>supported</tt> parameters.
	 */
	public static boolean support(List<Parameter> supported, List<Parameter> ifsupported){
		
		ArrayList<Parameter> ifsupportedlist = new ArrayList<Parameter>();
		for(Parameter param: ifsupported)
			ifsupportedlist.add(param);
		ArrayList<Parameter> supportedlist = new ArrayList<Parameter>();
		for(Parameter param: supported)
			supportedlist.add(param);
		
//		This along with the loop states that all parameters must be present having the same # and names.
//		but should take into consideration the isOptional => the #mandatory parameters and their names must be equal...
		loop: while(!ifsupportedlist.isEmpty()){
			Parameter param = ifsupportedlist.get(0);
			for(Parameter supparam: supportedlist){
				if(param.getName().equalsIgnoreCase(supparam.getName())){
					if(!param.getValue().equalsIgnoreCase(supparam.getValue()) 
						&& !supparam.getValue().equals(XMLDefinitions.VALUE_any)
						&& !supparam.isOptional()){
						return false;
					}
					
					ifsupportedlist.remove(param);
					supportedlist.remove(supparam);
					continue loop;
				}
			}
			return false;
		}
		
		for(Parameter param: supportedlist){
			if(!param.isOptional()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method implements the support consistency level for the <tt>ContentTypes</tt>.
	 * @param supported The supported <tt>ContentType</tt>.
	 * @param ifsupported The <tt>ContentType</tt> to be checked.
	 * @return True if the <tt>ifsupported</tt> <tt>ContentType</tt> is supported by <tt>supported</tt> <tt>ContentType</tt>.
	 */
	public static boolean support(ContentType supported, ContentType ifsupported){
		if(!supported.getMimeType().equalsIgnoreCase(ifsupported.getMimeType()))
			return false;
		return support(supported.getContentTypeParameters(), ifsupported.getContentTypeParameters());
	}
	
	/**
	 * This method implements the generic support consistency level only for the parameters.
	 * @param supported The supported parameters 
	 * @param ifsupported The parameters to be checked.
	 * @return True if the <tt>ifsupported</tt> parameters are generically supported by <tt>supported</tt> parameters.
	 */
	public static boolean gensupport(List<Parameter> supported, List<Parameter> ifsupported){
		if(supported==null&&ifsupported==null)
			return true;
		if(supported==null&&ifsupported.size()==0)
			return true;
		if(ifsupported==null&&supported.size()==0)
			return true;
		if((supported.size()==0)&&(ifsupported.size()==0))
			return true;
		/*
		 * Generic support means that the source/target format may have some parameters
		 * but in this case they must have the same value...
		 */
		for(Parameter param:ifsupported){
			for(Parameter supparam: supported){
				if(param.getName().equalsIgnoreCase(supparam.getName()) 
						&& !param.getValue().equalsIgnoreCase(supparam.getValue()) 
						&& !supparam.getValue().equals(XMLDefinitions.VALUE_any)
						&& !supparam.getValue().equals(XMLDefinitions.VALUE_notset)
						&& !supparam.isOptional()){
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * This method implements the generic support consistency level for the <tt>ContentTypes</tt>.
	 * @param supported The supported <tt>ContentType</tt>.
	 * @param ifsupported The <tt>ContentType</tt> to be checked.
	 * @return True if the <tt>ifsupported</tt> <tt>ContentType</tt> is generically supported by <tt>supported</tt> <tt>ContentType</tt>.
	 */
	public static boolean gensupport(ContentType supported, ContentType ifsupported){
		if(!supported.getMimeType().equalsIgnoreCase(ifsupported.getMimeType()))
			return false;
		return gensupport(supported.getContentTypeParameters(), ifsupported.getContentTypeParameters());
	}
	
//	public static boolean supportsParameter(Parameter[] supported, Parameter ifsupported){
//		for(Parameter param:supported){
//			if(param.getName().equalsIgnoreCase(ifsupported.getName())){
//				if(param.getValue().equalsIgnoreCase(ifsupported.getName())||param.getValue().equalsIgnoreCase(XMLDefinitions.VALUE_any))
//					return true;
//				else
//					return false;
//			}
//		}
//		return false;
//	}
	
	/**
	 * @see java.lang.Object#toString()
	 * @return The string representation of the <tt>ContentType</tt>.
	 */
	@Override
	public String toString(){
		StringWriter writer= new StringWriter();
		writer.append("MimeType=\""+this.mimeType+"\"");
		if(contentTypeParameters!=null && contentTypeParameters.size()>0){
			for(Parameter param: contentTypeParameters){
				writer.append(", "+param.toString());
			}
		}
		return writer.toString();
	}
	
	/* 
	 * Support and fill unbound. Just testing this 
	 * null if not supported
	 * empty no unbound parameters
	 */
	/**
	 * This method fills the unbound parameters of the supported content type parameters if they are supported. Otherwise null.
	 * @param supported The supported parameters 
	 * @param ifsupported The parameters to be checked.
	 * @return The filled parameters if supported. Otherwise null.
	 */
	public static List<Parameter> supportAndFillUnbound(List<Parameter> supported, List<Parameter> ifsupported){
		ArrayList<Parameter> unbound = new ArrayList<Parameter>();
		
		ArrayList<Parameter> ifsupportedlist = new ArrayList<Parameter>();
		for(Parameter param: ifsupported)
			ifsupportedlist.add(param);
		ArrayList<Parameter> supportedlist = new ArrayList<Parameter>();
		for(Parameter param: supported)
			supportedlist.add(param);
		
//		This along with the loop states that all parameters must be present having the same # and names.
//		but should take into consideration the isOptional => the #mandatory parameters and their names must be equal...
		loop: while(!ifsupportedlist.isEmpty()){
			Parameter param = ifsupportedlist.get(0);
			for(Parameter supparam: supportedlist){
				if(param.getName().equalsIgnoreCase(supparam.getName())){
					if(!param.getValue().equalsIgnoreCase(supparam.getValue()) 
						&& !supparam.getValue().equals(XMLDefinitions.VALUE_any)
						&& !supparam.isOptional()){
						return null;
					}
					if(supparam.getValue().equals(XMLDefinitions.VALUE_any) 
							&& !param.getValue().equals(XMLDefinitions.VALUE_any)){
						unbound.add(new Parameter(param.getName(), param.getValue()));
					}
						
					ifsupportedlist.remove(param);
					supportedlist.remove(supparam);
					continue loop;
				}
			}
			return null;
		}
		
		for(Parameter param: supportedlist){
			if(!param.isOptional()){
				return null;
			}
		}
		return unbound;
	}
	
	/* 
	 * TODO: Attention to correctly implement this method in order to use ContentFormat
	 * in HashMaps 
	 */
	/**
	 * @see java.lang.Object#hashCode()
	 * @return The hashcode of the <tt>ContentType</tt>.
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + Parameter.hashCode(contentTypeParameters);
		result = PRIME * result + ((mimeType == null) ? 0 : mimeType.toLowerCase().hashCode());
		return result;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 * @return The <tt>ContentType</tt> cloned.
	 */
	public ContentType clone(){
		ContentType format = new ContentType();
		format.setMimeType(this.mimeType);
		if(this.contentTypeParameters!=null){
			ArrayList<Parameter> newparams = new ArrayList<Parameter>();
			for(Parameter param: contentTypeParameters){
				Parameter newparam = new Parameter();
				newparam.setName(param.getName());
				newparam.setValue(param.getValue());
				if(param.isOptional())
					newparam.setOptional(true);
				newparams.add(newparam);
			}
			format.setContentTypeParameters(newparams);
		}
		return format;
	}
}
