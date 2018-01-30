/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm.crypto;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECPoint;

/**
 *
 * @author bowen
 */
public class KeyNonce {
    private final byte[] key;
    private final byte[] nonce;

    public KeyNonce(byte[] key, byte[] nonce) {
        this.key = key;
        this.nonce = nonce;
    }

    public byte[] getKey() {
        return key;
    }

    public byte[] getNonce() {
        return nonce;
    }
    
}
