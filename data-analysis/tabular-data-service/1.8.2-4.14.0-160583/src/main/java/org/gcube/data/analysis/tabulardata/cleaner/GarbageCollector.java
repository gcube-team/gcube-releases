package org.gcube.data.analysis.tabulardata.cleaner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@Slf4j
public class GarbageCollector {

	private static final long timerPeriod = 600000; //10 minute 

	private CubeManager cubeManager;

	private Timer timer ;

	private GarbageCollectorTimer gbTimer = new GarbageCollectorTimer();

	protected GarbageCollector(CubeManager cubeManager){
		this.cubeManager = cubeManager;
		timer = new Timer("GarbageCollector",true);
		timer.schedule(gbTimer, timerPeriod, timerPeriod);
		log.trace("GarbageCollector timer Started");
	}

	public void addTablesToRemove(Set<TableId> tables){
		log.trace("table added for future removal : {}", tables);
		if (tables!=null && tables.size()>0)
			gbTimer.addTableToRemove(tables);

	}

	public void stop(){
		timer.cancel();
	}


	class GarbageCollectorTimer extends TimerTask{

		private List<TableId> tablesToRemove = new ArrayList<TableId>() ;
		
		@Override
		public void run() {
			log.trace("running GarbageCollector timer");
			removeTables();			
		}

		private void removeTables(){
			List<TableId> copyToRemove;
			synchronized (tablesToRemove) {
				copyToRemove =  new ArrayList<>(tablesToRemove);
			}

			Iterator<TableId> iterator = copyToRemove.iterator();
			List<TableId> alreadyRemoved = new ArrayList<TableId>();
			while (iterator.hasNext()){
				TableId tableId = iterator.next();
				if (tableId!=null)
					try{
						log.info("removing table "+tableId);
						Table table = cubeManager.getTable(tableId);
						if (table.contains(DatasetViewTableMetadata.class)){
							DatasetViewTableMetadata dvtm = table.getMetadata(DatasetViewTableMetadata.class);
							try{
								cubeManager.removeTable(dvtm.getTargetDatasetViewTableId());
							}catch (NoSuchTableException e) {}
						}
						cubeManager.removeTable(tableId);
						alreadyRemoved.add(tableId);
					}catch (NoSuchTableException e) {
						log.warn("table {} already removed",tableId);
						alreadyRemoved.add(tableId);
					}catch (Exception e) {
						log.warn("error removing table with id {}",tableId,e);
					}
			}
			synchronized (tablesToRemove) {
				tablesToRemove.removeAll(alreadyRemoved);
			}
		}

		public void addTableToRemove(Set<TableId> tables){
			synchronized (tablesToRemove) {
				tablesToRemove.addAll(tables);
			}
		}

		@Override
		public boolean cancel() {
			log.trace("cancelling GarbageCollector timer");
			removeTables();	
			return super.cancel();
		}

	}

}
