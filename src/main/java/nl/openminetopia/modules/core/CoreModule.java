package nl.openminetopia.modules.core;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.core.commands.OpenMinetopiaCommand;
import org.jetbrains.annotations.NotNull;

public class CoreModule extends ExtendedSpigotModule {
    public CoreModule(SpigotModuleManager<@NotNull DailyLife> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new OpenMinetopiaCommand());
    }
}
