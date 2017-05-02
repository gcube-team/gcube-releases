package gr.uoa.di.madgik.commons.configuration;

import gr.uoa.di.madgik.commons.configuration.parameter.IParameter;
import gr.uoa.di.madgik.commons.configuration.parameter.ObjectParameter;
import gr.uoa.di.madgik.commons.configuration.parameter.ParameterFactory;
import gr.uoa.di.madgik.commons.configuration.parameter.elements.graph.DependencyGraph;
import gr.uoa.di.madgik.commons.utils.XMLUtils;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is the main entry point to the Configuration Utility. Once the {@link ConfigurationManager}
 * class is loaded, the configuration file is loaded and parsed, the {@link DependencyGraph} is cosntructed
 * and then resolved. If after the parameter evaluation there are still some parameters that were not resolved
 * the process stops and the Configuration manager will not be able to be used for all the parameters that were
 * declared in the configuration file. Since all configuration parameters are evaluated at startup and the default
 * behaviour for most parameters is to serve values that cannot be changed, the {@link ConfigurationManager}
 * does not perform any type of locking. One parameter though, {@link ObjectParameter} can be set to be shared.
 * This means that the same reference will be passed to any requestor of the parameter. In this case any kind of
 * locking that might be nessecary should be performed by the client code, or be handled withinthe objec itself.
 * Event though a client might change the value of an {@link ObjectParameter} field, this change is not reflected
 * in the configuration file. If the {@link ConfigurationManager} is reinitialized, the change made to the object's
 * field are not mirrored.
 *
 * TODO Rethink the way the configuration file is found
 *
 * @author gpapanikos
 */
public class ConfigurationManager
{

	private static Logger logger = Logger.getLogger(ConfigurationManager.class.getName());
	private static File confFile = null;
	
	private static Map<String, IParameter> Parameters = null;


	static
	{
		try
		{
			ConfigurationManager.confFile = new File("configuration.conf");
			if(!ConfigurationManager.confFile.exists())
			{
				Properties props = new Properties();
				URL configResource = Thread.currentThread().getContextClassLoader().getResource("configuration.properties");
				if(configResource != null)
				{
					props.load(configResource.openStream());
					String filename = props.getProperty("configuration.filename");
					ConfigurationManager.confFile = new File(filename);
				}
				
			}
			ConfigurationManager.Parameters = new Hashtable<String, IParameter>();
			ConfigurationManager.Parse();
			ConfigurationManager.GenerateParameters();
		} catch (Exception ex)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Could not initialize configuration manager", ex);
		}
	}

	/**
	 * Retrieves the configuration file the {@link ConfigurationManager} is using
	 *
	 * @return the configuration file
	 */
	public static File GetGonfigurationFile()
	{
		return ConfigurationManager.confFile;
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static Object GetParameter(String ParameterName) throws Exception
	{
		IParameter param = ConfigurationManager.Parameters.get(ParameterName);
		if (param == null)
		{
			throw new Exception("Parameter " + ParameterName + " does not exist");
		}
		if (param.IsInternal())
		{
			throw new Exception("Parameter " + ParameterName + " does not exist");
		}
		return param.GetValue();
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static Boolean GetBooleanParameter(String ParameterName) throws Exception
	{
		Object val = ConfigurationManager.GetParameter(ParameterName);
		if (val instanceof Boolean)
		{
			return (Boolean) val;
		}
		throw new Exception("Could not convert parameter value to boolean");
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static Byte GetByteParameter(String ParameterName) throws Exception
	{
		Object val = ConfigurationManager.GetParameter(ParameterName);
		if (val instanceof Byte)
		{
			return (Byte) val;
		}
		throw new Exception("Could not convert parameter value to byte");
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static Double GetDoubleParameter(String ParameterName) throws Exception
	{
		Object val = ConfigurationManager.GetParameter(ParameterName);
		if (val instanceof Double)
		{
			return (Double) val;
		}
		throw new Exception("Could not convert parameter value to double");
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static Float GetFloatParameter(String ParameterName) throws Exception
	{
		Object val = ConfigurationManager.GetParameter(ParameterName);
		if (val instanceof Float)
		{
			return (Float) val;
		}
		throw new Exception("Could not convert parameter value to float");
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static Integer GetIntegerParameter(String ParameterName) throws Exception
	{
		Object val = ConfigurationManager.GetParameter(ParameterName);
		if (val instanceof Integer)
		{
			return (Integer) val;
		}
		throw new Exception("Could not convert parameter value to integer");
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static Long GetLongParameter(String ParameterName) throws Exception
	{
		Object val = ConfigurationManager.GetParameter(ParameterName);
		if (val instanceof Long)
		{
			return (Long) val;
		}
		throw new Exception("Could not convert parameter value to long");
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static Short GetShortParameter(String ParameterName) throws Exception
	{
		Object val = ConfigurationManager.GetParameter(ParameterName);
		if (val instanceof Short)
		{
			return (Short) val;
		}
		throw new Exception("Could not convert parameter value to short");
	}

	/**
	 * Retrieves the parameter value attached to the specified key if the key points to a valid non internal
	 * parameter. If the key does not point to a valid non internal entry, an exception is thrown
	 *
	 * @param ParameterName the name of the parameter
	 * @return the value of the parameter
	 * @throws java.lang.Exception The parameter is not defined of the value is not of the expected type
	 */
	public static String GetStringParameter(String ParameterName) throws Exception
	{
		Object val = ConfigurationManager.GetParameter(ParameterName);
		if (val instanceof String)
		{
			return (String) val;
		}
		throw new Exception("Could not convert parameter value to string");
	}

	private static void GenerateParameters() throws Exception
	{
		DependencyGraph graph = new DependencyGraph(ConfigurationManager.Parameters);
		graph.ConstructGraph();
		graph.ResolveDependencies();
		if (!graph.AllChecked())
		{
			throw new Exception("unable to resolve all dependencies");
		}
	}

	private static void Parse()
	{
		try
		{
			FileReader reader = new FileReader(ConfigurationManager.GetGonfigurationFile());
			StringBuilder buf = new StringBuilder();
			while (true)
			{
				char[] cbuf = new char[1024];
				int num = reader.read(cbuf);
				if (num < 0)
				{
					break;
				}
				buf.append(cbuf, 0, num);
			}
			Document doc = XMLUtils.Deserialize(buf.toString());
			List<Element> params = XMLUtils.GetChildElementsWithName(doc.getDocumentElement(), "param");
			for (Element par : params)
			{
				IParameter param = ParameterFactory.GetParameter(par);
				if (ConfigurationManager.Parameters.containsKey(param.GetName()))
				{
					throw new Exception("Duplicate key " + param.GetName());
				}
				ConfigurationManager.Parameters.put(param.GetName(), param);
			}
		} catch (Exception ex)
		{
			if(logger.isLoggable(Level.WARNING)) logger.log(Level.WARNING, "Could not parse Configuration file", ex);
		}
	}
}
