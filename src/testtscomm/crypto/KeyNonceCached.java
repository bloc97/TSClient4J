/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm.crypto;


/**
 *
 * @author bowen
 */
public class KeyNonceCached extends KeyNonce {
    private final int i;

    public KeyNonceCached(byte[] key, byte[] nonce, int i) {
        super(key, nonce);
        this.i = i;
    }

    public int getI() {
        return i;
    }
    
}
