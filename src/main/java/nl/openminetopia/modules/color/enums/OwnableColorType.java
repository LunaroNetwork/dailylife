package nl.openminetopia.modules.color.enums;

import lombok.Getter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.language.MessageConfiguration;
import nl.openminetopia.modules.color.objects.*;

@Getter
public enum OwnableColorType {
    PREFIX(DailyLife.getMessageConfiguration().message("color_prefix_display_name"), DailyLife.getDefaultConfiguration().getDefaultPrefixColor()),
    CHAT(DailyLife.getMessageConfiguration().message("color_chat_display_name"), DailyLife.getDefaultConfiguration().getDefaultChatColor()),
    LEVEL(DailyLife.getMessageConfiguration().message("color_level_display_name"), DailyLife.getDefaultConfiguration().getDefaultLevelColor());

    private final String displayName;
    private final String defaultColor;

    OwnableColorType(String displayName, String defaultColor) {
        this.displayName = displayName;
        this.defaultColor = defaultColor;
    }

    public Class<? extends OwnableColor> correspondingClass() {
        return switch (this) {
            case PREFIX -> PrefixColor.class;
            case CHAT -> ChatColor.class;
            case LEVEL -> LevelColor.class;
        };
    }

    public OwnableColor defaultColor() {
        DefaultConfiguration configuration = DailyLife.getDefaultConfiguration();
        return switch (this) {
            case PREFIX -> new PrefixColor(-1, configuration.getDefaultPrefixColor(), -1);
            case CHAT -> new ChatColor(-1, configuration.getDefaultChatColor(), -1);
            case LEVEL -> new LevelColor(-1, configuration.getDefaultLevelColor(), -1);
        };
    }
}