/**
 * 
 */
package org.gcube.portlets.user.workspace.server.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.homelibrary.home.workspace.accessmanager.ACLType;

/**
 * The Class AclTypeComparator.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Mar 5, 2015
 */
public class AclTypeComparator implements Comparator<ACLType>{

	
	public static final Map<ACLType, Integer> aclTypeOrder;
    static
    {
        aclTypeOrder = new LinkedHashMap<ACLType, Integer>();
        aclTypeOrder.put(ACLType.READ_ONLY, 0);
        aclTypeOrder.put(ACLType.WRITE_OWNER, 1);
        aclTypeOrder.put(ACLType.WRITE_ALL, 2);
//        aclTypeOrder.put(ACLType.ADMINISTRATOR, 3);
    }
    

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ACLType o1, ACLType o2) {
		if(o1==null)
			return -1;
		if(o2==null)
			return 1;
		
		Integer order1 = aclTypeOrder.get(o1);
		Integer order2 = aclTypeOrder.get(o2);
		
		if(order1==null)
			return -1;
		
		if(order2==null)
			return 1;
		
		if(order1==order2)
			return 0;
		
		return order1<order2?-1:1;
	}
	

	/**
	 * Gets the allowed.
	 *
	 * @param aclType the acl type to compare
	 * @param includeEqual if true include equal ACLType, no otherwise
	 * @return the allowed
	 */
	public List<ACLType> getAllowed(ACLType aclType, boolean includeEqual) {
		
		List<ACLType> allowed = new ArrayList<ACLType>();
		
		if(aclType==null)
			return allowed;
		
		//IF MAP DOES NOT CONTAINS ACT TYPE IT IS NOT COMPARABLE
		if(aclTypeOrder.get(aclType)==null)
			return allowed;
		
		for (ACLType aMap : aclTypeOrder.keySet()) {
			int comparator = compare(aMap, aclType);
			if(comparator==1)
				allowed.add(aMap);
			else if(includeEqual && comparator==0)
				allowed.add(aMap);
		}
		
		return allowed;
	}

	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		AclTypeComparator comparator = new AclTypeComparator();
		
		List<ACLType> allowed = comparator.getAllowed(ACLType.WRITE_ALL, false);
		
		System.out.println(allowed);
		
	}


}
