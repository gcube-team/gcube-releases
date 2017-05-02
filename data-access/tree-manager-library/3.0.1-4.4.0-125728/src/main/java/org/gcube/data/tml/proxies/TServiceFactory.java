package org.gcube.data.tml.proxies;

import static org.gcube.data.tml.Constants.*;

import java.net.URI;

import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.fw.builders.SingletonBuilder;
import org.gcube.common.clients.fw.builders.SingletonBuilderImpl;
import org.gcube.common.clients.fw.builders.StatefulBuilder;
import org.gcube.common.clients.fw.builders.StatefulBuilderImpl;
import org.gcube.common.clients.fw.queries.StatefulQuery;
import org.gcube.data.tml.plugins.TBinderPlugin;
import org.gcube.data.tml.plugins.TReaderPlugin;
import org.gcube.data.tml.plugins.TWriterPlugin;
import org.gcube.data.tml.stubs.TBinderStub;
import org.gcube.data.tml.stubs.TReaderStub;
import org.gcube.data.tml.stubs.TWriterStub;

/**
 * Factory methods for {@link TBinder}s, {@link TReader}s, and {@link TWriter}s and related queries.
 * 
 * @author Fabio Simeoni
 *
 */
public class TServiceFactory {

	/**
	 * The {@link TBinderPlugin}.
	 */
	public static final TBinderPlugin binderPlugin = new TBinderPlugin();
	
	/**
	 * The {@link TReaderPlugin}.
	 */
	public static final TReaderPlugin readerPlugin = new TReaderPlugin();
	
	/**
	 * The {@link TWriterPlugin}.
	 */
	public static final TWriterPlugin writerPlugin = new TWriterPlugin();
	
	/**
	 * Returns a {@link TBinder} builder.
	 * @return the the builder
	 */
	public static SingletonBuilder<? extends TBinder> binder() {
		return new SingletonBuilderImpl<TBinderStub,DefaultTBinder>(binderPlugin);
	}

	/**
	 * Returns an {@link AsyncTBinder} builder.
	 * @return the builder
	 */
	public static SingletonBuilder<? extends AsyncTBinder> async_binder() {
		Property<Long> infinite_timeout = Property.timeout(0);
		return new SingletonBuilderImpl<TBinderStub,DefaultTBinder>(binderPlugin,infinite_timeout);
	}
	
	/**
	 * Returns a {@link TReader} builder.
	 * @return the builder
	 */
	public static StatefulBuilder<TReader> reader() {
		return new StatefulBuilderImpl<TReaderStub,TReader>(readerPlugin);
	}
	
	/**
	 * Returns a {@link TReader} builder.
	 * @return the builder
	 */
	public static StatefulBuilder<TWriter> writer() {
		return new StatefulBuilderImpl<TWriterStub,TWriter>(writerPlugin);
	}
	
	/**
	 * Returns a query for {@link TBinder} endpoints with a given plugin.
	 * @param name the name of the plugin
	 * @return the query
	 */
	public static StatefulQuery plugin(final String name) {
		
		StatefulQuery query = new StatefulQuery(binderPlugin);
		query.addNamespace("tm",URI.create(namespace));
		query.addCondition("$resource/Data/tm:Plugin/name/text() eq '"+name+"'");
		
		return query;
	}
	
	/**
	 * Returns a builder for a query for {@link TReader} instances for a given source.
	 * @return the query
	 */
	public static SourceQueryBuilder readSource() {

		StatefulQuery query = new StatefulQuery(readerPlugin);
		return new SourceQueryBuilder(query);
		
	}
	
	/**
	 * Returns a builder for a query for {@link TWriter} instances for a given source.
	 * @return the query
	 */
	public static SourceQueryBuilder writeSource() {
		
		StatefulQuery query = new StatefulQuery(writerPlugin);
		return new SourceQueryBuilder(query);
	}
	
}
