package nl.openminetopia.modules.player.listeners;

import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        BankingModule bankingModule = DailyLife.getModuleManager().get(BankingModule.class);
        BankAccountModel accountModel = bankingModule.getAccountById(player.getUniqueId());

        if(accountModel != null) {
            bankingModule.getBankAccountModels().remove(accountModel);
            accountModel.save();
            accountModel.getSavingTask().cancel();
        }

        PlayerManager.getInstance().getMinetopiaPlayer(player).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) return;
            minetopiaPlayer.updatePlaytime();
            minetopiaPlayer.save().whenComplete((unused, throwable1) -> {
                if (throwable1 != null) {
                    DailyLife.getInstance().getLogger().severe("Couldn't save Player (" + player.getName() + "): " + throwable1.getMessage());
                    return;
                }
                DailyLife.getInstance().getLogger().info("Saved player data for " + player.getName());
            });

            PlayerManager.getInstance().getOnlinePlayers().remove(player.getUniqueId());
        });
    }
}