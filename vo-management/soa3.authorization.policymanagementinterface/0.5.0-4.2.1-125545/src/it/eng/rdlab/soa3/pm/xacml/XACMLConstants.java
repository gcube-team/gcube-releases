package it.eng.rdlab.soa3.pm.xacml;

public interface XACMLConstants 
{
	public String 	TIME_ONE_ONLY = "urn:oasis:names:tc:xacml:1.0:function:time-one-and-only",
					DATE_ONE_ONLY = "urn:oasis:names:tc:xacml:1.0:function:date-one-and-only",
					DATE_TIME_ONE_ONLY = "urn:oasis:names:tc:xacml:1.0:function:dateTime-one-and-only",
					TIME_DATA_TYPE = "http://www.w3.org/2001/XMLSchema#time",
					DATE_DATA_TYPE = "http://www.w3.org/2001/XMLSchema#date",
					DATE_TIME_DATA_TYPE = "http://www.w3.org/2001/XMLSchema#dateTime",
					TIME_CURRENT = "urn:oasis:names:tc:xacml:1.0:environment:current-time",
					DATE_CURRENT =	"urn:oasis:names:tc:xacml:1.0:environment:current-date",
					DATE_TIME_CURRENT =	"urn:oasis:names:tc:xacml:1.0:environment:current-dateTime",
					TIME_FORMAT = "HH:mm:ss",
					DATE_FORMAT = "yyyy-MM-dd",
					DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss",
					FUNCTION_AND = "urn:oasis:names:tc:xacml:1.0:function:and",
					FUNCTION_OR = "urn:oasis:names:tc:xacml:1.0:function:or";
}
