package nl.openminetopia.modules.data;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.data.adapters.DatabaseAdapter;
import nl.openminetopia.modules.data.adapters.utils.AdapterUtil;
import nl.openminetopia.modules.data.types.DatabaseType;
import org.jetbrains.annotations.NotNull;

@Getter
public class DataModule extends ExtendedSpigotModule {

    private DatabaseAdapter adapter;

    public DataModule(SpigotModuleManager<@NotNull DailyLife> moduleManager) {
        super(moduleManager);
    }

    @Override
    public void onEnable() {
        DefaultConfiguration configuration = DailyLife.getDefaultConfiguration();
        DatabaseType type = configuration.getDatabaseType();

        adapter = AdapterUtil.getAdapter(type);
        adapter.connect();
    }
}
