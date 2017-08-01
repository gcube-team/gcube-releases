package org.gcube.data.simulfishgrowthdata.model.verify;

import gr.i2s.fishgrowth.model.Site;

public class SiteVerify extends EntityVerify<Site> {

	public static int MIN_TEMP = -5;
	public static int MAX_TEMP = 50;

	public SiteVerify(Site entity) {
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

		if (entity.getPeriodJanA() < MIN_TEMP || entity.getPeriodJanA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "January A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodJanB() < MIN_TEMP || entity.getPeriodJanB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "January B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodFebA() < MIN_TEMP || entity.getPeriodFebA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "February A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodFebB() < MIN_TEMP || entity.getPeriodFebB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "February B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodMarA() < MIN_TEMP || entity.getPeriodMarA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "March A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodMarB() < MIN_TEMP || entity.getPeriodMarB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "March B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodAprA() < MIN_TEMP || entity.getPeriodAprA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "April A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodAprB() < MIN_TEMP || entity.getPeriodAprB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "April B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodMayA() < MIN_TEMP || entity.getPeriodMayA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "May A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodMayB() < MIN_TEMP || entity.getPeriodMayB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "May B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodJunA() < MIN_TEMP || entity.getPeriodJunA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "June A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodJunB() < MIN_TEMP || entity.getPeriodJunB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "June B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodJulA() < MIN_TEMP || entity.getPeriodJulA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "July A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodJulB() < MIN_TEMP || entity.getPeriodJulB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "July B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodAugA() < MIN_TEMP || entity.getPeriodAugA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "August A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodAugB() < MIN_TEMP || entity.getPeriodAugB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "August B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodSepA() < MIN_TEMP || entity.getPeriodSepA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "September A", MIN_TEMP, MAX_TEMP),
					toThrow);
		}
		if (entity.getPeriodSepB() < MIN_TEMP || entity.getPeriodSepB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "September B", MIN_TEMP, MAX_TEMP),
					toThrow);
		}
		if (entity.getPeriodOctA() < MIN_TEMP || entity.getPeriodOctA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "October A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodOctB() < MIN_TEMP || entity.getPeriodOctB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "October B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodNovA() < MIN_TEMP || entity.getPeriodNovA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "November A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodNovB() < MIN_TEMP || entity.getPeriodNovB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "November B", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodDecA() < MIN_TEMP || entity.getPeriodDecA() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "December A", MIN_TEMP, MAX_TEMP), toThrow);
		}
		if (entity.getPeriodDecB() < MIN_TEMP || entity.getPeriodDecB() > MAX_TEMP) {
			toThrow = new VerifyException(
					String.format("Period %s should be between %s and %s]", "December B", MIN_TEMP, MAX_TEMP), toThrow);
		}

		if (toThrow != null)
			throw toThrow;

	}

	@Override
	public EntityVerify<Site> normalise() {
		super.normalise();

		return this;
	}

}
