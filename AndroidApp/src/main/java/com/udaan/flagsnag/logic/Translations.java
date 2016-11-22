package com.udaan.flagsnag.logic;

import java.util.Locale;

/*
 * This class provides the grammatically correct translations
 */
public class Translations {
	public static String scorePost(int score, String category, Locale locale) {
		String translated = null;
		
		if (locale.getLanguage().equals("es")) {
			translated = "Marqu� " + score + " en la categor�a \"" + category + "\"";
			translated = "I scored " + score + " in " + category;
		}
		else {
			translated = "I scored " + score + " in \"" + category + "\" category";
		}
		return translated;
	}
}