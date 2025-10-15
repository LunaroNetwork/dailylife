package nl.openminetopia.modules.banking.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.configuration.language.MessageConfiguration;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public enum AccountType {
    PRIVATE("<gold>", DailyLife.getMessageConfiguration().message("banking_private_account"), Material.GOLD_BLOCK,  14),
    COMPANY("<aqua>", DailyLife.getMessageConfiguration().message("banking_company_account"), Material.DIAMOND_BLOCK, 16),
    SAVINGS("<red>", DailyLife.getMessageConfiguration().message("banking_savings_account"), Material.REDSTONE_BLOCK, 12),
    GOVERNMENT("<dark_green>", DailyLife.getMessageConfiguration().message("banking_government_account"), Material.EMERALD_BLOCK, 10);

    private final String color;
    private final String name;
    private final Material material;
    private final int slot;

}