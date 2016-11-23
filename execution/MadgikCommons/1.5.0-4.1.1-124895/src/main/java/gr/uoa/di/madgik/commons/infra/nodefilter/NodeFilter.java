package gr.uoa.di.madgik.commons.infra.nodefilter;

import gr.uoa.di.madgik.commons.infra.HostingNode;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NodeFilter {
	protected static Logger logger = Logger.getLogger(NodeFilter.class.getName());

	public static boolean filterNode(Facet facet, ConstraintType filterType, HostingNode hostingNode) {
		//Set<HostingNode> filteredNodes = new HashSet<HostingNode>();

		switch (filterType) {
			case WEAK:
				logger.log(Level.INFO, "Required WEAK constraint");
				if (facet.applyWeakConstraints(hostingNode))
					return true;
				else
					logger.log(Level.INFO, "WEAK constraint not satisfied");
				break;
			case STRONG:
				logger.log(Level.INFO, "Required STRONG constraint");
				if (facet.applyStrongConstraints(hostingNode))
					return true;
				else
					logger.log(Level.INFO, "STRONG constraint not satisfied");
				break;
			case BOTH:
				logger.log(Level.INFO, "Required BOTH constraint");
				if (facet.applyStrongConstraints(hostingNode)) {
					if (facet.applyWeakConstraints(hostingNode))
						return true;
					else
						logger.log(Level.INFO, "WEAK constraint not satisfied");
				}
				else
					logger.log(Level.INFO, "STRONG constraint not satisfied");
				
				break;
			case NONE:
				logger.log(Level.INFO, "Required NO constraint");
			default:
				break;
		}

		return false;
	}
}
