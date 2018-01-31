/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets;

/**
 *
 * @author bowen
 * @param <E>
 */
public interface Payload<E extends Payload<E>> {
    public byte[] getRaw();
    public E setRaw(byte[] raw);
    public int length();
}
