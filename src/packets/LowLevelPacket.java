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
public interface LowLevelPacket {
    public static final int MAC_LENGTH = 8;
    
    public default void initEmptyRaw() {
        initEmptyRaw(0);
    }
    
    public default void initEmptyRaw(int payloadSize) {
        setRaw(new byte[getMacLength() + getHeaderLength() + payloadSize]);
    }
    
    public byte[] getRaw();
    
    public LowLevelPacket setRaw(byte[] raw);
    
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
    
    public default LowLevelPacket setMac(byte[] mac) {
        if (mac.length != getMacLength()) {
            throw new IllegalArgumentException("Unexpected MAC array size!");
        }
        System.arraycopy(mac, 0, getRaw(), 0, mac.length);
        return this;
    }
    public default LowLevelPacket setHeader(byte[] header) {
        if (header.length != getHeaderLength()) {
            throw new IllegalArgumentException("Unexpected Header array size!");
        }
        System.arraycopy(header, 0, getRaw(), getMacLength(), header.length);
        return this;
    }
    public default LowLevelPacket setPayload(byte[] payload) {
        int totalHeaderLength = getMacLength() + getHeaderLength();
        if (payload.length != getPayloadLength()) {
            byte[] raw = getRaw();
            byte[] newRaw = new byte[totalHeaderLength + payload.length];
            System.arraycopy(raw, 0, newRaw, 0, totalHeaderLength);
            setRaw(newRaw);
        }
        System.arraycopy(payload, 0, getRaw(), totalHeaderLength, payload.length);
        return this;
    }
    
    public long getUid();
    public default short getPid() {
        return (short)(getUid() & 0xFFFF);
    }
    public default int getGid() {
        return (int)((getUid() >> 16) & 0xFFFFFFFF);
    }
    
    public LowLevelPacket setUid(long uid);
    public LowLevelPacket setPid(short pid);
    public default LowLevelPacket setPid(int pid) {
        return setPid((short) (pid & 0xFFFF));
    }
    public LowLevelPacket setGid(int gid);
    
    public default byte getPt() {
        return getRaw()[getMacLength() + getHeaderLength() - 1];
    }
    public default LowLevelPacket setPt(byte pt) {
        getRaw()[getMacLength() + getHeaderLength() - 1] = pt;
        return this;
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
    
    public default PacketType getType() {
        int index = getPt() & 0xF;
        if (index >= PacketType.values().length) {
            throw new IllegalStateException("Packet type is undefined!");
        }
        return PacketType.values()[index];
    }
    public default LowLevelPacket setType(PacketType type) {
        PacketType[] types = PacketType.values();
        for (int i=0; i<types.length; i++) {
            if (types[i].equals(type)) {
                byte newPt = (byte)((getPt() & 0xF0) | (i & 0xF));
                setPt(newPt);
                return this;
            }
        }
        throw new IllegalStateException("Unexpected error when searching for packet type!");
    }
    
    public default boolean isUnencrypted() {
        return retrievePtBit(getPt(), 7);
    }
    public default LowLevelPacket setIsUnencrypted(boolean isUnencrypted) {
        setPtBit(this, isUnencrypted, 7);
        return this;
    }
    public default boolean isCompressed() {
        return retrievePtBit(getPt(), 6);
    }
    public default LowLevelPacket setIsCompressed(boolean isCompressed) {
        setPtBit(this, isCompressed, 6);
        return this;
    }
    public default boolean isNewProtocol() {
        return retrievePtBit(getPt(), 5);
    }
    public default LowLevelPacket setIsNewProtocol(boolean isNewProtocol) {
        setPtBit(this, isNewProtocol, 5);
        return this;
    }
    public default boolean isFragmented() {
        return retrievePtBit(getPt(), 4);
    }
    public default LowLevelPacket setIsFragmented(boolean isFragmented) {
        setPtBit(this, isFragmented, 4);
        return this;
    }
    
    public static long replacePidInUid(short pid, long uid) {
        return (uid & 0xFFFFFFFFFFFF0000L) | pid;
    }
    public static long replaceGidInUid(int gid, long uid) {
        return (uid & 0xFFFF00000000FFFFL) | gid;
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
    
    public static boolean retrievePtBit(byte pt, int pos) {
        return ((pt >> pos) & 1) == 1;
    }
    
    public static short bytesToShort(byte msb, byte lsb) {
        return (short)(((msb & 0xFF) << 8) | (lsb & 0xFF));
    }
    
    public static short bytesToShort(byte[] data, int startOffset) {
        return bytesToShort(data, startOffset, true);
    }
    public static short bytesToShort(byte[] data, int startOffset, boolean isBigEndian) {
        if (isBigEndian) {
            return bytesToShort(data[startOffset], data[startOffset + 1]);
        } else {
            return bytesToShort(data[startOffset + 1], data[startOffset]);
        }
    }
    
    public static void shortToBytes(short value, byte[] data, int startOffset) {
        shortToBytes(value, data, startOffset, true);
    }
    public static void shortToBytes(short value, byte[] data, int startOffset, boolean isBigEndian) {
        if (isBigEndian) {
            data[startOffset    ] = (byte)((value >> 8) & 0xFF);
            data[startOffset + 1] = (byte)((value     ) & 0xFF);
        } else {
            data[startOffset    ] = (byte)((value     ) & 0xFF);
            data[startOffset + 1] = (byte)((value >> 8) & 0xFF);
        }
    }
}
