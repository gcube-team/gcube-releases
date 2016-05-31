package org.gcube.application.framework.accesslogger.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Template for each entry of the access log
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class TemplateModel {
	/*
	 * The date of the entry of the log
	 */
	private Date date;
	/*
	 * The username of the user
	 */
	private String user;
	/*
	 * The current VRE
	 */
	private String vre;
	/*
	 * The type of entry
	 */
	private String entryType;
	/*
	 * The message that will be logged
	 */
	private String message;
	
	private static final String delimiterCharacters = " -> ";
	private static final String separateCharacters = ", ";
	private static final String USER = "USER";
	private static final String VRE = "VRE";
	private static final String ENTRY_TYPE = "ENTRY_TYPE";
	private static final String MESSAGE = "MESSAGE";
	private static final String dateFormat = "yyyy-MM-dd kk:mm:ss";
	private static final String SEPARATOR = System.getProperty("line.separator");
	
	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(TemplateModel.class);
	
	/**
	 * Constructor
	 * 
	 * @param user The username of the user
	 * @param vre The VRE that is used
	 * @param entryType The type of entry
	 * @param message The message to be logged
	 * @param currentDate The current date
	 */
	public TemplateModel(String user, String vre, String entryType, String message, Date currentDate) {
		this.date = currentDate;
		this.user = user;
		this.vre = vre;
		this.message = message;
		this.entryType = entryType;
	}
	
	/**
	 * Creates an entry line for the log
	 * 
	 * @return The log entry as a String
	 */
	public String createEntryLine() {
		String entryLine = "";
		SimpleDateFormat dFormat = new SimpleDateFormat(dateFormat);
		try {
			String dateAndTime = (dFormat.format(this.date)).toString();
			entryLine += dateAndTime + separateCharacters + VRE + delimiterCharacters + this.vre + separateCharacters + USER + delimiterCharacters + this.user
				+ separateCharacters + ENTRY_TYPE + delimiterCharacters + this.entryType + separateCharacters + MESSAGE + delimiterCharacters + this.message + SEPARATOR;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		logger.debug("A new entry line has been created. The entry is: ");
		logger.debug(entryLine);
		return entryLine;
	}
	
}
