package org.gcube.data.spd.client.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.again;
import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.pipe;
import static org.gcube.data.streams.dsl.Streams.publishStringsIn;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import java.net.URI;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.ResultGenerator;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.stubs.ClassificationStub;
import org.gcube.data.spd.stubs.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedPluginException;
import org.gcube.data.streams.Stream;

public class DefaultClassification implements Classification{

	private final ProxyDelegate<ClassificationStub> delegate;
	
	private final ResultGenerator<TaxonomyItem> taxonItemGenerator = new ResultGenerator<TaxonomyItem>();
	
		
	public DefaultClassification(ProxyDelegate<ClassificationStub> config){
		this.delegate = config;
	}

	@Override
	public Stream<TaxonomyItem> getTaxonChildrenById(final String id)
			throws UnsupportedPluginException, UnsupportedCapabilityException,
			InvalidIdentifierException {
		Call<ClassificationStub, URI> call = new Call<ClassificationStub, URI>() {
			@Override
			public URI call(ClassificationStub classification) throws Exception {
				return new URI(classification.retrieveTaxonChildrenByTaxonId(id));
			}
		};
		
		try {
			return pipe(convert(delegate.make(call)).of(GenericRecord.class).withDefaults()).through(taxonItemGenerator);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public Stream<TaxonomyItem> getTaxaByIds(Stream<String> ids) {
		final String idsLocator = publishStringsIn(ids).withDefaults().toString();
		Call<ClassificationStub, URI> call = new Call<ClassificationStub, URI>() {
			@Override
			public URI call(ClassificationStub classification) throws Exception {
				return new URI(classification.getTaxaByIds(idsLocator));
			}
		};
		
		try {
			return pipe(convert(delegate.make(call)).of(GenericRecord.class).withDefaults()).through(taxonItemGenerator);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public Stream<TaxonomyItem> getTaxonTreeById(final String id)
			throws UnsupportedPluginException, UnsupportedCapabilityException,
			InvalidIdentifierException {
		Call<ClassificationStub, URI> call = new Call<ClassificationStub, URI>() {
			@Override
			public URI call(ClassificationStub classification) throws Exception {
				return new URI(classification.retrieveChildrenTreeById(id));
			}
		};
		
		try {
			return pipe(convert(delegate.make(call)).of(GenericRecord.class).withDefaults()).through(taxonItemGenerator);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public Stream<TaxonomyItem> getSynonymsById(final String id)
			throws UnsupportedPluginException, UnsupportedCapabilityException,
			InvalidIdentifierException {
		Call<ClassificationStub, URI> call = new Call<ClassificationStub, URI>() {
			@Override
			public URI call(ClassificationStub classification) throws Exception {
				return new URI(classification.retrieveSynonymsById(id));
			}
		};
		
		try {
			return pipe(convert(delegate.make(call)).of(GenericRecord.class).withDefaults()).through(taxonItemGenerator);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
		
	}

}
