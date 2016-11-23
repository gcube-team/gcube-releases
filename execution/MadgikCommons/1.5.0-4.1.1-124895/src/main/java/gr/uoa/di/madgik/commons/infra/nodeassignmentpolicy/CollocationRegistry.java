package gr.uoa.di.madgik.commons.infra.nodeassignmentpolicy;

import gr.uoa.di.madgik.commons.infra.HostingNode;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author gerasimos.farantatos - DI NKUA
 *
 */
public class CollocationRegistry 
{
	
	private Map<String, HostingNode> nodes = new HashMap<String, HostingNode>();
	private Map<String, Integer> timesSelected = new HashMap<String, Integer>();
	private Map<String, Integer> roundsSelected = new HashMap<String, Integer>();
	private Map<String, Float> collocationScore = new HashMap<String, Float>();
	private Map<String, Float> totalCollocationScore = new HashMap<String, Float>();
	
	public void markSelected(HostingNode node)
	{
		nodes.put(node.getId(), node);
		if(!timesSelected.containsKey(node.getId())) timesSelected.put(node.getId(), 1);
		else timesSelected.put(node.getId(), timesSelected.get(node.getId()) + 1);
		if(!roundsSelected.containsKey(node.getId())) roundsSelected.put(node.getId(), 1);
		if(!collocationScore.containsKey(node.getId())) collocationScore.put(node.getId(), 0.0f);
		if(!totalCollocationScore.containsKey(node.getId())) totalCollocationScore.put(node.getId(), 0.0f);
	}
	
	public boolean isSelected(HostingNode node)
	{
		return nodes.containsKey(node.getId());
	}
	
	public int timesSelected(HostingNode node)
	{
		if(!timesSelected.containsKey(node.getId())) return 0;
		return timesSelected.get(node.getId());
	}
	
	public int currentRound(HostingNode node)
	{
		Integer round = roundsSelected.get(node.getId());
		if(round == null) return 1;
		return round;
	}
	
	public void newRound(HostingNode node)
	{
		if(!roundsSelected.containsKey(node.getId())) roundsSelected.put(node.getId(), 1);
		else roundsSelected.put(node.getId(), roundsSelected.get(node.getId()) + 1);
		collocationScore.put(node.getId(), 0.0f);
	}
	
	public float getCollocationScore(HostingNode node) throws Exception
	{
		if(!isSelected(node)) throw new Exception("Node is not marked as selected");
		return collocationScore.get(node.getId());
	}
	
	public float getTotalCollocationScore(HostingNode node) throws Exception
	{
		if(!isSelected(node)) throw new Exception("Node is not marked as selected");
		return totalCollocationScore.get(node.getId());
	}
	
	public void addToCollocationScore(HostingNode node, float value) throws Exception
	{
		if(!isSelected(node)) throw new Exception("Node is not marked as selected");
		collocationScore.put(node.getId(), collocationScore.get(node.getId())+value);
		totalCollocationScore.put(node.getId(), totalCollocationScore.get(node.getId())+value);
	}
	
}
