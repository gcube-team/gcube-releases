package gr.uoa.di.madgik.commons.state.store;

import gr.uoa.di.madgik.commons.configuration.parameter.ICloneable;
import gr.uoa.di.madgik.commons.state.StateManager;
import java.io.File;

/**
 * Configuration class holding information on the files that must be used for the State Store Repository to
 * persist information on the registry and the data. If the files are changed and then the {@link StateManager}
 * is reinitialized, all previously stored state will not be available.
 *
 * @author gpapanikos
 */
public class StateStoreInfo implements ICloneable
{

	private File EntryRegistryFile = null;
	private File EntryDataFile = null;

	/**
	 * Creates a new instance of the class
	 * 
	 * @param EntryRegistryFile The file to use for the registry repository
	 * @param EntryDataFile The file to use for the data repository
	 */
	public StateStoreInfo(File EntryRegistryFile, File EntryDataFile)
	{
		this.EntryRegistryFile = EntryRegistryFile;
		this.EntryDataFile = EntryDataFile;
		if(this.EntryDataFile.getAbsolutePath().equals(this.EntryRegistryFile.getAbsolutePath()))
			throw new IllegalArgumentException("Registry and Data files cannot be the same");
	}

	/**
	 * Retrieves the file to use for the registry repository
	 *
	 * @return the file
	 */
	public File GetEntryRegistryFile()
	{
		return this.EntryRegistryFile;
	}

	/**
	 * Retrieves the file to use for the data repository
	 *
	 * @return The file
	 */
	public File GetEntryDataFile()
	{
		return this.EntryDataFile;
	}

	@Override
	public String toString()
	{
		return "EntryRegistryFile : " + EntryRegistryFile + ", EntryDataFile : " + EntryDataFile;
	}

	public Object Clone()
	{
		return new StateStoreInfo(new File(new String(this.EntryRegistryFile.getAbsolutePath())), new File(new String(this.EntryDataFile.getAbsolutePath())));
	}
}
