package mineworld;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.RenderDistance;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Main_ContribControl {

	Main plugin;
	
    public Main_ContribControl(Main parent) {
    	plugin = parent;
    }
    
    public void sendSoundToAll(String url) {
    	if(plugin.contrib) {
    		SpoutManager.getSoundManager().playGlobalCustomMusic(plugin, url, false, null, -1, 50);
    	}
    }
    
    public void sendSoundEffectToAllToLocation(Location location, String url) {
    	if(plugin.contrib) {
    		SpoutManager.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, location, 25, 60);
    	}
    }
    
    public void sendSoundEffectToAll(String url) {
    	if(plugin.contrib) {
    		SpoutManager.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, null, -1, 60);
    	}
    }
    
    public void sendSoundEffectToAll(String url, int volume) {
    	if(plugin.contrib) {
    		SpoutManager.getSoundManager().playGlobalCustomSoundEffect(plugin, url, false, null, -1, volume);
    	}
    }
    
    public void sendNotification(Player player, String title, String text) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
			contribplayer.sendNotification(title, text, Material.FIRE);
    	}
    }
    
    public void sendNotification(Player player, String title, String text, Material mat) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
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
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
    		SpoutManager.getSoundManager().playCustomSoundEffect(plugin, contribplayer, url, false, player.getLocation(), 25, 60);
    	}
    }
    
    public void sendPlayerSoundEffectToLocation(Player player, Location location, String url) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
    		SpoutManager.getSoundManager().playCustomSoundEffect(plugin, contribplayer, url, false, location, 25, 60);
    	}
    }
    
    public void stopSound(Player player) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
    		SpoutManager.getSoundManager().stopMusic(contribplayer);
    	}
    }
    
    public void stopAllSound() {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				stopSound(p);
    			}
    		}
    	}
    }
    
    public void set_fog(RenderDistance render) {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				SpoutPlayer contribplayer = SpoutManager.getPlayer(p);
    				contribplayer.setRenderDistance(render);
    			}
    		}
    	}
    }

    public void bool_clouds(Boolean bool) {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				SpoutPlayer contribplayer = SpoutManager.getPlayer(p);
    				SpoutManager.getSkyManager().setCloudsVisible(contribplayer, bool);
    			}
    		}
    	}
    }
    
    public void h_clouds(int h) {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				SpoutPlayer contribplayer = SpoutManager.getPlayer(p);
    				SpoutManager.getSkyManager().setCloudHeight(contribplayer, h);
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
    
    public void setMoonURLtoAll(String url) {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				setPlayerMoonURL(p, url);
    			}
    		}
    	}
    }
    
    public void setPlayerSunURL(Player player, String url) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
			SpoutManager.getSkyManager().setSunTextureUrl(contribplayer, url);
    	}
    }
    
    public void setPlayerMoonURL(Player player, String url) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
			SpoutManager.getSkyManager().setMoonTextureUrl(contribplayer, url);
    	}
    }
    
    public void setPlayerTitle(Player player, String title) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
			SpoutManager.getAppearanceManager().setGlobalTitle(contribplayer, title);
    	}
    }
    
    public void setEntityTitleToPlayer(Player player, LivingEntity entity, String title) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
			SpoutManager.getAppearanceManager().setPlayerTitle(contribplayer, entity, title);
    	}
    }
    
    public void setPlayerSkin(Player player, String url) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
			SpoutManager.getAppearanceManager().resetGlobalSkin(contribplayer);
			SpoutManager.getAppearanceManager().setGlobalSkin(contribplayer, url);
    	}
    }
    
    public void setPlayerSkin(HumanEntity entity, String url) {
    	if(plugin.contrib) {
    		SpoutManager.getAppearanceManager().setGlobalSkin(entity, url);
    	}
    }
    
    public void setTexturePack(Player player, String url) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
			contribplayer.setTexturePack(url);
    	}
    }
    
    public void setPlayerCape(Player player, String url) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
    		SpoutManager.getAppearanceManager().setGlobalCloak(contribplayer, url);
    	}
    }
    
    public Boolean isClient(Player player, Boolean msg) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
			if(contribplayer.isSpoutCraftEnabled()) {
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