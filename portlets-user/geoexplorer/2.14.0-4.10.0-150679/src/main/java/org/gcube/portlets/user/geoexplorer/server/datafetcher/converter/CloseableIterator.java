/**
 * 
 */
package org.gcube.portlets.user.geoexplorer.server.datafetcher.converter;

import java.io.Closeable;
import java.util.Iterator;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface CloseableIterator<E> extends Iterator<E>, Closeable {

}
