package nl.openminetopia.configuration.language;

import net.kyori.adventure.text.Component;

public interface LanguageMessageConfiguration {
    String message(String identifier);
    Component component(String identifier);
}
