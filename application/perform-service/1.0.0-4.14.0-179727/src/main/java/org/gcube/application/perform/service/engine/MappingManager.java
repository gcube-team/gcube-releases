package org.gcube.application.perform.service.engine;

import java.sql.SQLException;

import org.gcube.application.perform.service.engine.model.BeanNotFound;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.InvalidRequestException;
import org.gcube.application.perform.service.engine.model.anagraphic.Batch;
import org.gcube.application.perform.service.engine.model.anagraphic.Farm;


public interface MappingManager {

	public Batch getBatch(DBQueryDescriptor desc) throws BeanNotFound, SQLException, InvalidRequestException, InternalException;
	public Farm getFarm(DBQueryDescriptor desc)throws BeanNotFound, SQLException,InvalidRequestException, InternalException;
	
}
