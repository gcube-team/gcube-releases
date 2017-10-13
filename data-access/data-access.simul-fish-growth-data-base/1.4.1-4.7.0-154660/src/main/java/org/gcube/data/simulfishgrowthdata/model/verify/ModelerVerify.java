package org.gcube.data.simulfishgrowthdata.model.verify;

import org.apache.commons.lang.StringUtils;

import gr.i2s.fishgrowth.model.Modeler;

public class ModelerVerify extends EntityVerify<Modeler> {

	public ModelerVerify(Modeler entity) {
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

		if (entity.getUploadFilenameData() == null) {
			toThrow = new VerifyException(String.format("No samples uploaded. Data needed in order to proceed."),
					toThrow);
		}
		if (entity.getUploadFilenameWeights() == null) {
			toThrow = new VerifyException(
					String.format("No weight limit indicators uploaded. Data needed in order to proceed."), toThrow);
		}

		if (toThrow != null)
			throw toThrow;

	}

	@Override
	public EntityVerify<Modeler> normalise() {
		super.normalise();

		entity.setComments(StringUtils.trimToEmpty(entity.getComments()));

		entity.setUploadFileLocationData(StringUtils.trimToNull(entity.getUploadFileLocationData()));
		entity.setUploadFileLocationWeights(StringUtils.trimToNull(entity.getUploadFileLocationWeights()));

		entity.setUploadFilenameData(StringUtils.trimToNull(entity.getUploadFilenameData()));
		entity.setUploadFilenameWeights(StringUtils.trimToNull(entity.getUploadFilenameWeights()));

		entity.setUploadFileTypeData(StringUtils.trimToNull(entity.getUploadFileTypeData()));
		entity.setUploadFileTypeWeights(StringUtils.trimToNull(entity.getUploadFileTypeWeights()));

		return this;
	}

}
