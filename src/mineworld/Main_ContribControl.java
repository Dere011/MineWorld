package mineworld;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;

public class Main_ContribControl {

	Main plugin;
	
    public Main_ContribControl(Main parent) {
    	plugin = parent;
    }
    
    //ContribPlayer player = ContribCraftPlayer.getContribPlayer(p);
    public void sendSoundToAll(String url) {
    	if(plugin.contrib) {
			BukkitContrib.getSoundManager().playGlobalCustomMusic(plugin, url, false, null, -1, 60);
    	}
    }
    
    public void sendSoundEffectToAllToLocation(Location location, String url) {
    	if(plugin.contrib) {
			BukkitContrib.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, location, 30, 60);
    	}
    }
    
    public void sendSoundEffectToAll(String url) {
    	if(plugin.contrib) {
			BukkitContrib.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, null, -1, 60);
    	}
    }
    
    public void sendNotification(Player player, String title, String text) {
    	if(plugin.contrib) {
    		ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			contribplayer.sendNotification(title, text, Material.FIRE);
    	}
    }
    
    public void sendNotification(Player player, String title, String text, Material mat) {
    	if(plugin.contrib) {
    		ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			contribplayer.sendNotification(title, text, mat);
    	}
    }
    
    public void sendNotificationToAll(String title, String text, Material water) {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				sendNotification(p, title, text, water);
    			}
    		}
    	}
    }
    
    public void sendPlayerSoundEffect(Player player, String url) {
    	if(plugin.contrib) {
    		ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			BukkitContrib.getSoundManager().playCustomSoundEffect(plugin, contribplayer, url, false, player.getLocation(), 30, 60);
    	}
    }
    
    public void sendPlayerSoundEffectToLocation(Player player, Location location, String url) {
    	if(plugin.contrib) {
    		ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			BukkitContrib.getSoundManager().playCustomSoundEffect(plugin, contribplayer, url, false, location, 30, 60);
    	}
    }

    public void bool_clouds(Boolean bool) {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(p);
    				BukkitContrib.getSkyManager().setCloudsVisible(contribplayer, bool);
    			}
    		}
    	}
    }
    
    public void setSunURLtoAll(String url) {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				setPlayerSunURL(p, url);
    			}
    		}
    	}
    }
    
    public void setPlayerSunURL(Player player, String url) {
    	if(plugin.contrib) {
			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			BukkitContrib.getSkyManager().setSunTextureUrl(contribplayer, url);
    	}
    }
    
    public void setPlayerMoonURL(Player player, String url) {
    	if(plugin.contrib) {
			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			BukkitContrib.getSkyManager().setMoonTextureUrl(contribplayer, url);
    	}
    }
    
    public void setPlayerTitle(Player player, String title) {
    	if(plugin.contrib) {
			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			BukkitContrib.getAppearanceManager().setGlobalTitle(contribplayer, title);
    	}
    }
    
    public void setEntityTitleToPlayer(Player player, LivingEntity entity, String title) {
    	if(plugin.contrib) {
			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			BukkitContrib.getAppearanceManager().setPlayerTitle(contribplayer, entity, title);
    	}
    }
    
    public void setPlayerSkin(Player player, String url) {
    	if(plugin.contrib) {
			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			BukkitContrib.getAppearanceManager().resetGlobalSkin(contribplayer);
			BukkitContrib.getAppearanceManager().setGlobalSkin(contribplayer, url);
    	}
    }
    
    public void setPlayerSkin(HumanEntity entity, String url) {
    	if(plugin.contrib) {
			BukkitContrib.getAppearanceManager().setGlobalSkin(entity, url);
    	}
    }
    
    public void setTexturePack(Player player, String url) {
    	if(plugin.contrib) {
			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			contribplayer.setTexturePack(url);
    	}
    }
    
    public void setPlayerCape(Player player, String url) {
    	if(plugin.contrib) {
			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			BukkitContrib.getAppearanceManager().setGlobalCloak(contribplayer, url);
    	}
    }
    
    public Boolean isClient(Player player, Boolean msg) {
    	if(plugin.contrib) {
			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
			if(contribplayer.isBukkitContribEnabled()) {
				return true;
			}else{
				if(msg) {
					plugin.Main_MessageControl.sendTaggedMessage(player, "Vous devez avoir le client MW pour faire cette action.", 1, "");
				}
				return false;
			}
    	}else{
    		return true;
    	}
    }
}