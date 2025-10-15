package nl.openminetopia.modules.staff.mod.commands.subcommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import nl.openminetopia.api.player.PlayerManager;
import nl.openminetopia.modules.color.models.HometownModel;
import nl.openminetopia.modules.color.objects.Hometown;
import nl.openminetopia.modules.data.storm.StormDatabase;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@CommandAlias("mod")
public class ModHometownCommand extends BaseCommand {

    @Subcommand("hometown create")
    @Syntax("<name> <color>")
    @CommandPermission("ln.dl.mod.hometown.create")
    @Description("Maak een nieuwe hometown aan.")
    public void create(Player player, String name, String color) throws Exception {
        var storm = StormDatabase.getInstance().getStorm();

        storm.buildQuery(HometownModel.class)
                .execute()
                .thenAccept(all -> {
                    boolean exists = all.stream()
                            .anyMatch(h -> h.getName().equalsIgnoreCase(name));

                    if (exists) {
                        player.sendMessage("§cEen hometown met deze naam bestaat al!");
                        return;
                    }

                    HometownModel model = new HometownModel();
                    model.setName(name);
                    model.setColorId(color);

                    try {
                        storm.save(model);
                        player.sendMessage("§aHometown §f" + name + " §amet kleur §f" + color + " §ais aangemaakt.");
                    } catch (SQLException e) {
                        player.sendMessage("§cEr is een fout opgetreden bij het opslaan van de hometown.");
                        e.printStackTrace();
                    }
                });
    }

    @Subcommand("hometown set")
    @Syntax("<player> [hometown]")
    @CommandPermission("ln.dl.mod.hometown.set")
    @CommandCompletion("@players @hometowns")
    @Description("Stel de hometown van een speler in of reset deze.")
    public void set(Player executor, OfflinePlayer target, @Optional String hometownName) {
        var storm = StormDatabase.getInstance().getStorm();

        PlayerManager.getInstance().getMinetopiaPlayer(target).whenComplete((minetopiaPlayer, throwable) -> {
            if (minetopiaPlayer == null) {
                executor.sendMessage("§cKon speler niet vinden of laden!");
                return;
            }

            // Reset
            if (hometownName == null || hometownName.trim().isEmpty()) {
                minetopiaPlayer.setHometown(null);
                executor.sendMessage("§aHometown van §f" + target.getName() + " §ais gereset.");
                return;
            }

            try {
                // Haal alle HometownModels op en filter lokaal
                storm.buildQuery(HometownModel.class)
                        .execute()
                        .thenAccept(all -> {
                            HometownModel model = all.stream()
                                    .filter(h -> h.getName().equalsIgnoreCase(hometownName))
                                    .findFirst()
                                    .orElse(null);

                            if (model == null) {
                                executor.sendMessage("§cGeen hometown gevonden met de naam §f" + hometownName + "§c!");
                                return;
                            }

                            // Converteer HometownModel naar Hometown object
                            Hometown hometown = new Hometown(model.getName(), model.getColorId()) {};
                            minetopiaPlayer.setHometown(hometown);

                            executor.sendMessage("§aHometown van §f" + target.getName() + " §ais nu ingesteld op §f" + hometown.getName() + "§a.");
                        });
            } catch (Exception e) {
                e.printStackTrace();
                executor.sendMessage("§cEr is een fout opgetreden bij het instellen van de hometown.");
            }
        });
    }


    /* ===============================
       /mod hometown delete <name>
       =============================== */
    @Subcommand("hometown delete")
    @Syntax("<hometown>")
    @CommandPermission("ln.dl.mod.hometown.delete")
    @CommandCompletion("@hometowns")
    @Description("Verwijder een bestaande hometown.")
    public void delete(Player player, String name) throws Exception {
        var storm = StormDatabase.getInstance().getStorm();

        storm.buildQuery(HometownModel.class)
                .execute()
                .thenAccept(all -> {
                    HometownModel model = all.stream()
                            .filter(h -> h.getName().equalsIgnoreCase(name))
                            .findFirst()
                            .orElse(null);

                    if (model == null) {
                        player.sendMessage("§cGeen hometown gevonden met de naam §f" + name + "§c!");
                        return;
                    }

                    try {
                        storm.delete(model);
                        player.sendMessage("§aHometown §f" + name + " §ais verwijderd.");
                    } catch (SQLException e) {
                        player.sendMessage("§cEr is een fout opgetreden bij het verwijderen van de hometown.");
                        e.printStackTrace();
                    }
                });
    }
}
