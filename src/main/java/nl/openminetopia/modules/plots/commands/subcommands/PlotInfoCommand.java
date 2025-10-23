package nl.openminetopia.modules.plots.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.Component;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.language.MessageConfiguration;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.plots.utils.PlotUtil;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandAlias("plotinfo|pi")
public class PlotInfoCommand extends BaseCommand {

    @Default
    @Description("Bekijk informatie van een plot.")
    @CommandPermission("ln.dl.plot.info")
    public void plotInfo(Player player) {
        ProtectedRegion region = PlotUtil.getPlot(player.getLocation());

        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);
        if (minetopiaPlayer == null) return;

        if (region == null) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_invalid_location"));
            return;
        }

        if (region.getFlag(PlotModule.PLOT_FLAG) == null) {
            ChatUtils.sendFormattedMessage(minetopiaPlayer, MessageConfiguration.message("plot_not_valid"));
            return;
        }

        String owners = region.getOwners().getUniqueIds().stream()
                .map(ownerId -> Bukkit.getOfflinePlayer(ownerId).getName())
                .collect(Collectors.joining(", "));

        String members = region.getMembers().getUniqueIds().stream()
                .map(memberId -> Bukkit.getOfflinePlayer(memberId).getName())
                .collect(Collectors.joining(", "));

        ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("multi_message_title", player)
                .replace("<text>", MessageConfiguration.message("plot_info_title")));
        ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("multi_message_value", player)
                .replace("<text>", MessageConfiguration.message("plot_info_plotnumber"))
                .replace("<plot>", region.getId()));
        ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("multi_message_value", player)
                .replace("<text>", MessageConfiguration.message("plot_info_owners"))
                .replace("<owners>", (region.getOwners().size() > 0 ? owners : "Geen.")));
        ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("multi_message_value", player)
                .replace("<text>", MessageConfiguration.message("plot_info_members"))
                .replace("<members>", (region.getMembers().size() > 0 ? members : "Geen.")));
        ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("multi_message_category", player)
                .replace("<text>", MessageConfiguration.message("plot_info_staffinfo")));

        if(!player.hasPermission("ln.dl.plot.info.staff")) return;
        if (region.getFlag(PlotModule.PLOT_DESCRIPTION) != null) {
            String description = region.getFlag(PlotModule.PLOT_DESCRIPTION);
            if (description != null && !description.isEmpty())
                ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("multi_message_value", player)
                        .replace("<text>", MessageConfiguration.message("plot_info_description"))
                        .replace("<description>", description));
        }else{
            ChatUtils.sendFormattedMessage(minetopiaPlayer, DailyLife.getMessageConfiguration().message("multi_message_value", player)
                    .replace("<text>", MessageConfiguration.message("plot_info_description"))
                    .replace("<description>", ""));
        }
    }
}
