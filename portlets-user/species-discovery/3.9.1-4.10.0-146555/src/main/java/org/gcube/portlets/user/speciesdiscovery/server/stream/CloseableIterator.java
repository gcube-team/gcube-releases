/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.server.stream;

import java.io.Closeable;
import java.util.Iterator;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface CloseableIterator<E> extends Iterator<E>, Closeable {

}
