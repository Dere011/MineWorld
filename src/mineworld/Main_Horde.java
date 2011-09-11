package mineworld;

import me.desmin88.mobdisguise.api.MobDisguiseAPI;
import me.taylorkelly.bigbrother.datablock.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main_Horde {
	
	private Main plugin;
	public int mort = 0;
	
    public Main_Horde(Main parent) {
    	plugin = parent;
    }
    
    public void respawn(Player player) {
	    plugin.Main_MessageControl.sendTaggedMessage(p, "Bienvenue au Bunker, une horde est en cours.", 1, "[HORDE]");
		plugin.Main_MessageControl.sendTaggedMessage(p, "Vous pouvez sortir ou attendre la fin de l'apocalyspe.", 1, "[HORDE]");
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if(player == p) {
						Location spawnpoint = getHordeSpawn();
		        		if(spawnpoint != null) {
		        			player.teleport(spawnpoint);
		        			if(plugin.can_horde.contains(player)) {
		        				plugin.can_horde.remove(player);
		        				MobDisguiseAPI.disguisePlayer(p, "zombie");
		        			}
		        		}
					}
				}
			}
		}, (long) 30);
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if(player == p) {
						Location spawnpoint = getHordeSpawn();
		        		if(spawnpoint != null) {
		        			player.teleport(spawnpoint);
		        			if(plugin.can_horde.contains(player)) {
		        				plugin.can_horde.remove(player);
		        				MobDisguiseAPI.disguisePlayer(p, "zombie");
		        			}
		        		}
					}
				}
			}
		}, (long) 60);
    }
    
    public void playerTogoule(Player player) {
    	String name = player.getName();
		plugin.can_horde.add(player);
		plugin.Main_TimeControl.is_goule.add(player);
		MobDisguiseAPI.disguisePlayer(player, "zombie");
		plugin.Main_ContribControl.sendSoundToAll("http://mineworld.fr/contrib/sound/orch_hit_csharp_short.wav");
		plugin.Main_MessageControl.sendTaggedMessageToAll(ChatColor.GREEN + name + " est devenu une " + ChatColor.RED + " goule " + ChatColor.GREEN + " !", 1, "[HORDE]");
		Chat bb = new Chat((Player) plugin.zombie, name + " est devenu une goule.", "world");
		bb.send();
    }
    
    public void gouleToPlayer(Player player) {
		if(plugin.Main_TimeControl.is_goule.contains(player)) {
			plugin.Main_TimeControl.is_goule.remove(player);
		}
		if(MobDisguiseAPI.isDisguised(player)) {
			MobDisguiseAPI.undisguisePlayerAsPlayer(player, "zombie");
		}
    }
    
    public Location getHordeSpawn() {
    	int x = 99;
    	int y = 58;
    	int z = 32;
    	World world = plugin.getServer().getWorld("horde");
    	Location location = new Location(world, x, y, z);
    	return location;
    }
    
    public boolean is_spawnhorde(Player player) {
		ApplicableRegionSet set = getregion(player);
    	for (ProtectedRegion pregion : set) {
    		if(pregion.getId().contains("horde_spawn")) {
        			return true;
    		}
    	}
    	return false;
    }
	
}