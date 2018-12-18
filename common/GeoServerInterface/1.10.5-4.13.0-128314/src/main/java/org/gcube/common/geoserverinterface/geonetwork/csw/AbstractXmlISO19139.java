package org.gcube.common.geoserverinterface.geonetwork.csw;

public abstract class AbstractXmlISO19139 {
	
	private String tagMD_Metedata = "MD_Metadata";
	private String tagFileIdentifier = "fileIdentifier"; 
	private String tagCharacterString = "CharacterString";
	private String tagLanguage = "language";
	private String tagCharacterSet = "characterSet";
	private String tagMD_CharacterSetCode ="MD_CharacterSetCode";
	private String valueAttrMD_CharacterSetCodeCodeList ="./resources/codeList.xml#MD_CharacterSetCode";
	private String valueAttrMD_CharacterSetCodeCodeListLanguage = "http://www.isotc211.org/2005/resources/codeList.xml#MD_CharacterSetCode";
	private String valueAttrMD_CharacterSetCodeCodeListValue = "utf8";
	private String attrCodeList ="codeList";
	private String attrCodeListValue = "codeListValue";
	private String tagHierarchyLevel = "hierarchyLevel"; 
	private String tagMD_ScopeCode = "MD_ScopeCode";
	private String valueAttrMD_ScopeCodeCodeList = "./resources/codeList.xml#MD_ScopeCode";
	private String valueAttrMD_ScopeCodeCodeListValue = "dataset";
	private String tagdDateStamp = "dateStamp";
	private String tagDateTime = "DateTime";
	private String tagMetadataStandardName = "metadataStandardName";
	private String tagMetadataStandardVersion = "metadataStandardVersion";
	
	private String tagIdentificationInfo = "identificationInfo";
	private String tagMD_DataIdentification = "MD_DataIdentification";
	private String tagCitation = "citation";
	private String tagCI_Citation = "CI_Citation";
	private String tagMD_ProgressCode = "MD_ProgressCode";
	private String valueAttrMD_ProgressCodeCodeList = "./resources/codeList.xml#MD_ProgressCode";
	private String valueAttrMD_ProgressCodeCodeListValue = "completed";

	private String tagTitle = "title";
	private String tagDate = "date";
	private String tagCI_Date = "CI_Date";
	private String tagDateType = "dateType";
	private String tagCI_DateTypeCode = "CI_DateTypeCode";
	private String valueAttrCI_DateTypeCodeCodeList = "./resources/codeList.xml#CI_DateTypeCode";
	private String valueAttrCI_DateTypeCodeCodeListValue = "revision";
	
	private String tagAbstract = "abstract";
	
	private String tagStatus = "status";
	private String tagTopicCategory = "topicCategory";
	private String tagMD_TopicCategoryCode = "MD_TopicCategoryCode";
	private String tagExtent = "extent";
	private String tagEX_Extent = "EX_Extent";
	private String tagGeographicElement = "geographicElement";
	private String tagEX_GeographicBoundingBox = "EX_GeographicBoundingBox";
	
	private String tagWestBoundLongitude = "westBoundLongitude";
	private String tagDecimal = "Decimal";
	private String tagXmin = "xmin";
	private String tagXmax = "xmax";
	private String tagEastBoundLongitude = "eastBoundLongitude";
	private String tagSouthBoundLatitude = "southBoundLatitude";
	private String tagNorthBoundLatitude = "northBoundLatitude";
	
	private String tagDistributionInfo = "distributionInfo";
	private String tagMD_Distribution = "MD_Distribution";
	private String tagDistributionFormat = "distributionFormat";
	private String tagMD_Format = "MD_Format";
	
	private String tagName = "name";
	private String tagVersion = "version";
	
	private String tagTransferOptions = "transferOptions";
	private String tagMD_DigitalTransferOptions = "MD_DigitalTransferOptions";
	private String tagOnLine = "onLine";
	private String tagCI_OnlineResource = "CI_OnlineResource";
	private String tagLinkage = "linkage";
	private String tagURL = "URL";
	private String tagProtocol = "protocol";
	private String tagDescription = "description";
	
	private String tagDataQualityInfo = "dataQualityInfo";
	private String tagDQ_DataQuality = "DQ_DataQuality";
	
	private String tagScope = "scope";
	private String tagDQ_Scope = "DQ_Scope";
	private String tagLevel = "level";

