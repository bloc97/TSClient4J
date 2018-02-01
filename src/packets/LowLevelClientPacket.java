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
public class LowLevelClientPacket extends LowLevelServerPacket {

    public final static int HEADER_LENGTH = 5;

    public LowLevelClientPacket(byte[] raw) {
        super(raw);
    }

    @Override
    public int getHeaderLength() {
        return HEADER_LENGTH;
    }
    
    public short getCid() {
        int startIndex = getMacLength() + 2;
        return LowLevelPacket.bytesToShort(getRaw(), startIndex);
    }
    
    public LowLevelClientPacket setCid(short cid) {
        int startIndex = getMacLength() + 2;
        LowLevelPacket.shortToBytes(cid, getRaw(), startIndex);
        return this;
    }

    @Override
    public LowLevelClientPacket setMac(byte[] mac) {
        return (LowLevelClientPacket) super.setMac(mac);
    }

    @Override
    public LowLevelClientPacket setHeader(byte[] header) {
        return (LowLevelClientPacket) super.setHeader(header);
    }

    @Override
    public LowLevelClientPacket setPayload(byte[] payload) {
        return (LowLevelClientPacket) super.setPayload(payload);
    }

    @Override
    public LowLevelClientPacket setPid(int pid) {
        return (LowLevelClientPacket) super.setPid(pid);
    }

    @Override
    public LowLevelClientPacket setPt(byte pt) {
        return (LowLevelClientPacket) super.setPt(pt);
    }

    @Override
    public LowLevelClientPacket setType(PacketType type) {
        return (LowLevelClientPacket) super.setType(type);
    }

    @Override
    public LowLevelClientPacket setIsUnencrypted(boolean isUnencrypted) {
        return (LowLevelClientPacket) super.setIsUnencrypted(isUnencrypted);
    }

    @Override
    public LowLevelClientPacket setIsCompressed(boolean isCompressed) {
        return (LowLevelClientPacket) super.setIsCompressed(isCompressed);
    }

    @Override
    public LowLevelClientPacket setIsNewProtocol(boolean isNewProtocol) {
        return (LowLevelClientPacket) super.setIsNewProtocol(isNewProtocol);
    }

    @Override
    public LowLevelClientPacket setIsFragmented(boolean isFragmented) {
        return (LowLevelClientPacket) super.setIsFragmented(isFragmented);
    }

    @Override
    public LowLevelClientPacket setPid(short pid) {
        return (LowLevelClientPacket) super.setPid(pid);
    }

    @Override
    public LowLevelClientPacket setUid(long uid) {
        return (LowLevelClientPacket) super.setUid(uid);
    }

    @Override
    public LowLevelClientPacket setRaw(byte[] raw) {
        return (LowLevelClientPacket) super.setRaw(raw);
    }

    @Override
    public LowLevelClientPacket setGid(int gid) {
        return (LowLevelClientPacket) super.setGid(gid);
    }
    
    
    
    
    
}
