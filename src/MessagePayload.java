import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class MessagePayload {

    static int protocolVersion = Integer.reverseBytes(70015);
    static Long services = Long.reverseBytes(1);
    static Long timeStamp = Long.reverseBytes(System.currentTimeMillis());
    static byte[] remoteIpAddress = "45.144.112.208".getBytes();
    static int remotePort = Integer.reverseBytes(8333);
    static byte[] localAddress = "127.0.0.1".getBytes();
    static int localPort = Integer.reverseBytes(8333);
    static int nonce = 0;
    static byte[] userAgent = "/Samuel:0.21.1".getBytes();
    static int block = Integer.reverseBytes(0);
    static ByteArrayOutputStream payloadStream = new ByteArrayOutputStream();
    static DataOutputStream payload = new DataOutputStream(payloadStream);


    public static byte[] contructPayload() throws IOException {

        payload.writeInt(protocolVersion);
        payload.writeLong(services);
        payload.writeLong(timeStamp);
        payload.write(remoteIpAddress);
        payload.writeInt(remotePort);
        payload.write(localAddress);
        payload.writeInt(localPort);
        payload.writeInt(nonce);
        payload.write(userAgent);
        payload.writeInt(block);

        return payloadStream.toByteArray();

    }


}
