package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.datatransformation.datatransformationlibrary.PropertiesManager;
import org.gcube.datatransformation.datatransformationlibrary.imanagers.IManager;
import org.gcube.datatransformation.datatransformationlibrary.model.ContentType;
import org.gcube.datatransformation.datatransformationlibrary.model.ExtTransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;
import org.gcube.datatransformation.datatransformationlibrary.model.Source;
import org.gcube.datatransformation.datatransformationlibrary.model.Target;
import org.gcube.datatransformation.datatransformationlibrary.model.TargetHandlerDesc;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationUnit;
import org.gcube.datatransformation.datatransformationlibrary.model.TransformationProgram;
import org.gcube.datatransformation.datatransformationlibrary.model.Transformer;
import org.gcube.datatransformation.datatransformationlibrary.model.XMLDefinitions;


/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Transformations� Graph is responsible to find transformationUnit units which can be used in order to perform a transformationUnit for an object, from its content type (source) to a target content type. The transformations� graph consists of nodes which correspond to content types and edges to transformationUnit units.
 * </p>
 * <p>
 * The selection of the transformationUnit units is based to the following rules. In the beginning the graph searches for transformationUnit units which have exact match of the source and target content types. If no one is available, the graph tries to search for a path in the graph from the source content type to the target content type and thus create a composite transformationUnit unit. If the graph does not manage to find a path, it searches for existing transformationUnit units or paths but for content types which do not have exact match i.e. the media type and the subtype identifier are always taken under consideration but for the parameters the rule is that if the parameters of the content type (source / target) denoted in the transformationUnit unit are contained and their values match with the object�s content type / the target of the transformationUnit respectively, then the transformationUnit unit is supposed to support the transformationUnit even if the source�s object / target content type contains more parameters.
 * </p>
 */
//TODO: Use read write locks but extremely carefully...
//http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/locks/ReentrantReadWriteLock.html
public class TransformationsGraphImpl implements TransformationsGraph {

	/**
	 * @author Dimitris Katris, NKUA
	 * <p>
	 * The consistency in which the graph is searched.
	 * </p>
	 */
	protected enum Consistency {
		EXACT, SUPPORT, GENERIC
	}
	
	private NodesCollection nodes = new NodesCollection();
	
	private IManager imanager;
	
	private static Logger log = LoggerFactory.getLogger(TransformationsGraphImpl.class);
	
	private Updater updater;
	
