package org.gcube.portal.plugins.thread;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.plugins.bean.LDAPInfo;
import org.gcube.portal.plugins.util.LDAPUtil;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class UpdateUserToLDAPGroupThread implements Runnable {
	private static Log _log = LogFactoryUtil.getLog(UpdateUserToLDAPGroupThread.class);


	private String username;
	private String scope;
	private long vreGroupId;
	private boolean remove;
	/**
	 * 
	 * @param username
	 * @param scope
	 * @param vreGroupId
	 * @param remove set true to remove the user from the group, false to add the user
	 */
	public UpdateUserToLDAPGroupThread(String username, String scope, long vreGroupId, boolean remove) {
		super();
		this.username = username;
		this.scope = scope;
		this.vreGroupId = vreGroupId;
		this.remove = remove;
	}

	@Override
	public void run() {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);

		LDAPInfo info = LDAPUtil.getLDAPCoordinates();
		GroupManager gm = new LiferayGroupManager();


		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, info.getLdapUrl());
		env.put(Context.SECURITY_PRINCIPAL, info.getPrincipal());
		env.put(Context.SECURITY_CREDENTIALS, info.getLdapPassword());

		try {
			GCubeGroup root = LDAPUtil.getRootVO();
			GCubeGroup vre = gm.getGroup(vreGroupId);
			GCubeGroup vo = gm.getGroup(vre.getParentGroupId());


			DirContext ctx = new InitialDirContext(env);

			String subCtx = LDAPUtil.getOrgSubContext(root.getGroupName());
			String orgSubCtx = "ou="+vo.getGroupName()+","+subCtx;
			String vreSubCtx = "cn="+vre.getGroupName()+","+orgSubCtx;
			//if the VRE is very NEW the LDAP Group may not exist
			if (!LDAPUtil.checkIfLDAPGroupExists(ctx, vreSubCtx)) 
				LDAPUtil.createGroupVRE(ctx, vreSubCtx, vre.getGroupName());
			//here update the list of users in such VRE with the username
			if (remove)
				removeUserFromGroup(username, ctx, vreSubCtx, vre);
			else
				addUsertoGroup(username, ctx, vreSubCtx, vre);
		} catch (NamingException e) {
			_log.error("Something went Wrong during UpdateUserToLDAPGroupThread");
			e.printStackTrace();
		} catch (Exception es) {
			_log.error("Something went Wrong during UpdateUserToLDAPGroupThread in retrieving Liferay Organization");
			es.printStackTrace();
		} finally {
			ScopeProvider.instance.set(currScope);
		}
		ScopeProvider.instance.set(currScope); //restore the scope in ThreadLocal
	}

	/**
	 * 
	 * @param username
	 * @param ctx
	 * @param vreSubCtx
	 * @param vre
	 */
	private static void addUsertoGroup(String username, DirContext ctx, String vreSubCtx, GCubeGroup vre) {
		String user = username;
		try {				
			Attribute memberUid = new BasicAttribute("memberUid");
			memberUid.add(user);
			Attributes attributes = new BasicAttributes();
			attributes.put(memberUid);		
			ctx.modifyAttributes(vreSubCtx, DirContext.ADD_ATTRIBUTE, attributes);
			_log.info("Added user: " + user + " to VRE: " + vre.getGroupName());
		}
		catch (NamingException ex) {
			_log.warn("Not adding already existing user: " + user);
		}
	}
	
	/**
	 * 
	 * @param username
	 * @param ctx
	 * @param vreSubCtx
	 * @param vre
	 */
	private static void removeUserFromGroup(String username, DirContext ctx, String vreSubCtx, GCubeGroup vre) {
		String user = username;
		try {				
			Attribute memberUid = new BasicAttribute("memberUid");
			memberUid.add(user);
			Attributes attributes = new BasicAttributes();
			attributes.put(memberUid);	
			ctx.modifyAttributes(vreSubCtx, DirContext.REMOVE_ATTRIBUTE, attributes);
			_log.info("Removed user: " + user + " from VRE: " + vre.getGroupName());
		}
		catch (NamingException ex) {
			_log.warn("Not removing, not existing user? " + user);
		}
	}

}


