package org.gcube.data.analysis.statisticalmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.analysis.statisticalmanager.experimentspace.AlgorithmCategory;
import org.gcube.data.analysis.statisticalmanager.persistence.algorithms.AlgorithmCategoryDescriptor;
import org.gcube.data.analysis.statisticalmanager.persistence.algorithms.AlgorithmDescriptor;
import org.gcube.data.analysis.statisticalmanager.persistence.algorithms.AlgorithmManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

public class AlgorithmManagerTest {

	private static String[] scopes=new String[]{
//		"/gcube",
//		"/gcube/devNext",
//		"/gcube/devNext/NextNext"
		"/gcube/devsec/devVRE"
	};
	
	@Before
	public void initAlgos() throws Exception{
		AlgorithmManager.initInstalledAlgorithms("/home/fabio/workspaces/trunk_workspace/statistical-manager-gcubews/config/cfg/",false);
	}
	
	
	@Test
	public void init() throws Exception{
		System.err.println("Init Algorithms");		
		for(String scope:scopes){
			ScopeProvider.instance.set(scope);
			System.err.println("****************** SCOPE IS  "+scope);		
			
			String toLookFor="ICCAT_VPA";
			
			System.out.println("Looking for installed : ");
			Map<AlgorithmCategory,AlgorithmCategoryDescriptor> installed=AlgorithmManager.getInstalledAlgorithms();
			for(AlgorithmCategoryDescriptor category:installed.values())
				if(category.containsAlgorithm(toLookFor)) System.out.println("Found in category : "+category.getCategory().name());
			
			System.err.println("Installed  : "+count(installed));
			for(AlgorithmCategoryDescriptor category:installed.values())
				System.err.println("\t"+category.getCategory()+" : "+category.getAlgorithms().size());
			
			Map<AlgorithmCategory,AlgorithmCategoryDescriptor> available=AlgorithmManager.getAvailableAlgorithms(null);
			System.err.println("Available : "+count(available));
			for(AlgorithmCategoryDescriptor category:available.values())
				System.err.println("\t"+category.getCategory()+" : "+category.getAlgorithms().size());			
			
			
			HashSet<String> userPerspectiveCategories=new HashSet<String>();
			for(AlgorithmCategoryDescriptor category:installed.values())
				for(AlgorithmDescriptor desc:category.getAlgorithms().values())
					userPerspectiveCategories.addAll(desc.getUserPerspectiveCategory());
			
			System.err.println("User perspective categories");
			System.err.println(userPerspectiveCategories);
		
			
			
		}
		
		
	}
	
	private static int count(Map<AlgorithmCategory,AlgorithmCategoryDescriptor> toCount){
		int count=0;
		for(AlgorithmCategoryDescriptor desc:toCount.values())
			count+=desc.getAlgorithms().size();
		return count;
	}
	
	
	@Test
	public void checkOrder() throws Exception{		
		ScopeProvider.instance.set(scopes[0]);
		Assert.assertTrue(checkOrder(AlgorithmManager.getInstalledAlgorithms().keySet(),AlgorithmManager.getInstalledAlgorithms().keySet()));
		Assert.assertTrue(checkOrder(AlgorithmManager.getInstalledAlgorithms().values(),AlgorithmManager.getInstalledAlgorithms().values()));
		Assert.assertTrue(checkOrder(AlgorithmManager.getAvailableAlgorithms(null).values(),AlgorithmManager.getAvailableAlgorithms(null).values()));
		Assert.assertTrue(checkOrder(Arrays.asList((AlgorithmManager.groupByUserPerspective(AlgorithmManager.getAvailableAlgorithms(null)).getList())),
				Arrays.asList(AlgorithmManager.groupByUserPerspective(AlgorithmManager.getAvailableAlgorithms(null)).getList())));
	}
	
	
	private <T> boolean checkOrder(Collection<T> first,Collection<T> second){
		System.out.println("Checking "+second);
		System.out.println("Against "+first);
		if(first==second)return true;
		if((first==null && second!=null)||(second==null&&first!=null)) return false;
		T[] firstArray=(T[]) first.toArray();
		T[] secondArray=(T[]) second.toArray();
		
		int offset=0; // offset between arrays index due to lacking of elements
		for(int i=0;i<firstArray.length;i++){
			T firstElement=firstArray[i];
			if(isPresent(secondArray,firstElement)){
				T secondElement=secondArray[i-offset];
				if(!secondElement.equals(firstElement)){
					System.out.println("Uncoherent elements : found "+secondElement+", expected "+firstElement);
					return false;
				}
			}else offset++;
		}
		return true;
	}
	
	private <T> boolean isPresent(T[] collection,T value){
		if(collection==null||collection.length==0)return false;
		for(T element: collection)
			if(value.equals(element)) return true;
		return false;
	}
	
}
