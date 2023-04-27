package me.randomgamingdev.mcvanilladynamiclighting;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Light;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedList;
import java.util.Vector;

public class LightCalc extends BukkitRunnable {
    final private MCVanillaDynamicLighting plugin;
    private LinkedList<Location> lightSources = new LinkedList<Location>();
    final private Object[] lightItems = new Object[] {
            new Pair<Material, Integer>(Material.BEACON, 14),
            new Pair<Material, Integer>(Material.CONDUIT, 14),
            new Pair<Material, Integer>(Material.GLOWSTONE, 14),
            new Pair<Material, Integer>(Material.JACK_O_LANTERN, 14),
            new Pair<Material, Integer>(Material.LANTERN, 14),
            new Pair<Material, Integer>(Material.LAVA_BUCKET, 14),
            new Pair<Material, Integer>(Material.CAMPFIRE, 14),
            new Pair<Material, Integer>(Material.SEA_LANTERN, 14),
            new Pair<Material, Integer>(Material.SHROOMLIGHT, 14),
            new Pair<Material, Integer>(Material.TORCH, 14),
            new Pair<Material, Integer>(Material.SOUL_CAMPFIRE, 10),
            new Pair<Material, Integer>(Material.SOUL_LANTERN, 10),
            new Pair<Material, Integer>(Material.SOUL_TORCH, 10),
            new Pair<Material, Integer>(Material.ENCHANTING_TABLE, 7),
            new Pair<Material, Integer>(Material.ENDER_CHEST, 7),
            new Pair<Material, Integer>(Material.GLOW_LICHEN, 7),
            new Pair<Material, Integer>(Material.REDSTONE_TORCH, 7),
            new Pair<Material, Integer>(Material.SEA_PICKLE, 6),
            //new Pair<Material, Integer>(Material.SCULK_CATALYST, 6),
            new Pair<Material, Integer>(Material.AMETHYST_CLUSTER, 5),
            new Pair<Material, Integer>(Material.LARGE_AMETHYST_BUD, 4),
            new Pair<Material, Integer>(Material.MAGMA_BLOCK, 3),
            new Pair<Material, Integer>(Material.MEDIUM_AMETHYST_BUD, 2),
            new Pair<Material, Integer>(Material.BREWING_STAND, 1),
            new Pair<Material, Integer>(Material.DRAGON_EGG, 1),
            new Pair<Material, Integer>(Material.END_PORTAL_FRAME, 1),
            new Pair<Material, Integer>(Material.SCULK_SENSOR, 1),
            new Pair<Material, Integer>(Material.SMALL_AMETHYST_BUD, 1),
    };

    LightCalc(MCVanillaDynamicLighting plugin) {
        this.plugin = plugin;
    }

    public void ReplaceLightSources() {
        for (Location lightSource : lightSources) {
            Block block = lightSource.getBlock();
            Material blockType = block.getType();
            if (blockType != Material.LIGHT)
                continue;
            if (((Waterlogged)block.getBlockData()).isWaterlogged())
                block.setType(Material.WATER);
            else
                block.setType(Material.AIR);
        }
        lightSources.clear();
    }

    public boolean PlaceLight(Location location, int level) {
        Block block = location.getBlock();
        Material blockType = block.getType();
        boolean isWater = blockType == Material.WATER;
        if (blockType != Material.AIR && (!isWater && ((Levelled)block).getLevel() == 0))
            return false;
        block.setType(Material.LIGHT);
        Light lightData = (Light)block.getBlockData();
        if (isWater)
            lightData.setWaterlogged(true);
        lightData.setLevel(level);
        block.setBlockData(lightData);
        lightSources.push(location);
        return true;
    }

    @Override
    public void run() {
        Server server = plugin.getServer();
        ReplaceLightSources();
        for (Player player : server.getOnlinePlayers()) {
            PlayerInventory inventory = player.getInventory();

            ItemStack handItem = inventory.getItemInMainHand();
            ItemStack offHandItem = inventory.getItemInOffHand();
            ItemStack headItem = inventory.getHelmet();
            Material handItemType = null;
            if (handItem != null)
                handItemType = handItem.getType();
            Material offHandItemType = null;
            if (offHandItem != null)
                offHandItemType = offHandItem.getType();
            Material headItemType = null;
            if (headItem != null)
                headItemType = headItem.getType();

            for (Object lightItemObj : lightItems) {
                Pair<Material, Integer> lightItem = (Pair<Material, Integer>)lightItemObj;
                if (handItemType != lightItem.first &&
                    offHandItemType != lightItem.first &&
                    headItemType != lightItem.first)
                        continue;
                Location location = player.getLocation();
                location.setY(location.getY() + 1);
                if (PlaceLight(location, lightItem.second))
                    break;
            }
        }
    }
}
