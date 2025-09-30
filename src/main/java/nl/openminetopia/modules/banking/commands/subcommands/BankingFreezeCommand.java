package nl.openminetopia.modules.banking.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.DailyLife;
import nl.openminetopia.configuration.MessageConfiguration;
import nl.openminetopia.modules.banking.BankingModule;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.command.CommandSender;

@CommandAlias("accounts|account|rekening")
public class BankingFreezeCommand extends BaseCommand {

    @Subcommand("freeze")
    @CommandCompletion("@accountNames")
    @CommandPermission("openminetopia.banking.freeze")
    public void freezeAccount(CommandSender sender, String accountName) {
        BankingModule bankingModule = DailyLife.getModuleManager().get(BankingModule.class);
        bankingModule.getAccountByNameAsync(accountName).thenAccept(accountModel -> {
            if (accountModel == null) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("banking_account_not_found"));
                return;
            }

            boolean newState = !accountModel.getFrozen();

            accountModel.setFrozen(newState);
            accountModel.save();

            if (newState) {
                ChatUtils.sendMessage(sender, MessageConfiguration.message("banking_account_frozen")
                        .replace("<account_name>", accountModel.getName()));
                return;
            }
            ChatUtils.sendMessage(sender, MessageConfiguration.message("banking_account_unfrozen")
                    .replace("<account_name>", accountModel.getName()));
        });
    }
}
