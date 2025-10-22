package nl.openminetopia.modules.staff;

import nl.openminetopia.modules.staff.mod.commands.subcommands.*;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.staff.admintool.commands.AdminToolCommand;
import nl.openminetopia.modules.staff.admintool.commands.subcommands.AdminToolGetCommand;
import nl.openminetopia.modules.staff.admintool.commands.subcommands.AdminToolOpenCommand;
import nl.openminetopia.modules.staff.admintool.listeners.PlayerDropItemListener;
import nl.openminetopia.modules.staff.admintool.listeners.PlayerEntityInteractListener;
import nl.openminetopia.modules.staff.admintool.listeners.PlayerInteractListener;
import nl.openminetopia.modules.staff.chat.commands.StaffchatCommand;
import nl.openminetopia.modules.staff.chat.listeners.PlayerChatListener;
import nl.openminetopia.modules.staff.mod.commands.ModCommand;
import org.jetbrains.annotations.NotNull;

public class StaffModule extends ExtendedSpigotModule {
    public StaffModule(SpigotModuleManager<@NotNull DailyLife> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        registerComponent(new ModCommand());
        registerComponent(new ModSetLevelCommand());
        registerComponent(new ModChatSpyCommand());
        registerComponent(new ModCommandSpyCommand());
        registerComponent(new ModInfoCommand());
        registerComponent(new ModHometownCommand());
        registerComponent(new ModSeenCommand());

        registerComponent(new AdminToolCommand());
        registerComponent(new AdminToolOpenCommand());
        registerComponent(new AdminToolGetCommand());

        registerComponent(new StaffchatCommand());
        registerComponent(new PlayerChatListener());

        registerComponent(new PlayerDropItemListener());
        registerComponent(new PlayerInteractListener());
        registerComponent(new PlayerEntityInteractListener());
    }
}
