package org.gcube.informationsystem.collector.impl.xmlstorage.exist;

import org.gcube.informationsystem.collector.impl.xmlstorage.backup.Scheduler;


/**
 * Exist Scheduler for periodic backups
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
class ExistScheduler extends Scheduler {

    protected ExistScheduler(int intervalInHours) {
	super(intervalInHours);
    }
    
    /**{@inheritDoc} */
    @Override
    protected void doBackup() throws Exception {
	logger.info("Scheduled backup on going...");
	org.gcube.informationsystem.collector.impl.xmlstorage.exist.State.getDataManager().backup();
	logger.info("Scheduled backup completed");
    }

}
