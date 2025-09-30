package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void dropItem(final PlayerDropItemEvent event) {
        if (!HandcuffManager.getInstance().isHandcuffed(event.getPlayer())) return;

        if (!DailyLife.getDefaultConfiguration().isHandcuffCanDropItems()) event.setCancelled(true);
    }
}
