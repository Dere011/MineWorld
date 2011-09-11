 package mineworld;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import me.desmin88.mobdisguise.api.MobDisguiseAPI;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace; 
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutCustomBlockDesign;
import org.getspout.spoutapi.player.RenderDistance;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

public class Main_PlayerListener extends PlayerListener {
	
    private final Main plugin;
	private final Random rand = new Random();
	private List<String> musiclist = null;
	
    public Main_PlayerListener(Main plugin) {
    	this.plugin = plugin;
    }
    
    public void sendTeleportEffect(final Player player, Boolean fast) {
    	Long time;
    	if(fast) { time = (long) 100; }else{ time = (long) 150; }
    	final Location location = player.getLocation();
    	plugin.D.freezePlayer(player, time);
    	plugin.CC.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/teleportationstart.wav");
    	if(plugin.CC.isClient(player, false)) {
    		for (int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++) {
	            for (int z = location.getBlockZ() - 1; z <= location.getBlockZ() + 1; z++) {
	                for (int y = location.getBlockY() + 1; y <= location.getBlockY() + 2; y++) {
	                	Block block = location.getWorld().getBlockAt(x, y, z);
		                player.sendBlockChange(block.getLocation(), Material.PORTAL, (byte) 0);
	                }
	            }
	        }
    	}
    	player.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
					plugin.CC.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/teleportation.wav");
			        for (int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++) {
			            for (int z = location.getBlockZ() - 1; z <= location.getBlockZ() + 1; z++) {
			                for (int y = location.getBlockY() + 1; y <= location.getBlockY() + 2; y++) {
			                	Block block = location.getWorld().getBlockAt(x, y, z);
				                player.sendBlockChange(block.getLocation(), block.getTypeId(), block.getData());
			                }
			            }
			        }
				}
    	}, (long) time);
	}
    
    public Location getSpawnLocation() {
    	String config = "spawn.";
    	plugin.conf_server.load();
    	int x = plugin.conf_server.getInt(config+ "x", 0);
    	int y = plugin.conf_server.getInt(config+ "y", 0);
    	int z = plugin.conf_server.getInt(config+ "z", 0);
    	String world = plugin.conf_server.getString(config + "world");
    	if(world == null) {
    		return null;
    	}
    	Location location = new Location(plugin.getServer().getWorld(world), x, y, z);
    	return location;
    }
    
    public Location getHomeLocation(Player player) {
    	String config = "load-player."+ player.getName() +".home.";
    	plugin.conf_player.load();
    	int x = plugin.conf_player.getInt(config+ "x", 0);
    	int y = plugin.conf_player.getInt(config+ "y", 0);
    	int z = plugin.conf_player.getInt(config+ "z", 0);
    	String world = plugin.conf_player.getString(config + "world");
    	if(world == null) {
    		return null;
    	}
    	Location location = new Location(plugin.getServer().getWorld(world), x, y, z);
    	return location;
    }
    
    public void setHomeLocation(Player player) {
    	String config = "load-player."+ player.getName() +".home.";
    	plugin.conf_player.load();
    	plugin.conf_player.setProperty(config+ "x", player.getLocation().getX());
    	plugin.conf_player.setProperty(config+ "y", player.getLocation().getY());
    	plugin.conf_player.setProperty(config+ "z", player.getLocation().getZ());
    	plugin.conf_player.setProperty(config + "world", player.getLocation().getWorld().getName());
    	plugin.conf_player.save();
    }
    
    public void setSpawn(Player player) {
    	String config = "spawn.";
    	plugin.conf_server.load();
    	plugin.conf_server.setProperty(config+ "x", player.getLocation().getX());
    	plugin.conf_server.setProperty(config+ "y", player.getLocation().getY());
    	plugin.conf_server.setProperty(config+ "z", player.getLocation().getZ());
    	plugin.conf_server.setProperty(config + "world", player.getLocation().getWorld().getName());
    	plugin.conf_server.save();
    }
    
    public void start_teleportation_tohome(final Player player, Boolean fast) {
    	ApplicableRegionSet set = plugin.D.getregion(player);
    	Boolean notp = false;
    	for (ProtectedRegion pregion : set) {
    		String id = pregion.getId();
    		if(id.contains("notp")) {
				notp = true;
    		}
    	}
    	if(notp) {
    		plugin.CC.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/notp.wav");
    		plugin.MC.sendTaggedMessage(player, "Teleportation impossible, vous êtes dans une zone de non-téléportation.", 1, "");
    	}else if(plugin.TC.horde || plugin.TC.prehorde) {
    		plugin.CC.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
    		plugin.MC.sendTaggedMessage(player, "Teleportation impossible, un évenement inconnu perturbe la téléportation.", 1, "");
    	}else{
    		Long time;
    	   	if(fast) {
    	   		time = (long) 100;
    	   		plugin.MC.sendTaggedMessage(player, "Teleportation dans 5 secondes.", 1, "");
				sendTeleportEffect(player, true);	
        	}else{
        		time = (long) 150;
        		plugin.MC.sendTaggedMessage(player, "Teleportation dans 10 secondes.", 1, "");
				sendTeleportEffect(player, false);
        	}
	    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run()
					{
						Location home = getHomeLocation(player);
						if(home != null) {
							player.teleport(home);
							plugin.MC.sendTaggedMessage(player, "Teleportation en cours.", 1, "");
						}else{
							plugin.CC.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
							plugin.MC.sendTaggedMessage(player, "Teleportation impossible.", 1, "");
						}
					}
	    	}, (long) time);
    	}
    }
    
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	if (plugin.D.is_spy(player) || plugin.Main_Visiteur.is_visiteur(player)) {
    		event.setCancelled(true);
    		return;
    	}
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
    		Block block = event.getClickedBlock();
    		if(block.getType() == Material.STONE_BUTTON) {
    			plugin.CC.sendSoundEffectToAllToLocation(block.getLocation(), "http://mineworld.fr/contrib/sound/click_button.wav");
    		}else if(block.getType() == Material.LEVER) {
    			plugin.CC.sendSoundEffectToAllToLocation(block.getLocation(), "http://mineworld.fr/contrib/sound/click_lever.wav");
    		}
    		if(block.getType() == Material.CHEST) {
    			plugin.CC.sendSoundEffectToAllToLocation(block.getLocation(), "http://mineworld.fr/contrib/sound/open_chest.wav");
    			// EVENT START
    			int coffre = (block.getX()-block.getY()-block.getZ());
    			if(coffre == 1254) {
        			Boolean time_customaction = (Boolean) plugin.getPlayerConfig(player, "event_01", "boolean");
                	if(!time_customaction) {
            			ItemStack item = plugin.Main_ContribControl.sp.getItemManager().getCustomItemStack(13374, 1);
            			player.getInventory().addItem(item);
            			ItemStack item2 = plugin.Main_ContribControl.sp.getItemManager().getCustomItemStack(13375, 1);
            			player.getInventory().addItem(item2);
            			plugin.MC.sendTaggedMessage(player, "Vous avez découvert un artefact rare.", 1, "");
            			plugin.MC.sendMessageToAll(player.getName()+" vient de découvrire un artefact rare.");
                		plugin.Main_ContribControl.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/gnomeftw.wav");
                		plugin.setPlayerConfig(player, "event_01", true);
                	}
    			}else if(coffre == 1485) {
    				Boolean time_customaction = (Boolean) plugin.getPlayerConfig(player, "event_02", "boolean");
        			if(!time_customaction) {
            			ItemStack item = plugin.Main_ContribControl.sp.getItemManager().getCustomItemStack(13374, 1);
            			player.getInventory().addItem(item);
            			ItemStack item2 = plugin.Main_ContribControl.sp.getItemManager().getCustomItemStack(13375, 1);
            			player.getInventory().addItem(item2);
            			plugin.MC.sendTaggedMessage(player, "Vous avez découvert un artefact rare.", 1, "");
            			plugin.MC.sendMessageToAll(player.getName()+" vient de découvrire un artefact rare.");
                		plugin.Main_ContribControl.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/gnomeftw.wav");
                		plugin.setPlayerConfig(player, "event_02", true);
                	}
    			}
    			// EVENT STOP
    		}
    	}
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		Block block = event.getClickedBlock();
		    if((block.getType() == Material.BED || block.getType() == Material.BED_BLOCK) && player.getInventory().contains(Material.COMPASS)) {
	    		int lasthome_set = (Integer) plugin.getPlayerConfig(player, "time_lasthomeset", "int");
	        	if((plugin.timetamps-lasthome_set) > 60) {
	        		setHomeLocation(player);
	        		plugin.setPlayerConfig(player, "time_lasthomeset", plugin.timetamps);
	        		plugin.CC.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/home_set.wav");
	        		plugin.MC.sendTaggedMessage(player, "Votre point de home est maintenant défini.", 1, "[HOME]");
	        	}else{
	        		plugin.CC.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
	        		plugin.MC.sendTaggedMessage(player, "Merci d'attendre 1 minute entre chaque requête.", 1, "[HOME]");
	        	}
				event.setCancelled(true);
				return;
    		}
			if (event.getPlayer().getItemInHand().getTypeId() == 324 || event.getPlayer().getItemInHand().getTypeId() == 330)
    	    {
    		    if (event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getType() == Material.CACTUS)
        		{
    		    	event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).setTypeId(0);
    		    	Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.NORTH).getLocation();
    		    	event.getClickedBlock().getWorld().dropItemNaturally(location,new ItemStack(81,1));
        		}
    	    	else if (event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getType() == Material.CACTUS)
        		{
    	    		event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).setTypeId(0);
    	    		Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.EAST).getLocation();
    		    	event.getClickedBlock().getWorld().dropItemNaturally(location,new ItemStack(81,1));
        		}
    		    else if (event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getType() == Material.CACTUS)
    	    	{
    		    	event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).setTypeId(0);
    		    	Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.SOUTH).getLocation();
    		    	event.getClickedBlock().getWorld().dropItemNaturally(location,new ItemStack(81,1));
        		}
    		    else if (event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getType() == Material.CACTUS)
    	    	{
    		    	event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).setTypeId(0);
    		    	Location location = event.getClickedBlock().getRelative(event.getBlockFace()).getRelative(BlockFace.UP).getRelative(BlockFace.WEST).getLocation();
    		    	event.getClickedBlock().getWorld().dropItemNaturally(location,new ItemStack(81,1));
        		}
    	    }
			if(event.getItem() == null)  { return; }
			int handid = event.getItem().getTypeId();
	    	if(handid == 347) {
	    		Location loc = event.getClickedBlock().getLocation();
	    		plugin.CC.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/clock_use.wav");
	    		event.getPlayer().sendMessage("Votre montre affiche les informations suivante :");
	    		Block blocksee = event.getClickedBlock().getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY()+1, loc.getBlockZ());
	    		event.getPlayer().sendMessage("Taux de lumière : "+ blocksee.getLightLevel());
	    		event.getPlayer().sendMessage("Type d'environnement (Biome) : "+ blocksee.getBiome().name());
	    		event.getPlayer().sendMessage("Heure : "+ event.getPlayer().getWorld().getTime());
	    		event.setCancelled(true);
	    	}
		}
		if(plugin.CC.isClient(event.getPlayer(), false)) {
			int handid = plugin.CC.sp.getPlayer(event.getPlayer()).getItemInHand().getDurability();
	    	if(handid == 13371) {
	    		event.getPlayer().getInventory().setItemInHand(plugin.D.decreaseItemStack(event.getPlayer().getInventory().getItemInHand()));
	    	    plugin.CC.sendSoundEffectToAllToLocation(event.getPlayer().getLocation(), "http://mineworld.fr/contrib/sound/drink_beer.wav");
	    	    plugin.MC.sendTaggedMessage(event.getPlayer(), "Vous venez de boire une bière brune.", 1, "");
	    	    plugin.setPlayerConfig(event.getPlayer(), "time_last_alco", plugin.timetamps);
	    	    plugin.alco_player.add(event.getPlayer());
	    	    event.setCancelled(true);
	    	}
		}
    }
    
    public void Main_onPlayerJoin_do(PlayerJoinEvent event) {
    	Player p = event.getPlayer();
    	int news_rev = (Integer) plugin.getServerConfig("informations.news_rev", "int");
	    boolean first_connexion = (Boolean) plugin.getPlayerConfig(p, "first_connexion", "boolean");
	    int last_news_rev = (Integer) plugin.getPlayerConfig(p, "last_news_rev", "int");
	    if(!first_connexion) {
	    	plugin.MC.sendSmallNotRegisteredMsg(p);
		}else if(last_news_rev < news_rev) {
			plugin.MC.sendSmallLastNews(p);
		}
    }
    
    public void onPlayerMove(PlayerMoveEvent event) {
    	Player p = event.getPlayer();
    	if(plugin.block_player.contains(p)) {
    		event.setCancelled(true);
    	}
    	// GESTION MUSIQUE PAR ZONE - START
		ConfigurationNode node = null;
		if(musiclist == null) {
    		Configuration conf = plugin.conf_server;
    		if(plugin.Server_configFile.exists()){
        		conf.load();
				musiclist = conf.getKeys("load-music");
				if(!musiclist.isEmpty()) {
					node = conf.getNode("load-music");
				}
    		}
		}
		// GESTION MUSIQUE PAR ZONE - END
    	if(!plugin.last_move.containsKey(p) || (plugin.last_move.get(p)+3) < plugin.timetamps) {
    		plugin.last_move.put(p, (int) plugin.timetamps);
    		Boolean stopdark = true;
    		Boolean client = false;
	    	ApplicableRegionSet set = plugin.D.getregion(p);
	    	if(plugin.CC.isClient(p, false)) {
	    		client = true;
	    	}
	    	for (ProtectedRegion pregion : set) {
	    		String id = pregion.getId();
	    		if(id.equals("stargate") || id.contains("client")) {
	    			if(!client) {
	    				Location spawnpoint = getSpawnLocation();
		        		if(spawnpoint != null && !p.isOp()) {
		        			p.teleport(spawnpoint);
		        		}
	    			}
	    		}else if(!plugin.TC.horde && (id.contains("horde_spawn") || p.getWorld().getName().contains("horde"))) {
					Location spawnpoint = getSpawnLocation();
	        		if(spawnpoint != null && !p.isOp()) {
	        			p.teleport(spawnpoint);
	        		}
	    		}else if(client && !plugin.TC.horde && id.contains("spawn_protection")) {
	    			if(plugin.last_region.get(p) == null || !plugin.last_region.get(p).contains("spawn_protection")) {
	    				plugin.CC.sendPlayerSoundEffectToLocation(p, new Location(p.getWorld(), 185, 56, 94), "http://mineworld.fr/contrib/sound/dafa.ogg");
	    				plugin.last_region.put(p, "spawn_protection");
	    			}
	    		}
	    		if(id.contains("in_dark") && !plugin.in_dark.contains(p)) {
	    			plugin.in_dark.add(p);
	    			p.setPlayerTime(15000, false);
	    			SpoutManager.getPlayer(p).setRenderDistance(RenderDistance.TINY);
	    			stopdark = false;
	    			plugin.CC.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/in_dark.wav");
	    		}else if(id.contains("in_dark") && plugin.in_dark.contains(p)) {
	    			p.setPlayerTime(15000, false);
	    			SpoutManager.getPlayer(p).setRenderDistance(RenderDistance.TINY);
	    			stopdark = false;
	    		}
	    		if(id.contains("bouncer")) {
	 				Location spawnpoint = getSpawnLocation();
	        		if(spawnpoint != null && !p.isOp()) {
	        			p.teleport(spawnpoint);
	        			plugin.CC.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/bouncer.wav");
	        		}
	    		}
	    		if(client && musiclist != null) {
					for(String music : musiclist){
						String name = node.getString(music + ".id");
						String url = node.getString(music + ".url");
						if(id.contains(name)) {
							if(plugin.last_region.get(p) == null || !plugin.last_region.get(p).contains(name)) {
								plugin.CC.sendPlayerSoundEffectToLocation(p, p.getLocation(), url);
								plugin.last_region.put(p, name);
							}
						}
					}
	    		}
	    	}
	    	if(stopdark && plugin.in_dark.contains(p)) {
		    	plugin.in_dark.remove(p);
				p.resetPlayerTime();
				SpoutManager.getPlayer(p).setRenderDistance(RenderDistance.FAR);
	    	}
    	}
    }
    
    public void onPlayerChat(PlayerChatEvent event) {
    	Player player = event.getPlayer();
    	boolean muted = (Boolean) plugin.getPlayerConfig(player, "is_muted", "boolean");
    	if (!muted) {
    		String message = event.getMessage();
      		plugin.logger.log(Level.INFO, "[CHAT] "+ player.getName() + " > "+ message);
    		if(plugin.TC.horde) {
    			if (plugin.TC.is_goule.contains(player)) {
    				plugin.CC.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/hunter_attackmix_0"+ plugin.D.showRandomInteger(1, 3, rand) +".wav");
    			}else{
    				Boolean ok = false;
    				for(Entity nearby_entity : player.getNearbyEntities(10, 10, 10)){
    					if(nearby_entity instanceof Player) {
    						if (!plugin.TC.is_goule.contains(player)) {
    							plugin.MC.chatMessage((Player) nearby_entity, player, ChatColor.RED + player.getName() +" > "+ message);
    							plugin.MC.sendTaggedMessage(player, "Vous avez aggro une goule !", 1, "[HORDE]");
    						}else{
    							plugin.MC.chatMessage((Player) nearby_entity, player, ChatColor.RED + player.getName() +" > "+ message.replace("a", "e").replace("i", "o").replace("c", "b").replace("c", "b"));
    						}
    						ok = true;
    					}
    				}
    				if(ok) {
    					plugin.MC.sendTaggedMessage(player, "Inutile, seule les hordes de zombies vous entendent.", 1, "[HORDE]");
    				}
    			}
    		}else if(player.isOp()) {
    			plugin.MC.chatMessageToAll(player, event.getMessage());
    		}else if(plugin.D.is_freebuild(player)) {
    			plugin.MC.chatMessageToAllFreeBuild(player, event.getMessage());
    		}else if(plugin.V.is_visiteur(player) || plugin.iv_chat.contains(player)) {
				if((plugin.V.visiteur_number()+plugin.iv_chat.size()) > 1) {
					plugin.MC.chatMessageToAllVisiteur(player, event.getMessage());
				}else{
					plugin.MC.sendTaggedMessage(player, "Impossible, il n'y a actuellement que vous en visiteur sur le serveur.", 1, "[VISITEUR]");
				}  
	    	}else{
	    		if(plugin.D.is_alco(player)) {
		    		int alcol = plugin.showRandomInteger(1, 5, rand);
		    		if(alcol == 1) {
		    			message = ChatColor.DARK_AQUA + message + "...Abrgggrrrgg";
		    		}else if(alcol == 2) {
		    			message = ChatColor.DARK_AQUA + message + "...ahteeggee";
		    		}else if(alcol == 3) {
		    			message = ChatColor.DARK_AQUA + "Brueegge..."+ message;
		    			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/boomer_dive_01.wav");
		    		}else if(alcol == 4) {
		    			message = ChatColor.DARK_AQUA + "...Propos incohérent...";
		    			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/boomer_dive_01.wav");
		    		}else if(alcol == 5) {
		    			message = ChatColor.DARK_AQUA + message + "...Abrggggggg";
		    		}
		    	}
	    		plugin.MC.chatMessageToAllNonVisiteur(player, message);
	    	}
    	}
		event.setCancelled(true);
		return;
    }
    
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	final Player player = event.getPlayer();
    	if (event.getMessage().contains("/spawn") && !event.getMessage().contains("/spawnmob")) {
        	int time_lastspawn = (Integer) plugin.getPlayerConfig(player, "time_lastspawn", "int");
        	if((time_lastspawn+360) < plugin.timetamps) {
        		final Location spawnpoint = getSpawnLocation();
        		if(spawnpoint != null) {
        	    	if(plugin.D.is_notp(player) && plugin.H.is_spawnhorde(player)) {
        	    		plugin.MC.sendTaggedMessage(player, "Téléportation vers la zone de spawn dans 10 secondes.", 1, "[SPAWN]");
	        	    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	        				public void run()
	        				{
	        					for (Player p : plugin.getServer().getOnlinePlayers()) {
	        						if(player == p) {
	        			        		player.teleport(spawnpoint);
	        						}
	        					}
	        				}
	        	    	}, (long) 150);
	        	    	sendTeleportEffect(player, false);
	        			plugin.setPlayerConfig(player, "time_lastspawn", plugin.timetamps);
        	    	}else{
        	    		plugin.CC.sendPlayerSoundEffect(player, plugin.BEEP_ERROR);
        	    		plugin.MC.sendTaggedMessage(player, "Teleportation impossible, vous êtes dans une zone de non-téléportation.", 1, "");
        	    	}
        		}else{
        			plugin.CC.sendPlayerSoundEffect(player, plugin.BEEP_ERROR);
        			plugin.MC.sendTaggedMessage(player, "Erreur avec le point de spawn, téléportation impossible.", 1, "[SPAWN]");
        		}
        	}else{
        		plugin.CC.sendPlayerSoundEffect(player, plugin.BEEP_ERROR);
        		plugin.MC.sendTaggedMessage(player, "Merci d'attendre "+ ChatColor.BLUE +"5 minutes"+ ChatColor.WHITE +" entre chaque requête de spawn.", 1, "[SPAWN]");
        	}
    		event.setCancelled(true);
    		return;
    	}
    	if (plugin.V.is_visiteur(event.getPlayer())) {
    		plugin.V.denied_message(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/me")) {
    		if(plugin.TC.horde || plugin.TC.prehorde) {
	    		event.setCancelled(true);
	    		return;
    		}
    	}
    	/*if (event.getMessage().equals("/cancel")) {
    		shop.cancel(player);
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/buy")) {
    		shop.buy(player);
    		event.setCancelled(true);
    		return;
    	}*/
    	if (event.getMessage().equals("/suicid") || event.getMessage().equals("/suicide")) {
    		if(plugin.H.is_spawnhorde(player)) {
    			event.setCancelled(true);
    			return;
    		}else{
	    		player.getInventory().clear();
	    		player.damage(100);
	    		event.setCancelled(true);
	    		return;
    		}
    	}
    	if (event.getMessage().equals("/iv start")) {
    		if(plugin.CC.isClient(player, true)) {
	    		if(!plugin.iv_do.contains(player)) {
	    			plugin.iv_do.add(player);
	    			plugin.MC.sendTaggedMessage(player, "Système inter-visiteur actif.", 1, "[IV]");
	    		}else{
	    			plugin.MC.sendTaggedMessage(player, "Système inter-visiteur actif.", 1, "[IV]");
	    		}
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/iv stop")) {
    		if(plugin.CC.isClient(player, true)) {
	    		if(plugin.iv_do.contains(player)) {
	    			plugin.iv_do.remove(player);
	    			if(plugin.iv_chat.contains(player)) {
	    				plugin.iv_chat.remove(player);
	    			}
	    		}else{
	    			plugin.MC.sendTaggedMessage(player, "Système inter-visiteur déjà innactif.", 1, "[IV]");
	    		}
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/iv chat")) {
    		if(plugin.CC.isClient(player, true)) {
    			if(plugin.iv_do.contains(player)) {
		    		if(!plugin.iv_chat.contains(player)) {
		    			plugin.iv_chat.add(player);
		    			plugin.MC.sendTaggedMessage(player, "Système inter-visiteur avec chat actif.", 1, "[IV]");
		    		}else{
		    			plugin.iv_chat.remove(player);
		    			plugin.MC.sendTaggedMessage(player, "Système inter-visiteur avec chat innactif.", 1, "[IV]");
		    		}
    			}else{
    				plugin.MC.sendTaggedMessage(player, "Le système inter-visiteur est innactif, vous devez l'activer avant de faire cette commande.", 1, "[IV]");
    			}
    		}
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (event.getMessage().equals("/aro")) {
    		if(plugin.CC.isClient(player, true)) {
    			int time_customaction = (Integer) plugin.getPlayerConfig(player, "time_customaction", "int");
            	if((plugin.timetamps-time_customaction) > 5) {
            		plugin.CC.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/boomer_dive_01.wav");
            		plugin.setPlayerConfig(player, "time_customaction", plugin.timetamps);
            	}else{
            		plugin.MC.sendTaggedMessage(player, "Merci d'attendre 5 secondes entre chaque action personnalisé.", 1, "");
            	}
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/ano")) {
    		if(plugin.CC.isClient(player, true)) {
    			int time_customaction = (Integer) plugin.getPlayerConfig(player, "time_customaction", "int");
            	if((plugin.timetamps-time_customaction) > 5) {
            		plugin.CC.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/No"+plugin.D.showRandomInteger(1, 12, rand)+".wav");
            		plugin.setPlayerConfig(player, "time_customaction", plugin.timetamps);
            	}else{
            		plugin.MC.sendTaggedMessage(player, "Merci d'attendre 5 secondes entre chaque action personnalisé.", 1, "");
            	}
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/arire")) {
    		if(plugin.CC.isClient(player, true)) {
    			int time_customaction = (Integer) plugin.getPlayerConfig(player, "time_customaction", "int");
            	if((plugin.timetamps-time_customaction) > 5) {
            		plugin.CC.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/Laughter"+plugin.D.showRandomInteger(1, 6, rand)+".wav");
            		plugin.setPlayerConfig(player, "time_customaction", plugin.timetamps);
            	}else{
            		plugin.MC.sendTaggedMessage(player, "Merci d'attendre 5 secondes entre chaque action personnalisé.", 1, "");
            	}
    		}
    		event.setCancelled(true);
    		return;
    	}
       	if (event.getMessage().equals("/list") || event.getMessage().equals("/online")) {
       		if(plugin.CC.isClient(player, true)) {
       			String list = "";
       			Integer visiteur = 0;
       			for (Player p : plugin.getServer().getOnlinePlayers()) {
       				if(p.isOp()) {
       					list = list + ChatColor.RED + p.getName() + ChatColor.WHITE +", ";
       				}else if(plugin.ismodo(p)) {
       					list = list + ChatColor.GREEN + p.getName() + ChatColor.WHITE +", ";
       				}else if(plugin.Main_Visiteur.is_visiteur(p)) {
       					visiteur++;
       				}else if(plugin.Main_ContribControl.isClient(p, false)) {
       					list = list + ChatColor.GOLD + p.getName() + ", ";
       				}else{
       					list = list + ChatColor.WHITE + p.getName() + ", ";
       				}
       			}
   				list = list + ChatColor.WHITE + "et "+visiteur+" visiteur(s).";
   				plugin.MC.sendTaggedMessage(player, "Joueur en ligne :", 1, "[LIST]");
   				plugin.MC.sendTaggedMessage(player, list, 1, "[LIST]");
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/stopmusic")) {
    		if(plugin.CC.isClient(player, true)) {
    			plugin.CC.stopSound(player);
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/sethome")) {
    		plugin.MC.sendTaggedMessage(player, "Vous devez cliquer sur un lit avec une boussole (dans votre inventaire) pour définir votre point de home.", 1, "");
			event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/home")) {
    		if(!plugin.H.is_spawnhorde(player)) {
	    		if(plugin.CC.isClient(player, false)) {
		    		int lasthome = (Integer) plugin.getPlayerConfig(player, "time_lasthome", "int");
			       	if((plugin.timetamps-lasthome) > 60) {
			       		start_teleportation_tohome(player, true);
			       		plugin.MC.sendTaggedMessage(player, "Vous devez cliquer sur un lit avec une boussole (dans votre inventaire) pour définir votre point de home.", 1, "[INFO]");
			       		plugin.setPlayerConfig(player, "time_lasthome", plugin.timetamps);
			       	}else{
			       		plugin.CC.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
			       		plugin.MC.sendTaggedMessage(player, "Merci d'attendre 1 minutes entre chaque requête.", 1, "");
			       	}
	    		}else{
	    			int lasthome = (Integer) plugin.getPlayerConfig(player, "time_lasthome", "int");
			       	if((plugin.timetamps-lasthome) > 180) {
			       		start_teleportation_tohome(player, false);
			       		plugin.MC.sendTaggedMessage(player, "Vous devez cliquer sur un lit avec une boussole (dans votre inventaire) pour définir votre point de home.", 1, "[INFO]");
			       		plugin.setPlayerConfig(player, "time_lasthome", plugin.timetamps);
			       	}else{
			       		plugin.MC.sendTaggedMessage(player, "Merci d'attendre 3 minute entre chaque requête.", 1, "");
			       	}
	    		}
    		}
	    	event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/setmyspawn")) {
    		if(player.isOp()) {
    			setSpawn(player);
    			player.sendMessage("Spawn set.");
    		}
    		event.setCancelled(true);
    		return;
    	}
    	// OP COMMANDES START
    	if (event.getMessage().contains("/md")) {
    		if(!player.isOp()) {
    			player.kickPlayer("Erreur securité 5003.");
	    		event.setCancelled(true);
	    		return;
    		}
    	}
    	if (event.getMessage().contains("/gives")) {
    		if(!player.isOp()) {
    			player.kickPlayer("Erreur securité 5003.");
	    		event.setCancelled(true);
	    		return;
    		}else{
    			ItemStack item = plugin.Main_ContribControl.sp.getItemManager().getCustomItemStack(Integer.parseInt(event.getMessage().replace("/gives ", "")), 1);
    			player.getInventory().addItem(item);
    		}
    	}
    	if (event.getMessage().contains("/override")) {
    		if(!player.isOp()) {
    			player.kickPlayer("Erreur securité 5003.");
	    		event.setCancelled(true);
	    		return;
    		}else{
    			Block target = player.getTargetBlock(null, 100);
    		    overrideBlock(target);
    		}
    	}
       	if (event.getMessage().contains("/reloadgives")) {
    		if(!player.isOp()) {
    			player.kickPlayer("Erreur securité 5003.");
	    		event.setCancelled(true);
	    		return;
    		}else{
        		Configuration conf = plugin.conf_items;
		    	if(plugin.ITEM_configFile.exists()){
		    		conf.load();
					List<String> itemlist = conf.getKeys("load-items");
					if(!itemlist.isEmpty()) {
						ConfigurationNode node = conf.getNode("load-items");
						for(String item : itemlist){
							int id = node.getInt(item + ".id", 0);
							String name = node.getString(item + ".name");
							String url = node.getString(item + ".url");
					    	SpoutManager.getItemManager().setCustomItemBlock(1, id, (short) 0);
					    	SpoutManager.getItemManager().setItemTexture(id, null, url);
					    	SpoutManager.getItemManager().setItemName(id, name);
						}
					}
				}
    		}
    	}
    	if (event.getMessage().contains("/goule") && player.isOp()) {
    		plugin.can_horde.add(player);
    		plugin.TC.is_goule.add(player);
    		plugin.CC.sendSoundToAll("http://mineworld.fr/contrib/sound/orch_hit_csharp_short.wav");
    		event.setCancelled(true);
    		return;
    	}
       	// OP COMMANDES END
    }
    
    public void setSpawnTimed(Player p) {
    	final Player player = p;
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if(player == p) {
						Location spawnpoint = getSpawnLocation();
		        		if(spawnpoint != null) {
		        			player.teleport(spawnpoint);
		        		}
					}
				}
			}
    	}, (long) 20);
    }
    
    public void afterjoin(Player player, Boolean back) {
		if(plugin.ismodo(player)) {
			ctc.setPlayerTitle(player, ChatColor.GREEN.toString() + "[MODO]\n"+ player.getName());
		}else if(player.isOp()) {
			ctc.setPlayerTitle(player, ChatColor.RED.toString() + "[ADMIN]\n"+ player.getName());
			ctc.setPlayerCape(player, "http://mineworld.fr/contrib/cape/admin.png");
		}
		ctc.setPlayerSunURL(player, "http://mineworld.fr/contrib/sun/sun.png");
		if(plugin.mobcycle == 3) {
			ctc.setPlayerMoonURL(player, "http://mineworld.fr/contrib/sun/new_deadmoon.png");
		}else{
			ctc.setPlayerMoonURL(player, "http://mineworld.fr/contrib/sun/new_moon.png");
		}
		if(!back) {
	   	 	ctc.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/mineworld.ogg");
	   	 	ctc.sendNotification(player, "Notification", "Bienvenue sur MineWorld!");
		}
		if(!plugin.Main_TimeControl.in_cycle) {
			plugin.Main_ContribControl.set_fog(RenderDistance.FAR);
		}
		Boolean noclient = (Boolean) plugin.getPlayerConfig(player, "noclient", "boolean");
		if(noclient && plugin.Main_ContribControl.isClient(player, false)) {
			player.kickPlayer("Désolé, votre compte est interdit de client.");
		}
		if (!player.isOp() && MobDisguiseAPI.isDisguised(player)) {
			MobDisguiseAPI.undisguisePlayerAsPlayer(player, "zombie");
		}
		if(is_alco(player) && !plugin.alco_player.contains(player)) {
			plugin.alco_player.add(player);
		}
	}
    
    public void overrideBlock(Block block){
        SpoutManager.getItemManager().overrideBlock(block, 456, 0);
    }
    
    public void buildDiamondQuartz() throws Exception {
        SpoutCustomBlockDesign diamondQuartz = new SpoutCustomBlockDesign();
        diamondQuartz.setBrightness(100);
        diamondQuartz.setBoundingBox(0, 0, 0, 1, 1, 1);
        diamondQuartz.setQuadNumber(2);
        diamondQuartz.setTexture(plugin.getDescription().getName(), "http://dl.dropbox.com/u/7238554/diamond.png");
        diamondQuartz.setMinBrightness(0.3F);
        diamondQuartz.setMaxBrightness(1F);
        diamondQuartz.setVertex(0, 0, 0.5F, 1F, 0.5F, 8, 0, 16, 16);
        diamondQuartz.setVertex(0, 1, 0.25F, 0.5F, 0F, 4, 8, 16, 16);
        diamondQuartz.setVertex(0, 2, 0.75F, 0.5F, 0F, 12, 8, 16, 16);
        diamondQuartz.setVertex(1, 0, 0.25F, 0.5F, 0F, 4, 8, 16, 16);
        diamondQuartz.setVertex(1, 1, 0.5F, 0F, 0.5F, 8, 16, 16, 16);
        diamondQuartz.setVertex(1, 2, 0.75F, 0.5F, 0F, 12, 8, 16, 16);
        SpoutManager.getItemManager().setCustomBlockDesign(456, 0, diamondQuartz);
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
    	final Player player = event.getPlayer();
    	
    	boolean remove_me = (Boolean) plugin.getPlayerConfig(player, "remove_me", "boolean");
    	if(remove_me == true) {
    		player.kickPlayer("Votre compte est bloqué.");
    		return;
    	}
    	String version = (String) plugin.getServerConfig("informations.version", "string");
   	 	String[] anTxt = plugin.MC.createstrings(2);
   	 	anTxt[0] = "Bienvenue sur "+ ChatColor.RED + "MineWorld 2.0"+ ChatColor.WHITE + ", le semi-roleplay post-apocalypse.";
   	 	anTxt[1] = "Version : MineWorld DEV "+ ChatColor.RED + "V" + version + ChatColor.WHITE +" / Minecraft "+ ChatColor.GOLD + "V1.7.3";
   	 	plugin.MC.sendTaggedMessage(player, anTxt, 2, "");
   	 	
   	 	int lastdeconnexion = (Integer) plugin.getPlayerConfig(player, "time_lastdeconnexion", "int");
   	 	String timetxt = "Moins d'une heure";
   	 	int time = (int) (plugin.timetamps-lastdeconnexion);
   	 	if(time <= 60) {
   	 		timetxt = time+" secondes";
   	 	}else{
   	 		timetxt = ((int) Math.floor(time/60))+ " minutes";
   	 	}
   	 	Boolean back = false;
		if(plugin.V.whitelist.contains(player.getName().toLowerCase())) {
			plugin.MC.sendTaggedMessage(player, "Votre compte est dans notre WhiteList.", 1, "[WHITELIST]");
			if(lastdeconnexion == 0 || (lastdeconnexion+(60*30)) < plugin.timetamps) {
				if(player.isOp()) {
					event.setJoinMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " a rejoint le serveur.");
				}else{
					event.setJoinMessage(ChatColor.GOLD + player.getName() + " a rejoint le serveur.");
				}
			}else{
				back = true;
				event.setJoinMessage(ChatColor.DARK_GRAY + player.getName() + " est de retour ("+ timetxt +").");
			}	
		}else{
			plugin.V.add_visiteur(player);
			plugin.MC.sendTaggedMessage(player, "Vous avez un compte visiteur.", 1, "[WHITELIST]");
			event.setJoinMessage(ChatColor.DARK_GRAY + "Un visiteur a rejoint le serveur.");
			plugin.MC.sendVisiteurMsg(player);
			setSpawnTimed(player);
			player.setHealth(200);
			return;
		}
		
		final Boolean backback = back;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if(player == p) {
						afterjoin(player, backback);
					}
				}
			}
    	}, (long) 30);
		
    	Main_onPlayerJoin_do(event);

    	if(plugin.is_first_loaded == false) {
    		plugin.is_first_loaded = true;
			plugin.removeallitems();
    		Configuration conf = plugin.conf_items;
    		if(plugin.ITEM_configFile.exists()){
        		conf.load();
				List<String> itemlist = conf.getKeys("load-items");
				if(!itemlist.isEmpty()) {
					ConfigurationNode node = conf.getNode("load-items");
					for(String item : itemlist){
						int id = node.getInt(item + ".id", 0);
						String name = node.getString(item + ".name");
						String url = node.getString(item + ".url");
				    	SpoutManager.getItemManager().setCustomItemBlock(1, id, (short) 0);
				    	SpoutManager.getItemManager().setItemTexture(id, null, url);
				    	SpoutManager.getItemManager().setItemName(id, name);
				    	plugin.sendInfo("[MwITEM] Chargement de l'objet "+ id +" ("+name+")");
					}
				}
				try {
				      buildDiamondQuartz();
				} catch (Exception e) {
				      e.printStackTrace();
				}
    		}
    	}
    	
    	int firsttime = (Integer) plugin.getPlayerConfig(player, "time_firsttime", "int");
    	if(firsttime == 0) {
    		setSpawnTimed(player);
    		plugin.setPlayerConfig(player, "time_firsttime", plugin.timetamps);
    	}
    	plugin.setPlayerConfig(player, "time_lastconnexion", plugin.timetamps);
    	
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					if(player == p) {
						plugin.CHC.PlayerOR.add(player);
						plugin.CHC.ResendAll.add(player);
					}
				}
			}
    	}, (long) 50);
    	
	   	if(plugin.TC.horde) {
	   		if(time > (60*10)) {
	   			respawn_player(player);
	   		}
	   	}
    }
    
    public void onPlayerQuit(PlayerQuitEvent event) {
    	if (plugin.V.is_visiteur(event.getPlayer())) {
    		event.setQuitMessage(ChatColor.DARK_GRAY + "Un visiteur a quitté le serveur.");
    		plugin.V.remove_visiteur(event.getPlayer());
    	}else{
    		event.setQuitMessage(ChatColor.DARK_GRAY + event.getPlayer().getName() + " s'est deconnecté du serveur.");
    	}
    	if(plugin.CHC.PlayerOR.contains(event.getPlayer())) {
    		plugin.CHC.PlayerOR.remove(event.getPlayer());
    	}
		if(plugin.CHC.error_tick.containsKey(event.getPlayer())) {
			plugin.CHC.error_tick.remove(event.getPlayer());
		}
		if(plugin.modo.contains(event.getPlayer().getName())) {
			plugin.modo.remove(event.getPlayer().getName());
		}
		if(plugin.correct.contains(event.getPlayer().getName())) {
			plugin.correct.remove(event.getPlayer().getName());
		}
		if(plugin.anim.contains(event.getPlayer().getName())) {
			plugin.anim.remove(event.getPlayer().getName());
		}
		if(plugin.last_region.containsKey(event.getPlayer())) {
			plugin.last_region.remove(event.getPlayer());
		}
		if(plugin.Main_TimeControl.player_horde.contains(event.getPlayer())) {
			plugin.Main_TimeControl.player_horde.remove(event.getPlayer());
		}
		if(plugin.alco_player.contains(event.getPlayer())) {
			plugin.alco_player.remove(event.getPlayer());
		}
		if(plugin.spy_player.contains(event.getPlayer())) {
			plugin.spy_player.remove(event.getPlayer());
		}
		if(plugin.iv_do.contains(event.getPlayer())) {
			plugin.iv_do.remove(event.getPlayer());
		}
		if(plugin.iv_chat.contains(event.getPlayer())) {
			plugin.iv_chat.remove(event.getPlayer());
		}
		plugin.setPlayerConfig(event.getPlayer(), "time_lastdeconnexion", plugin.timetamps);
    }
    
    public void respawn_player(final Player player) {
    	if(plugin.TC.horde) {
    		plugin.H.respawn(player);
    	}else{
        	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
    			public void run()
    			{
    				for (Player p : plugin.getServer().getOnlinePlayers()) {
    					if(player == p) {
    						Location spawnpoint = getSpawnLocation();
    		        		if(spawnpoint != null) {
    		        			player.teleport(spawnpoint);
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
    						Location spawnpoint = getSpawnLocation();
    		        		if(spawnpoint != null) {
    		        			player.teleport(spawnpoint);
    		        		}
    					}
    				}
    			}
        	}, (long) 60);
    	}
    }
    
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	respawn_player(event.getPlayer());
    	plugin.D.remove_alco(event.getPlayer());
    } 

    public void onPlayerTeleport (PlayerTeleportEvent event) {
    	final Player player = event.getPlayer();
    	if (plugin.Main_Visiteur.is_visiteur(player)) {
    		if(!event.getTo().getWorld().getName().contains("world") || event.getTo().getWorld().getName().contains("oldworld")) {
	        	int time_last_vdteleport = (Integer) plugin.getPlayerConfig(player, "time_last_vdteleport", "int");
	    	    if((plugin.timetamps-time_last_vdteleport) > 20) {
	    	    	msg.sendTaggedMessage(player, "Les visiteurs ne peuvent pas prendre ce téléporteur.", 1, "[DENIED]");
	    	    	plugin.setPlayerConfig(player, "time_last_vdteleport", plugin.timetamps);
	    	    }
	    	    respawn_player(player);
	    		event.setCancelled(true);
	    		return;
    		}
    	}
    }
    
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer()) || (!event.getPlayer().isOp() && plugin.is_spy(event.getPlayer()))) {
    		event.setCancelled(true);
    		return;
    	}
		int handid = event.getItem().getItemStack().getDurability();
    	if(handid > 13370 && !plugin.Main_ContribControl.isClient(event.getPlayer(), false)) {
    		event.setCancelled(true);
    		return;
    	}
    }
    
    public void onPlayerPreLogin (PlayerPreLoginEvent event) {
    	plugin.conf_player.load();
    	try {
			plugin.Main_Visiteur.charge_whitelist();
		} catch (IOException e) {
		}
    	Boolean banned = plugin.conf_player.getBoolean("load-player."+ event.getName() +".is_banned", false);
		Boolean admin = plugin.conf_player.getBoolean("load-player."+ event.getName() +".is_admin", false);
		Boolean modo = plugin.conf_player.getBoolean("load-player."+ event.getName() +".is_modo", false);
		Boolean anim = plugin.conf_player.getBoolean("load-player."+ event.getName() +".is_anim", false);
		Boolean correc = plugin.conf_player.getBoolean("load-player."+ event.getName() +".is_correc", false);
		if(modo && !plugin.modo.contains(event.getName())) {
			plugin.modo.add(event.getName());
		}
		if(anim && !plugin.anim.contains(event.getName())) {
			plugin.anim.add(event.getName());
		}
		if(correc && !plugin.correct.contains(event.getName())) {
			plugin.correct.add(event.getName());
		}
		if(admin) {
			event.allow();
		}else{
	    	if(banned) {
	    		String msg = plugin.conf_player.getString("load-player."+ event.getName() +".banned_msg");
	    		if(msg != "") {
	    			event.disallow(Result.KICK_BANNED, "Votre compte est banni, la raison est : "+ msg);
	    		}else{
	    			event.disallow(Result.KICK_BANNED, "Votre compte est banni.");
	    		}
	    	}else if(plugin.maintenance_status) {
	    			event.disallow(Result.KICK_FULL, plugin.maintenance_message);
	    	}
		}
    }
}