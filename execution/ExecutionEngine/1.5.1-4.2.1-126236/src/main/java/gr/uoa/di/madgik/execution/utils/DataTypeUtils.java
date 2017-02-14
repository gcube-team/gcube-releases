package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.commons.utils.XMLUtils;
import gr.uoa.di.madgik.execution.datatype.DataTypeArray;
import gr.uoa.di.madgik.execution.datatype.DataTypeBooleanClass;
import gr.uoa.di.madgik.execution.datatype.DataTypeBooleanPrimitive;
import gr.uoa.di.madgik.execution.datatype.DataTypeConvertable;
import gr.uoa.di.madgik.execution.datatype.DataTypeDoubleClass;
import gr.uoa.di.madgik.execution.datatype.DataTypeDoublePrimitive;
import gr.uoa.di.madgik.execution.datatype.DataTypeFloatClass;
import gr.uoa.di.madgik.execution.datatype.DataTypeFloatPrimitive;
import gr.uoa.di.madgik.execution.datatype.DataTypeIntegerClass;
import gr.uoa.di.madgik.execution.datatype.DataTypeIntegerPrimitive;
import gr.uoa.di.madgik.execution.datatype.DataTypeLongClass;
import gr.uoa.di.madgik.execution.datatype.DataTypeLongPrimitive;
import gr.uoa.di.madgik.execution.datatype.DataTypeReflectable;
import gr.uoa.di.madgik.execution.datatype.DataTypeResultSet;
import gr.uoa.di.madgik.execution.datatype.DataTypeString;
import gr.uoa.di.madgik.execution.datatype.IDataType;
import gr.uoa.di.madgik.execution.datatype.NamedDataType;
import gr.uoa.di.madgik.execution.exception.ExecutionSerializationException;
import gr.uoa.di.madgik.execution.exception.ExecutionValidationException;
import java.net.URI;
import java.net.URL;
import org.w3c.dom.Element;

public class DataTypeUtils
{
	public static URI GetValueAsProxyLocator(Object value) throws ExecutionValidationException
	{
		if(value==null) return null;
		if(value instanceof URI) return (URI)value;
		if(value instanceof String)
		{
			try
			{
				return new URI((String)value);
			}
			catch(Exception ex)
			{
				throw new ExecutionValidationException("Could not transform value "+value+" to proxy locator",ex);
			}
		}
		throw new ExecutionValidationException("Could not transform value "+value+" to proxy locator");
	}

	public static URI GetValueAsStoreLocator(Object value) throws ExecutionValidationException
	{
		if(value==null) return null;
		if(value instanceof URI) return (URI)value;
		if(value instanceof String)
		{
			try
			{
				return new URI((String)value);
			}
			catch(Exception ex)
			{
				throw new ExecutionValidationException("Could not transform value "+value+" to store locator",ex);
			}
		}
		throw new ExecutionValidationException("Could not transform value "+value+" to store locator");
	}
	
	public static URL GetValueAsURL(Object value) throws ExecutionValidationException
	{
		if(value==null) return null;
		if(value instanceof URL) return (URL)value;
		if(value instanceof String)
		{
			try
			{
				return new URL((String)value);
			}catch(Exception ex)
			{
				throw new ExecutionValidationException("Could not transform value "+value+" to URL",ex);
			}
		}
		throw new ExecutionValidationException("Could not transform value "+value+" to URL");
	}
	
	public static String GetValueAsString(Object value) throws ExecutionValidationException
	{
		if(value==null) return null;
		if(value instanceof Double) return Double.toString((Double)value);
		if(value instanceof Float) return Float.toString((Float)value);
		if(value instanceof Boolean) return Boolean.toString((Boolean)value);
		if(value instanceof Integer) return Integer.toString((Integer)value);
		if(value instanceof String) return (String)value;
		throw new ExecutionValidationException("Could not transform value "+value+" to string");
	}
	
	public static int GetValueAsInteger(Object value) throws ExecutionValidationException
	{
		if(value==null) throw new ExecutionValidationException("Could not transform null value to integer");
		if(value instanceof Double) return  ((Double)value).intValue();
		if(value instanceof Float) return ((Float)value).intValue();
		if(value instanceof Boolean) return (((Boolean)value).booleanValue() ? 1 : 0);
		if(value instanceof Integer) return ((Integer)value).intValue();
		if(value instanceof String) return Integer.parseInt((String)value);
		throw new ExecutionValidationException("Could not transform value "+value+" to integer");
	}
	
