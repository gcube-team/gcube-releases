/**
 * 
 */
package org.gcube.data.tmf.impl;

import static java.text.DateFormat.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import org.gcube.data.tmf.api.Environment;
import org.gcube.data.tmf.api.Source;
import org.gcube.data.tmf.api.SourceEvent;
import org.gcube.data.tmf.api.SourceLifecycle;
import org.gcube.data.tmf.api.SourceNotifier;
import org.gcube.data.tmf.api.SourceReader;
import org.gcube.data.tmf.api.SourceWriter;
import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A partial implementation of {@link Source}.
 * 
 * @author Fabio Simeoni
 * 
 */
public abstract class AbstractSource implements Source {

	private final static long serialVersionUID = 1L;

	private final static Logger log = LoggerFactory
			.getLogger(AbstractSource.class);

	/** @serial the identifier of the source. */
	private final String identifier;
	/** @serial the name of the source. */
	private String name;
	/** @serial the description of the source. */
	private String description;
	/** @serial the type of the source. */
	private boolean isUser = true;

	/** @serial the cardinality of the source. */
	private Long cardinality;
	/** @serial the time of creation of the source. */
	private Calendar creationTime;
	/** @serial the time of last update of the source. */
	private Calendar lastUpdate;

	/** @serial collection lifetime callbacks. */
	private SourceLifecycle lifecycle;
	/** @serial the document reader associated with the source. */
	private SourceReader reader;
	/** @serial the document writer associated with the source. */
	private SourceWriter writer;

	/** @serial the context of the current call. */
	private Environment environment;

	transient private Thread shutdownhook;
	
	private SourceNotifier notifier;

	/**
	 * Creates an instance with a given identifier.
	 * 
	 * @param id
	 *            the identifier
	 * @throws InvalidRequestException
	 *             if the identifier is <code>null</code>
	 */
	public AbstractSource(String id) throws InvalidRequestException {

		if (id == null)
			throw new InvalidRequestException("source identifier is null");

		identifier = id;
	}

	/** {@inheritDoc} */
	@Override
	public String id() {
		return identifier;
	}

	/** {@inheritDoc} */
	@Override
	public String name() {
		return name;
	}

	/**
	 * Sets the name of the source.
	 * 
	 * @param name
	 *            the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** {@inheritDoc} */
	@Override
	public String description() {
		return description;
	}

	/**
	 * Sets a free-form description for the source.
	 * 
	 * @param description
	 *            the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/** {@inheritDoc} */
	@Override
	public Calendar creationTime() {
		return creationTime;
	}

	/**
	 * Sets the creation time of the source.
	 * 
	 * @param time
	 *            the creation time
	 */
	public Calendar setCreationTime(Calendar time) {
		return creationTime = time;
	}

