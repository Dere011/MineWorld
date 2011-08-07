package mineworld;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class Main_BlockListener extends BlockListener {
	
	Main plugin;
    private final Random rand = new Random();

    public Main_BlockListener(Main parent) {
    	plugin = parent;
    }
    
	private static int showRandomInteger(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    long range = (long)aEnd - (long)aStart + 1;
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);
	    return randomNumber;
	}
    
    public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.getCause() != null && event.getCause() == IgniteCause.LIGHTNING) {
			event.setCancelled(true);
		}
    }
    
    public void onBlockPlace(BlockPlaceEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer())) {
    		plugin.Main_Visiteur.denied_message(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	if(event.getBlock().getTypeId() == 48 || event.getBlock().getTypeId() == 84 || event.getBlock().getTypeId() == 25 || event.getBlock().getTypeId() == 87 || event.getBlock().getTypeId() == 88 || event.getBlock().getTypeId() == 89) {
    		if(!plugin.Main_ContribControl.isClient(event.getPlayer(), true)) {
        		event.setCancelled(true);
        		return;
    		}
    	}
    	String worldname = event.getPlayer().getWorld().getName();
    	if(!event.getPlayer().isOp() && (plugin.is_spy(event.getPlayer()) || worldname.contains("old"))) {
    		event.setCancelled(true);
    		return;
    	}
    }
    
    public void onBlockDamage(BlockDamageEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer())) {
    		plugin.Main_Visiteur.denied_message(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	String worldname = event.getPlayer().getWorld().getName();
    	if(!event.getPlayer().isOp() && (plugin.is_spy(event.getPlayer()) || worldname.contains("old"))) {
    		event.setCancelled(true);
    		return;
    	}
    }
    
    public void onBlockBreak(BlockBreakEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer())) {
    		plugin.Main_Visiteur.denied_message(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	if(event.getBlock().getTypeId() == 48 || event.getBlock().getTypeId() == 84 || event.getBlock().getTypeId() == 25 || event.getBlock().getTypeId() == 87 || event.getBlock().getTypeId() == 88 || event.getBlock().getTypeId() == 89) {
    		if(!plugin.Main_ContribControl.isClient(event.getPlayer(), true)) {
        		event.setCancelled(true);
        		return;
    		}
    	}
    	String worldname = event.getPlayer().getWorld().getName();
    	if(!event.getPlayer().isOp() && (plugin.is_spy(event.getPlayer()) || worldname.contains("old"))) {
    		event.setCancelled(true);
    		return;
    	}
    	if(plugin.Main_TimeControl.dead_sun && (event.getBlock().getType() == Material.LONG_GRASS || event.getBlock().getType() == Material.DEAD_BUSH)) {
    		if(plugin.Main_ContribControl.isClient(event.getPlayer(), false)) {
	    		int random = showRandomInteger(1, 30, rand);
	    		if(random == 10 && (event.getBlock().getLightLevel() >= 13 && event.getPlayer().getLocation().getBlock().getLightLevel() >= 13)) {
	    			int randomint = showRandomInteger(1, 5, rand);
	    			int randomblock = showRandomInteger(1, 5, rand);
	    			ItemStack item;
	    			if(randomblock == 1) {
	    				item = new ItemStack(Material.CLAY);
	    			}else{
	    				item = new ItemStack(Material.DEAD_BUSH);
	    			}
	    			item.setAmount(randomint);
	    			event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
	    			plugin.Main_ContribControl.sendNotification(event.getPlayer(), "Notification", "Vous avez trouvé "+randomint+" herbes.");
	    			plugin.Main_ContribControl.sendPlayerSoundEffect(event.getPlayer(), "http://mineworld.fr/contrib/sound/beeperror.wav");
	    		}
    		}
    	}
    }
}