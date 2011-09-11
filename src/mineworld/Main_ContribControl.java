package mineworld;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.player.RenderDistance;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Main_ContribControl {

	Main plugin;
	SpoutManager sp;
	
    public Main_ContribControl(Main parent) {
    	plugin = parent;
    }
    
    public void send_error(Player player, String message) {
    	if(plugin.contrib) {
    		
    		int mw = sp.getPlayer(player).getMainScreen().getWidth();
    		int mh = sp.getPlayer(player).getMainScreen().getHeight();
    		
    		GenericPopup popup = new GenericPopup();
    		popup.setWidth(mw/2);
    		popup.setHeight(mh/2);
    		int margew = ((mw/2)/2);
    		int margeh = ((mh/2)/2);
    		popup.setX(margew);
    		popup.setY(margeh);
    		
    		popup.attachWidget(new GenericButton("Test").setAlignX(Align.FIRST).setX(70).setY(102).setHeight(35).setWidth(100));
    		popup.attachWidget(new GenericTextField().setX(70).setY(142).setHeight(35).setWidth(100));
    		popup.attachWidget(new GenericSlider().setX(70).setY(242).setHeight(35).setWidth(100));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.FIRST)).setAlignX(Align.FIRST).setX(0).setY(0).setHeight(427).setWidth(240));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.FIRST)).setAlignX(Align.SECOND).setX(0).setY(0).setHeight(427).setWidth(240));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.FIRST)).setAlignX(Align.THIRD).setX(0).setY(0).setHeight(427).setWidth(240));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.SECOND)).setAlignX(Align.FIRST).setX(0).setY(0).setHeight(427).setWidth(240));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.SECOND)).setAlignX(Align.SECOND).setX(0).setY(0).setHeight(427).setWidth(240));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.SECOND)).setAlignX(Align.THIRD).setX(0).setY(0).setHeight(427).setWidth(240));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.THIRD)).setAlignX(Align.FIRST).setX(0).setY(0).setHeight(427).setWidth(240));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.THIRD)).setAlignX(Align.SECOND).setX(0).setY(0).setHeight(427).setWidth(240));
    		popup.attachWidget(((GenericLabel) new GenericLabel("Some\nLonger Text\nis").setHexColor(0xFFFFFF).setAlignY(Align.THIRD)).setAlignX(Align.THIRD).setX(0).setY(0).setHeight(427).setWidth(240));

    		popup.attachWidget(new GenericLabel("Bottom right of middle").setHexColor(0xFFFFFF).setX(130).setY(230).setHeight(427).setWidth(240));
    		((SpoutPlayer) player).getMainScreen().attachPopupScreen(popup);
    		
    	}
    }
    
    public Serializable getBlockData(Block block, String key) {
    	if(plugin.contrib) {
    		return SpoutManager.getChunkDataManager().getBlockData(key, block.getWorld(), block.getX(), block.getY(), block.getZ());
    	}else{
    		return null;
    	}
    }
    
    public void setBlockData(Block block, String key, Serializable value) {
    	if(plugin.contrib) {
    		SpoutManager.getChunkDataManager().setBlockData(key, block.getWorld(), block.getX(), block.getY(), block.getZ(), value);
    	}
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
    
    public void stopSound(Player player) {
    	if(plugin.contrib) {
    		//SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
    		//SpoutManager.getSoundManager().stopMusic(contribplayer);
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
    
    public void setSkyColortoAll(Color color) {
    	if(plugin.contrib) {
    		for (Player p : plugin.getServer().getOnlinePlayers()) {
    			if(p.isOnline()) {
    				setPlayerSkyColor(p, color);
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
    
    public void setPlayerSkyColor(Player player, Color color) {
    	if(plugin.contrib) {
    		SpoutPlayer contribplayer = SpoutManager.getPlayer(player);
    		SpoutManager.getSkyManager().setSkyColor(contribplayer, color);
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