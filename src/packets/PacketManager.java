/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.PriorityBlockingQueue;
import testtscomm.crypto.CryptoUtils;

/**
 *
 * @author bowen
 */
public class PacketManager {
    
    private long[] clientUidCount = new long[PacketType.values().length];
    private long[] serverUidCount = new long[PacketType.values().length];
    
    private final ConcurrentSkipListMap<Long, LowLevelClientPacket> waitingAckMap = new ConcurrentSkipListMap<>();
    private final ConcurrentSkipListMap<Long, Integer> waitingAckResendCount = new ConcurrentSkipListMap<>();
    private final PriorityBlockingQueue<LowLevelServerPacket> fragmentedReceiveQueue = new PriorityBlockingQueue<>();
    
    public LowLevelClientPacket encode(HighLevelPacket packet) {
        LowLevelClientPacket lowPacket = new LowLevelClientPacket();
        
        if (packet.getType() == PacketType.INIT) {
            lowPacket.setMac(CryptoUtils.TS3INIT_MAC);
            lowPacket.setPid(101).setCid(0).setType(PacketType.INIT);
            lowPacket.setPayload(packet.getPayload().getRaw());
            return lowPacket;
        }
        return lowPacket;
    }
    
    
}
