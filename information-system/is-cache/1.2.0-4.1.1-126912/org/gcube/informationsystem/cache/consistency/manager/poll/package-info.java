/**
 * This package export the defaul cache consistency manager, which acts on a poll-basis.
 * This means that is contacts the origin server (IS) at regular basis and harvests its
 * dictated information (currently only service RIs). Then it simply invalidates the old
 * information and sets the new one as it's default. All its mechanics are completely
 * transparent to the system and can only be assumed the existence of the 
 * {@link org.gcube.informationsystem.cache.consistency.manager.poll.PollManager#initialize(org.gcube.informationsystem.cache.ISCache)}
 * method which is enforced by the {@link org.gcube.informationsystem.cache.consistency.manager.ConsistencyManagerIF}.
 * However, the execution parameters can be monitored and  managed at runtime, since the
 * component is fully JMX-enabled. Users/admins can use jconsole for this purpose.
 */
package org.gcube.informationsystem.cache.consistency.manager.poll;