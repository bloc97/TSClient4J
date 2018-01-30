/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm;

/**
 *
 * @author bowen
 */
public interface LowLevelPacket {
    
    public static enum Type {
        VOICE, WHISPER, COMMAND, COMMANDLOW, PING, PONG, ACK, ACKLOW, INIT
    }
    
    public byte[] getRaw();
}
