/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets.payloads.handshake;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import packets.payloads.InitPayload;
import static packets.payloads.InitPayload.DEFAULT_TS_STAMP;

/**
 *
 * @author bowen
 */
public final class HandShakePayload4 extends InitPayload {
    
    
    
    public HandShakePayload4(HandShakePayload3 payload3, byte[] clientinitiv) {
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(DEFAULT_TS_STAMP);
            stream.write(4);
            stream.write(payload3.getXCopy());
            stream.write(payload3.getNCopy());
            stream.write(payload3.getLevelCopy());
            stream.write(payload3.getA2Copy());
            
            //RSA Puzzle
            BigInteger ex = new BigInteger("2");
            ex.pow(payload3.getLevel());
            BigInteger y = payload3.getX().modPow(ex, payload3.getN());
            
            //Pad to length of 64
            byte[] yByte = y.toByteArray();
            if (yByte.length < 64) {
                byte[] tempYByte = new byte[64];
                System.arraycopy(yByte, 0, tempYByte, 64 - yByte.length, yByte.length);
                yByte = tempYByte;
            }
            stream.write(yByte);
            
            stream.write(clientinitiv);
        
        } catch (IOException ex) {
            
        }
        
        setRaw(stream.toByteArray());
        
        if (getStep() != 4) {
            throw new IllegalStateException("Handshake packet error, step is invalid!");
        }
        
    }
    
    public byte[] getStampCopy() {
        return Arrays.copyOfRange(getRaw(), 0, 4);
    }

    public BigInteger getX() {
        return bigEndianByteArrToBigInteger(getXCopy());
    }

    public byte[] getXCopy() {
        return Arrays.copyOfRange(getRaw(), 5, 69);
    }
    
    public BigInteger getN() {
        return bigEndianByteArrToBigInteger(getNCopy());
    }

    public byte[] getNCopy() {
        return Arrays.copyOfRange(getRaw(), 69, 133);
    }
    
    public int getLevel() {
        return bigEndianByteArrToBigInteger(getLevelCopy()).intValue();
    }
    public byte[] getLevelCopy() {
        return Arrays.copyOfRange(getRaw(), 133, 137);
    }

    public byte[] getA2Copy() {
        return Arrays.copyOfRange(getRaw(), 137, 237);
    }
    
    public BigInteger getY() {
        return bigEndianByteArrToBigInteger(getYCopy());
    }

    public byte[] getYCopy() {
        return Arrays.copyOfRange(getRaw(), 237, 301);
    }
    
    public byte[] getCommandDataCopy() {
        return Arrays.copyOfRange(getRaw(), 301, length());
    }
    
    @Override
    public byte getStep() {
        return getRaw()[4];
    }
    
}
