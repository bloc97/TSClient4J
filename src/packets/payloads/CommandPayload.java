/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packets.payloads;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author bowen
 */
public class CommandPayload implements Payload {

    private byte[] raw;
    
    public CommandPayload(String command) {
        raw = command.getBytes(StandardCharsets.US_ASCII);
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
    
}
