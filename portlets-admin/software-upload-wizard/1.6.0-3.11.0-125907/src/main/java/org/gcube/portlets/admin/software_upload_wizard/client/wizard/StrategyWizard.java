package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

import java.util.List;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;

public class StrategyWizard implements IWizard {

	private IWizard wizardStrategy;

	public StrategyWizard(IWizard strategy) {
		this.wizardStrategy = strategy;
	}

	@Override
	public WizardCard getCurrentCard() {
		return wizardStrategy.getCurrentCard();
	}

	@Override
	public WizardCard goToNextCard() throws Exception {
		return wizardStrategy.goToNextCard();
	}

	@Override
	public WizardCard goToPreviousCard() throws Exception {
		return wizardStrategy.goToPreviousCard();
	}

	@Override
	public boolean isOnFirstCard() {
		return wizardStrategy.isOnFirstCard();
	}

	@Override
	public boolean isOnLastCard() {
		return wizardStrategy.isOnLastCard();
	}

	@Override
	public List<WizardCard> getCards() {
		return wizardStrategy.getCards();
	}

	@Override
	public void setCards(List<WizardCard> cards) throws Exception {
		wizardStrategy.setCards(cards);
	}

	@Override
	public int getCurrentWizardStepNumber() {
		return wizardStrategy.getCurrentWizardStepNumber();
	}

	@Override
	public int getTotalWizardStepsNumber() {
		return wizardStrategy.getTotalWizardStepsNumber();
	}

}
