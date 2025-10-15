package nl.openminetopia.modules.staff.mod.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.player.utils.PlaytimeUtil;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("mod")
public class ModSeenCommand extends BaseCommand {

    @Subcommand("seen")
    @Syntax("<player>")
    @CommandPermission("ln.dl.mod.seen")
    @CommandCompletion("@players")
    @Description("Bekijk wanneer een speler voor het laatst online was.")
    public void seen(Player executor, OfflinePlayer target) {

        PlayerManager.getInstance().getMinetopiaPlayer(target).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) {
                executor.sendMessage(DailyLife.getMessageConfiguration().component("player_not_found", executor));
                return;
            }

            if (target.isOnline()) {
                executor.sendMessage(ChatUtils.color(DailyLife.getMessageConfiguration().message("staff_mod_seen_online", executor)
                        .replace("<player>", target.getName())
                ));
                return;
            }

            long lastSeen = minetopiaPlayer.getLastSeen();
            if (lastSeen == 0L) {
                executor.sendMessage(ChatUtils.color(DailyLife.getMessageConfiguration().message("staff_mod_seen_nodata", executor)
                        .replace("<player>", target.getName())
                ));
                return;
            }

            executor.sendMessage(ChatUtils.color(DailyLife.getMessageConfiguration().message("staff_mod_seen_offline", executor)
                    .replace("<player>", target.getName())
                    .replace("<time>", PlaytimeUtil.formatPlaytime(System.currentTimeMillis() - lastSeen, executor))
            ));
        });
    }
}
