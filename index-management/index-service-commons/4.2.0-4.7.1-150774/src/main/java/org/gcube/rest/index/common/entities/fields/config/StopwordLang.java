package org.gcube.rest.index.common.entities.fields.config;

public enum StopwordLang {

	
	ARABIC("_arabic_"), ARMENIAN("_armenian_"), BASQUE("_basque_"), BRAZILIAN("_brazilian_"), 
	BULGARIAN("_bulgarian_"), CATALAN("_catalan_"), CJK("_cjk_"), CZECH("_czech_"), DANISH("_danish_"), 
	DUTCH("_dutch_"), ENGLISH("_english_"), FINNISH("_finnish_"), FRENCH("_french_"), GALICIAN("_galician_"),
	GERMAN("_german_"), GREEK("_greek_"), HINDI("_hindi_"), HUNGARIAN("_hungarian_"), INDONESIAN("_indonesian_"), 
	IRISH("_irish_"), ITALIAN("_italian_"), LATVIAN("_latvian_"), LITHUANIAN("_lithuanian_"), NORWEGIAN("_norwegian_"), 
	PERSIAN("_persian_"), PORTUGUESE("_portuguese_"), ROMANIAN("_romanian_"), RUSSIAN("_russian_"), SORANI("_sorani_"), 
	SPANISH("_spanish_"), SWEDISH("_swedish_"), TURKISH("_turkish_"), THAI("_thai_");
	
	private String value;
	
	StopwordLang(String value){
		this.value = value;
	}
	
	public String value(){
		return value;
	}
	
	
	
	
}
