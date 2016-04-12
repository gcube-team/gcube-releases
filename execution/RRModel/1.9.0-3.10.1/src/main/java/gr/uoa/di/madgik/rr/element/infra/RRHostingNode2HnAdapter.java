package gr.uoa.di.madgik.rr.element.infra;

public class RRHostingNode2HnAdapter extends gr.uoa.di.madgik.commons.infra.HostingNodeAdapter {

	@Override
	public gr.uoa.di.madgik.commons.infra.HostingNode adapt(Object o) throws Exception {
		//System.out.println("Calling adapt on HostingNode");
		
		if(o == null) return null;
		if(!(o instanceof HostingNode)) throw new Exception("Cannot adapt object of type " + o.getClass().getName()); 
		HostingNode hn = (HostingNode)o;
		gr.uoa.di.madgik.commons.infra.HostingNode targetHn = new gr.uoa.di.madgik.commons.infra.HostingNode(hn.getID(), hn.getPairs());
		if(hn.isLocal()) targetHn.markLocal();
		return targetHn;
	}
	
}
