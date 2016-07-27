package gr.uoa.di.madgik.commons.configuration.parameter;

import gr.uoa.di.madgik.commons.configuration.parameter.elements.Constructor;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.Method;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Parameter of type {@link gr.uoa.di.madgik.commons.configuration.parameter.IParameter.ParameterType#Object}.
 * An example of this instnace configuratino type is the following
 * <p>
 * <pre>
 * {@code
 *
 * <param name="parameterKey" type="Object" generated="false" internal="false" shared="false">
 *  <class value="full.package.name.ClassName"/>
 *  <constructor>
 *   <arguments>
 *    <arg order="1" name="arg1" param="parameterKey1"/>
 *    <arg order="2" name="arg2" param="parameterKey2"/>
 *    <arg order="3" name="arg3" param="parameterKey3"/>
 *   </arguments>
 *  </constructor>
 *  <calls>
 *   <method order="1" name="method1">
 *    <arguments>
 *     <arg order="1" name="arg1" param="parameterKey4"/>
 *     <arg order="2" name="arg2" param="parameterKey5"/>
 *     <arg order="3" name="arg3" param="parameterKey6"/>
 *    </arguments>
 *    <output param="parameterKey7"/>
 *   </method>
 *   <method order="2" name="method2">
 *    <arguments>
 *     <arg order="1" name="arg1" param="parameterKey8"/>
 *     <arg order="2" name="arg2" param="parameterKey9"/>
 *     <arg order="3" name="arg3" param="parameterKey10"/>
 *    </arguments>
 *    <output param="parameterKey11"/>
 *   </method>
 *  </calls>
 * </param>
 *
 * }
 * </pre>
 * </p>
 * If the {@link ObjectParameter#IsShared()} is set to false, the object needs also to implement the
 * {@link ICloneable} interface in order to Clone the object and serve a disjoint reference everytime
 * it is requested by a client.
 *
 * @author gpapanikos
 */
public class ObjectParameter implements IParameter
{

	private String Name = null;
	private Object Value = null;
	private boolean generated = false;
	private boolean internal = false;
	private String ClassName = null;
	private Constructor Constructor = null;
	private List<Method> Methods = null;
	private boolean checked = false;
	private boolean shared = false;

	public Class<?> GetParameterClassType()
	{
		return this.Value.getClass();
	}

	public Boolean IsChecked()
	{
		return this.checked;
	}

	public void Check()
	{
		this.checked = true;
	}

	public String GetClassName()
	{
		return this.ClassName;
	}

	public Constructor GetConstructor()
	{
		return this.Constructor;
	}

	public List<Method> GetMethods()
	{
		Collections.sort(this.Methods);
		return this.Methods;
	}

	public ParameterType GetParameterType()
	{
		return ParameterType.Object;
	}

	public String GetName()
	{
		return Name;
	}

	public Boolean IsGenerated()
	{
		return this.generated;
	}

	public Boolean IsInternal()
	{
		return this.internal;
	}

	public Boolean IsShared()
	{
		return this.shared;
	}

	public Object GetValue()
	{
		if (!this.IsShared() && (this.Value instanceof ICloneable))
		{
			return ((ICloneable) this.Value).Clone();
		}
		return this.Value;
	}

	public void SetValue(Object Value) throws Exception
	{
		this.Value = Value;
	}

	public void FromXML(String xml) throws Exception
	{
		Document doc = XMLUtils.Deserialize(xml);
		this.FromXML(doc.getDocumentElement());
	}

	public void FromXML(Element element) throws Exception
	{
		if (!XMLUtils.AttributeExists(element, "name"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		this.Name = XMLUtils.GetAttribute(element, "name");
		if (!XMLUtils.AttributeExists(element, "type"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		if (!ParameterType.valueOf(XMLUtils.GetAttribute(element, "type")).equals(ParameterType.Object))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		if (!XMLUtils.AttributeExists(element, "shared"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		this.shared = Boolean.getBoolean(XMLUtils.GetAttribute(element, "shared"));
		if (!XMLUtils.AttributeExists(element, "generated"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		this.generated = Boolean.getBoolean(XMLUtils.GetAttribute(element, "generated"));
		if (!XMLUtils.AttributeExists(element, "internal"))
		{
			throw new Exception("Not valid serialization of parameter");
		}
		this.internal = Boolean.getBoolean(XMLUtils.GetAttribute(element, "internal"));
		if (!this.generated)
		{
			Element classelem = XMLUtils.GetChildElementWithName(element, "class");
			if (classelem == null)
			{
				throw new Exception("Not valid serialization of parameter");
			}
			if (!XMLUtils.AttributeExists(classelem, "value"))
			{
				throw new Exception("Not valid serialization of parameter");
			}
			this.ClassName = XMLUtils.GetAttribute(classelem, "value");
			Element constrelem = XMLUtils.GetChildElementWithName(element, "constructor");
			if (constrelem == null)
			{
				this.Constructor = null;
			} else
			{
				this.Constructor = new Constructor();
				this.Constructor.FromXML(constrelem);
			}
			Element callselem = XMLUtils.GetChildElementWithName(element, "calls");
			if (callselem == null)
			{
				this.Methods = new ArrayList<Method>();
			} else
			{
				this.Methods = new ArrayList<Method>();
				List<Element> methelems = XMLUtils.GetChildElementsWithName(callselem, "method");
				for (Element meth : methelems)
				{
					Method m = new Method();
					m.FromXML(meth);
					this.Methods.add(m);
				}
			}
		}
	}
}
