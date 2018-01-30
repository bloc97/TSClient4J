/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testtscomm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.params.ECDHUPublicParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

/**
 *
 * @author bowen
 */
public class TestTSComm {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        
        
        DatagramSocket socket = new DatagramSocket(51000, InetAddress.getLocalHost());
        SocketAddress address = new InetSocketAddress(InetAddress.getByName("167.114.219.246"), 6701);
        

        Date d = new Date();
        int unixTime = (int)d.getTime() / 1000;
        
        //Send packet 0
        byte[] mac = new byte[] {0x54, 0x53, 0x33, 0x49, 0x4E, 0x49, 0x54, 0x31};
        byte[] data = new byte[] {0x06, 0x3B, (byte)0xEC, (byte)0xE9, 0x00, (byte)((unixTime >> 24) & 0xFF), (byte)((unixTime >> 16) & 0xFF), (byte)((unixTime >> 8) & 0xFF), (byte)(unixTime & 0xFF), 1, 1, 1, 1, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        ClientPacket packet0 = new ClientPacket(mac, (short)101, (short)0, (byte)0x88, data);
        
        socket.send(new DatagramPacket(packet0.getRaw(), packet0.getRaw().length, address));

        //Receive packet 1
        ServerPacket packet1 = new ServerPacket(1+16+4);
        socket.receive(new DatagramPacket(packet1.getRaw(), packet1.getRaw().length));
        
        System.out.println(Arrays.toString(packet1.getRaw()));
        System.out.println(Arrays.toString(packet1.getPayload()));
        System.out.println(new String(packet1.getRaw()));
        System.out.println(new String(packet1.getPayload()));
        
        byte[] p1p = packet1.getPayload();
        data = new byte[] {0x06, 0x3B, (byte)0xEC, (byte)0xE9, 0x02, 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for (int i=0; i<20; i++) {
            data[i+5] = p1p[i+1];
        }
        ClientPacket packet2 = new ClientPacket(mac, (short)101, (short)0, (byte)0x88, data);
        socket.send(new DatagramPacket(packet2.getRaw(), packet2.getRaw().length, address));
        
        ServerPacket packet3 = new ServerPacket(1+64+64+4+100);
        socket.receive(new DatagramPacket(packet3.getRaw(), packet3.getRaw().length));
        
        System.out.println(Arrays.toString(packet3.getRaw()));
        System.out.println(Arrays.toString(packet3.getPayload()));
        System.out.println(new String(packet3.getRaw(), Charset.defaultCharset()));
        System.out.println(new String(packet3.getPayload(), Charset.defaultCharset()));
        
        byte[] level = new byte[4];
        //System.arraycopy(packet3.getPayload(), 1+64+64, level, 0, 4);
        System.arraycopy(packet3.getRaw(), 11+1+64+64, level, 0, 4);
        System.out.println(Arrays.toString(level));
        System.out.println("" + ((level[0] << 24) | (level[1] << 16) | (level[2] << 8) | level[3]));
        
        int v = 0;
        data = new byte[4+1+64+64+4+100+64+v];
        System.arraycopy(new byte[] {0x06, 0x3B, (byte)0xEC, (byte)0xE9, 0x04}, 0, data, 0, 5);
        System.arraycopy(packet3.getPayload(), 1, data, 5, 64+64+4+100);
        
        
        System.out.println((int)byteArrToLong(level));
        BigInteger ex = new BigInteger("2");
        ex = ex.pow((int)byteArrToLong(level));
        
        BigInteger x = byteArrToInteger(Arrays.copyOfRange(packet3.getPayload(), 1, 1+64));
        BigInteger n = byteArrToInteger(Arrays.copyOfRange(packet3.getPayload(), 1+64, 1+64+64));
        
        System.out.println(x);
        System.out.println(n);
        
        BigInteger y = x.modPow(ex, n);
        byte[] yByte = y.toByteArray();
        
        if (yByte.length < 64) {
            byte[] tempYByte = new byte[64];
            System.arraycopy(yByte, 0, tempYByte, 64 - yByte.length, yByte.length);
            yByte = tempYByte;
        }
        System.arraycopy(yByte, 0, data, 4+1+64+64+4+100+(64-yByte.length), yByte.length);
        
        ClientPacket packet4 = new ClientPacket(mac, (short)101, (short)0, (byte)0x88, data);
        socket.send(new DatagramPacket(packet4.getRaw(), packet4.getRaw().length, address));
        
        System.out.println(y);
        
        ServerPacket packet5 = new ServerPacket(1+64+64+4+100);
        socket.receive(new DatagramPacket(packet5.getRaw(), packet5.getRaw().length));
        
        System.out.println(Arrays.toString(packet5.getRaw()));
        
        EAXBlockCipher cipher = new EAXBlockCipher(new AESEngine());
        cipher.init(true, new ECDHUPublicParameters(new ECPublicKeyParameters(new , ecdp), ecpkp1));
        
        socket.close();
        /*
        Socket socket = new Socket(InetAddress.getLocalHost(), 15580);
        OutputStream toServer = socket.getOutputStream();
        InputStream fromServer = socket.getInputStream();
        
        toServer.write(53);
        
        toServer.write("test".getBytes());
        
        Thread.sleep(1000);
        toServer.close();
        fromServer.close();
        */
    }
    
    public static long byteArrToLong(byte[] number) {
        long value = 0;
        for (int i = 0; i<number.length; i++) {
            value = (value << 8) + (number[i] & 0xff);
        }
        return value;
    }
    public static BigInteger byteArrToInteger(byte[] number) {
        BigInteger value = new BigInteger("0");
        for (int i = 0; i < number.length; i++) {
            value = value.shiftLeft(8).add(new BigInteger("" + (number[i] & 0xFF)));
        }
        return value;
    }
    public static int totient(int num){ //euler's totient function calculator. returns totient
    int count=0;
    for(int a=1;a<num;a++){ //definition of totient: the amount of numbers less than num coprime to it
      if(GCD(num,a)==1){ //coprime
        count++;
      }
    }
    return(count);
 }
public static int GCD(int a, int b){ //faster euclidean algorithm-see GCD for explanation
    int temp;
    if(a<b){
      temp=a;
      a=b;
      b=temp;
    }
    if(a%b==0){
      return(b);
    }
    return(GCD(a%b,b));
  }
    
}
