package nl.openminetopia.utils.modules;

import com.jazzkuh.modulemanager.spigot.SpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.DailyLife;
import org.jetbrains.annotations.NotNull;

public class ExtendedSpigotModule extends SpigotModule<@NotNull DailyLife> {

    public ExtendedSpigotModule(SpigotModuleManager<@NotNull DailyLife> moduleManager) {
        super(moduleManager);
    }

    @Override
    public boolean shouldLoad() {
        return !DailyLife.getDefaultConfiguration().isModuleDisabled(this.getClass());
    }
}