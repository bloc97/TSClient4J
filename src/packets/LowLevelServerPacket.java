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
public class LowLevelServerPacket<E extends LowLevelServerPacket<E>> implements LowLevelPacket<LowLevelServerPacket<E>> {
    
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
    public E setRaw(byte[] raw) {
        if (raw.length < getMacLength() + getHeaderLength()) {
            throw new IllegalArgumentException("Unexpected RAW array size!");
        }
        this.raw = raw;
        return (E) this;
    }

    @Override
    public long getUid() {
        return uid;
    }
    
    protected void setInternalPid(short pid) {
        getRaw()[getMacLength()    ] = (byte)((uid >> 8) & 0xFF);
        getRaw()[getMacLength() + 1] = (byte)((uid     ) & 0xFF);
    }
    
    @Override
    public E setUid(long uid) {
        this.uid = uid;
        setInternalPid((short)(uid & 0xFFFF));
        return (E) this;
    }

    @Override
    public E setPid(short pid) {
        this.uid = (uid & 0xFFFFFFFFFFFF0000L) | pid;
        setInternalPid(pid);
        return (E) this;
    }

    @Override
    public E setGid(int gid) {
        this.uid = (uid & 0xFFFF00000000FFFFL) | gid;
        return (E) this;
    }

    @Override
    public int getHeaderLength() {
        return HEADER_LENGTH;
    }
    
    


    
    
}
