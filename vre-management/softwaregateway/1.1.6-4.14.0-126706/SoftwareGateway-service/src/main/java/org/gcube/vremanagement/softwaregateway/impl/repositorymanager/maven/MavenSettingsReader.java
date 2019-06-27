package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.maven.settings.Settings;

import org.apache.maven.settings.io.xpp3.SettingsXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

class MavenSettingsReader {

	static Settings settings = null;
	
	/**
	 * Gets the local settings
	 * @return the settings
	 */
	static Settings getSettings(File settingsFile) throws InvalidMavenSettings {
		try {
			return getSettings(new FileReader(settingsFile)) ;
		} catch (Exception e) {
			throw new InvalidMavenSettings(e);
		} 
	}
	
	/**
	 * Gets the local settings
	 * @return the settings
	 */
	static Settings getSettings(Reader reader) throws InvalidMavenSettings {
		if (settings == null)
			try {
				settings = loadSettingsProfiles(reader);
			} catch (Exception e) {
				throw new InvalidMavenSettings(e);
			} 
		return settings;
	}
	
	

	/**
	 * Load profiles from <code>settings.xml</code>.
	 * @param settingsFile the setting.xml file
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	static private Settings loadSettingsProfiles(Reader freader) throws FileNotFoundException, IOException, XmlPullParserException {
		SettingsXpp3Reader reader = new SettingsXpp3Reader();
		return reader.read(freader);
		
	}
	
	@SuppressWarnings("serial")
	static class InvalidMavenSettings extends Exception {
		InvalidMavenSettings(Exception e) {
			super(e);
		}
	}
}