	private String tagLineage = "lineage";
	private String tagLI_Lineage = "LI_Lineage";
	private String tagStatement = "statement";
	
	
	public String getTagMD_Metedata() {
		return tagMD_Metedata;
	}
	public String getTagFileIdentifier() {
		return tagFileIdentifier;
	}
	public String getTagCharacterString() {
		return tagCharacterString;
	}
	public String getTagLanguage() {
		return tagLanguage;
	}
	public String getTagCharacterSet() {
		return tagCharacterSet;
	}
	public String getTagMD_CharacterSetCode() {
		return tagMD_CharacterSetCode;
	}
	public String getValueAttrMD_CharacterSetCodeCodeList() {
		return valueAttrMD_CharacterSetCodeCodeList;
	}
	public String getValueAttrMD_CharacterSetCodeCodeListValue() {
		return valueAttrMD_CharacterSetCodeCodeListValue;
	}
	public String getAttrCodeList() {
		return attrCodeList;
	}
	public String getAttrCodeListValue() {
		return attrCodeListValue;
	}
	public String getTagHierarchyLevel() {
		return tagHierarchyLevel;
	}
	public String getTagMD_ScopeCode() {
		return tagMD_ScopeCode;
	}
	public String getValueAttrMD_ScopeCodeCodeList() {
		return valueAttrMD_ScopeCodeCodeList;
	}
	public String getValueAttrMD_ScopeCodeCodeListValue() {
		return valueAttrMD_ScopeCodeCodeListValue;
	}
	public String getTagdDateStamp() {
		return tagdDateStamp;
	}
	public String getTagDateTime() {
		return tagDateTime;
	}
	public String getTagMetadataStandardName() {
		return tagMetadataStandardName;
	}
	public String getTagMetadataStandardVersion() {
		return tagMetadataStandardVersion;
	}
	public String getTagIdentificationInfo() {
		return tagIdentificationInfo;
	}
	public String getTagMD_DataIdentification() {
		return tagMD_DataIdentification;
	}
	public String getTagCitation() {
		return tagCitation;
	}
	public String getTagCI_Citation() {
		return tagCI_Citation;
	}
	public String getTagTitle() {
		return tagTitle;
	}
	public String getTagDate() {
		return tagDate;
	}
	public String getTagCI_Date() {
		return tagCI_Date;
	}
	public String getTagAbstract() {
		return tagAbstract;
	}
	public String getTagStatus() {
		return tagStatus;
	}
	public String getTagTopicCategory() {
		return tagTopicCategory;
	}
	public String getTagMD_TopicCategoryCode() {
		return tagMD_TopicCategoryCode;
	}
	public String getTagExtent() {
		return tagExtent;
	}
	public String getTagGeographicElement() {
		return tagGeographicElement;
	}
	public String getTagEX_GeographicBoundingBox() {
		return tagEX_GeographicBoundingBox;
	}
	public String getTagWestBoundLongitude() {
		return tagWestBoundLongitude;
	}
	public String getTagDecimal() {
		return tagDecimal;
	}
	public String getTagEastBoundLongitude() {
		return tagEastBoundLongitude;
	}
	public String getTagSouthBoundLatitude() {
		return tagSouthBoundLatitude;
	}
	public String getTagNorthBoundLatitude() {
		return tagNorthBoundLatitude;
	}
	public String getTagDistributionInfo() {
		return tagDistributionInfo;
	}
	public String getTagMD_Distribution() {
		return tagMD_Distribution;
	}
	public String getTagDistributionFormat() {
		return tagDistributionFormat;
	}
	public String getTagMD_Format() {
		return tagMD_Format;
	}
	public String getTagName() {
		return tagName;
	}
	public String getTagVersion() {
		return tagVersion;
	}
	public String getTagTransferOptions() {
		return tagTransferOptions;
	}
	public String getTagMD_DigitalTransferOptions() {
		return tagMD_DigitalTransferOptions;
	}
	public String getTagOnLine() {
		return tagOnLine;
	}
	public String getTagCI_OnlineResource() {
		return tagCI_OnlineResource;
	}
	public String getTagLinkage() {
		return tagLinkage;
	}
	public String getTagURL() {
		return tagURL;
	}
	public String getTagProtocol() {
		return tagProtocol;
	}
	public String getTagDescription() {
		return tagDescription;
	}
	public String getTagDataQualityInfo() {
		return tagDataQualityInfo;
	}
	public String getTagDQ_DataQuality() {
		return tagDQ_DataQuality;
	}
	public String getTagScope() {
		return tagScope;
	}
	public String getTagDQ_Scope() {
		return tagDQ_Scope;
	}
	public String getTagLevel() {
		return tagLevel;
	}
	public String getTagLineage() {
		return tagLineage;
	}
	public String getTagLI_Lineage() {
		return tagLI_Lineage;
	}
	public String getTagStatement() {
		return tagStatement;
	}
	public String getTagDateType() {
		return tagDateType;
	}
	public String getTagCI_DateTypeCode() {
		return tagCI_DateTypeCode;
	}
	public String getValueAttrCI_DateTypeCodeCodeList() {
		return valueAttrCI_DateTypeCodeCodeList;
	}
	public String getValueAttrCI_DateTypeCodeCodeListValue() {
		return valueAttrCI_DateTypeCodeCodeListValue;
	}
	public String getTagMD_ProgressCode() {
		return tagMD_ProgressCode;
	}
	public String getValueAttrMD_ProgressCodeCodeList() {
		return valueAttrMD_ProgressCodeCodeList;
	}
	public String getValueAttrMD_ProgressCodeCodeListValue() {
		return valueAttrMD_ProgressCodeCodeListValue;
	}
	public String getTagEX_Extent() {
		return tagEX_Extent;
	}
	public String getTagXmin() {
		return tagXmin;
	}
	public String getTagXmax() {
		return tagXmax;
	}
	public String getValueAttrMD_CharacterSetCodeCodeListLanguage() {
		return valueAttrMD_CharacterSetCodeCodeListLanguage;
	}
}
