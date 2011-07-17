package mineworld;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Main_MessageControl {

	Main plugin;
	
    public Main_MessageControl(Main parent) {
    	plugin = parent;
    }
    
    public String[] createstrings(int number) {
	   	 String[] anTxt;
	   	 anTxt = new String[number];
	   	 return anTxt;
    }
    
    public void sendTaggedMessage(Player p, String txt, int type, String extratag) {
    	 String[] anTxt = createstrings(1);
	   	 anTxt[0] = txt;
	   	 sendTaggedMessage(p, anTxt, type, extratag);
	}
    
    public String getTag(Player p) {
    	String mineworldtag = "";
		mineworldtag = ChatColor.GOLD + "[" + ChatColor.DARK_AQUA +"Mine"+ ChatColor.DARK_GREEN + "World" + ChatColor.GOLD + "]" + ChatColor.WHITE;
		return mineworldtag;
    }
   
	public void sendTaggedMessage(Player p, String[] txt, int type, String extratag) {
		String mineworldtag;
		if(extratag == "") {
			mineworldtag = getTag(p);
		}else{
			mineworldtag = ChatColor.GOLD + extratag;
		}
		if(type == 1) {
			for (String t : txt) {
				p.sendMessage(mineworldtag + ChatColor.WHITE + " " + t);
			}
		}else if(type == 2) {
			p.sendMessage("");
			p.sendMessage(ChatColor.GOLD + "################## "+ mineworldtag +" "+ ChatColor.GOLD +"##################");
			for (String t : txt) {
				p.sendMessage(ChatColor.WHITE + t);
			}
		}
	}
	
    public void sendTaggedMessageToAll(String[] txt, int type, String extratag) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOnline()) {
				sendTaggedMessage(p, txt, type, extratag);
			}
		}
    }
    
    public void sendTaggedMessageToAll(String txt, int type, String extratag) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOnline()) {
				sendTaggedMessage(p, txt, type, extratag);
			}
		}
    }
    
    public void sendMessageToAll(String txt) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOnline()) {
				p.sendMessage(txt);
			}
		}
    }
    
    public void chatMessage(Player player, Player playerchat, String message, String extratag) {
    	if(plugin.Main_ContribControl.isClient(player, false)) {
			boolean tick = (Boolean) plugin.getPlayerConfig(player, "message_tick", "boolean");
			if(!tick) {
				plugin.Main_ContribControl.sendPlayerSoundEffect(player, "http://mineworld.fr/contrib/sound/beepclear.wav");
			}
    	}
    	Boolean is_modo = ((Boolean) plugin.modo.contains(playerchat.getName()));
    	Boolean is_correct = ((Boolean) plugin.correct.contains(playerchat.getName()));
    	Boolean is_anim = ((Boolean) plugin.anim.contains(playerchat.getName()));
    	Boolean is_admin = ((Boolean) playerchat.isOp());
    	String tag = "";
    	if(extratag == "") {
    		if(plugin.Main_ContribControl.isClient(playerchat, false)) {
    			tag = ChatColor.DARK_GREEN + "[MEMBRE" + ChatColor.GOLD + "+" + ChatColor.DARK_GREEN + "]";
    		}else{
    			tag = ChatColor.DARK_GREEN + "[MEMBRE]";
    		}
    	}else{
    		tag = ChatColor.GOLD + extratag;
    	}
    	String prepseudo = "" + ChatColor.WHITE;
    	if(is_modo) {
    		tag = ChatColor.GREEN + "[MODO]";
    	}else if(is_anim) {
    		tag = ChatColor.WHITE + "[ANIM]";
    	}else if(is_correct) {
        	tag = ChatColor.DARK_PURPLE + "[CORREC]";
    	}else if(is_admin) {
    		tag = ChatColor.RED + "[ADMIN]";
    		prepseudo = "" + ChatColor.GOLD;
    	}
    	if(plugin.Main_ContribControl.isClient(playerchat, false)) {
	    	int color = (Integer) plugin.getPlayerConfig(playerchat, "plus.color", "int");
	    	if(color != 0) {
	    		if(color == 1) {
	    			prepseudo = "" + ChatColor.BLUE;
	    		}else if(color == 2) {
	    			prepseudo = "" + ChatColor.GREEN;
	    		}else if(color == 3) {
	    			prepseudo = "" + ChatColor.YELLOW;
	    		}
	    	}
    	}
    	player.sendMessage(tag + " " + ChatColor.WHITE + "" + prepseudo + "" + playerchat.getName() + ChatColor.WHITE + " > " + message);
    }

    public void chatMessageToAll(Player player, String txt) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOnline()) {
				chatMessage(p, player, txt, "");
			}
		}
    }
    
    public void chatMessageToAllVisiteur(Player player, String txt) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOnline() && (plugin.correct.contains(p.getName()) || plugin.anim.contains(p.getName()) || plugin.modo.contains(p.getName()) || p.isOp() || plugin.Main_Visiteur.is_visiteur(p))) {
				chatMessage(p, player, txt, "[VISITEUR]");
			}
		}
    }
    
    public void chatMessageToAllNonVisiteur(Player player, String txt) {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if(p.isOnline() && !plugin.Main_Visiteur.is_visiteur(p)) {
				chatMessage(p, player, txt, "");
			}
		}
    }
    
    public void sendSmallNotRegisteredMsg(Player p) {
    	sendTaggedMessage(p, "Votre compte n'est pas débloquer, '/mineworld guide' pour en savoir plus.", 1, "[GUIDE]");
    }

    public void sendNotRegisteredMsg(Player p) {
    	String mineworldtag = getTag(p);
		p.sendMessage(ChatColor.GOLD + "################## "+mineworldtag+" "+ ChatColor.GOLD +"##################");
    	p.sendMessage(ChatColor.DARK_GRAY + "Vous n'avez pas encore débloquer votre compte MineWorld.");
    	p.sendMessage(ChatColor.DARK_GRAY + "Merci de vous rendre sur notre site pour lire le guide MineWorld.");
		p.sendMessage(ChatColor.DARK_GRAY + "La commande pour DEBLOQUER votre compte seras fourni SUR le guide.");
    }
    
    public void sendVisiteurMsg(Player p) {
    	String mineworldtag = getTag(p);
		p.sendMessage(ChatColor.GOLD + "################## "+mineworldtag+" "+ ChatColor.GOLD +"##################");
    	p.sendMessage(ChatColor.DARK_GRAY + "Votre compte est en accès limité (Compte Visiteur).");
    	p.sendMessage(ChatColor.DARK_GRAY + "Les visiteurs ne peuvent pas voir les membres et inversement.");
    	p.sendMessage(ChatColor.DARK_GRAY + "Le chat du serveur est séparé pour les deux groupes.");
    	p.sendMessage(ChatColor.DARK_GRAY + "L'inscription WhiteList est disponible sur notre site.");
    	p.sendMessage(ChatColor.DARK_GRAY + "Bonne visite sur MineWorld.");
    }
    
    public void sendSmallLastNews(Player p) {
    	sendTaggedMessage(p, "Une news est disponible, '/mineworld news' pour en savoir plus.", 1, "[NEWS]");
    }
    
    public void sendLastNews(Player p) {
    	String news = (String) plugin.getServerConfig("informations.news", "string");
    	String mineworldtag = getTag(p);
    	p.sendMessage(ChatColor.GOLD + "################## "+mineworldtag+" "+ ChatColor.GOLD +"##################");
		p.sendMessage(ChatColor.DARK_AQUA + news);
		p.sendMessage(ChatColor.DARK_GREEN + "<< /mineworld ok >>"+ ChatColor.DARK_AQUA + " pour confirmer la lecture.");
    }
}