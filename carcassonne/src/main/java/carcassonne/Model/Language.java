package carcassonne.Model;

import carcassonne.Service.databaseService;
import java.util.*;

public class Language {
    private databaseService DBSI = databaseService.getInstance();

    private String currentLanguage = "en";
    private Map<String, Map<String, String>> languages = new HashMap<>();

    public Language() {
        languages.put(currentLanguage, DBSI.getTranslations(currentLanguage));
    }

    public void setLanguage(String languageCode) {
        currentLanguage = languageCode;
        if(!languages.containsKey(languageCode)) {
            languages.put(languageCode, DBSI.getTranslations(languageCode));
        }
    }

    public String getString(String key) {
        Map<String, String> translations = languages.get(currentLanguage);
        if(translations.containsKey(key)) return translations.get(key);
        return languages.get("en").get(key);
    }
}
