package org.gcube.vremanagement.vremodeler.impl.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.gcube.common.authorization.library.AuthorizedTasks;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.vremanagement.vremodeler.db.DBInterface;
import org.gcube.vremanagement.vremodeler.impl.deploy.UndeployVRE;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VRE;
import org.gcube.vremanagement.vremodeler.utils.reports.Status;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;

public class ExpiredVREFinderTask{

	
	private static HashMap<String, ExpiredVREFinderTask> taskMap= new HashMap<String, ExpiredVREFinderTask>();
	
	public static ExpiredVREFinderTask get(String scope) throws Exception{
		if (!taskMap.containsKey(scope)){
			ExpiredVREFinderTask task = new ExpiredVREFinderTask(scope);
			taskMap.put(scope.toString(), task);
			return task;
		} else return taskMap.get(scope.toString());
	}
	
	GCUBELog logger = new GCUBELog(ExpiredVREFinderTask.class);
	
	Timer timer;

	Dao<VRE, String> vreDao;
	
	PreparedQuery<VRE> preparedQuery;
	
    private ExpiredVREFinderTask(String scope) throws Exception {
    	vreDao =
                DaoManager.createDao(DBInterface.connect(scope), VRE.class);
        logger.trace("object created in scope "+scope.toString());
        SelectArg selectArg = new SelectArg();
    	preparedQuery = vreDao.queryBuilder().where().lt("intervalTo", selectArg ).prepare();
    	timer = new Timer(true);
        timer.schedule(new ExpiredTask(scope),
                      60000,        //initial delay (1 minute)
                      10*(60*60000));  //delay period (10 hours)
    }

    
    public void cancel(){
    	this.timer.cancel();
    }
    
    class ExpiredTask extends TimerTask {
    	
    	String scope;
    	
    	ExpiredTask(String scope){
    		super();
    		this.scope = scope;
    	}
    	
        public void run() {
           logger.trace("executing task in scope "+scope.toString());
           ScopeProvider.instance.set(scope);
           try{
        		preparedQuery.setArgumentHolderValue(0, Calendar.getInstance().getTimeInMillis());
            	for (VRE vre: vreDao.query(preparedQuery)){
            		logger.trace("vre "+vre.getName()+" found with status "+vre.getStatus());
            			if (vre.getStatus().equals(Status.Deployed.toString())){
            				
            				logger.trace("preparing to undeploy vre "+vre.getName());
            				UndeployVRE undeploy = new UndeployVRE(vre.getId());
            				AuthorizedTasks.bind(undeploy);
            				undeploy.start();
            			}
            			
            	}
            	
            }catch (Exception e) {
				logger.warn("error executing task for expired vres",e);
			}finally{
				ScopeProvider.instance.reset();
			}
        }
    }
}
