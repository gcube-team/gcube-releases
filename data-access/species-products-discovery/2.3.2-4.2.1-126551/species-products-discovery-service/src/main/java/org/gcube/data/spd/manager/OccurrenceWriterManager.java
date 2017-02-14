package org.gcube.data.spd.manager;

import java.util.HashSet;
import java.util.List;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.ValidatorFactory;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.util.Util;
import org.gcube.data.spd.plugin.fwk.writers.ResultElementWriterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceWriterManager extends
		ResultElementWriterManager<OccurrencePoint> {

	private Logger logger = LoggerFactory.getLogger(OccurrenceWriterManager.class);
	
	private HashSet<String> idsSet;
	
	public OccurrenceWriterManager(String provider) {
		super(provider);
		this.idsSet = new HashSet<String>(400);
	}

	@Override
	protected OccurrencePoint _enrich(OccurrencePoint t) {
		t.setProvider(provider);
		t.setId(Util.keyEnrichment(this.provider, t.getId()));
		return t;
	}

	@Override
	public boolean filter(OccurrencePoint obj) {
		
		if (obj ==null){
			logger.trace("("+this.provider+") object null discarded ");
			return false;
		}
		
		List<ValidationError> errors = ValidatorFactory.validator().validate(obj);
		
		if (errors.size()>0){
			logger.warn("("+this.provider+") object discarded for the following reasons: "+errors);
			return false;
		}
		
		if (this.idsSet.contains(obj.getId())){
			logger.trace("("+this.provider+") an item with id "+obj.getId()+" already found");
			return false;
		}
		else {
			this.idsSet.add(obj.getId());
			return true;
		}
	}

}
