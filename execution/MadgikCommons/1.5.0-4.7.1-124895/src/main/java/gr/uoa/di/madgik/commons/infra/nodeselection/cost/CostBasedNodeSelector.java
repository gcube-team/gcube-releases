package gr.uoa.di.madgik.commons.infra.nodeselection.cost;

import gr.uoa.di.madgik.commons.infra.HostingNode;
import gr.uoa.di.madgik.commons.infra.HostingNodeUtils;
import gr.uoa.di.madgik.commons.infra.nodeselection.HostingNodeInfo;
import gr.uoa.di.madgik.commons.infra.nodeselection.NodeSelector;
import gr.uoa.di.madgik.commons.infra.nodeselection.cost.CostFunction.CostFactor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CostBasedNodeSelector implements NodeSelector
{
	private CostFunction function = null;
	private HostingNode previousSelected = null;
	
	private static HostingNode localNode = null;
	
	private NodeSelector tieBreakerSelector = null;
	
	private static Logger logger = Logger.getLogger(CostBasedNodeSelector.class.getName());
	
	public CostBasedNodeSelector(CostFunction function)
	{
		this.function = function;
	}
	
	public CostBasedNodeSelector(CostFunction function, NodeSelector tieBreaker)
	{
		this(function);
		this.tieBreakerSelector = tieBreaker;
	}

	@Override
	public HostingNode selectNode(List<HostingNode> candidates) {
		HostingNode selected = assessNodes(candidates).get(0).node;
		markSelected(selected);
		return selected;
	}

	@Override
	public List<HostingNodeInfo> assessNodes(List<HostingNode> candidates) {
		
		List<HostingNodeInfo> hns = new ArrayList<HostingNodeInfo>();
		if(candidates.size() == 0) return hns;
		Map<HostingNode, Float> assessedNodes = new HashMap<HostingNode, Float>();
		Map<String, Float> extremeValues = new HashMap<String, Float>();
		Set<String> factorsToRemove = new HashSet<String>();
		
		searchLocal(candidates);
		
		for(CostFactor factor : function.getCostFactors())
		{
			float max = Float.MIN_VALUE; 
			
			boolean skip = false;
			for(HostingNode candidate : candidates)
			{
				if(!isPropertyValid(factor.name, candidate))
				{
					factorsToRemove.add(factor.name);
					skip = true;
					break;
				}
			
				float propertyValue = evaluate(factor.name, candidate);
				if(max < propertyValue)
					max = propertyValue;
			}
			if(!skip) extremeValues.put(factor.name, max);
		}
		
		//redo adjusting values
		for(CostFactor factor : function.getCostFactors())
		{
			float max = Float.MIN_VALUE; 
			
			boolean skip = false;
			for(HostingNode candidate : candidates)
			{
				if(!isPropertyValid(factor.name, candidate))
				{
					factorsToRemove.add(factor.name);
					skip = true;
					break;
				}
			
				float propertyValue = evaluate(factor.name, candidate);
				propertyValue = adjustValue(factor.name, candidate, extremeValues, propertyValue);
				if(max < propertyValue)
					max = propertyValue;
			}
			if(!skip) extremeValues.put(factor.name, max);
		}
		
		function.removeCostFactors(factorsToRemove);
		
		if(function.getCostFactors().size() == 0)
		{
			Collections.shuffle(candidates);
			int i = 0;
			for(HostingNode candidate : candidates)
				hns.add(new HostingNodeInfo(candidate, 1.0f));
			return hns;
		}
		
		for(HostingNode candidate : candidates)
		{
			//System.out.println("node: " + candidate.getId());
			Float val = 0.0f;
			for(CostFactor factor : function.getCostFactors())
			{
				//System.out.print("property: " + factor.name + " val: " + candidate.getPropertyByName(factor.name) + " extr: " + extremeValues.get(factor.name) + " normalized: " + Float.parseFloat(candidate.getPropertyByName(factor.name))/extremeValues.get(factor.name));
				Float v = evaluate(factor.name, candidate);
				v = adjustValue(factor.name, candidate, extremeValues.get(factor.name), v);
				Float normalizedValue = v/extremeValues.get(factor.name);
				if(normalizedValue.isNaN() || normalizedValue.isInfinite()) normalizedValue = 0.0f;
				if(factor.isAscending()) val += factor.getCoefficient() * normalizedValue;
				else val += factor.getCoefficient() * (1.0f-normalizedValue);
				//if(factor.ascending) System.out.println(" factor(+) coeff: " + factor.coefficient + " v: " + factor.coefficient*normalizedValue);
				//else System.out.println(" factor(-): coeff:" + factor.coefficient + " v: " + factor.coefficient*(1-normalizedValue));
			}
			assessedNodes.put(candidate, val);
		}
		Map<HostingNode, Float> sortedNodes = new TreeMap<HostingNode, Float>(new CostComparator(assessedNodes));
		sortedNodes.putAll(assessedNodes);
		float maxScore = sortedNodes.entrySet().iterator().next().getValue();
		for(Map.Entry<HostingNode, Float> hn : sortedNodes.entrySet()) hns.add(new HostingNodeInfo(hn.getKey(), maxScore != 0.0f ? hn.getValue()/maxScore : 1.0f));
		
		if(tieBreakerSelector != null) return resolveTies(hns);
		return hns;
	}
	
	private List<HostingNodeInfo> resolveTies(List<HostingNodeInfo> hns)
	{
		List<HostingNodeInfo> resolvedHns = new ArrayList<HostingNodeInfo>();
		boolean tieFound = false;
		List<HostingNode> ties = new ArrayList<HostingNode>();
		float tieScore = 0.0f;
		int beforeTieIndex = 0;
		int afterTieIndex = 0;
		if(hns.isEmpty() || hns.size() == 1) return hns;
		boolean resolveTies = false;
		boolean nextRunStart = false;
		for(int i = 1; i < hns.size(); i++)
		{
			HostingNodeInfo curr = hns.get(i);
			HostingNodeInfo prev = hns.get(i-1);
			HostingNodeInfo next = null;
			if(i+1 < hns.size()) next = hns.get(i+1);
			if(Math.abs(curr.score - prev.score) < 1e-10)
			{
				if(ties.isEmpty()) beforeTieIndex = i-2;
				ties.add(prev.node);
				tieScore = prev.score;
				if(next == null || Math.abs(next.score - curr.score) > 1e-10)
				{ 
					ties.add(curr.node);
					afterTieIndex = i+1;
					resolveTies = true;
				}
			}else
			{
				if(!resolveTies && !nextRunStart) resolvedHns.add(prev);
				if(next == null) resolvedHns.add(curr);
				nextRunStart = false;
			}
			if(resolveTies)
			{
				if(ties.size() > 0)
				{
					List<HostingNodeInfo> tieBreaked = tieBreakerSelector.assessNodes(ties);
					float upper, lower;
					boolean interpolate = false;
					float interpolationFactor = 1.0f;
					if(beforeTieIndex == -1) upper = tieScore;
					else
					{
						upper = hns.get(beforeTieIndex).score;
						interpolationFactor = 0.9f;
						interpolate = true;
					}
					if(afterTieIndex == hns.size()) lower = tieScore;
					else 
					{
						lower = hns.get(afterTieIndex).score;
						interpolate = true;
					}
					for(HostingNodeInfo tb : tieBreaked)
						resolvedHns.add(new HostingNodeInfo(tb.node, lower + tb.score * (upper-lower) * interpolationFactor));
				}
				resolveTies = false;
				nextRunStart = true;
				ties = new ArrayList<HostingNode>();
			}
		}
		return resolvedHns;
	}

	private float evaluate(String factor, HostingNode candidate)
	{
		if(factor.equals(CostFunction.DistanceToPrevious))
		{
			if(previousSelected == null)
			{
				if(candidate.isLocal() || localNode == null) return HostingNodeUtils.minDistanceInSameNode();
				try
				{
					return HostingNodeUtils.distance(candidate, localNode);
				}catch(Exception e)
				{
					logger.log(Level.WARNING, "Could not evaluate node distance. Returning " + HostingNodeUtils.minDistanceInSameDomain(), e);
					return HostingNodeUtils.minDistanceInSameDomain();
				}
			}
			try
			{
				return HostingNodeUtils.distance(candidate, previousSelected);
			}catch(Exception e)
			{
				logger.log(Level.WARNING, "Could not evaluate node distance. Returning " + HostingNodeUtils.minDistanceInSameDomain(), e);
				return HostingNodeUtils.minDistanceInSameDomain();
			}
		}
		return Float.parseFloat(candidate.getPropertyByName(factor));
	}
	
	private boolean isPropertyValid(String factor, HostingNode candidate)
	{
		if(factor.equals(CostFunction.DistanceToPrevious)) return true;
		return candidate.getPropertyByName(factor) != null;
	}
	
	private Float adjustValue(String factor, HostingNode candidate, Map<String, Float> extremeValues, Float val)
	{
		if(previousSelected == null || !factor.equals(CostFunction.DistanceToPrevious)) return val;
		if(candidate.getId().equals(previousSelected.getId()))
		{
			extremeValues.put(factor, extremeValues.get(factor)*2.0f);
			return extremeValues.get(factor);
		}
		return val;
	}
	
	private Float adjustValue(String factor, HostingNode candidate, Float extremeValue, Float val)
	{
		if(previousSelected == null || !factor.equals(CostFunction.DistanceToPrevious)) return val;
		if(candidate.getId().equals(previousSelected.getId())) return extremeValue;
		return val;
	}
	
	private void searchLocal(List<HostingNode> candidates)
	{
		if(localNode == null)
		{
			for(HostingNode candidate : candidates)
			{
				if(candidate.isLocal())
				{
					localNode = candidate;
					break;
				}
			}
		}
	}
	
	@Override
	public void markSelected(HostingNode node)
	{ 
		this.previousSelected = node;
	}
}
