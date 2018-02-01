/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets.payloads.handshake;

import java.util.Arrays;
import java.util.Collections;
import packets.payloads.InitPayload;

/**
 *
 * @author bowen
 */
public final class HandShakePayload1 extends InitPayload {
    
    public HandShakePayload1(byte[] payload, HandShakePayload0 payload0) {
        setRaw(payload);
        if (!Arrays.equals(payload0.getRandomBytesA0Copy(), getA0Copy())) {
            throw new IllegalArgumentException("Discrepancy between Handshake Packet 0 and Packet 1.");
        }
        
        if (getStep() != 1) {
            throw new IllegalStateException("Handshake packet error, step is invalid!");
        }
    }
    
    public byte[] getA1Copy() {
        return Arrays.copyOfRange(getRaw(), 1, 17);
    }
    public byte[] getReversedA0Copy() {
        return Arrays.copyOfRange(getRaw(), 17, 21);
    }
    public byte[] getA0Copy() {
        byte[] a0 = getReversedA0Copy();
        byte[] a0rev = new byte[a0.length];
        for (int i=0; i<a0.length; i++) {
            a0rev[a0.length - 1 - i] = a0[i];
        }
        //Collections.reverse(Arrays.asList(a0));
        return a0rev;
    }

    @Override
    public byte getStep() {
        return getRaw()[0];
    }
    
}
