package client.utils;

import javax.json.Json;
import javax.json.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Supplies translations to the rest of the application.
 * On creation the TranslationSupplier accepts a language string.
 * This language string should correspond to a json file stored in /locales/ under the same name.
 */
public class TranslationSupplier {
    private static final String localesPath = "locales/";
    private HashMap<String, String> translationMap;
    private String currentLanguage;

    /**
     * Constructor for the translation supplier
     *
     * @param lang This language string should correspond to a json file stored in localesPath under the same name.
     * @throws FileNotFoundException in case there is no corresponding json file found.
     */
    public TranslationSupplier(String lang) throws FileNotFoundException {
        currentLanguage = lang;
        var path = localesPath + lang + ".json";
        if(!FileSystemUtils.checkIfFileExists(localesPath + lang + ".json")) {
            throw new FileNotFoundException(path + " could not be located");
        }
        var reader = Json.createReader(new FileReader(path));
        JsonObject json = reader.readObject();
        reader.close();

        translationMap = new HashMap<>();
        json.forEach((key, val) -> translationMap.put(key,val.toString()));
    }

    /**
     * Method used to get a translation from the translation supplier.
     *
     * @param key the key for the translation
     * @return Either the correct translation or null
     */
    public String getTranslation(String key) { return translationMap.get(key); }
}
