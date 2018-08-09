package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc;

public class Tags {
	
	//*********** COMMON
	
	public static final String sort="sort";
	public static final String dir="dir";
	public static final String ASC="ASC";
	public static final String START="start";
	public static final String OFFSET="offset";
	public static final String LIMIT="limit";
	
	
	
	
	public static final String JSONUTF8="application/json; charset=utf-8";
	public static final String IMAGE_JPEG="image/jpeg";
	public static final String IMAGE_GIF="image/gif";
	public static final String IMAGE_PNG="image/png";
	public static final String TOTAL_COUNT="totalcount";
	public static final String DATA="data";
	public static final String EMPTY_JSON="{\""+DATA+"\":[],\""+TOTAL_COUNT+"\":0}";
	
	
	//************* Session
	
	public static final String SPECIES_SEARCH_FILTER="SPECIES_SEARCH_FILTER";
	
	//************* HTTP PARAMETERS
	
	public static final String RESOURCE_TYPE="resource_type";
	public static final String PIC_NAME="PIC_NAME";
	
	//************ Servlets
	
	public static final String phylogenyServlet="PhylogenyServlet";
	public static final String resourceServlet="ResourceServlet";
	public static final String speciesServlet="SpeciesServlet";
	public static final String imageServlet="ImageServlet";
	public static final String mapServlet="MapServlet";
	
	
	//************ RPC Services
	public static final String localService="AquaMapsSpeciesViewLocalImpl";
	
	
	//*********** Config
	
	public static final String characteristicFilter="characteristics";
	public static final String nameFilter="names";
	public static final String codeFilter="codes";

	
	//*********** Response messages
	
	public static final String ERROR_MESSAGE="ERROR_MESSAGE";
	public static final String RETRIEVED_MAPS="RETRIEVED_MAPS";
}