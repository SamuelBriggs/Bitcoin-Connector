import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;

public class ClientSocket {

    public static void main(String[] args) {
        String host = "45.144.112.208";
        int port = 8333;

        System.out.println(connectToSocket(host, port));


    }

    public static String connectToSocket(String ipAddress, int port) {

            try (Socket socket = new Socket(ipAddress, port)) {

                byte[] payload = new byte[0]; // verack has no payload
                byte[] magicBytes = hexStringToByteArray("f9beb4d9");
                byte[] command = "verack".getBytes("ASCII");
                byte[] size = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(payload.length).array();
                byte[] checksum = calculateChecksum(payload);
                byte[] verackMessage = concatenateArrays(magicBytes, padCommand(command), size, checksum, payload);
              //  String verackMessage = "F9BEB4D976657261636B000000000000000000005DF6E0E2";
               // byte[] byteArrayVerAck = HexFormat.of().parseHex(verackMessage);
             //   System.out.println(Arrays.toString(byteArrayVerAck));
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                byte[] versionMessage = createVersionMessage();

                sendOutputStream(outputStream, versionMessage);



             //   outputStream.write(versionMessage);
                readMessage(inputStream);
                outputStream.write(verackMessage);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "Connected to the server at " + ipAddress + " on port " + port;

    }





public static byte[] combineByteArrays(byte[] array1, byte[] array2) {
    // Allocate a ByteBuffer with the combined length of the two arrays
    ByteBuffer buffer = ByteBuffer.allocate(array1.length + array2.length);


    buffer.put(array1);
    buffer.put(array2);

    return buffer.array();
}

    private static byte[] createMessage(String command, byte[] payload) {
        byte[] magicBytes= HexFormat.of().parseHex("f9beb4d9");

                //hexStringToByteArray("F9BEB4D9"); // Bitcoin mainnet magic bytes
        byte[] commandBytes = Arrays.copyOf(command.getBytes(StandardCharsets.US_ASCII), 12);
        byte[] payloadLength = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(payload.length).array();
        byte[] checksum = Arrays.copyOfRange(sha256(sha256(payload)), 0, 4);

        ByteBuffer buffer = ByteBuffer.allocate(24 + payload.length);
        buffer.put(magicBytes);
        buffer.put(commandBytes);
        buffer.put(payloadLength);
        buffer.put(checksum);
        buffer.put(payload);
        return buffer.array();
    }



private static byte[] createVersionMessage() throws IOException {
        // Placeholder for actual version message creation logic
    //    String payloadHex = "0100000000000000"; // Example payload (version, services, timestamp, etc.)
        byte[] payload = MessagePayload.contructPayload();
        byte[] header = HeaderPayload.constructHeader(payload);

        return combineByteArray(header, payload);

   //     return createMessage("version", payload);
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

    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static void readMessage(InputStream inputStream) throws IOException {
//        byte[] magicBytes = new byte[4];
//        byte[] command = new byte[12];
//        byte[] size = new byte[4];
//        byte[] checksum = new byte[4];
//
//        inputStream.read(command);
//        inputStream.read(size);
//        inputStream.read(checksum);
//
//        String magicBytesStr = bytesToHex(magicBytes);
//        String commandStr = new String(command).trim();
//        int sizeInt = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN).getInt();
//        String checksumStr = bytesToHex(checksum);
//
//        int payloadSize = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN).getInt();
//        byte[] payload = new byte[payloadSize];


        try {
            byte[] content = readInputStream(inputStream);
            String contentAsString = new String(content, "UTF-8");
            System.out.println(contentAsString);
        } catch (IOException e) {
            e.printStackTrace();
        }



//
//        inputStream.read(payload);
//        System.out.println(magicBytesStr + "this is byte");
//        System.out.println("<-message");
//        System.out.println("magic_bytes: " + BitcoinClient.bytesToHex(magicBytes));
//        System.out.println("command:     " + new String(command).trim());
//        System.out.println("size:        " + payloadSize);
//        System.out.println("checksum:    " + BitcoinClient.bytesToHex(checksum));
//        System.out.println("payload:     " + BitcoinClient.bytesToHex(payload));
//        System.out.println();

    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        System.out.println("getting here");
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }


    private static byte[] calculateChecksum(byte[] payload) {
        // The checksum is the first 4 bytes of the double SHA256 hash of the payload
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash1 = digest.digest(payload);
            byte[] hash2 = digest.digest(hash1);
            byte[] checksum = new byte[4];
            System.arraycopy(hash2, 0, checksum, 0, 4);
            return checksum;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] padCommand(byte[] command) {
        byte[] paddedCommand = new byte[12];
        System.arraycopy(command, 0, paddedCommand, 0, command.length);
        return paddedCommand;
    }

    private static byte[] concatenateArrays(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];
        int currentPosition = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentPosition, array.length);
            currentPosition += array.length;
        }

        return result;
    }

    public static byte[] combineByteArray(byte[] array1, byte[] array2) {
        byte[] combinedArray = new byte[array1.length + array2.length];

        // Copy first array into combined array
        System.arraycopy(array1, 0, combinedArray, 0, array1.length);

        // Copy second array into combined array
        System.arraycopy(array2, 0, combinedArray, array1.length, array2.length);

        return combinedArray;
    }

    public static void sendOutputStream(OutputStream outputStream, byte[] message) throws IOException {
        try {
            outputStream.write(message);
            outputStream.flush(); // Ensure data is sent
            System.out.println("Data sent successfully.");
        } catch (IOException e) {
            System.err.println("Failed to send data: " + e.getMessage());
            throw e;
        }
    }

}
