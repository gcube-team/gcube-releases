package org.gcube.portlets.admin.wfdocslibrary.shared;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <code> ForwardAction </code> class represent and oriented edge in the graph, it is the component of each adjacency matrix cell
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
@SuppressWarnings("serial")
public class ForwardAction implements Serializable {

	private Map<WfRole, Map<UserInfo, Boolean>> actions;
	/**
	 * in case of curved edges this represents the points where the edge bends
	 */
	private List<EdgePoint> points;

	public ForwardAction() {}
	
	public ForwardAction(Map<WfRole, Map<UserInfo, Boolean>> actions,	List<EdgePoint> points) {
		super();
		this.actions = actions;
		this.points = points;
	}

	/**
	 * constructor to be used in template mode
	 * @param points
	 * @param roles
	 */
	public ForwardAction(List<EdgePoint> points, WfRole[] roles) {
		this.points = points;
		actions = new HashMap<WfRole, Map<UserInfo,Boolean>>();
		for (int i = 0; i < roles.length; i++) {
			actions.put(roles[i], null);
		}		
	}

	public Map<WfRole, Map<UserInfo, Boolean>> getActions() {	return actions;}
	public void setActions(Map<WfRole, Map<UserInfo, Boolean>> actions) {	this.actions = actions;}
	public List<EdgePoint> getPoints() {return points;	}
	public void setPoints(List<EdgePoint> points) {	this.points = points;	}

	/**
	 * 
	 * @return just the list of roles associated to this forward action (the edge labels)
	 */
	public ArrayList<WfRole> getRoles() {
		 ArrayList<WfRole> toRet = new ArrayList<WfRole>();
		 for (WfRole role : actions.keySet()) {
			 toRet.add(role);
		 }
		 return toRet;
	}
	
	public String getRolesToString() {
		String toRet = "";
//		 for (Entry<WfRole, Map<String, Boolean>> roles : actions.entrySet()) {
		 for (WfRole role : actions.keySet()) {
			 toRet += (role.getRolename()) +  " ";
		 }
		 return toRet;
	}
	/**
	 * 
	 */
	public String toString() {
		String toReturn =  "";
		for (Entry<WfRole, Map<UserInfo, Boolean>> roles : actions.entrySet()) {
			toReturn += " Required Role: " + roles.getKey().getRolename() + ", forwarded?";
			Map<UserInfo, Boolean> toPrint = roles.getValue();
			if ( toPrint != null) {
				for (Entry<UserInfo, Boolean> entry : toPrint.entrySet()) {
					toReturn += " " + entry.getKey() + ":" + entry.getValue();
				}
			}
		}
		return toReturn;
	}

}
