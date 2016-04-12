package org.gcube.data;

import static org.gcube.data.trees.data.Nodes.*;

import java.net.URI;
import java.util.Calendar;
import java.util.Locale;

import org.gcube.data.trees.data.Tree;

public class TestFixture {

	public static String STOREID = "teststore";
	
	public static String TEST_METADATA_SCHEMA_NAME="dc";
	public static URI TEST_METADATA_SCHEMA_URI=URI.create("http://dc");
	public static Locale TEST_LANGUAGE=Locale.ENGLISH;
	public static String TEST_ATTR1="10";
	public static String TEST_ATTR2="hello";
	public static boolean TEST_ATTR3=true;
	public static final String SOMETYPE = "sometype";
	public static final Calendar NOW = Calendar.getInstance();
	
	final static public String ATTR1 = "a1";
	final static public String ATTR2 = "a2";
	final static public String ATTR3 = "a3";

	final static public String METADATA="isDescribedBy";
	final static public String ANNOTATION = "isAnnotatedBy";
	final static public String ALTERNATIVE ="hasAlternative";
	final static public String PART="isPartOf";
	final static public String BYTESTREAM="bytestream";
	final static public String BYTESTREAM_URI="url";
	final static public String NAME="name";
	final static public String LAST_UPDATE="lastUpdateTime";
	final static public String MIME_TYPE ="mimeType";
	final static public String LENGTH = "length";
	final static public String CREATION_TIME = "creationTime";
	final static public String LANGUAGE = "language";
	final static public String SCHEMA_URI="schemaURI";
	final static public String SCHEMA_NAME="schemaName";
	final static public String TYPE="type";
	final static public String PREVIOUS = "previous";
	final static public String ORDER = "order";
	final static public String PROPERTY = "property";
	final static public String PROPERTY_TYPE="type";
	final static public String PROPERTY_VALUE="value";
	
	public static Tree TEST_TREE = 
			attr(t(
					e(CREATION_TIME,Calendar.getInstance()),
					e(LAST_UPDATE,Calendar.getInstance()),
					e(TYPE,SOMETYPE),
					e(BYTESTREAM_URI,"sms://node19.d.d4science.research-infrastructures.eu:8080/wsrf/services/gcube/contentmanagement/storagelayer/storagemanagementservice/porttype1?oid=e83a4ed0-2dff-11df-a81e-c20ddc2e724e"),
					e(METADATA,n(
								e(CREATION_TIME,NOW),
								e(LAST_UPDATE,NOW),
								e(SCHEMA_NAME,TEST_METADATA_SCHEMA_NAME),
								e(SCHEMA_URI,TEST_METADATA_SCHEMA_URI), 
								e(LANGUAGE,TEST_LANGUAGE.getLanguage()), 
								e(BYTESTREAM,"<a>hello world</a>"))),
							
					e(METADATA,n(
								e(CREATION_TIME,NOW),
								e(LAST_UPDATE,NOW),
								e(SCHEMA_NAME,TEST_METADATA_SCHEMA_NAME),
								e(SCHEMA_URI,TEST_METADATA_SCHEMA_URI), 
								e(LANGUAGE,TEST_LANGUAGE.getLanguage()), 
								e(BYTESTREAM,"<a>hi world</a>"))),
							
					e(METADATA,n(
								e(CREATION_TIME,NOW),
								e(LAST_UPDATE,NOW),
								e(SCHEMA_NAME,"marc"),
								e(SCHEMA_URI,"http://marc"), 
								e(LANGUAGE,"it"), 
								e(BYTESTREAM,"<a>ciao mondo</a>"))),
					
					e(ANNOTATION,n(
								e(CREATION_TIME,NOW),
								e(LAST_UPDATE,NOW),
								e(SCHEMA_NAME,"myschema"),
								e(SCHEMA_URI,"http://myschema"), 
								e(LANGUAGE,TEST_LANGUAGE.getLanguage()), 
								e(BYTESTREAM,"<a>some annotation</a>"))),
					e(ANNOTATION,n(
								e(CREATION_TIME,NOW),
								e(LAST_UPDATE,NOW),
								e(SCHEMA_NAME,"myschema"),
								e(SCHEMA_URI,"http://myschema"), 
								e(LANGUAGE,TEST_LANGUAGE.getLanguage()), 
								e(BYTESTREAM,"<a>some other annotation</a>"),
								e(PREVIOUS,"15"))),
					e(ANNOTATION,n(
								e(CREATION_TIME,NOW),
								e(LAST_UPDATE,NOW),
								e(SCHEMA_NAME,"myschema"),
								e(SCHEMA_URI,"http://myschema"), 
								e(LANGUAGE,TEST_LANGUAGE.getLanguage()), 
								e(BYTESTREAM,"<a>some other annotation</a>")))
		
		),a(ATTR1,TEST_ATTR1),a(ATTR2,TEST_ATTR2),a(ATTR3,TEST_ATTR3));
}
