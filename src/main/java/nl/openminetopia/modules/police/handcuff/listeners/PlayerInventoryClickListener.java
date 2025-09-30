package nl.openminetopia.modules.police.handcuff.listeners;

import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.police.handcuff.HandcuffManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class PlayerInventoryClickListener implements Listener {

    @EventHandler
    public void playerInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (!HandcuffManager.getInstance().isHandcuffed(player)) return;

        if (!DailyLife.getDefaultConfiguration().isHandcuffCanDropItems()) event.setCancelled(true);

    }
}
