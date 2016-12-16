package org.gcube.data.streams.handlers;

import static org.gcube.data.streams.Utils.*;

import org.gcube.data.streams.exceptions.StreamContingency;

/**
 * A {@link FaultHandler} that silently absorbs {@link StreamContingency}s
 * but re-throws all other failures.
 * 
 * @author Fabio Simeoni
 *
 */
public class RethrowUnrecoverableHandler implements FaultHandler {

	@Override
	public void handle(RuntimeException failure) {
		
		if (!isContingency(failure))
			throw failure;
	}
}
