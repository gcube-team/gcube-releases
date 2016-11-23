package gr.uoa.di.madgik.environment.is.elements;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemSerializationException;
import java.io.Serializable;
import java.util.Map;
import org.w3c.dom.Element;

public interface IInformationSystemElement extends Serializable
{
//	public enum ElementType
//	{
//		Node,
//		BoundaryListerner
//	}
//	
//	public ElementType GetElementType();
	public Map<String,ExtensionPair> getStaticInfo();
	public Map<String,ExtensionPair> getDynamicInfo();
	public String ToXML(boolean includeStatic, boolean includeDynamic) throws EnvironmentInformationSystemSerializationException;
	public void FromXML(String xml) throws EnvironmentInformationSystemSerializationException;
	public void FromXML(Element xml) throws EnvironmentInformationSystemSerializationException;
}
