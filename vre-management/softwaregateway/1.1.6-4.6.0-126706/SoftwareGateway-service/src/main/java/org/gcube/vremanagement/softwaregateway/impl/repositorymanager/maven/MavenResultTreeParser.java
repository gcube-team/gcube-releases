package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.ArtifactConstants;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.ArtifactCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.ArtifactTreeNode;

/**
 * @author Luca Frosini (ISTI-CNR)
 */
public class MavenResultTreeParser {
	
	/**
	 * More on this level token
	 */
	public static final String MORE_ON_THIS_LEVEL = "+-";
	/**
	 * More on this level toker (regular expression)
	 */
	public static final String MORE_ON_THIS_LEVEL_REGEX = "\\+-";
	/**
	 * Last in the level token
	 */
	public static final String LAST_OF_THIS_LEVEL = "\\-";
	/**
	 * Continue Token
	 */
	public static final String CONTINUE = "|";
	/**
	 * Second level indent
	 */
	public static final String INDENT = "   ";
	
	/**
	 * Class logger. 
	 */
	private static final GCUBELog logger = new GCUBELog(MavenResultTreeParser.class);
	
	private File resultTree; 
	
	private ArtifactTreeNode root;
	
	private boolean filter = false;
	private String scopeFilter;
	private boolean filtered = false;
	
	private static final String RESOLVED_DEPENDECIES = "ResolvedDependencies";
	
	/**
	 * Constructor
	 * @param treeFile file to parse
	 * @throws Exception if the file does not exist
	 */
	public MavenResultTreeParser(File treeFile) throws Exception {
		if(treeFile.exists()){
			this.resultTree = treeFile;
		}else{
			Exception e = new Exception("Textual file with tree specification does not exist");
			logger.error(e);
			throw e;
		}
	}
	
	/**
	 * Build the tree parsing file
	 * @throws Exception if parsing tree fails
	 */
	private void buildTree() throws Exception {
		logger.debug("Parsing " + resultTree.getAbsolutePath() + " file");
		String nextStr = null;
				
		BufferedReader filebuf = new BufferedReader(new FileReader(resultTree));
		
		nextStr = filebuf.readLine(); // Read the first line from file
		System.out.println("firstLine: "+nextStr);
		ArtifactTreeNode node =null;
		
		// Jump to second line because the first one is a fake artifact created for dependency resolution
		if(nextStr!=null){
			String firstStr = "mygroup:myartifact:" + ArtifactConstants.DEFAULT_PACKAGING + ":" + (filter?scopeFilter:"GHN") + ":1.000.00:runtime";
			ArtifactCoordinates artifact = getArtifactInfo(firstStr);
			
			root = new ArtifactTreeNode(null,artifact,0);
			logger.debug(root.toString());
		}else{
			Exception e = new Exception("File to parse differs from template");
			logger.error(e);
			throw e;
		}
		
		node = root;
//		nextStr = filebuf.readLine();
		nextStr="+- "+nextStr+":runtime";
		while(nextStr!=null){
			node = parseLines(node,nextStr);
			nextStr = filebuf.readLine();
		}
		
		filebuf.close();
		
	}

	
	/**
	 * Build the tree parsing file
	 * @param scope scope to filter
	 * @throws Exception if parsing tree fails
	 * @return xml representation of tree filtered by scope
	 */
	public String getScopedDependecy(String scope) throws Exception {
		if(scope!=null){
			filter = true;
			this.scopeFilter = scope;
		}else{
			filter = false;
		}
		buildTree();
		return "";
	}
	
