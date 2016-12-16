package org.gcube.data.streams.handlers;

import static org.gcube.data.streams.Utils.*;

import org.gcube.data.streams.exceptions.StreamContingency;

/**
 * A {@link FaultHandler} that silently absorbs {@link StreamContingency}s
 * and stops iteration at the first unrecoverable failure.
 * 
 * @author Fabio Simeoni
 *
 */
public class StopUnrecoverableHandler implements FaultHandler {

	@Override
	public void handle(RuntimeException failure) {

		if (!isContingency(failure))
			iteration.stop();
	}
}
