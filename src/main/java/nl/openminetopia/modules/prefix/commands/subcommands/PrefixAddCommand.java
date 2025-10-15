package nl.openminetopia.modules.prefix.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.player.utils.PlaytimeUtil;
import nl.openminetopia.modules.prefix.objects.Prefix;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("prefix")
public class PrefixAddCommand extends BaseCommand {

    /**
     * Add a prefix to a player.
     *
     * @param expiresAt The time in minutes when the prefix expires. (-1 for never)
     */
    @Subcommand("add")
    @Syntax("<speler> <minuten> <prefix>")
    @CommandCompletion("@players -1 @nothing")
    @CommandPermission("ln.dl.prefix.add")
    @Description("Voeg een prefix toe aan een speler voor een bepaalde tijd.")
    public void addPrefix(Player player, OfflinePlayer offlinePlayer, Integer expiresAt, String prefix) {
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (offlinePlayer == null) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("player_not_found", minetopiaPlayer.getBukkit()));
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer).whenComplete((targetMinetopiaPlayer, throwable1) -> {
            if (targetMinetopiaPlayer == null) {
                ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("player_not_found", minetopiaPlayer.getBukkit()));
                return;
            }

            for (Prefix prefix1 : targetMinetopiaPlayer.getPrefixes()) {
                if (prefix1.getPrefix().equalsIgnoreCase(prefix)) {
                    ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("prefix_already_exists", minetopiaPlayer.getBukkit())
                            .replace("<player>", (offlinePlayer.getName() == null ? "null" : offlinePlayer.getName()))
                            .replace("<prefix>", prefix));
                    return;
                }
            }

            long expiresAtMillis = System.currentTimeMillis() + minutesToMillis(expiresAt);
            if (expiresAt == -1) expiresAtMillis = -1;

            Prefix prefixModel = new Prefix(prefix, expiresAtMillis);
            targetMinetopiaPlayer.addPrefix(prefixModel);
            targetMinetopiaPlayer.setActivePrefix(prefixModel);

            ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("prefix_added", minetopiaPlayer.getBukkit())
                    .replace("<player>", (offlinePlayer.getName() == null ? "null" : offlinePlayer.getName()))
                    .replace("<prefix>", prefix)
                    .replace("<time>", expiresAt == -1 ? "nooit" : PlaytimeUtil.formatPlaytime(minutesToMillis(expiresAt), offlinePlayer)));
        });
    }

    private long minutesToMillis(int minutes) {
        return minutes * 60 * 1000L;
    }
}