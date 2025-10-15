package nl.openminetopia.configuration.language;

import net.kyori.adventure.text.Component;
import nl.openminetopia.DailyLife;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.config.ConfigurateConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DutchMessageConfiguration extends ConfigurateConfig implements LanguageMessageConfiguration {

    private final Map<String, String> messages = new HashMap<>();

    public DutchMessageConfiguration(File file) {
        super(file, "nl-NL.yml", "default/languages/nl-NL.yml", true);

        rootNode.childrenMap().forEach((s, node) -> {
            if (!(s instanceof String identifier)) return;
            messages.put(identifier, node.getString());
        });
    }

    @Override
    public String message(String identifier) {
        String message = messages.get(identifier);
        if (message == null) {
            return identifier + " was not found.";
        }
        return message;
    }

    public Component component(String identifier) {
        return ChatUtils.color(message(identifier));
    }
}
