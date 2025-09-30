package nl.openminetopia.api.player;

import lombok.Getter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.player.PlayerModule;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class PlayerManager {

    private static PlayerManager instance;

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    private final PlayerModule playerModule = DailyLife.getModuleManager().get(PlayerModule.class);
    private final Map<UUID, MinetopiaPlayer> onlinePlayers = new ConcurrentHashMap<>();
    private final Map<UUID, CompletableFuture<MinetopiaPlayer>> loadingPlayers = new ConcurrentHashMap<>();

    /**
     * Retrieves a {@link MinetopiaPlayer} object for an online player.
     * <p>
     * This method checks the {@code onlinePlayers} map to find the {@link MinetopiaPlayer} associated with the given {@link Player}.
     * </p>
     * <ul>
     *   <li>If the player is not online or not in the map, this method returns {@code null}.</li>
     *   <li>For offline players, use {@link #getMinetopiaPlayer(OfflinePlayer)} to load the player data asynchronously.</li>
     * </ul>
     *
     * @param player The {@link Player} whose {@link MinetopiaPlayer} object should be retrieved.
     * @return The {@link MinetopiaPlayer} object if the player is online; {@code null} otherwise.
     */
    public MinetopiaPlayer getOnlineMinetopiaPlayer(Player player) {
        return onlinePlayers.get(player.getUniqueId());
    }

    /**
     * Retrieves a {@link MinetopiaPlayer} object for the given player.
     * <ul>
     *   <li>If the player is online, the object will be retrieved from the onlinePlayers map.</li>
     *   <li>If the player is offline, the object will be loaded from the database.</li>
     *   <li>If the player is not found in the database, a new {@link MinetopiaPlayer} object will be created.</li>
     * </ul>
     * <p>
     * Note: Use {@link #getOnlineMinetopiaPlayer(Player)} if you are certain the player is online.
     * </p>
     *
     * @param player The {@link OfflinePlayer} for whom the {@link MinetopiaPlayer} object should be retrieved.
     * @return A {@link CompletableFuture} containing the {@link MinetopiaPlayer} object.
     * @since 1.3.0
     */
    public CompletableFuture<MinetopiaPlayer> getMinetopiaPlayer(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();

        if (onlinePlayers.containsKey(uuid)) {
            return CompletableFuture.completedFuture(onlinePlayers.get(uuid));
        }

        // Prevent duplicate loads
        return loadingPlayers.computeIfAbsent(uuid, id -> {
            CompletableFuture<MinetopiaPlayer> future = new CompletableFuture<>();

            this.playerModule.getPlayerModel(uuid).whenComplete((playerModel, throwable) -> {
                if (throwable != null) {
                    future.completeExceptionally(throwable);
                    loadingPlayers.remove(uuid);
                    return;
                }

                MinetopiaPlayer minetopiaPlayer = new MinetopiaPlayer(uuid, playerModel);
                minetopiaPlayer.load().join();
                if (player.isOnline()) {
                    onlinePlayers.put(uuid, minetopiaPlayer);
                }

                future.complete(minetopiaPlayer);
                loadingPlayers.remove(uuid);
            });

            return future;
        });
    }
}