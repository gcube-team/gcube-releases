package org.gcube.portlets.admin.software_upload_wizard.client.wizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import net.customware.gwt.dispatch.client.DispatchAsync;

import org.gcube.portlets.admin.software_upload_wizard.client.event.NumberOfPackagesUpdatedEvent;
import org.gcube.portlets.admin.software_upload_wizard.client.event.NumberOfPackagesUpdatedEventHandler;
import org.gcube.portlets.admin.software_upload_wizard.client.util.Util;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.FileUploadCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.GenericLibraryPackageDataCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.MavenDependenciesCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.SubmitCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.gcubewebservice.GCubeWebServiceMainPackageDataCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.gcubewebservice.GCubeWebServiceMultiPackServiceDataCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.GeneralSoftwareInfoCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.InstallNotesCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.LicenseCard;
import org.gcube.portlets.admin.software_upload_wizard.client.view.card.generalinfo.MaintainersAndChangesCard;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIds;
import org.gcube.portlets.admin.software_upload_wizard.shared.rpc.GetPackageIdsResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GCubeWebServiceDynamicWizard implements IWizard {

	private List<WizardCard> firstCards;
	private List<WizardCard> variableCards = new ArrayList<WizardCard>();
	private List<WizardCard> lastCards;

	private WizardCard currentCard;

	private ListIterator<List<WizardCard>> currentCardsetIterator;
	private ListIterator<WizardCard> currentCardIterator;
	private boolean wentListNext;
	private boolean wentCardNext;

	public GCubeWebServiceDynamicWizard(ArrayList<String> packageIds) {
		firstCards = Arrays.asList(new GCubeWebServiceMultiPackServiceDataCard(),
				new GCubeWebServiceMainPackageDataCard(packageIds.get(0)),
				new FileUploadCard(packageIds.get(0), "Main Package"),
				new MavenDependenciesCard(packageIds.get(0), "Main Package"),
				new GenericLibraryPackageDataCard(packageIds.get(1),
						"Stubs Package"), new FileUploadCard(packageIds.get(1),
						"Stubs Package"),
				new MavenDependenciesCard(packageIds.get(1), "Stubs Package"));
		lastCards = Arrays.asList(new GeneralSoftwareInfoCard(packageIds.get(0)),
				new MaintainersAndChangesCard(packageIds.get(0)),
				new InstallNotesCard(), new LicenseCard(), new SubmitCard());
		// currentCard = firstCards.get(0);

		currentCardsetIterator = Arrays.asList(firstCards, variableCards,
				lastCards).listIterator();

		currentCardIterator = currentCardsetIterator.next().listIterator();
		currentCard = currentCardIterator.next();
		wentListNext = true;
		wentCardNext = true;

		bind();
	}

	private void bind() {
		Util.getEventBus().addHandler(NumberOfPackagesUpdatedEvent.TYPE,
				new NumberOfPackagesUpdatedEventHandler() {

					@Override
					public void onNumberOfPackagesUpdated(
							NumberOfPackagesUpdatedEvent event) {
						refreshVariableCards();
					}
				});

	}

	@Override
	public WizardCard getCurrentCard() {
		return currentCard;
	}

	@Override
	public WizardCard goToNextCard() throws Exception {

		// If current card is a card of first cards
		if (firstCards.contains(currentCard)) {
			// If wizard is on the last card of the first cards array
			Log.trace("Card is on first cards with index: "
					+ firstCards.indexOf(currentCard));
			if (firstCards.size() - 1 == firstCards.indexOf(currentCard)) {
				// If variable cards set is empty, set next card as first card
				// of last cards set, otherwise use first card of variable cards
				// set
				if (variableCards.size() != 0) {
					Log.trace("Returning first card of variable cards.");
					currentCard = variableCards.get(0);
				} else {
					Log.trace("Returning first card of last cards.");
					currentCard = lastCards.get(0);
				}
			} else {
				int newCardIndex = firstCards.indexOf(currentCard) + 1;
				Log.trace("Returning next card of first cards with index: "
						+ newCardIndex);
				currentCard = firstCards.get(newCardIndex);
			}
		} else if (variableCards.contains(currentCard)) {
			Log.trace("Card is on variable cards with index: "
					+ variableCards.indexOf(currentCard));
			if (variableCards.size() - 1 == variableCards.indexOf(currentCard)) {
				Log.trace("Returning first card of last cards.");
				currentCard = lastCards.get(0);
			} else {
				int newCardIndex = variableCards.indexOf(currentCard) + 1;
				Log.trace("Returning next card of variable cards with index: "
						+ newCardIndex);
				currentCard = variableCards.get(newCardIndex);
			}
		} else if (lastCards.contains(currentCard)) {
			Log.trace("Card is on last cards with index: "
					+ lastCards.indexOf(currentCard));
			if (lastCards.size() - 1 == lastCards.indexOf(currentCard)) {
				Log.trace("No more cards left in wizard");
				throw new Exception("No more cards left in wizard");
			} else {
				int newCardIndex = lastCards.indexOf(currentCard) + 1;
				Log.trace("Returning next card of last cards with index: "
						+ (newCardIndex));
				currentCard = lastCards.get(newCardIndex);
			}
		}
		Log.trace("Returning card type: "
				+ currentCard
						.getClass()
						.getName()
						.substring(
								currentCard.getClass().getName()
										.lastIndexOf(".") + 1));
		return currentCard;
	}

	@Override
	public WizardCard goToPreviousCard() throws Exception {
		if (lastCards.contains(currentCard)) {
			Log.trace("Card is on last cards with index: "
					+ lastCards.indexOf(currentCard));
			if (lastCards.indexOf(currentCard) == 0) {
				Log.trace("Variable cards set has " + variableCards.size()
						+ " cards");
				if (variableCards.size() == 0) {
					int newCardIndex = firstCards.size() - 1;
					Log.trace("Returning last card of first cards set with index: "
							+ newCardIndex);
					currentCard = firstCards.get(newCardIndex);
				} else {
					int newCardIndex = variableCards.size() - 1;
					Log.trace("Returning last card of variable cards set with index: "
							+ newCardIndex);
					currentCard = variableCards.get(newCardIndex);
				}
			} else {
				int newCardIndex = lastCards.indexOf(currentCard) - 1;
				Log.trace("Returning previous card of last cards set with index: "
						+ newCardIndex);
				currentCard = lastCards.get(newCardIndex);
			}
		} else if (variableCards.contains(currentCard)) {
			Log.trace("Card is on variable cards with index: "
					+ variableCards.indexOf(currentCard));
			if (variableCards.indexOf(currentCard) == 0) {
				int newCardIndex = firstCards.size() - 1;
				Log.trace("Returning last card of first cards set with index: "
						+ newCardIndex);
				currentCard = firstCards.get(newCardIndex);
			} else {
				int newCardIndex = variableCards.indexOf(currentCard) - 1;
				Log.trace("Returning previous card of variable cards set with index: "
						+ newCardIndex);
				currentCard = variableCards.get(newCardIndex);
			}
		} else if (firstCards.contains(currentCard)) {
			Log.trace("Card is on first cards with index: "
					+ firstCards.indexOf(currentCard));
			if (firstCards.indexOf(currentCard) == 0) {
				Log.trace("First card of wizard reached");
				throw new Exception("First card of wizard reached");
			} else {
				int newCardIndex = firstCards.indexOf(currentCard) - 1;
				Log.trace("Returning previous card of first cards set with index: "
						+ newCardIndex);
				currentCard = firstCards.get(newCardIndex);
			}
		}
		Log.trace("Returning card type: "
				+ currentCard
						.getClass()
						.getName()
						.substring(
								currentCard.getClass().getName()
										.lastIndexOf(".") + 1));
		return currentCard;
	}

	@Override
	public boolean isOnFirstCard() {
		return firstCards.contains(currentCard)
				&& firstCards.indexOf(currentCard) == 0;
	}

	@Override
	public boolean isOnLastCard() {
		return lastCards.contains(currentCard)
				&& lastCards.indexOf(currentCard) == lastCards.size() - 1;
	}

	@Override
	public List<WizardCard> getCards() {
		List<WizardCard> result = new ArrayList<WizardCard>();
		result.addAll(firstCards);
		result.addAll(variableCards);
		result.addAll(lastCards);
		return result;
	}

	@Override
	public void setCards(List<WizardCard> cards) throws Exception {
		throw new Exception("Unable to set cards for this type of wizard.");
	}

	private void refreshVariableCards() {
		Log.trace("Refreshing variable wizard cards");
		DispatchAsync dispatch = Util.getDispatcher();
		dispatch.execute(new GetPackageIds(),
				new AsyncCallback<GetPackageIdsResult>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(GetPackageIdsResult result) {
						
						
						ArrayList<String> packageIds = result.getIds();
						
						Log.trace("Got packageIds:");
						for (int i=0; i< packageIds.size();i++)
							Log.trace("Package #"+i+": " + packageIds.get(i));
						variableCards = new ArrayList<WizardCard>();
						// remove the first two packages which are mandatory
						packageIds.remove(0);
						packageIds.remove(0);
						if (packageIds.size() > 0) {
							int i = 1;
							for (String id : packageIds) {
								variableCards
										.add(new GenericLibraryPackageDataCard(
												id, "Additional Package #" + i));
								variableCards.add(new FileUploadCard(id,
										"Additional Package #" + i));
								variableCards.add(new MavenDependenciesCard(id,
										"Additional Package #" + i));
								i++;
							}
						}
						Log.trace("Variable wizard cards refreshed.");
					}
				});

	}

	@Override
	public int getCurrentWizardStepNumber() {
		// TODO Implement this method
		return 0;
	}

	@Override
	public int getTotalWizardStepsNumber() {
		// TODO Implement this method
		return 0;
	}

	// @Override
	// public WizardCard goToNextCard() throws Exception {
	// try {
	// if (!wentCardNext)
	// currentCard = currentCardIterator.next();
	// wentCardNext=true;
	// currentCard = currentCardIterator.next();
	//
	// Log.trace("Returning card type: "
	// + currentCard
	// .getClass()
	// .getName()
	// .substring(
	// currentCard.getClass().getName()
	// .lastIndexOf(".") + 1));
	// return currentCard;
	// } catch (NoSuchElementException e) {
	// try {
	// if (!wentListNext)
	// currentCardIterator = currentCardsetIterator.next()
	// .listIterator();
	// wentListNext=true;
	//
	// currentCardIterator = currentCardsetIterator.next()
	// .listIterator();
	//
	// return goToNextCard();
	// } catch (NoSuchElementException e2) {
	// throw new Exception("End of wizard reached");
	// }
	// }
	// }
	//
	// @Override
	// public WizardCard goToPreviousCard() throws Exception {
	// try {
	// if (wentCardNext)
	// currentCard = currentCardIterator.previous();
	// wentCardNext = false;
	// currentCard = currentCardIterator.previous();
	//
	// Log.trace("Returning card type: "
	// + currentCard
	// .getClass()
	// .getName()
	// .substring(
	// currentCard.getClass().getName()
	// .lastIndexOf(".") + 1));
	// return currentCard;
	// } catch (NoSuchElementException e) {
	// try {
	// if (wentListNext)
	// currentCardIterator = currentCardsetIterator.previous()
	// .listIterator();
	// wentListNext = false;
	// currentCardIterator = currentCardsetIterator.previous()
	// .listIterator();
	//
	// return goToPreviousCard();
	// } catch (NoSuchElementException e2) {
	// throw new Exception("Start of wizard reached");
	// }
	// }
	// }
}
