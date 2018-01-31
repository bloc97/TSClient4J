/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm;

import testtscomm.crypto.CryptoUtils;

/**
 *
 * @author bowen
 */
public class ServerPacket implements LowLevelPacket {
    
    private byte[] raw;
    private final int gid;
    
    public ServerPacket(int payloadLength, int gid) {
        this(new byte[payloadLength + CryptoUtils.SERVERHEADER_LENGTH], gid);
    }
    public ServerPacket(byte[] data, int gid) {
        raw = data;
        this.gid = gid;
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }

    @Override
    public byte[] getPayload() {
        byte[] payload = new byte[raw.length - CryptoUtils.SERVERHEADER_LENGTH];
        System.arraycopy(raw, CryptoUtils.SERVERHEADER_LENGTH, payload, 0, payload.length);
        return payload;
    }

    @Override
    public byte[] getMac() {
        byte[] mac = new byte[8];
        System.arraycopy(raw, 0, mac, 0, 8);
        return mac;
    }

    @Override
    public short getPid() {
        return (short)((raw[8] << 4) | (raw[9]));
    }

    @Override
    public byte getPt() {
        return raw[10];
    }
    
    @Override
    public void setRaw(byte[] raw) {
        this.raw = raw;
    }

    @Override
    public int getGid() {
        return gid;
    }
    
    
    @Override
    public byte[] getHeader() {
        byte[] header = new byte[CryptoUtils.SERVERHEADER_LENGTH];
        System.arraycopy(raw, 0, header, 0, header.length);
        return header;
    }
    
}
