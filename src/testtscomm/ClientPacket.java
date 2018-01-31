/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm;

import packets.LowLevelPacket;
import testtscomm.crypto.CryptoUtils;

/**
 *
 * @author bowen
 */
public class ClientPacket implements LowLevelPacket {
    
    private byte[] raw;
    private final int gid;
    
    public ClientPacket(byte[] mac, short pid, short gid, short cid, byte pt, byte[] payload) {
        raw = new byte[payload.length + CryptoUtils.CLIENTHEADER_LENGTH];
        
        for (int i=0; i<8; i++) {
            raw[i] = mac[i];
        }
        raw[8] = (byte)(pid >> 4);
        raw[9] = (byte)(pid & 0xF);
        
        raw[10] = (byte)(cid >> 4);
        raw[11] = (byte)(cid & 0xF);
        
        raw[12] = pt;
        
        for (int i=0; i<payload.length; i++) {
            raw[i+CryptoUtils.CLIENTHEADER_LENGTH] = payload[i];
        }
        
        this.gid = gid;
    }

    @Override
    public byte[] getRaw() {
        return raw;
    }

    @Override
    public byte[] getPayloadCopy() {
        byte[] payload = new byte[raw.length - CryptoUtils.CLIENTHEADER_LENGTH];
        System.arraycopy(raw, CryptoUtils.CLIENTHEADER_LENGTH, payload, 0, payload.length);
        return payload;
    }

    @Override
    public byte[] getMacCopy() {
        byte[] mac = new byte[8];
        System.arraycopy(raw, 0, mac, 0, 8);
        return mac;
    }

    @Override
    public short getPid() {
        return (short)((raw[8] << 4) | (raw[9]));
    }
    
    public short getCid() {
        return (short)((raw[10] << 4) | (raw[11]));
    }

    @Override
    public byte getPt() {
        return raw[12];
    }

    @Override
    public int getGid() {
        return gid;
    }
    
    @Override
    public byte[] getHeaderCopy() {
        byte[] header = new byte[CryptoUtils.CLIENTHEADER_LENGTH - CryptoUtils.MAC_LENGTH];
        System.arraycopy(raw, CryptoUtils.MAC_LENGTH, header, 0, header.length);
        return header;
    }
    
    @Override
    public void setRaw(byte[] raw) {
        this.raw = raw;
    }
    
    
}