	/**
	 * Instantiates a new <tt>TransformationsGraphImpl</tt>
	 * 
	 * @param imanager The <tt>IManager</tt> from which the graph will read information.
	 */
	public TransformationsGraphImpl(IManager imanager){
		this.imanager=imanager;
		Updater updater = new Updater();
		this.updater=updater;
		updater.setTransformationsGraph(this);
		updater.start();
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph#destroy()
	 */
	synchronized public void destroy(){
		nodes.clear();
		updater.stopThread();
		updater.interrupt();
	}
	
	/**
	 * Updates the <tt>TransformationsGraphImpl</tt>
	 * 
	 * @throws Exception
	 */
	synchronized protected void update() throws Exception {
		NodesCollection newNodes = new NodesCollection();
		log.info("Creating Transformations Graph...");
		log.info("Getting available program IDs...");
		String[] programids;
		try {
			programids = imanager.getAvailableTransformationProgramIDs();
		} catch (Exception e) {
			log.error("Did not manage to get All Available Transformation Program IDs",e);
			throw new Exception("Did not manage to get All Transformation Available Program IDs");
		}
		
		if(programids==null || programids.length==0){
			log.warn("Could not find any information about transformationUnit programs.");
			return;
		}
		for(String pid: programids){
			try {
				//For not to overload IS... TODO: Maybe get All of them in one call or get them in groups...
				try {Thread.sleep(1000);} catch (Exception e) {}
				
				TransformationProgram transformationProgram;
				try {
					log.trace("Going to get transformationUnit program with id "+pid);
					transformationProgram = imanager.getTransformationProgram(pid);
				} catch (Exception e) {
					log.error("Could not get transformationUnit program with id "+pid+", continuing...",e);
					continue;
				}
				if(transformationProgram==null){
					log.warn("Could not find transformationUnit program with id "+pid+", continuing...");
					continue;
				}
				addTransformationProgramInGraph(transformationProgram, newNodes);
			} catch (Exception e) {//In order not to block from any unexpected error...
				log.error("Could not add transformationUnit program with id "+pid+" in the graph, continuing...");
			}
		}
		if(nodes!=null){
			NodesCollection oldNodes = nodes;
			oldNodes.clear();
			oldNodes=null;
		}
		//Currently doesn't really make any sense. Only by using R/W locks. 
		nodes=newNodes;
	}
	
	private void addTransformationProgramInGraph(TransformationProgram transformationProgram, NodesCollection newNodes){
		Transformer transformer = transformationProgram.getTransformer();
		if(transformer !=null && transformer.getGlobalProgramParams()!=null
				&& transformer.getGlobalProgramParams().size()>0){
			for(Parameter progParameter: transformer.getGlobalProgramParams()){
				if(progParameter!=null && !progParameter.isOptional() && 
						progParameter.getValue().equals(XMLDefinitions.VALUE_notset)){
					log.info("Transformation program "+transformationProgram.getId()+" has transformer with compalsory program parameter which is not set. Cannot be part of transformationUnit's graph");
					return;
				}
			}
		}
		ArrayList<TransformationUnit> supportedTransfs = transformationProgram.getTransformationUnits();
		nextTR: for(TransformationUnit transformationUnit: supportedTransfs){
			if(transformationUnit.getSources().size()!=1){
				log.info("Transfomration Units with more than one sources do not take part in transformationUnit's graph, TP: "+transformationProgram.getId()+", TR: "+transformationUnit.getId());
				continue nextTR;
			}
			if(transformationUnit.getProgramParameters()!=null && transformationUnit.getProgramParameters().size()>0){
				for(Parameter progParameter: transformationUnit.getProgramParameters()){
					if(progParameter!=null && !progParameter.isOptional() && 
							progParameter.getValue().equals(XMLDefinitions.VALUE_notset)){
						log.info("Transformation unit "+transformationUnit.getId()+" has compalsory program parameter which is not set. Cannot be part of transformationUnit's graph");
						continue nextTR;
					}
				}
			}
			
			log.debug("Importing in the graph, TP: "+transformationProgram.getId()+", TU: "+transformationUnit.getId());
			TNode snode,tnode;
			if(!newNodes.exists(transformationUnit.getSources().get(0).getContentType())){
				snode=newNodes.add(transformationUnit.getSources().get(0).getContentType());
			}else{
				snode=newNodes.get(transformationUnit.getSources().get(0).getContentType());
			}
			if(!newNodes.exists(transformationUnit.getTarget().getContentType())){
				tnode=newNodes.add(transformationUnit.getTarget().getContentType());
			}else{
				tnode=newNodes.get(transformationUnit.getTarget().getContentType());
			}
			snode.addEdge(new TEdge(transformationUnit, tnode));
		}
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph#findApplicableTransformationUnits(org.gcube.datatransformation.datatransformationlibrary.model.ContentType, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, boolean)
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @param targetContentType The <tt>ContentType</tt> of the target <tt>DataElement</tt>.
	 * @param createAndPublishCompositeTP If true then a new composite <tt>TransformationProgram</tt> is created and published if no available <tt>TransformationProgram</tt> exists.
	 * @return The available <tt>TransformationUnits</tt>.
	 */
	synchronized public ArrayList <TransformationUnit> findApplicableTransformationUnits(ContentType sourceContentType, ContentType targetContentType, boolean createAndPublishCompositeTP){
		log.info("Trying to find TPs with exact match of the Content Types");
		ArrayList<TransformationUnit> transformationUnits = getTransformationUnitsByConsistencyLevel(sourceContentType, targetContentType, Consistency.EXACT, createAndPublishCompositeTP);
		if(transformationUnits!=null && transformationUnits.size()>0)
			return transformationUnits;
		log.info("Trying to find TPs with supporting match of the Content Types");
		transformationUnits = getTransformationUnitsByConsistencyLevel(sourceContentType, targetContentType, Consistency.SUPPORT, createAndPublishCompositeTP);
		if(transformationUnits!=null && transformationUnits.size()>0)
			return transformationUnits;
		log.info("Trying to find TPs with generic match of the Content Types");
		return getTransformationUnitsByConsistencyLevel(sourceContentType, targetContentType, Consistency.GENERIC, createAndPublishCompositeTP);
	}
	
	//TODO: These transformationUnit Units must also include totally generic parameters '-'.
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph#findAnyTransformationUnits(org.gcube.datatransformation.datatransformationlibrary.model.ContentType, org.gcube.datatransformation.datatransformationlibrary.model.ContentType, boolean)
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @param targetContentType The <tt>ContentType</tt> of the target <tt>DataElement</tt>.
	 * @param createAndPublishCompositeTP If true then a new composite <tt>TransformationProgram</tt> is created and published if no available <tt>TransformationProgram</tt> exists.
	 * @return The available <tt>TransformationUnits</tt>.
	 */
	synchronized public ArrayList <TransformationUnit> findAnyTransformationUnits(ContentType sourceContentType, ContentType targetContentType, boolean createAndPublishCompositeTP){
		log.info("Trying to find TPs with exact match of the Content Types");
		ArrayList<TransformationUnit> transformationUnits = getTransformationUnitsByConsistencyLevel(sourceContentType, targetContentType, Consistency.EXACT, createAndPublishCompositeTP);
		if(transformationUnits!=null && transformationUnits.size()>0)
			return transformationUnits;
		log.info("Trying to find TPs with supporting match of the Content Types");
		transformationUnits = getTransformationUnitsByConsistencyLevel(sourceContentType, targetContentType, Consistency.SUPPORT, createAndPublishCompositeTP);
		if(transformationUnits!=null && transformationUnits.size()>0)
			return transformationUnits;
		log.info("Trying to find TPs with generic match of the Content Types");
		return getTransformationUnitsByConsistencyLevel(sourceContentType, targetContentType, Consistency.GENERIC, createAndPublishCompositeTP);
	}
	
	/**
	 * @see org.gcube.datatransformation.datatransformationlibrary.model.graph.TransformationsGraph#findAvailableTargetContentTypes(org.gcube.datatransformation.datatransformationlibrary.model.ContentType)
	 * @param sourceContentType The <tt>ContentType</tt> of the source <tt>DataElement</tt>.
	 * @return The available target <tt>ContentTypes</tt> from the <tt>sourceContentType</tt>.
	 */
	synchronized public ArrayList<ContentType> findAvailableTargetContentTypes(ContentType sourceContentType){
		ArrayList<ContentType> targetContentTypes = new ArrayList<ContentType>();
		HashSet<TNode> visited = new HashSet<TNode>();
		LinkedList<TNode> toSearch = new LinkedList<TNode>();
		
		ArrayList<TNode> snodes = nodes.getAnyThatSupport(sourceContentType);
		for(TNode node: snodes){
			toSearch.add(node);
			visited.add(node);
		}
		
		while(!toSearch.isEmpty()){
			TNode tosearch = toSearch.poll();
			ArrayList<TEdge> sedges = tosearch.getEdges();
			for(TEdge edge: sedges){
				TNode tnode = edge.getToNode();
				if(!visited.contains(tnode)){
					log.trace("Node: "+tnode.toString()+" not visited");
					targetContentTypes.add(tnode);
					toSearch.add(tnode);
					visited.add(tnode);
					ArrayList<TNode> nodessuptrg = nodes.getAnyThatSupport(tnode);
					for(TNode nodesuptrg : nodessuptrg){
						if(!visited.contains(nodesuptrg)){
							toSearch.add(nodesuptrg);
							visited.add(nodesuptrg);
						}
					}
				}else{
					log.trace("Node: "+tnode.toString()+" already visited");
				}
			}
		}
		return targetContentTypes;
	}
	
	private ArrayList<TransformationUnit> getTransformationUnitsByConsistencyLevel(ContentType sourceContentType, ContentType targetContentType, Consistency consistencyLevel, boolean createAndPublishCompositeTP){
		ArrayList<TransformationUnit> transformationUnits = new ArrayList<TransformationUnit>();
		
		ArrayList<TNodeWithUnbound> snodes=null;
		ArrayList<TNodeWithUnbound> tnodes=null;
		
		log.info("Trying to find transformationUnit from "+sourceContentType.toString()+" to "+targetContentType.toString()+" with consistency "+consistencyLevel+".");
		if(sourceContentType.equals(targetContentType)){
			log.info("Source and target content types are the same, no need to transform the content ;-)...");
			return null;
		}
		if(consistencyLevel.equals(Consistency.EXACT)){
			snodes = nodes.getExactlySupportedWithUnbound(sourceContentType);
			tnodes = nodes.getExactlySupportedWithUnbound(targetContentType);
		} else if(consistencyLevel.equals(Consistency.SUPPORT)){
			snodes = nodes.getAnyThatSupportWithUnbound(sourceContentType);
			tnodes = nodes.getAnyThatSupportWithUnbound(targetContentType);
		} else if(consistencyLevel.equals(Consistency.GENERIC)){
			snodes = nodes.getGenericallySupported(sourceContentType);
			tnodes = nodes.getGenericallySupported(targetContentType);
		} else{
			log.error("...");
			return null;
		}
		
		if(snodes==null || snodes.isEmpty()){
			log.trace("DTS doesn't have "+consistencyLevel+" support for any transformation unit for this source.");
			return null;
		}
		if(tnodes==null || tnodes.isEmpty()){
			log.trace("DTS doesn't have "+consistencyLevel+" support for any transformation unit for this target.");
			return null;
		}
		
		/* Searching first for direct transformations */
		for(TNodeWithUnbound snode: snodes){
			ArrayList<TEdge> sedges = snode.getNode().getEdges();
			for(TNodeWithUnbound tnode: tnodes){
				for(TEdge edge: sedges){
					if(edge.getToNode().equals(tnode.getNode()))
						transformationUnits.add(edge.getTransformationUnit());
				}
			}
		}
		
		/* Should search here for transformations requiring more than one steps for each pair <snode, tnode> */
		if(transformationUnits.size()==0 && createAndPublishCompositeTP){
			ArrayList<Path> paths = new ArrayList<Path>();
			for(TNodeWithUnbound snode: snodes){
				for(TNodeWithUnbound tnode: tnodes){
					PathFinder pfinder = new PathFinder();
					ArrayList<Path> tmppaths = pfinder.getPaths(snode, tnode, consistencyLevel, nodes);
					if(tmppaths!=null && tmppaths.size()>0){
						paths.addAll(tmppaths);
					}
				}
			}
			if(paths!=null && paths.size()>0){
				Path mincostpath=null;
				log.info("Managed to find "+paths.size()+" paths with "+consistencyLevel+" consistency.");
				for(Path path: paths){
					log.trace("PATH: ");
					for(TEdge edge: path.getPath()){
						log.trace("Edge: TP=\""+edge.getTransformationUnit().getTransformationProgram().getId()+"\" - TR=\""+edge.getTransformationUnit().getId()+"\"");
						for(Parameter param: path.getSUnbound(edge))
							log.trace("SrcUnbound: "+param.toString());
						for(Parameter param: path.getTUnbound(edge))
							log.trace("TrgUnbound: "+param.toString());
					}
					if(mincostpath==null || mincostpath.getCost()>path.getCost()){
						mincostpath=path;
					}
				}
				if(mincostpath!=null){//Should be...
					log.info("The min cost of the path is "+mincostpath.getCost()+".");
					//I should now create a new TP and publish it...
					TransformationProgram newTP = new TransformationProgram();
					
					//Instead of putting the objects source and transformationUnit's target content type in the new transformationUnit Unit,
					//we put the ones of the program so if it has * keep them...
//					ContentType tUSourceContentType = sourceContentType;
					ContentType tUSourceContentType = mincostpath.getPath().get(0).getTransformationUnit().getSources().get(0).getContentType();
//					ContentType tUTargetContentType = targetContentType;
					ContentType tUTargetContentType = mincostpath.getPath().get(mincostpath.getPath().size()-1).getTransformationUnit().getTarget().getContentType();
					
					newTP.setId(UUID.randomUUID().toString());
					newTP.setName("TP_"+sourceContentType.getMimeType().replaceAll("/", "_")+"to"+targetContentType.getMimeType().replaceAll("/", "_"));
					newTP.setDescription("Composite TP automatically created transforming "+tUSourceContentType.toString()+" to "+tUTargetContentType.toString());
					
					TransformationUnit transformationUnit = new TransformationUnit();
					transformationUnit.setTransformationProgram(newTP);
					transformationUnit.setComposite(true);
					transformationUnit.setId("0");
					ArrayList<Source> sources = new ArrayList<Source>();
					Source source = new Source();
					source.setContentType(tUSourceContentType);
					source.setTransformationUnit(transformationUnit);
					source.setInputID("TRInput0");
					sources.add(source);
					Target target = new Target();
					target.setContentType(tUTargetContentType);
					target.setTransformationUnit(transformationUnit);
					target.setOutputID("TROutput");
					transformationUnit.setSources(sources);
					transformationUnit.setTarget(target);
					ArrayList<ExtTransformationUnit> exttransformations = new ArrayList<ExtTransformationUnit>();
					for(int i=0;i<mincostpath.getPath().size();i++){
						TEdge edge = mincostpath.getPath().get(i);
						log.info("pid=\""+edge.getTransformationUnit().getTransformationProgram().getId()+"\" - tid=\""+edge.getTransformationUnit().getId()+"\"");
						//TODO: For these I guess I have to keep the previous ExtTransformationUnit and ADD (not set) them there...
						for(Parameter param: mincostpath.getSUnbound(edge))
							log.info("s_"+param.getName()+": "+param.getValue());
						for(Parameter param: mincostpath.getTUnbound(edge))
							log.info("t_"+param.getName()+": "+param.getValue());
						ExtTransformationUnit exttransformation = new ExtTransformationUnit();
						exttransformation.setTransformationUnit(transformationUnit);
						exttransformation.setReferencedTransformationProgramID(edge.getTransformationUnit().getTransformationProgram().getId());
						exttransformation.setReferencedTransformationUnitID(edge.getTransformationUnit().getId());
						
						TargetHandlerDesc targetInputDataHandlerDesc = new TargetHandlerDesc();
						targetInputDataHandlerDesc.setTargetID(edge.getTransformationUnit().getSources().get(0).getInputID());
						if(i==0){
							targetInputDataHandlerDesc.setThisID("TRInput0");
						}else{
							targetInputDataHandlerDesc.setThisID("TRBridge"+(i-1));
						}
						targetInputDataHandlerDesc.setType(XMLDefinitions.ELEMENT_TargetInput);
						
						TargetHandlerDesc targetOutputDataHandlerDesc = new TargetHandlerDesc();
						targetOutputDataHandlerDesc.setTargetID(edge.getTransformationUnit().getTarget().getOutputID());
						if(i==mincostpath.getPath().size()-1){
							targetOutputDataHandlerDesc.setThisID("TROutput");
						}else{
							targetOutputDataHandlerDesc.setThisID("TRBridge"+i);
						}
						targetOutputDataHandlerDesc.setType(XMLDefinitions.ELEMENT_TargetOutput);
						
						ArrayList<TargetHandlerDesc> targetHandlerDescs = new ArrayList<TargetHandlerDesc>();
						targetHandlerDescs.add(targetInputDataHandlerDesc);
						targetHandlerDescs.add(targetOutputDataHandlerDesc);
						exttransformation.setTargetIOs(targetHandlerDescs);
						
						//if consistency equals exactconsistency then no unbound params must be present
						if(consistencyLevel.equals(Consistency.SUPPORT)){
							//TODO: Check the things below...
							//Maybe the the last target unbound parameters should not be set 
							//e.g. in case pdf -> jpeg -> png 100x100, keep pdf -> jpeg -> png *x* is preferable
							if(i!=mincostpath.getPath().size()-1){//This may do the above...
								exttransformation.setUnboundContentTypeParameters(mincostpath.getTUnbound(edge));
							}
						}else if(consistencyLevel.equals(Consistency.GENERIC)){
							//TODO: fill in any unbound params - The two lines below are set without even thinking :-)
							if(i!=mincostpath.getPath().size()-1){
								exttransformation.setUnboundContentTypeParameters(mincostpath.getTUnbound(edge));
							}
						}
						exttransformations.add(exttransformation);
					}
					transformationUnit.setExtTransformationList(exttransformations);
					ArrayList<TransformationUnit> tUnits = new ArrayList<TransformationUnit>();
					tUnits.add(transformationUnit);
					newTP.setTransformationUnits(tUnits);
					//Publishing the program...
					try {
						imanager.publishTransformationProgram(newTP);
						transformationUnits.add(transformationUnit);
					} catch (Exception e) {
						log.error("Could not publish the transformationUnit program");
					}
					try {
						//Updating the graph with the newly created transformationUnit program
						addTransformationProgramInGraph(newTP, this.nodes);
					} catch (Exception e) {
						log.error("Could not add in the graph the newly created transformationUnit program", e);
					}
				}
			}
		}
		return transformationUnits;
	}
}

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * Updates the <tt>TransformationsGraph</tt> each <tt>UPDATEINTERVAL</tt> milliseconds.
 * </p>
 */
class Updater extends Thread{
	
	public Updater(){
		this.setDaemon(true);
	}
	
	private TransformationsGraphImpl graph;

	private boolean finished = false; 
	
	private static Logger log = LoggerFactory.getLogger(Updater.class);
	
	private static long UPDATEINTERVAL = PropertiesManager.getInMillisPropertyValue("graph.update", "3600");
	
	public void run(){
		Thread.currentThread().setName("Transformations Graph Updater");
		while(!finished){
			log.info("Starting updating the Transformations Graph");
			long ts = System.currentTimeMillis();
			try {
				graph.update();
			} catch (Exception e1) {
				log.error("Could not update graph keeping the old nodes...",e1);
			}
			log.info("Updating the Transformations Graph has finished after " + (System.currentTimeMillis() - ts) + "msecs, sleeping for "+(UPDATEINTERVAL/1000)+" secs...");
			try {
				Thread.sleep(UPDATEINTERVAL);
			} catch (InterruptedException e) {log.info("Updater has been Interupted");}
		}
		log.info("Updater has finished...");
	}

	public void setTransformationsGraph(TransformationsGraphImpl graph) {
		this.graph = graph;
	}
	
	protected void stopThread(){
		finished=true;
	}
}