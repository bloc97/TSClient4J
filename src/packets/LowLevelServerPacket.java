/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets;

/**
 *
 * @author bowen
 * @param <E>
 */
public class LowLevelServerPacket implements LowLevelPacket {
    
    public final static int HEADER_LENGTH = 3;
    
    private byte[] raw;
    private long uid;

    public LowLevelServerPacket(byte[] raw) {
        setRaw(raw);
    }
    
    @Override
    public byte[] getRaw() {
        return raw;
    }

    @Override
    public LowLevelServerPacket setRaw(byte[] raw) {
        if (raw.length < getMacLength() + getHeaderLength()) {
            throw new IllegalArgumentException("Unexpected RAW array size!");
        }
        this.raw = raw;
        return this;
    }

    @Override
    public long getUid() {
        return uid;
    }
    
    protected void setInternalPid(short pid) {
        LowLevelPacket.shortToBytes(pid, getRaw(), getMacLength());
    }
    
    @Override
    public LowLevelServerPacket setUid(long uid) {
        this.uid = uid;
        setInternalPid((short)(uid & 0xFFFF));
        return this;
    }

    @Override
    public LowLevelServerPacket setPid(short pid) {
        this.uid = LowLevelPacket.replacePidInUid(pid, uid);
        setInternalPid(pid);
        return this;
    }

    @Override
    public LowLevelServerPacket setGid(int gid) {
        this.uid = LowLevelPacket.replaceGidInUid(gid, uid);
        return this;
    }

    @Override
    public int getHeaderLength() {
        return HEADER_LENGTH;
    }

    @Override
    public LowLevelServerPacket setIsFragmented(boolean isFragmented) {
        return (LowLevelServerPacket) LowLevelPacket.super.setIsFragmented(isFragmented);
    }

    @Override
    public LowLevelServerPacket setIsNewProtocol(boolean isNewProtocol) {
        return (LowLevelServerPacket) LowLevelPacket.super.setIsNewProtocol(isNewProtocol);
    }

    @Override
    public LowLevelServerPacket setIsCompressed(boolean isCompressed) {
        return (LowLevelServerPacket) LowLevelPacket.super.setIsCompressed(isCompressed);
    }

    @Override
    public LowLevelServerPacket setIsUnencrypted(boolean isUnencrypted) {
        return (LowLevelServerPacket) LowLevelPacket.super.setIsUnencrypted(isUnencrypted);
    }

    @Override
    public LowLevelServerPacket setType(PacketType type) {
        return (LowLevelServerPacket) LowLevelPacket.super.setType(type);
    }

    @Override
    public LowLevelServerPacket setPt(byte pt) {
        return (LowLevelServerPacket) LowLevelPacket.super.setPt(pt);
    }

    @Override
    public LowLevelServerPacket setPid(int pid) {
        return (LowLevelServerPacket) LowLevelPacket.super.setPid(pid);
    }

    @Override
    public LowLevelServerPacket setPayload(byte[] payload) {
        return (LowLevelServerPacket) LowLevelPacket.super.setPayload(payload);
    }

    @Override
    public LowLevelServerPacket setHeader(byte[] header) {
        return (LowLevelServerPacket) LowLevelPacket.super.setHeader(header);
    }

    @Override
    public LowLevelServerPacket setMac(byte[] mac) {
        return (LowLevelServerPacket) LowLevelPacket.super.setMac(mac);
    }
    
    


    
    
}
