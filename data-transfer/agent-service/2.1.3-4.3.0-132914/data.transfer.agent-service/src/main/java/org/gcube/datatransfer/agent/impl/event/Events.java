package org.gcube.datatransfer.agent.impl.event;

import org.gcube.common.core.utils.events.GCUBETopic;

public class Events {

	public static  enum TransferTopics implements GCUBETopic {TRANSFER_START,TRANSFER_END,TRANSFER_FAIL,TRANSFER_CANCEL};
}