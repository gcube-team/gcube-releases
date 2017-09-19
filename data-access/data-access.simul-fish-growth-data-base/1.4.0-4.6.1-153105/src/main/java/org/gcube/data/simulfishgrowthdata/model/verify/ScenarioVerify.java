package org.gcube.data.simulfishgrowthdata.model.verify;

import org.apache.commons.lang.StringUtils;

import gr.i2s.fishgrowth.model.Scenario;

public class ScenarioVerify extends EntityVerify<Scenario> {

	int MIN_FISH_NO = 100;
	int MAX_FISH_NO = 300000;
	double MIN_WEIGHT = 1.0;
	double MAX_WEIGHT = 500.0;

	public ScenarioVerify(Scenario entity) {
		super(entity);
	}

	@Override
	public void verify() throws EntityVerify.VerifyException {
		VerifyException toThrow = null;

		try {
			super.verify();
		} catch (VerifyException e) {
			toThrow = e;
		}

		if (entity.getFishNo() < MIN_FISH_NO || entity.getFishNo() > MAX_FISH_NO) {
			toThrow = new VerifyException(String.format("Fish number is %s. It should be between %s and %s]",
					entity.getFishNo(), MIN_FISH_NO, MAX_FISH_NO), toThrow);
		}

		if (entity.getWeight() < MIN_WEIGHT || entity.getWeight() > MAX_WEIGHT) {
			toThrow = new VerifyException(String.format("Fish weight is %.2f. It should be between %.2f and %.2f]",
					entity.getWeight(), MIN_WEIGHT, MAX_WEIGHT), toThrow);
		}

		if (entity.getStartDate().after(entity.getTargetDate())) {
			toThrow = new VerifyException(String.format("Start date %s is after target date %s.",
					entity.getStartDateFrm(), entity.getTargetDateFrm()), toThrow);
		}

		if (toThrow != null)
			throw toThrow;

	}

	@Override
	public EntityVerify<Scenario> normalise() {
		super.normalise();

		entity.setComments(StringUtils.trimToEmpty(entity.getComments()));

		return this;
	}

}
