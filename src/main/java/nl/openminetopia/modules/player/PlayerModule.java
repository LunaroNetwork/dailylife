package nl.openminetopia.modules.player;

import com.craftmend.storm.api.enums.Where;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.player.commands.PlaytimeCommand;
import nl.openminetopia.modules.player.configuration.LevelCheckConfiguration;
import nl.openminetopia.modules.player.listeners.LevelcheckNpcListener;
import nl.openminetopia.modules.player.listeners.PlayerPreLoginListener;
import nl.openminetopia.modules.player.listeners.PlayerQuitListener;
import nl.openminetopia.modules.player.models.PlayerModel;

import nl.openminetopia.modules.player.runnables.LevelCalculateRunnable;
import nl.openminetopia.modules.player.runnables.MinetopiaPlayerSaveRunnable;
import nl.openminetopia.modules.player.runnables.PlayerPlaytimeRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Setter @Getter
public class PlayerModule extends ExtendedSpigotModule {

    public PlayerModule(SpigotModuleManager<@NotNull DailyLife> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    private LevelCheckConfiguration configuration;
    private LevelCalculateRunnable levelCalculateRunnable;

    private MinetopiaPlayerSaveRunnable minetopiaPlayerSaveRunnable;
    private PlayerPlaytimeRunnable playerPlaytimeRunnable;

    @Override
    public void onEnable() {
        configuration = new LevelCheckConfiguration(DailyLife.getInstance().getDataFolder());
        configuration.saveConfiguration();

        registerComponent(new PlayerPreLoginListener());
        registerComponent(new PlayerQuitListener());
        if (DailyLife.getInstance().isNpcSupport()) registerComponent(new LevelcheckNpcListener());

        registerComponent(new PlaytimeCommand());

        levelCalculateRunnable = new LevelCalculateRunnable(this, PlayerManager.getInstance(), 5000L, 50, 30 * 1000L, () -> new ArrayList<>(PlayerManager.getInstance().getOnlinePlayers().keySet()));
        DailyLife.getInstance().registerDirtyPlayerRunnable(levelCalculateRunnable, 20L);

        minetopiaPlayerSaveRunnable = new MinetopiaPlayerSaveRunnable(PlayerManager.getInstance(), 5 * 60 * 1000L, 50, 30 * 60 * 1000L, () -> new ArrayList<>(PlayerManager.getInstance().getOnlinePlayers().keySet()), true);
        DailyLife.getInstance().registerDirtyPlayerRunnable(minetopiaPlayerSaveRunnable, 20L * 5);

        playerPlaytimeRunnable = new PlayerPlaytimeRunnable(PlayerManager.getInstance(), 1000L * 5, 50, 20 * 1000L, () -> new ArrayList<>(PlayerManager.getInstance().getOnlinePlayers().keySet()), true);
        DailyLife.getInstance().registerDirtyPlayerRunnable(playerPlaytimeRunnable, 20L);

    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getMinetopiaPlayer(player).join();
            if (minetopiaPlayer == null) continue;
            minetopiaPlayer.updatePlaytime();
            minetopiaPlayer.save().join();
        }
        DailyLife.getInstance().unregisterDirtyPlayerRunnable(levelCalculateRunnable);
        DailyLife.getInstance().unregisterDirtyPlayerRunnable(minetopiaPlayerSaveRunnable);
        DailyLife.getInstance().unregisterDirtyPlayerRunnable(playerPlaytimeRunnable);
    }

    private CompletableFuture<Optional<PlayerModel>> findPlayerModel(@NotNull UUID uuid) {
        CompletableFuture<Optional<PlayerModel>> completableFuture = new CompletableFuture<>();
        StormDatabase.getExecutorService().submit(() -> {
            try {
                Collection<PlayerModel> playerModel = StormDatabase.getInstance().getStorm().buildQuery(PlayerModel.class)
                        .where("uuid", Where.EQUAL, uuid.toString())
                        .limit(1).execute().join();

                Bukkit.getScheduler().runTaskLaterAsynchronously(DailyLife.getInstance(), () -> completableFuture.complete(playerModel.stream().findFirst()), 1L);
            } catch (Exception exception) {
                exception.printStackTrace();
                completableFuture.completeExceptionally(exception);
            }
        });
        return completableFuture;
    }

    public CompletableFuture<PlayerModel> getPlayerModel(UUID uuid) {
        CompletableFuture<PlayerModel> future = new CompletableFuture<>();

        findPlayerModel(uuid).thenAccept(playerModel -> {
            if (playerModel.isEmpty()) {
                PlayerModel createdModel = new PlayerModel();
                createdModel.setUniqueId(uuid);
                createdModel.setPlaytime(0L);
                createdModel.setWageTime(0L);
                createdModel.setLevel(1);
                createdModel.setActivePrefixId(-1);
                createdModel.setActivePrefixColorId(-1);
                createdModel.setActiveChatColorId(-1);
                createdModel.setActiveLevelColorId(-1);
                createdModel.setStaffchatEnabled(false);
                createdModel.setCommandSpyEnabled(false);
                createdModel.setChatSpyEnabled(false);
                createdModel.setPrefixes(new ArrayList<>());
                createdModel.setColors(new ArrayList<>());
                createdModel.setFitnessReset(false);
                future.complete(createdModel);

                StormDatabase.getInstance().saveStormModel(createdModel);
                return;
            }

            future.complete(playerModel.get());
        });

        return future;
    }
}
