/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets;

import packets.highlevel.InitPacket;
import packets.payloads.InitPayload;
import packets.payloads.handshake.HandShakePayload0;
import packets.payloads.handshake.HandShakePayload1;
import packets.payloads.handshake.HandShakePayload3;
import testtscomm.crypto.CryptoUtils;

/**
 *
 * @author bowen
 */
public abstract class HandshakeUtils {
    
    public static LowLevelClientPacket encode(InitPayload payload) {
        LowLevelClientPacket lowPacket = new LowLevelClientPacket();
        
        lowPacket.setMac(CryptoUtils.TS3INIT_MAC);
        lowPacket.setPid(101).setCid(0).setType(PacketType.INIT);
        lowPacket.setPayload(payload.getRaw());
        return lowPacket;
    }
    
    
    public static InitPacket<HandShakePayload1> decode1(LowLevelServerPacket packet, HandShakePayload0 payload) {
        InitPacket<HandShakePayload1> highPacket = new InitPacket(new HandShakePayload1(packet.getPayloadCopy(), payload));
        return highPacket;
    }
    
    public static InitPacket<HandShakePayload1> decode3(LowLevelServerPacket packet) {
        InitPacket<HandShakePayload1> highPacket = new InitPacket(new HandShakePayload3(packet.getPayloadCopy()));
        return highPacket;
    }
    
}
