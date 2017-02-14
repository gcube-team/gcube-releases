package gr.uoa.di.madgik.commons.configuration.parameter;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class to instnatiate {@link IParameter} instnaces from their XML serialization
 *
 * @author gpapanikos
 */
public class ParameterFactory
{

	/**
	 * Idetirfies the {@link IParameter} type that the XML subtree defines, instnatiates
	 * it and populates it
	 *
	 * @param xml the XML subtree
	 * @return the paramter instnace
	 * @throws java.lang.Exception the deserialization could not be performed
	 */
	public static IParameter GetParameter(String xml) throws Exception
	{
		Document doc = XMLUtils.Deserialize(xml);
		return ParameterFactory.GetParameter(doc.getDocumentElement());
	}

	/**
	 * Idetirfies the {@link IParameter} type that the XML subtree defines, instnatiates
	 * it and populates it
	 *
	 * @param element the XML subtree
	 * @return the paramter instnace
	 * @throws java.lang.Exception the deserialization could not be performed
	 */
	public static IParameter GetParameter(Element element) throws Exception
	{
		if (!XMLUtils.AttributeExists(element, "type"))
		{
			throw new Exception("Not a valid serialization of parameter");
		}
		IParameter.ParameterType paramt = IParameter.ParameterType.valueOf(XMLUtils.GetAttribute(element, "type"));
		IParameter param = null;
		switch (paramt)
		{
			case BooleanClass:
			{
				param = new BooleanClassParameter();
				break;
			}
			case BooleanPrimitive:
			{
				param = new BooleanPrimitiveParameter();
				break;
			}
			case ByteClass:
			{
				param = new ByteClassParameter();
				break;
			}
			case BytePrimitive:
			{
				param = new BytePrimitiveParameter();
				break;
			}
			case DoubleClass:
			{
				param = new DoubleClassParameter();
				break;
			}
			case DoublePrimitive:
			{
				param = new DoublePrimitiveParameter();
				break;
			}
			case FloatClass:
			{
				param = new FloatClassParameter();
				break;
			}
			case FloatPrimitive:
			{
				param = new FloatPrimitiveParameter();
				break;
			}
			case IntegerClass:
			{
				param = new IntegerClassParameter();
				break;
			}
			case IntegerPrimitive:
			{
				param = new IntegerPrimitiveParameter();
				break;
			}
			case LongClass:
			{
				param = new LongClassParameter();
				break;
			}
			case LongPrimitive:
			{
				param = new LongPrimitiveParameter();
				break;
			}
			case ShortClass:
			{
				param = new ShortClassParameter();
				break;
			}
			case ShortPrimitive:
			{
				param = new ShortPrimitiveParameter();
				break;
			}
			case String:
			{
				param = new StringParameter();
				break;
			}
			case XML:
			{
				param = new XMLParameter();
				break;
			}
			case Object:
			{
				param = new ObjectParameter();
				break;
			}
		}
		param.FromXML(element);
		return param;
	}
}
