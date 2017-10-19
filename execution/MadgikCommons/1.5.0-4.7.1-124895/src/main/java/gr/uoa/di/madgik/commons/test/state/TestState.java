package gr.uoa.di.madgik.commons.test.state;

import gr.uoa.di.madgik.commons.configuration.ConfigurationManager;
import gr.uoa.di.madgik.commons.state.StateManager;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gpapanikos
 */
public class TestState
{

	private static Logger logger = Logger.getLogger(TestState.class.getName());

	/**
	 * 
	 * @param args dfrsg
	 * @throws java.lang.Exception sdf
	 */
	public static void main(String[] args) throws Exception
	{
		boolean CleanUpOnExit = false;

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Run... " + StateManager.GetStateStoreInfo());

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Checking to add entries");

		if (!StateManager.Contains("alphanumeric"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Adding alphanumeric");
			StateManager.Put("alphanumeric", "this is the alphanumeric example");
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry with key alphanumeric already present");
		}
		if (!StateManager.Contains("bytearray"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Adding bytearray");
			StateManager.Put("bytearray", new String("this is the bytearray example").getBytes(ConfigurationManager.GetStringParameter("EncodingCharset")));
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry with key bytearray already present");
		}
		if (!StateManager.Contains("serializable"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Adding serializable");
			StateManager.Put("serializable", new TestClassSerializable());
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry with key serializable already present");
		}
		if (!StateManager.Contains("iserializable"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Adding iserializable");
			StateManager.Put("iserializable", new TestClassISerializable());
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry with key iserializable already present");
		}
		if (!StateManager.Contains("file"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Adding file");
			StateManager.Put("file", new File("/home/gpapanikos/Desktop/example.jpg"));
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry with key file already present");
		}

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Checking to retrieve entries");

		if (StateManager.Contains("alphanumeric"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving alphanumeric");
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + StateManager.GetAlphanumeric("alphanumeric"));
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry of alphanumeric not found");
		}
		if (StateManager.Contains("bytearray"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving bytearray");
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + new String(StateManager.GetByteArray("bytearray"), ConfigurationManager.GetStringParameter("EncodingCharset")));
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry of bytearray not found");
		}
		if (StateManager.Contains("serializable"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving serializable");
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + StateManager.GetSerializable("serializable"));
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry of serializable not found");
		}
		if (StateManager.Contains("iserializable"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving iserializable");
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + StateManager.GetISerializable("iserializable"));
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry of iserializable not found");
		}
		if (StateManager.Contains("file"))
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving file");
			File f = StateManager.GetFile("file");
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + f.getAbsolutePath());
			f.delete();
		} else
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Entry of file not found");
		}

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Deleting entry alphanumeric");

		StateManager.Delete("alphanumeric");

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Alphanumeric entry now exists : " + StateManager.Contains("alphanumeric"));

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Re-Adding alphanumeric");
		StateManager.Put("alphanumeric", "this is the alphanumeric example");
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Alphanumeric entry now exists : " + StateManager.Contains("alphanumeric"));
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving alphanumeric");
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + StateManager.GetAlphanumeric("alphanumeric"));

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Deleting entry bytearray");

		StateManager.Delete("bytearray");

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Bytearray entry now exists : " + StateManager.Contains("bytearray"));

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Re-Adding bytearray");
		StateManager.Put("bytearray", "this is the bytearray example".getBytes(ConfigurationManager.GetStringParameter("EncodingCharset")));
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Bytearray entry now exists : " + StateManager.Contains("bytearray"));
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving bytearray");
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + new String(StateManager.GetByteArray("bytearray"), ConfigurationManager.GetStringParameter("EncodingCharset")));

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Deleting entry serializable");

		StateManager.Delete("serializable");

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Serializable entry now exists : " + StateManager.Contains("serializable"));

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Re-Adding serializable");
		StateManager.Put("serializable", new TestClassSerializable());
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "serializable entry now exists : " + StateManager.Contains("serializable"));
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving serializable");
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + StateManager.GetSerializable("serializable").toString());

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Deleting entry file");

		StateManager.Delete("file");

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "File entry now exists : " + StateManager.Contains("file"));

		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Re-Adding file");
		StateManager.Put("file", new File("/home/gpapanikos/Desktop/example.jpg"));
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "File entry now exists : " + StateManager.Contains("file"));
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieving file");
		File f = StateManager.GetFile("file");
		if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Retrieved value is : " + f.getAbsolutePath());
		f.delete();

		if (CleanUpOnExit)
		{
			if(logger.isLoggable(Level.INFO)) logger.log(Level.INFO, "Cleanup entry file");
			StateManager.Compact();
		}

	}
}
