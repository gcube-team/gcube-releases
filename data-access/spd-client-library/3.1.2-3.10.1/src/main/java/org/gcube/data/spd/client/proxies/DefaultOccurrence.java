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
import org.gcube.data.spd.model.PointInfo;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.stubs.OccurrenceStub;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamSkipSignal;
import org.gcube.data.streams.generators.Generator;

public class DefaultOccurrence implements Occurrence {

	private final ProxyDelegate<OccurrenceStub> delegate;
	
	private final ResultGenerator<OccurrencePoint> occurrencesGenerator = new ResultGenerator<OccurrencePoint>();
	
	public DefaultOccurrence(ProxyDelegate<OccurrenceStub> config){
		this.delegate = config;
	}
	
	@Override
	public Stream<OccurrencePoint> getByIds(Stream<String> ids) {
		final String idsLocator = publishStringsIn(ids).withDefaults().toString();
		Call<OccurrenceStub, URI> call = new Call<OccurrenceStub, URI>() {
			@Override
			public URI call(OccurrenceStub occurrence) throws Exception {
				return new URI(occurrence.getByIds(idsLocator));
			}
		};
		try {
			return pipe(convert(delegate.make(call)).of(GenericRecord.class).withDefaults()).through(occurrencesGenerator);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public Stream<OccurrencePoint> getByKeys(Stream<String> keys) {
		final String idsLocator = publishStringsIn(keys).withDefaults().toString();
		Call<OccurrenceStub, URI> call = new Call<OccurrenceStub, URI>() {
			@Override
			public URI call(OccurrenceStub occurrence) throws Exception {
				return new URI(occurrence.getByKeys(idsLocator));
			}
		};
		try {
			return pipe(convert(delegate.make(call)).of(GenericRecord.class).withDefaults()).through(occurrencesGenerator);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}
	}
	
	@Override
	public String createLayer(Stream<PointInfo> coordinatesStream) {
		Stream<String> convertedStream = pipe(coordinatesStream).through(new Generator<PointInfo, String>() {

			@Override
			public String yield(PointInfo element) {
				try {
					return Bindings.toXml(element);
				} catch (Exception e) {
					throw new StreamSkipSignal();
				}
			}
			
		});
		
		final String coordinateLocator = publishStringsIn(convertedStream).withDefaults().toString();
		
		Call<OccurrenceStub, String> call = new Call<OccurrenceStub, String>() {
			@Override
			public String call(OccurrenceStub occurrence) throws Exception {
				return occurrence.createLayer(coordinateLocator);
			}
		};
		
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw again(e).asServiceException();
		}	
	}


}
