/**
 * 
 */
package org.gcube.vremanagement.executor.pluginmanager;

import org.gcube.vremanagement.executor.exception.InvalidPluginStateEvolutionException;
import org.gcube.vremanagement.executor.plugin.PercentageSetter;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.gcube.vremanagement.executor.plugin.PluginDeclaration;

/**
 * @author Luca Frosini (ISTI - CNR) http://www.lucafrosini.com/
 *
 */
public class PercentageSetterImpl<T extends Plugin<? extends PluginDeclaration>> implements PercentageSetter {

	private final RunnablePlugin<T> runnablePlugin;
	
	public PercentageSetterImpl(RunnablePlugin<T> runnablePlugin){
		this.runnablePlugin = runnablePlugin;
	}
	
	public void setPercentageEvolution(Integer percentage){
		try {
			if(percentage<0 || percentage>100){
				throw new InvalidPluginStateEvolutionException("Percentage must be beetween 0 and 100");
			}
			this.runnablePlugin.setPercentage(percentage);
		} catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
}
