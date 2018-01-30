/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm.crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import static testtscomm.crypto.CryptoUtils.TS3INIT_MAC;

/**
 *
 * @author bowen
 */
public class CryptoSession {
    
    private final EAXBlockCipher eaxCipher = new EAXBlockCipher(new AESEngine());
    private Identity identity;
    private volatile boolean isInit = false;
    
    private final byte[] ivStruct = new byte[20];
    private final byte[] fakeSignature = new byte[CryptoUtils.MAC_LENGTH];
    private final KeyNonceCached[] cachedKeyNonces = new KeyNonceCached[CryptoUtils.PACKET_TYPES * 2];
    
    
    
    
    
    
    public void encrypt(byte[] packet) {
        //if packet.type == Init
        fakeEncrypt(packet, TS3INIT_MAC);
        
        //if packet.isUnencrypted
        fakeEncrypt(packet, fakeSignature);
        
        CipherParameters parameters = new AEADParameters(new KeyParameter(key), 8 * TS3INIT_MAC.length, nonce, packetHeader);
        eaxCipher.init(true, cp);
        
    }
    public void fakeEncrypt(byte[] packet, byte[] mac) {
        System.arraycopy(mac, 0, packet, 0, mac.length);
    }
    
}
