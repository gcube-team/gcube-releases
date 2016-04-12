package org.gcube.dataanalysis.seadatanet;

class DivaFilePostResponse{
		Double obs_x0;
		Double obs_x1;
		Double obs_y0;
		Double obs_y1;
		Double obs_v0;
		Double obs_v1;

		Integer obs_count;

		String sessionid;

		public Double getObs_x0() {
			return obs_x0;
		}

		public void setObs_x0(Double obs_x0) {
			this.obs_x0 = obs_x0;
		}

		public Double getObs_x1() {
			return obs_x1;
		}

		public void setObs_x1(Double obs_x1) {
			this.obs_x1 = obs_x1;
		}

		public Double getObs_y0() {
			return obs_y0;
		}

		public void setObs_y0(Double obs_y0) {
			this.obs_y0 = obs_y0;
		}

		public Double getObs_y1() {
			return obs_y1;
		}

		public void setObs_y1(Double obs_y1) {
			this.obs_y1 = obs_y1;
		}

		public Double getObs_v0() {
			return obs_v0;
		}

		public void setObs_v0(Double obs_v0) {
			this.obs_v0 = obs_v0;
		}

		public Double getObs_v1() {
			return obs_v1;
		}

		public void setObs_v1(Double obs_v1) {
			this.obs_v1 = obs_v1;
		}

		public Integer getObs_count() {
			return obs_count;
		}

		public void setObs_count(Integer obs_count) {
			this.obs_count = obs_count;
		}

		public String getSessionid() {
			return sessionid;
		}

		public void setSessionid(String sessionid) {
			this.sessionid = sessionid;
		}

		public DivaFilePostResponse(Double obs_x0, Double obs_x1, Double obs_y0,
				Double obs_y1, Double obs_v0, Double obs_v1, Integer obs_count,
				String sessionid) {
			super();
			this.obs_x0 = obs_x0;
			this.obs_x1 = obs_x1;
			this.obs_y0 = obs_y0;
			this.obs_y1 = obs_y1;
			this.obs_v0 = obs_v0;
			this.obs_v1 = obs_v1;
			this.obs_count = obs_count;
			this.sessionid = sessionid;
		}

		@Override
		public String toString() {
			return "DivaFilePostResponse [obs_x0=" + obs_x0 + ", obs_x1="
					+ obs_x1 + ", obs_y0=" + obs_y0 + ", obs_y1=" + obs_y1
					+ ", obs_v0=" + obs_v0 + ", obs_v1=" + obs_v1
					+ ", obs_count=" + obs_count + ", sessionid=" + sessionid
					+ "]";
		}
		


	}