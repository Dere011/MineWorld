package mineworld;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main_Divers {
	
	Main plugin;
	
	public Main_Divers(Main plugin) {
		this.plugin = plugin;
	}
	
    public void freezePlayer(final Player player, Long time) {
		if(!plugin.block_player.contains(player.getUniqueId())) {
			plugin.block_player.add(player.getUniqueId());
		}
    	player.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				if(plugin.block_player.contains(player.getUniqueId())) {
					plugin.block_player.remove(player.getUniqueId());
				}
			}
    	}, (long) time);
    }
   
    public boolean is_alco(Player p) {
    	if(plugin.CC.isClient(p, false)) {
	    	int last_alco = (Integer) plugin.getPlayerConfig(p, "time_last_alco", "int");
		    if(last_alco > (plugin.timetamps-60*5)) {
		    	return true;
		    }
    	}
	    return false;
    }
    
    public void remove_alco(Player p) {
    	if(plugin.CC.isClient(p, false)) {
    		plugin.setPlayerConfig(p, "time_last_alco", 0);
    		if(plugin.alco_player.contains(p.getUniqueId())){
    			plugin.alco_player.remove(p.getUniqueId());
    		}
    	}
    }
    
    public WorldGuardPlugin getWorldGuard() {
        Plugin theplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (theplugin == null || !(theplugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) theplugin;
    }
    
    public WorldEditPlugin getWorldEdit() {
        Plugin theplugin = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
     
        // WorldGuard may not be loaded
        if (theplugin == null || !(theplugin instanceof WorldEditPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldEditPlugin) theplugin;
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
    
    public Boolean is_spy(Player player) {
    	if(plugin.spy_player.contains(player)) {
    		return true;
    	}
    	return false;
    }
    
    public Boolean is_notp(Player player) {
    	ApplicableRegionSet set = getregion(player);
    	for (ProtectedRegion pregion : set) {
    		String id = pregion.getId();
    		if(id.contains("notp")) {
    			return true;
    		}
    	}
    	return false;
    }
    
    public int showRandomInteger(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    long range = (long)aEnd - (long)aStart + 1;
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);
	    return randomNumber;
	}
    
	public static ItemStack decreaseItemStack(ItemStack stack) {
		if (stack.getTypeId() == 0) {
			return null;
		}
		int amount = stack.getAmount() - 1;
		if (amount == 0) {
			stack = null;
		} else {
			stack.setAmount(amount);
		}
		return stack;
	}
    
}