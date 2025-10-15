package nl.openminetopia.modules.player.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.language.MessageConfiguration;
import nl.openminetopia.modules.player.utils.PlaytimeUtil;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("playtime|time|ptime")
public class PlaytimeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Description("Get your or another player's playtime.")
    public void playtime(Player player, @Optional OfflinePlayer target) {

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("database_read_error", player));
            return;
        }

        if (target == null || !player.hasPermission("openminetopia.playtime.others")) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("player_time_self", player)
                    .replace("<playtime>", PlaytimeUtil.formatPlaytime(minetopiaPlayer.getPlaytime(), player)));
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(target).whenComplete((targetMinetopiaPlayer, throwable1) -> {
            if (targetMinetopiaPlayer == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("player_not_found", player));
                return;
            }

            ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("player_time_other_player", player)
                    .replace("<player>", target.getName() == null ? "null" : target.getName())
                    .replace("<playtime>", PlaytimeUtil.formatPlaytime(targetMinetopiaPlayer.getPlaytime(), player)));
        });
    }
}
