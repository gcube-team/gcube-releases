package org.gcube.resourcemanagement.support.server.utils.persistence;

import java.io.File;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;
import org.gcube.resourcemanagement.support.server.utils.ServerConsole;

public class TestPersistence {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		PersistentItem<ScopeBean[]> persistentScopes = new PersistentItem<ScopeBean[]>("data.xml", 10000) {
			// Builds the data to persist
			private void refreshData() {
				try {
					ScopeBean[] toStore =
						ScopeManager.getAvailableScopes().values().toArray(new ScopeBean[]{});
					this.setData(toStore);
				} catch (Exception e) {
					ServerConsole.error(LOG_PREFIX, e);
				}
			}

			public void onLoad() {
				ScopeManager.setScopeConfigFile("test-suite" + File.separator + "scopes" + File.separator + "scopedata_admin.xml");

				this.refreshData();
			}
			public void onRefresh() {
				this.refreshData();

				ScopeBean[] scopes = this.getData();
				System.out.println(scopes);
			}
			public void onDestroy() {
				this.setData(null);
			}
		};


	}

}
