package nl.openminetopia.modules.teleporter;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.teleporter.commands.TeleporterCommand;
import nl.openminetopia.modules.teleporter.commands.subcommands.TeleporterCreateCommand;
import nl.openminetopia.modules.teleporter.listeners.TeleporterInteractListener;
import nl.openminetopia.modules.teleporter.listeners.block.TeleporterBreakListener;
import nl.openminetopia.modules.teleporter.listeners.block.TeleporterPlaceListener;
import org.jetbrains.annotations.NotNull;

public final class TeleporterModule extends ExtendedSpigotModule {

    public TeleporterModule(SpigotModuleManager<@NotNull DailyLife> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new TeleporterCommand());
        registerComponent(new TeleporterCreateCommand());

        registerComponent(new TeleporterPlaceListener());
        registerComponent(new TeleporterInteractListener());
        registerComponent(new TeleporterBreakListener());
    }



}
