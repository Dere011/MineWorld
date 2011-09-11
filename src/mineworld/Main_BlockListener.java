package mineworld;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main_BlockListener extends BlockListener {
	
	Main plugin;

    public Main_BlockListener(Main parent) {
    	plugin = parent;
    }
    
    public WorldGuardPlugin getWorldGuard() {
        Plugin theplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (theplugin == null || !(theplugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) theplugin;
    }
    
    public ApplicableRegionSet getregion(Player player) {
    	WorldGuardPlugin worldGuard = getWorldGuard();
    	com.sk89q.worldedit.Vector pt = toVector(player.getLocation());
    	RegionManager regionManager = worldGuard.getRegionManager(player.getWorld());
    	return regionManager.getApplicableRegions(pt);
    }
    
    public Boolean is_freebuild(Player player) {
    	ApplicableRegionSet set = getregion(player);
    	for (ProtectedRegion pregion : set) {
    		String id = pregion.getId();
    		if(id.equals("freebuild")) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public void onBlockIgnite(BlockIgniteEvent event) {
		if(event.getCause() != null && event.getCause() == IgniteCause.LIGHTNING) {
			event.setCancelled(true);
		}
    }
    
    public void onBlockPlace(BlockPlaceEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer()) && !is_freebuild(event.getPlayer())) {
    		plugin.Main_Visiteur.denied_message(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
		int handid = event.getItemInHand().getDurability();
    	if(handid > 13370) {
    		event.setCancelled(true);
    		return;
    	}
    	if(!event.getPlayer().isOp() && plugin.is_spy(event.getPlayer())) {
    		event.setCancelled(true);
    		return;
    	}
    }
    
    public void onBlockDamage(BlockDamageEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer()) && !is_freebuild(event.getPlayer())) {
    		plugin.Main_Visiteur.denied_message(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
		int handid = event.getItemInHand().getDurability();
    	if(handid > 13370) {
    		event.setCancelled(true);
    		return;
    	}
    	if(!event.getPlayer().isOp() && plugin.is_spy(event.getPlayer())) {
    		event.setCancelled(true);
    		return;
    	}
    }
    
    public void onBlockBreak(BlockBreakEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer()) && !is_freebuild(event.getPlayer())) {
    		plugin.Main_Visiteur.denied_message(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	if(!event.getPlayer().isOp() && plugin.is_spy(event.getPlayer())) {
    		event.setCancelled(true);
    		return;
    	}
    }
}