package nl.openminetopia.modules.staff.mod.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.player.events.PlayerLevelChangeEvent;
import nl.openminetopia.modules.plots.PlotModule;
import nl.openminetopia.modules.prefix.models.PrefixModel;
import nl.openminetopia.modules.prefix.objects.Prefix;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.events.EventUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("mod")
public class ModInfoCommand extends BaseCommand {

    @Subcommand("info")
    @Syntax("<player>")
    @CommandPermission("ln.dl.mod.info")
    @CommandCompletion("@players")
    @Description("Get plugin info about a player.")
    public void info(Player player, OfflinePlayer offlinePlayer) {
        if (offlinePlayer.getPlayer() == null) {
            player.sendMessage("This player does not exist.");
            return;
        }

        PlayerManager.getInstance().getMinetopiaPlayer(offlinePlayer.getPlayer()).whenComplete((minetopiaPlayer, throwable1) -> {
            if (minetopiaPlayer == null) return;

            // Title
            player.sendMessage(ChatUtils.format(minetopiaPlayer,
                    DailyLife.getMessageConfiguration().message("multi_message_title", player)
                    .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_title", player))
            ));

            //global
            player.sendMessage(ChatUtils.format(minetopiaPlayer,
                    DailyLife.getMessageConfiguration().message("multi_message_value", player)
                            .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_name", player))
            ));
            player.sendMessage(ChatUtils.format(minetopiaPlayer,
                    DailyLife.getMessageConfiguration().message("multi_message_value", player)
                            .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_uuid", player))
            ));
            player.sendMessage(ChatUtils.format(minetopiaPlayer,
                    DailyLife.getMessageConfiguration().message("multi_message_value", player)
                            .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_hometown", player))
                            .replace("<hometown>", minetopiaPlayer.getHometown().getName())
            ));
            player.sendMessage(ChatUtils.format(minetopiaPlayer,
                    DailyLife.getMessageConfiguration().message("multi_message_value", player)
                            .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_level", player))
            ));
            player.sendMessage(ChatUtils.format(minetopiaPlayer,
                    DailyLife.getMessageConfiguration().message("multi_message_value", player)
                            .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_balance", player))
            ));

            // Prefixes
            player.sendMessage(ChatUtils.format(minetopiaPlayer,
                    DailyLife.getMessageConfiguration().message("multi_message_category", player)
                            .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_prefix_category", player))
            ));
            for(Prefix prefix : minetopiaPlayer.getPrefixes()){
                player.sendMessage(ChatUtils.format(minetopiaPlayer,
                        DailyLife.getMessageConfiguration().message("multi_message_list", player)
                                .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_prefix_list", player))
                                .replace("<prefixes>", prefix.getPrefix())
                ));
            }

            // Plots
            player.sendMessage(ChatUtils.format(minetopiaPlayer,
                    DailyLife.getMessageConfiguration().message("multi_message_category", player)
                            .replace("<text>", DailyLife.getMessageConfiguration().message("staff_mod_info_plot_category", player))
            ));

            World world = BukkitAdapter.adapt(player.getWorld());

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager manager = container.get(world);

            if (manager == null) {
                player.sendMessage(DailyLife.getMessageConfiguration().component("plot_error_manager_loading", player));
                return;
            }

            Map<String, String> regionNames = new HashMap<>();
            manager.getRegions().entrySet().stream()
                    .filter(entry -> entry.getValue().getFlag(PlotModule.PLOT_FLAG) != null)
                    .filter(entry -> entry.getValue().getOwners().contains(offlinePlayer.getUniqueId()) || entry.getValue().getMembers().contains(offlinePlayer.getUniqueId()))
                    .forEach(entry -> regionNames.put(entry.getKey(), entry.getValue().getOwners().contains(offlinePlayer.getUniqueId()) ? "Owner" : "Member"));
            List<Map.Entry<String, String>> entries = new ArrayList<>(regionNames.entrySet());



            for(int i = 1; i < entries.size(); i++){
                Map.Entry<String, String> entry = entries.get(i);
                player.sendMessage(ChatUtils.format(minetopiaPlayer,
                        DailyLife.getMessageConfiguration().message("multi_message_list", player)
                                .replace("<text>", entry.getKey() + " <gray>(" + entry.getValue() + ")")
                ));
            }

        });
    }
}
