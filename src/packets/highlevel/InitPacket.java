/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets.highlevel;

import packets.HighLevelPacket;
import packets.PacketType;
import packets.payloads.InitPayload;

/**
 *
 * @author bowen
 * @param <E>
 */
public class InitPacket<E extends InitPayload> extends HighLevelPacket<InitPacket<E>, E> {

    private E payload;

    public InitPacket(E payload) {
        this.payload = payload;
    }
    
    @Override
    public E getPayload() {
        return payload;
    }

    @Override
    public InitPacket<E> setPayload(E payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public PacketType getType() {
        return PacketType.INIT;
    }
    
}
