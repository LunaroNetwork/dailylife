package nl.openminetopia.modules.books.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.books.BooksModule;
import nl.openminetopia.modules.books.objects.CustomBook;
import nl.openminetopia.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandAlias("boek|book|boeken|books")
public class BooksCommand extends BaseCommand {

    @Subcommand("krijg|get")
    @CommandCompletion("@books")
    public void getBook(Player player, String book) {
        BooksModule booksModule = DailyLife.getModuleManager().get(BooksModule.class);

        if (!player.hasPermission("openminetopia.books." + book)) {
            player.sendMessage(ChatUtils.color("<red>Je hebt geen toestemming om dit boek te krijgen."));
            return;
        }

        booksModule.getConfiguration().getCustomBooks().stream()
                .filter(customBook -> customBook.getIdentifier().equalsIgnoreCase(book))
                .findFirst()
                .ifPresentOrElse(customBook -> {
                    askForVariables(player, customBook, 0);
                }, () -> player.sendMessage(ChatUtils.color("<red>Dit boek bestaat niet.")));
    }

    private void askForVariables(Player player, CustomBook book, int index) {
        String[] keys = book.getVariables().keySet().toArray(new String[0]);
        BooksModule booksModule = DailyLife.getModuleManager().get(BooksModule.class);
        MinetopiaPlayer minetopiaPlayer = PlayerManager.getInstance().getOnlineMinetopiaPlayer(player);

        if (index >= keys.length) {
            // Alle variabelen zijn ingevuld, geef het boek
            Bukkit.getScheduler().runTask(DailyLife.getInstance(), () -> {
                if (book.isCopy()) {
                    player.getInventory().addItem(book.getBookItem(booksModule.getVariableResponses().get(player.getUniqueId()), player, true));
                }
                player.getInventory().addItem(book.getBookItem(booksModule.getVariableResponses().get(player.getUniqueId()), player, false));
            });

            player.sendMessage(ChatUtils.format(minetopiaPlayer, "<gold>Je hebt het boek <yellow>" + book.getName() + " <gold>ontvangen!"));
            return;
        }

        String key = keys[index];
        String explanation = book.getVariables().get(key);

        player.sendMessage(ChatUtils.format(minetopiaPlayer, explanation));

        DailyLife.getChatInputHandler().waitForInput(player, response -> {
            Map<String, String> responded = new HashMap<>();
            if (booksModule.getVariableResponses().containsKey(player.getUniqueId())) {
                responded = booksModule.getVariableResponses().get(player.getUniqueId());
            }
            responded.put(key, response); // Sla het antwoord op in de map
            booksModule.getVariableResponses().put(player.getUniqueId(), responded); // Sla het antwoord op in de map
            askForVariables(player, book, index + 1); // Ga naar de volgende variabele
        });
    }
}