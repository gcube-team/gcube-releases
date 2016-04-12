package org.gcube.portlets.admin.wfdocslibrary.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ForwardActionWithDest implements Serializable {
		private ForwardAction fwa;
		private String toStepLabel;
		
		public ForwardActionWithDest() { }

		public ForwardActionWithDest(ForwardAction fwa, String toStepLabel) {
			this.fwa = fwa;
			this.toStepLabel = toStepLabel;
		}

		public ForwardAction getFwAction() {
			return fwa;
		}

		public void setFwa(ForwardAction fwa) {
			this.fwa = fwa;
		}

		public String getToStepLabel() {
			return toStepLabel;
		}

		public void setToStepLabel(String toStepLabel) {
			this.toStepLabel = toStepLabel;
		}	
}
