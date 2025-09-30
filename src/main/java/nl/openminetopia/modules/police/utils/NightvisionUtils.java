package nl.openminetopia.modules.police.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.DailyLife;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.utils.item.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

@UtilityClass
public class NightvisionUtils {

    public void applyNightvisonEffects(Player target) {
        DefaultConfiguration configuration = DailyLife.getDefaultConfiguration();

        ItemUtils.applyEffects(target, configuration.getNightvisionEffects(), PotionEffect.INFINITE_DURATION);
    }

    public void clearNightvisionEffects(Player target) {
        DefaultConfiguration configuration = DailyLife.getDefaultConfiguration();

        ItemUtils.clearEffects(target, configuration.getNightvisionEffects());
    }

    public boolean isNightvisonItem(ItemStack head) {
        return ItemUtils.isSimilarToAny(head, DailyLife.getDefaultConfiguration().getNightvisionItems());
    }
}
