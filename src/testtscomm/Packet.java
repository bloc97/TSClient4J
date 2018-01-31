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
public interface Packet {
    
    public long getUid();
    public short getPid();
    public int getGid();
    
    public void setUid(long uid);
    public void setPid(long pid);
    public void setGid(long gid);
    
    public byte getPt();
    public void setPt(byte pt);
    
    
    public byte[] getRaw();
    public void setRaw(byte[] raw);
    
    public byte[] getMacCopy();
    public byte[] getHeaderCopy();
    public byte[] getPayloadCopy();
}
