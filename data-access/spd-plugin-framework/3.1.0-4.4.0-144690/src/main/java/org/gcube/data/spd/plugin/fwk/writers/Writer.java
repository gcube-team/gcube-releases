package org.gcube.data.spd.plugin.fwk.writers;



import org.gcube.data.spd.model.exceptions.InvalidRecordException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.model.exceptions.WrapperAlreadyDisposedException;
import org.gcube.data.spd.plugin.fwk.writers.rswrapper.AbstractWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Writer<F> extends AbstractWriter<F>{

	private static final Logger logger = LoggerFactory.getLogger(Writer.class);
		
	private WriterManager<F> manager = new DefaultWriter();
	
		
	public Writer(AbstractWrapper<F> wrapper) {
		super(wrapper);
	}
	
	public Writer(AbstractWrapper<F> wrapper, WriterManager<F> manager) {
		super(wrapper);
		this.manager = manager;
	}

	public class DefaultWriter extends WriterManager<F>{}


	@Override
	public synchronized boolean write(F t) {
		if(this.getWrapper().isClosed()) return false;
		if (manager.filter(t)){
			try{
				getWrapper().add(manager.enrich(t));
			}catch (InvalidRecordException e) {
				logger.warn("error putting a result in the Writer",e);
				return false;
			}catch (WrapperAlreadyDisposedException e) {
				logger.warn("wrapper already disposed",e);
				return false;
			}
		} else{
			logger.debug("a result has been filtered");
			return false;
		}
		wrote++;
		return true;
	}

	@Override
	public synchronized boolean write(StreamException error) {
		try{
			getWrapper().add(error);
		}catch (InvalidRecordException e) {
			logger.warn("error putting execption in the Writer",e);
			return false;
		}catch (WrapperAlreadyDisposedException e) {
			logger.warn("wrapper already disposed",e);
			return false;
		}
		if (error instanceof StreamBlockingException ){
			getWrapper().close();
			return false;
		}
		return true;
	}

	@Override
	public boolean isAlive() {
		return !getWrapper().isClosed();
	}
	
	

}
