 package mineworld;

import net.minecraft.server.Packet20NamedEntitySpawn;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Main_CommandsControl { 
	
	Main plugin;

    public Main_CommandsControl(Main parent) {
    	plugin = parent;
    }
    
	private long restartsure = 0;
	private long maintenancesure = 0;
    
    public Boolean Main_onCommand_do(CommandSender sender, Command command, String commandLabel, String[] args) {
    	String subCommand = args[0].toLowerCase();
    	
    	if ((sender instanceof Player) == false) {
	    	if (subCommand.equals("kickall4774")) {
	    		plugin.D.kickall("Restart de routine (Le serveur sera de retour dans moins de 30 secondes).", true);
	    		plugin.maintenance_status = true;
	    		plugin.I.remove_all_items();
	    		return false;
	        }
	    	
	    	if (subCommand.equals("crash66565465") && plugin.maintenance_status != true) {
	    		plugin.D.kickall("Le serveur est détecté comme planter, restart en cours.", true);
    			plugin.maintenance_status = true;
    			plugin.I.remove_all_items();
	    		return false;
	        }
	    	
	    	if (subCommand.equals("kickall5000")) {
	    		plugin.D.kickall("Le serveur va subir une mise a jour, restart en cours.", true);
    			plugin.maintenance_status = true;
    			plugin.I.remove_all_items();
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
	    	
	    	if (subCommand.equals("maintenance34120")) {
	    		if(plugin.maintenance_status == false) {
	    			plugin.D.kickall("Notre serveur passe en maintenance.", false);
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
	    		plugin.Main_ContribControl.sendNotificationToAll("Notification", "Sauvegarde du serveur.", Material.FLINT_AND_STEEL);
	    		return false;
	        }
    	}
    	
        Player player = (Player) sender;
        
		if (command.getName().toLowerCase().equals("modo")) {
			if(!plugin.ismodo(player)) {
				plugin.MC.sendTaggedMessage(player, "Commande interdite.", 2, "[DENIED]");
				return false;
			}
			if(plugin.CC.isClient(player, true)) {
				if (subCommand.equals("restart")) {
					if((restartsure+30) > plugin.timetamps){
						plugin.sendInfo("Le modérateur "+ player.getDisplayName() +" arrête le serveur.");
						CraftServer server = (CraftServer) plugin.getServer();
						final CommandSender cs = server.getServer().console;
						server.dispatchCommand(cs, "stop");
					}else{
						restartsure = plugin.timetamps;
						plugin.MC.sendTaggedMessage(player, "Voulez-vous vraiment redémarrer le serveur (/modo restart pour confirmer) ?", 2, "[MODO]");
					}
					return false;
				}else if (subCommand.equals("home")) {
						if(args[1] != null && !args[1].isEmpty()){
							Location home = plugin.D.getPlayerHomeLocation(args[1]);
							if(home == null) {
								plugin.MC.sendTaggedMessage(player, "Le joueur ne possède pas de home / Nom du joueur invalide.", 2, "[MODO]");
							}else{
								player.teleport(home);
							}
						}else{
							plugin.MC.sendTaggedMessage(player, "Commande invalide", 2, "[MODO]");
						}
						return false;
				}else if (subCommand.equals("spy")) {
					if(plugin.spy_player.contains(player.getUniqueId())) {
						plugin.spy_player.remove(player.getUniqueId());
						plugin.sendInfo("Le modérateur "+ player.getDisplayName() +" désactive le mode SPY.");
						plugin.MC.sendTaggedMessage(player, "Mode SPY innactif.", 2, "[MODO]");
			    		for (Entity entity : player.getNearbyEntities(24, 24, 24)) {
			    			if (entity instanceof Player) {
								if(!((Player) entity).isOp()) {
									CraftPlayer unHide = (CraftPlayer) player;
									CraftPlayer unHideFrom = (CraftPlayer) entity;
									unHide.getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(unHideFrom.getHandle()));
								}
							}
			    		}
					}else{
						plugin.spy_player.add(player.getUniqueId());
						plugin.sendInfo("Le modérateur "+ player.getDisplayName() +" active le mode SPY.");
						plugin.MC.sendTaggedMessage(player, "Mode SPY actif.", 2, "[MODO]");
					}
					return false;
				}else if (subCommand.equals("maintenance")) {
					if((maintenancesure+30) > plugin.timetamps){
						plugin.maintenance_status = true;
						plugin.maintenance_message = "Serveur en maintenance (défini par "+ player.getDisplayName() +").";
						plugin.sendInfo("Le modérateur "+ player.getDisplayName() +" défini une maintenance.");
			    		plugin.D.kickall("Serveur en maintenance (défini par "+ player.getDisplayName() +").", false);
					}else{
						maintenancesure = plugin.timetamps;
						plugin.MC.sendTaggedMessage(player, "Voulez-vous vraiment définir le mode maintenance (/modo maintenance pour confirmer) ?", 2, "[MODO]");
					}
					return false;
				}
			}
		}
        
		if (command.getName().toLowerCase().equals("mineworld")) {
			if (subCommand.equals("ok")) {
			    plugin.MC.sendTaggedMessage(player, "Merci, la news est maintenant inscrite comme lu.", 2, "");
			    Integer news_rev = (Integer) plugin.getServerConfig("informations.news_rev", "int");
				plugin.setPlayerConfig(player, "last_news_rev", news_rev);
				return true;
			}else if (subCommand.equals("news")) {
				plugin.MC.sendLastNews(player);
				return true;
			}else if (subCommand.equals("tick")) {
				if(plugin.CC.isClient(player, true)) {
					boolean tick = (Boolean) plugin.getPlayerConfig(player, "message_tick", "boolean");
					if(!tick) {
						plugin.setPlayerConfig(player, "message_tick", true);
						plugin.MC.sendTaggedMessage(player, "Service de tick innactif.", 1, "[TICK]");
					}else{
						plugin.setPlayerConfig(player, "message_tick", false);
					    plugin.MC.sendTaggedMessage(player, "Service de tick actif.", 1, "[TICK]");
					}
				}
				return true;
			}else if (subCommand.equals("removeme")) {
				plugin.setPlayerConfig(player, "remove_me", true);
				player.kickPlayer("Merci de votre visite sur MineWorld.");
				return true;
			/*}else if (subCommand.equals("shop")) {
				if(plugin.CC.isClient(player, true)) {
					shop.shop(player, args);
				}
				return true;
			}else if (subCommand.equals("money")) {
				if(plugin.CC.isClient(player, true)) {
					shop.getmoney(player);
				}
				return true;*/
			}
		}
		return false;
    }
}