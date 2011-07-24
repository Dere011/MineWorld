package mineworld;

import java.io.IOException;
import java.util.Random;

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
import org.bukkit.plugin.Plugin;
import org.bukkitcontrib.BukkitContrib;
import org.bukkitcontrib.event.input.RenderDistance;
import org.bukkitcontrib.player.ContribCraftPlayer;
import org.bukkitcontrib.player.ContribPlayer;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

public class Main_PlayerListener extends PlayerListener {
	
    private final Main plugin;
    private final Main_NPC mnpc;
    private final Main_ShopSystem shop;
	private final Main_MessageControl msg;
	private final Main_ChunkControl cc;
	private final Main_TimeControl tc;
	private final Main_ContribControl ctc;
	private final Random rand = new Random();
	
    public Main_PlayerListener(Main parent) {
    	this.plugin = parent;
    	this.mnpc = parent.Main_NPC;
    	this.shop = parent.Main_ShopSystem;
    	this.msg = parent.Main_MessageControl;
    	this.cc = parent.Main_ChunkControl;
    	this.tc = parent.Main_TimeControl;
    	this.ctc = parent.Main_ContribControl;
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
    
    public void sendTeleportEffect(final Player player, Boolean fast) {
    	Long time;
    	if(fast) {
    		time = (long) 100;
    	}else{
    		time = (long) 150;
    	}
    	final Location location = player.getLocation();
    	plugin.freeze(player, time);
    	if(plugin.Main_ContribControl.isClient(player, false)) {
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
    
    public WorldGuardPlugin getWorldGuard() {
        Plugin theplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
     
        // WorldGuard may not be loaded
        if (theplugin == null || !(theplugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }
     
        return (WorldGuardPlugin) theplugin;
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
    
    public void start_teleportation_tohome(final Player player, Boolean fast) {
		if(tc.pre_dead_sun || tc.dead_sun) {
    		ctc.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
    		msg.sendTaggedMessage(player, "Teleportation impossible, un �venement inconnu perturbe la t�l�portation.", 1, "");
    	}else{
    		Long time;
    	   	if(fast) {
    	   		time = (long) 100;
				msg.sendTaggedMessage(player, "Teleportation dans 5 secondes.", 1, "");
				sendTeleportEffect(player, true);	
        	}else{
        		time = (long) 150;
				msg.sendTaggedMessage(player, "Teleportation dans 10 secondes.", 1, "");
				sendTeleportEffect(player, false);
        	}
	    	plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run()
					{
						Location home = getHomeLocation(player);
						if(home != null) {
							player.teleport(home);
							msg.sendTaggedMessage(player, "Teleportation en cours.", 1, "");
						}else{
							ctc.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
							msg.sendTaggedMessage(player, "Teleportation impossible.", 1, "");
						}
					}
	    	}, (long) time);
    	}
    }
    
    public void onPlayerInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	if (plugin.is_spy(player) || plugin.Main_Visiteur.is_visiteur(player)) {
    		event.setCancelled(true);
    		return;
    	}
    	String worldname = player.getWorld().getName();
    	
    	if(!event.getPlayer().isOp() && worldname.contains("old")) {
	    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
	    		Block block = event.getClickedBlock();
	    		if(block.getType() != Material.WOOD_DOOR && block.getType() != Material.WOODEN_DOOR) {
			    	event.setCancelled(true);
			    	return;
	    		}
	    	}
    	}
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    		Block block = event.getClickedBlock();
		    if((block.getType() == Material.BED || block.getType() == Material.BED_BLOCK) && player.getInventory().contains(Material.COMPASS)) {
	    		int lasthome_set = (Integer) plugin.getPlayerConfig(player, "time_lasthomeset", "int");
	        	if((plugin.timetamps-lasthome_set) > 60) {
	        		setHomeLocation(player);
	        		plugin.setPlayerConfig(player, "time_lasthomeset", plugin.timetamps);
	        		msg.sendTaggedMessage(player, "Votre point de home est maintenant d�fini.", 1, "");
	        	}else{
	        		ctc.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
	        		msg.sendTaggedMessage(player, "Merci d'attendre 1 minute entre chaque requ�te.", 1, "");
	        	}
				event.setCancelled(true);
				return;
    		}
    	}
    	
    	if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
    		Block block = event.getClickedBlock();
    		if(block.getType() == Material.STONE_BUTTON || block.getType() == Material.LEVER) {
		    	player.getWorld().playEffect(block.getLocation(), Effect.CLICK2, 1);
    		}
    		if(block.getType() == Material.CHEST && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
    			ctc.sendSoundEffectToAllToLocation(block.getLocation(), "http://mineworld.fr/contrib/sound/open_chest.wav");
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
	    		event.getPlayer().sendMessage("Taux de lumi�re : "+ block.getLightLevel());
	    		event.getPlayer().sendMessage("Type d'environnement (Biome) : "+ block.getBiome().name());
	    		event.getPlayer().sendMessage("Heure (Long) : "+ event.getPlayer().getWorld().getTime());
	    	}
		}
		/*if(event.getItem() == null)  { return; }
  		if(event.getItem().getType() == Material.SHEARS && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
			ctc.sendSoundEffectToAllToLocation(event.getPlayer().getLocation(), "http://mineworld.fr/contrib/sound/sf_neon_03.ogg");
		}*/
    }
    
    public void Main_onPlayerJoin_do(PlayerJoinEvent event) {
    	Player p = event.getPlayer();
    	int news_rev = (Integer) plugin.getServerConfig("informations.news_rev", "int");
	    boolean first_connexion = (Boolean) plugin.getPlayerConfig(p, "first_connexion", "boolean");
	    int last_news_rev = (Integer) plugin.getPlayerConfig(p, "last_news_rev", "int");
	    if(!first_connexion) {
	    	msg.sendSmallNotRegisteredMsg(p);
		}else if(last_news_rev < news_rev) {
			msg.sendSmallLastNews(p);
		}
    }
    
    public void onPlayerMove(PlayerMoveEvent event) {
    	Player p = event.getPlayer();
    	if(plugin.block_player.contains(p)) {
    		event.setCancelled(true);
    	}
    	
    	// TESTE
    	WorldGuardPlugin worldGuard = getWorldGuard();
    	com.sk89q.worldedit.Vector pt = toVector(p.getLocation());
    	RegionManager regionManager = worldGuard.getRegionManager(p.getWorld());
    	ApplicableRegionSet set = regionManager.getApplicableRegions(pt);
    	for (ProtectedRegion pregion : set) {
    		if(pregion.getId().contains("spawn_protection")) {
    			if(plugin.last_region.get(p) == null || !plugin.last_region.get(p).contains("spawn_protection")) {
    				plugin.Main_ContribControl.sendPlayerSoundEffectToLocation(p, new Location(p.getWorld(), 191, 54, 86), "http://mineworld.fr/contrib/sound/dafa.ogg");
    				plugin.last_region.put(p, "spawn_protection");
    			}
    		}else if(pregion.getId().contains("maisondere")) {
    			if(plugin.last_region.get(p) == null || !plugin.last_region.get(p).contains("maison_dere")) {
    				plugin.Main_ContribControl.sendPlayerSoundEffectToLocation(p, new Location(p.getWorld(), -631, 72, -545), "http://mineworld.fr/contrib/sound/portal_still_alive.wav");
    				plugin.last_region.put(p, "maison_dere");
    			}
    		}
    	}
    	// TESTE
    	
    	if (plugin.Main_Visiteur.is_visiteur(p)) {
			String WorldName = p.getWorld().getName();
    		if(!WorldName.contains("world") || WorldName.contains("oldworld")) {
	        	int time_last_vdteleport = (Integer) plugin.getPlayerConfig(p, "time_last_vdteleport", "int");
	    	    if((plugin.timetamps-time_last_vdteleport) > 20) {
	    	    	msg.sendTaggedMessage(p, "Les visiteurs ne peuvent pas prendre ce t�l�porteur.", 1, "[DENIED]");
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
    			msg.sendTaggedMessage(p, "D�sol�, le mode SPY interdit l'utilisation du chat.", 1, "[DENIED]");
	    		event.setCancelled(true);
	    		return;
    		}
    		if(p.isOp()) {
	    		msg.chatMessageToAll(p, event.getMessage());
	    		event.setCancelled(true);
	    		return;
    		}else if (plugin.Main_Visiteur.is_visiteur(p)) {
				if(plugin.Main_Visiteur.visiteur_number() > 1) {
					msg.chatMessageToAllVisiteur(p, event.getMessage());
					event.setCancelled(true);
					return;
				}else{
					msg.sendTaggedMessage(p, "Impossible, il n'y a que vous en visiteur sur le serveur.", 1, "[DENIED]");
					event.setCancelled(true);
					return;
				}
	    	}else{
	    		msg.chatMessageToAllNonVisiteur(p, event.getMessage());
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
        			msg.sendTaggedMessage(player, "T�l�portation vers la zone de spawn dans 10 secondes.", 1, "[SPAWN]");
        	    	respawn_player_deleyed(player);
        	    	sendTeleportEffect(player, false);
        			plugin.setPlayerConfig(player, "time_lastspawn", plugin.timetamps);
        		}else{
        			ctc.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
        			msg.sendTaggedMessage(player, "Erreur avec le point de spawn, t�l�portation impossible.", 1, "[SPAWN]");
        		}
        	}else{
        		ctc.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
        		msg.sendTaggedMessage(player, "Merci d'attendre "+ ChatColor.BLUE +"360 secondes"+ ChatColor.WHITE +" entre chaque requ�te de spawn.", 1, "[SPAWN]");
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
    	if(!event.getPlayer().isOp() && !worldname.equals("world")) {
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/cancel")) {
    		shop.cancel(player);
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/buy")) {
    		shop.buy(player);
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/md")) {
    		if(!player.isOp()) {
    			player.kickPlayer("Erreur securit� 5003.");
	    		event.setCancelled(true);
	    		return;
    		}
    	}
    	if (event.getMessage().equals("/aro")) {
    		if(plugin.Main_ContribControl.isClient(player, true)) {
    			int time_customaction = (Integer) plugin.getPlayerConfig(player, "time_customaction", "int");
            	if((plugin.timetamps-time_customaction) > 10) {
            		plugin.Main_ContribControl.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/boomer_dive_01.wav");
            		plugin.setPlayerConfig(player, "time_lastspawn", plugin.timetamps);
            	}else{
            		msg.sendTaggedMessage(player, "Merci d'attendre 10 secondes entre chaque action personnalis�.", 1, "");
            	}
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/ano")) {
    		if(plugin.Main_ContribControl.isClient(player, true)) {
    			int time_customaction = (Integer) plugin.getPlayerConfig(player, "time_customaction", "int");
            	if((plugin.timetamps-time_customaction) > 10) {
            		plugin.Main_ContribControl.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/No"+showRandomInteger(1, 12, rand)+".wav");
            		plugin.setPlayerConfig(player, "time_lastspawn", plugin.timetamps);
            	}else{
            		msg.sendTaggedMessage(player, "Merci d'attendre 10 secondes entre chaque action personnalis�.", 1, "");
            	}
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/arire")) {
    		if(plugin.Main_ContribControl.isClient(player, true)) {
    			int time_customaction = (Integer) plugin.getPlayerConfig(player, "time_customaction", "int");
            	if((plugin.timetamps-time_customaction) > 10) {
            		plugin.Main_ContribControl.sendSoundEffectToAllToLocation(player.getLocation(), "http://mineworld.fr/contrib/sound/Laughter"+showRandomInteger(1, 6, rand)+".wav");
            		plugin.setPlayerConfig(player, "time_lastspawn", plugin.timetamps);
            	}else{
            		msg.sendTaggedMessage(player, "Merci d'attendre 10 secondes entre chaque action personnalis�.", 1, "");
            	}
    		}
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (event.getMessage().equals("/list")) {
       		if(plugin.Main_ContribControl.isClient(player, true)) {
       			String list = "";
       			Integer visiteur = 0;
       			for (Player p : plugin.getServer().getOnlinePlayers()) {
       				if(p.getName().contains("MWSTAFF")) {
       					continue;
       				}
       				if(p.isOp()) {
       					list = list + ChatColor.RED + p.getName() + ChatColor.WHITE +", ";
       				}else if(plugin.ismodo(p)) {
       					list = list + ChatColor.GREEN + p.getName() + ChatColor.WHITE +", ";
       				}else if(plugin.Main_Visiteur.is_visiteur(p)) {
       					visiteur++;
       				}else{
       					list = list + p.getName() + ", ";
       				}
       			}
   				list = list + "et "+visiteur+" visiteur(s).";
       			msg.sendTaggedMessage(player, "Joueur en ligne :", 1, "[LIST]");
       			msg.sendTaggedMessage(player, list, 1, "[LIST]");
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/sun")) {
    		if(player.isOp()) {
	    		String url = event.getMessage().replace("/sun ", "");
	    		plugin.Main_ContribControl.setSunURLtoAll(url);
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/skin")) {
    		if(player.isOp()) {
	    		String url = event.getMessage().replace("/skin ", "");
	    		plugin.Main_ContribControl.setPlayerSkin(player, url);
    		}
    		event.setCancelled(true);
    		return;
    	}
    	
    	if (event.getMessage().contains("/music")) {
    		if(player.isOp()) {
	    		String url = event.getMessage().replace("/music ", "");
	    		plugin.Main_ContribControl.sendSoundEffectToAllToLocation(player.getLocation(), url);
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/allmusic")) {
    		if(player.isOp()) {
	    		String url = event.getMessage().replace("/allmusic ", "");
	    		plugin.Main_ContribControl.sendSoundToAll(url);
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().equals("/stopmusic")) {
    		if(plugin.Main_ContribControl.isClient(player, true)) {
    			ContribPlayer contribplayer = ContribCraftPlayer.getContribPlayer(player);
    			BukkitContrib.getSoundManager().stopMusic(contribplayer);
    		}
    		event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/sethome")) {
    		msg.sendTaggedMessage(player, "Vous devez cliquer sur un lit avec une boussole (dans votre inventaire) pour d�finir votre point de home.", 1, "");
			event.setCancelled(true);
    		return;
    	}
    	if (event.getMessage().contains("/home")) {
    		if(plugin.Main_ContribControl.isClient(player, true)) {
	    		int lasthome = (Integer) plugin.getPlayerConfig(player, "time_lasthome", "int");
		       	if((plugin.timetamps-lasthome) > 60) {
		       		start_teleportation_tohome(player, true);
					msg.sendTaggedMessage(player, "Vous devez cliquer sur un lit avec une boussole (dans votre inventaire) pour d�finir votre point de home.", 1, "[INFO]");
		       		plugin.setPlayerConfig(player, "time_lasthome", plugin.timetamps);
		       	}else{
		       		plugin.Main_ContribControl.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beeperror.wav");
		       		msg.sendTaggedMessage(player, "Merci d'attendre 1 minutes entre chaque requ�te.", 1, "");
		       	}
    		}else{
    			int lasthome = (Integer) plugin.getPlayerConfig(player, "time_lasthome", "int");
		       	if((plugin.timetamps-lasthome) > 180) {
		       		start_teleportation_tohome(player, false);
					msg.sendTaggedMessage(player, "Vous devez cliquer sur un lit avec une boussole (dans votre inventaire) pour d�finir votre point de home.", 1, "[INFO]");
		       		plugin.setPlayerConfig(player, "time_lasthome", plugin.timetamps);
		       	}else{
		       		msg.sendTaggedMessage(player, "Merci d'attendre 3 minute entre chaque requ�te.", 1, "");
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
    	if (event.getMessage().contains("/setspawn")) {
    		if(player.isOp()) {
    			player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
    			player.sendMessage("Spawn set.");
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
	    if((plugin.timetamps-sneaking_lasttime) > 120) {
	    	msg.sendTaggedMessage(player, "Le mode sneaking vous cache des monstres qui sont loin de votre position.", 1, "[INFO-MINEWORLD]");
	    	plugin.setPlayerConfig(player, "time_sneaking_chunk", plugin.timetamps);
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
    
    public void afterjoin(Player player, Boolean back) {
		if(plugin.ismodo(player)) {
			ctc.setPlayerTitle(player, ChatColor.GREEN.toString() + "[MODO]\n"+ player.getName());
		}else if(player.isOp()) {
			ctc.setPlayerTitle(player, ChatColor.RED.toString() + "[ADMIN]\n"+ player.getName());
			ctc.setPlayerCape(player, "http://mineworld.fr/contrib/cape/admin.png");
		}
		if(tc.dead_sun) {
			ctc.setPlayerSunURL(player, "http://mineworld.fr/contrib/sun/deadsunsun.png");
		}else{
			ctc.setPlayerSunURL(player, "http://mineworld.fr/contrib/sun/sun.png");
		}
		ctc.setPlayerMoonURL(player, "http://mineworld.fr/contrib/sun/moon.png");
		if(!back) {
	   	 	ctc.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/gnomeftw.wav");
	   	 	ctc.sendNotification(player, "Notification", "Bienvenue sur MineWorld!");
		}
		plugin.Main_ContribControl.set_fog(RenderDistance.NORMAL);
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
    	final Player player = event.getPlayer();
    	boolean remove_me = (Boolean) plugin.getPlayerConfig(player, "remove_me", "boolean");
    	if(remove_me == true) {
    		player.kickPlayer("Votre compte est bloqu�.");
    		return;
    	}
    	
    	String version = (String) plugin.getServerConfig("informations.version", "string");
   	 	String[] anTxt = msg.createstrings(2);
   	 	anTxt[0] = "Bienvenue sur MineWorld 2.0, le serveur semi-roleplay post-apocalypse.";
   	 	anTxt[1] = "Version : MineWorld DEV "+ ChatColor.RED + "V" + version + ChatColor.WHITE +" / Minecraft "+ ChatColor.GOLD + "V1.7.3";
   	 	msg.sendTaggedMessage(player, anTxt, 2, "");
   	 	
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
   	 	
   	 	Boolean back = false;
		if(plugin.Main_Visiteur.whitelist.contains(player.getName().toLowerCase())) {
			if(player.getName().contains("MWSTAFF")) {
				event.setJoinMessage("Un visiteur a rejoint le serveur.");
			}else{
				msg.sendTaggedMessage(player, "Votre compte est bien dans notre WhiteList.", 1, "[WHITELIST]");
				if(lastdeconnexion == 0 || (lastdeconnexion+360) < plugin.timetamps) {
					if(player.isOp()) {
						event.setJoinMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " a rejoint le serveur.");
					}else{
						event.setJoinMessage(ChatColor.GOLD + player.getName() + " a rejoint le serveur.");
					}
				}else{
					back = true;
					event.setJoinMessage(ChatColor.DARK_GRAY + player.getName() + " est de retour ("+ timetxt +").");
				}
			}
		}else{
			plugin.Main_Visiteur.add_visiteur(player);
			msg.sendTaggedMessage(player, "Vous avez un compte visiteur.", 1, "[WHITELIST]");
			event.setJoinMessage(ChatColor.DARK_GRAY + "Un visiteur a rejoint le serveur.");
			player.setDisplayName("visiteur");
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
		if(event.getPlayer().getName().contains("MWSTAFF")) {
			event.setQuitMessage("Un visiteur a rejoint le serveur.");
		}else{
	    	if (plugin.Main_Visiteur.is_visiteur(event.getPlayer())) {
	    		event.setQuitMessage(ChatColor.DARK_GRAY + "Un visiteur a quitt� le serveur.");
	    		plugin.Main_Visiteur.remove_visiteur(event.getPlayer());
	    	}else{
	    		event.setQuitMessage(ChatColor.DARK_GRAY + event.getPlayer().getName() + " a quitt� le serveur.");
	    	}
		}
    	if(cc.player_lastchunk.containsKey(event.getPlayer())) {
    		cc.player_lastchunk.remove(event.getPlayer());
    	}
    	if(cc.PlayerOR.contains(event.getPlayer())) {
    		cc.PlayerOR.remove(event.getPlayer());
    	}
		if(cc.error_tick.containsKey(event.getPlayer())) {
			cc.error_tick.remove(event.getPlayer());
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
		        			plugin.Main_ChunkControl.ResendAll.remove(p);
		        			plugin.Main_ChunkControl.player_chunkupdate.remove(p);
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
	    	    	msg.sendTaggedMessage(player, "Les visiteurs ne peuvent pas prendre ce t�l�porteur.", 1, "[DENIED]");
	    	    	plugin.setPlayerConfig(player, "time_last_vdteleport", plugin.timetamps);
	    	    }
	    	    respawn_player(player);
	    		event.setCancelled(true);
	    		return;
    		}
    	}
    	
		plugin.Main_ChunkControl.ResendAll.remove(player);
		plugin.Main_ChunkControl.player_chunkupdate.remove(player);
    	
    	// ANCIEN MONDES
    	if(event.getTo().getWorld() != null) {
	    	String lastworld = (String) plugin.getPlayerConfig(player, "last_world", "string");
			plugin.setPlayerConfig(player, "last_world", event.getTo().getWorld().getName());
			if(lastworld != null && !lastworld.isEmpty()) {
		    	if(!lastworld.contains(event.getTo().getWorld().getName())) {
		    		if(event.getTo().getWorld().getName().contains("oldworld")) {
		    			player.sendMessage("Terre, premier monde MineWorld 1.0.");
		    			plugin.Main_ContribControl.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/scare_wind_reverse.ogg");
		    			mvspawn(player);
		    		}
		    		if(event.getTo().getWorld().getName().contains("oldaerelon")) {
		    			player.sendMessage("Aerelon, deuxi�me monde MineWorld 1.0.");
		    			plugin.Main_ContribControl.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/scare_wind_reverse.ogg");
		    			mvspawn(player);
		    		}
		    		if(event.getTo().getWorld().getName().contains("olddeathworld")) {
		       			player.sendMessage("DeathWorld, troisi�me monde MineWorld 1.0.");
		       			plugin.Main_ContribControl.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/scare_wind_reverse.ogg");
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