	public static long GetValueAsLong(Object value) throws ExecutionValidationException
	{
		if(value==null) throw new ExecutionValidationException("Could not transform null value to long");
		if(value instanceof Double) return  ((Double)value).longValue();
		if(value instanceof Float) return ((Float)value).longValue();
		if(value instanceof Boolean) return (((Boolean)value).booleanValue() ? 1 : 0);
		if(value instanceof Integer) return ((Integer)value).longValue();
		if(value instanceof String) return Long.parseLong((String)value);
		throw new ExecutionValidationException("Could not transform value "+value+" to long");
	}
	
	public static double GetValueAsDouble(Object value) throws ExecutionValidationException
	{
		if(value==null) throw new ExecutionValidationException("Could not transform null value to double");
		if(value instanceof Double) return ((Double)value).doubleValue();
		if(value instanceof Float) return ((Float)value).doubleValue();
		if(value instanceof Boolean) return (((Boolean)value).booleanValue() ? 1 : 0);
		if(value instanceof Integer) return ((Integer)value).doubleValue();
		if(value instanceof String) return Double.parseDouble((String)value);
		throw new ExecutionValidationException("Could not transform value "+value+" to double");
	}
	
	public static float GetValueAsFloat(Object value) throws ExecutionValidationException
	{
		if(value==null) throw new ExecutionValidationException("Could not transform null value to float");
		if(value instanceof Double) return ((Double)value).floatValue();
		if(value instanceof Float) return ((Float)value).floatValue();
		if(value instanceof Boolean) return (((Boolean)value).booleanValue() ? 1 : 0);
		if(value instanceof Integer) return ((Integer)value).floatValue();
		if(value instanceof String) return Float.parseFloat((String)value);
		throw new ExecutionValidationException("Could not transform value "+value+" to float");
	}
	
	public static boolean GetValueAsBoolean(Object value) throws ExecutionValidationException
	{
		if(value==null) throw new ExecutionValidationException("Could not transform null value to boolean");
		if(value instanceof Double)
		{
			if(((Double)value)==0) return Boolean.FALSE;
			else if (((Double)value)==1) return Boolean.TRUE;
			throw new ExecutionValidationException("Could not transform value to boolean");
		}
		if(value instanceof Float)
		{
			if(((Float)value)==0) return Boolean.FALSE;
			else if (((Float)value)==1) return Boolean.TRUE;
			throw new ExecutionValidationException("Could not transform value to boolean");
		}
		if(value instanceof Boolean) return ((Boolean)value).booleanValue();
		if(value instanceof Integer)
		{
			if(((Integer)value)==0) return Boolean.FALSE;
			else if (((Integer)value)==1) return Boolean.TRUE;
			throw new ExecutionValidationException("Could not transform value to boolean");
		}
		if(value instanceof String)
		{
			boolean b= Boolean.parseBoolean((String)value);
			if(!b)
			{
				if(((String)value).equals("1")) return Boolean.TRUE;
				else if(((String)value).equals("0")) return Boolean.FALSE;
				else if (((String)value).equalsIgnoreCase("false")) return Boolean.FALSE;
			}
			return b;
		}
		throw new ExecutionValidationException("Could not transform value "+value+" to boolean");
	}
	
	public static NamedDataType GetNamedDataType(boolean IsAvailable, String Name, String Token, IDataType.DataTypes type, Object Value) throws ExecutionValidationException
	{
		NamedDataType ndt=new NamedDataType();
		ndt.IsAvailable=IsAvailable;
		ndt.Name=Name;
		ndt.Token=Token;
		ndt.Value=DataTypeUtils.GetDataType(type, Value);
		return ndt;
	}

	public static IDataType GetDataType(IDataType.DataTypes type, Object value) throws ExecutionValidationException
	{
		IDataType dt = null;
		switch (type)
		{
			case BooleanClass:
			{
				dt = new DataTypeBooleanClass();
				break;
			}
			case BooleanPrimitive:
			{
				dt = new DataTypeBooleanPrimitive();
				break;
			}
			case DoubleClass:
			{
				dt = new DataTypeDoubleClass();
				break;
			}
			case DoublePrimitive:
			{
				dt = new DataTypeDoublePrimitive();
				break;
			}
			case FloatClass:
			{
				dt = new DataTypeFloatClass();
				break;
			}
			case FloatPrimitive:
			{
				dt = new DataTypeFloatPrimitive();
				break;
			}
			case IntegerClass:
			{
				dt = new DataTypeIntegerClass();
				break;
			}
			case IntegerPrimitive:
			{
				dt = new DataTypeIntegerPrimitive();
				break;
			}
			case LongClass:
			{
				dt = new DataTypeLongClass();
				break;
			}
			case LongPrimitive:
			{
				dt = new DataTypeLongPrimitive();
				break;
			}
			case String:
			{
				dt = new DataTypeString();
				break;
			}
			case ResultSet:
			{
				dt = new DataTypeResultSet();
				break;
			}
			case Convertable:
			{
				dt = new DataTypeConvertable();
				break;
			}
			case Reflectable:
			{
				dt = new DataTypeReflectable();
				break;
			}
			case Array:
			{
				dt = new DataTypeArray();
				break;
			}
			default:
			{
				throw new ExecutionValidationException("Unrecognized type found");
			}
		}
		dt.SetValue(value);
		return dt;
	}

