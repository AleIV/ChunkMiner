package net.noobsters.core.paper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.inventory.meta.Damageable;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

public class ChunkMiner implements Listener {

    private @NonNull Core instance;

    public ChunkMiner(Core instance) {
        this.instance = instance;

        SmithingRecipe superPickaxe = new SmithingRecipe(new NamespacedKey(instance, "superPickaxe"),
                new ItemStack(Material.AIR), new RecipeChoice.MaterialChoice(Material.DIAMOND_PICKAXE),
                new RecipeChoice.MaterialChoice(Material.ENDER_EYE));
        Bukkit.addRecipe(superPickaxe);
    }

    @EventHandler
    public void onSmith(PrepareSmithingEvent e){
        var inv = e.getInventory();
        var one = inv.getItem(0);
        var two = inv.getItem(1);
        if(one != null && two != null && one.getType() == Material.DIAMOND_PICKAXE && two.getType() == Material.ENDER_EYE){
            var result = new ItemStack(Material.NETHERITE_PICKAXE);
            var meta = result.getItemMeta();
            meta.setCustomModelData(102);
            meta.setDisplayName(ChatColor.GOLD + "Super Pickaxe");
            result.setItemMeta(meta);
            e.setResult(result);

        }
        
    }

    @EventHandler
    public void onMine(BlockBreakEvent e){
        
        if(e.getPlayer().getInventory().getItemInMainHand() == null) return;

        var player = e.getPlayer();
        final var item = player.getInventory().getItemInMainHand();

        if(item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == 102){

            var meta = item.getItemMeta();
            var damageable = (Damageable) meta;
            
            var damage = 0;

            if(damageable.getDamage() == 2031){
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
            }else if(!damageable.hasDamage()){
                damage = 31;
            }else{
                damage = damageable.getDamage()+31;
                if(damage > 2031){
                    damage = 2031;
                }
            }

            damageable.setDamage(damage);
            item.setItemMeta(meta);

            var chunk = e.getBlock().getChunk();
            var blockY = e.getBlock().getY();
            var block = chunk.getBlock(0, blockY, 0);
            
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        var relative = block.getRelative(x, y, z);
                        var type = relative.getType();
                        if(type.toString().contains("WATER") || type.toString().contains("LAVA") || type.toString().contains("AIR")){
                            //Bukkit.broadcastMessage(ChatColor.RED + type.toString());
                        }else{
                            relative.breakNaturally(item, true);
                            //Bukkit.broadcastMessage(ChatColor.GREEN + type.toString());
                        }
                        
    
                    }
                }
            }
            

        }

    }

}