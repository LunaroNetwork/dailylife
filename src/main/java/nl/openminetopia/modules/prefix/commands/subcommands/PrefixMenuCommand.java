package nl.openminetopia.modules.prefix.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.prefix.menus.PrefixMenu;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("prefix")
public class PrefixMenuCommand extends BaseCommand {

    @Subcommand("menu")
    @Description("Open het prefix menu.")
    @CommandCompletion("@players")
    public void prefixMenu(Player player, @Optional OfflinePlayer target) {
        OfflinePlayer targetPlayer = (target != null) ? target : player;

        if (!targetPlayer.getUniqueId().equals(player.getUniqueId()) &&
                !player.hasPermission("openminetopia.prefix.menu.others")) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("prefix_menu_other_no_permission", player));
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(targetPlayer).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) return;
            new PrefixMenu(player, targetPlayer, minetopiaPlayer).open(player);
        });
    }
}
