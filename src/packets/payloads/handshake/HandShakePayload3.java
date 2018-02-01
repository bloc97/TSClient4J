/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets.payloads.handshake;

import java.math.BigInteger;
import java.util.Arrays;
import packets.payloads.InitPayload;

/**
 *
 * @author bowen
 */
public final class HandShakePayload3 extends InitPayload {
    
    public HandShakePayload3(byte[] payload) {
        setRaw(payload);
        
        if (getStep() != 3) {
            throw new IllegalStateException("Handshake packet error, step is invalid!");
        }
    }

    @Override
    public byte getStep() {
        return getRaw()[0];
    }

    public BigInteger getX() {
        return bigEndianByteArrToBigInteger(getXCopy());
    }

    public byte[] getXCopy() {
        return Arrays.copyOfRange(getRaw(), 1, 65);
    }
    
    public BigInteger getN() {
        return bigEndianByteArrToBigInteger(getNCopy());
    }

    public byte[] getNCopy() {
        return Arrays.copyOfRange(getRaw(), 65, 129);
    }
    
    public int getLevel() {
        return bigEndianByteArrToBigInteger(getLevelCopy()).intValue();
    }
    public byte[] getLevelCopy() {
        return Arrays.copyOfRange(getRaw(), 129, 133);
    }

    public byte[] getA2Copy() {
        return Arrays.copyOfRange(getRaw(), 133, 233);
    }
    
}
