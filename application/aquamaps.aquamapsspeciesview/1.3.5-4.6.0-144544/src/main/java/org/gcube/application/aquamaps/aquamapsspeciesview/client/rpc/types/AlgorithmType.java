package org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.types;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum AlgorithmType implements IsSerializable{

	NativeRange,SuitableRange,NativeRange2050,SuitableRange2050,HSPENRegeneration,
	LINEAR,PARABOLIC;
	private AlgorithmType() {
		// TODO Auto-generated constructor stub
	}
}