	public static IDataType GetDataType(IDataType.DataTypes type) throws ExecutionValidationException
	{
		IDataType dt = null;
		switch (type)
		{
			case BooleanClass:
			{
				dt = new DataTypeBooleanClass();
				break;
			}
			case BooleanPrimitive:
			{
				dt = new DataTypeBooleanPrimitive();
				break;
			}
			case DoubleClass:
			{
				dt = new DataTypeDoubleClass();
				break;
			}
			case DoublePrimitive:
			{
				dt = new DataTypeDoublePrimitive();
				break;
			}
			case FloatClass:
			{
				dt = new DataTypeFloatClass();
				break;
			}
			case FloatPrimitive:
			{
				dt = new DataTypeFloatPrimitive();
				break;
			}
			case IntegerClass:
			{
				dt = new DataTypeIntegerClass();
				break;
			}
			case IntegerPrimitive:
			{
				dt = new DataTypeIntegerPrimitive();
				break;
			}
			case LongClass:
			{
				dt = new DataTypeLongClass();
				break;
			}
			case LongPrimitive:
			{
				dt = new DataTypeLongPrimitive();
				break;
			}
			case String:
			{
				dt = new DataTypeString();
				break;
			}
			case ResultSet:
			{
				dt = new DataTypeResultSet();
				break;
			}
			case Convertable:
			{
				dt = new DataTypeConvertable();
				break;
			}
			case Reflectable:
			{
				dt = new DataTypeReflectable();
				break;
			}
			case Array:
			{
				dt = new DataTypeArray();
				break;
			}
			default:
			{
				throw new ExecutionValidationException("Unrecognized type found");
			}
		}
		return dt;
	}

	public static IDataType []GetArrayOfDataType(IDataType.DataTypes type, int length) throws ExecutionValidationException
	{
		switch (type)
		{
			case BooleanClass:
			{
				return new DataTypeBooleanClass[length];
			}
			case BooleanPrimitive:
			{
				return new DataTypeBooleanPrimitive[length];
			}
			case DoubleClass:
			{
				return new DataTypeDoubleClass[length];
			}
			case DoublePrimitive:
			{
				return new DataTypeDoublePrimitive[length];
			}
			case FloatClass:
			{
				return new DataTypeFloatClass[length];
			}
			case FloatPrimitive:
			{
				return new DataTypeFloatPrimitive[length];
			}
			case IntegerClass:
			{
				return new DataTypeIntegerClass[length];
			}
			case IntegerPrimitive:
			{
				return new DataTypeIntegerPrimitive[length];
			}
			case LongClass:
			{
				return new DataTypeLongClass[length];
			}
			case LongPrimitive:
			{
				return new DataTypeLongPrimitive[length];
			}
			case String:
			{
				return new DataTypeString[length];
			}
			case Reflectable:
			{
				return new DataTypeReflectable[length];
			}
			case ResultSet:
			{
				return new DataTypeResultSet[length];
			}
			case Convertable:
			{
				return new DataTypeConvertable[length];
			}
			case Array:
			{
				return new DataTypeArray[length];
			}
			default:
			{
				throw new ExecutionValidationException("Unrecognized type found");
			}
		}
	}
	
