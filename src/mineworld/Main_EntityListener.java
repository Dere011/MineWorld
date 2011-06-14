package mineworld;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import npcspawner.BasicHumanNpc;
import npcspawner.NpcEntityTargetEvent;
import npcspawner.NpcEntityTargetEvent.NpcTargetReason;

public class Main_EntityListener extends EntityListener {

	private final Main plugin;
    private final Main_NPC mnpc;
    private final Configuration conf_npc;
    private final Configuration conf_player;

    public Main_EntityListener(Main parent) {
    	this.plugin = parent;
        this.mnpc = parent.Main_NPC;
        this.conf_npc = parent.conf_npc;
        this.conf_player = parent.conf_player; 
    }
    
    public void onEntityDeath(EntityDeathEvent event) {
		if(plugin.move_last.containsKey(event.getEntity())) {
			plugin.move_last.remove(event.getEntity());
		}
    }
    
    public void onEntityDamage(EntityDamageEvent event) {
	    if (event instanceof EntityDamageByEntityEvent) {
	    	EntityDamageByEntityEvent dmgByEntity = (EntityDamageByEntityEvent) event;
        	if (dmgByEntity instanceof Player) {
    	    	if (plugin.Main_Visiteur.is_visiteur((Player) dmgByEntity)) {
    	    		plugin.Main_Visiteur.denied_message((Player) dmgByEntity);
    	    		event.setCancelled(true);
    	    		return;
    	    	}
        	}
        	if (dmgByEntity.getDamager() instanceof Player) {
    	    	if (plugin.Main_Visiteur.is_visiteur((Player) dmgByEntity.getDamager())) {
    	    		plugin.Main_Visiteur.denied_message((Player) dmgByEntity.getDamager());
    	    		event.setCancelled(true);
    	    		return;
    	    	}
        	}
	    	if (dmgByEntity != null && event.getEntity() instanceof HumanEntity) {
	            BasicHumanNpc npc = mnpc.HumanNPCList.getBasicHumanNpc(event.getEntity());
	            if (npc != null && dmgByEntity.getDamager() instanceof Player) {
	                Player p = (Player) dmgByEntity.getDamager();
	                if(p.isOnline()) {
	        			conf_npc.load();
	        			conf_player.load();
		    			String npcid = npc.getUniqueId();
		    			ConfigurationNode node = conf_npc.getNode("load-npcs");
		    			String noattaque = node.getString("npc_"+ npcid +".msg.attaqued");
		    			Boolean attaqueifattaqued = node.getBoolean("npc_"+ npcid +".msg.attaquedifattaqued", false);
		    			int attaqueforce = node.getInt("npc_"+ npcid +".msg.attaqueforce", 1);
		    			String faction = node.getString("npc_"+ npcid +".faction");
		    			if(faction == null) {
		    				faction = "MineWorld";
		    			}
		    			if(noattaque != null && attaqueifattaqued != null) {
							if(!p.isOp()) {
								if(attaqueifattaqued) {
									p.damage(attaqueforce, npc.getBukkitEntity());
									npc.animateArmSwing();
								}
								int p_reputation = conf_player.getInt("load-player."+ p.getName() +".npc_reputation_"+ faction, -1000);	
								if(p_reputation == -1000) {
									conf_player.setProperty("load-player."+ p.getName() +".npc_reputation_"+ faction, 0);
									conf_player.save();
					    			p_reputation = 0;
								}
								conf_player.setProperty("load-player."+ p.getName() +".npc_reputation_"+ faction, p_reputation-1);
								p.sendMessage(ChatColor.RED + "Vous avez perdu un point de réputation auprés de la faction "+faction+".");
								if(p_reputation == -1) {
									p.sendMessage(ChatColor.RED + "La faction "+faction+" vous considèrent maintenant comme neutre.");
								}else if(p_reputation == -11) {
									p.sendMessage(ChatColor.RED + "La faction "+faction+" vous considèrent maintenant comme hostile.");
								}
								conf_player.save();
		                    }
		                    if(noattaque.length() > 1) {
		                    	p.sendMessage("["+faction+"] <" + npc.getName() + "> " + noattaque);
		                    }
		                    event.setCancelled(true);
		    			}
	                }
	            }
	         }
	      }
    }

    public void onEntityTarget(EntityTargetEvent event) {
    	if (event.getTarget() instanceof Player) {
	    	if (plugin.Main_Visiteur.is_visiteur((Player) event.getTarget())) {
	    		event.setCancelled(true);
	    		return;
	    	}
    	}
        if (event instanceof NpcEntityTargetEvent) {
            NpcEntityTargetEvent nevent = (NpcEntityTargetEvent)event;
            BasicHumanNpc npc = mnpc.HumanNPCList.getBasicHumanNpc(event.getEntity());
            if (event.getTarget() instanceof Player) {
    			conf_npc.load();
    			conf_player.load();
    			String npcid = npc.getUniqueId();
    			ConfigurationNode node = conf_npc.getNode("load-npcs");
    			String hello = node.getString("npc_"+ npcid +".msg.hello");
    			String rightclic = node.getString("npc_"+ npcid +".msg.rightclic");
    			String faction = node.getString("npc_"+ npcid +".faction");
    			if(faction == null) {
    				faction = "MineWorld";
    			}
    			Player p = (Player) event.getTarget();
                if(p.isOp() && nevent.getNpcReason() == NpcTargetReason.NPC_RIGHTCLICKED) {
                	p.sendMessage(npc.getName() + " => " + npcid);
                }
    			if(hello != null && rightclic != null) {
	                if (nevent.getNpcReason() == NpcTargetReason.CLOSEST_PLAYER) {
	                	if(hello.length() > 1) {
							int p_reputation = conf_player.getInt("load-player."+ p.getName() +".npc_reputation_"+ faction, -1000);	
							if(p_reputation == -1000) {
								conf_player.setProperty("load-player."+ p.getName() +".npc_reputation_"+ faction, 0);
								conf_player.save();
				    			p_reputation = 0;
							}
	                		if(p_reputation >= 0) {
	                			p.sendMessage("["+faction+"] <" + npc.getName() + "> " + hello);
	                		}
	                	}
	                    event.setCancelled(true);
	                } else if (nevent.getNpcReason() == NpcTargetReason.NPC_RIGHTCLICKED) {
						int p_reputation = conf_player.getInt("load-player."+ p.getName() +".npc_reputation_"+ faction, -1000);	
						if(p_reputation == -1000) {
							conf_player.setProperty("load-player."+ p.getName() +".npc_reputation_"+ faction, 0);
							conf_player.save();
			    			p_reputation = 0;
						}
	                	if(rightclic.length() > 1) {
	                		if(p_reputation > -10) {
	                			p.sendMessage("["+faction+"] <" + npc.getName() + "> " + rightclic);
	                		}
	                    }
	                    event.setCancelled(true);
	                }
    			}
            }
        }
    }
}