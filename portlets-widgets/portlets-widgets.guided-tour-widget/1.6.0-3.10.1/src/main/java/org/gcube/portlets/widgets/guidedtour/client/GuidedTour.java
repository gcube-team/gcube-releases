package org.gcube.portlets.widgets.guidedtour.client;



import java.util.HashMap;

import org.gcube.portlets.widgets.guidedtour.client.steps.*;
import org.gcube.portlets.widgets.guidedtour.client.types.ThemeColor;
import org.gcube.portlets.widgets.guidedtour.client.types.VerticalAlignment;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GuidedTour implements EntryPoint{

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//showGuidedTour();
		//WelcomeGuideChecker.check("Ciccio").isSetShow();
	}


	private void showGuidedTour() {
		TourStep step1 = new GCUBETemplate1Text1ImageML(true) {

			@Override
			public HashMap<TourLanguage, String> setStepTitle() {
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, "gCube Reporting");
				languages.put(TourLanguage.IT, "gCube Reporting");
				return languages;
			}

			@Override
			public String setStepImage() {
				return "images/tour/tour_1.jpg";
			}

			@Override
			public HashMap<TourLanguage, String> setStepBody() {
				String en = "<div style=\"line-height: 19px; padding: 10px; font-size: 14px; \">" +
						"<div style=\"padding-bottom: 40px;\">" +
						"<b>gCube Reporting</b> allows users to create Reports and generate different " +
						"export formats (OpenXML, HTML, PDF) ) based on results retrieved from the infrastructure." +
						"</div>" +
						"<div style=\"padding-bottom: 40px;\">" +
						"gCube Templates are dynamically and statically completed." +
						"</div>" +
						"<div style=\"padding-bottom: 40px;\">" +
						"gCube Templates are loaded by the <b>gCube Report Generator</b> to produce actual reports." +
						"</div>" +
						"<div style=\"padding-bottom: 10px;\">" +
						"<b>Discover</b> gCube Reporting features through this quick tour." +
						"</div>" +
						"</div>";
				
				String it = "<div style=\"line-height: 19px; padding: 10px; font-size: 14px; \">" +
						"<div style=\"padding-bottom: 40px;\">" +
						"<b>gCube Reporting</b> permette agli utenti di creare Reports e di generarne different " +
						" formati (OpenXML, HTML, PDF) ) basati sui risultati  on results recuperati dalla e-infrastructure." +
						"</div>" +
						"<div style=\"padding-bottom: 40px;\">" +
						"I Template gCube possono essere completati sia dinamicamente che staticamente." +
						"</div>" +
						"<div style=\"padding-bottom: 40px;\">" +
						"I Template gCube sono caricate nella <b>gCube Report Generator</b> per produrre report effettivi." +
						"</div>" +
						"<div style=\"padding-bottom: 10px;\">" +
						"<b>Scopri</b> le caratteristiche di gCube Reporting fattraverso questo tour veloce." +
						"</div>" +
						"</div>";
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, en);
				languages.put(TourLanguage.IT, it);
				return languages;
			}
		};
		TourStep stepNew = new GCUBETemplate1Text1Image() {
			
			@Override
			public String setStepTitle() {
				return "ToolBox";
			}
			
			@Override
			public String setStepImage() {
				return "images/tour/tour2.jpg";
			}
			
			@Override
			public String setStepBody() {
				return "<div style=\"line-height: 19px; font-size: 16px; padding: 10px;\">" +
				"<div style=\"padding-bottom: 90px;\">" +
				"<b>Define</b> your template by selecting from the Toolbox components." +
				"</div>" +
				"<div style=\"padding-bottom: 80px;\">" +
				"Choose from <b>several components:</b><br />" +
				"<span style=\"font-style: italic;\">title, headings, images, tables </span> and much more!</b>" +
				"</div>" +
				"<div style=\"padding-bottom: 10px;\">" +
				"Choose from <b>two different layouts:</b>: one column or double columns for each section." +
				"</div>" +
				"</div>";
			}
		};
		
		TourStep step2 = new GCUBETemplate1Text1ImageML(false) {

			@Override
			public HashMap<TourLanguage, String> setStepTitle() {
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, "ToolBox");
				languages.put(TourLanguage.IT, "Strumenti");
				return languages;
			}

			@Override
			public String setStepImage() {
				return "images/tour/tour2.jpg";
			}
			@Override
			public HashMap<TourLanguage, String> setStepBody() {
				String en =  "<div style=\"line-height: 19px; font-size: 16px; padding: 10px;\">" +
						"<div style=\"padding-bottom: 90px;\">" +
						"<b>Define</b> your template by selecting from the Toolbox components." +
						"</div>" +
						"<div style=\"padding-bottom: 80px;\">" +
						"Choose from <b>several components:</b><br />" +
						"<span style=\"font-style: italic;\">title, headings, images, tables </span> and much more!</b>" +
						"</div>" +
						"<div style=\"padding-bottom: 10px;\">" +
						"Choose from <b>two different layouts:</b>: one column or double columns for each section." +
						"</div>" +
						"</div>";
				
				String it =  "<div style=\"line-height: 19px; font-size: 16px; padding: 10px;\">" +
						"<div style=\"padding-bottom: 90px;\">" +
						"<b>Definisci</b> il tuo template selezionando dalla barra degli strumenti." +
						"</div>" +
						"<div style=\"padding-bottom: 80px;\">" +
						"Scegli fra <b>diversi componenti:</b><br />" +
						"<span style=\"font-style: italic;\">title, headings, images, tables </span> and much more!</b>" +
						"</div>" +
						"<div style=\"padding-bottom: 10px;\">" +
						"Scegli fra <b>due diversi layout:</b>: una colonna o due colonne per ogni section." +
						"</div>" +
						"</div>";

				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, en);
				languages.put(TourLanguage.IT, it);
				return languages;
			}

		};
		TourStep step3 = new GCUBETemplate2Text2ImageML(false) {

			@Override
			public HashMap<TourLanguage, String> setStepTitle() {
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, "ToC & Reporting");
				languages.put(TourLanguage.IT, "Indice Analitico");
				return languages;

			}

			@Override
			public String setStepImage() {
				return "images/tour/tourToc.jpg";
			}

			@Override
			public  HashMap<TourLanguage, String> setStepBody() {
				String en =  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 10px;\">" +
						"Add a dynamic component for automatic <b>Table Of Content</b> creation." +
						"</div>" +
						"</div>";
				
				String it =  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 10px;\">" +
						"Aggiungi un componente dinamico per la creazione automatica della <b>Table Of Content</b>." +
						"</div>" +
						"</div>";

				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, en);
				languages.put(TourLanguage.IT, it);
				return languages;
			}

			@Override
			public String setStepOtherImage() {
				return "images/tour/tourFormat.jpg";
			}

			@Override
			public  HashMap<TourLanguage, String> setStepOtherBody() {
				String en =  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 10px;\">" +
						"<b>Format text</b> as you would in a word processor using the Formatting Bar." +
						"</div>" +
						"</div>";
				String it =  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 10px;\">" +
						"<b>Formatta testo</b> come in word usando la Barra di Formattazione." +
						"</div>" +
						"</div>";
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, en);
				languages.put(TourLanguage.IT, it);
				return languages;
			}
		};
		TourStep step4 = new GCUBETemplate2Text2ImageML(false) {

			@Override
			public HashMap<TourLanguage, String> setStepTitle() {
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, "Tables");
				languages.put(TourLanguage.IT, "Tabelle");
				return languages;
			}

			@Override
			public String setStepImage() {
				return "images/tour/tour3_1.jpg";
			}

			@Override
			public HashMap<TourLanguage, String> setStepBody() {
				String en=  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 80px;\">" +
						"Add static or dynamic <b>Tables</b>." +
						"</div>" +
						"</div>";
				String it =  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 80px;\">" +
						"Aggiungi <b>Tabelle</b> statiche o dinamiche." +
						"</div>" +
						"</div>";
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, en);
				languages.put(TourLanguage.IT, it);
				return languages;
			}

			@Override
			public String setStepOtherImage() {
				return "images/tour/tour3_2.jpg";
			}

			@Override
			public HashMap<TourLanguage, String> setStepOtherBody() {
				String en = "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 10px;\">" +
						"<b>Merge</b> table cells, add/remove rows and much more!" +
						"</div>" +
						"</div>";
				String it =  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 10px;\">" +
						"<b>Unisci</b> celle, aggiungi/rimuovi righe and much more!" +
						"</div>" +
						"</div>";
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, en);
				languages.put(TourLanguage.IT, it);
				return languages;
			}
		};

		TourStep step5 = new GCUBETemplate1Text1ImageML(false) {

			@Override
			public HashMap<TourLanguage, String> setStepTitle() {
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, "Sections");
				languages.put(TourLanguage.IT, "Sezioni");
				return languages;
			}

			@Override
			public String setStepImage() {
				return "images/tour/tour4.jpg";
			}

			@Override
			public HashMap<TourLanguage, String> setStepBody() {
				String en =  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 70px;\">" +
						"Divide your template into various <b>Sections.</b>" +
						"</div>" +
						"<div style=\"padding-bottom: 50px;\">" +
						"Sections can be <b>imported</b> and/or <b>exported</b> from existing reports or templates." +
						"</div>" +
						"</div>";	
				String it =  "<div style=\"line-height: 19px; padding: 10px; font-size: 16px; \">" +
						"<div style=\"padding-bottom: 70px;\">" +
						"Dividi il tuo template in varie <b>Sezioni.</b>" +
						"</div>" +
						"<div style=\"padding-bottom: 50px;\">" +
						"Le Sezioni possono essere <b>importate</b> e/o <b>esportate</b> da reports or templates esistenti." +
						"</div>" +
						"</div>";	
				
				HashMap<TourLanguage, String> languages = new HashMap<TourLanguage, String>();
				languages.put(TourLanguage.EN, en);
				languages.put(TourLanguage.IT, it);
				return languages;
			}


		};
		//step1.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		step2.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		step3.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		step4.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		step5.setTextVerticalAlignment(VerticalAlignment.ALIGN_MIDDLE);
		String userGuideUrl = "https://gcube.wiki.gcube-system.org/gcube/index.php/Common_Functionality#Template_Creation";
		GCUBEGuidedTour gt = new GCUBEGuidedTour("gCube Template Creator", "TESTE", userGuideUrl, 780, 450, false, ThemeColor.BLUE, TourLanguage.EN, TourLanguage.IT);
		gt.addStep(step1);
		gt.addStep(step2);
		gt.addStep(step3);
		gt.addStep(step4);
		gt.addStep(step5);
		gt.openTour();
	}
}
