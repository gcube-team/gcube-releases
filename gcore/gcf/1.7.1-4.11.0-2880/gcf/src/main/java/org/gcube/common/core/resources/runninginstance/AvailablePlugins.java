package org.gcube.common.core.resources.runninginstance;

import java.util.HashSet;
import java.util.Set;

import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.service.Version;

/**
 * Available Plugins for {@link GCUBERunningInstance}
 * 
 * @author Manuee Simi (ISTI-CNR)
 *
 */
public class AvailablePlugins {
	
	Set<AvailablePlugin> plugins = new HashSet<AvailablePlugin>();
	

	/**
	 * @return the plugins
	 */
	public Set<AvailablePlugin> getPlugins() {
		return plugins;
	}


	/**
	 * @param plugins the plugins to set
	 */
	public void setPlugins(Set<AvailablePlugin> plugins) {
		this.plugins = plugins;
	}

	public static class PluginService extends org.gcube.common.core.resources.service.Dependency.Service {}

	public class AvailablePlugin extends PluginService {
		
		protected String _package;
		protected String packageversion;
		
		public String getPluginPackage(){return this._package;}
		
		public void setPluginPackage(String name) {this._package = name;}				
		
		public String getPluginVersion() {return this.packageversion;}
		
		public void setPluginVersion(String version) {this.packageversion = version; /*Version.completeVersionRange(version);*/}

		public AvailablePlugins getOuterType() {return AvailablePlugins.this;}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((_package == null) ? 0 : _package.hashCode());
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((packageversion == null) ? 0 : packageversion.hashCode());
			result = prime * result + ((version == null) ? 0 : version.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			AvailablePlugin other = (AvailablePlugin) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (_package == null) {
				if (other._package != null)
					return false;
			} else if (!_package.equals(other._package))
				return false;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			if (packageversion == null) {
				if (other.packageversion != null)
					return false;
			} else if (!packageversion.equals(other.packageversion))
				return false;
			if (version == null) {
				if (other.version != null)
					return false;
			} else if (!version.equals(other.version))
				return false;
			return true;
		}
				
	}
}
