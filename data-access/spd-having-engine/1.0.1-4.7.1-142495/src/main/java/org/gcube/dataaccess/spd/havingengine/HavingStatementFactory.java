/**
 * 
 */
package org.gcube.dataaccess.spd.havingengine;

/**
 * {@link HavingStatement} factory.
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 */
public interface HavingStatementFactory {
	
	
	/**
	 * Compile the passed expression for the target class.
	 * @param expression the expression to compile.
	 * @return the compiled statement.
	 * @throws Exception if an error occurs during the compilation.
	 */
	public abstract <T> HavingStatement<T> compile(String expression) throws Exception;

}
