package mineworld;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace; 
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.inventory.ItemStack;

public class Main_PlayerListener extends PlayerListener {
	
    private final Main plugin;
    private final Main_NPC mnpc;
    
    public Main_PlayerListener(Main parent) {
        this.plugin = parent;
        this.mnpc = parent.Main_NPC;
    }
    
    public void sendTeleportEffect(final Player player) {
    	final Location location = player.getLocation();
        for (int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++) {
            for (int z = location.getBlockZ() - 1; z <= location.getBlockZ() + 1; z++) {
                for (int y = location.getBlockY() + 1; y <= location.getBlockY() + 2; y++) {
                	Block block = location.getWorld().getBlockAt(x, y, z);
	                player.sendBlockChange(block.getLocation(), Material.PORTAL, (byte) 0);
                }
            }
        }
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
			        for (int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++) {
			            for (int z = location.getBlockZ() - 1; z <= location.getBlockZ() + 1; z++) {
			                for (int y = location.getBlockY() + 1; y <= location.getBlockY() + 2; y++) {
			                	Block block = location.getWorld().getBlockAt(x, y, z);
				                player.sendBlockChange(block.getLocation(), block.getTypeId(), block.getData());
			                }
			            }
			        }
				}
    	}, (long) 300);
	}
    
    public void NPC_onPlayerJoin_do(PlayerJoinEvent event) {
    	if(plugin.npc_is_first_loaded == false) {
    		mnpc.ReloadAllNpcs();
    		plugin.npc_is_first_loaded = true;
			plugin.removeallitems();
    	}
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
    
    public void start_teleportation_tohome(final Player player) {
		plugin.Main_MessageControl.sendTaggedMessage(player, "Teleportation dans 10 secondes.", 1, "");
		sendTeleportEffect(player);
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run()
				{
					Location home = getHomeLocation(player);
					if(home != null) {
						player.teleport(home);
						plugin.Main_MessageControl.sendTaggedMessage(player, "Teleportation en cours.", 1, "");
					}else{
						plugin.Main_MessageControl.sendTaggedMessage(player, "Teleportation impossible.", 1, "");
					}
				}
    	}, (long) 150);
    }
    
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	if (plugin.is_spy(player) || plugin.Main_Visiteur.is_visiteur(player)) {
    		event.setCancelled(true);
    		return;
    	}
    	
    	String worldname = player.getWorld().getName();
    	if(!event.getPlayer().isOp() && (worldname.contains("olddeathworld") || worldname.contains("oldworld") || worldname.contains("oldaerelon"))) {
	    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
	    		Block block = event.getClickedBlock();
	    		if(block.getType() != Material.WOOD_DOOR && block.getType() != Material.WOODEN_DOOR) {
			    	event.setCancelled(true);
			    	return;
	    		}
	    	}
    	}
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
    		Block block = event.getClickedBlock();
    		if(block.getType() == Material.STONE_BUTTON || block.getType() == Material.LEVER) {
		    	player.getWorld().playEffect(block.getLocation(), Effect.CLICK2, 1);
    		}
    	}
    	
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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
	    		event.getPlayer().sendMessage("Votre montre affiche les informations suivante :");
	    		Block block = event.getClickedBlock().getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY()+1, loc.getBlockZ());
	    		event.getPlayer().sendMessage("Taux de lumière : "+ block.getLightLevel());
	    		event.getPlayer().sendMessage("Type d'environnement : "+ block.getBiome().name());
	    		event.getPlayer().sendMessage("Heure (Long) : "+ event.getPlayer().getWorld().getTime());
	    		event.getPlayer().sendMessage("Nom du monde : "+ event.getPlayer().getWorld().getName());
	    	}
		}
    }
    
    public void Main_onPlayerJoin_do(PlayerJoinEvent event) {
    	Player p = event.getPlayer();
    	int news_rev = (Integer) plugin.getServerConfig("informations.news_rev", "int");
	    boolean first_connexion = (Boolean) plugin.getPlayerConfig(p, "first_connexion", "boolean");
	    int last_news_rev = (Integer) plugin.getPlayerConfig(p, "last_news_rev", "int");
	    if(!first_connexion) {
	    	plugin.Main_MessageControl.sendSmallNotRegisteredMsg(p);
		}else if(last_news_rev < news_rev) {
			plugin.Main_MessageControl.sendSmallLastNews(p);
		}
    }
    
    public void onPlayerMove(PlayerMoveEvent event) {
    	Player p = event.getPlayer();
    	if (plugin.Main_Visiteur.is_visiteur(p)) {
			String WorldName = p.getWorld().getName();
    		if(!WorldName.contains("world") || WorldName.contains("oldworld")) {
	        	int time_last_vdteleport = (Integer) plugin.getPlayerConfig(p, "time_last_vdteleport", "int");
	    	    if((plugin.timetamps-time_last_vdteleport) > 20) {
	    	    	plugin.Main_MessageControl.sendTaggedMessage(p, "Les visiteurs ne peuvent pas prendre ce téléporteur.", 1, "[DENIED]");
	    	    	plugin.setPlayerConfig(p, "time_last_vdteleport", plugin.timetamps);
	    	    }
	    	    respawn_player(p);
    		}
    		return;
    	}
    }
    
    public void onPlayerChat(PlayerChatEvent event) {
    	Player p = event.getPlayer();
    	boolean muted = (Boolean) plugin.getPlayerConfig(p, "is_muted", "boolean");
    	if (!muted) {
    		if(plugin.is_spy(p)) {
    			plugin.Main_MessageControl.sendTaggedMessage(p, "Le mode SPY interdit l'utilisation du chat.", 1, "[DENIED]");
	    		event.setCancelled(true);
	    		return;
    		}
    		if(p.isOp()) {
	    		plugin.Main_MessageControl.chatMessageToAll(p, event.getMessage());
	    		event.setCancelled(true);
	    		return;
    		}else if (plugin.Main_Visiteur.is_visiteur(p)) {
				if(plugin.Main_Visiteur.visiteur_number() > 1) {
					plugin.Main_MessageControl.chatMessageToAllVisiteur(p, event.getMessage());
					event.setCancelled(true);
					return;
				}else{
					plugin.Main_MessageControl.sendTaggedMessage(p, "Impossible, il n'y a que vous en visiteur sur le serveur.", 1, "[DENIED]");
					event.setCancelled(true);
					return;
				}
	    	}else{
	    		plugin.Main_MessageControl.chatMessageToAllNonVisiteur(p, event.getMessage());
	    		event.setCancelled(true);
	    		return;
	    	}
    	}else{
    		event.setCancelled(true);
    		return;
    	}
    }
    
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    	Player player = event.getPlayer();
    	if (event.getMessage().contains("/spawn") && !event.getMessage().contains("/spawnmob")) {
        	int time_lastspawn = (Integer) plugin.getPlayerConfig(player, "time_lastspawn", "int");
        	if((plugin.timetamps-time_lastspawn) > 360) {
        		Location spawnpoint = getSpawnLocation();
        		if(spawnpoint != null) {
        			plugin.Main_MessageControl.sendTaggedMessage(player, "Téléportation vers la zone de spawn dans 10 secondes.", 1, "[SPAWN]");
        	    	respawn_player_deleyed(player);
        	    	sendTeleportEffect(player);
        			plugin.setPlayerConfig(player, "time_lastspawn", plugin.timetamps);
        		}else{
        			plugin.Main_MessageControl.sendTaggedMessage(player, "Erreur avec le point de spawn, téléportation impossible.", 1, "[SPAWN]");
        		}
        	}else{
        		plugin.Main_MessageControl.sendTaggedMessage(player, "Merci d'attendre "+ ChatColor.BLUE +"360 secondes"+ ChatColor.WHITE +" entre chaque requête de spawn.", 1, "[SPAWN]");
        	}
    		event.setCancelled(true);
    		return;
    	}
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer())) {
    		plugin.Main_Visiteur.denied_message(event.getPlayer());
    		event.setCancelled(true);
    		return;
    	}
    	String worldname = event.getPlayer().getWorld().getName();
    	if(!event.getPlayer().isOp() && (worldname.contains("olddeathworld") || worldname.contains("oldworld") || worldname.contains("oldaerelon"))) {
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/sethome")) {
    		if(player.isSleeping() && player.getInventory().contains(Material.COMPASS)) {
	    	 	int lasthome_set = (Integer) plugin.getPlayerConfig(player, "time_lasthomeset", "int");
	        	if((plugin.timetamps-lasthome_set) > 60) {
	        		setHomeLocation(player);
	        		plugin.setPlayerConfig(player, "time_lasthomeset", plugin.timetamps);
	        		plugin.Main_MessageControl.sendTaggedMessage(player, "Votre point de home est maintenant défini.", 1, "");
	        	}else{
	        		plugin.Main_MessageControl.sendTaggedMessage(player, "Merci d'attendre 1 minute entre chaque requête.", 1, "");
	        	}
    		}else{
    			plugin.Main_MessageControl.sendTaggedMessage(player, "Vous devez être dans un lit avec une boussole pour définir votre point de home.", 1, "");
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/home")) {
    		int lasthome = (Integer) plugin.getPlayerConfig(player, "time_lasthome", "int");
	       	if((plugin.timetamps-lasthome) > 60) {
	       		start_teleportation_tohome(player);
	       		plugin.Main_MessageControl.sendTaggedMessage(player, "Vous devez utiliser une boussole pour définir un home.", 1, "[INFO]");
	       		plugin.setPlayerConfig(player, "time_lasthome", plugin.timetamps);
	       	}else{
	       		plugin.Main_MessageControl.sendTaggedMessage(player, "Merci d'attendre 1 minute entre chaque requête.", 1, "");
	       	}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/setmyspawn")) {
    		if(player.isOp()) {
    			setSpawn(player);
    			player.sendMessage("Spawn set.");
    		}else{
    			player.kickPlayer("Commande interdite.");
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/setspawn")) {
    		if(player.isOp()) {
    			player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
    			player.sendMessage("Spawn set.");
    		}else{
    			player.kickPlayer("Commande interdite.");
    		}
    		event.setCancelled(true);
    		return;
    	}
    }
    
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
    	Player player = event.getPlayer();
    	if (plugin.Main_Visiteur.is_visiteur(player)) {
    		return;
    	}
    	int sneaking_lasttime = (Integer) plugin.getPlayerConfig(player, "time_sneaking_chunk", "int");
    	int sneaking_tick = (Integer) plugin.getPlayerConfig(player, "sneaking_tick", "int");
    	if(sneaking_tick == 1) {
		    if((plugin.timetamps-sneaking_lasttime) > 120) {
		    	plugin.Main_MessageControl.sendTaggedMessage(player, "Le mode sneaking vous cache des monstres qui sont loin de votre position.", 1, "[INFO-MINEWORLD]");
		    	plugin.setPlayerConfig(player, "time_sneaking_chunk", plugin.timetamps);
		    	plugin.setPlayerConfig(player, "sneaking_tick", 0);
		    }
    	}else{
    	    if((plugin.timetamps-sneaking_lasttime) > 120) {
    	    	plugin.Main_MessageControl.sendTaggedMessage(player, "Le mode sneaking corrige le problème des joueurs / items invisibles.", 1, "[INFO-MINEWORLD]");
    	    	plugin.setPlayerConfig(player, "time_sneaking_chunk", plugin.timetamps);
    	    	plugin.setPlayerConfig(player, "sneaking_tick", 1);
    	    }
    	}
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

    public void onPlayerJoin(PlayerJoinEvent event) {
    	final Player player = event.getPlayer();
    	boolean remove_me = (Boolean) plugin.getPlayerConfig(player, "remove_me", "boolean");
    	if(remove_me == true) {
    		player.kickPlayer("Votre compte est bloqué.");
    		return;
    	}
    	
    	String version = (String) plugin.getServerConfig("informations.version", "string");
   	 	String[] anTxt = plugin.Main_MessageControl.createstrings(2);
   	 	anTxt[0] = "Bienvenue sur MineWorld 2.0, le serveur semi-roleplay post-apocalypse.";
   	 	anTxt[1] = "Version : MineWorld DEV "+ ChatColor.RED + "V" + version + ChatColor.WHITE +" / Minecraft "+ ChatColor.GOLD + "V1.6.6";
   	 	plugin.Main_MessageControl.sendTaggedMessage(player, anTxt, 2, "");
   	 	
   	 	int lastdeconnexion = (Integer) plugin.getPlayerConfig(player, "time_lastdeconnexion", "int");
   	 	String timetxt = "";
   	 	int time = (int) (plugin.timetamps-lastdeconnexion);
   	 	if(time < 60) {
   	 		timetxt = time+" secondes";
   	 	}else if(time < 120) {
   	 		timetxt = "1 minute";
   	 	}else if(time < 180) {
   	 		timetxt = "2 minutes";
   	 	}else if(time < 240) {
   	 		timetxt = "3 minutes";	
   	 	}else if(time < 300) {
   	 		timetxt = "4 minutes";
   	 	}else if(time < 360) {
   	 		timetxt = "5 minutes";
   	 	}else{
   	 		timetxt = "";
   	 	}
   	 	
		if(plugin.Main_Visiteur.whitelist.contains(player.getName().toLowerCase())) {
			plugin.Main_MessageControl.sendTaggedMessage(player, "Votre compte est bien dans notre WhiteList.", 1, "[WHITELIST]");
			if(lastdeconnexion == 0 || (lastdeconnexion+360) < plugin.timetamps) {
				if(event.getPlayer().isOp()) {
					event.setJoinMessage(ChatColor.RED + event.getPlayer().getName() + ChatColor.GOLD + " a rejoint le serveur.");
				}else{
					event.setJoinMessage(ChatColor.GOLD + event.getPlayer().getName() + " a rejoint le serveur.");
				}
			}else{
				event.setJoinMessage(ChatColor.DARK_GRAY + event.getPlayer().getName() + " est de retour ("+ timetxt +").");
			}
		}else{
			plugin.Main_Visiteur.add_visiteur(player);
			plugin.Main_MessageControl.sendTaggedMessage(player, "Vous avez un compte visiteur.", 1, "[WHITELIST]");
			event.setJoinMessage(ChatColor.DARK_GRAY + "Un visiteur a rejoint le serveur.");
			player.setDisplayName("visiteur");
			setSpawnTimed(player);
			player.setHealth(200);
			return;
		}
		
    	// Main
    	Main_onPlayerJoin_do(event);

        // NPC
    	NPC_onPlayerJoin_do(event);
    	
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
						plugin.Main_ChunkControl.PlayerOR.add(player);
					}
				}
			}
    	}, (long) 50);
    }
    
    public void onPlayerQuit(PlayerQuitEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer())) {
    		event.setQuitMessage(ChatColor.DARK_GRAY + "Un visiteur a quitté le serveur.");
    		plugin.Main_Visiteur.remove_visiteur(event.getPlayer());
    	}else{
    		event.setQuitMessage(ChatColor.DARK_GRAY + event.getPlayer().getName() + " a quitté le serveur.");
    	}
    	if(plugin.Main_ChunkControl.player_lastchunk.containsKey(event.getPlayer())) {
    		plugin.Main_ChunkControl.player_lastchunk.remove(event.getPlayer());
    	}
    	if(plugin.Main_ChunkControl.PlayerOR.contains(event.getPlayer())) {
    		plugin.Main_ChunkControl.PlayerOR.remove(event.getPlayer());
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
		if(plugin.stop.contains(event.getPlayer().getName())) {
			plugin.stop.remove(event.getPlayer().getName());
		}
		plugin.setPlayerConfig(event.getPlayer(), "time_lastdeconnexion", plugin.timetamps);
    }
    
    public void respawn_player(Player p) {
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
    
    public void respawn_player_deleyed(Player p) {
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
    	}, (long) 100);
    	
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
    	}, (long) 120);
    }
    
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	respawn_player(event.getPlayer());
    }
    
    public void mvspawn(final Player player) {
    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run()
			{
				player.performCommand("mvspawn");
			}
    	}, (long) 30);
    }

    public void onPlayerTeleport (PlayerTeleportEvent event) {
    	final Player player = event.getPlayer();
    	
    	// ANTI VISITEUR
    	if (plugin.Main_Visiteur.is_visiteur(player)) {
    		if(!event.getTo().getWorld().getName().contains("world") || event.getTo().getWorld().getName().contains("oldworld")) {
	        	int time_last_vdteleport = (Integer) plugin.getPlayerConfig(player, "time_last_vdteleport", "int");
	    	    if((plugin.timetamps-time_last_vdteleport) > 20) {
	    	    	plugin.Main_MessageControl.sendTaggedMessage(player, "Les visiteurs ne peuvent pas prendre ce téléporteur.", 1, "[DENIED]");
	    	    	plugin.setPlayerConfig(player, "time_last_vdteleport", plugin.timetamps);
	    	    }
	    	    respawn_player(player);
	    		event.setCancelled(true);
	    		return;
    		}
    	}

    	// ANTI INVISIBLE
    	plugin.Main_ChunkControl.anti_invisible_delayed(player);
    	
    	// ANCIEN MONDES
    	if(event.getTo().getWorld() != null) {
	    	String lastworld = (String) plugin.getPlayerConfig(player, "last_world", "string");
			plugin.setPlayerConfig(player, "last_world", event.getTo().getWorld().getName());
			if(lastworld != null && !lastworld.isEmpty()) {
		    	if(!lastworld.contains(event.getTo().getWorld().getName())) {
		    		if(event.getTo().getWorld().getName().contains("oldworld")) {
		    			player.sendMessage("Terre, premier monde MineWorld 1.0.");
		    			mvspawn(player);
		    		}
		    		if(event.getTo().getWorld().getName().contains("oldaerelon")) {
		    			player.sendMessage("Aerelon, deuxième monde MineWorld 1.0.");
		    			mvspawn(player);
		    		}
		    		if(event.getTo().getWorld().getName().contains("olddeathworld")) {
		       			player.sendMessage("DeathWorld, troisième monde MineWorld 1.0.");
		    			mvspawn(player);
		    		}
		    	}
			}
    	}
    }
    
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer())) {
    		event.setCancelled(true);
    		return;
    	}
    	String worldname = event.getPlayer().getWorld().getName();
    	if(!event.getPlayer().isOp() && (plugin.is_spy(event.getPlayer()) || worldname.contains("olddeathworld") || worldname.contains("oldworld") || worldname.contains("oldaerelon"))) {
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
		Boolean stop = plugin.conf_player.getBoolean("load-player."+ event.getName() +".is_stop", false);
		if(modo && !plugin.modo.contains(event.getName())) {
			plugin.modo.add(event.getName());
		}
		if(anim && !plugin.anim.contains(event.getName())) {
			plugin.anim.add(event.getName());
		}
		if(correc && !plugin.correct.contains(event.getName())) {
			plugin.correct.add(event.getName());
		}
		if(stop && !plugin.stop.contains(event.getName())) {
			plugin.stop.add(event.getName());
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