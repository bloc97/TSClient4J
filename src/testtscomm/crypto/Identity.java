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
public class Identity {
    private final KeyPair keyPair;
    private final String publicKeyString, privateKeyString;
    private long keyOffset, lastCheckedKeyOffset;

    public Identity(KeyPair keyPair, String publicKeyString, String privateKeyString, long keyOffset, long lastCheckedKeyOffset) {
        this.keyPair = keyPair;
        this.publicKeyString = publicKeyString;
        this.privateKeyString = privateKeyString;
        this.keyOffset = keyOffset;
        this.lastCheckedKeyOffset = lastCheckedKeyOffset < keyOffset ? keyOffset : lastCheckedKeyOffset;
    }

    public long getKeyOffset() {
        return keyOffset;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public long getLastCheckedKeyOffset() {
        return lastCheckedKeyOffset;
    }

    public String getPrivateKeyString() {
        return privateKeyString;
    }

    public String getPublicKeyString() {
        return publicKeyString;
    }

    public void setKeyOffset(long keyOffset) {
        this.keyOffset = keyOffset;
    }

    public void setLastCheckedKeyOffset(long lastCheckedKeyOffset) {
        this.lastCheckedKeyOffset = lastCheckedKeyOffset;
    }
}