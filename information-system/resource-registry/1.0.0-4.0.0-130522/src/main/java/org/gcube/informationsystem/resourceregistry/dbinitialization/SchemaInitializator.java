/**
 * 
 */
package org.gcube.informationsystem.resourceregistry.dbinitialization;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.embedded.ValueSchema;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.entity.Facet;
import org.gcube.informationsystem.model.entity.Resource;
import org.gcube.informationsystem.model.relation.ConsistOf;
import org.gcube.informationsystem.model.relation.RelatedTo;
import org.gcube.informationsystem.model.relation.Relation;
import org.gcube.informationsystem.resourceregistry.dbinitialization.Tree.Node;
import org.gcube.informationsystem.resourceregistry.dbinitialization.Tree.NodeVisitor;
import org.gcube.informationsystem.resourceregistry.resources.impl.SchemaManagementImpl;
import org.gcube.informationsystem.types.TypeBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 * 
 */
@SuppressWarnings("rawtypes")
public class SchemaInitializator {

	private static Logger logger = LoggerFactory.getLogger(SchemaInitializator.class);

	private static Set<Package> packages;

	private static final Comparator<Class<Entity>> entityComparator;
	private static final Comparator<Class<Relation>> relationComparator;
	private static final Comparator<Class<Embedded>> embeddedComparator;

	
	private static boolean DRY_RUN;

	static {
		DRY_RUN = false;
		packages = new HashSet<>();

		entityComparator = new Comparator<Class<Entity>>() {

			@Override
			public int compare(Class<Entity> o1, Class<Entity> o2) {
				//logger.trace("Comparing {} with {}", o1, o2);
				return String.valueOf(o1).compareTo(String.valueOf(o2));
			}
		};

		relationComparator = new Comparator<Class<Relation>>() {

			@Override
			public int compare(Class<Relation> o1, Class<Relation> o2) {
				//logger.trace("Comparing {} with {}", o1, o2);
				return String.valueOf(o1).compareTo(String.valueOf(o2));
			}
		};
		
		embeddedComparator = new Comparator<Class<Embedded>>() {

			@Override
			public int compare(Class<Embedded> o1, Class<Embedded> o2) {
				//logger.trace("Comparing {} with {}", o1, o2);
				return String.valueOf(o1).compareTo(String.valueOf(o2));
			}
		};

	}

	protected static void addPackage(Package p){
		packages.add(p);
	}

