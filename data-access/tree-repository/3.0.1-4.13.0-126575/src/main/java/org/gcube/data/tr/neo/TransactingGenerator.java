package org.gcube.data.tr.neo;

import org.gcube.data.streams.delegates.StreamListener;
import org.gcube.data.streams.generators.Generator;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactingGenerator<FROM,TO> implements Generator<FROM,TO>,StreamListener {
		
	private static Logger log = LoggerFactory.getLogger(TransactingGenerator.class);
	
	private static final int BATCH_TRANSACTION_SIZE = 2;

	final GraphDatabaseService db;
	final Generator<FROM,TO> generator;
	final String name;
		
	private long count=0;
		
	Transaction transaction;
		
	public TransactingGenerator(GraphDatabaseService db,Generator<FROM,TO> filter,String name) {
		this.generator=filter;
		this.db=db;
		this.name=name;
	}
		
	@Override
	public void onStart() {
		log.trace("starting transaction "+name);
		transaction= db.beginTx();
	}
		
	public TO yield(FROM element) {
		
		TO result = generator.yield(element);
		
		count++;
		
		//restart transaction
		if (count % BATCH_TRANSACTION_SIZE == 0) {
			//log.trace(name+" is restarting after "+ BATCH_TRANSACTION_SIZE + "(" + count+ " processed so far)");
			transaction.success();
			transaction.finish();
			transaction = db.beginTx();
		}
		
		return result;
		

	};
		
	@Override
	public void onEnd() {
		
		log.trace("closing transaction "+name);
		
		//close up last transaction
		if (transaction!=null) {
			transaction.success();
			transaction.finish();
		}
	}
	
	@Override
	public void onClose() {
	}
		
}