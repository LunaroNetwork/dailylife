package nl.openminetopia.modules.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.data.DataModule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

@Getter
public class SkriptModule extends ExtendedSpigotModule {

    public SkriptModule(SpigotModuleManager<@NotNull DailyLife> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    private SkriptAddon addon;

    @Override
    public void onEnable() {
        if(!Bukkit.getServer().getPluginManager().isPluginEnabled("Skript")) return;

        addon = Skript.registerAddon(DailyLife.getInstance());
        try {
            addon.loadClasses("nl.openminetopia.modules.skript", "expressions");
            addon.loadClasses("nl.openminetopia.modules.skript", "effects");
        } catch (IOException e) {
            DailyLife.getInstance().getLogger().severe("Failed to load classes for Skript module");
        }
    }
}