	/**
	 * Marks the source as a user or as a system source.
	 * 
	 * @param isUser
	 *            <code>true<code> if the source is a user source, <code>false</code>
	 *            otherwise
	 */
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isUser() {
		return isUser;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized Long cardinality() {
		return cardinality;
	}

	/**
	 * Sets the cardinality of the source, changing automatically the time of
	 * last modification of the source.
	 * 
	 * @param cardinality
	 *            the cardinality
	 * 
	 * @see #setLastUpdate(Calendar)
	 */
	public synchronized void setCardinality(Long cardinality) {
		if (this.cardinality != cardinality)
			this.cardinality = cardinality;
	}

	/** {@inheritDoc} */
	@Override
	public synchronized Calendar lastUpdate() {
		return lastUpdate;
	}

	/**
	 * Sets the time in which the source was last modified.
	 * <p>
	 * An actual change in the value of this property notifies the service of a
	 * {@link SourceEvent#CHANGE} event.
	 * 
	 * @param time
	 *            the last modification time
	 */
	public synchronized void setLastUpdate(Calendar time) {

		// must be a real change and not the first non-null value
		if (lastUpdate != null && !time.equals(lastUpdate)) {

			// notify consumers
			notifier.notify(SourceEvent.CHANGE);
		}
		
		lastUpdate = time;
	}

	/** {@inheritDoc} */
	@Override
	public SourceLifecycle lifecycle() {
		return lifecycle;
	}

	/**
	 * Sets the {@link SourceLifecycle}.
	 * 
	 * @param lifecycle
	 *            the lifecycle
	 */
	public void setLifecycle(SourceLifecycle lifecycle) {
		this.lifecycle = lifecycle;
		installShutDownHook();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SourceReader reader() {
		return reader;
	}

	/**
	 * Sets the {@link SourceReader}
	 * 
	 * @param reader
	 *            the reader
	 */
	public void setReader(SourceReader reader) {
		log.trace("setting reader to {} on {}", reader, this);
		this.reader = reader;
	}

	/**
	 * Sets the {@link SourceWriter}
	 * 
	 * @param writer
	 *            the writer
	 */
	public void setWriter(SourceWriter writer) {
		log.trace("setting writer to {} on {}", writer, this);
		this.writer = writer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SourceWriter writer() {
		return writer;
	}

	/**
	 * {@inheritDoc}
	 **/
	public void setNotifier(SourceNotifier notifier) {
		this.notifier = notifier;
	}

	/**
	 * {@inheritDoc}
	 **/
	@Override
	public SourceNotifier notifier() {
		return notifier;
	}

	/** {@inheritDoc} */
	public Environment environment() {
		return environment;
	}

	/** {@inheritDoc} */
	public void setEnvironment(Environment env) {
		environment = env;
	}

	/**
	 * @serialData the {@link SourceLifecycle}, the {@link SourceReader} and the
	 *             {@link SourceWriter}.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	// invoked upon deserialisation, resets non-serializable defaults
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		in.defaultReadObject();

		// check invariants
		if (lifecycle() == null)
			throw new IOException(
					"invalid serialisation, missing source lifecycle");
		
		installShutDownHook();

	}

	private void installShutDownHook() {
		log.trace("installing shutdown hook for "+id());
		shutdownhook = new Thread(new ShutdownHook());
		Runtime.getRuntime().addShutdownHook(shutdownhook);
	}

	@Override
	public String toString() {
		return "[identifier="
				+ identifier
				+ ",name="
				+ name
				+ ", description="
				+ description
				+", types="
				+ types()
				+ ", creationTime="
				+ (creationTime == null ? creationTime : getInstance().format(
						creationTime.getTime()))
				+ ", isUser="
				+ isUser
				+ ", cardinality="
				+ cardinality
				+ ", lastUpdate="
				+ (lastUpdate == null ? lastUpdate : getInstance().format(
						lastUpdate.getTime())) + ", lifecycle=" + lifecycle
				+ ", reader=" + reader + ", writer=" + writer + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cardinality ^ (cardinality >>> 32));
		result = prime * result
				+ ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + (isUser ? 1231 : 1237);
		result = prime * result
				+ ((lastUpdate == null) ? 0 : lastUpdate.hashCode());
		result = prime * result
				+ ((lifecycle == null) ? 0 : lifecycle.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((reader == null) ? 0 : reader.hashCode());
		result = prime * result + ((writer == null) ? 0 : writer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractSource))
			return false;
		AbstractSource other = (AbstractSource) obj;
		if (cardinality != other.cardinality)
			return false;
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (!creationTime.equals(other.creationTime))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (isUser != other.isUser)
			return false;
		if (lastUpdate == null) {
			if (other.lastUpdate != null)
				return false;
		} else if (!lastUpdate.equals(other.lastUpdate))
			return false;
		if (lifecycle == null) {
			if (other.lifecycle != null)
				return false;
		} else if (!lifecycle.equals(other.lifecycle))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (reader == null) {
			if (other.reader != null)
				return false;
		} else if (!reader.equals(other.reader))
			return false;
		if (writer == null) {
			if (other.writer != null)
				return false;
		} else if (!writer.equals(other.writer))
			return false;
		return true;
	}

	class ShutdownHook implements Runnable {
		/** {@inheritDoc} */
		@Override
		public void run() {
			lifecycle().stop();
		}
	}
}
