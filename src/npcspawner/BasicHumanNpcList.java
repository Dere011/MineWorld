package npcspawner;

import java.util.Collection;
import java.util.HashMap;
import org.bukkit.entity.Entity;

import npcspawner.BasicHumanNpc;

public class BasicHumanNpcList extends HashMap<String, BasicHumanNpc> {
	
	private static final long serialVersionUID = 1L;
    public static BasicHumanNpcList list;
   
    public boolean containsBukkitEntity(Entity entity)
    {
        for(BasicHumanNpc bnpc : this.values())
        {
            if(bnpc.getBukkitEntity().getEntityId() == entity.getEntityId())
                return true;
        }

        return false;
    }

    public BasicHumanNpc getBasicHumanNpc(Entity entity)
    {
        for(BasicHumanNpc bnpc : this.values())
        {
            if(bnpc.getBukkitEntity().getEntityId() == entity.getEntityId())
                return bnpc;
        }

        return null;
    }
    
    public boolean removeAllNpc()
    {
        for(BasicHumanNpc bnpc : this.values())
        {
        	//bnpc.getMCEntity().world.e(bnpc.getMCEntity());
        	bnpc.getMCEntity().world.removeEntity(bnpc.getMCEntity());
        }
        return false;
    }
    
    public Collection<BasicHumanNpc> GetNPCS()
    {
        return this.values();
    }   
    
    public static BasicHumanNpcList GetNPCSlist()
    {
        return list;
    }   
}
