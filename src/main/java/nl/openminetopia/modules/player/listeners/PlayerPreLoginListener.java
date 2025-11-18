package nl.openminetopia.modules.player.listeners;

import net.kyori.adventure.text.Component;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.configuration.language.MessageConfiguration;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerPreLoginListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerPreLogin(final AsyncPlayerPreLoginEvent event) {
        PlayerManager.getInstance().getOnlinePlayers().remove(event.getUniqueId());

        if (Bukkit.isPrimaryThread()) {
            DailyLife.getInstance().getLogger().severe("PlayerPreLoginEvent is called on the main thread! Dit mag niet!");
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    MessageConfiguration.component("player_data_not_loaded"));
            return;
        }

        long startTime = System.currentTimeMillis();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(event.getUniqueId());

        try {
            // Data direct laden (blokkerend)
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance()
                    .getMinetopiaPlayer(offlinePlayer)
                    .join(); // join() wacht tot future klaar is

            if (minetopiaPlayer == null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        MessageConfiguration.component("player_data_not_loaded"));
                DailyLife.getInstance().getLogger().warning("Error: MinetopiaPlayer is null");
                return;
            }

            // Data opslaan in memory
            PlayerManager.getInstance().getOnlinePlayers().put(event.getUniqueId(), minetopiaPlayer);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            DailyLife.getInstance().getLogger().info("Loaded player data for "
                    + offlinePlayer.getName() + " (" + offlinePlayer.getUniqueId() + ") in " + duration + "ms");

        } catch (Exception ex) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    MessageConfiguration.component("player_data_not_loaded"));
            DailyLife.getInstance().getLogger().warning("Error loading player model: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Haal de al geladen MinetopiaPlayer op
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (minetopiaPlayer != null) {
            // Data is geladen → stuur bericht naar de speler zelf
            ChatUtils.sendMessage(player,
                    DailyLife.getMessageConfiguration().message("player_data_loaded", player));
        } else {
            // Fail-safe: dit zou normaal nooit gebeuren
            DailyLife.getInstance().getLogger().warning("MinetopiaPlayer not found for " + player.getName());
        }

        event.joinMessage(Component.text(""));
        for (Player target : Bukkit.getOnlinePlayers()) {
            if(!target.canSee(player)) continue;
            ChatUtils.sendMessage(target,
                    DailyLife.getMessageConfiguration().message("player_server_join", target)
                            .replace("<username>", player.getName()));
        }
    }
}