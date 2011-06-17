package mineworld;

import org.bukkit.ChunkSnapshot;
import org.bukkit.block.Biome;

public class Main_ChunkCopy implements ChunkSnapshot {
    private final int x, z;
    private final String worldname;
    private final byte[] buf; // Flat buffer in uncompressed chunk file format
    private final byte[] hmap; // Height map
    private final long capture_fulltime;

    private static final int BLOCKDATA_OFF = 32768;
    private static final int BLOCKLIGHT_OFF = BLOCKDATA_OFF + 16384;
    private static final int SKYLIGHT_OFF = BLOCKLIGHT_OFF + 16384;

    Main_ChunkCopy(int x, int z, String wname, long wtime, byte[] buf, byte[] hmap) {
        this.x = x;
        this.z = z;
        this.worldname = wname;
        this.capture_fulltime = wtime;
        this.buf = buf;
        this.hmap = hmap;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
    
    public byte[] getdata() {
        return buf;
    }

    public String getWorldName() {
        return worldname;
    }
    
    public boolean a(int i, int j, int k, int l) {
        byte b0 = (byte) l;
        buf[i << 11 | k << 7 | j] = (byte) (b0 & 255);
        return true;
    }
    
    public boolean setRawTypeId(int i, int j, int k, int l) {
        if (i >= -32000000 && k >= -32000000 && i < 32000000 && k <= 32000000) {
            if (j < 0) {
                return false;
            } else if (j >= 128) {
                return false;
            } else {
                return this.a(i & 15, j, k & 15, l);
            }
        } else {
            return false;
        }
    }
    
    public int getBlockTypeId(int x, int y, int z) {
        return buf[x << 11 | z << 7 | y] & 255;
    }

    public int getBlockData(int x, int y, int z) {
        int off = ((x << 10) | (z << 6) | (y >> 1)) + BLOCKDATA_OFF;

        return ((y & 1) == 0) ? (buf[off] & 0xF) : ((buf[off] >> 4) & 0xF);
    }

    public int getBlockSkyLight(int x, int y, int z) {
        int off = ((x << 10) | (z << 6) | (y >> 1)) + SKYLIGHT_OFF;

        return ((y & 1) == 0) ? (buf[off] & 0xF) : ((buf[off] >> 4) & 0xF);
    }

    public int getBlockEmittedLight(int x, int y, int z) {
        int off = ((x << 10) | (z << 6) | (y >> 1)) + BLOCKLIGHT_OFF;

        return ((y & 1) == 0) ? (buf[off] & 0xF) : ((buf[off] >> 4) & 0xF);
    }

    public int getHighestBlockYAt(int x, int z) {
        return hmap[z << 4 | x] & 255;
    }

    public long getCaptureFullTime() {
        return capture_fulltime;
    }

	@Override
	public Biome getBiome(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getRawBiomeRainfall(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getRawBiomeTemperature(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
}