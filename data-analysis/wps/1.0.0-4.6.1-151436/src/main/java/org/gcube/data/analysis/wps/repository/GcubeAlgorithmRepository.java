package org.gcube.data.analysis.wps.repository;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.n52.wps.algorithm.annotation.Algorithm;
import org.n52.wps.server.IAlgorithm;
import org.n52.wps.server.IAlgorithmRepository;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GcubeAlgorithmRepository implements IAlgorithmRepository {

	private static Logger log = LoggerFactory.getLogger(GcubeAlgorithmRepository.class);

	private static AlgorithmUpdater updater = null;

	private static Reflections reflection;
	
	private static final String PACKAGE_TO_FIND = "org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses";
	
	public GcubeAlgorithmRepository(){
		log.info("gcube algorithm repository started");
		if (updater==null) throw new RuntimeException("GcubeAlgorithmRepository cannot be initialized: updater is null");
		updateRepository();
		updater.startWhatcher();
	}

	public ProcessDescriptionType getProcessDescription(String identifier){
		updateRepository();
		log.info("getProcessDescription with identifier {} ",identifier);
		try{
			Set<Class<?>> classes = reflection.getTypesAnnotatedWith(Algorithm.class);	
			for (Class<?> _class: classes){
				if (_class.getAnnotation(Algorithm.class).identifier().equals(identifier)){
					return ((IAlgorithm)_class.newInstance()).getDescription();
				}
			}
		}catch(Exception e){}
		throw new RuntimeException(String.format("Algorithm with process id %s not found", identifier));
	}

	public boolean containsAlgorithm(String identifier) {
		updateRepository();
		log.info("containsAlgorithm with identifier {} ",identifier);
		Set<Class<?>> classes = reflection.getTypesAnnotatedWith(Algorithm.class);	
		for (Class<?> _class: classes){
			if (_class.getAnnotation(Algorithm.class).identifier().equals(identifier)){
				return true;
			}
		}
		return false;
	}

	public IAlgorithm getAlgorithm(String identifier){
		updateRepository();
		log.info("getAlgorithm with identifier {} ",identifier);
		try{
			Set<Class<?>> classes = reflection.getTypesAnnotatedWith(Algorithm.class);	
			for (Class<?> _class: classes){
				if (_class.getAnnotation(Algorithm.class).identifier().equals(identifier)){
					if (IAlgorithm.class.isAssignableFrom(_class)){
						return (IAlgorithm)_class.newInstance();
					} else {
						log.warn("found algorothm class {} is no assignable from {}",_class.getName(), IAlgorithm.class.getName());
						break;
					}
				}
			}
		}catch(Exception e){}
		throw new RuntimeException(String.format("Algorithm with id %s not found", identifier));
	}

	public static Set<Class<?>> getAllAlgorithms() {
		updateRepository();
		return reflection.getTypesAnnotatedWith(Algorithm.class);
	}

	private static synchronized void updateRepository(){
		if (reflection==null || updater.mustUpdate()){
			log.info("updating repository ({}) ",updater.mustUpdate());
			updater.reset();
			ConfigurationBuilder confBuilder = new ConfigurationBuilder()
			.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(PACKAGE_TO_FIND)))
			.setUrls(((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs());
			reflection = new Reflections(confBuilder);
		}
	}

	@Override
	public Collection<String> getAlgorithmNames() {
		updateRepository();
		Collection<String> toReturn = new ArrayList<String>(); 
		Set<Class<?>> classes = reflection.getTypesAnnotatedWith(Algorithm.class);	
		for (Class<?> _class: classes){
			toReturn.add(_class.getAnnotation(Algorithm.class).title());
		}
		return toReturn;
	}

	@Override
	public void shutdown() {
		reflection = null;
	}

	public static boolean setUpdater(AlgorithmUpdater au){
		if (updater==null){
			updater = au;
			return true;
		} else return false;
	}
	
}
