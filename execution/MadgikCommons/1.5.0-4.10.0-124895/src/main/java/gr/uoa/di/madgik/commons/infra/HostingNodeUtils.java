package gr.uoa.di.madgik.commons.infra;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HostingNodeUtils 
{
	
	private static String getDomain(HostingNode hn)
	{
		String domain = hn.getPropertyByName(HostingNode.DomainProperty);
		return domain;
	}
	
	public static float distance(HostingNode hn1, HostingNode hn2) throws Exception
	{
		String hostName1 = hn1.getPropertyByName(HostingNode.HostnameProperty);
		String hostName2 = hn2.getPropertyByName(HostingNode.HostnameProperty);
		if(hostName1 == null || hostName2 == null) throw new Exception("No host name information");
		
		if(HostingNodeTopology.containsNode(hostName1) && HostingNodeTopology.containsNode(hostName2))
		{
			if(HostingNodeTopology.sameNode(hostName1, hostName2)) return 0.0f;
			if(HostingNodeTopology.sameRack(hostName1, hostName2)) return 1.0f;
			if(HostingNodeTopology.sameDomain(hostName1, hostName2)) return 2.0f;
			return 3.0f;
		}
		
		if(hostName1.equals(hostName2)) return 0.0f;

		float distance = distanceHeuristic(hostName1, hostName2);

		if (distance < 3.0f)
			return distance;
		
		String domain1 = getDomain(hn1);
		String domain2 = getDomain(hn2);
		if(domain1 != null && domain2!=null && domain1.equals(domain2)) {
			distance = 3.0f;
		}
		
		return distance;
	}

	private static float distanceHeuristic(String hostName1, String hostName2) {
		boolean reverse = true;
		int compareNum, similarities;
		float distance = maxDistance();


		if (isIP(hostName1) ^ isIP(hostName2))
				return distance;
		
		if (isIP(hostName1) && isIP(hostName2))
			reverse = false;
		
		String tokensArray1[] = hostName1.split("\\.");
		List<String> tokensList1 = Arrays.asList(tokensArray1);
		
		String tokensArray2[] = hostName2.split("\\.");
		List<String> tokensList2 = Arrays.asList(tokensArray2);
		
		if (reverse) {
			Collections.reverse(tokensList1);
			Collections.reverse(tokensList2);
		}
		
		similarities = 0;
		compareNum = tokensList1.size() < tokensList2.size()? tokensList1.size() : tokensList2.size();
		for (int i = 0; i < compareNum; i++) {
			if (!tokensList1.get(i).equals(tokensList2.get(i)))
				break;
			similarities++;
		}
		
		if (similarities > 0)
			distance = 3f - (float)similarities/compareNum;
		return distance;
	}
	
	private static boolean isIP(String host) {
		return host.matches("\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
	}
	
	public static float minDistanceInSameNode()
	{
		return 0.0f;
	}
	
	public static float maxDistanceInSameNode()
	{
		return 0.0f;
	}
	
	public static float minDistanceInSameRack()
	{
		return 1.0f;
	}
	
	public static float maxDistanceInSameRack()
	{
		return 2.0f;
	}
	
	public static float minDistanceInSameDomain()
	{
		return 2.0f;
	}
	
	public static float maxDistanceInSameDomain()
	{
		return 3.0f;
	}
	
	public static float maxDistance()
	{
		return 4.0f;
	}
}
