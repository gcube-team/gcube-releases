package org.gcube.data.streams.publishers;

import gr.uoa.di.madgik.grs.proxy.IWriterProxy;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPWriterProxy;

/**
 * The transport used by a {@link RsPublisher}.
 * 
 * @author Fabio Simeoni
 *
 */
public enum RsTransport {

	TCP() {
		IWriterProxy proxy() {
			return new TCPWriterProxy();
		}
	},
	LOCAL {
		@Override
		IWriterProxy proxy() {
			return new LocalWriterProxy();
		}
	};
	
	abstract IWriterProxy proxy();
	
}
