package org.gcube.portlets.user.searchportlet.client.widgets.guidedtour;
//package org.gcube.portlets.user.searchportlet.client.widgets;
//
//import org.gcube.portlets.user.searchportlet.client.SearchPortlet;
//import org.gcube.portlets.widgets.guidedtour.client.GCUBEGuidedTour;
//import org.gcube.portlets.widgets.guidedtour.client.steps.GCUBETemplate1Text1Image;
//import org.gcube.portlets.widgets.guidedtour.client.steps.TourStep;
//
//public class QuickGuidedTour {
//
//	public QuickGuidedTour() {
//
//	}
//
//	public void showGuide() {
//		GCUBEGuidedTour guide = new GCUBEGuidedTour("Search", SearchPortlet.class.getName(),
//				"https://technical.wiki.d4science.research-infrastructures.eu/documentation/index.php/Common_Functionality#Search",
//				750, 500, false);
//
//		TourStep intro = new GCUBETemplate1Text1Image(true) {
//
//			@Override
//			public String setStepTitle() {
//				return "gCube Search";
//			}
//
//			@Override
//			public String setStepImage() {
//				return "images/tourImages/search-main.png";
//			}
//
//			@Override
//			public String setStepBody() {
//				return new SearchGuideIntroHTML().getHTML();
//			} 
//		};
//
//		TourStep step1 = new GCUBETemplate1Text1Image(true) {
//
//			@Override
//			public String setStepTitle() {
//				return "Select Collections";
//			}
//
//			@Override
//			public String setStepImage() {
//				return "images/tourImages/selectCollections.png";
//			}
//
//			@Override
//			public String setStepBody() {
//				return new SearchGuideStep1HTML().getHTML();
//			} 
//		};
//
//		TourStep step2 = new GCUBETemplate1Text1Image(true) {
//
//			@Override
//			public String setStepTitle() {
//				return "Simple Search";
//			}
//
//			@Override
//			public String setStepImage() {
//				return "images/tourImages/simpleSearch.png";
//			}
//
//			@Override
//			public String setStepBody() {
//				return new SearchGuideStep2HTML().getHTML();
//			} 
//		};
//
//		TourStep step3 = new GCUBETemplate1Text1Image(true) {
//
//			@Override
//			public String setStepTitle() {
//				return "Advanced Search";
//			}
//
//			@Override
//			public String setStepImage() {
//				return "images/tourImages/advancedSearch.png";
//			}
//
//			@Override
//			public String setStepBody() {
//				return new SearchGuideStep3HTML().getHTML();
//			} 
//		};
//
//		TourStep step4 = new GCUBETemplate1Text1Image(true) {
//
//			@Override
//			public String setStepTitle() {
//				return "Collections Browsing";
//			}
//
//			@Override
//			public String setStepImage() {
//				return "images/tourImages/browse.png";
//			}
//
//			@Override
//			public String setStepBody() {
//				return new SearchGuideStep4HTML().getHTML();
//			} 
//		};
//
//		guide.addStep(intro);
//		guide.addStep(step1);
//		guide.addStep(step2);
//		guide.addStep(step3);
//		guide.addStep(step4);
//		guide.openTour();
//	}
//
//
//}
