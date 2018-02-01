/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets.payloads;

import java.math.BigInteger;

/**
 *
 * @author bowen
 */
public abstract class InitPayload implements Payload {

    public static final byte[] DEFAULT_TS_STAMP = new byte[] {(byte)0x06, (byte)0x3B, (byte)0xEC, (byte)0xE9};
    
    private byte[] raw;
    
    public InitPayload() {
        this.raw = new byte[0];
    }
    
    @Override
    public byte[] getRaw() {
        return raw;
    }

    @Override
    public void setRaw(byte[] raw) {
        this.raw = raw;
    }

    @Override
    public int length() {
        return raw.length;
    }
    
    public abstract byte getStep();
    
    
    public static long bigEndianByteArrToLong(byte[] number) {
        long value = 0;
        for (int i = 0; i<number.length; i++) {
            value = (value << 8) + (number[i] & 0xff);
        }
        return value;
    }
    public static BigInteger bigEndianByteArrToBigInteger(byte[] number) {
        BigInteger value = BigInteger.ZERO;
        for (int i = 0; i < number.length; i++) {
            value = value.shiftLeft(8).add(new BigInteger("" + (number[i] & 0xFF)));
        }
        return value;
    }
}
