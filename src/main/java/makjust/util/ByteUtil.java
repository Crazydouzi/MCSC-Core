package makjust.util;


import java.io.IOException;
import java.io.InputStream;

public class ByteUtil {
    private static final int BUFFER_SIZE = 1024;

    public static String switchInputStreamToString(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        byte[] totalBytes = new byte[0];
        int inputStreamBytesLenth = inputStream.available();
        int bufferSize = inputStreamBytesLenth / BUFFER_SIZE;
        int endBytesLength = inputStreamBytesLenth - bufferSize * BUFFER_SIZE;
        for(int i = 0;i < bufferSize;i++){
            // 如果输入流里的字节被读取结束，再次读取不会返回0，而是会被阻塞
            inputStream.read(bytes);
            totalBytes = byteMerge(totalBytes, bytes);
        }

        if(endBytesLength > 0){
            inputStream.read(bytes);
            bytes = subBytes(bytes, 0, endBytesLength);
            totalBytes = byteMerge(totalBytes, bytes);
        }

        return new String(totalBytes);
    }

    private static byte[] subBytes(byte[] b,int off,int length){
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }

    private static byte[] byteMerge(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        // System.arraycopy(原数组, 原数组被复制部分的起始索引, 新的数组, 新数组被粘贴部分的起始索引, 原数组被复制部分的长度)
        // System.arraycopy如果复制引用，称为浅复制，反之，为深复制
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
