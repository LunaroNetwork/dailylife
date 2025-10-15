package nl.openminetopia.modules.teleporter.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.openminetopia.DailyLife;
import nl.openminetopia.configuration.DefaultConfiguration;
import nl.openminetopia.modules.places.PlacesModule;
import nl.openminetopia.modules.teleporter.utils.enums.PressurePlate;
import nl.openminetopia.utils.ChatUtils;
import nl.openminetopia.utils.PersistentDataUtil;
import nl.openminetopia.utils.item.ItemBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Transformation;

import java.util.UUID;

@UtilityClass
public final class TeleporterUtil {

    public ItemStack buildPlate(PressurePlate plate, Location location, boolean addDisplay) {
        ItemBuilder builder = new ItemBuilder(plate.getMaterial());
        builder.setName("<gold>Teleporter Plate");

        builder.addLoreLine("<dark_gray>X: <gray>" + location.x());
        builder.addLoreLine("<dark_gray>Y: <gray>" + location.y());
        builder.addLoreLine("<dark_gray>Z: <gray>" + location.z());
        builder.addLoreLine("<dark_gray>World: <gray>" + location.getWorld().getName());

        /* Should just use MorePersistentDataTypes but okay */
        builder.setNBT("teleporter.x", location.x());
        builder.setNBT("teleporter.y", location.y());
        builder.setNBT("teleporter.z", location.z());
        builder.setNBT("teleporter.yaw", location.getYaw());
        builder.setNBT("teleporter.pitch", location.getPitch());
        builder.setNBT("teleporter.world", location.getWorld().getName());

        if (addDisplay) builder.setNBT("teleporter.display", true);

        return builder.toItemStack();
    }

    public Location decodeNBT(ItemStack item) {
        Double x = PersistentDataUtil.getDouble(item, "teleporter.x");
        Double y = PersistentDataUtil.getDouble(item, "teleporter.y");
        Double z = PersistentDataUtil.getDouble(item, "teleporter.z");
        Float yaw = (Float) PersistentDataUtil.get(item, "teleporter.yaw");
        Float pitch = (Float) PersistentDataUtil.get(item, "teleporter.pitch");
        String worldName = PersistentDataUtil.getString(item, "teleporter.world");

        if (x == null || y == null || z == null || yaw == null || pitch == null || worldName == null) return null;
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    /* Assuming a teleporter check has been done */
    public Location blockLocation(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, DailyLife.getInstance());
        return data.get(new NamespacedKey(DailyLife.getInstance(), "teleporter.location"), DataType.LOCATION);
    }

    public void setTeleporter(Block block, Location location, boolean addDisplay) {
        PersistentDataContainer data = new CustomBlockData(block, DailyLife.getInstance());
        data.set(new NamespacedKey(DailyLife.getInstance(), "teleporter.location"), DataType.LOCATION, location);

        if (!addDisplay) return;
        /*
        * Entity display = addDisplay(block, location);
        * data.set(new NamespacedKey(DailyLife.getInstance(), "teleporter.entity"), DataType.UUID, display.getUniqueId());
        */
        addSign(block, location);
    }

    public Entity addDisplay(Block block, Location location) {
        Location blockLocation = block.getLocation();
        blockLocation.add(0.5, 1.4, 0.5);

        /* Add config support */
        TextDisplay display = block.getWorld().spawn(blockLocation, TextDisplay.class);
        display.text(displayText(location));
        display.setBillboard(Display.Billboard.CENTER);
        display.setAlignment(TextDisplay.TextAlignment.CENTER);
        display.setShadowed(true);
        display.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));

        Transformation transformation = display.getTransformation();
        transformation.getScale().set(0.5);
        display.setTransformation(transformation);

        return display;
    }

    private void addSign(Block block, Location location) {
        Block above = block.getRelative(BlockFace.UP);
        if (!above.getType().isAir()) return; // alleen als er lucht boven zit

        // Zoek een muur om sign tegenaan te hangen
        for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            Block support = above.getRelative(face.getOppositeFace());
            if (support.getType().isSolid()) {
                above.setType(Material.OAK_WALL_SIGN);
                BlockData data = above.getBlockData();
                if (data instanceof org.bukkit.block.data.type.WallSign wallSign) {
                    wallSign.setFacing(face);
                    above.setBlockData(wallSign, false);
                }
                org.bukkit.block.Sign sign = (org.bukkit.block.Sign) above.getState();
                sign.getSide(Side.FRONT).line(0, MiniMessage.miniMessage().deserialize("<gray>[<dark_green>T<green>eleporter<gray>]"));
                sign.getSide(Side.FRONT).line(1, MiniMessage.miniMessage().deserialize(location.getWorld().getName()));
                sign.getSide(Side.FRONT).line(2, MiniMessage.miniMessage().deserialize("<gray>" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));
                sign.update(true, false);
                break;
            }
        }
    }

    private Component displayText(Location location) {
        DefaultConfiguration configuration = DailyLife.getDefaultConfiguration();
        String text = StringUtils.join(configuration.getTeleporterDisplayLines(), "\n<reset>");

        text = text
                .replace("<x>", String.valueOf((int) location.x()))
                .replace("<y>", String.valueOf((int) location.y()))
                .replace("<z>", String.valueOf((int) location.z()))
                .replace("<world>", location.getWorld().getName());
        return MiniMessage.miniMessage().deserialize(text);
    }

    public void removeTeleporter(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, DailyLife.getInstance());
        UUID uuid = data.get(new NamespacedKey(DailyLife.getInstance(), "teleporter.entity"), DataType.UUID);
        if (uuid == null) return;

        Entity entity = Bukkit.getEntity(uuid);
        if (entity == null) return;

        entity.remove();
    }

    public boolean isTeleporterItem(ItemStack item) {
        if (item == null || item.getType().equals(Material.AIR)) return false;
        return PersistentDataUtil.getDouble(item, "teleporter.x") != null;
    }

    public boolean isTeleporterBlock(Block block) {
        PersistentDataContainer data = new CustomBlockData(block, DailyLife.getInstance());
        return data.has(new NamespacedKey(DailyLife.getInstance(), "teleporter.location"));
    }

}
