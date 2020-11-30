package com.rescue.hc.lib.util;

public class CrcUtil {

   public static byte[] toPrimitives(Byte[] oBytes)
    {
        byte[] bytes = new byte[oBytes.length];

        for(int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }

  public  static   Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];

        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; // Autoboxing

        return bytes;
    }


    public static byte ChechSumNew(byte[] buffer, int leng)
    {
        byte sum = 0;
        for (int i = 1; i < leng; i++)
        {
            sum += buffer[i];
        }
        return sum;
    }
}
