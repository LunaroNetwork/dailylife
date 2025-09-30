package nl.openminetopia.modules.books;

import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.books.commands.BooksCommand;
import nl.openminetopia.modules.books.configuration.BooksConfiguration;
import nl.openminetopia.modules.books.objects.CustomBook;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter @Setter
public class BooksModule extends ExtendedSpigotModule {
    public BooksModule(SpigotModuleManager<@NotNull DailyLife> moduleManager) {
        super(moduleManager);
    }

    private BooksConfiguration configuration;
    private Map<UUID, Map<String, String>> variableResponses;

    @Override
    public void onEnable() {
        configuration = new BooksConfiguration(DailyLife.getInstance().getDataFolder());
        variableResponses = new HashMap<>();

        registerComponent(new BooksCommand());

        DailyLife.getCommandManager().getCommandCompletions().registerAsyncCompletion("books", c -> {
            if (!(c.getSender() instanceof Player player)) return null;

            return configuration.getCustomBooks().stream()
                    .map(CustomBook::getIdentifier)
                    .filter(identifier -> player.hasPermission("openminetopia.books." + identifier))
                    .toList();
        });
    }
}