	private ArtifactTreeNode parseLines(ArtifactTreeNode node, String nextStr) throws Exception {
		logger.debug("Parsing line : " + nextStr);
		
		String token = ""; 
		if(nextStr.contains(LAST_OF_THIS_LEVEL)){
			token = LAST_OF_THIS_LEVEL + " ";
		}else if(nextStr.contains(MORE_ON_THIS_LEVEL)){
			token = MORE_ON_THIS_LEVEL_REGEX + " ";			
		}
		logger.debug("token =" + token);
		
		
		nextStr = nextStr.replace(CONTINUE, " ");
		int deep = 1;
		StringBuilder concatIndent = new StringBuilder();
		concatIndent.append(INDENT);
		while(nextStr.startsWith(concatIndent.toString())){
			++deep;
			concatIndent.append(INDENT);
		}
		logger.debug("deep = " + deep);
		
		
		String artifactCoordinates = nextStr.split(token)[1];
		
		filtered = false;
		ArtifactCoordinates artifact = getArtifactInfo(artifactCoordinates);
		String scopesToFilter="";
		if(scopeFilter.equalsIgnoreCase("compile")){
			scopesToFilter="test, provided";
		}else if (scopeFilter.equalsIgnoreCase("runtime")){
			scopesToFilter="test, provided, compile";
		}else{
			scopesToFilter="test";
		}
		
		
		if(filter && artifact.getScope()!=null && scopesToFilter.contains(artifact.getScope())){
			filtered = true;
		}
		
		ArtifactTreeNode parent = node;
		for(int auxDeep=node.getDeep();auxDeep>=deep;--auxDeep){
			parent = parent.getParent();
		}
		
		ArtifactTreeNode thisNode = new ArtifactTreeNode(parent,artifact,deep);
		logger.debug(thisNode.toString());
		if(!filtered){
			parent.addSon(thisNode);
		}
		
		return thisNode;
	}
	
	/**
	 * @param artifactCoordinates string representing artifact ex. Portal.ThumbnailService.1.00.00:Thumbnailer-service:tar.gz:GHN:1.00.00:runtime
	 * @return ArtifactCoordinates class
	 * @throws Exception if Artifact coordinates differs from template
	 */
	public static ArtifactCoordinates getArtifactInfo(String artifactCoordinates) throws Exception {
		
		logger.debug("Creating Artifact with coordinates = " + artifactCoordinates);
		
		//ex: Portal.ThumbnailService.1.00.00:Thumbnailer-service:tar.gz:GHN:1.00.00:runtime
		String[] coordinates = artifactCoordinates.split(":");
		
//		if(	coordinates.length<6 && coordinates.length!=5 &&
//			!coordinates[2].equals(ArtifactConstants.DEFAULT_PACKAGING) &&  
//			!coordinates[5].equals(ArtifactConstants.DEFAULT_DEPENDENCY_SCOPE)) {
//			
//			Exception e = new Exception("Artifact coordinates differs from template");
//			logger.error(e);
//			throw e;
//		}
//		
//		String groupsID = coordinates[0];
//		String artifactID = coordinates[1];
//		String classifier = null;
//		String artifactVersion = coordinates[4];
//		if(coordinates.length==6){
//			classifier = coordinates[3];
//			artifactVersion = coordinates[4];
//		} else {
//			artifactVersion = coordinates[3];
//		}
		
		String groupId=coordinates[0];
		String artifactId=coordinates[1];
		String classifier=coordinates[2];
		String artifactVersion="";
		String scope="";
		if((coordinates[3].matches("^(\\*|\\d+(\\.\\d+){0,2}(\\.\\*)?)(\\.(\\S+)){0,1}$"))){
			artifactVersion=coordinates[3];
			if(coordinates.length>4)
				scope=coordinates[4];
			else{
				scope="runtime";
			}

		}else{
			artifactVersion=coordinates[4];
			artifactId=artifactId+"#"+coordinates[3];
			if(coordinates.length>5)
				scope=coordinates[5];
			else{
				scope="runtime";
			}

		}		
		ArtifactCoordinates ret = new ArtifactCoordinates(groupId,artifactId,artifactVersion, scope, classifier);
		if(classifier!=null){
			ret.setClassifier(classifier);
		}
		return ret;
	}
	
	/**
	 * @return String representation of the tree
	 */
	public String printTree(){
		return root.printTree();
	}
	
	/**
	 * @return List of dependency. Each element is a piece of XML
	 * @throws Exception if fails
	 */
	public String[] listDependencyArray() throws Exception{
		List<String> dep = new ArrayList<String>();
		root.listDependency(dep);
		String[] ret = new String[dep.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = dep.get(i);
		}
		return ret;
	}
	
	/**
	 * @return List of dependency as single XML string
	 * @throws Exception if fails
	 */
	public String listDependency() throws Exception{
		List<String> dep = new ArrayList<String>();
		root.listDependency(dep);
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(RESOLVED_DEPENDECIES).append(">\n");
		for (int i = 0; i < dep.size(); i++) {
			sb.append(dep.get(i));
		}
		sb.append("</").append(RESOLVED_DEPENDECIES).append(">\n");
		return sb.toString();
	}
	
}
