/**
 * 
 */
package org.cotrix.gcube.extension;

import static org.cotrix.action.UserAction.*;
import static org.cotrix.common.CommonUtils.*;
import static org.cotrix.common.Constants.*;
import static org.cotrix.domain.dsl.Users.*;
import static org.cotrix.repository.UserQueries.*;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.cotrix.common.BeanSession;
import org.cotrix.common.events.Current;
import org.cotrix.domain.user.Role;
import org.cotrix.domain.user.User;
import org.cotrix.gcube.stubs.PortalUser;
import org.cotrix.gcube.stubs.SessionToken;
import org.cotrix.io.CloudService;
import org.cotrix.repository.UserRepository;
import org.cotrix.security.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.virtual.workspace.WorkspacePlugin;
import org.virtualrepository.RepositoryService;

/**
 * @author "Federico De Faveri federico.defaveri@fao.org"
 * 
 */
@Alternative
@Priority(RUNTIME)
@Singleton
public class GCubeRealm implements Realm {

	private Logger logger = LoggerFactory.getLogger(GCubeRealm.class);

	@Inject
	private PortalProxyProvider safePortalUrlProvider;

	@Inject
	private PortalProxyProvider portalProxyProvider;

	@Inject
	private UserRepository userRepository;

	@Inject
	private RoleMapper roleMapper;

	@Inject
	private CloudService cloud;

	@Inject
	@Current
	private BeanSession session;

	@Inject
	private RequestLifecycle lifecycle;

	private ExecutorService asyncPool = Executors.newCachedThreadPool();

	@Override
	public boolean supports(Object token) {

		return token instanceof SessionToken;

	}

	@Override
	public String login(Object token) {

		SessionToken stoken = reveal(token, SessionToken.class);

		PortalProxy portalProxy = portalProxyProvider.getPortalProxy(stoken);

		session.add(PortalProxy.class, portalProxy);

		PortalUser external = portalProxy.getPortalUser();

		User internal = userRepository.get(userByName(external.userName()));

		if (internal == null)
			internal = intern(external);
		else
			update(external, internal);

		initSession(stoken,internal);

		return external.userName();
	}

	private void initSession(SessionToken token, final User user) {

		lifecycle.init(token, user);

		final int timeout = 2*60*1000;

		//refresh cloud with "personal" repos
		for (final RepositoryService service : cloud.repositories())
			if (service.name().equals(WorkspacePlugin.name)) {

				asyncPool.submit(

						new Runnable() {

							@Override
							public void run() {
								try {
									cloud.discover(timeout,service);
								}
								catch(Exception e) {
									logger.error("cannot refresh cloud for "+user.name());
								}
							}
						}
						);

				break;
			}

		//remember external session details
		session.add(SessionToken.class, token);

	}

	private User intern(PortalUser external) {

		logger.info("interning external gCube user: {}", external);

		Collection<Role> roles = roleMapper.map(external.roles());

		User user = user().name(external.userName()).fullName(external.fullName()).email(external.email()).is(roles).build();

		userRepository.add(user);

		//TODO tmp workaround
		User changeset = modifyUser(user).can(VIEW.on(user.id())).build();
		userRepository.update(changeset);

		return user;

	}

	private void update(PortalUser external, User internal) {

		logger.trace("updating internal user from external gCube user: {}", external);

		Collection<Role> roles = roleMapper.map(external.roles());

		User modified = modifyUser(internal)
				.fullName(external.fullName())
				.email(external.email())
				.isNoLonger(PortalRole.roles()) //eliminate older gcube roles first
				.is(roles).build();

		userRepository.update(modified);
	}


	@Override
	public void add(String name, String pwd) {
		throw new UnsupportedOperationException("sign up active only through iMarine portal");
	}

}
