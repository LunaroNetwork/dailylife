package nl.openminetopia.modules.misc.utils;

import lombok.experimental.UtilityClass;
import nl.openminetopia.DailyLife;
import nl.openminetopia.modules.misc.objects.PvPItem;
import nl.openminetopia.utils.item.ItemUtils;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class MiscUtils {

    public boolean isValidHeadItem(ItemStack head) {
        return ItemUtils.isSimilarToAny(head, DailyLife.getDefaultConfiguration().getHeadWhitelist());
    }

    public PvPItem getPvPItem(ItemStack item) {
        for (PvPItem pvpItem : DailyLife.getDefaultConfiguration().getPvpItems()) {
            if (pvpItem.isSimilar(item)) return pvpItem;
        }
        return null;
    }
}
