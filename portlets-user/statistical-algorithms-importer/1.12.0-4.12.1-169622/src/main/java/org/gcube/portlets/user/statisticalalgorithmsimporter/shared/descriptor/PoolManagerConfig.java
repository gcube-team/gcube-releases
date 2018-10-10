package org.gcube.portlets.user.statisticalalgorithmsimporter.shared.descriptor;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class PoolManagerConfig implements Serializable {

	private static final long serialVersionUID = 5010071163308091807L;
	private boolean enable;

	public PoolManagerConfig() {

	}

	public PoolManagerConfig(boolean enable) {
		super();
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "PoolManagerConfig [enable=" + enable + "]";
	}

}
