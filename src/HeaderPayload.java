import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

public class HeaderPayload {
    static ByteArrayOutputStream headerOutputStream = new ByteArrayOutputStream();
    static DataOutputStream header = new DataOutputStream(headerOutputStream);
    static int magicNumber;
    static byte[] command = new byte[12];
    static int size;
    static byte[] checkSum;

    private static void setMagicNumber(){
        magicNumber = 0xD9B4BEF9;
    }

    private static void setCommand(){
       System.arraycopy("version".getBytes(), 0, command, 0, "version".length());

    }
    private static void setSize(byte[] payloadByte){
        size = Integer.reverse(payloadByte.length);
    }

    private static void setChecksum(byte[] payloadByte){
        CRC32 crc32 = new CRC32();
        crc32.update(payloadByte);
        checkSum = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) crc32.getValue()).array();
    }
    public static byte[] constructHeader(byte[] payloadByte) throws IOException {
        setMagicNumber();
        setCommand();
        setChecksum(payloadByte);
        setSize(payloadByte);

        header.write(magicNumber);
        header.write(command);
        header.write(size);
        header.write(checkSum);

        return headerOutputStream.toByteArray();
    }



}
