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
public class ServerPacket {
    
    private final byte[] raw;
    
    public ServerPacket(int expectedLength) {
        this(new byte[expectedLength + 11]);
    }
    public ServerPacket(byte[] data) {
        raw = data;
    }

    public byte[] getRaw() {
        return raw;
    }

    public byte[] getPayload() {
        byte[] payload = new byte[raw.length - 11];
        System.arraycopy(raw, 11, payload, 0, payload.length);
        return payload;
    }

    public byte[] getMac() {
        byte[] mac = new byte[8];
        System.arraycopy(raw, 0, mac, 0, 8);
        return mac;
    }

    public short getPid() {
        return (short)((raw[8] << 4) | (raw[9]));
    }

    public byte getPt() {
        return raw[10];
    }
    
    
}
