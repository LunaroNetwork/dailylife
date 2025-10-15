package nl.openminetopia.configuration.language;

import net.kyori.adventure.text.Component;
import nl.openminetopia.DailyLife;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.config.ConfigurateConfig;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EnglishMessageConfiguration extends ConfigurateConfig implements LanguageMessageConfiguration {

    private final Map<String, String> messages = new HashMap<>();

    public EnglishMessageConfiguration(File file) {
        super(file, "en-EN.yml", "default/languages/en-EN.yml", true);

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
