package gr.uoa.di.madgik.commons.infra.nodefilter;

import gr.uoa.di.madgik.commons.infra.HostingNode;

public interface Facet {
	boolean applyStrongConstraints(HostingNode hostingNode);
	boolean applyWeakConstraints(HostingNode hostingNode);
}

