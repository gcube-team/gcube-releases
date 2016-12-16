/**
 * 
 */
package org.gcube.informationsystem.impl.utils.discovery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.gcube.informationsystem.impl.utils.discovery.Tree.Node;
import org.gcube.informationsystem.impl.utils.discovery.Tree.NodeVisitor;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ERDiscovery {

	private static Logger logger = LoggerFactory.getLogger(ERDiscovery.class);

	protected static Set<Package> packages;
	
	static {
		packages = new HashSet<>();
	}
	
	public static void addPackage(Package p){
		packages.add(p);
	}
	
	protected final Comparator<Class<Entity>> entityComparator;
	protected final Comparator<Class<Relation>> relationComparator;
	protected final Comparator<Class<Embedded>> embeddedComparator;

	protected final Map<Class<Embedded>, Node<Class<Embedded>>> visitedEmbedded;
	protected final Map<Class<Entity>, Node<Class<Entity>>> visitedEntity;
	protected final Map<Class<Relation>, Node<Class<Relation>>> visitedRelation;
	
	protected final Tree<Class<Embedded>> embeddedTree;
	protected final Tree<Class<Entity>> entityTree;
	protected final Tree<Class<Relation>> relationTree;
	
	public ERDiscovery(){

		entityComparator = new Comparator<Class<Entity>>() {

			@Override
			public int compare(Class<Entity> o1, Class<Entity> o2) {
				//logger.trace("Comparing {} with {}", o1, o2);
				return String.valueOf(o1).compareTo(String.valueOf(o2));
			}
		};
		visitedEntity = new TreeMap<>(entityComparator);
		entityTree = new Tree<>(entityComparator, Entity.class);
		visitedEntity.put(Entity.class, entityTree.getRootElement());
		
		relationComparator = new Comparator<Class<Relation>>() {

			@Override
			public int compare(Class<Relation> o1, Class<Relation> o2) {
				//logger.trace("Comparing {} with {}", o1, o2);
				return String.valueOf(o1).compareTo(String.valueOf(o2));
			}
		};
		visitedRelation = new TreeMap<>(relationComparator);
		relationTree = new Tree<>(relationComparator, Relation.class);
		visitedRelation.put(Relation.class, relationTree.getRootElement());
		
		
		embeddedComparator = new Comparator<Class<Embedded>>() {

			@Override
			public int compare(Class<Embedded> o1, Class<Embedded> o2) {
				//logger.trace("Comparing {} with {}", o1, o2);
				return String.valueOf(o1).compareTo(String.valueOf(o2));
			}
		};
		visitedEmbedded = new TreeMap<>(embeddedComparator);
		embeddedTree = new Tree<>(embeddedComparator, Embedded.class);
		visitedEmbedded.put(Embedded.class, embeddedTree.getRootElement());
		
	}
	
	protected void addEntity(Class<? extends Entity> clz, Class<? extends Entity> parent){
		Node<Class<Entity>> parentNode = visitedEntity.get(parent);
		logger.debug(" --------- Adding {} to {}", clz, parentNode);
		Node<Class<Entity>> node = parentNode.addChild((Class<Entity>) clz);
		visitedEntity.put((Class<Entity>) clz, node);
	}

	protected void analizeEntity(Class<? extends Entity> clz) {
		logger.trace(" --- Analizyng Entity {}", clz.getCanonicalName());
		if(visitedEntity.containsKey(clz)){
			logger.trace(" --------- discarding {} because was already managed", clz);
			return;
		}else{
			
			Class<?>[] interfaces = clz.getInterfaces();
			List<Class<Entity>> interfaceList = new ArrayList<>();

			for (Class<?> interfaceClass : interfaces) {
				if(!Entity.class.isAssignableFrom(interfaceClass)){
					logger.trace(" --------- discarding {} because is not a {}", interfaceClass, Entity.class.getSimpleName());
					continue;
				}

				Class<Entity> e = (Class<Entity>) interfaceClass;

				if(visitedEntity.containsKey(e)){
					addEntity(clz, e);
					break;
				}else{
					interfaceList.add(e);
				}
			}

			if(!visitedEntity.containsKey(clz)){
				for(Class<Entity> interfaceClass : interfaceList){
					analizeEntity(interfaceClass);
					Class<Entity> e = (Class<Entity>) interfaceClass;
					addEntity(clz, e);
					break;
				}
			}

			logger.trace("{}", (Object[]) interfaces);
		}
	}
	
	protected void addEmbedded(Class<? extends Embedded> clz, Class<? extends Embedded> parent){
		Node<Class<Embedded>> parentNode = visitedEmbedded.get(parent);
		logger.debug(" --------- Adding {} to {}", clz, parentNode);
		Node<Class<Embedded>> node = parentNode.addChild((Class<Embedded>) clz);
		visitedEmbedded.put((Class<Embedded>) clz, node);
	}
	
	protected void analizeEmbedded(Class<? extends Embedded> clz) {
		logger.trace(" --- Analizyng Embedded {}", clz.getCanonicalName());
		if(visitedEmbedded.containsKey(clz)){
			logger.trace(" --------- discarding {} because was already managed", clz);
			return;
		}else{
			
			Class<?>[] interfaces = clz.getInterfaces();
			List<Class<Embedded>> interfaceList = new ArrayList<>();

			for (Class<?> interfaceClass : interfaces) {
				if(!Embedded.class.isAssignableFrom(interfaceClass)){
					logger.trace(" --------- discarding {} because is not a {}", interfaceClass, Embedded.class.getSimpleName());
					continue;
				}

				Class<Embedded> e = (Class<Embedded>) interfaceClass;

				if(!visitedEmbedded.containsKey(e)){
					analizeEmbedded(e);
					break;
				}else{
					interfaceList.add(e);
				}
			}
			
			if(!visitedRelation.containsKey(clz)){
				for(Class<Embedded> interfaceClass : interfaceList){
					analizeEmbedded(interfaceClass);
					Class<Embedded> e = (Class<Embedded>) interfaceClass;
					addEmbedded(clz, e);
					break;
				}
			}
			
			logger.trace("{}", (Object[]) interfaces);
		}
	}
	
	protected void addRelation(Class<? extends Relation> clz, Class<? extends Relation> parent){
		Node<Class<Relation>> parentNode = visitedRelation.get(parent);
		logger.debug(" --------- Adding {} to {}", clz, parentNode);
		Node<Class<Relation>> node = parentNode.addChild((Class<Relation>) clz);
		visitedRelation.put((Class<Relation>) clz, node);
	}
	
	protected void analizeRelation(Class<? extends Relation> clz) {
		logger.trace(" --- Analizyng Relation {}", clz.getCanonicalName());
		if(visitedRelation.containsKey(clz)){
			logger.trace(" --------- discarding {} because was already managed", clz);
			return;
		}else{

			Class<?>[] interfaces = clz.getInterfaces();
			List<Class<Relation>> interfaceList = new ArrayList<>();

			for (Class<?> interfaceClass : interfaces) {
				if(!Relation.class.isAssignableFrom(interfaceClass)){
					logger.trace(" --------- discarding {} because is not a {}", interfaceClass, Relation.class.getSimpleName());
					continue;
				}
				Class<Relation> r = (Class<Relation>) interfaceClass;

				if(visitedRelation.containsKey(r)){
					addRelation(clz, r);
					break;
				}else{
					interfaceList.add(r);
				}
			}

			if(!visitedRelation.containsKey(clz)){
				for(Class<Relation> interfaceClass : interfaceList){
					analizeRelation(interfaceClass);
					Class<Relation> v = (Class<Relation>) interfaceClass;
					addRelation(clz, v);
					break;
				}
			}

			logger.trace("{}", (Object[]) interfaces);
		}
	}


	protected static <T> NodeVisitor<Class<T>> printNodeVisitor(Class<T> t) {
		return new NodeVisitor<Class<T>>() {

			@Override
			public boolean visit(final Node<Class<T>> node) {
				final StringBuilder sb = new StringBuilder();
				Node<Class<T>> curr = node;
				do {
					if (sb.length() > 0) {
						sb.insert(0, " > ");
					}
					sb.insert(0, String.valueOf(curr.getValue()));
					curr = curr.getParent();
				} while (curr != null);
				logger.debug(sb.toString());
				return true;
			}
		};
	}


	protected <T> NodeVisitor<Class<T>> getNodeVisitor(Class<T> t, final ERAction erAction) {
		return new NodeVisitor<Class<T>>() {

			@Override
			public boolean visit(final Node<Class<T>> node) {
				Class<T> clz = node.getValue();
				try{
					if(Embedded.class.isAssignableFrom(clz)) {
						erAction.manageEmbeddedClass((Class<Embedded>) clz);
					}else if (Entity.class.isAssignableFrom(clz)) {
						erAction.manageEntityClass((Class<Entity>) clz);
					}else if (Relation.class.isAssignableFrom(clz)) {
						erAction.manageRelationClass((Class<Relation>)clz); 
					}
				}catch(Exception e){
					logger.error("Error while visiting {} corresponding to {}", node, clz);
				}
				
				return true;
			}
		};
	}


	public void discoverERTypes() throws Exception {
		
		for (Package p : ERDiscovery.packages) {
			logger.trace("Analyzing {}", p);
			try {
				List<Class<?>> classes = ReflectionUtility.getClassesForPackage(p);
				for (Class<?> clz : classes) {
					logger.trace("Analyzing {}", clz);
					if(!clz.isInterface()){
						logger.trace("Discarding {} that is not an interface", clz);
						continue;
					}
					
					if (Embedded.class.isAssignableFrom(clz)) {
						analizeEmbedded((Class<Embedded>) clz);
					}
					
					if (Entity.class.isAssignableFrom(clz)) {
						analizeEntity((Class<Entity>) clz);
					}
					
					if (Relation.class.isAssignableFrom(clz)) {
						analizeRelation((Class<Relation>) clz);
					}
				}
			} catch (ClassNotFoundException e) {
				logger.error("Error discovering classes inside package {}",
						p.getName(), e);
				throw e;
			}
		}
	}
	
	public void manageDiscoveredERTypes(ERAction erAction)  throws Exception {
		
		NodeVisitor<Class<Embedded>> embeddedNodeVisitor = getNodeVisitor(Embedded.class, erAction);
		embeddedTree.visitNodes(embeddedNodeVisitor);
		
		NodeVisitor<Class<Entity>> vertexNodeVisitor = getNodeVisitor(Entity.class, erAction);
		entityTree.visitNodes(vertexNodeVisitor);

		NodeVisitor<Class<Relation>> edgeNodeVisitor = getNodeVisitor(Relation.class, erAction);
		relationTree.visitNodes(edgeNodeVisitor);

	}

}
