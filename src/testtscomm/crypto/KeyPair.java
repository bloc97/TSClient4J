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

public class KeyPair {
    private final ECPoint publicKey;
    private final BigInteger privateKey;

    public KeyPair(ECPoint publicKey, BigInteger privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public ECPoint getPublicKey() {
        return publicKey;
    }

}