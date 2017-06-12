package gr.uoa.di.madgik.commons.infra.nodeselection.cost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CostFunction 
{
	public static String DistanceToPrevious = "distanceToPrevious";
	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public static class CostFactor
	{
		public final String name;
		private float coefficient;
		private final boolean ascending;
		
		public CostFactor(String name, float coefficient, boolean ascending)
		{
			this.name = name;
			this.coefficient = coefficient;
			this.ascending = ascending;
		}
		
		public float getCoefficient()
		{
			return coefficient;
		}
		
		public boolean isAscending()
		{
			return ascending;
		}
	}
	
	private float fractionSum = 0.0f;
	private Map<String, CostFactor> costFactors = new HashMap<String, CostFactor>();
	
	public CostFunction() { }
	
	public void addCostFactor(String name, float fraction, boolean ascending) throws Exception
	{
		boolean locked = false;
		try
		{
			lock.writeLock().lock(); locked = true;
			if(fractionSum + fraction > 1.0f+ 1e-5f) throw new Exception("Coefficients exceed 1.0");
			costFactors.put(name, new CostFactor(name, fraction, ascending));
		}finally
		{
			if(locked) lock.writeLock().unlock();
		}
	}
	
	public void removeCostFactors(Set<String> factorsToRemove)
	{
		if(factorsToRemove.isEmpty()) return;
		boolean locked = false;
		try
		{
			lock.writeLock().lock(); locked = true;
			for(String factor : factorsToRemove) 
			{
				float coeff = costFactors.get(factor).coefficient;
				float coeffFactor = 1.0f/(1.0f - coeff);
				costFactors.remove(factor);
				for(CostFactor f : costFactors.values()) f.coefficient *= coeffFactor; //compensate for removal; factors should always sum to 1.0f
			}
		}finally
		{
			if(locked) lock.writeLock().unlock();
		}
	}
	
	public List<CostFactor> getCostFactors()
	{
		boolean locked = false;
		try
		{
			lock.readLock().lock(); locked = true;
			return new ArrayList<CostFactor>(costFactors.values());
		}finally
		{
			if(locked) lock.readLock().unlock();
		}
	}
}
