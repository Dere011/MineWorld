package mineworld;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import me.desmin88.mobdisguise.api.*;
import me.taylorkelly.bigbrother.datablock.Chat;

public class Main_EntityListener extends EntityListener {

	private final Main plugin;
	private final Random rand = new Random();
	private Map<Entity, String> damagernodrop = new HashMap<Entity, String>();
	private Map<Entity, String> damagerList = new HashMap<Entity, String>();

    public Main_EntityListener(Main parent) {
    	this.plugin = parent;
    }
    
    public void onEntityDeath(EntityDeathEvent event) {
		if(plugin.move_last.containsKey(event.getEntity())) {
			plugin.move_last.remove(event.getEntity());
		}
		if (event.getEntity() instanceof Player) {
			if(damagerList != null && damagerList.containsKey(event.getEntity())) {
				String type = damagerList.get(event.getEntity());
				String name = ((Player) event.getEntity()).getName();
				if(type == "DROWNING") {
					plugin.MC.sendMessageToAll(name + "vient de se noyer.");
				}else if(type == "FALL") {
					plugin.MC.sendMessageToAll(name + "vient de s'écraser au sol.");
				}else if(type == "SUFFOCATION") {
					plugin.MC.sendMessageToAll(name + "s'est étouffé.");
				}else if(type == "FIRE" || type == "FIRE_TICK") {
					plugin.MC.sendMessageToAll(name + "a pris feu.");
				}
			}
			plugin.CC.sendSoundEffectToAllToLocation(event.getEntity().getLocation(), "http://mineworld.fr/contrib/sound/DeathScream0"+ plugin.D.showRandomInteger(1, 9, rand)+".wav");
			if(MobDisguiseAPI.isDisguised((Player) event.getEntity())) {
				MobDisguiseAPI.undisguisePlayerAsPlayer((Player) event.getEntity(), "zombie");
			}
			if(plugin.PL.is_alco((Player) event.getEntity())) {
				plugin.PL.remove_alco((Player) event.getEntity());
			}
			if(plugin.TC.horde) {
				plugin.H.mort++;
				plugin.H.gouleToPlayer((Player) event.getEntity());
				if(plugin.TC.player_horde.contains(event.getEntity())) {
					plugin.TC.player_horde.remove(event.getEntity());
					if(plugin.D.showRandomInteger(1, 10, rand) == 10) {
						plugin.H.playerTogoule((Player) event.getEntity());
					}
				}
				if(plugin.D.showRandomInteger(1, 10, rand) == 10) {
					plugin.I.drop(event.getEntity().getLocation(), 13374);
				}
				plugin.CC.sendSoundToAll("http://mineworld.fr/contrib/sound/zombiechoir_0"+plugin.D.showRandomInteger(1, 7, rand)+".wav");
			}else{
				if(plugin.D.showRandomInteger(1, 50, rand) == 10) {
					plugin.I.drop(event.getEntity().getLocation(), 13373);
				}
			}
		}
		if(damagerList == null || !damagerList.containsKey(event.getEntity())) return;
		if(damagerList.get(event.getEntity()).contains("NONONO") && plugin.D.showRandomInteger(1, 5, rand) != 5) {
			event.getDrops().clear();
		}
		damagerList.remove(event.getEntity());
    }
    
