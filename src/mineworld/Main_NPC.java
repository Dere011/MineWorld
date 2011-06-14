package mineworld;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import npcspawner.BasicHumanNpc;
import npcspawner.BasicHumanNpcList;
import npcspawner.NpcSpawner;

public class Main_NPC {
	
    private final Main plugin;
    public BasicHumanNpcList HumanNPCList;
	public BasicHumanNpc thesun = null;
	public BasicHumanNpc thebot = null;
	
	private int last_npc_reload = 0;
	private int is_core_tick = 0;
	
	Thread t_01;
    
    public Main_NPC(Main parent) {
        this.plugin = parent;
        HumanNPCList = new BasicHumanNpcList();
        thesun = null;
        thebot = null;
    }
    
	public Runnable runThread(final Main plugin) {
		if(t_01 == null) {
			t_01 = new Thread(new Runnable() {
				public void run()
				{
			    	try {
			    		if (plugin.playerInServer()) {
			    			do_cron();
			    		}
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
		            return;
				}
			});
		}
		t_01.setPriority(Thread.MIN_PRIORITY);
		t_01.setDaemon(true);
		return t_01;
	}
    
    public Boolean NPC_onCommand_do(CommandSender sender, Command command, String commandLabel, String[] args) {
        String subCommand = args[0].toLowerCase();
 			
        Player player = (Player) sender;
        Location l = player.getLocation();
 			
 			if(!sender.isOp() || args.length < 1) {
 			    return false;
 			}

             Configuration conf_npc = plugin.conf_npc;
             Configuration conf_player = plugin.conf_player;
             
            if (subCommand.equals("create")) {
                 if (args.length < 3) {
                     return false;
                 } 
                 if (HumanNPCList.get(args[1]) != null) {
                     player.sendMessage("This npc-id is already in use.");
                     return true;
                 }
                 conf_npc.load();
                 BasicHumanNpc hnpc = NpcSpawner.SpawnBasicHumanNpc(args[1], args[2], player.getWorld(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                 HumanNPCList.put(args[1], hnpc);
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".id", args[1]);
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".name", args[2]);
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".enabled", true);
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".itemlist", "0,0,0,0,0,");
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.x", l.getX());
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.y", l.getY());
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.z", l.getZ());
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.world", l.getWorld().getName());
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.pitch", l.getPitch());
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.yaw", l.getYaw());
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".msg.hello", "");
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".msg.rightclic", "");
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".msg.attaqued", "");
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".msg.attaquedifattaqued", false);
                 conf_npc.setProperty("load-npcs.npc_"+ args[1] +".msg.attaqueforce", 1);	
                 conf_npc.save();
             } else if (subCommand.equals("move")) {
                 if (args.length < 1) {
                     return false;
                 }
                 BasicHumanNpc npc = HumanNPCList.get(args[1]);
                 if (npc != null) {
                	 conf_npc.load();
                     npc.moveTo(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
                     conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.x", l.getX());
                     conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.y", l.getY());
                     conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.z", l.getZ());
                     conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.world", l.getWorld().getName());
                     conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.yaw", l.getYaw());
                     conf_npc.setProperty("load-npcs.npc_"+ args[1] +".loc.pitch", l.getPitch());
                     conf_npc.save();
                     return true;
                 }
             } else if (subCommand.equals("changename")) {
                 if (args.length < 2) {
                     return false;
                 }
                 BasicHumanNpc npc = this.HumanNPCList.get(args[1]);
                 if (npc != null) {
                 	npc.setName(args[2]);
                 	conf_npc.load();
                 	conf_npc.setProperty("load-npcs.npc_"+ args[1] +".name", args[2]);
                 	conf_npc.save();
                    return true;
                 }

             } else if (subCommand.equals("reputation")) {
            	 conf_player.load();
            	 conf_player.setProperty("load-player."+ args[1] +".npc_reputation_"+ args[2], args[3]);
            	 conf_player.save();
             } else if (subCommand.equals("delete")) {
                 if (args.length < 1) {
                     return false;
                 }
                 conf_npc.load();
                 BasicHumanNpc npc = HumanNPCList.get(args[1]);
                 conf_npc.removeProperty("load-npcs.npc_"+ args[1]);
                 NpcSpawner.RemoveBasicHumanNpc(npc);
                 HumanNPCList.remove(npc.getUniqueId());
             } else if (subCommand.equals("deleteall")) {
             	this.HumanNPCList.removeAllNpc();
                this.HumanNPCList = new BasicHumanNpcList();
             } else if (subCommand.equals("reload")) {
             	this.HumanNPCList.removeAllNpc();
                this.HumanNPCList = new BasicHumanNpcList();
                ReloadAllNpcs();
             }
             
             return false;
     }
	
	public void do_cron() {
		Configuration conf_npc = plugin.conf_npc;
		Configuration conf_player = plugin.conf_player;
		
		if(is_core_tick >= 3) {
				is_core_tick = 0;
		    	for (BasicHumanNpc entry : HumanNPCList.GetNPCS()) {
		    		
		    			String npcid = entry.getUniqueId();
			    		if(Main.chicken == null) {
			    			if(npcid.contains("500000")) {
			    				Main.chicken = entry.getBukkitEntity();
			    			}
			    			if(npcid.contains("500001")) {
			    				Main.zombie = entry.getBukkitEntity();
			    			}
			    			if(npcid.contains("500003")) {
			    				Main.spider = entry.getBukkitEntity();
			    			}
			    			if(npcid.contains("500004")) {
			    				Main.slime = entry.getBukkitEntity();
			    			}
			    		}
			    		
						boolean is_attaqued = false;
	    				boolean opishere = false;

		    			for (Entity e : entry.getBukkitEntity().getNearbyEntities(10.0, 10.0, 10.0)) {
			    			if (e instanceof Creature) {
			    				Creature c = (Creature) e;
				    			if (e instanceof Wolf) {
							    		if (plugin.checkLocation(entry.getBukkitEntity().getLocation(), e.getLocation(), 4.0)) {
							    			if(((Wolf) e).isSitting()) {
								    			if(((Wolf) e).isAngry()) {
								    				rotateNPCToCreature(entry, e);
								    				entry.animateArmSwing();
								    				((Wolf) e).setAngry(false);
								    				((Wolf) e).setHealth(10);
								    				((Wolf) e).setTarget(null); 
							                        break;
								    			}else if(((Wolf) e).isAngry() == false) {
								    				if(((Wolf) e).getHealth() < 10) {
								    					((Wolf) e).setHealth(10);
								    				}
								    				((Wolf) e).setTarget(null);
								    			}
							    			}else{
							    				if(((Wolf) e).isAngry()) {
								    				rotateNPCToCreature(entry, e);
								    				entry.animateArmSwing();
								    				c.damage(100, entry.getBukkitEntity());
								    				is_attaqued = true;
							                        break;
							    				}
							    			}
							    		}
				    			}else{
			    					if (e instanceof Monster) {
						    			if (plugin.checkLocation(entry.getBukkitEntity().getLocation(), e.getLocation(), 4.0)) {
						    				conf_npc.load();
						        			ConfigurationNode node = conf_npc.getNode("load-npcs");
						        			Boolean nontueur = node.getBoolean("npc_"+ npcid +".nontueur", false);
						    				if(nontueur == false) {
							    				rotateNPCToCreature(entry, e);
							    				entry.animateArmSwing();
							    				c.damage(100, entry.getBukkitEntity());
							    				c.remove();
							    				is_attaqued = true;
						                        break;
						    				}
						    			}
			    					}
				    			}
			    			}
			    			if(is_attaqued == false) {
				    			if (e instanceof Player) {
					    			Player p = (Player) e;
				    				if (p.isOnline() && !plugin.Main_Visiteur.is_visiteur(p) && !p.isDead() && p.getWorld() == entry.getBukkitEntity().getWorld()) {
						    			if (plugin.checkLocation(p.getLocation(), entry.getBukkitEntity().getLocation(), 10.0)) {
						    					if(p.isOp()) {
							    					if(p.getHealth() < 100) {
														entry.animateArmSwing();
														p.setHealth(100);
													}
							    					rotateNPCToPlayer(entry, p);
							    					opishere = true;
												}
						    			}
						    			if (plugin.checkLocation(p.getLocation(), entry.getBukkitEntity().getLocation(), 3.0)) {
						    				conf_npc.load();
						    				conf_player.load();
						        			ConfigurationNode node = conf_npc.getNode("load-npcs");
						    				String faction = node.getString("npc_"+ npcid +".faction");
						        			int attaqueforce = node.getInt("npc_"+ npcid +".msg.attaqueforce", 1);
						        			int minreputation = node.getInt("npc_"+ npcid +".reputation", -1000);
						        			if(faction == null) {
						        				faction = "MineWorld";
						        			}
											int p_reputation = conf_player.getInt("load-player."+ p.getName() +".npc_reputation_"+faction, -1000);
											if(p_reputation == -1000) {
												conf_player.setProperty("load-player."+ p.getName() +".npc_reputation_"+faction, 0);
												conf_player.save();
										    	p_reputation = 0;
											}
											if(minreputation == -1000) {
												conf_npc.setProperty("load-npcs.npc_"+ npcid +".reputation", -10);
												conf_npc.save();
										    	minreputation = -10;
											}
											if(p_reputation <= minreputation) {
												if(attaqueforce == 0) { attaqueforce = 1; }
												if(p_reputation >= -30 && p_reputation < 0) { attaqueforce = 100; }
												if(p_reputation <= -100 && p_reputation < -30) { attaqueforce = 300; }
												rotateNPCToPlayer(entry, p);
												entry.animateArmSwing();
												p.damage(attaqueforce, entry.getBukkitEntity());
												p.sendMessage(ChatColor.RED + "La faction "+faction+" vous considèrent comme hostile.");
												p.sendMessage(ChatColor.RED + "Vous avez "+p_reputation+" point(s) de réputation avec la faction "+faction+".");
											}else if(p_reputation >= 0 && p_reputation < 5 && opishere == false) {
												rotateNPCToPlayer(entry, p);
											}else if(p_reputation >= 5) {
												if(p.getHealth() < 10) {
													entry.animateArmSwing();
													p.setHealth(10);
													p.sendMessage(ChatColor.DARK_GREEN + entry.getBukkitEntity().getName()+ " ("+faction+") vient de vous soigner.");
												}else if(p.getFireTicks() > 0) {
													p.setFireTicks(0);
												}
											}
						    			}
				    				}
				    			}
			    			}
			    		}
		    		}
			}else{
				is_core_tick++;
			}
			if(last_npc_reload > 2000) {
				last_npc_reload = 0;
				if(HumanNPCList != null) {
					HumanNPCList.removeAllNpc();
				}
		    	HumanNPCList = new BasicHumanNpcList();
		    	ReloadAllNpcs();
	        }else{
	        	last_npc_reload++;
	        }
    }

    public static ArrayList<Integer> GetItem(String name) {
    	ArrayList<Integer> array = new ArrayList<Integer>();
    	if (name.isEmpty()) {
    		name = "0,0,0,0,0,";
    	}
        for (String s : name.split(",")) {
    		array.add(Integer.parseInt(s));
    	} 
        return array;
    }
    
    public static void addItems(BasicHumanNpc npc, ArrayList<Integer> items) {
    	if (items != null) {
    	PlayerInventory inv = npc.getBukkitEntity().getInventory();

    	Material inHand = Material.getMaterial(items.get(0));
    	Material helmet = Material.getMaterial(items.get(1));
    	Material chestplate = Material.getMaterial(items.get(2));
    	Material leggings = Material.getMaterial(items.get(3));
    	Material boots = Material.getMaterial(items.get(4));

    	inv.setItemInHand(new ItemStack(inHand));
    	inv.setHelmet(new ItemStack(helmet));
    	inv.setChestplate(new ItemStack(chestplate));
    	inv.setLeggings(new ItemStack(leggings));
    	inv.setBoots(new ItemStack(boots));
    	}
    }
    
    public void rotateNPCToPlayer(BasicHumanNpc NPC, Player player) {
        Location loc = NPC.getBukkitEntity().getLocation();
        double xDiff = player.getLocation().getX() - loc.getX();
        double yDiff = player.getLocation().getY() - loc.getY();
        double zDiff = player.getLocation().getZ() - loc.getZ();
        double DistanceXZ = Math.sqrt(xDiff*xDiff+zDiff*zDiff);
        double DistanceY = Math.sqrt(DistanceXZ*DistanceXZ+yDiff*yDiff);
        double yaw = (Math.acos(xDiff/DistanceXZ)*180/Math.PI);
        double pitch = (Math.acos(yDiff/DistanceY)*180/Math.PI)-90;
        if(zDiff < 0.0){
                yaw = yaw + (Math.abs(180 - yaw)*2);
        }
        NPC.moveTo(loc.getX(), loc.getY(), loc.getZ(), (float)yaw-90, (float)pitch);
    } 
    
    public void rotateNPCToCreature(BasicHumanNpc NPC, Entity e) {
        Location loc = NPC.getBukkitEntity().getLocation();
        double xDiff = e.getLocation().getX() - loc.getX();
        double yDiff = e.getLocation().getY() - loc.getY();
        double zDiff = e.getLocation().getZ() - loc.getZ();
        double DistanceXZ = Math.sqrt(xDiff*xDiff+zDiff*zDiff);
        double DistanceY = Math.sqrt(DistanceXZ*DistanceXZ+yDiff*yDiff);
        double yaw = (Math.acos(xDiff/DistanceXZ)*180/Math.PI);
        double pitch = (Math.acos(yDiff/DistanceY)*180/Math.PI)-90;
        if(zDiff < 0.0){
                yaw = yaw + (Math.abs(180 - yaw)*2);
        }
        NPC.moveTo(loc.getX(), loc.getY(), loc.getZ(), (float)yaw-90, (float)pitch);
    }

    public void ReloadAllNpcs() {
    	try {
    		Configuration conf = plugin.conf_npc;
    		if(plugin.NPC_configFile.exists()){
        		conf.load();
				List<String> npclist = conf.getKeys("load-npcs");
				if(!npclist.isEmpty()) {
					ConfigurationNode node = conf.getNode("load-npcs");
					for(String npc : npclist){
						String id = node.getString(npc + ".id");
						String name = node.getString(npc + ".name");
						boolean enabled = node.getBoolean(npc + ".enabled", false);
						String itemlist = node.getString(npc + ".itemlist");
						String w = node.getString(npc + ".loc.world");
						Double x = node.getDouble(npc + ".loc.x", 0);
						Double y = node.getDouble(npc + ".loc.y", 0);
						Double z = node.getDouble(npc + ".loc.z", 0);
						float yaw = node.getInt(npc + ".loc.yaw", 0);
						float pitch = node.getInt(npc + ".loc.pitch", 0);
						if(HumanNPCList.get(id) == null && enabled) {
							World world = plugin.getServer().getWorld(w);
							BasicHumanNpc hnpc = NpcSpawner.SpawnBasicHumanNpc(id, name, world, x, y, z, yaw, pitch);
							HumanNPCList.put(id, hnpc);
				            if(itemlist != null) {
					            ArrayList<Integer> items = GetItem(itemlist);
					            addItems(hnpc, items);
				            }
						}
					}
					System.out.println();
				}
			}
        } catch (Exception e) {
            plugin.sendError(e.getMessage() + e.getStackTrace().toString());
            e.printStackTrace();
            return;
        }
    }
}