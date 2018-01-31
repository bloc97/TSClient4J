/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm.crypto;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962NamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.prng.FixedSecureRandom;
import org.bouncycastle.math.ec.ECPoint;

/**
 *
 * @author bowen
 */
public abstract class CryptoUtils {
    
    private static final String DUMMYSTRING = "c:\\windows\\system\\firewall32.cpl";
    private static final byte[] DUMMYKEY = DUMMYSTRING.substring(0, 16).getBytes(StandardCharsets.US_ASCII);
    private static final byte[] DUMMYNONCE = DUMMYSTRING.substring(16, 32).getBytes(StandardCharsets.US_ASCII);
    public static final KeyNonce DUMMYPAIR = new KeyNonce(DUMMYKEY, DUMMYNONCE);
    
    
    public static final byte[] TS3INIT_MAC = "TS3INIT1".getBytes(StandardCharsets.US_ASCII);
    public static final byte[] INIT_VERSION = new byte[] {0x06, 0x3B, (byte)0xEC, (byte)0xE9};
    
    public static final SecureRandom secureRandom = new SecureRandom();
    private static final ECKeyGenerationParameters keyGenerator = new ECKeyGenerationParameters(lookupParameters(X9ObjectIdentifiers.prime256v1), secureRandom);
    
    public static final int MAC_LENGTH = 8;
    public static final int CLIENTHEADER_LENGTH = 5+8;
    public static final int SERVERHEADER_LENGTH = 3+8;
    public static final int PACKET_TYPES = 9;
                
    private static ECDomainParameters lookupParameters(ASN1ObjectIdentifier identifier) {
        ECDomainParameters p = ECGOST3410NamedCurves.getByOID(identifier);
        if (p == null) {
            X9ECParameters x9 = X962NamedCurves.getByOID(identifier);
            
            if (x9 == null) {
                x9 = SECNamedCurves.getByOID(identifier);
                if (x9 == null) {
                    x9 = NISTNamedCurves.getByOID(identifier);
                    if (x9 == null) {
                        x9 = TeleTrusTNamedCurves.getByOID(identifier);
                    }
                }
            }
            
            if (x9 == null) {
                throw new IllegalArgumentException("OID is not a valid public key parameter set");
            }
            
            p = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
            
        }
        return p;
    }
    
    
    public static Identity loadIdentity(String key, long keyOffset, long lastCheckedKeyOffset) throws IOException {
        KeyPair keyPair = importPublicAndPrivateKey(Base64.getDecoder().decode(key));
        return new Identity(keyPair, exportPublicKey(keyPair.getPublicKey()), exportPublicAndPrivateKey(keyPair), keyOffset, lastCheckedKeyOffset);
    }
    
    public static Identity loadIdentity(KeyPair keyPair, long keyOffset, long lastCheckedKeyOffset) throws IOException {
        return new Identity(keyPair, exportPublicKey(keyPair.getPublicKey()), exportPublicAndPrivateKey(keyPair), keyOffset, lastCheckedKeyOffset);
    }
    
    
    public static ECPoint importPublicKey(byte[] asnByteArray) throws IOException {
        DLSequence sequence = (DLSequence)DERSequence.fromByteArray(asnByteArray);
        ASN1Integer x = (ASN1Integer)sequence.getObjectAt(2);
        ASN1Integer y = (ASN1Integer)sequence.getObjectAt(3);
        
        return keyGenerator.getDomainParameters().getCurve().createPoint(x.getValue(), y.getValue());
    }
    
    public static KeyPair importPublicAndPrivateKey(byte[] asnByteArray) throws IOException {
        DLSequence sequence = (DLSequence)DERSequence.fromByteArray(asnByteArray);
        ASN1Integer x = (ASN1Integer)sequence.getObjectAt(2);
        ASN1Integer y = (ASN1Integer)sequence.getObjectAt(3);
        ASN1Integer k = (ASN1Integer)sequence.getObjectAt(4);
        
        return new KeyPair(keyGenerator.getDomainParameters().getCurve().createPoint(x.getValue(), y.getValue()), k.getValue());
    }
    
    public static String exportPublicKey(ECPoint key) throws IOException {
        DERSequence sequence = new DERSequence(
                new ASN1Encodable[]{
                    new DERBitString(new byte[] {0x00}, 7), 
                    new ASN1Integer(32), 
                    new ASN1Integer(key.getAffineXCoord().toBigInteger()), 
                    new ASN1Integer(key.getAffineYCoord().toBigInteger())
                }
        );
        return Base64.getEncoder().encodeToString(sequence.getEncoded());
    }
    
