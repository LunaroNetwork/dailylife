package nl.openminetopia.modules.actionbar;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.actionbar.commands.ActionbarCommand;
import nl.openminetopia.modules.actionbar.listeners.ActionbarJoinListener;
import nl.openminetopia.modules.actionbar.runnables.ActionbarRunnable;
import nl.openminetopia.modules.data.DataModule;
import org.jetbrains.annotations.NotNull;

public class ActionbarModule extends ExtendedSpigotModule {

    @Getter
    private ActionbarRunnable actionbarRunnable;

    public ActionbarModule(SpigotModuleManager<@NotNull DailyLife> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new ActionbarJoinListener());
        registerComponent(new ActionbarCommand());

        this.actionbarRunnable = new ActionbarRunnable(PlayerManager.getInstance(), 1000, 100, 1500, () -> PlayerManager.getInstance().getOnlinePlayers().keySet().stream().toList());
        DailyLife.getInstance().registerDirtyPlayerRunnable(actionbarRunnable, 20L);
    }

    @Override
    public void onDisable() {
        DailyLife.getInstance().unregisterDirtyPlayerRunnable(actionbarRunnable);
    }
}
