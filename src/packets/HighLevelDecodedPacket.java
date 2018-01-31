/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets;

import static packets.LowLevelPacket.MAC_LENGTH;

/**
 *
 * @author bowen
 * @param <E>
 * @param <F>
 */
public interface HighLevelDecodedPacket<E extends HighLevelDecodedPacket<E, F>, F extends Payload<F>> {
    
    public F getPayload();
    public E setPayload(F payload);
    
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
    
    public LowLevelPacket.Type getType();
    public E setType(LowLevelPacket.Type type);
    
    public boolean isToBeUnencrypted();
    public E setIsToBeEncrypted(boolean b);
    public boolean isToBeCompressed();
    public E setToBeCompressed(boolean b);
    public boolean isToBeNewProtocol();
    public E setToBeNewProtocol(boolean b);
    
}
