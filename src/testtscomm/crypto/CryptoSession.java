/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm.crypto;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.ECPoint;
import testtscomm.ClientPacket;
import testtscomm.LowLevelPacket;
import testtscomm.ServerPacket;
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

    public Identity getIdentity() {
        return identity;
    }

    public boolean isInit() {
        return isInit;
    }
    
    public CryptoSession() {
        reset();
    }

    public void setIdentity(Identity identity) {
        reset();
        this.identity = identity;
    }
    
    public void reset() {
        Arrays.fill(ivStruct, (byte)0);
        Arrays.fill(fakeSignature, (byte)0);
        Arrays.fill(cachedKeyNonces, null);
    }
    
    public void initKey(String alpha, String beta, String omega) throws IOException {
        if (identity == null) {
            throw new IllegalStateException("Attemted to initialise when identity is null!");
        }
        
        byte[] alphaBytes = Base64.getDecoder().decode(alpha);
        byte[] betaBytes = Base64.getDecoder().decode(beta);
        byte[] omegaBytes = Base64.getDecoder().decode(omega);
        
        ECPoint serverPublicKey = CryptoUtils.importPublicKey(omegaBytes);
        
        byte[] sharedKey = getSharedSecret(serverPublicKey);
        setSharedSecret(alphaBytes, betaBytes, sharedKey);
        
        isInit = true;
    }
    
    private byte[] getSharedSecret(ECPoint key) {
        ECPoint p = key.multiply(identity.getKeyPair().getPrivateKey()).normalize();
        
        byte[] keyArr = p.getAffineXCoord().toBigInteger().toByteArray();
        
        if (keyArr.length == 32) {
            return keyArr;
        }
        
        byte[] sharedData = new byte[32];
        if (keyArr.length > 32) {
            System.arraycopy(keyArr, keyArr.length - 32, sharedData, 0, 32);
        } else {
            System.arraycopy(keyArr, 0, sharedData, 32 - keyArr.length, keyArr.length);
        }
        
        return sharedData;
    }
    
    private void setSharedSecret(byte[] alphaBytes, byte[] betaBytes, byte[] sharedKey) {
        System.arraycopy(alphaBytes, 0, ivStruct, 0, 10);
	System.arraycopy(betaBytes, 0, ivStruct, 10, 10);
        
        
        byte[] buffer = CryptoUtils.hash1It(sharedKey);
        CryptoUtils.xorBinary(ivStruct, buffer, 20, ivStruct);
        
        buffer = CryptoUtils.hash1It(ivStruct, 0, 20);
        System.arraycopy(buffer, 0, fakeSignature, 0, 8);
    }
    
    
    
    public KeyNonce getKeyNonce(boolean isFromServer, short packetId, int generationId, byte packetType) {
        if (!isInit) {
            return CryptoUtils.DUMMYPAIR;
        }
        
        byte packetTypeRaw = (byte)(packetType & 0xF);
        
        int cacheIndex = packetTypeRaw * (isFromServer ? 1 : 2);
        
        if (cachedKeyNonces[cacheIndex] == null || cachedKeyNonces[cacheIndex].getI() != generationId) {
            
            byte[] tmp = new byte[26];
            
            if (isFromServer) {
                tmp[0] = 0x30;
            } else {
                tmp[0] = 0x31;
            }
            
            tmp[1] = packetTypeRaw;
            
            System.arraycopy(new byte[] {(byte)((generationId >> 24) & 0xFF), (byte)((generationId >> 16) & 0xFF), (byte)((generationId >> 8) & 0xFF), (byte)(generationId & 0xFF)}, 0, tmp, 2, 4);
            System.arraycopy(ivStruct, 0, tmp, 6, 20);
            
            byte[] result = CryptoUtils.hash256It(tmp);
            
            cachedKeyNonces[cacheIndex] = new KeyNonceCached(Arrays.copyOfRange(result, 0, 16), Arrays.copyOfRange(result, 16, 32), generationId);
            
        }
        
        byte[] key = new byte[16];
        byte[] nonce = new byte[16];
        
        System.arraycopy(cachedKeyNonces[cacheIndex].getKey(), 0, key, 0, 16);
        System.arraycopy(cachedKeyNonces[cacheIndex].getNonce(), 0, nonce, 0, 16);
        
        key[0] ^= (byte)((packetId >> 8) & 0xFF);
        key[1] ^= (byte)((packetId) & 0xFF);
        
        return new KeyNonce(key, nonce);
    }
    
    public void encrypt(ClientPacket packet) {
        //if packet.type == Init
        if ((packet.getPt() & 0xF) == 0x08) {
            fakeEncrypt(packet.getRaw(), TS3INIT_MAC);
            return;
        }
        
        //if packet is Unencrypted
        if ((packet.getPt() >> 7) == 0x01) {
            fakeEncrypt(packet.getRaw(), fakeSignature);
            return;
        }
        KeyNonce keyNonce = getKeyNonce(false, packet.getPid(), packet.getGid(), packet.getPt());
        CipherParameters parameters = new AEADParameters(new KeyParameter(keyNonce.getKey()), CryptoUtils.MAC_LENGTH * 8, keyNonce.getNonce(), packet.getHeader());
        
        byte[] result;
        int len;
        
        synchronized (eaxCipher) {
            eaxCipher.init(true, parameters);
            result = new byte[eaxCipher.getOutputSize(packet.getPayload().length)];
            try {
                len = eaxCipher.processBytes(packet.getPayload(), 0, packet.getPayload().length, result, 0);
                len += eaxCipher.doFinal(result, len);
            } catch (Exception ex) {
                throw new IllegalStateException("Internal encryption error!");
            }
        }
        System.out.println(Arrays.toString(eaxCipher.getMac()));
        byte[] header = packet.getHeader();
        packet.setRaw(new byte[CryptoUtils.CLIENTHEADER_LENGTH + len]);
        
        System.arraycopy(result, len - CryptoUtils.MAC_LENGTH, packet.getRaw(), 0, CryptoUtils.MAC_LENGTH);
        System.arraycopy(header, 0, packet.getRaw(), CryptoUtils.MAC_LENGTH, CryptoUtils.CLIENTHEADER_LENGTH - CryptoUtils.MAC_LENGTH);
        System.arraycopy(result, 0, packet.getRaw(), CryptoUtils.CLIENTHEADER_LENGTH, len - CryptoUtils.MAC_LENGTH);
        
    }
    public void fakeEncrypt(byte[] packetData, byte[] mac) {
        System.arraycopy(mac, 0, packetData, 0, mac.length);
    }
    
    public boolean decrypt(ServerPacket packet) {
        
        if ((packet.getPt() & 0xF) == 0x08) {
            return fakeDecrypt(packet.getRaw(), TS3INIT_MAC);
        }
        
        //if packet is Unencrypted
        if ((packet.getPt() >> 7) == 0x01) {
            return fakeDecrypt(packet.getRaw(), fakeSignature);
        }
        
        KeyNonce keyNonce = getKeyNonce(true, packet.getPid(), packet.getGid(), packet.getPt());
        int dataLength = packet.getRaw().length - CryptoUtils.SERVERHEADER_LENGTH;
        
        CipherParameters parameters = new AEADParameters(new KeyParameter(keyNonce.getKey()), CryptoUtils.MAC_LENGTH * 8, keyNonce.getNonce(), packet.getHeader());
        
        try {
            byte[] result;
            int len;
            synchronized (eaxCipher) {
                eaxCipher.init(false, parameters);
                result = new byte[eaxCipher.getOutputSize(dataLength + CryptoUtils.MAC_LENGTH)];
                
                len = 0;
                len += eaxCipher.processBytes(packet.getRaw(), CryptoUtils.SERVERHEADER_LENGTH, dataLength, result, 0);
                len += eaxCipher.processBytes(packet.getRaw(), 0, CryptoUtils.MAC_LENGTH , result, len);
                len += eaxCipher.doFinal(result, len);
            }
            System.arraycopy(result, 0, packet.getRaw(), CryptoUtils.SERVERHEADER_LENGTH, len);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        
        return true;
    }
    public boolean decrypt(ClientPacket packet) {
        
        if ((packet.getPt() & 0xF) == 0x08) {
            return fakeDecrypt(packet.getRaw(), TS3INIT_MAC);
        }
        
        //if packet is Unencrypted
        if ((packet.getPt() >> 7) == 0x01) {
            return fakeDecrypt(packet.getRaw(), fakeSignature);
        }
        
        KeyNonce keyNonce = getKeyNonce(true, packet.getPid(), packet.getGid(), packet.getPt());
        int dataLength = packet.getRaw().length - CryptoUtils.CLIENTHEADER_LENGTH;
        
        CipherParameters parameters = new AEADParameters(new KeyParameter(keyNonce.getKey()), CryptoUtils.MAC_LENGTH * 8, keyNonce.getNonce(), packet.getHeader());
        
        try {
            byte[] result;
            int len;
            synchronized (eaxCipher) {
                eaxCipher.init(false, parameters);
                System.out.println(Arrays.toString(eaxCipher.getMac()));
                result = new byte[eaxCipher.getOutputSize(dataLength + CryptoUtils.MAC_LENGTH)];
                
                len = 0;
                len += eaxCipher.processBytes(packet.getRaw(), CryptoUtils.CLIENTHEADER_LENGTH, dataLength, result, 0);
                len += eaxCipher.processBytes(packet.getRaw(), 0, CryptoUtils.MAC_LENGTH , result, len);
                len += eaxCipher.doFinal(result, len);
            }
            System.arraycopy(result, 0, packet.getRaw(), CryptoUtils.CLIENTHEADER_LENGTH, len);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    public boolean fakeDecrypt(byte[] packetData, byte[] mac) {
        if (!CryptoUtils.checkEqual(packetData, 0, mac, 0, CryptoUtils.MAC_LENGTH)) {
            return false;
        }
        return true;
    }
    
}
