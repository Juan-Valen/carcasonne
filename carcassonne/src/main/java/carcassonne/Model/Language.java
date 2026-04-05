package carcassonne.Model;

import carcassonne.Service.databaseService;
import java.util.*;

public class Language {
//    public Locale currentLocale;
//    public ResourceBundle resourceBundle;

    private databaseService DBSI = databaseService.getInstance();

    private String currentLanguage = "en";
    private Map<String, Map<String, String>> languages = new HashMap<>();




    public Language() {
//        this.currentLocale = new Locale("en");
//        this.resourceBundle = ResourceBundle.getBundle("bundle", currentLocale);
        languages.put(currentLanguage, DBSI.getTranslations(currentLanguage));


    }

    public void setLanguage(String languageCode) {
//        this.currentLocale = new Locale(languageCode);
//        this.resourceBundle = ResourceBundle.getBundle("bundle", currentLocale);
        currentLanguage = languageCode;
        if(!languages.containsKey(languageCode)) {
            languages.put(languageCode, DBSI.getTranslations(languageCode));
        }
    }

    public String getString(String key) {
//        return resourceBundle.getString(key);
        Map<String, String> translations = languages.get(currentLanguage);
        if(translations.containsKey(key)) return translations.get(key);
        return languages.get("en").get(key);
    }
}
