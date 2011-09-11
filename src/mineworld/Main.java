package mineworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

import net.citizensnpcs.api.CitizensManager;
import net.citizensnpcs.resources.npclib.HumanNPC;
import net.citizensnpcs.resources.npclib.NPCList;
import net.minecraft.server.Packet29DestroyEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.*;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.SpoutManager;

public class Main extends JavaPlugin {

	PluginManager pm;
    final Logger logger = Logger.getLogger("Minecraft");
    PluginDescriptionFile pdfFile;
    
    final Random rand = new Random();
    
    public Main_EntityListener Main_EntityListener;
    public Main_PlayerListener Main_PlayerListener;
    public Main_MoveControl Main_MoveControl;
    public Main_BlockListener Main_BlockListener;
    public Main_Visiteur Main_Visiteur;
    public Main_CommandsControl Main_CommandsControl;
    public Main_MessageControl Main_MessageControl;
    public Main_ShopSystem Main_ShopSystem;
    public Main_ContribControl Main_ContribControl;
    public Main_TimeControl Main_TimeControl;
    public Main_ChunkControl Main_ChunkControl;
    public Main_ChunkListener Main_ChunkListener;
    
    public List<String> modo = new ArrayList<String>();
    public List<String> correct = new ArrayList<String>();
    public List<String> anim = new ArrayList<String>();
    public List<Player> world_whitelist = new ArrayList<Player>();
    public List<Player> block_player = new ArrayList<Player>();
    public List<Player> alco_player = new ArrayList<Player>();
    public List<Player> can_horde = new ArrayList<Player>();
    public Map<Player, Integer> last_move = new HashMap<Player, Integer>();
    public List<Player> spy_player = new ArrayList<Player>();
    public List<Player> in_dark = new ArrayList<Player>();
    public List<Player> iv_chat = new ArrayList<Player>();
    public List<Player> iv_do = new ArrayList<Player>();
    
    public Map<Entity, Block> move_last = new HashMap<Entity, Block>();
    
    public Map<Player, String> last_region = new HashMap<Player, String>();

	protected static File maindir = new File("plugins" + File.separatorChar + "MineWorld");
	protected File NPC_configFile = new File(maindir, "npc_config.yml");
	protected File Player_configFile = new File(maindir, "player_config.yml");
	protected File Server_configFile = new File(maindir, "server_config.yml");
	protected File ITEM_configFile = new File(maindir, "item_config.yml");
	
	Configuration conf_player = new Configuration(Player_configFile);
	Configuration conf_npc = new Configuration(NPC_configFile);
	Configuration conf_server = new Configuration(Server_configFile);
	Configuration conf_items = new Configuration(ITEM_configFile);
	
	public Boolean is_first_loaded = false;
	private Boolean debug_enable = false;
	public Boolean contrib = true;
	
	public int cron_tick_stats = 0;
	public int cron_tick = 0;
	public int cron_tick_gen = 0;
	public int number_creature = 0;
	public int cron_tick_heal = 0;
	public int cron_tick_extra = 0;

	public long timetamps = 0;
	
	public int lastreload = 0;
	public int lasttimereload = 0;
	public int lastplayerleft = 0;
    public int is_core_tick = 0;
    public int refreshchunkt = 0;
    
    Thread t_01, t_02, t_03, t_04, t_05;
	
    public static Entity chicken;
    public static Entity zombie;
    public static Entity slime;
    public static Entity spider;
    public static int mobcycle = 0;
    
    public Boolean maintenance_status = false;
    public String maintenance_message = "Notre serveur est actuellement en maintenance.";
    
