/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets;

import packets.payloads.Payload;

/**
 * Note: High level packets are the raw, unencrypted or decoded data from low level packets.
 * @author bowen
 * @param <E>
 * @param <F>
 */
public abstract class HighLevelPacket<E extends HighLevelPacket<E, F>, F extends Payload> {
    
    private long uid = 0;
    private boolean[] flags = new boolean[3];
    
    public abstract F getPayload();
    public abstract E setPayload(F payload);
    
    public long getUid() {
        return uid;
    }
    public short getPid() {
        return (short)(getUid() & 0xFFFF);
    }
    public int getGid() {
        return (int)((getUid() >> 16) & 0xFFFFFFFF);
    }
    
    public E setUid(long uid) {
        this.uid = uid;
        return (E) this;
    }
    public E setPid(short pid) {
        this.uid = LowLevelPacket.replacePidInUid(pid, uid);
        return (E) this;
    }
    public E setPid(int pid) {
        return setPid((short) (pid & 0xFFFF));
    }
    public E setGid(int gid) {
        this.uid = LowLevelPacket.replaceGidInUid(gid, uid);
        return (E) this;
    }
    
    public abstract PacketType getType();
    //public abstract E setType(LowLevelPacket.Type type);
    
    public boolean isToBeUnencrypted() {
        return flags[0];
    }
    public E setIsToBeEncrypted(boolean b) {
        flags[0] = b;
        return (E) this;
    }
    public boolean isToBeCompressed() {
        return flags[1];
    }
    public E setToBeCompressed(boolean b) {
        flags[1] = b;
        return (E) this;
    }
    public boolean isToBeNewProtocol() {
        return flags[2];
    }
    public E setToBeNewProtocol(boolean b) {
        flags[2] = b;
        return (E) this;
    }
    
}
