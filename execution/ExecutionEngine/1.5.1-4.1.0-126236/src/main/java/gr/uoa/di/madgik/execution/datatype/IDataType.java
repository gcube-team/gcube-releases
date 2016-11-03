package gr.uoa.di.madgik.execution.datatype;

import java.io.Serializable;

import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import org.w3c.dom.Element;

/**
 * The Interface IDataType.
 */
public interface IDataType extends Serializable
{
	
	/**
	 * The known DataTypes.
	 */
	public enum DataTypes
	{
		
		/** Boolean class implemented by {@link DataTypeBooleanClass}. */
		BooleanClass,
		
		/** Boolean primitive implemented by {@link DataTypeBooleanPrimitive}. */
		BooleanPrimitive,
		
		/** Double class implemented by {@link DataTypeDoubleClass}. */
		DoubleClass,
		
		/** Double primitive implemented by {@link DataTypeDoublePrimitive}. */
		DoublePrimitive,
		
		/** Float class implemented by {@link DataTypeFloatClass}. */
		FloatClass,
		
		/** Float primitive implemented by {@link DataTypeFloatPrimitive}. */
		FloatPrimitive,
		
		/** Integer class implemented by {@link DataTypeIntegerClass}. */
		IntegerClass,
		
		/** Integer primitive implemented by {@link DataTypeIntegerPrimitive}. */
		IntegerPrimitive,
		
		/** Long class implemented by {@link DataTypeLongClass}. */
		LongClass,
		
		/** Long primitive implemented by {@link DataTypeLongPrimitive}. */
		LongPrimitive,
		
		/** String implemented by {@link DataTypeString}. */
		String,
		
		/** Result set implemented by {@link DataTypeResultSet}. */
		ResultSet,
		
		/** Convertable implemented by {@link DataTypeConvertable}. */
		Convertable,
		
		/** Reflectable implemented by {@link DataTypeReflectable}. */
		Reflectable,
		
		/** Array implemented by {@link DataTypeArray}. */
		Array
	}
	
	/**
	 * Sets the value of this type
	 * 
	 * @param Value the value to set
	 * 
	 * @throws ExecutionValidationException A validation error occurred
	 */
	public void SetValue(Object Value) throws ExecutionValidationException;

	/**
	 * Retrieves the value of the type
	 * 
	 * @return the value
	 */
	public Object GetValue();

	/**
	 * Sets the value as a string representation
	 * 
	 * @param val the value to set
	 * 
	 * @throws ExecutionValidationException A validation error occurred
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public void SetStringValue(String val) throws ExecutionValidationException,ExecutionSerializationException;

	/**
	 * Retrieves the value as a string
	 * 
	 * @return the string representation of the value
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public String GetStringValue() throws ExecutionSerializationException;
	
	/**
	 * Parses the xml serialization of the data type as retrieved by {@link IDataType#ToXML()} 
	 * 
	 * @param XML the XML serialization
	 * 
	 * @throws ExecutionSerializationException A serialization error occurred
	 */
	public void FromXML(String XML) throws ExecutionSerializationException;
	
	/**
	 * Parses the xml serialization of the data type as retrieved by {@link IDataType#ToXML()} 
	 * 
	 * @param XML The root element of the serialization
	 * 
	 * @throws ExecutionSerializationException A validation error occurred
	 */
	public void FromXML(Element XML) throws ExecutionSerializationException;
	
	/**
	 * Creates an xml serialization of the data type
	 * 
	 * @return the XML serialization
	 * 
	 * @throws ExecutionSerializationException a serialization error occurred
	 */
	public String ToXML() throws ExecutionSerializationException;

	/**
	 * Retrieves if the implementation of the data type can suggest
	 * the run time type of the hosted value
	 * 
	 * @return true, if it can
	 */
	public boolean CanSuggestDataTypeClass();
	
	/**
	 * Retrieves the run time type of the data type's value 
	 * if {@link IDataType#CanSuggestDataTypeClass()} indicates that
	 * the data type can suggest a type
	 * 
	 * @return the run time type
	 */
	public Class<?> GetDataTypeClass();
	
	/**
	 * Retrieves the known data type of the type
	 * 
	 * @return the data type
	 */
	public IDataType.DataTypes GetDataTypeEnum();
}