    public void onEnable() {
    	PluginManager pm = getServer().getPluginManager();
    	PluginDescriptionFile pdfFile = getDescription();

        Main_ContribControl = new Main_ContribControl(this);
        Main_TimeControl = new Main_TimeControl(this);
        Main_ChunkControl = new Main_ChunkControl(this);
        Main_ShopSystem = new Main_ShopSystem(this);
        Main_CommandsControl = new Main_CommandsControl(this);
        Main_MessageControl = new Main_MessageControl(this);
        Main_EntityListener = new Main_EntityListener(this);
        Main_PlayerListener = new Main_PlayerListener(this);
        Main_MoveControl = new Main_MoveControl(this);
        Main_BlockListener = new Main_BlockListener(this);
        Main_Visiteur = new Main_Visiteur(this);
        Main_ChunkListener = new Main_ChunkListener(this);
        
        pm.registerEvent(Type.PLAYER_JOIN, Main_PlayerListener, Priority.Normal, this); 
        pm.registerEvent(Type.PLAYER_QUIT, Main_PlayerListener, Priority.Normal, this); 
        pm.registerEvent(Type.PLAYER_INTERACT, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_PRELOGIN, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_CHAT, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_TOGGLE_SNEAK, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_PICKUP_ITEM, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_MOVE, Main_PlayerListener, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_RESPAWN, Main_PlayerListener, Priority.Normal, this);
        
        pm.registerEvent(Type.ENTITY_DAMAGE, Main_EntityListener, Priority.Normal, this);
        pm.registerEvent(Type.ENTITY_DEATH, Main_EntityListener, Priority.Normal, this);
        pm.registerEvent(Type.ENTITY_TARGET, Main_EntityListener, Priority.Normal, this);
        
        pm.registerEvent(Type.BLOCK_IGNITE, Main_BlockListener, Priority.Normal, this);
        pm.registerEvent(Type.BLOCK_BREAK, Main_BlockListener, Priority.Normal, this); 
        pm.registerEvent(Type.BLOCK_DAMAGE, Main_BlockListener, Priority.Normal, this); 
        pm.registerEvent(Type.BLOCK_PLACE, Main_BlockListener, Priority.Normal, this);
        
        pm.registerEvent(Type.CHUNK_UNLOAD, Main_ChunkListener, Priority.Normal, this);
        
    	runAllThread();

    	try {
			Main_Visiteur.charge_whitelist();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
        logger.log(Level.INFO, pdfFile.getName() + " version " + pdfFile.getVersion() + " enabled.");
    }
    
    public void freeze(final Player player, Long time) {
		if(!block_player.contains(player)) {
			block_player.add(player);
		}
    	player.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run()
			{
				if(block_player.contains(player)) {
					block_player.remove(player);
				}
			}
    	}, (long) time);
    }
    
	public Runnable runThread_01() {
		if(this.t_01 == null) {
			this.t_01 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		do_cron_01();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			this.t_01.setPriority(Thread.MIN_PRIORITY);
			this.t_01.setDaemon(true);
		}
		return this.t_01;
	}
	
	public Runnable runThread_02() {
		if(this.t_02 == null) {
			this.t_02 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		do_cron_02();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			this.t_02.setPriority(Thread.MIN_PRIORITY);
			this.t_02.setDaemon(true);
		}
		return this.t_02;
	}
	
	public Runnable runThread_03() {
		if(this.t_03 == null) {
			this.t_03 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		do_cron_03();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			this.t_03.setPriority(Thread.MIN_PRIORITY);
			this.t_03.setDaemon(true);
		}
		return this.t_03;
	}
	
	public Runnable runThread_04() {
		if(this.t_04 == null) {
			this.t_04 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		do_cron_04();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			this.t_04.setPriority(Thread.MIN_PRIORITY);
			this.t_04.setDaemon(true);
		}
		return this.t_04;
	}
	
	public Runnable runThread_05() {
		if(this.t_05 == null) {
			this.t_05 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		do_cron_05();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
			this.t_05.setPriority(Thread.MIN_PRIORITY);
			this.t_05.setDaemon(true);
		}
		return this.t_05;
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
	
	public long timetamps() {
		timetamps = System.currentTimeMillis() / 1000L;
		return timetamps;
	}
    
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	    try {
	        if (!command.getName().toLowerCase().equals("mn") && !command.getName().toLowerCase().equals("mineworld") && !command.getName().toLowerCase().equals("modo")) {
	            return false;
	        }
	        Main_CommandsControl.Main_onCommand_do(sender, command, commandLabel, args);
	        return true;
	    } catch (Exception e) {
	        sender.sendMessage("[MINEWORLD] Une erreur est survenue.");
	        sendError(e.getMessage() + e.getStackTrace().toString());
	        e.printStackTrace();
	        return true;
	    }
    }
    
    public void onDisable() {
        try {
            logger.log(Level.INFO, "MineWorld disabled.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "MineWorld : error: " + e.getMessage() + e.getStackTrace().toString());
            e.printStackTrace();
            return;
        }
    }
    
    public Boolean playerInServer() {
    	if(getServer().getOnlinePlayers().length > 0) {
    		return true;
    	}
    	return false;
    }
    
    public void removeallitems() {
		for (World w : getServer().getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (e instanceof Creature) {
					((Creature) e).remove();
				}
				if (e instanceof Arrow || e instanceof Item) {
					e.remove();
				}
			}
		}
    }
    
    public boolean ismodo(Player p) {
    	if(modo.contains(p.getName())) {
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public boolean isbot(Player p) {
        HumanNPC npc = CitizensManager.get(p);
        if (npc != null) {
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public void do_cron_01() {
    	if(playerInServer()) {
			if(cron_tick_heal > 15) {
				Boolean isgood = true;
				Boolean onlyvisitor = true;
				cron_tick_heal = 0;
				for (Player p : getServer().getOnlinePlayers()) {
			    	if (Main_Visiteur.is_visiteur(p) || !p.getWorld().getName().contains("world")) {
			    		continue;
			    	}
			    	onlyvisitor = false;
					if(p.isSleeping()) {
						if(p.getHealth() < 10) {
							p.setHealth(p.getHealth()+1);
						}
					}else{
						isgood = false;
					}
				}
				if(isgood && !onlyvisitor) {
					Main_MessageControl.sendTaggedMessageToAll("Après une longue nuit, voici le lever du jour...", 1, "[SUN]");
					getServer().getWorld("world").setFullTime(23100);
				}
			}else{
				cron_tick_heal++;
			}
			lastplayerleft = 0;
    	}else{
			if(lastplayerleft > 500) {
				lastplayerleft = 0;
				removeallitems();
			}else{
				lastplayerleft++;
			}
    	}
    }
    
    public void do_cron_02() {
    	if(playerInServer()) {
    		if(cron_tick_extra > 1) {
    			cron_tick_extra = 0;
    			for (Player p : getServer().getOnlinePlayers()) {
    				SpoutManager.getPlayer(p).setWalkingMultiplier(1);
    				if(!Main_TimeControl.horde) {
			    		if(alco_player.contains(p) && Main_PlayerListener.is_alco(p)) {
			    			Main_ContribControl.setPlayerTitle(p, ChatColor.DARK_AQUA.toString() + p.getName());
			    			Main_ContribControl.sp.getPlayer(p).setGravityMultiplier(0.3);
			    			Main_ContribControl.sp.getPlayer(p).setJumpingMultiplier(1.1);
			    		}else if(alco_player.contains(p) && !Main_PlayerListener.is_alco(p)) {
			    			Main_ContribControl.setPlayerTitle(p, p.getName());
			    			Main_ContribControl.sp.getPlayer(p).setGravityMultiplier(1);
			    			Main_ContribControl.sp.getPlayer(p).setJumpingMultiplier(1);
			    			alco_player.remove(p);
			    		}
    				}
    			}
    			if(!Main_TimeControl.horde) {
    	    		NPCList npclist = CitizensManager.getList();
    	    		int i = 0;
    	    		for(final Entry<Integer, HumanNPC> npc : npclist.entrySet()){
    	    			i++;
    	    			if(npc != null) {
    		            		getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
    							public void run()
    							{
    								if(zombie == null) {
	    								if(npc.getValue().getName() == "zombie") {
	    									zombie = npc.getValue().getPlayer();
	    								}else if(npc.getValue().getName() == "chicken") {
	    									chicken = npc.getValue().getPlayer();
	    								}else if(npc.getValue().getName() == "spider") {
	    									spider = npc.getValue().getPlayer();
	    								}else if(npc.getValue().getName() == "slime") {
	    									slime = npc.getValue().getPlayer();
	    								}
    								}
    								Main_ContribControl.setPlayerSkin(npc.getValue().getPlayer(), "http://mineworld.fr/contrib/skin/"+npc.getValue().getName().toLowerCase()+".png");
    							}
    		            	}, (long) 30);
    	    			}
    	    		}
        		}
    		}else{
    			cron_tick_extra++;
    		}
	    	if(cron_tick_gen > 15) {
				cron_tick_gen = 0;
				number_creature = 0;
		        if(Main_TimeControl.meteo_monde_type == 1) {
		        	Main_ContribControl.bool_clouds(false);
		    	}else if(Main_TimeControl.meteo_monde_type == 2) {
		    		Main_ContribControl.bool_clouds(true);
		    	}
		        SpoutManager.getAppearanceManager().resetAllTitles();
		        SpoutManager.getAppearanceManager().resetAllCloaks();
		        if(!Main_TimeControl.horde) {
			        for (Player p : getServer().getOnlinePlayers()) {
			    		if(ismodo(p)) {
			    			Main_ContribControl.setPlayerTitle(p, ChatColor.GREEN.toString() + "[MODO]\n"+ p.getName());
			    		}else if(p.isOp()) {
			    			Main_ContribControl.setPlayerTitle(p, ChatColor.RED.toString() + "[ADMIN]\n"+ p.getName());
			    			Main_ContribControl.setPlayerCape(p, "http://mineworld.fr/contrib/cape/admin.png");
			    		}
			        }
		        }
				for (World w : getServer().getWorlds()) {
					for (Entity e : w.getEntities()) {
						if (e instanceof Creature) {
							number_creature++;
						}
					}
				}
				Integer news_rev = (Integer) getServerConfig("informations.news_rev", "int");
				for (Player p : getServer().getOnlinePlayers()) {
			    	if (Main_Visiteur.is_visiteur(p)) {
			    		Main_MessageControl.sendVisiteurMsg(p);
			    		continue;
			    	}
				    boolean first_connexion = (Boolean) getPlayerConfig(p, "first_connexion", "boolean");
				    int last_news_rev = (Integer) getPlayerConfig(p, "last_news_rev", "int");
				    if(!first_connexion) {
				    	Main_MessageControl.sendSmallNotRegisteredMsg(p);
					}else if(last_news_rev < news_rev) {
						Main_MessageControl.sendSmallLastNews(p);
					}
				}
				setServerConfig("informations.number_mobs", number_creature);
			}else{
				cron_tick_gen++;
			}
			if(cron_tick_stats > 220) {
				cron_tick_stats = 0;
				for (Player p : getServer().getOnlinePlayers()) {
			    	if (Main_Visiteur.is_visiteur(p) || !Main_ContribControl.isClient(p, false)) {
			    		continue;
			    	}
					if(Main_ChunkControl.error_tick.containsKey(p)) {
						Main_ChunkControl.error_tick.remove(p);
					}
					conf_player.load();
					int ppresences = conf_player.getInt("load-player."+ p.getName() +".ppresences", 0);
					Main_MessageControl.sendTaggedMessage(p, "Vous avez reçut "+ ChatColor.DARK_GREEN + "1 point"+ ChatColor.WHITE + " de présence.", 1, "");
					Main_ContribControl.sendNotification(p, "MONEY Transaction", "+1 PPoint(s)");
					Main_ContribControl.sendPlayerSoundEffect(p, "http://mineworld.fr/contrib/sound/money.wav");
					ppresences++;
					setPlayerConfig(p, "ppresences", ppresences);
				}
			}else{
				cron_tick_stats++;
			}
    	}
    }
    
    public void do_cron_03() {
    	if(playerInServer()) {
			World world = getServer().getWorld("world");
    		if(!isDay(world) || Main_TimeControl.horde) {
	    		for (Entity e : world.getEntities()) {
	    			if (e instanceof Monster) {
	    				if (e instanceof Zombie) {
	    					Integer random = showRandomInteger(1, 40, rand);
	    					if(Main_TimeControl.horde) {
	    						random = showRandomInteger(1, 10, rand);
	    					}
		    				if(random == 5) {
		    					for (Entity entity : e.getNearbyEntities(24, 24, 24)) {
		    		    			if (entity instanceof Player && !isbot((Player) entity)) {
		    		    				Main_ContribControl.sendPlayerSoundEffectToLocation((Player) entity, e.getLocation(), "http://mineworld.fr/contrib/sound/breathing"+showRandomInteger(1, 9, rand)+".wav");
		    		    			}
		    					}
		    		    	}else if(random == 10) {
			    					for (Entity entity : e.getNearbyEntities(24, 24, 24)) {
			    		    			if (entity instanceof Player && !isbot((Player) entity)) {
			    		    				Main_ContribControl.sendPlayerSoundEffectToLocation((Player) entity, e.getLocation(), "http://mineworld.fr/contrib/sound/shout0"+showRandomInteger(1, 8, rand)+".wav");
			    		    			}
			    					}
		    		    	}else if(random == 15) {
		    					for (Entity entity : e.getNearbyEntities(24, 24, 24)) {
		    						double distance = getdistance(entity, e);
		    		    			if (entity instanceof Player && distance > 8 && !isbot((Player) entity)) {
		    		    				Main_ContribControl.sendPlayerSoundEffectToLocation((Player) entity, e.getLocation(), "http://mineworld.fr/contrib/sound/hunter_attackmix_0"+showRandomInteger(1, 3, rand)+".wav");
		    		    			}
		    					}
		    		    	}
	    				}else if(e instanceof Creeper){
	    					Integer random = showRandomInteger(1, 40, rand);
	    					if(Main_TimeControl.horde) {
	    						random = showRandomInteger(1, 10, rand);
	    					}
		    				if(random == 5 && ((Creature) e).getTarget() == null) {
		    					for (Entity entity : e.getNearbyEntities(50, 50, 50)) {
		    						double distance = getdistance(entity, e);
		    		    			if (entity instanceof Player && distance > 8 && !isbot((Player) entity)) {
		    		    				Main_ContribControl.sendPlayerSoundEffectToLocation((Player) entity, e.getLocation(), "http://mineworld.fr/contrib/sound/female_cry_"+showRandomInteger(1, 4, rand)+".wav");
		    		    			}
		    					}
		    		    	}else if(random == 10 && ((Creature) e).getTarget() == null) {
		    					for (Entity entity : e.getNearbyEntities(50, 50, 50)) {
		    						double distance = getdistance(entity, e);
		    		    			if (entity instanceof Player && distance > 8 && !isbot((Player) entity)) {
		    		    				Main_ContribControl.sendPlayerSoundEffectToLocation((Player) entity, e.getLocation(), "http://mineworld.fr/contrib/sound/walking_cry_"+showRandomInteger(1, 4, rand)+".wav");
		    		    			}
		    					}
		    		    	}
	    				}else if(e instanceof Skeleton){
	    					Integer random = showRandomInteger(1, 40, rand);
	    					if(Main_TimeControl.horde) {
	    						random = showRandomInteger(1, 10, rand);
	    					}
		    				if(random == 5 && ((Creature) e).getTarget() == null) {
		    					for (Entity entity : e.getNearbyEntities(50, 50, 50)) {
		    						double distance = getdistance(entity, e);
		    		    			if (entity instanceof Player && distance > 8 && !isbot((Player) entity)) {
		    		    				Main_ContribControl.sendPlayerSoundEffectToLocation((Player) entity, e.getLocation(), "http://mineworld.fr/contrib/sound/spitter_lurk_"+showRandomInteger(1, 20, rand)+".wav");
		    		    			}
		    					}
		    		    	}
	    				}
						if((((Creature) e).getTarget() == null && !Main_TimeControl.horde) || Main_TimeControl.horde) {
							double lastdistance = 1000000.0;
							Entity thetarget = null;
							for (Entity ee : e.getNearbyEntities(50, 50, 50)) {
								if (ee instanceof Player) {
									if(!Main_Visiteur.is_visiteur((Player) ee)) {
										if(!((Player) ee).isOp() && !ee.isDead() && ((Player) ee).isOnline()) {
											double distance = getdistance(ee, e);
											if (distance < lastdistance) {
												thetarget = ee;
												lastdistance = distance;
											}
										}
									}
								}
							}
							if(thetarget != null) {
								double range = 10.0;
								if(Main_TimeControl.horde) {
									range = 1.3;
								}else if(thetarget.getLocation().getBlock().getLightLevel() < 8) {
									range = 5.0;
								}
								if (checkLocation(thetarget.getLocation(), e.getLocation(), range)) {
									((Creature) e).setTarget((LivingEntity) thetarget);
								}else{
									Main_MoveControl.moveCloserToLocation(e, thetarget.getLocation());
								}
							}
						}
		    		}
	    		}
    		}
    	}
    }
	
    public void do_cron_04() { 
		if(!world_whitelist.isEmpty()) {
			for (Player player : world_whitelist) {
				if(!player.isOnline()) {
					world_whitelist.remove(player);
				}
			}
		}
		
		if(!Main_ChunkControl.PlayerOR.isEmpty()) {
			for (Player player : Main_ChunkControl.PlayerOR) {
				if(!player.isOnline()) {
					Main_ChunkControl.PlayerOR.remove(player);
				}
			}
		}
		
		if(!Main_Visiteur.visiteur.isEmpty()) {
			for (Player player : Main_Visiteur.visiteur) {
				if(!player.isOnline()) {
					Main_Visiteur.visiteur.remove(player);
				}
			}
		}	
    }
    
    public Boolean is_spy(Player player) {
    	if(spy_player.contains(player)) {
    		return true;
    	}
    	return false;
    }
    
    public void do_cron_05() { 
    	timetamps();
    	if(!spy_player.isEmpty()) {
	    	for (Player player : spy_player) {
	    		for (Entity entity : player.getNearbyEntities(24, 24, 24)) {
	    			if (entity instanceof Player) {
						if(!((Player) entity).isOp()) {
							CraftPlayer unHide = (CraftPlayer) entity;
							CraftPlayer unHideFrom = (CraftPlayer) player;
							unHide.getHandle().netServerHandler.sendPacket(new Packet29DestroyEntity(unHideFrom.getEntityId()));
						}
					}
	    		}
	    	}
	    }
    }
    
    private void runAllThread() {
    	// Main
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, runThread_01(), 1, 10); 
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, runThread_02(), 1, 100); 
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, runThread_03(), 1, 20); 
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, runThread_04(), 1, 5000);
    	
    	// Main - UltraDO
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, runThread_05(), 1, 1);
		
    	// Visiteur
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, Main_Visiteur.runThread_1(this), 1, 25); 
    	
    	// TIMECONTROL
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, Main_TimeControl.runThread_meteo(this), 1, 10);
    	
    	// CHUNKCONTROL | ANTI-INVISIBLE
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, Main_ChunkControl.runThread_1(this), 1, 25);
    }
    
	public Object getNPCConfig(String npcname, String name, String type) {
    	conf_npc.load();
    	String config = "load-player."+ npcname +"."+ name;
      	if(type == "int") {
    		return conf_npc.getInt(config, 0);
    	}else if(type == "string") {
    		return conf_npc.getString(config);
    	}else if(type == "boolean") {
    		return conf_npc.getBoolean(config, false);
    	}
    	return false;
    }
	
	public boolean isDay(World world) {
		return world.getTime() < 12000 || world.getTime() == 24000;
	}
    
    public Object getPlayerConfig(Player player, String name, String type) {
    	conf_player.load();
    	String config = "load-player."+ player.getName() +"."+ name;
    	if(type == "int" || type == "float") {
    		return conf_player.getInt(config, 0);
    	}else if(type == "string") {
    		return conf_player.getString(config);
    	}else if(type == "boolean") {
    		return conf_player.getBoolean(config, false);
    	}
    	return false;
    }
    
    public Object getServerConfig(String name, String type) {
    	conf_server.load();
    	if(type == "int" || type == "integer") {
    		return conf_server.getInt(name, 0);
    	}else if(type == "string") {
    		return conf_server.getString(name);
    	}else if(type == "boolean") {
    		return conf_server.getBoolean(name, false);
    	}
    	return false;
    }
    
    public void setPlayerConfig(Player player, String name, Object value) {
    	conf_player.load();
    	String config = "load-player."+ player.getName() +"."+ name;
    	conf_player.setProperty(config, value);
    	conf_player.save();
    }
    
    public void setServerConfig(String name, Object value) {
    	conf_server.load();
    	conf_server.setProperty(name, value);
    	conf_server.save();
    }
    
    public void sendError(String msg) {
    	logger.log(Level.WARNING, "MineWorld error: " + msg);
    }
    
    public void sendInfo(String msg) {
    	logger.log(Level.INFO, "MineWorld info: " + msg);
    }
    
    public void sendDebug(String msg) {
    	if(debug_enable) {
    		logger.log(Level.INFO, "MineWorld debug: " + msg);
    	}
    }
    
    public boolean checkLocation(Location loc, Location ploc, Double range) {
    	if ((ploc.getX() <= loc.getX() + range && ploc.getX() >= loc.getX()
    	- range)
    	&& (ploc.getY() >= loc.getY() - range && ploc.getY() <= loc
    	.getY() + range)
    	&& (ploc.getZ() >= loc.getZ() - range && ploc.getZ() <= loc
    	.getZ() + range))
    	return true;
    	else
    	return false;
    }
    
    public double getdistance(Entity ent1, Location loc) {
		double deltax = Math.abs(ent1.getLocation()
				.getX() - loc.getX());
		double deltay = Math.abs(ent1.getLocation()
				.getY() - loc.getY());
		double deltaz = Math.abs(ent1.getLocation()
				.getZ() - loc.getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
    public double getdistance(Location loc2, Location loc) {
		double deltax = Math.abs(loc2
				.getX() - loc.getX());
		double deltay = Math.abs(loc2
				.getY() - loc.getY());
		double deltaz = Math.abs(loc2
				.getZ() - loc.getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
    public double getdistance(Entity ent1, Block ent2) {
		double deltax = Math.abs(ent1.getLocation()
				.getX() - ent2.getLocation().getX());
		double deltay = Math.abs(ent1.getLocation()
				.getY() - ent2.getLocation().getY());
		double deltaz = Math.abs(ent1.getLocation()
				.getZ() - ent2.getLocation().getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
    public double getdistance(Player ent1, Block ent2) {
		double deltax = Math.abs(ent1.getLocation()
				.getX() - ent2.getLocation().getX());
		double deltay = Math.abs(ent1.getLocation()
				.getY() - ent2.getLocation().getY());
		double deltaz = Math.abs(ent1.getLocation()
				.getZ() - ent2.getLocation().getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
    
    public double getdistance(Entity ent1, Entity ent2) {
		double deltax = Math.abs(ent1.getLocation()
				.getX() - ent2.getLocation().getX());
		double deltay = Math.abs(ent1.getLocation()
				.getY() - ent2.getLocation().getY());
		double deltaz = Math.abs(ent1.getLocation()
				.getZ() - ent2.getLocation().getZ());
		double distance = Math
				.sqrt((deltax * deltax)
						+ (deltay * deltay)
						+ (deltaz * deltaz));
		return distance;
    }
}