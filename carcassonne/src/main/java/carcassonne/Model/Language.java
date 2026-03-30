package carcassonne.Model;

import java.util.Locale;
import java.util.ResourceBundle;

public class Language {
    public Locale currentLocale;
    public ResourceBundle resourceBundle;
    public Language() {
        this.currentLocale = new Locale("en");
        this.resourceBundle = ResourceBundle.getBundle("bundle", currentLocale);
    }

    public void setLanguage(String languageCode) {
        this.currentLocale = new Locale(languageCode);
        this.resourceBundle = ResourceBundle.getBundle("bundle", currentLocale);
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }
}
