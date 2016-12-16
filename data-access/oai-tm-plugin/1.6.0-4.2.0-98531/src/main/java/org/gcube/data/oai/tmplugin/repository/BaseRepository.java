package org.gcube.data.oai.tmplugin.repository;

import static org.gcube.data.streams.dsl.Streams.pipe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.common.Harvester;
import org.gcube.common.data.Record;
import org.gcube.common.data.RecordIterator;
import org.gcube.common.repository.Identify;
import org.gcube.common.repository.Set;
import org.gcube.data.oai.tmplugin.binders.OAIDCBinder;
import org.gcube.data.oai.tmplugin.repository.iterators.RepositoryIterator;
import org.gcube.data.oai.tmplugin.repository.iterators.SetIterator;
import org.gcube.data.oai.tmplugin.requests.Request;
import org.gcube.data.oai.tmplugin.requests.WrapSetsRequest;
import org.gcube.data.oai.tmplugin.utils.Constants;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.exceptions.StreamException;
import org.gcube.data.streams.generators.Generator;
import org.gcube.data.tmf.api.exceptions.UnknownTreeException;
import org.gcube.data.trees.data.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link Repository} implementation
 * 
 */
public class BaseRepository implements Repository{

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BaseRepository.class);

	transient private Harvester connection;

	private final String url;
	private final String metadataFormat;
	transient private String description;
	transient private String name;
	private boolean wrapSetsRequest;

	private OAIDCBinder binder;


	// sees repository as a whole, no set boundaries
	public BaseRepository(Request request) throws Exception {
		if (request instanceof WrapSetsRequest)
			wrapSetsRequest = true;
		else
			wrapSetsRequest = false;

		this.url = request.getRepositoryUrl();
		this.metadataFormat = request.getMetadataFormat();
		this.description = request.getDescription();
		this.name = request.getName();
		//		log.info("url: " + this.url + " - metadataFormat: " + this.metadataFormat);

		if (connect(this.url))
			binder = new OAIDCBinder(request);
	}

	/**
	 * {@inheritDoc}
	 */
	public String url() {
		return url;
	}

	/**
	 * {@inheritDoc}
	 */
	public String metadataFormat() {
		return metadataFormat;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String description() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tree get(String id, List<org.gcube.data.oai.tmplugin.repository.Set> sets) throws UnknownTreeException, Exception {
		log.info("get id: " + id + " - sets: " + sets.toString());
		try {

			Record record=connection.getRecord(id,metadataFormat);

			//check record is in one of sets
			if (!sets.isEmpty()) {

				//check membership to set
				boolean inSomeSet= false;
				loop: 
					for (int i=0; i<record.getHeader().getSpecList().size(); i++)
						for (org.gcube.data.oai.tmplugin.repository.Set set: sets)
							if(record.getHeader().getSpecList().get(i).equals(set.id())){
								inSomeSet= true;
								break loop;
							}

				if(!inSomeSet) 
					throw new UnknownTreeException();

			}
			//			log.info("record.getIdentifier() " + record.getIdentifier());
			return binder.bind(record);
		}
		catch(Exception e) {
			throw new UnknownTreeException(id,e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stream<Tree> getAllIn(List<org.gcube.data.oai.tmplugin.repository.Set> sets) {

		Stream<Record> records = null;

		if (sets.isEmpty()){

			records = new RepositoryIterator() {
				@Override 
				protected RecordIterator fetchRecords() throws FileNotFoundException, Exception{
					log.info("getAllIn - RepositoryIterator, sets Empty ");
					return connection.listRecords(metadataFormat);
				}


				@Override
				public boolean isClosed() {
					// TODO Auto-generated method stub
					return false;
				}			
			};
		}

		else
			records = new SetIterator(sets) {
			@Override 
			protected RecordIterator fetchRecords(org.gcube.data.oai.tmplugin.repository.Set set) throws Exception{
				log.info("getAllIn - SetIterator on set id: " + set.id());
				return connection.listRecords(null, null, set.id(), metadataFormat);
			}

			@Override
			public boolean isClosed() {
				// TODO Auto-generated method stub
				return false;
			}
		};


		Generator<Record,Tree> parser = new Generator<Record,Tree>() {
			public Tree yield(Record record) {
				try {					
					return binder.bind(record);
				}
				catch(Exception e) {
					//					throw new StreamSkipException();
					throw new StreamException();
				}

			}
		};

		return pipe(records).through(parser);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Summary summary(List<org.gcube.data.oai.tmplugin.repository.Set> sets) throws Exception {

		Stream<Record> records = null;

		if (sets == null || sets.isEmpty()){

			records = new RepositoryIterator() {
				@Override 
				protected RecordIterator fetchRecords() throws FileNotFoundException, Exception{
					log.info("summary RepositoryIterator, no set ");
					log.info("fetchRecords sets.isEmpty");
					return connection.listRecords(metadataFormat);
				}


				@Override
				public boolean isClosed() {
					// TODO Auto-generated method stub
					return false;
				}			
			};
		}

		else
			records = new SetIterator(sets) {
			@Override 
			protected RecordIterator fetchRecords(org.gcube.data.oai.tmplugin.repository.Set set) throws Exception{
				log.info("summary SetIterator on set id: " + set.id());
				return connection.listRecords(null, null, set.id(), metadataFormat);
			}

			@Override
			public boolean isClosed() {
				// TODO Auto-generated method stub
				return false;
			}
		};

		Calendar lastUpdate = null;
		long cardinality = 0;

		while (records.hasNext()){
			try {
				Record record= records.next();
				if (record==null || record.getMetadata()==null || record.IsDeleted())
					continue;
				cardinality++;
				Calendar tempUpdate= null;
				String datastamp = null;
				try{
					datastamp = record.getHeader().getDatestamp();
					if (datastamp!=null){
						tempUpdate = Constants.getDate(datastamp);
						if (lastUpdate==null || tempUpdate.after(lastUpdate)) 
							lastUpdate= tempUpdate;
					}
				}catch (Exception e) {
					log.error("Error getting last update ", e);
				}
			}
			catch(Exception e) {
				log.error("could not count record",e);
			}
		}

		//attempts to recognise retrieval failures
		if(lastUpdate==null)
			throw new Exception();

		return new Summary(lastUpdate, cardinality);		
	}



	// used internally to establish a connection at instantiation and load time
	private Boolean connect(String url) throws Exception {

		log.info("connecting to repository @ " + url);
		try {
			connection = new Harvester(url);
			if (connection != null){
				Identify identify = connection.identify();

				name = identify.getRepositoryName();

				if (name!=null)
					description = name;
				else
					log.info("0 records");

			}	

		} catch (Exception e) {
			//			System.out.println("exc");
			log.error("could not connect to repository @ " + url);
			//			throw new Exception("could not connect to repository @ " + url, e);
			return false;
		}

		return true;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<org.gcube.data.oai.tmplugin.repository.Set> getSetsWith(List<String> setIds) {

		log.info("getSetsWith " + setIds.toString());
		boolean emptySetsList = setIds.isEmpty();

		log.info(emptySetsList ? "retrieving all sets in repository " + this.url : "retrieving sets {} in repository " + this.url, setIds);
		List<org.gcube.data.oai.tmplugin.repository.Set> output = new ArrayList<org.gcube.data.oai.tmplugin.repository.Set>();

		if (!emptySetsList || wrapSetsRequest){

			List<String> done = new ArrayList<String>(setIds);
			try {

				List<Set> OAISets = connection.listSets(); // remote connection

				for (Set set : OAISets) {

					//				log.info(set.getSetName() + " " + set.getSetSpec() + " " + set.getSetDescription());

					String setId = set.getSetSpec();
					//				log.info(setId);
					// create only if client did not specify sets or specified this
					// set
					if (emptySetsList || done.contains(setId)) {
						String description = set.getSetName()!="" ? set
								.getSetName() : null;
								output.add(new org.gcube.data.oai.tmplugin.repository.Set(setId, set.getSetName(), description));

								done.remove(setId);

								if (!emptySetsList && done.isEmpty())
									break;
					}

				}

			} catch (Exception e) {
				log.error("could not process sets in repository " + this.url, e);
				throw new RuntimeException("could not process sets in repository " + this.url, e);
			}
			if (!done.isEmpty()){
				log.error("unkwnon sets " + done + " in repository " + this.url);
				throw new RuntimeException("unkwnon sets " + done + " in repository " + this.url);
			}
		}


		return output;
	}



	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	// invoked upon deserialisation, resets non-serializable defaults
	private void readObject(java.io.ObjectInputStream in) throws IOException,
	ClassNotFoundException {

		in.defaultReadObject();

		// check invariants
		if (url == null)
			throw new IOException(
					"invalid serialisation, missing respository url");

		// recreate connection connection
		try {
			connect(url);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
