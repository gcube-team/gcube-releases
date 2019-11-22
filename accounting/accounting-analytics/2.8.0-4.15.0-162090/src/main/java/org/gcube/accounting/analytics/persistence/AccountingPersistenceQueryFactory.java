/**
 * 
 */
package org.gcube.accounting.analytics.persistence;

/**
 * @author Luca Frosini (ISTI - CNR)
 *
 */
public class AccountingPersistenceQueryFactory {
	
	private static final InheritableThreadLocal<String> forcedQueryScope = new InheritableThreadLocal<String>() {
		
		@Override
		protected String initialValue() {
			return null;
		}
		
	};
	
	/**
	 * Used to force the query in a certain scope without changing the current effective scope.
	 * Please note that is responsibility of the AccountingPersistenceBackendQuery implementation
	 * use the scope to query. 
	 * The facility method AccountingPersistenceBackendQuery.getScopeToQuery() has been also created
	 */
	public static InheritableThreadLocal<String> getForcedQueryScope() {
		return forcedQueryScope;
	}
	
	public static AccountingPersistenceQuery getInstance() {
		return AccountingPersistenceQuery.getInstance();
	}

	
}
