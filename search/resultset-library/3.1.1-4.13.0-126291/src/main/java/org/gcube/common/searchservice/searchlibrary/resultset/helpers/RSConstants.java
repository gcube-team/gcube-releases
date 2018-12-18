package org.gcube.common.searchservice.searchlibrary.resultset.helpers;

import java.util.regex.Pattern;
import java.util.UUID;

/**
 * This class holds some constant values that are used for the creation and usage of a {@link org.gcube.common.searchservice.searchlibrary.resultset.ResultSet}
 * 
 * @author UoA
 */
public class RSConstants {
	/**
	 * Enumeration used to defoine type of resource to use
	 *
	 */
	public static enum RESOURCETYPE {WSRF,WS};
	/**
	 * Enumeration used to specify demands on result production
	 */
	public static enum CONTROLFLOW {MORE,STOP,TIMEOUT};
	/**
	 * Enumeration used to specify demands on result consumption
	 */
	public static enum TRANSPORTTYPE {SOAP,ATTACH,SOCKET};
	/**
	 * Used to serialize updates to head part
	 */
	public static final Object lockHead=new Object(); 
	/**
	 * Used to notify about new part creation
	 */
	public static final Object sleepOnIt=new Object();
	/**
	 * Used to notify about data flow new data request
	 */
	public static final Object controlFlowOnIt=new Object();
	/**
	 * The maximum amount of time a Thread will block on a wait call 
	 */
	public static final int sleepTime=250;
	/**
	 * The maximum amount of time a Thread will wait for the next part to be produced unless otherwise specified
	 */
	public static final int sleepMax=300000;
	/**
	 * The maximum amount of time a Thread will wait for the next part to be produced unless otherwise specified
	 */
//	public static final int sleepMax=30000;
	
	/**
	 * The maximum file size created during content spliting
	 */
	//public static int partSize=1048576;
	public static int partSize=2097152;
	/**
	 * The the block size to use for byte reading
	 */
	public static int blockSize=4096;
	/**
	 * The block size used to transport over socket streaming
	 */
	public static int transportBlockSize=8192;
	/**
	 * The base directory where all the parts are kept 
	 */
	public static final String baseDirectory="/tmp/resultset/";
	/**
	 * Flag to indicate a content part
	 */
	public static final short CONTENT=0;
	/**
	 * Flag to indicate a header part
	 */
	public static final short HEADER=1;
	/**
	 * Flag to indicate a paged content part
	 */
	public static final short PAGEDCONTENT=2;
	/**
	 * File extention for all public content parts
	 */
	public static final String cextention=".rs";
	/**
	 * File extention for all public paged content parts
	 */
	public static final String pextention=".prs";
	/**
	 * File extention for flow control flag
	 */
	public static final String dfextention=".df";
	/**
	 * File extention for all tmp part
	 */
	public static final String textention=".tmp";
	/**
	 * File extention for all public header parts
	 */
	public static final String hextention=".hrs";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String ResultSetTag="ResultSet";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String HeadTag="Head";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String PropertiesTag="Properties";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String PropertyTag="Property";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String PropertyAttributeNameTag="Name";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String isHead="isHead";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String localFileName="localFileName";
	/**
	 * Tag used to signfy controled creation
	 */
	public static final String dataFlow="dataFlow";
	/**
	 * Tag used to signfy forward creation
	 */
	public static final String forward="forward";
	/**
	 * The time leasing tag
	 */
	public static final String expireDate="expireDate";
	/**
	 * The public key tag
	 */
	public static final String pKey="pKey";
	/**
	 * The encryption key tag
	 */
	public static final String encKey="encKey";
	/**
	 * Tag used to signfy forward creation
	 */
	public static final String access="access";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String PartsTag="Parts";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String PartTag="Part";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String CustomPropertiesTag="CustomProperties";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String BodyTag="Body";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String RecordsTag="Records";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String RecordTag="RSRecord";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String nextLink="next";
	/**
	 * Tag used in the xml representation of a part
	 */
	public static final String previousLink="previous";
	/**
	 * Generates a unigue string
	 * 
	 * @return The unique string
	 */
	public static String nextUUID(){
		return UUID.randomUUID().toString();
	}
	/**
	 * The pattern to seperate results
	 */
	public static final Pattern respat =Pattern.compile("<"+RSConstants.RecordTag);

}
