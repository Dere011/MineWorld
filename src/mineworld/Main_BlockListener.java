package mineworld;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class Main_BlockListener extends BlockListener {
	
	Main plugin;

    public Main_BlockListener(Main parent) {
    	plugin = parent;
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
    	String worldname = event.getPlayer().getWorld().getName();
    	if(!event.getPlayer().isOp() && (plugin.is_spy(event.getPlayer()) || worldname.contains("olddeathworld") || worldname.contains("oldworld") || worldname.contains("oldaerelon"))) {
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
    	if(!event.getPlayer().isOp() && (plugin.is_spy(event.getPlayer()) || worldname.contains("olddeathworld") || worldname.contains("oldworld") || worldname.contains("oldaerelon"))) {
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
    	String worldname = event.getPlayer().getWorld().getName();
    	if(!event.getPlayer().isOp() && (plugin.is_spy(event.getPlayer()) || worldname.contains("olddeathworld") || worldname.contains("oldworld") || worldname.contains("oldaerelon"))) {
    		event.setCancelled(true);
    		return;
    	}
    }
}