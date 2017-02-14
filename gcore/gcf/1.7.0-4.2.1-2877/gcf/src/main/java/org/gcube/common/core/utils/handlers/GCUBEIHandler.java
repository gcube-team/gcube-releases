package org.gcube.common.core.utils.handlers;

import java.util.Map;

import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.handlers.events.Monitor;
import org.gcube.common.core.utils.handlers.events.Topic;
import org.gcube.common.core.utils.handlers.events.Topic.LifetimeTopic;
import org.gcube.common.core.utils.logging.GCUBELog;

/**
 * Characterises the behaviour of <em>handlers</em>, i.e. objects that model arbitrary tasks.
 * 
 * <p>
 * 
 * A handler:
 *
 * <ul> 
 *  <li>has an identity and a configurable name (cf. {@link #getId()},{@link #getName()},{@link #setName(String)});</li>
 *  <li>can be executed (cf. {@link #run()});</li>
 *  <li>may log its execution to an external logger (cf. {@link #setLogger(GCUBELog)});</li>
 *  <li>may notifyclients events that relate to its execution 
 *  	(cf. {@link #subscribe(Monitor, Topic...)},{@link #unsubscribe(Monitor, Topic...)}) ;
 *  </li>
 *  <li>may need to use {@link GCUBEScopeManager GCUBEScopeManagers} and/or {@link GCUBESecurityManager GCUBESecurityManagers} 
 * during its execution (cf. {@link #setScopeManager(GCUBEScopeManager)},{@link #setSecurityManager(GCUBESecurityManager)}); 
 *  <li>may use two forms of state during its execution:
 *  	<ul>
 *  		<li>the <em>handled object</em>, a distinguished object upon or on behalf of which the handler may execute (cf. {@link #setHandled(Object)}, {@link #getHandled()}).</li>
 *  		<li>a <em>blackboard</em> of arbitrary named values (cf. {@link #getBlackboard()},{@link #setBlackboard(Map)},{@link #clearBlackboard()}.</li>
 * 		</ul>
 *  </li>
 * </ul>
 * 
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 * @param <HANDLED> the type of the handled object.
 * @see GCUBEHandler
 */
public interface GCUBEIHandler<HANDLED> {

	/**Sets the logger.
	 * @param logger the logger.*/
	public void setLogger(GCUBELog logger);
	
	/**Returns the logger.
	 * @return the logger.*/
	public GCUBELog getLogger();

	/**Executes the task implemented by the handler.
	 * @throws Exception if the execution of the task does not complete normally.*/
	public void run() throws Exception;

	/**
	 * Returns the handled object.
	 * @return the handled object.
	 */
	public HANDLED getHandled();

	/**Sets the handled object.
	 * @param handled the handled object.*/
	public void setHandled(HANDLED handled);

	/**Returns the blackboard.
	 * @return the blackboard.*/
	public Map<String, Object> getBlackboard();

	/**Sets the blackboard.
	 * @param blackboard the blackboard.*/
	public void setBlackboard(Map<String, Object> blackboard);

	/**Clears the blackboard.**/
	public void clearBlackboard();

	/**Returns the identifier of the handler.
	 * @return the identifier.*/
	public String getId();

	/**Returns the name of the handler, by default, the name of its class.
	 * @return the name.*/
	public String getName();

	/**
	 * Sets the name of the handler.
	 * @param name the name.
	 */
	public void setName(String name);

	/**Subscribes a {@link Monitor} to one or more {@link Topic Topics}.
	 * @param monitor the monitor.
	 * @param topics (optional) the topics. If omitted, the monitor is subscribed to {@link LifetimeTopic}.*/
	public void subscribe(Monitor monitor, Topic... topics);

	/**Unsubscribes a {@link Monitor} from one or more {@link Topic Topics}.
	 * @param monitor the monitor.
	 * @param topics (optional) the topics. If omitted, the monitor is subscribed to {@link LifetimeTopic}.*/
	public void unsubscribe(Monitor monitor, Topic... topics);

	/**Reverts the actions of the handler.
	 * By default, it simply <code>debug</code>-logs the invocation. 
	 * If required, override as per handler semantics. For robustness, do not 
	 * assume the invocation occurs after the execution of the handler. */
	public void undo();
	
	/**
	 * Sets a scope manager for the handler.
	 * @param manager the manager. */
	public void setScopeManager(GCUBEScopeManager manager);
	
	/**Returns the scope manager of the handler.
	 * @return the manager.*/
	public GCUBEScopeManager getScopeManager();
	
	/**Sets a security manager for the handler.
	 * @param manager the manager.*/
	public void setSecurityManager(GCUBESecurityManager manager);
	
	/**Returns the security manager of the handler.
	 * @return the manager.*/
	public GCUBESecurityManager getSecurityManager();
}