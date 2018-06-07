package org.gcube.common.homelibrary.jcr.home;

import java.util.List;

import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeManager;
import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.data.ApplicationsArea;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.jcr.JCRUser;
import org.gcube.common.homelibrary.jcr.data.JCRApplicationsArea;
import org.gcube.common.homelibrary.jcr.repository.JCRRepository;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;



public class JCRHome implements Home{

	private final HomeManager homeManager;
	private final JCRUser owner;

	//private final File persistenceFolder;
	private final JCRRepository repository;
	private final JCRWorkspace workspace;
	private JCRApplicationsArea applicationArea;

//	private Logger logger;

	public JCRHome(HomeManager homeManager, JCRUser user) throws Exception {
		super();
//		setupLogger();
		this.owner = user;
		//this.persistenceFolder = persistenceFolder;
		this.homeManager = homeManager;
		this.repository = new JCRRepository(user);
		this.workspace = new JCRWorkspace(this, repository);	
	}

//
//	private void setupLogger() {
//		this.logger = LoggerFactory.getLogger(JCRHome.class);
//	}


	@Override
	public HomeManager getHomeManager() {
		return homeManager;
	}


	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public Workspace getWorkspace() throws WorkspaceFolderNotFoundException,
	InternalErrorException {
		return workspace;
	}

	@Override
	public ApplicationsArea getDataArea() throws InternalErrorException {
		if (applicationArea==null)
			applicationArea = new JCRApplicationsArea(workspace);
		return applicationArea;
	}

	@Override
	public List<String> listScopes() throws  InternalErrorException {
		try {
			return repository.listScopes();
		} catch (RepositoryException e) {
			throw new InternalErrorException(e);
		}
	}

	@Override
	public void releaseResources() {
		// TODO Auto-generated method stub
		
	}

}
