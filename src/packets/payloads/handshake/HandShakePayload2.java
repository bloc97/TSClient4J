/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets.payloads.handshake;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import packets.payloads.InitPayload;
import static packets.payloads.InitPayload.DEFAULT_TS_STAMP;

/**
 *
 * @author bowen
 */
public final class HandShakePayload2 extends InitPayload {
    
    public HandShakePayload2(HandShakePayload1 payload1) {
        
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(DEFAULT_TS_STAMP);
            stream.write(2);
            stream.write(payload1.getA1Copy());
            stream.write(payload1.getReversedA0Copy());
            
        } catch (IOException ex) {
            
        }
        
        setRaw(stream.toByteArray());
        
        if (getStep() != 2) {
            throw new IllegalStateException("Handshake packet error, step is invalid!");
        }
    }
    
    public byte[] getStampCopy() {
        return Arrays.copyOfRange(getRaw(), 0, 4);
    }
    
    public byte[] getA1Copy() {
        return Arrays.copyOfRange(getRaw(), 5, 21);
    }

    public byte[] getA0ReversedCopy() {
        return Arrays.copyOfRange(getRaw(), 21, 25);
    }

    @Override
    public byte getStep() {
        return getRaw()[4];
    }
    
}
