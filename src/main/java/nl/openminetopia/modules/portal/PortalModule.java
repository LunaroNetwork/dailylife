package nl.openminetopia.modules.portal;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.portal.commands.LinkCommand;
import org.jetbrains.annotations.NotNull;

public class PortalModule extends ExtendedSpigotModule {

    public PortalModule(SpigotModuleManager<@NotNull DailyLife> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        if (DailyLife.getDefaultConfiguration().isPortalEnabled()) {
            registerComponent(new LinkCommand());
        }
    }



    public String getPortalUrl() {
        return DailyLife.getDefaultConfiguration().getPortalUrl();
    }

    public String getPortalApiUrl() {
        return "https://" + getPortalUrl() + "/api";
    }
}
