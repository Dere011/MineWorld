package mineworld;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Main_CommandsControl {
	
	Main plugin;
	int restartsure = 0;

    public Main_CommandsControl(Main parent) {
    	plugin = parent;
    }
    
    public Boolean Main_onCommand_do(CommandSender sender, Command command, String commandLabel, String[] args) {
    	String subCommand = args[0].toLowerCase();
    	
    	if ((sender instanceof Player) == false) {
	    	if (subCommand.equals("kickall4774")) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			p.kickPlayer("Restart de routine (Le serveur sera de retour dans moins de 30 secondes).");
	    			plugin.maintenance_status = true;
	        	}
	    		plugin.removeallitems();
	    		return false;
	        }
	    	
	    	if (subCommand.equals("crash66565465") && plugin.maintenance_status != true) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			p.kickPlayer("Le serveur est détecté comme planter. Restart en cours.");
	    			plugin.maintenance_status = true;
	        	}
	    		plugin.removeallitems();
	    		return false;
	        }
	    	
	    	if (subCommand.equals("restartmsg4774")) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(p.isOnline()) {
	    				p.sendMessage(ChatColor.RED + "Restart de routine dans moins de "+ ChatColor.BLUE + "20"+ ChatColor.RED + " secondes.");
	    			}
	    		}
	    		return false;
	        }
	    	
	    	if (subCommand.equals("restartmsg4550")) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(p.isOnline()) {
	    				p.sendMessage(ChatColor.RED + "Restart de mise à jour dans moins de "+ ChatColor.BLUE + "20"+ ChatColor.RED + " secondes.");
	    			}
	    		}
	    		return false;
	        }
   	
	    	if (subCommand.equals("kickall5000")) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			p.kickPlayer("Le serveur va subir une mise a jour, restart en cours.");
	    			plugin.maintenance_status = true;
	        	}
	    		plugin.removeallitems();
	    		return false;
	        }
	    	
	    	if (subCommand.equals("maintenance34120")) {
	    		if(plugin.maintenance_status == false) {
		    		for (Player p : plugin.getServer().getOnlinePlayers()) {
		    			if(!p.isOp()) {
		    				p.kickPlayer("Notre serveur passe en maintenance.");
		    			}
		        	}
		    		plugin.maintenance_status = true;
	    		}else{
	    			plugin.maintenance_status = false;
	    		}
	    		return false;
	        }
	    	
	    	if (subCommand.equals("save4774")) {
	    		for (Player p : plugin.getServer().getOnlinePlayers()) {
	    			if(p.isOnline()) {
	    				p.sendMessage(ChatColor.DARK_GRAY + "[Sauvegarde du serveur en cours]");
	    			}
	    		}
	    		return false;
	        }
    	}
		
        Player player = (Player) sender;
        
		if (command.getName().toLowerCase().equals("modo")) {
			if (subCommand.equals("restart")) {
				if(restartsure){
					
				}else{
					restartsure = true;
					plugin.Main_MessageControl.sendTaggedMessage(player, "Voulez-vous vraiment redémarrer le serveur (/modo restart pour confirmer) ?", 2, "[MODO]");
				}
			}else if (subCommand.equals("spy")) {
				
			}else if (subCommand.equals("maintenance")) {
				
			}else if (subCommand.equals("laststart")) {
				
			}
		}
        
		if (command.getName().toLowerCase().equals("mineworld")) {
			if (subCommand.equals("unlock")) {
			    if(args[1].contains("4875dmw")) {
			    	String[] anTxt = plugin.Main_MessageControl.createstrings(2);
				   	anTxt[0] = "Merci, votre compte est maintenant débloqué.";
				   	anTxt[1] = "Attention : toutes questions relatives à une réponse déjà fourni dans le guide vous seras maintenant sanctionnable.";
				   	plugin.Main_MessageControl.sendTaggedMessage(player, anTxt, 2, "");
					plugin.setPlayerConfig(player, "first_connexion", true);
					return true;
				}
			}else if (subCommand.equals("ok")) {
			    plugin.Main_MessageControl.sendTaggedMessage(player, "Merci, la news est maintenant inscrite comme lu.", 2, "");
			    Integer news_rev = (Integer) plugin.getServerConfig("informations.news_rev", "int");
				plugin.setPlayerConfig(player, "last_news_rev", news_rev);
				return true;
			}else if (subCommand.equals("tick")) {
				boolean tick = (Boolean) plugin.getPlayerConfig(player, "message_tick", "boolean");
				if(!tick) {
					plugin.setPlayerConfig(player, "message_tick", true);
					plugin.Main_MessageControl.sendTaggedMessage(player, "Service de tick innactif.", 1, "[TICK]");
				}else{
					plugin.setPlayerConfig(player, "message_tick", false);
				    plugin.Main_MessageControl.sendTaggedMessage(player, "Service de tick actif.", 1, "[TICK]");
				}
				return true;
			}else if (subCommand.equals("fchunk") || subCommand.equals("fc") || subCommand.equals("forcechunk")) {
				plugin.Main_ChunkControl.CacheOnlyChunk(player);
				plugin.Main_MessageControl.sendTaggedMessage(player, "Regénération des chunks en cours.", 1, "[CHUNKFIX]");	
				return true;
			}else if (subCommand.equals("news")) {
				plugin.Main_MessageControl.sendLastNews(player);
				return true;
			}else if (subCommand.equals("guide")) {
				plugin.Main_MessageControl.sendNotRegisteredMsg(player);
				return true;
			}else if (subCommand.equals("removeme")) {
				plugin.setPlayerConfig(player, "remove_me", true);
				player.kickPlayer("Merci de votre visite sur MineWorld.");
				return true;
			}else if (subCommand.equals("stats_pp")) {
				plugin.conf_player.load();
				int ppresences = plugin.conf_player.getInt("load-player."+ player.getName() +".ppresences", 0);
			    plugin.Main_MessageControl.sendTaggedMessage(player, "Vous avez "+ ppresences +" points de présences.", 1, "");
				return true;
			}else if (subCommand.equals("parrain")) {
			    plugin.Main_MessageControl.sendTaggedMessage(player, "Votre code parrain est : <Service de parrain indisponible>", 1, "");
				return true;
			}else if (subCommand.equals("visiteur")) {
				int visiteur = 0;
				for (Entity ent : player.getNearbyEntities(24, 24, 24)) {
					if (ent instanceof Player) {
						if(plugin.Main_Visiteur.is_visiteur((Player) ent) != plugin.Main_Visiteur.is_visiteur(player)) {
							visiteur++;
						}
					}
				}
				plugin.Main_MessageControl.sendTaggedMessage(player, "Il y a "+visiteur+" visiteurs autour de vous...", 1, "");
		return false;
    }
    
    /*if(player.isOp()) {
		if (subCommand.equals("deadday")) {
			plugin.Main_TimeControl.is_preburningday = true;
    	} else if (subCommand.equals("goodday")) {
    		plugin.Main_TimeControl.is_preburningday = false;
        } else if (subCommand.equals("burn")) {
            if(plugin.burn) {
            	plugin.burn = false;
            }else{
            	plugin.burn = true;
            }
        } else if (subCommand.equals("ttrue")) {
        	player.getWorld().setStorm(true);
        	player.getWorld().setThundering(true);
        } else if (subCommand.equals("tfalse")) {
        	player.getWorld().setStorm(false);
        	player.getWorld().setThundering(false);
        } else if (subCommand.equals("setspawn")) {
        	player.getWorld().setSpawnLocation(player.getLocation().getBlockX(), player.getLocation().getBlockY()+1, player.getLocation().getBlockZ());
        } else if (subCommand.equals("thor")) {
            if(plugin.thor) {
            	plugin.thor = false;
            }else{
            	plugin.thor = true;
            } 
        } else if (subCommand.equals("wave")) {
            if(plugin.deathwave) {
            	plugin.deathwave = false;
            	player.sendMessage(ChatColor.DARK_BLUE + "wave control : Disabled");
            }else{
            	plugin.deathwave = true;
            	player.sendMessage(ChatColor.DARK_BLUE + "wave control : Enabled");
            } 
        } else if (subCommand.equals("time")) {
            player.sendMessage(ChatColor.DARK_BLUE + " " + plugin.Main_TimeControl.meteo_monde_tick);
        } else if (subCommand.equals("wavetarget")) {
            if(plugin.deathwavetarget) {
            	plugin.deathwavetarget = false;
            	player.sendMessage(ChatColor.DARK_BLUE + "wave control : Disabled");
            }else{
            	plugin.deathwavetarget = true;
            	player.sendMessage(ChatColor.DARK_BLUE + "wave control : Enabled");
            } 
        } else if (subCommand.equals("wavetime")) {
            plugin.wavetimer = Integer.parseInt(args[1]);
            player.sendMessage(ChatColor.DARK_BLUE + "wave control set : "+ plugin.wavetimer);
        } else if (subCommand.equals("burnradius")) {
            plugin.burnradius = Integer.parseInt(args[1]);
            player.sendMessage(ChatColor.DARK_BLUE + "burnradius set : "+ plugin.burnradius);
        } else if (subCommand.equals("settime")) {
            plugin.Main_TimeControl.meteo_monde_tick = Integer.parseInt(args[1]);
            player.sendMessage(ChatColor.DARK_BLUE + "time set : "+ plugin.Main_TimeControl.meteo_monde_tick);
        } else if (subCommand.equals("nuke")) {
            if(plugin.nuke) {
            	plugin.nuke = false;
            	player.sendMessage(ChatColor.DARK_BLUE + "Nuke : Disabled");
            	plugin.setServerConfig("informations.nuke", false);
            }else{
            	plugin.nuke = true;
            	player.sendMessage(ChatColor.DARK_BLUE + "Nuke : Enabled");
            }
        } else if (subCommand.equals("nuketime")) {
            if(plugin.lastbomb.timed == false) {
            	plugin.lastbomb.timed();
            	player.sendMessage(ChatColor.DARK_RED + "Activation de la bombe...");
            }else{
            	plugin.lastbomb.timed = false;
            	plugin.lastbomb.timed_tick = 0;
            	player.sendMessage(ChatColor.DARK_RED + "Désactivation de la bombe...");
            	plugin.setServerConfig("informations.nuke", false);
            }
        } else if (subCommand.equals("nukearme")) {
            if(plugin.lastbomb.armed == false) {
            	plugin.lastbomb.armed = true;
            	player.sendMessage(ChatColor.DARK_RED + "Armage de la bombe.");
            }else{
            	plugin.lastbomb.armed = false;
            	player.sendMessage(ChatColor.DARK_RED + "Désarmage de la bombe.");
            	plugin.setServerConfig("informations.nuke", false);
            }
        } else if (subCommand.equals("nukeall")) {
            if(plugin.lastbomb.msgall == false) {
            	plugin.lastbomb.msgall = true;
            	player.sendMessage(ChatColor.DARK_RED + "msgall on");
            }else{
            	plugin.lastbomb.msgall = false;
            	player.sendMessage(ChatColor.DARK_RED + "msgall off");
            }
        } else if (subCommand.equals("storm")) {
        	Main_AimBlock aiming = new Main_AimBlock(player);
            Block block = aiming.getTargetBlock();
        	plugin.setServerConfig("load-storm.storm_"+ args[1] +".x", block.getX());
        	plugin.setServerConfig("load-storm.storm_"+ args[1] +".y", block.getY());
        	plugin.setServerConfig("load-storm.storm_"+ args[1] +".z", block.getZ());
        	plugin.setServerConfig("load-storm.storm_"+ args[1] +".world", block.getWorld().getName());
        	player.sendMessage(ChatColor.DARK_RED + "setet storm");
        } else if (subCommand.equals("nukeex")) {
            plugin.lastbomb.explode();
            plugin.setServerConfig("informations.nuke", false);
        } else if (subCommand.equals("nukecra")) {
        	plugin.lastbomb.createenabled = false;
        } else if (subCommand.equals("blackhole")) {
            if(plugin.hole == false) {
            	plugin.hole = true;
            	player.sendMessage(ChatColor.DARK_RED + "Activation du BlackHole");
            }else{
            	plugin.hole = false;
            	player.sendMessage(ChatColor.DARK_RED + "Desactivation du BlackHole");
            }
        }
	}*/
}