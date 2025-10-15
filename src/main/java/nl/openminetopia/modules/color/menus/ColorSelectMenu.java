package nl.openminetopia.modules.color.menus;

import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.language.MessageConfiguration;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.objects.OwnableColor;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class ColorSelectMenu extends PaginatedMenu {

    private List<OwnableColor> colors;
    private final OwnableColorType type;

    public ColorSelectMenu(Player player, OfflinePlayer target, MinetopiaPlayer minetopiaPlayer, OwnableColorType type) {
        super(type.getDisplayName() + "<reset><black> menu", 2, 9);

        gui.disableAllInteractions();

        gui.setItem(17, this.nextPageItem());
        gui.setItem(9, this.previousPageItem());

        this.type = type;

        if (minetopiaPlayer == null) {
            gui.close(player);
            return;
        }

        colors = minetopiaPlayer.getColors().stream()
                .filter(color -> color.getClass().equals(type.correspondingClass()))
                .toList();

        ItemBuilder defaultIcon = new ItemBuilder(Material.IRON_INGOT)
                .addLoreLine("")
                .addLoreLine(DailyLife.getMessageConfiguration().message("color_menu_lore_expire_never", player))
                .setName(type.getDefaultColor() + DailyLife.getMessageConfiguration().message("color_menu_name_default", player));

        gui.addItem(new GuiItem(defaultIcon.toItemStack(), event -> {
            minetopiaPlayer.setActiveColor(null, type);
            player.sendMessage(ChatUtils.color(DailyLife.getMessageConfiguration().message("color_menu_selected", player)
                    .replace("<type>", type.getDisplayName())
                    .replace("<selected>", DailyLife.getMessageConfiguration().message("color_menu_name_default", player))));
        }));

        colors.forEach(color -> {
            ItemBuilder icon = new ItemBuilder(Material.IRON_INGOT)
                    .addLoreLine("")
                    .setName(color.displayName());

            if (color.getExpiresAt() != -1 && color.getExpiresAt() - System.currentTimeMillis() < -1)
                icon.addLoreLine(MessageConfiguration.component("color_menu_lore_expired"));
            if (color.getExpiresAt() != -1 && color.getExpiresAt() - System.currentTimeMillis() > -1)
                icon.addLoreLine(DailyLife.getMessageConfiguration().message("color_menu_lore_expire_time", player)
                        .replace("<time>", millisToTime(color.getExpiresAt() - System.currentTimeMillis(), player)));
            if (color.getExpiresAt() == -1) icon.addLoreLine(MessageConfiguration.component("color_expires_never"));

            gui.addItem(new GuiItem(icon.toItemStack(), event -> {
                minetopiaPlayer.setActiveColor(color, type);
                player.sendMessage(ChatUtils.color(DailyLife.getMessageConfiguration().message("color_menu_selected", player)
                        .replace("<type>", type.getDisplayName())
                        .replace("<selected>", color.displayName())));
            }));
        });

        gui.setItem(13, new GuiItem(new ItemBuilder(Material.LADDER).setName(DailyLife.getMessageConfiguration().message("go_back", player)).toItemStack(),
                e -> new ColorTypeMenu(player, target, minetopiaPlayer).open(player)));

        gui.setItem(14, new GuiItem(new ItemBuilder(Material.BARRIER).setName("<red>Locked").toItemStack(),
                e -> new ColorLockedMenu(player, this).open(player)));
    }

    private String millisToTime(long millis, OfflinePlayer player) {
        long totalSeconds = millis / 1000;
        long totalMinutes = totalSeconds / 60;
        long totalHours = totalMinutes / 60;

        long days = totalHours / 24;
        long hours = totalHours % 24;
        long minutes = totalMinutes % 60;
        long seconds = totalSeconds % 60;

        return DailyLife.getMessageConfiguration().message("time_format", player)
                .replace("<days>", String.valueOf(days))
                .replace("<hours>", String.valueOf(hours))
                .replace("<minutes>", String.valueOf(minutes))
                .replace("<seconds>", String.valueOf(seconds));
    }
}
