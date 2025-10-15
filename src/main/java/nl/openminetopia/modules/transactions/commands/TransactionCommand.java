package nl.openminetopia.modules.transactions.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.DailyLife;
import nl.openminetopia.configuration.language.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.modules.banking.menus.BankTransactionsMenu;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.transactions.TransactionsModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.entity.Player;

@CommandAlias("transactions|transacties")
@CommandPermission("openminetopia.transactions")
public class TransactionCommand extends BaseCommand {

    private final TransactionsModule transactionsModule = DailyLife.getModuleManager().get(TransactionsModule.class);
    private final BankingModule bankingModule = DailyLife.getModuleManager().get(BankingModule.class);

    @Default
    @Syntax("<name>")
    @CommandCompletion("@accountNames")
    public void checkTransactionHistory(Player player, String accountName) {
        BankAccountModel accountModel = bankingModule.getAccountByName(accountName);

        if (accountModel == null) {
            ChatUtils.sendMessage(player, DailyLife.getMessageConfiguration().message("banking_account_not_found", player));
            return;
        }

        new BankTransactionsMenu(player, accountModel).open(player);
    }

}
