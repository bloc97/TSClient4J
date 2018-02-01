/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets.payloads;

/**
 *
 * @author bowen
 * @param <E>
 */
public interface Payload {
    public byte[] getRaw();
    public void setRaw(byte[] raw);
    public int length();
}
