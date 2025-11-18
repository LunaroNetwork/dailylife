package nl.openminetopia.modules.currencies.listeners;

import lombok.RequiredArgsConstructor;
import nl.openminetopia.modules.currencies.CurrencyModule;
import nl.openminetopia.modules.currencies.models.CurrencyModel;
import nl.openminetopia.modules.currencies.objects.RegisteredCurrency;
import nl.openminetopia.modules.data.storm.StormDatabase;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.api.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CurrencyJoinListener implements Listener {

    private final CurrencyModule currencyModule;
    private final PlayerManager playerManager;

    @EventHandler
    public void onCurrencyJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MinetopiaPlayer minetopiaPlayer = playerManager.getOnlinePlayers().get(player.getUniqueId());
        if (minetopiaPlayer == null) return;

        List<CurrencyModel> currencyModels = new ArrayList<>();
        currencyModule.getCurrencies(player.getUniqueId()).whenComplete((models, throwable) -> {
            if (throwable != null) {
                currencyModule.getLogger().error("Couldn't load player currencies: {}", throwable.getMessage());
                return;
            }

            currencyModels.addAll(models);
            currencyModule.getLogger().info("Loaded {} currencies for {}", currencyModels.size(), player.getName());

            for (RegisteredCurrency registeredCurrency : currencyModule.getCurrencies()) {
                CurrencyModel currencyModel = currencyModels.stream()
                        .filter(model -> model.getName().equalsIgnoreCase(registeredCurrency.getId()))
                        .findAny()
                        .orElse(null);

                if (currencyModel == null) {
                    // Stel aanmaakmoment in op huidige playtime
                    long currentPlaytime = minetopiaPlayer.getPlaytime();

                    CurrencyModel newCurrencyModel = new CurrencyModel(
                            player.getUniqueId(),
                            registeredCurrency.getId(),
                            0d,
                            0L,
                            currentPlaytime // nieuwe parameter creationPlaytime
                    );

                    currencyModels.add(newCurrencyModel);
                    StormDatabase.getInstance().saveStormModel(newCurrencyModel);
                }
            }

            currencyModule.getCurrencyModels().put(player.getUniqueId(), currencyModels);
        });
    }
}
