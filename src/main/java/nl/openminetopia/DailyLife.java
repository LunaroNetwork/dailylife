package nl.openminetopia;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.MessageType;
import co.aikar.commands.PaperCommandManager;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import com.jeff_media.customblockdata.CustomBlockData;
import io.vertx.core.Vertx;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.configuration.language.MessageConfiguration;
import nl.openminetopia.framework.runnables.AbstractDirtyRunnable;
import nl.openminetopia.framework.runnables.listeners.PlayerLifecycleListener;
import nl.openminetopia.modules.color.ColorModule;
import nl.openminetopia.modules.language.LanguageDatabase;
import nl.openminetopia.registry.CommandComponentRegistry;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.input.ChatInputHandler;
import nl.openminetopia.utils.placeholderapi.OpenMinetopiaExpansion;
import nl.openminetopia.utils.wrappers.listeners.CitzensNpcClickListener;
import nl.openminetopia.utils.wrappers.listeners.FancyNpcClickListener;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter @Setter
public final class DailyLife extends JavaPlugin {

    @Getter
    private static DailyLife instance;

    @Getter @Setter
    private static LanguageDatabase languageDatabase;

    @Getter @Setter(AccessLevel.PRIVATE)
    private static SpigotModuleManager<@NotNull DailyLife> moduleManager;

    @Getter @Setter
    private static PaperCommandManager commandManager;

    @Getter @Setter
    private static DefaultConfiguration defaultConfiguration;

    @Getter @Setter
    private static MessageConfiguration messageConfiguration;

    @Getter
    private static ChatInputHandler chatInputHandler;

    @Getter
    private static MiniMessage miniMessage;
    private final Map<String, String> colorCodes = new HashMap<>();

    @Getter
    private final List<AbstractDirtyRunnable<UUID>> dirtyPlayerRunnables = new CopyOnWriteArrayList<>();

    private boolean npcSupport = false;

    private boolean labymodSupport = false;

    private Vertx vertx;

    public DailyLife() {
        instance = this;
        moduleManager = new SpigotModuleManager<>(this, getComponentLogger());
    }

    @Override
    public void onEnable() {
        commandManager = new PaperCommandManager(this);
        moduleManager.setDebug(false);

        messageConfiguration = new MessageConfiguration(getDataFolder());
        messageConfiguration.saveConfiguration();

        if (defaultConfiguration.isMetricsEnabled()) {
            Metrics metrics = new Metrics(this, 23547);
            metrics.addCustomChart(new SimplePie("storage", () -> defaultConfiguration.getDatabaseType().toString()));
        }

        languageDatabase = new LanguageDatabase();
        try {
            languageDatabase.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        commandManager.enableUnstableAPI("help");
        commandManager.setFormat(MessageType.HELP, 1, ChatColor.GOLD);
        commandManager.setFormat(MessageType.HELP, 2, ChatColor.YELLOW);
        commandManager.setFormat(MessageType.HELP, 3, ChatColor.GRAY);

        if (getConfig().isConfigurationSection("message-colors")) {
            for (String key : getConfig().getConfigurationSection("message-colors").getKeys(false)) {
                String hex = getConfig().getString("message-colors." + key);
                if (hex != null && TextColor.fromCSSHexString(hex) != null) {
                    colorCodes.put(key, hex);
                }
            }
        }

        TagResolver.Builder resolverBuilder = TagResolver.builder();
        resolverBuilder.resolver(StandardTags.defaults());

        for (Map.Entry<String, String> entry : colorCodes.entrySet()) {
            TextColor color = TextColor.fromCSSHexString(entry.getValue());
            if (color != null) {
                resolverBuilder.resolver(
                        TagResolver.resolver(entry.getKey(), (args, ctx) -> Tag.styling(color))
                );
            }
        }

        this.miniMessage = MiniMessage.builder()
                .tags(resolverBuilder.build())
                .build();

        chatInputHandler = new ChatInputHandler();
        Bukkit.getPluginManager().registerEvents(chatInputHandler, this);

        CustomBlockData.registerListener(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new OpenMinetopiaExpansion().register();
            getLogger().info("Registered PlaceholderAPI expansion.");
        }

        // Registering of NPC wrapper listeners
        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            getLogger().info("Initializing Citizens support.");
            Bukkit.getPluginManager().registerEvents(new CitzensNpcClickListener(), this);
            npcSupport = true;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("FancyNpcs")) {
            getLogger().info("Initializing FancyNpcs support.");
            Bukkit.getPluginManager().registerEvents(new FancyNpcClickListener(), this);
            npcSupport = true;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("LabyModServerAPI")) {
            getLogger().info("Initializing LabyModServerAPI support.");
            labymodSupport = true;
        }

        moduleManager.getComponentRegistry().registerComponentHandler(
                BaseCommand.class,
                new CommandComponentRegistry(commandManager)
        );
        moduleManager.enable();
        getServer().getPluginManager().registerEvents(new PlayerLifecycleListener(this), this);
    }

    @Override
    public void onDisable() {
        moduleManager.disable();
    }

    @Override
    public void onLoad() {
        defaultConfiguration = new DefaultConfiguration(getDataFolder());
        defaultConfiguration.saveConfiguration();

        moduleManager.scanModules(this.getClass());
        moduleManager.load();
    }

    public Vertx getOrCreateVertx() {
        if (vertx == null) {
            vertx = Vertx.vertx();
        }
        return vertx;
    }

    public void registerDirtyPlayerRunnable(AbstractDirtyRunnable<UUID> runnable, long periodTicks) {
        dirtyPlayerRunnables.add(runnable);
        runnable.start(DailyLife.getInstance(), periodTicks);
    }

    public void unregisterDirtyPlayerRunnable(AbstractDirtyRunnable<UUID> runnable) {
        dirtyPlayerRunnables.remove(runnable);
        runnable.cancel();
    }

}