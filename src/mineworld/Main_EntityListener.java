package mineworld;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.inventory.ItemStack;
import me.desmin88.mobdisguise.api.*;
import me.taylorkelly.bigbrother.datablock.Chat;

public class Main_EntityListener extends EntityListener {

	private final Main plugin;
	private final Random rand = new Random();
	private Map<Entity, String> damagerList = new HashMap<Entity, String>();
	public int mort = 0;

    public Main_EntityListener(Main parent) {
    	this.plugin = parent;
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
    
    public void onEntityDeath(EntityDeathEvent event) {
		if(plugin.move_last.containsKey(event.getEntity())) {
			plugin.move_last.remove(event.getEntity());
		}
		if (event.getEntity() instanceof Player) {
			plugin.Main_ContribControl.sendSoundEffectToAllToLocation(event.getEntity().getLocation(), "http://mineworld.fr/contrib/sound/DeathScream0"+showRandomInteger(1, 9, rand)+".wav");
			if(MobDisguiseAPI.isDisguised((Player) event.getEntity())) {
				MobDisguiseAPI.undisguisePlayerAsPlayer((Player) event.getEntity(), "zombie");
			}
			if(plugin.Main_PlayerListener.is_alco((Player) event.getEntity())) {
				plugin.Main_PlayerListener.remove_alco((Player) event.getEntity());
			}
			if(plugin.Main_TimeControl.horde) {
				mort++;
				plugin.Main_ContribControl.sendSoundToAll("http://mineworld.fr/contrib/sound/zombiechoir_0"+showRandomInteger(1, 7, rand)+".wav");
				if(plugin.Main_TimeControl.is_goule.contains((Player) event.getEntity())) {
					plugin.Main_TimeControl.is_goule.add((Player) event.getEntity());
				}
				if(plugin.Main_TimeControl.player_horde.contains(event.getEntity())) {
					plugin.Main_TimeControl.player_horde.remove(event.getEntity());
					if(showRandomInteger(1, 10, rand) == 10) {
						plugin.can_horde.add((Player) event.getEntity());
						plugin.Main_TimeControl.is_goule.add((Player) event.getEntity());
						plugin.Main_ContribControl.sendSoundToAll("http://mineworld.fr/contrib/sound/orch_hit_csharp_short.wav");
						plugin.Main_MessageControl.sendTaggedMessageToAll(ChatColor.GREEN + ((Player) event.getEntity()).getName() + " est devenu une " + ChatColor.RED + " goule " + ChatColor.GREEN + " !", 1, "[HORDE]");
						Chat bb = new Chat((Player) plugin.zombie, ((Player) event.getEntity()).getName() + " est devenu une goule.", "world");
						bb.send();
					}
				}
				if(showRandomInteger(1, 10, rand) == 10) {
					ItemStack cadavre = plugin.Main_ContribControl.sp.getItemManager().getCustomItemStack(13373, 1);
					event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), cadavre);
				}
			}else{
				if(showRandomInteger(1, 50, rand) == 10) {
					ItemStack cadavre = plugin.Main_ContribControl.sp.getItemManager().getCustomItemStack(13372, 1);
					event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), cadavre);
				}
			}
		}
		if(event.getEntity() instanceof Slime) {
			event.getDrops().clear();
		}
		if(damagerList == null || !damagerList.containsKey(event.getEntity())) return;
		if(damagerList.get(event.getEntity()).contains("NONONO") && showRandomInteger(1, 5, rand) != 5) {
			event.getDrops().clear();
		}
		damagerList.remove(event.getEntity());
    }
    
    public void onEntityDamage(EntityDamageEvent event) {
    	if (event.getEntity() instanceof Creature) {
	    	switch(event.getCause()) {
		    	case DROWNING:
		    		damagerList.put(event.getEntity(), "NONONO");
		    		break;
		    	case FALL:
		    		damagerList.put(event.getEntity(), "NONONO");
		    		break;
		    	case SUFFOCATION:
		    		damagerList.put(event.getEntity(), "NONONO");
		    		break;
		    	case FIRE:
		    		damagerList.put(event.getEntity(), "NONONO");
		    		break;
		    	case FIRE_TICK:
		    		damagerList.put(event.getEntity(), "NONONO");
		    		break;
		    	default:
		    		if(damagerList.get(event.getEntity()) != null) {
		    			damagerList.remove(event.getEntity());
		    		}
	    	}
	    	if (event.getEntity() instanceof Slime) {
	    		damagerList.put(event.getEntity(), "NONONO");
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