	@SuppressWarnings("unchecked")
	protected static void analizeVertex(Map<Class<Entity>,Node<Class<Entity>>> visitedVertex, Class<Entity> clz) {
		logger.trace(" --- Analizyng Entity {}", clz.getCanonicalName());
		if(visitedVertex.containsKey(clz)){
			logger.trace(" --------- discarding {} because was already managed", clz);
			return;
		}else{
			//logger.trace(" --------- Adding {} to {}", clz, );

			Class<?>[] interfaces = clz.getInterfaces();
			List<Class<Entity>> interfaceList = new ArrayList<>();

			for (Class<?> interfaceClass : interfaces) {
				if(!Entity.class.isAssignableFrom(interfaceClass)){
					logger.trace(" --------- discarding {} because is not a {}", interfaceClass, Entity.class.getSimpleName());
					continue;
				}

				Class<Entity> v = (Class<Entity>) interfaceClass;

				if(visitedVertex.containsKey(v)){
					Node<Class<Entity>> root = visitedVertex.get(v);
					logger.trace(" --------- Adding {} to {}", clz, root);
					Node<Class<Entity>> node = root.addChild(clz);
					visitedVertex.put(clz, node);
					break;
				}else{
					interfaceList.add((Class<Entity>) interfaceClass);
				}
			}

			if(!visitedVertex.containsKey(clz)){
				for(Class<Entity> interfaceClass : interfaceList){
					analizeVertex(visitedVertex, interfaceClass);
					Class<Entity> v = (Class<Entity>) interfaceClass;
					Node<Class<Entity>> root = visitedVertex.get(v);
					logger.trace(" --------- Adding {} to {}", clz, root);
					Node<Class<Entity>> node = root.addChild(clz);
					visitedVertex.put(clz, node);
					break;
				}
			}

			logger.trace("{}", (Object[]) interfaces);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	protected static void analizeEmbedded(Map<Class<Embedded>,Node<Class<Embedded>>> visitedEmbedded, Class<Embedded> clz) {
		logger.trace(" --- Analizyng Embedded {}", clz.getCanonicalName());
		if(visitedEmbedded.containsKey(clz)){
			logger.trace(" --------- discarding {} because was already managed", clz);
			return;
		}else{
			//logger.trace(" --------- Adding {} to {}", clz, );

			Class<?>[] interfaces = clz.getInterfaces();
			List<Class<Embedded>> interfaceList = new ArrayList<>();

			for (Class<?> interfaceClass : interfaces) {
				if(!Embedded.class.isAssignableFrom(interfaceClass)){
					logger.trace(" --------- discarding {} because is not a {}", interfaceClass, Embedded.class.getSimpleName());
					continue;
				}

				Class<Embedded> v = (Class<Embedded>) interfaceClass;

				if(visitedEmbedded.containsKey(v)){
					Node<Class<Embedded>> root = visitedEmbedded.get(v);
					logger.trace(" --------- Adding {} to {}", clz, root);
					Node<Class<Embedded>> node = root.addChild(clz);
					visitedEmbedded.put(clz, node);
					break;
				}else{
					interfaceList.add((Class<Embedded>) interfaceClass);
				}
			}

			if(!visitedEmbedded.containsKey(clz)){
				for(Class<Embedded> interfaceClass : interfaceList){
					analizeEmbedded(visitedEmbedded, interfaceClass);
					Class<Embedded> v = (Class<Embedded>) interfaceClass;
					Node<Class<Embedded>> root = visitedEmbedded.get(v);
					logger.trace(" --------- Adding {} to {}", clz, root);
					Node<Class<Embedded>> node = root.addChild(clz);
					visitedEmbedded.put(clz, node);
					break;
				}
			}

			logger.trace("{}", (Object[]) interfaces);
		}
	}
	
	@SuppressWarnings({ "unchecked"})
	protected static void analizeEdge(Map<Class<Relation>,Node<Class<Relation>>> visitedEdge, Class<Relation> clz) {
		logger.trace(" --- Analizyng Relation {}", clz.getCanonicalName());
		if(visitedEdge.containsKey(clz)){
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
				Class<Relation> v = (Class<Relation>) interfaceClass;

				if(visitedEdge.containsKey(v)){
					Node<Class<Relation>> root = visitedEdge.get(v);
					logger.trace(" --------- Adding {} to {}", clz, root);
					Node<Class<Relation>> node = root.addChild(clz);
					visitedEdge.put(clz, node);
					break;
				}else{
					interfaceList.add((Class<Relation>) interfaceClass);
				}
			}

			if(!visitedEdge.containsKey(clz)){
				for(Class<Relation> interfaceClass : interfaceList){
					analizeEdge(visitedEdge, interfaceClass);
					Class<Relation> v = (Class<Relation>) interfaceClass;
					Node<Class<Relation>> root = visitedEdge.get(v);
					logger.trace(" --------- Adding {} to {}", clz, root);
					Node<Class<Relation>> node = root.addChild(clz);
					visitedEdge.put(clz, node);
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
	
	protected static <T> void createEmbedded(final Node<Class<T>> node){

		@SuppressWarnings("unchecked")
		Class<Embedded> clz = (Class<Embedded>) node.getValue();
		
		if(clz==Embedded.class){
			logger.trace("Discarding {} because is just a convenient interface", clz);
			return;
		}
		
		if(ValueSchema.class.isAssignableFrom(clz)){
			logger.trace("Discarding {} because was programmatically already created", clz);
			return;
		}
		
		try{
			String json = TypeBinder.serializeType(clz);
			logger.trace(json);
			if(!DRY_RUN){
				new SchemaManagementImpl().registerEmbeddedTypeSchema(json);
			}
		} catch(Exception e){
			logger.error("error serializing schema", e);
		}

	}
	
	protected static <T> void createVertex(final Node<Class<T>> node){

		@SuppressWarnings("unchecked")
		Class<Entity> clz = (Class<Entity>) node.getValue();
		
		try{
			String json = TypeBinder.serializeType(clz);
			logger.trace(json);
			if(!DRY_RUN){
				if (Facet.class.isAssignableFrom(clz)) {
					new SchemaManagementImpl().registerFacetSchema(json);
				} else if(Resource.class.isAssignableFrom(clz)){
					new SchemaManagementImpl().registerResourceSchema(json);
				} else {
					new SchemaManagementImpl().registerEntitySchema(json);
				}
			}
		} catch(Exception e){
			logger.error("error serializing schema", e);
		}

	}

	protected static <T> void createEdge(final Node<Class<T>> node){

		@SuppressWarnings("unchecked")
		Class<Relation> clz = (Class<Relation>) node.getValue();
		
		try{
			String json = TypeBinder.serializeType(clz);
			logger.trace(json);
			if(!DRY_RUN){
				if (ConsistOf.class.isAssignableFrom(clz)) {
					new SchemaManagementImpl().registerConsistOfSchema(json);
				} else if(RelatedTo.class.isAssignableFrom(clz)){
					new SchemaManagementImpl().registerRelatedToSchema(json);
				} else {
					new SchemaManagementImpl().registerRelationSchema(json);
				}
			}
		} catch(Exception e){
			logger.error("error serializing schema", e);
		}
		
	}

	protected static <T> NodeVisitor<Class<T>> getNodeVisitor(Class<T> t) {
		return new NodeVisitor<Class<T>>() {

			@Override
			public boolean visit(final Node<Class<T>> node) {
				Class<T> clz = node.getValue();
				if(Embedded.class.isAssignableFrom(clz)) {
					createEmbedded(node);
				}else if (Entity.class.isAssignableFrom(clz)) {
					createVertex(node);
				}else if (Relation.class.isAssignableFrom(clz)) {
					createEdge(node);
				}
				return true;
			}
		};
	}


	@SuppressWarnings({ "unchecked"})
	public static void createTypes() throws Exception{

		Tree<Class<Embedded>> embeddeds = new Tree<>(embeddedComparator);
		Tree<Class<Entity>> vertexes = new Tree<>(entityComparator);
		Tree<Class<Relation>> edges = new Tree<>(relationComparator);
		
		
		Map<Class<Embedded>, Node<Class<Embedded>>> addedEmbedded = new TreeMap<>(embeddedComparator);
		Map<Class<Entity>, Node<Class<Entity>>> addedVertex = new TreeMap<>(entityComparator);
		Map<Class<Relation>, Node<Class<Relation>>> addedEdge = new TreeMap<>(relationComparator);
		
		
		Node<Class<Embedded>> rootEmbedded = embeddeds.getRootElement();
		rootEmbedded.setValue(Embedded.class);
		addedEmbedded.put(Embedded.class, rootEmbedded);
		
		Node<Class<Entity>> rootVertex = vertexes.getRootElement();
		rootVertex.setValue(Entity.class);
		addedVertex.put(Entity.class, rootVertex);

		Node<Class<Relation>> rootEdge = edges.getRootElement();
		rootEdge.setValue(Relation.class);
		addedEdge.put(Relation.class, rootEdge);
		

		for (Package p : packages) {
			logger.trace("Analyzing {}", p);
			try {
				List<Class<?>> classes = ReflectionUtility.getClassesForPackage(p);
				for (Class<?> clz : classes) {
					//logger.trace("Analyzing {}", clz);
					if(!clz.isInterface()){
						logger.trace("Discarding {} that is not an interface", clz);
						continue;
					}
					
					if (Embedded.class.isAssignableFrom(clz)) {
						analizeEmbedded(addedEmbedded, (Class<Embedded>) clz);
					}
					
					if (Entity.class.isAssignableFrom(clz)) {
						analizeVertex(addedVertex, (Class<Entity>) clz);
					}
					
					if (Relation.class.isAssignableFrom(clz)) {
						analizeEdge(addedEdge, (Class<Relation>) clz);
					}
				}
			} catch (ClassNotFoundException e) {
				logger.error("Error discovering classes inside package {}",
						p.getName(), e);
				throw e;
			}
		}
		
		
		try{
			String json = TypeBinder.serializeType(ValueSchema.class);
			logger.trace(json);
			if(!DRY_RUN){
				new SchemaManagementImpl().registerEmbeddedTypeSchema(json);
			}
		} catch(Exception e){
			logger.error("error serializing schema", e);
		}
		createEmbedded(addedEmbedded.get(ValueSchema.class));
		
		NodeVisitor<Class<Embedded>> embeddedNodeVisitor = getNodeVisitor(Embedded.class);
		embeddeds.visitNodes(embeddedNodeVisitor);
		
		NodeVisitor<Class<Entity>> vertexNodeVisitor = getNodeVisitor(Entity.class);
		vertexes.visitNodes(vertexNodeVisitor);

		NodeVisitor<Class<Relation>> edgeNodeVisitor = getNodeVisitor(Relation.class);
		edges.visitNodes(edgeNodeVisitor);

	}

}
