package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class PlayerSlotChangeListener implements Listener {

    @EventHandler
    public void playerItemHeld(final PlayerItemHeldEvent event) {
        if (!HandcuffManager.getInstance().isHandcuffed(event.getPlayer())) return;

        if (!DailyLife.getDefaultConfiguration().isHandcuffCanChangeSlots()) event.setCancelled(true);
    }
}
