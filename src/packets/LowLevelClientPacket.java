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
public class LowLevelClientPacket<E extends LowLevelClientPacket<E>> extends LowLevelServerPacket<LowLevelClientPacket<E>> {

    public final static int HEADER_LENGTH = 5;

    public LowLevelClientPacket(byte[] raw) {
        super(raw);
    }

    @Override
    public int getHeaderLength() {
        return HEADER_LENGTH;
    }
    
    public short getCid() {
        int startIndex = getMacLength() + getHeaderLength() + 2;
        return (short)((getRaw()[startIndex] << 8) | getRaw()[startIndex + 1]);
    }
    
    public E setCid(short cid) {
        int startIndex = getMacLength() + getHeaderLength() + 2;
        getRaw()[startIndex    ] = (byte)((cid >> 8) & 0xFF);
        getRaw()[startIndex + 1] = (byte)((cid     ) & 0xFF);
        return (E) this;
    }
}