    public static String exportPublicAndPrivateKey(KeyPair key) throws IOException {
        DERSequence sequence = new DERSequence(
                new ASN1Encodable[]{
                    new DERBitString(new byte[] {(byte)0x80}, 7), 
                    new ASN1Integer(32), 
                    new ASN1Integer(key.getPublicKey().getAffineXCoord().toBigInteger()), 
                    new ASN1Integer(key.getPublicKey().getAffineYCoord().toBigInteger()),
                    new ASN1Integer(key.getPrivateKey())
                }
        );
        return Base64.getEncoder().encodeToString(sequence.getEncoded());
    }
    
    public static String getUidFromPublicKey(String key) {
        byte[] bytes = hash1It(key.getBytes(StandardCharsets.US_ASCII));
        return Base64.getEncoder().encodeToString(bytes);
    }
    
    public static ECPoint restorePublicKeyFromPrivateKet(BigInteger key) {
        return ECNamedCurveTable.getByOID(X9ObjectIdentifiers.prime256v1).getG().multiply(key).normalize();
    }
    
    
    //Translated from https://github.com/Splamy/TS3AudioBot/blob/master/TS3Client/Full/Ts3Crypt.cs
    public static boolean checkEqual(byte[] a1, int a1Index, byte[] a2, int a2Index, int len) {
        for (int i = 0; i < len; i++) {
            if (a1[i + a1Index] != a2[i + a2Index]) {
                return false;
            }
        }
        return true;
    }
    //Translated from https://github.com/Splamy/TS3AudioBot/blob/master/TS3Client/Full/Ts3Crypt.cs
    public static void xorBinary(byte[] a, byte[] b, int len, byte[] outBuf) {
        if (a.length < len || b.length < len || outBuf.length < len) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < len; i++) {
            outBuf[i] = (byte)(a[i] ^ b[i]);
        }
    }

    
    private static final SHA1Digest sha1 = new SHA1Digest();
    private static final SHA256Digest sha256 = new SHA256Digest();
    
    public static byte[] hash1It(byte[] data) {
        return hash1It(data, 0, 0);
    }
    public static byte[] hash1It(byte[] data, int offset) {
        return hash1It(data, offset, 0);
    }
    public static byte[] hash1It(byte[] data, int offset, int length) {
        return hashIt(sha1, data, offset, length);
    }
    public static byte[] hash256It(byte[] data) {
        return hash256It(data, 0, 0);
    }
    public static byte[] hash256It(byte[] data, int offset) {
        return hash256It(data, offset, 0);
    }
    public static byte[] hash256It(byte[] data, int offset, int length) {
        return hashIt(sha256, data, offset, length);
    }
    
    private static synchronized byte[] hashIt(GeneralDigest digest, byte[] data, int offset, int length) {
        digest.reset();
        digest.update(data, offset, length == 0 ? data.length - offset : length);
        
        byte[] result = new byte[digest.getDigestSize()];
        digest.doFinal(result, 0);
        return result;
    }
    
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        byte[] hashed = hash1It(password.getBytes(StandardCharsets.US_ASCII));
        return Base64.getEncoder().encodeToString(hashed);
    }
    
    //Equals Long.toString(Long.MAX_VALUE).length()
    private static final int maxUlongStringLen = 20;
    
    public static Identity generateIdentity() throws IOException {
        return generateIdentity(8);
    }
    
    public static Identity generateIdentity(int securityLevel) throws IOException {
        X9ECParameters params = ECNamedCurveTable.getByOID(X9ObjectIdentifiers.prime256v1);
        ECDomainParameters domainParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());
        ECKeyGenerationParameters keyParams = new ECKeyGenerationParameters(domainParams, secureRandom);
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        generator.init(keyParams);
        AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();
        
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) keyPair.getPublic();
        
        Identity identity = loadIdentity(new KeyPair(publicKey.getQ().normalize(), privateKey.getD()), 0, 0);
        improveSecurity(identity, securityLevel);
        return identity;
    }
    
    public static Identity generateTestIdentity(int securityLevel) throws IOException {
        byte[] fixedSource = new byte[1000000];
        Random random = new Random(34622);
        random.nextBytes(fixedSource);
        
        X9ECParameters params = ECNamedCurveTable.getByOID(X9ObjectIdentifiers.prime256v1);
        ECDomainParameters domainParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH(), params.getSeed());
        ECKeyGenerationParameters keyParams = new ECKeyGenerationParameters(domainParams, new org.bouncycastle.util.test.FixedSecureRandom(fixedSource));
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        generator.init(keyParams);
        AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();
        
        ECPrivateKeyParameters privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();
        ECPublicKeyParameters publicKey = (ECPublicKeyParameters) keyPair.getPublic();
        
        Identity identity = loadIdentity(new KeyPair(publicKey.getQ().normalize(), privateKey.getD()), 0, 0);
        improveSecurity(identity, securityLevel);
        return identity;
    }
    
    //Translated from https://github.com/Splamy/TS3AudioBot/blob/master/TS3Client/Full/Ts3Crypt.cs
    /// <summary><para>Tries to improve the security level of the provided identity to the new level.</para>
    /// <para>The algorithm takes approximately 2^toLevel milliseconds to calculate; so be careful!</para>
    /// This method can be canceled anytime since progress which is not enough for the next level
    /// will be saved in <see cref="IdentityData.LastCheckedKeyOffset"/> continuously.</summary>
    /// <param name="identity">The identity to improve.</param>
    /// <param name="toLevel">The targeted level.</param>
    public static void improveSecurity(Identity identity, int toLevel) {
        byte[] hashBuffer = new byte[identity.getPublicKeyString().length() + maxUlongStringLen];
        byte[] pubKeyBytes = identity.getPublicKeyString().getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(pubKeyBytes, 0, hashBuffer, 0, pubKeyBytes.length);

        identity.setLastCheckedKeyOffset(Math.max(identity.getKeyOffset(), identity.getLastCheckedKeyOffset()));
        int best = getSecurityLevel(hashBuffer, pubKeyBytes.length, identity.getKeyOffset());
        while (true) {
            if (best >= toLevel) return;

            int curr = getSecurityLevel(hashBuffer, pubKeyBytes.length, identity.getLastCheckedKeyOffset());
            if (curr > best) {
                identity.setKeyOffset(identity.getLastCheckedKeyOffset());
                best = curr;
            }
            identity.setLastCheckedKeyOffset(identity.getLastCheckedKeyOffset() + 1);
        }
    }
    
    
    //Translated from https://github.com/Splamy/TS3AudioBot/blob/master/TS3Client/Full/Ts3Crypt.cs
    private static int getSecurityLevel(byte[] hashBuffer, int pubKeyLen, long offset) {
        byte[] numBuffer = new byte[maxUlongStringLen];
        int numLen = 0;
        do
        {
            numBuffer[numLen] = (byte)('0' + (offset % 10));
            offset /= 10;
            numLen++;
        } while (offset > 0);
        for (int i = 0; i < numLen; i++)
            hashBuffer[pubKeyLen + i] = numBuffer[numLen - (i + 1)];
        byte[] outHash = hash1It(hashBuffer, 0, pubKeyLen + numLen);

        return getLeadingZeroBits(outHash);
    }
    
    //Translated from https://github.com/Splamy/TS3AudioBot/blob/master/TS3Client/Full/Ts3Crypt.cs
    private static int getLeadingZeroBits(byte[] data) {
        int curr = 0;
        int i;
        for (i = 0; i < data.length; i++)
            if (data[i] == 0) curr += 8;
            else break;
        if (i < data.length)
            for (int bit = 0; bit < 8; bit++)
                    if ((data[i] & (1 << bit)) == 0) curr++;
                    else break;
        return curr;
    }

    //Translated from https://github.com/Splamy/TS3AudioBot/blob/master/TS3Client/Full/Ts3Crypt.cs
    /// <summary>
    /// This is the reference function from the TS3 Server for checking if a hashcash offset
    /// is sufficient for the required level.
    /// </summary>
    /// <param name="data">The sha1 result from the current offset calculation</param>
    /// <param name="reqLevel">The required level to reach.</param>
    /// <returns>True if the hash meets the requirement, false otherwise.</returns>
    private static boolean validateHash(byte[] data, int reqLevel) {
        int levelMask = 1 << (reqLevel % 8) - 1;

        if (reqLevel < 8)
        {
            return (data[0] & levelMask) == 0;
        }
        else
        {
            int v9 = reqLevel / 8;
            int v10 = 0;
            while (data[v10] == 0)
            {
                if (++v10 >= v9)
                {
                    return (data[v9] & levelMask) == 0;
                }
            }
            return false;
        }
    }
    
    
}
