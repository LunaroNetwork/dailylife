package nl.openminetopia.modules.color;

import com.craftmend.storm.api.enums.Where;
import nl.openminetopia.modules.color.models.HometownModel;
import nl.openminetopia.modules.places.models.CityModel;
import nl.openminetopia.utils.modules.ExtendedSpigotModule;
import com.jazzkuh.modulemanager.spigot.SpigotModuleManager;
import lombok.Getter;
import lombok.Setter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.color.commands.ColorCommand;
import nl.openminetopia.modules.color.commands.subcommands.ColorAddCommand;
import nl.openminetopia.modules.color.commands.subcommands.ColorCreateCommand;
import nl.openminetopia.modules.color.commands.subcommands.ColorRemoveCommand;
import nl.openminetopia.modules.color.configuration.ColorsConfiguration;
import nl.openminetopia.modules.color.configuration.components.ColorComponent;
import nl.openminetopia.modules.color.enums.OwnableColorType;
import nl.openminetopia.modules.color.models.ColorModel;
import nl.openminetopia.modules.color.objects.*;
import nl.openminetopia.modules.data.DataModule;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.modules.data.utils.StormUtils;
import nl.openminetopia.modules.player.models.PlayerModel;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ColorModule extends ExtendedSpigotModule {


    public ColorModule(SpigotModuleManager<@NotNull DailyLife> moduleManager, DataModule dataModule) {
        super(moduleManager);
    }

    public Collection<ColorModel> colorModels = new ArrayList<>();

    @Getter
    @Setter
    private ColorsConfiguration configuration;

    @Override
    public void onEnable() {
        configuration = new ColorsConfiguration(DailyLife.getInstance().getDataFolder());
        configuration.saveConfiguration();

        registerComponent(new ColorCommand());
        registerComponent(new ColorAddCommand());
        registerComponent(new ColorRemoveCommand());
        registerComponent(new ColorCreateCommand());

        DailyLife.getCommandManager().getCommandCompletions().registerCompletion("colorTypes", context ->
                Arrays.stream(OwnableColorType.values()).map(OwnableColorType::name).toList());

        DailyLife.getCommandManager().getCommandCompletions().registerCompletion("colorIds", context ->
                configuration.components().stream()
                        .map(ColorComponent::identifier)
                        .toList());

        DailyLife.getCommandManager().getCommandCompletions().registerCompletion("playerColors", context -> {
            MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(context.getPlayer());
            if (minetopiaPlayer == null) return new ArrayList<>();

            return minetopiaPlayer.getColors().stream()
                    .map(OwnableColor::getColorId)
                    .toList();
        });

        DailyLife.getCommandManager().getCommandCompletions().registerCompletion("hometowns", c -> {
            try {
                return StormDatabase.getInstance()
                        .getStorm()
                        .buildQuery(HometownModel.class)
                        .execute()
                        .get()
                        .stream()
                        .map(HometownModel::getName)
                        .toList();
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        });
    }



    public List<OwnableColor> getColorsFromPlayer(PlayerModel playerModel) {
        return playerModel.getColors().stream().map(colorModel -> switch (colorModel.getType()) {
            case OwnableColorType.PREFIX ->
                    new PrefixColor(colorModel.getId(), colorModel.getColorId(), colorModel.getExpiresAt());
            case OwnableColorType.CHAT ->
                    new ChatColor(colorModel.getId(), colorModel.getColorId(), colorModel.getExpiresAt());
            case OwnableColorType.LEVEL ->
                    new LevelColor(colorModel.getId(), colorModel.getColorId(), colorModel.getExpiresAt());
        }).collect(Collectors.toList());
    }

    public Optional<OwnableColor> getActiveColorFromPlayer(PlayerModel playerModel, OwnableColorType type) {
        int activeId = switch (type) {
            case PREFIX -> playerModel.getActivePrefixColorId();
            case CHAT -> playerModel.getActiveChatColorId();
            case LEVEL -> playerModel.getActiveLevelColorId();
        };

        return getColorsFromPlayer(playerModel).stream()
                .filter(color -> color.getType() == type && color.getId() == activeId)
                .findFirst()
                .or(() -> Optional.of(type.defaultColor()));
    }

    public CompletableFuture<Integer> addColor(MinetopiaPlayer player, OwnableColor color) {
        ColorModel colorModel = new ColorModel();
        colorModel.setPlayerId(player.getPlayerModel().getId());
        colorModel.setColorId(color.getColorId());
        colorModel.setExpiresAt(color.getExpiresAt());
        colorModel.setType(color.getType());

        return StormDatabase.getInstance().saveStormModel(colorModel);
    }

    public CompletableFuture<Void> removeColor(OwnableColor color) {
        return StormUtils.deleteModelData(ColorModel.class,
                query -> query.where("id", Where.EQUAL, color.getId()));
    }

    public Optional<Hometown> getHometownFromPlayer(PlayerModel playerModel) {
        return Optional.ofNullable(playerModel.getHometown());
    }

}
