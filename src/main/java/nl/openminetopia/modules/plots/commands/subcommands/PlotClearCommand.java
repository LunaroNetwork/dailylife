package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.plots.utils.PlotUtil;
import org.bukkit.entity.Player;

@CommandAlias("plot|p")
public class PlotClearCommand extends BaseCommand {

    @Subcommand("clear|confiscate")
    @CommandPermission("ln.dl.plot.confiscate")
    @Description("Verwijderd alle members en spelers van een plot.")
    public void plotClear(Player player) {
        ProtectedRegion region = PlotUtil.getPlot(player.getLocation());
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (region == null) {
            player.sendMessage(DailyLife.getMessageConfiguration().component("plot_invalid_location", player));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            player.sendMessage(DailyLife.getMessageConfiguration().component("plot_invalid", player));
            return;
        }

        region.getOwners().clear();
        region.getMembers().clear();

        player.sendMessage(DailyLife.getMessageConfiguration().component("plot_clear_success", player));
    }
}