	public static Class<?> GetComponentTypeOfArrayInitializingCode(String arrayClassCode) throws ExecutionValidationException
	{
		if(arrayClassCode.endsWith("["+IDataType.DataTypes.BooleanPrimitive)) return boolean.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.DoublePrimitive)) return double.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.FloatPrimitive)) return float.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.IntegerPrimitive)) return int.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.LongPrimitive)) return long.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.String)) return String.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.IntegerClass)) return Integer.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.LongClass)) return Long.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.DoubleClass)) return Double.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.FloatClass)) return Float.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.BooleanClass)) return Boolean.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.ResultSet)) return URI.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.Convertable)) return DataTypeConvertable.class;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.Reflectable)) return DataTypeReflectable.class;
		else throw new ExecutionValidationException("Unrecognized component type in array initializer "+arrayClassCode);
	}
	
	public static IDataType.DataTypes GetComponentDataTypeOfArrayInitializingCode(String arrayClassCode) throws ExecutionValidationException
	{
		if(arrayClassCode.endsWith("["+IDataType.DataTypes.BooleanPrimitive)) return IDataType.DataTypes.BooleanPrimitive;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.DoublePrimitive)) return IDataType.DataTypes.DoublePrimitive;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.FloatPrimitive)) return IDataType.DataTypes.FloatPrimitive;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.IntegerPrimitive)) return IDataType.DataTypes.IntegerPrimitive;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.LongPrimitive)) return IDataType.DataTypes.LongPrimitive;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.String)) return IDataType.DataTypes.String;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.IntegerClass)) return IDataType.DataTypes.IntegerClass;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.LongClass)) return IDataType.DataTypes.LongClass;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.DoubleClass)) return IDataType.DataTypes.DoubleClass;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.FloatClass)) return IDataType.DataTypes.FloatClass;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.BooleanClass)) return IDataType.DataTypes.BooleanClass;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.ResultSet)) return IDataType.DataTypes.ResultSet;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.Convertable)) return IDataType.DataTypes.Convertable;
		else if(arrayClassCode.endsWith("["+IDataType.DataTypes.Reflectable)) return IDataType.DataTypes.Reflectable;
		else throw new ExecutionValidationException("Unrecognized component type in array initializer "+arrayClassCode);
	}
//	
//	public static int CountDimentionsOfObjectArray(Object array) throws ExecutionValidationException
//	{
//		if(!array.getClass().isArray()) return 0;
//		String arrayName=array.getClass().getName();
//		return DataTypeUtils.CountDimentionsOfObjectArrayCode(arrayName);
//	}
//	
	public static int CountDimentionsOfObjectArrayCode(String ArrayClassCode) throws ExecutionValidationException
	{
		if(!ArrayClassCode.startsWith("[")) throw new ExecutionValidationException("Array class code initializer not in correct format");
		return 1 + ArrayClassCode.lastIndexOf('[');
	}
	
	public static IDataType GetDataType(Element element) throws ExecutionSerializationException
	{
		try
		{
			IDataType elem=null;
			if(!XMLUtils.AttributeExists(element, "type")) throw new ExecutionSerializationException("Provided serialization is not valid");
			elem=DataTypeUtils.GetDataType(IDataType.DataTypes.valueOf(XMLUtils.GetAttribute(element, "type")));
			elem.FromXML(element);
			return elem;
		} catch (Exception ex)
		{
			throw new ExecutionSerializationException("Could not retrieve execution context config element from provided serialization", ex);
		}
	}
	
//	public static boolean AreEngineTypesEqual(String engineType, IDataType dataType) throws ExecutionValidationException
//	{
//		if(dataType==null) return false;
//		switch(dt.GetDataTypeEnum())
//		{
//			case BooleanClass:
//			case BooleanPrimitive:
//			case Convertable:
//			case DoubleClass:
//			case DoublePrimitive:
//			case FloatClass:
//			case FloatPrimitive:
//			case IntegerClass:
//			case IntegerPrimitive:
//			case LongClass:
//			case LongPrimitive:
//			case RecordStore:
//			case Reflectable:
//			case ResultSet:
//			case String:
//			{
//				return dt.GetDataTypeEnum().toString().equals(engineType);
//			}
//			case Array:
//			{
//				if(DataTypeUtils.CountDimentionsOfObjectArrayCode(((DataTypeArray)dataType).GetArrayClassCode())!=DataTypeUtils.CountDimentionsOfObjectArrayCode(engineType)) return false;
//				return DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(engineType).toString().equals(DataTypeUtils.GetComponentDataTypeOfArrayInitializingCode(((DataTypeArray)dataType).GetArrayClassCode()));
//			}
//			default:
//			{
//				throw new ExecutionValidationException("Unknown data type found");
//			}
//		}
//	}
//	
	public static boolean IsEngineTypeArray(String EngineType)
	{
		if(EngineType.startsWith("[")) return true;
		return false;
	}
	
	public static IDataType.DataTypes GetDataTypeOfEngineType(String EngineType)
	{
		if(DataTypeUtils.IsEngineTypeArray(EngineType)) return IDataType.DataTypes.Array;
		return IDataType.DataTypes.valueOf(EngineType);
	}
}
