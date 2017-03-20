package gr.uoa.di.madgik.commons.configuration.parameter;

import gr.uoa.di.madgik.commons.configuration.ConfigurationManager;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.graph.DependencyGraph;
import org.w3c.dom.Element;

/**
 * All elements that can be defined and retrieved ffrom the {@link ConfigurationManager} are parameters. All
 * these parameters have to implement this interface.
 *
 * @author gpapanikos
 */
public interface IParameter
{

	/**
	 * The type of parameters tat can be declared and instnatiated. The primitive types are distinguished in bothe
	 * their boxed an unboxed representations as the java platform treats them differntly and a boxed value cannot be
	 * used in a method that needs an unboxed value and vise-versa
	 */
	public enum ParameterType
	{

		/**
		 * Integer boxed type
		 */
		IntegerClass,
		/**
		 * Integer unboxed type
		 */
		IntegerPrimitive,
		/**
		 * Float boxed type
		 */
		FloatClass,
		/**
		 * Float unboxed type
		 */
		FloatPrimitive,
		/**
		 * Double boxed type
		 */
		DoubleClass,
		/**
		 * Double unboxed type
		 */
		DoublePrimitive,
		/**
		 * Short boxed type
		 */
		ShortClass,
		/**
		 * Short unboxed type
		 */
		ShortPrimitive,
		/**
		 * Long boxed type
		 */
		LongClass,
		/**
		 * Long unboxed type
		 */
		LongPrimitive,
		/**
		 * Boolean boxed type
		 */
		BooleanClass,
		/**
		 * Boolean unboxed type
		 */
		BooleanPrimitive,
		/**
		 * Byte boxed type
		 */
		ByteClass,
		/**
		 * Byte unboxed type
		 */
		BytePrimitive,
		/**
		 * String type
		 */
		String,
		/**
		 * XML type
		 */
		XML,
		/**
		 * Object type
		 */
		Object
	}

	/**
	 * Retrieves the {@link gr.uoa.di.madgik.commons.configuration.parameter.IParameter} the implementing
	 * instnace represents
	 * 
	 * @return the parameter type
	 */
	public ParameterType GetParameterType();

	/**
	 *  Retrieves the name of the parameter
	 *
	 * @return the parameter name
	 */
	public String GetName();

	/**
	 * Retrieves whether or not the parameter is generated or a specific value of generation description
	 * is available in the same decleration
	 *
	 * @return whether or not the parameter is generated
	 */
	public Boolean IsGenerated();

	/**
	 * Retrieves whether or not the parameter is internal and is only used as an intermediate step during
	 * the initialization of an other parameter or it can also be accessed by external clients
	 *
	 * @return wheter or not the parameter is generated
	 */
	public Boolean IsInternal();

	/**
	 * Retrieves whether or not the parameter has been evaluated during the
	 * {@link DependencyGraph#ResolveDependencies()} process
	 *
	 * @return whether or not the parameter has been evaluated 
	 */
	public Boolean IsChecked();

	/**
	 * Sets that the parameter has been evaluated during the
	 * {@link DependencyGraph#ResolveDependencies()} process
	 */
	public void Check();

	/**
	 * Retrieves the value of the parameter
	 *
	 * @return the value
	 */
	public Object GetValue();

	/**
	 * Sets the value of the parameter
	 *
	 * @param Value the value
	 * @throws java.lang.Exception the value is not of correct type for the parameter instnace
	 */
	public void SetValue(Object Value) throws Exception;

	/**
	 * Retireves the class type of the value the parameter stored
	 *
	 * @return the class
	 */
	public Class<?> GetParameterClassType();

	/**
	 * Parses the provided xml subtree and populates the parameter instnace
	 *
	 * @param xml the xml subtree
	 * @throws java.lang.Exception the parsing could be performed
	 */
	public void FromXML(String xml) throws Exception;

	/**
	 * Parses the provided xml subtree and populates the parameter instnace
	 *
	 * @param element the xml subtree
	 * @throws java.lang.Exception the parsing could be performed
	 */
	public void FromXML(Element element) throws Exception;
}
