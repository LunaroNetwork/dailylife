package nl.openminetopia.configuration.language;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import nl.openminetopia.DailyLife;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.SQLException;

public class MessageConfiguration {

    @Getter
    private final DutchMessageConfiguration dutchMessageConfiguration;
    @Getter
    private static EnglishMessageConfiguration englishMessageConfiguration = null;

    public MessageConfiguration(File dataFolder) {
        this.dutchMessageConfiguration = new DutchMessageConfiguration(new File(dataFolder, "nl-NL.yml"));
        this.englishMessageConfiguration = new EnglishMessageConfiguration(new File(dataFolder, "en-EN.yml"));
    }

    private LanguageMessageConfiguration getLanguageConfiguration(String lang) {
        if ("dutch".equalsIgnoreCase(lang)) {
            return dutchMessageConfiguration;
        }
        return englishMessageConfiguration;
    }

    private LanguageMessageConfiguration getLanguageConfiguration(OfflinePlayer player) {
        try {
            return getLanguageConfiguration(DailyLife.getLanguageDatabase().getLanguage(player.getUniqueId()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String message(String identifier, OfflinePlayer player) {
        return getLanguageConfiguration(player).message(identifier); //player is for language, not for placeholders: use player to whom message will be sent and not the placeholder target!
    }

    public static String message(String identifier) {
        return "<hover:show_text:'<red>Message not translatable yet!'><dark_gray>[<dark_red><bold>!</bold><dark_gray>]</hover> " + englishMessageConfiguration.message(identifier);
    }

    public Component component(String identifier, OfflinePlayer player) {
        return getLanguageConfiguration(player).component(identifier);
    }

    public static Component component(String identifier) {
        return ChatUtils.color("<hover:show_text:'<red>Message not translatable yet!'><dark_gray>[<dark_red><bold>!</bold><dark_gray>]</hover> ").append(englishMessageConfiguration.component(identifier));
    }


    public void saveConfiguration() {
        dutchMessageConfiguration.saveConfiguration();
        englishMessageConfiguration.saveConfiguration();
    }
}
