package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.DailyLife;
import nl.openminetopia.configuration.language.MessageConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.plots.utils.PlotUtil;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.WorldGuardUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotOwnersCommand extends BaseCommand {

    @Subcommand("addowner")
    @Description("Voegt een speler toe aan een plot.")
    @CommandPermission("openminetopia.plot.addowner")
    @CommandCompletion("@players @plotName")
    @Syntax("<speler> <region>")
    public void addPlotOwner(Player player, OfflinePlayer offlinePlayer, @Optional String regionName) {
        ProtectedRegion region = PlotUtil.getPlot(player.getLocation());
        if (regionName != null) {
            region = PlotUtil.getPlot(player.getWorld(), regionName);
        }

        if (offlinePlayer == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("player_not_found", player));
            return;
        }

        if (region == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_invalid_location", player));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_invalid", player));
            return;
        }

        if (region.getOwners().contains(offlinePlayer.getUniqueId())) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_owner_already", player)
                    .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() :
                            DailyLife.getMessageConfiguration().message("unknown", player)));
            return;
        }

        if (region.getMembers().contains(offlinePlayer.getUniqueId())) {
            region.getMembers().removePlayer(offlinePlayer.getUniqueId());
        }

        region.getOwners().addPlayer(offlinePlayer.getUniqueId());
        ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_owner_added", player)
                .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() :
                        DailyLife.getMessageConfiguration().message("unknown", player)));
    }

    @Subcommand("setowner")
    @Description("Zet ee, speler als eigenaar op een plot.")
    @CommandPermission("ln.dl.plot.setowner")
    @CommandCompletion("@players @plotName")
    @Syntax("<speler> <region>")
    public void setPlotOwner(Player player, OfflinePlayer offlinePlayer, @Optional String regionName) {
        ProtectedRegion region = PlotUtil.getPlot(player.getLocation());
        if (regionName != null) {
            region = PlotUtil.getPlot(player.getWorld(), regionName);
        }

        if (offlinePlayer == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("player_not_found", player));
            return;
        }

        if (region == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_invalid_location", player));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_invalid", player));
            return;
        }

        if (region.getMembers().contains(offlinePlayer.getUniqueId())) {
            region.getMembers().removePlayer(offlinePlayer.getUniqueId());
        }

        region.getOwners().clear();
        region.getOwners().addPlayer(offlinePlayer.getUniqueId());
        ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_owner_set", player)
                .replace("<player>", offlinePlayer.getName() != null ? offlinePlayer.getName() :
                        DailyLife.getMessageConfiguration().message("unknown", player)));
    }

    @Subcommand("removeowner")
    @Description("Verwijdert een speler van een plot.")
    @CommandPermission("ln.dl.plot.removeowner")
    @CommandCompletion("@players @plotName")
    @Syntax("<speler> <region>")
    public void removePlotOwner(Player player, OfflinePlayer offlinePlayer, @Optional String regionName) {
        ProtectedRegion region = WorldGuardUtils.getProtectedRegion(player.getLocation(), priority -> priority >= 0);
        if (regionName != null) {
            region = WorldGuardUtils.getProtectedRegions(player.getWorld(), p -> p >= 0)
                    .stream()
                    .filter(r -> r.getId().equalsIgnoreCase(regionName))
                    .findFirst()
                    .orElse(null);
        }

        PlayerProfile profile = offlinePlayer.getPlayerProfile();
        
        if (region == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_invalid_location", player));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_invalid", player));
            return;
        }

        if (!region.getOwners().contains(profile.getId())) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_owner_not_added", player)
                    .replace("<player>", profile.getName() != null ? profile.getName() :
                            DailyLife.getMessageConfiguration().message("unknown", player)));
            return;
        }

        region.getOwners().removePlayer(profile.getId());
        ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("plot_owner_removed", player)
                .replace("<player>", profile.getName() != null ? profile.getName() :
                        DailyLife.getMessageConfiguration().message("unknown", player)));
    }
}
