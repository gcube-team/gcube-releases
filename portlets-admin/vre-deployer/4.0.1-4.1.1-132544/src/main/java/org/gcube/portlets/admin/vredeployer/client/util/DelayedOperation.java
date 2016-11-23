package org.gcube.portlets.admin.vredeployer.client.util;

import com.google.gwt.user.client.Timer;

public abstract class DelayedOperation {
	public final void start(final int delayMills) {
		Timer t = new Timer() {
			@Override
			public void run() {
				doJob();
			}
		};
		t.schedule(delayMills);
	}

	public final void loop(final int delayMills) {
		Timer t = new Timer() {
			@Override
			public void run() {
				doJob();
				this.schedule(delayMills);
			}
		};
		t.schedule(delayMills);
	}

	public abstract void doJob();
}
