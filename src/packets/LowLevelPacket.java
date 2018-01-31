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
public interface LowLevelPacket<E extends LowLevelPacket<E>> {
    public static final int MAC_LENGTH = 8;
    public static enum Type {
        VOICE, WHISPER, COMMAND, COMMANDLOW, PING, PONG, ACK, ACKLOW, INIT
    }
    
    public byte[] getRaw();
    
    public E setRaw(byte[] raw);
    
    public default byte[] getMacCopy() {
        byte[] mac = new byte[getMacLength()];
        System.arraycopy(getRaw(), 0, mac, 0, mac.length);
        return mac;
    }
    public default byte[] getHeaderCopy() {
        byte[] header = new byte[getHeaderLength()];
        System.arraycopy(getRaw(), getMacLength(), header, 0, header.length);
        return header;
    }
    public default byte[] getPayloadCopy() {
        byte[] payload = new byte[getPayloadLength()];
        System.arraycopy(getRaw(), getMacLength() + getHeaderLength(), payload, 0, payload.length);
        return payload;
    }
    
    public default E setMac(byte[] mac) {
        if (mac.length != getMacLength()) {
            throw new IllegalArgumentException("Unexpected MAC array size!");
        }
        System.arraycopy(mac, 0, getRaw(), 0, mac.length);
        return (E) this;
    }
    public default E setHeader(byte[] header) {
        if (header.length != getHeaderLength()) {
            throw new IllegalArgumentException("Unexpected Header array size!");
        }
        System.arraycopy(header, 0, getRaw(), getMacLength(), header.length);
        return (E) this;
    }
    public default E setPayload(byte[] payload) {
        int totalHeaderLength = getMacLength() + getHeaderLength();
        if (payload.length != getPayloadLength()) {
            byte[] raw = getRaw();
            byte[] newRaw = new byte[totalHeaderLength + payload.length];
            System.arraycopy(raw, 0, newRaw, 0, totalHeaderLength);
            setRaw(newRaw);
        }
        System.arraycopy(payload, 0, getRaw(), totalHeaderLength, payload.length);
        return (E) this;
    }
    
    public long getUid();
    public default short getPid() {
        return (short)(getUid() & 0xFFFF);
    }
    public default int getGid() {
        return (int)((getUid() >> 16) & 0xFFFFFFFF);
    }
    
    public E setUid(long uid);
    public E setPid(short pid);
    public default E setPid(int pid) {
        return setPid((short) (pid & 0xFFFF));
    }
    public E setGid(int gid);
    
    public default byte getPt() {
        return getRaw()[getMacLength() + getHeaderLength() - 1];
    }
    public default E setPt(byte pt) {
        getRaw()[getMacLength() + getHeaderLength() - 1] = pt;
        return (E) this;
    }
    
    public default int length() {
        return getRaw().length;
    }
    public default int getMacLength() {
        return MAC_LENGTH;
    }
    public int getHeaderLength();
    public default int getPayloadLength() {
        return length() - (getMacLength() + getHeaderLength());
    }
    
    public default Type getType() {
        int index = getPt() & 0xF;
        if (index >= Type.values().length) {
            throw new IllegalStateException("Packet type is undefined!");
        }
        return Type.values()[index];
    }
    public default E setType(Type type) {
        Type[] types = Type.values();
        for (int i=0; i<types.length; i++) {
            if (types[i].equals(type)) {
                byte newPt = (byte)((getPt() & 0xF0) | (i & 0xF));
                setPt(newPt);
                return (E) this;
            }
        }
        throw new IllegalStateException("Unexpected error when searching for packet type!");
    }
    
    public static void setPtBit(LowLevelPacket packet, boolean b, int pos) {
        byte newPt;
        if (b) {
            newPt = (byte)(packet.getPt() |  (1 << pos));
        } else {
            newPt = (byte)(packet.getPt() & ~(1 << pos));
        }
        packet.setPt(newPt);
    }
    
    public default boolean isUnencrypted() {
        return ((getPt() >> 7) & 1) == 1;
    }
    public default E setIsUnencrypted(boolean isUnencrypted) {
        setPtBit(this, isUnencrypted, 7);
        return (E) this;
    }
    public default boolean isCompressed() {
        return ((getPt() >> 6) & 1) == 1;
    }
    public default E setIsCompressed(boolean isCompressed) {
        setPtBit(this, isCompressed, 6);
        return (E) this;
    }
    public default boolean isNewProtocol() {
        return ((getPt() >> 5) & 1) == 1;
    }
    public default E setIsNewProtocol(boolean isNewProtocol) {
        setPtBit(this, isNewProtocol, 5);
        return (E) this;
    }
    public default boolean isFragmented() {
        return ((getPt() >> 4) & 1) == 1;
    }
    public default E setIsFragmented(boolean isFragmented) {
        setPtBit(this, isFragmented, 4);
        return (E) this;
    }
}
