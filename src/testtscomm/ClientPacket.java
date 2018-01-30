/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm;

/**
 *
 * @author bowen
 */
public class ClientPacket implements LowLevelPacket {
    
    private final byte[] raw;
    
    public ClientPacket(byte[] mac, short pid, short cid, byte pt, byte[] payload) {
        raw = new byte[payload.length + 13];
        
        for (int i=0; i<8; i++) {
            raw[i] = mac[i];
        }
        raw[8] = (byte)(pid >> 4);
        raw[9] = (byte)(pid & 0xF);
        
        raw[10] = (byte)(cid >> 4);
        raw[11] = (byte)(cid & 0xF);
        
        raw[12] = pt;
        
        for (int i=0; i<payload.length; i++) {
            raw[i+13] = payload[i];
        }
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }
    
    
}
