package npcspawner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.server.Entity;
import net.minecraft.server.ItemInWorldManager;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

public class NpcSpawner {

    protected static WorldServer GetWorldServer(World world) {
        try {
            CraftWorld w = (CraftWorld) world;
            Field f;
            f = CraftWorld.class.getDeclaredField("world");

            f.setAccessible(true);
            return (WorldServer) f.get(w);

        } catch (Exception e) {
           e.printStackTrace();
        }

        return null;
    }
    
    private static MinecraftServer GetMinecraftServer(Server server) {
        if (server instanceof CraftServer) {
            CraftServer cs = (CraftServer) server;
            Field f;
            try {
                f = CraftServer.class.getDeclaredField("console");
            } catch (NoSuchFieldException ex) {
                return null;
            } catch (SecurityException ex) {
                return null;
            }
            MinecraftServer ms;
            try {
                f.setAccessible(true);
                ms = (MinecraftServer) f.get(cs);
            } catch (IllegalArgumentException ex) {
                return null;
            } catch (IllegalAccessException ex) {
                return null;
            }
            return ms;
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static BasicHumanNpc SpawnBasicHumanNpc(String uniqueId, String name, World world, double x, double y, double z, float yaw, float pitch, Boolean tueur, String skinurl) {
        try {
        	if(name.length() > 20) {
        		return null;
        	}
            WorldServer ws = GetWorldServer(world);
            MinecraftServer ms = GetMinecraftServer(ws.getServer());

            CHumanNpc eh = new CHumanNpc(ms, ws, name, new ItemInWorldManager(ws));
            
            eh.setPositionRotation(x, y, z, yaw, pitch);
            int m = MathHelper.floor(eh.locX / 16.0D);
            int n = MathHelper.floor(eh.locZ / 16.0D);
            
            ws.getChunkAt(m, n).a(eh);
            ws.entityList.add(eh);

            Class params[] = new Class[1];
            params[0] = Entity.class;
            Method method;
            method = net.minecraft.server.World.class.getDeclaredMethod("c", params);
            method.setAccessible(true);
            Object margs[] = new Object[1];
            margs[0] = eh;
            method.invoke(ws, margs);

            return new BasicHumanNpc(eh, uniqueId, name, new Location(world, x, y, z, yaw, pitch), tueur, skinurl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void RemoveBasicHumanNpc(BasicHumanNpc npc) {
        try {
        	npc.getMCEntity().world.removeEntity(npc.getMCEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
