package gr.cite.geoanalytics.context;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {
	
	private static Logger log = LoggerFactory.getLogger(ApplicationConfig.class);
	
	private static final long serverShutdownDelayDefault = 5;
	private static final TimeUnit serverShutdownDelayUnitDefault = TimeUnit.MINUTES;
	
	private long serverShutdownDelay = serverShutdownDelayDefault;
	private TimeUnit serverShutdownDelayUnit = serverShutdownDelayUnitDefault;
	
	private static final int maxUserSearchTermsDefault = 15;
	private static final int maxCustomerSearchTermsDefault = 15;
	private static final int maxTaxonomySearchTermsDefault = 15;
	private static final int maxShapeSearchTermsDefault = 15;
	private static final int maxDocumentSearchTermsDefault = 15;
	private static final String defaultLanguageDefault = "el";
	
	private int maxUserSearchTerms = maxUserSearchTermsDefault;
	private int maxCustomerSearchTerms = maxCustomerSearchTermsDefault;
	private int maxTaxonomySearchTerms = maxTaxonomySearchTermsDefault;
	private int maxShapeSearchTerms = maxShapeSearchTermsDefault;
	private int maxDocumentSearchTerms = maxDocumentSearchTermsDefault;
	private String defaultLanguage = defaultLanguageDefault;
	
	private SmtpConfig smtpConfig = null;
	
	public long getServerShutdownDelay() {
		return serverShutdownDelay;
	}
	public void setServerShutdownDelay(long serverShutdownDelay) {
		this.serverShutdownDelay = serverShutdownDelay;
	}
	public TimeUnit getServerShutdownDelayUnit() {
		return serverShutdownDelayUnit;
	}
	public void setServerShutdownDelayUnit(TimeUnit serverShutdownDelayUnit) {
		this.serverShutdownDelayUnit = serverShutdownDelayUnit;
	}
	public int getMaxUserSearchTerms() {
		return maxUserSearchTerms;
	}
	
	public void setMaxUserSearchTerms(String maxUserSearchTermsStr) {
		if(maxUserSearchTermsStr == null) {
			log.trace("Using default maximum number of user search terms: " + maxUserSearchTermsDefault);
			maxUserSearchTerms = maxUserSearchTermsDefault;
		}
		else {
			if(maxUserSearchTermsStr.equalsIgnoreCase("unlimited")) maxUserSearchTerms = Integer.MAX_VALUE;
			else maxUserSearchTerms = Integer.parseInt(maxUserSearchTermsStr);
			log.trace("Using maximum number of user search terms: " + maxUserSearchTermsStr);
		}	
	}
	
	public int getMaxCustomerSearchTerms() {
		return maxCustomerSearchTerms;
	}
	
	public void setMaxCustomerSearchTerms(String maxCustomerSearchTermsStr) {
		if(maxCustomerSearchTermsStr == null) {
			log.trace("Using default maximum number of customer search terms: " + maxCustomerSearchTermsDefault);
			maxCustomerSearchTerms = maxCustomerSearchTermsDefault;
		}
		else {
			if(maxCustomerSearchTermsStr.equalsIgnoreCase("unlimited")) maxCustomerSearchTerms = Integer.MAX_VALUE;
			else maxCustomerSearchTerms = Integer.parseInt(maxCustomerSearchTermsStr);
			log.trace("Using maximum number of customer search terms: " + maxCustomerSearchTermsStr);
		}
	}
	
	public int getMaxTaxonomySearchTerms() {
		return maxTaxonomySearchTerms;
	}
	
	public void setMaxTaxonomySearchTerms(String maxTaxonomySearchTermsStr) {
		if(maxTaxonomySearchTermsStr == null) {
			log.trace("Using default maximum number of taxonomy search terms: " + maxTaxonomySearchTermsDefault);
			maxTaxonomySearchTerms = maxTaxonomySearchTermsDefault;
		}
		else {
			if(maxTaxonomySearchTermsStr.equalsIgnoreCase("unlimited")) maxTaxonomySearchTerms = Integer.MAX_VALUE;
			else maxTaxonomySearchTerms = Integer.parseInt(maxTaxonomySearchTermsStr);
			log.trace("Using maximum number of taxonomy search terms: " + maxTaxonomySearchTermsStr);
		}
	}
	
	public int getMaxShapeSearchTerms() {
		return maxShapeSearchTerms;
	}
	
	public void setMaxShapeSearchTerms(String maxShapeSearchTermsStr) {
		if(maxShapeSearchTermsStr == null){
			log.trace("Using default maximum number of shape search terms: " + maxShapeSearchTermsDefault);
			maxShapeSearchTerms = maxShapeSearchTermsDefault;
		}
		else {
			if(maxShapeSearchTermsStr.equalsIgnoreCase("unlimited")) maxShapeSearchTerms = Integer.MAX_VALUE;
			else maxShapeSearchTerms = Integer.parseInt(maxShapeSearchTermsStr);
			log.trace("Using maximum number of shape search terms: " + maxShapeSearchTermsStr);
		}
	}
	
	public int getMaxDocumentSearchTerms() {
		return maxDocumentSearchTerms;
	}
	public void setMaxDocumentSearchTerms(String maxDocumentSearchTermsStr) {
		if(maxDocumentSearchTermsStr == null){
			log.trace("Using default maximum number of document search terms: " + maxDocumentSearchTermsDefault);
			maxDocumentSearchTerms = maxDocumentSearchTermsDefault;
		}
		else {
			if(maxDocumentSearchTermsStr.equalsIgnoreCase("unlimited")) maxDocumentSearchTerms = Integer.MAX_VALUE;
			else maxDocumentSearchTerms = Integer.parseInt(maxDocumentSearchTermsStr);
			log.trace("Using maximum number of document search terms: " + maxDocumentSearchTermsStr);
		}
	}
	
	public String getDefaultLanguage() {
		return defaultLanguage;
	}
	public void setDefaultLanguage(String defaultLanguage) {
		if(defaultLanguage == null) {
			log.trace("Using default value for default language: " + defaultLanguageDefault);
			this.defaultLanguage = defaultLanguageDefault;
		}
		else {
			this.defaultLanguage = defaultLanguage;
			log.trace("Using default language: " + defaultLanguage);
		}
	}
	
	public SmtpConfig getSmtpConfig() {
		return smtpConfig;
	}
	public void setSmtpConfig(SmtpConfig smtpConfig) {
		this.smtpConfig = smtpConfig;
	}
	@Override
	public String toString() {
		return "ApplicationConfig [serverShutdownDelay=" + serverShutdownDelay + ", serverShutdownDelayUnit="
				+ serverShutdownDelayUnit + ", maxUserSearchTerms=" + maxUserSearchTerms + ", maxCustomerSearchTerms="
				+ maxCustomerSearchTerms + ", maxTaxonomySearchTerms=" + maxTaxonomySearchTerms
				+ ", maxShapeSearchTerms=" + maxShapeSearchTerms + ", maxDocumentSearchTerms=" + maxDocumentSearchTerms
				+ ", defaultLanguage=" + defaultLanguage + ", smtpConfig=" + smtpConfig + "]";
	}
	
}