    public void onEntityDamage(EntityDamageEvent event) {
    	switch(event.getCause()) {
	    	case DROWNING:
	    		damagernodrop.put(event.getEntity(), "DROWNING");
	    		damagerList.put(event.getEntity(), "DROWNING");
	    		break;
	    	case FALL:
	    		damagernodrop.put(event.getEntity(), "FALL");
	    		damagerList.put(event.getEntity(), "FALL");
	    		break;
	    	case SUFFOCATION:
	    		damagernodrop.put(event.getEntity(), "SUFFOCATION");
	    		damagerList.put(event.getEntity(), "SUFFOCATION");
	    		break;
	    	case FIRE:
	    		damagernodrop.put(event.getEntity(), "FIRE");
	    		damagerList.put(event.getEntity(), "FIRE");
	    		break;
	    	case FIRE_TICK:
	    		damagernodrop.put(event.getEntity(), "FIRE_TICK");
	    		damagerList.put(event.getEntity(), "FIRE_TICK");
	    		break;
	    	default:
	    		if(damagerList.get(event.getEntity()) != null) {
	    			damagerList.remove(event.getEntity());
	    		}
    	}
	    if (event instanceof EntityDamageByEntityEvent) {
	    	EntityDamageByEntityEvent dmgByEntity = (EntityDamageByEntityEvent) event;
	    	if (dmgByEntity.getDamager() instanceof Player && dmgByEntity.getEntity() instanceof Player) {
		    	if(plugin.Main_TimeControl.horde && MobDisguiseAPI.isDisguised((Player) dmgByEntity.getDamager()) && !MobDisguiseAPI.isDisguised((Player) dmgByEntity.getEntity())) {
					MobDisguiseAPI.disguisePlayer((Player) dmgByEntity.getEntity(), "zombie");
					plugin.Main_ContribControl.sendSoundToAll("http://mineworld.fr/contrib/sound/critical_event_1.wav");
					plugin.Main_MessageControl.sendTaggedMessageToAll(ChatColor.RED + ((Player) dmgByEntity.getDamager()).getName() + ChatColor.WHITE + " vient de transformer "+ ChatColor.GREEN + ((Player) event.getEntity()).getName() + ChatColor.WHITE + " en goule.", 1, "[HORDE]");
					plugin.Main_MessageControl.sendTaggedMessageToAll(ChatColor.RED + ((Player) event.getEntity()).getName() + " est devenu une goule.", 1, "[HORDE]");
					int pgoule = plugin.conf_player.getInt("load-player."+ ((Player) dmgByEntity.getDamager()).getName() +".psurvie", 0);
					plugin.Main_MessageControl.sendTaggedMessage(((Player) dmgByEntity.getDamager()), "Vous avez reçut "+ ChatColor.GOLD + "1 point"+ ChatColor.WHITE + " de goule.", 1, "");
					plugin.Main_ContribControl.sendNotification(((Player) dmgByEntity.getDamager()), "Bravo !", "+1 GPoint(s)", Material.GOLD_ORE);
					pgoule++;
					plugin.setPlayerConfig(((Player) dmgByEntity.getDamager()), "pgoule", pgoule);
					Chat bb = new Chat((Player) plugin.zombie, ((Player) dmgByEntity.getDamager()).getName() + " vient de transformer "+ ((Player) event.getEntity()).getName() +" en goule.", "world");
					bb.send();
					if(plugin.Main_TimeControl.player_horde.contains(event.getEntity())) {
						plugin.Main_TimeControl.player_horde.remove(event.getEntity());
					}
					plugin.Main_TimeControl.is_goule.add((Player) event.getEntity());
				}
	    	}
        	if (dmgByEntity instanceof Player) {
    	    	if (plugin.Main_Visiteur.is_visiteur((Player) dmgByEntity)) {
    	    		plugin.Main_Visiteur.denied_message((Player) dmgByEntity);
    	    		event.setCancelled(true);
    	    		return;
    	    	}
        	}else if (dmgByEntity.getDamager() instanceof Player) {
    	    	if (plugin.Main_Visiteur.is_visiteur((Player) dmgByEntity.getDamager())) {
    	    		plugin.Main_Visiteur.denied_message((Player) dmgByEntity.getDamager());
    	    		event.setCancelled(true);
    	    		return;
    	    	}
        	}
	    }
    }

    public void onEntityTarget(EntityTargetEvent event) {
    	if (event.getTarget() instanceof Player) {
	    	if (plugin.Main_Visiteur.is_visiteur((Player) event.getTarget()) || (plugin.Main_TimeControl.horde && MobDisguiseAPI.isDisguised((Player) event.getTarget()))) {
	    		event.setCancelled(true);
	    		return;
	    	}
    	}
    }
}