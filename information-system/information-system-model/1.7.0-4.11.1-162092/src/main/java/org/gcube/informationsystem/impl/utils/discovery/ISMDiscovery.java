/**
 * 
 */
package org.gcube.informationsystem.impl.utils.discovery;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.gcube.informationsystem.model.ISManageable;
import org.gcube.informationsystem.model.annotations.ISProperty;
import org.gcube.informationsystem.model.embedded.Embedded;
import org.gcube.informationsystem.model.entity.Entity;
import org.gcube.informationsystem.model.relation.Relation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ISMDiscovery<ISM extends ISManageable> {

	private static Logger logger = LoggerFactory.getLogger(ISMDiscovery.class);

	protected final Class<ISM> root;
	protected final List<Package> packages;
	protected final List<Class<ISM>> discovered;
	
	public List<Class<ISM>> getDiscovered(){
		return discovered;
	}
	
	public ISMDiscovery(Class<ISM> root) {
		this.root = root;
		this.packages = new ArrayList<>();
		addPackage(root.getPackage());
		
		this.discovered = new ArrayList<>();
		add(root);

	}
	
	public void addPackage(Package p) {
		packages.add(p);
	}

	protected void add(Class<ISM> clz) {
		discovered.add(clz);
		logger.debug("+ Added {}.", clz);
	}

	protected void analizeISM(Class<ISM> clz) {
		logger.trace("Analizyng {}", clz);

		if (!clz.isInterface()) {
			logger.trace("- Discarding {} that is not an interface", clz);
			return;
		}
		
		if (!root.isAssignableFrom(clz)) {
			logger.trace("- Discarding {} because is not a {}", clz,
					root.getClass().getSimpleName());
			return;
		}
		
		if (discovered.contains(clz)) {
			logger.trace("- Discarding {} because was already managed", clz);
			return;
		}
			
		Class<?>[] interfaces = clz.getInterfaces();

		for (Class<?> interfaceClass : interfaces) {
			@SuppressWarnings("unchecked")
			Class<ISM> parent = (Class<ISM>) interfaceClass;
			analizeISM(parent);
		}
		
		if(root==Embedded.class){
			
			for (Method m : clz.getDeclaredMethods()){
				m.setAccessible(true);
				if(m.isAnnotationPresent(ISProperty.class)){
					if(root.isAssignableFrom(m.getReturnType())){
						@SuppressWarnings("unchecked")
						Class<ISM> type = (Class<ISM>) m.getReturnType();
						analizeISM(type);
					}
				}  

			}
		}
		
		add(clz);
	}

	public void discover() throws Exception {
		for(Package p : packages) {
			List<Class<?>> classes = ReflectionUtility.getClassesForPackage(p);
			for (Class<?> clz : classes) {
				@SuppressWarnings("unchecked")
				Class<ISM> ism = (Class<ISM>) clz;
				analizeISM(ism);
			}
		}

	}

	
	@SuppressWarnings("unchecked")
	public static void manageISM(SchemaAction schemaAction, Package... packages) throws Exception {
		ISMDiscovery<Embedded> embeddedDiscovery = new ISMDiscovery<>(Embedded.class);
		for(Package p : packages) {
			embeddedDiscovery.addPackage(p);
		}
		embeddedDiscovery.discover();
		
		for(Class<Embedded> embedded : embeddedDiscovery.getDiscovered()) {
			logger.info("Going to create : {}", embedded);
			schemaAction.manageEmbeddedClass(embedded);
		}
		
		ISMDiscovery<Entity> entityDiscovery = new ISMDiscovery<>(Entity.class);
		for(Package p : packages) {
			embeddedDiscovery.addPackage(p);
		}
		entityDiscovery.discover();
		
		for(Class<Entity> entity : entityDiscovery.getDiscovered()) {
			logger.info("Going to create : {}", entity);
			schemaAction.manageEntityClass(entity);
		}
		
		
		@SuppressWarnings("rawtypes")
		ISMDiscovery<Relation> relationDiscovery = new ISMDiscovery<>(Relation.class);
		for(Package p : packages) {
			embeddedDiscovery.addPackage(p);
		}
		relationDiscovery.discover();
		
		for(@SuppressWarnings("rawtypes") Class<Relation> relation : relationDiscovery.getDiscovered()) {
			logger.info("Going to create : {}", relation);
			schemaAction.manageRelationClass(relation);
		}
	}
	
	
}
