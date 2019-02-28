package org.gcube.common.core.security.utils;

class CredentialPropagationStatus 
{
	private boolean set,
					override,
					propagate;

	public CredentialPropagationStatus() 
	{
		set = false;
		override = false;
		propagate = false;
	}
	
	public boolean isSet() {
		return set;
	}

	public boolean isOverride() {
		return override;
	}

	public void setOverride(boolean override) {
		this.override = override;
	}

	public boolean isPropagate() {
		return propagate;
	}

	public void setPropagate(boolean propagate) {
		this.set = true;
		this.propagate = propagate;
	}


	
	

}
