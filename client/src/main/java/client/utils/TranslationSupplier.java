package client.utils;

import javax.json.Json;
import javax.json.JsonObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class TranslationSupplier {
    private static final String localesPath = "locales/";
    private HashMap<String, String> translationMap;

    public TranslationSupplier(String lang) throws FileNotFoundException {
        var path = localesPath + lang + ".json";
        if(!FileSystemUtils.checkIfFileExists(localesPath + lang + ".json")) {
            throw new FileNotFoundException(path + " could not be located");
        }
        var reader = Json.createReader(new FileReader(path));
        JsonObject json = reader.readObject();
        reader.close();

        translationMap = new HashMap<>();
        json.forEach((k, v) -> translationMap.put(k,v.toString()));
        System.out.println(translationMap);
    }

    public String getTranslation(String key) { return translationMap.get(key); }
}
