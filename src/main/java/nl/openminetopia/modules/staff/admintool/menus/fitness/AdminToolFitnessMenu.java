package nl.openminetopia.modules.staff.admintool.menus.fitness;

import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import nl.openminetopia.DailyLife;
import nl.openminetopia.api.player.fitness.FitnessStatisticType;
import nl.openminetopia.api.player.objects.MinetopiaPlayer;
import nl.openminetopia.modules.banking.models.BankAccountModel;
import nl.openminetopia.modules.fitness.FitnessModule;
import nl.openminetopia.modules.fitness.configuration.FitnessConfiguration;
import nl.openminetopia.modules.fitness.models.FitnessStatisticModel;
import nl.openminetopia.modules.staff.admintool.menus.AdminToolInfoMenu;
import nl.openminetopia.utils.item.ItemBuilder;
import nl.openminetopia.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

@Getter
public class AdminToolFitnessMenu extends Menu {

    private final Player player;
    private final OfflinePlayer offlinePlayer;
    private final MinetopiaPlayer minetopiaPlayer;
    private final BankAccountModel bankAccountModel;

    public AdminToolFitnessMenu(Player player, OfflinePlayer offlinePlayer, MinetopiaPlayer minetopiaPlayer, BankAccountModel bankAccountModel) {
        super("<gold>Fitheid <yellow>" + offlinePlayer.getPlayerProfile().getName(), 3);
        this.player = player;
        this.offlinePlayer = offlinePlayer;
        this.minetopiaPlayer = minetopiaPlayer;
        this.bankAccountModel = bankAccountModel;

        gui.disableAllInteractions();
        if (minetopiaPlayer == null) return;

        FitnessConfiguration configuration = DailyLife.getModuleManager().get(FitnessModule.class).getConfiguration();

        FitnessStatisticModel drinkingStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.DRINKING);
        ItemBuilder drinkingItemBuilder = new ItemBuilder(Material.POTION)
                .setName("<gold>Drinken " + drinkingStatistic.getFitnessGained() + "/" + drinkingStatistic.getMaximum())
                .addLoreLine(" ")
                .addLoreLine("<gold>Precieze score: <yellow>" + drinkingStatistic.getPoints())
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>" + configuration.getDrinkingPointsPerWaterBottle() + " <dark_purple>punt voor het drinken van water.")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>" + configuration.getDrinkingPointsPerPotion() + " <dark_purple>punt voor het drinken van potions.")
                .addLoreLine(" ");

        GuiItem targetDrinkingItem = new GuiItem(drinkingItemBuilder.toItemStack());
        gui.setItem(9, targetDrinkingItem);

        FitnessStatisticModel healthStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.HEALTH);
        ItemBuilder healthItemBuilder = new ItemBuilder(Material.APPLE)
                .setName("<gold>Fatsoenlijk eten " + healthStatistic.getFitnessGained() + "/" + healthStatistic.getMaximum())
                .addLoreLine(" ")
                .addLoreLine("<gold>Precieze score: <yellow>" + healthStatistic.getPoints())
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>" + configuration.getPointsAbove9Hearts() + " <dark_purple>punt als tijdens de")
                .addLoreLine("check hun voedselniveau hoger is dan <light_purple>9")
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers verliezen <light_purple>" + configuration.getPointsBelow5Hearts() + " <dark_purple>punt als tijdens de")
                .addLoreLine("check hun voedselniveau lager is dan <light_purple>5")
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers verliezen <light_purple>" + configuration.getPointsBelow2Hearts() + " <dark_purple>punt als tijdens de")
                .addLoreLine("check hun voedselniveau lager is dan <light_purple>2")
                .addLoreLine(" ");

        GuiItem targetHealthItem = new GuiItem(healthItemBuilder.toItemStack());
        gui.setItem(10, targetHealthItem);

        FitnessStatisticModel eatingStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.EATING);

        ItemBuilder foodItemBuilder = new ItemBuilder(Material.GOLDEN_APPLE)
                .setName("<gold>Eten " + eatingStatistic.getFitnessGained() + "/" + eatingStatistic.getMaximum())
                .addLoreLine(" ")
                .addLoreLine("<gold>Luxe eten genuttigd: <yellow>" + eatingStatistic.getTertiaryPoints().intValue())
                .addLoreLine("<gold>Goedkoop eten genuttigd: <yellow>" + eatingStatistic.getSecondaryPoints().intValue())
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Luxe eten:")
                .addLoreLine("<light_purple>" + configuration.getLuxuryFood()
                        .stream().map(fitnessFood -> fitnessFood.getMaterial().name())
                        .toString().replace("[", "").replace("]", ""))
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Goedkoop eten:")
                .addLoreLine("<light_purple>" + configuration.getCheapFood()
                        .stream().map(fitnessFood -> fitnessFood.getMaterial().name())
                        .toString().replace("[", "").replace("]", ""))
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>" + configuration.getPointsAbove9Hearts() + " <dark_purple>voor het eten van luxe voedsel.")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>" + configuration.getPointsBelow5Hearts() + " <dark_purple>voor het eten van goedkoop voedsel.")
                .addLoreLine(" ");

        GuiItem targetFoodItem = new GuiItem(foodItemBuilder.toItemStack());
        gui.setItem(11, targetFoodItem);

        FitnessStatisticModel climbingStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.CLIMBING);
        ItemBuilder climbingItemBuilder = new ItemBuilder(Material.LADDER)
                .setName("<gold>Klimmen " + climbingStatistic.getFitnessGained() + "/" + climbingStatistic.getMaximum())
                .addLoreLine(" ")
                .addLoreLine("<gold>Kilometers geklommen: <yellow>"
                        + (minetopiaPlayer.getBukkit().getStatistic(Statistic.CLIMB_ONE_CM) / 1000))
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>1 <dark_purple>punt per <light_purple>"
                        + (configuration.getCmPerClimbingLevel() / 1000) + " <dark_purple>kilometer klimmen.")
                .addLoreLine(" ");

        GuiItem targetClimbingItem = new GuiItem(climbingItemBuilder.toItemStack());
        gui.setItem(12, targetClimbingItem);

        FitnessStatisticModel flyingStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.FLYING);
        ItemBuilder flyingItemBuilder = new ItemBuilder(Material.ELYTRA)
                .setName("<gold>Vliegen " + flyingStatistic.getFitnessGained() + "/" + flyingStatistic.getMaximum())
                .addLoreLine(" ")
                .addLoreLine("<gold>Kilometers gevlogen: <yellow>" + (minetopiaPlayer.getBukkit().getStatistic(Statistic.AVIATE_ONE_CM) / 1000))
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>1 <dark_purple>punt per <light_purple>"
                        + (configuration.getCmPerFlyingLevel() / 1000) + " <dark_purple>kilometer vliegen.")
                .addLoreLine(" ");

        GuiItem targetFlyingItem = new GuiItem(flyingItemBuilder.toItemStack());
        gui.setItem(13, targetFlyingItem);

        FitnessStatisticModel walkingStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.WALKING);
        ItemBuilder walkingItemBuilder = new ItemBuilder(Material.LEATHER_BOOTS)
                .setName("<gold>Lopen " + walkingStatistic.getFitnessGained() + "/" + walkingStatistic.getMaximum())
                .addLoreLine(" ")
                .addLoreLine("<gold>Kilometers gelopen: <yellow>" + (minetopiaPlayer.getBukkit().getStatistic(Statistic.WALK_ONE_CM) / 1000))
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>1 <dark_purple>punt per <light_purple>"
                        + (configuration.getCmPerWalkingLevel() / 1000) + " <dark_purple>kilometer lopen.")
                .addLoreLine(" ");

        GuiItem targetWalkingItem = new GuiItem(walkingItemBuilder.toItemStack());
        gui.setItem(14, targetWalkingItem);

        FitnessStatisticModel swimmingStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.SWIMMING);
        ItemBuilder swimmingItemBuilder = new ItemBuilder(Material.OAK_BOAT)
                .setName("<gold>Zwemmen " + swimmingStatistic.getFitnessGained() + "/" + swimmingStatistic.getMaximum())
                .addLoreLine(" ")
                .addLoreLine("<gold>Kilometers gezwommen: <yellow>" + (minetopiaPlayer.getBukkit().getStatistic(Statistic.SWIM_ONE_CM) / 1000))
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>1 <dark_purple>punt per <light_purple>"
                        + (configuration.getCmPerSwimmingLevel() / 1000) + " <dark_purple>kilometer zwemmen.")
                .addLoreLine(" ");

        GuiItem targetSwimmingItem = new GuiItem(swimmingItemBuilder.toItemStack());
        gui.setItem(15, targetSwimmingItem);

        FitnessStatisticModel sprintingStatistic = minetopiaPlayer.getFitness().getStatistic(FitnessStatisticType.SPRINTING);
        ItemBuilder sprintingItemBuilder = new ItemBuilder(Material.DIAMOND_BOOTS)
                .setName("<gold>Rennen " + sprintingStatistic.getFitnessGained() + "/" + sprintingStatistic.getMaximum())
                .addLoreLine(" ")
                .addLoreLine("<gold>Kilometers gerend: <yellow>" + (minetopiaPlayer.getBukkit().getStatistic(Statistic.SPRINT_ONE_CM) / 1000))
                .addLoreLine(" ")
                .addLoreLine("<dark_purple>Spelers krijgen <light_purple>1 <dark_purple>punt per <light_purple>"
                        + (configuration.getCmPerSprintingLevel() / 1000) + " <dark_purple>kilometer rennen.")
                .addLoreLine(" ");

        GuiItem targetSprintingItem = new GuiItem(sprintingItemBuilder.toItemStack());
        gui.setItem(16, targetSprintingItem);

        ItemBuilder totalItemBuilder = new ItemBuilder(Material.PAPER)
                .setName("<gold>Totaal: <yellow>" + minetopiaPlayer.getFitness().getTotalFitness()
                        + "<gold>/<yellow>" + configuration.getMaxFitnessLevel())
                .addLoreLine(" ")
                .addLoreLine("<gold>Klik om de <yellow>fitness boosters <gold>te bekijken.");

        GuiItem targetTotalItem = new GuiItem(totalItemBuilder.toItemStack(), event -> {
            event.setCancelled(true);
            new AdminToolFitnessBoostersMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open((Player) event.getWhoClicked());
        });
        gui.setItem(17, targetTotalItem);

        ItemBuilder backItemBuilder = new ItemBuilder(Material.OAK_DOOR)
                .setName("<gray>Terug");

        GuiItem backItem = new GuiItem(backItemBuilder.toItemStack(), event -> {
            new AdminToolInfoMenu(player, offlinePlayer, minetopiaPlayer, bankAccountModel).open((Player) event.getWhoClicked());
        });
        gui.setItem(22, backItem);
    }
}