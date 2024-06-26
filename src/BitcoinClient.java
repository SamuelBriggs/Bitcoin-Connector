import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BitcoinClient {

    private static final String HOST = "45.144.112.208";
    private static final int PORT = 8333;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            byte[] versionMessage = createVersionMessage();
            outputStream.write(versionMessage);
            outputStream.flush();
            System.out.println("Version message sent.");

            receiveMessages(inputStream);

            byte[] verackMessage = createVerackMessage();
            outputStream.write(verackMessage);
            outputStream.flush();
            System.out.println("Verack message sent.");

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static byte[] createVersionMessage() throws IOException, NoSuchAlgorithmException {
        ByteBuffer buffer = ByteBuffer.allocate(850000);


        buffer.put(hexStringToByteArray("f9beb4d9"));


        buffer.put("version".getBytes(StandardCharsets.US_ASCII));
        buffer.position(buffer.position() + (12 - "version".length()));
        buffer.putInt(61);


        int checksumPosition = buffer.position();
        buffer.putInt(0);


        ByteBuffer payloadBuffer = ByteBuffer.allocate(61);
        payloadBuffer.order(ByteOrder.LITTLE_ENDIAN);
        payloadBuffer.putInt(70015);
        payloadBuffer.putLong(1);
        payloadBuffer.putLong(System.currentTimeMillis() / 1000);
        payloadBuffer.putLong(1);
        payloadBuffer.put(new byte[16]);
        payloadBuffer.putShort((short) PORT);
        payloadBuffer.putLong(1);
        payloadBuffer.put(InetAddress.getLocalHost().getAddress());

        payloadBuffer.putShort((short) PORT);
        payloadBuffer.putLong(0);
        payloadBuffer.put((byte) 0);
        payloadBuffer.putInt(0);
        payloadBuffer.put((byte) 0);

        byte[] payload = payloadBuffer.array();

        // Compute checksum
        byte[] checksum = computeChecksum(payload);
        buffer.position(checksumPosition);
        buffer.put(checksum);
        buffer.put(payload);

        return buffer.array();
    }

    private static void receiveMessages(InputStream inputStream) throws IOException {
        byte[] magicBytes = new byte[4];
        byte[] command = new byte[12];
        byte[] size = new byte[4];
        byte[] checksum = new byte[4];

        while (true) {
            inputStream.read(magicBytes);
            inputStream.read(command);
            inputStream.read(size);
            inputStream.read(checksum);

            String commandString = new String(command, StandardCharsets.US_ASCII).trim();
            System.out.println("<-" + commandString);

            int payloadSize = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN).getInt();
            byte[] payload = new byte[payloadSize];
            inputStream.read(payload);

            System.out.println("Payload: " + bytesToHex(payload));

            if ("verack".equals(commandString)) {
                break;
            }
        }
    }

    private static byte[] createVerackMessage() throws NoSuchAlgorithmException {
        ByteBuffer buffer = ByteBuffer.allocate(24);


        buffer.put(hexStringToByteArray("f9beb4d9"));


        buffer.put("verack".getBytes(StandardCharsets.US_ASCII));
        buffer.position(buffer.position() + (12 - "verack".length())); // Padding


        buffer.putInt(0);


        buffer.putInt(0);

        return buffer.array();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] computeChecksum(byte[] payload) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] firstHash = digest.digest(payload);
        byte[] secondHash = digest.digest(firstHash);
        byte[] checksum = new byte[4];
        System.arraycopy(secondHash, 0, checksum, 0, 4);
        return checksum;
    }
}
