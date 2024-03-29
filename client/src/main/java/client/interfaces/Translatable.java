package client.interfaces;

import client.utils.TranslationSupplier;

public interface Translatable {
    /**
     * Translates the current scene using a translationSUpplier
     * @param translationSupplier an instance of a translationsupplier. If null, the default english will be displayed.
     */
    void translate(TranslationSupplier translationSupplier);
}
