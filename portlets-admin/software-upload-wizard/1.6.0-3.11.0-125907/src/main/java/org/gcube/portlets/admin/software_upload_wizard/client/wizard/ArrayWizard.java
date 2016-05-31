package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

import java.util.List;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;


public class ArrayWizard implements IWizard {

	private List<WizardCard> cards;
	private int currentCardIndex = 0;
	
	public ArrayWizard(List<WizardCard> startingCards) {
		this.cards = startingCards;
	}
	
	@Override
	public WizardCard getCurrentCard() {
		return cards.get(currentCardIndex);
	}
	
	@Override
	public boolean isOnFirstCard() {
		if (currentCardIndex==0) return true;
		else return false;
	}
	@Override
	public boolean isOnLastCard() {
		if (currentCardIndex == cards.size()-1) return true;
		else return false;
	}
	
	@Override
	public List<WizardCard> getCards() {
		return cards;
	}
	
	@Override
	public void setCards(List<WizardCard> cards) {
		this.cards = cards;
	}

	@Override
	public WizardCard goToNextCard() throws Exception {
		if (cards.size()==0) throw new Exception("Wizard is uninitialized");
		if (isOnLastCard()) throw new Exception("Wizard is on last card.");
		return cards.get(++currentCardIndex);
	}

	@Override
	public WizardCard goToPreviousCard() throws Exception {
		if (cards.size()==0) throw new Exception("Wizard is uninitialized");
		if (isOnFirstCard()) throw new Exception ("Wizard is on first card.");
		return cards.get(--currentCardIndex);
	}

	@Override
	public int getCurrentWizardStepNumber() {
		return getCards().indexOf(getCurrentCard())+1;
	}

	@Override
	public int getTotalWizardStepsNumber() {
		return getCards().size();
	}

}
