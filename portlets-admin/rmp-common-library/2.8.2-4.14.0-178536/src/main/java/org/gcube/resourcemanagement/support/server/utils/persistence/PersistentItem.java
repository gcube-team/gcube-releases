/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: PersistentItem.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.utils.persistence;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.gcube.resourcemanagement.support.server.utils.ServerConsole;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * Wrapping for data that can be persisted on the filesystem.
 * <pre>
 * new <b>PersistentItem</b><i>&lt;DataTypeToPersist&gt;</i>(fileToStore, refreshDelay) {
 *  <b>public void</b> onLoad() {
 *	 <i>//...</i>
 *  }
 *  <i>
 *  // The other methods to overload
 *  // ...
 *  </i>
 * }
 * </pre>
 * @author Daniele Strollo (ISTI-CNR)
 */
public abstract class PersistentItem<T extends Serializable>
implements PersistenceHandler<T> {
	protected static final String LOG_PREFIX = "[PERSISTENCE]";
	private long refreshDelay = -1;
	private boolean processClosed = false;
	private String persistenceFileName = null;
	private XStream serializer = null;

	/**
	 * @deprecated for internal use only.
	 */
	public PersistentItem() {
	}

	public PersistentItem(final String persistenceFileName) {
		this(persistenceFileName, -1);
	}

	public PersistentItem(final String persistenceFileName, final long refreshDelay) {
		this.setRefreshDelay(refreshDelay);
		this.setPersistenceFileName(persistenceFileName);

		this.onLoad();

		// loops to retrieve the data to persist
		if (this.refreshDelay > 0) {
			new Thread() {
				public void run() {
					while (!processClosed) {
						try {
							sleep(refreshDelay);
						} catch (final InterruptedException e) {
							ServerConsole.error(LOG_PREFIX, e);
						}
						// time elapsed... do refresh
						onRefresh();
					}
				}
			} .start();
		}
	}

	public PersistentItem(final T data, final String persistenceFileName, final long refreshDelay) {
		this(persistenceFileName, refreshDelay);
		this.setData(data);
	}

	protected final void setPersistenceFileName(final String persistenceFileName) {
		this.persistenceFileName = persistenceFileName;
	}

	protected final void setRefreshDelay(final long refreshDelay) {
		this.refreshDelay = refreshDelay;
	}

	private synchronized XStream getSerializer() {
		if (this.serializer == null) {
			StaxDriver driver = new StaxDriver();
			driver.setRepairingNamespace(false);
			this.serializer = new XStream(driver);
			this.serializer.addDefaultImplementation(Vector.class, List.class);
		}
		return this.serializer;
	}

	public final void destroy() {
		this.processClosed = true;
		// Thread has finished... invoking destroy
		onDestroy();
	}

	/**
	 * Returns the corresponding persistent data.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final T getData() {
		try {
			StringBuilder xml = new StringBuilder();
			BufferedReader reader = new BufferedReader(new FileReader(persistenceFileName));
			String currLine = null;

			while ((currLine = reader.readLine()) != null) {
				xml.append(currLine);
			}
			reader.close();
			return (T) this.getSerializer().fromXML(xml.toString());
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			return null;
		}
	}

	public final void setData(final T data) {
		String xmlData = this.getSerializer().toXML(data);

		BufferedOutputStream mine = null;
		try {
			mine = new BufferedOutputStream(new FileOutputStream(persistenceFileName));
			mine.write(xmlData.getBytes());
			//mine.flush();
			mine.close();
		} catch (Exception e) {
			ServerConsole.error(LOG_PREFIX, e);
			return;
		}
	}
}
