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

/**
 *
 * @author bowen
 */
public final class HandShakePayload0 extends InitPayload {
    
    public HandShakePayload0(Random random) {
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            stream.write(DEFAULT_TS_STAMP);
            
            stream.write(0);
            
            byte[] unixTime = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt((int)(new Date().getTime()/1000)).array();
            stream.write(unixTime);
            
            byte[] randomBytes = new byte[4];
            random.nextBytes(randomBytes);
            stream.write(randomBytes);
            
            stream.write(new byte[8]);
            
        } catch (IOException ex) {
        }
        
        setRaw(stream.toByteArray());
        
        if (getStep() != 0) {
            throw new IllegalStateException("Handshake packet error, step is invalid!");
        }
    }
    
    public byte[] getStampCopy() {
        return Arrays.copyOfRange(getRaw(), 0, 4);
    }

    public byte[] getUnixTimeCopy() {
        return Arrays.copyOfRange(getRaw(), 5, 9);
    }

    public byte[] getRandomBytesA0Copy() {
        return Arrays.copyOfRange(getRaw(), 9, 13);
    }

    @Override
    public byte getStep() {
        return getRaw()[4];
    }
    
}
