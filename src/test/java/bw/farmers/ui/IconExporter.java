package bw.farmers.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Writes the app icon, painted by {@link Logo}, as the files the installers need:
 * {@code farmersbw.ico} (Windows), {@code farmersbw.icns} (macOS) and {@code farmersbw.png} (Linux).
 * Run with {@code ./gradlew icons}.
 */
public final class IconExporter {

    private IconExporter() {
    }

    public static void main(String[] args) throws IOException {
        Path out = Path.of(args.length > 0 ? args[0] : "packaging/icons");
        Files.createDirectories(out);
        Files.write(out.resolve("farmersbw.png"), png(512));
        writeIco(out.resolve("farmersbw.ico"), 16, 20, 24, 32, 40, 48, 64, 128, 256);
        writeIcns(out.resolve("farmersbw.icns"));
        System.out.println("wrote icons to " + out.toAbsolutePath());
    }

    private static byte[] png(int px) throws IOException {
        BufferedImage img = Logo.image(px);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(img, "png", bytes);
        return bytes.toByteArray();
    }

    /** An ICO file whose entries are PNGs, which every Windows since Vista reads. */
    private static void writeIco(Path file, int... sizes) throws IOException {
        byte[][] images = new byte[sizes.length][];
        for (int i = 0; i < sizes.length; i++) {
            images[i] = png(sizes[i]);
        }
        ByteBuffer head = ByteBuffer.allocate(6 + 16 * sizes.length).order(ByteOrder.LITTLE_ENDIAN);
        head.putShort((short) 0).putShort((short) 1).putShort((short) sizes.length);
        int offset = head.capacity();
        for (int i = 0; i < sizes.length; i++) {
            int s = sizes[i];
            head.put((byte) (s >= 256 ? 0 : s)).put((byte) (s >= 256 ? 0 : s)).put((byte) 0).put((byte) 0);
            head.putShort((short) 1).putShort((short) 32);
            head.putInt(images[i].length).putInt(offset);
            offset += images[i].length;
        }
        try (OutputStream o = Files.newOutputStream(file)) {
            o.write(head.array());
            for (byte[] image : images) {
                o.write(image);
            }
        }
    }

    /** An ICNS file with PNG entries from 32 to 1024 pixels. */
    private static void writeIcns(Path file) throws IOException {
        Map<String, Integer> types = new LinkedHashMap<>();
        types.put("ic11", 32);   // 16@2x
        types.put("ic12", 64);   // 32@2x
        types.put("ic07", 128);
        types.put("ic08", 256);
        types.put("ic13", 256);  // 128@2x
        types.put("ic09", 512);
        types.put("ic14", 512);  // 256@2x
        types.put("ic10", 1024); // 512@2x
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        DataOutputStream d = new DataOutputStream(body);
        for (Map.Entry<String, Integer> t : types.entrySet()) {
            byte[] image = png(t.getValue());
            d.writeBytes(t.getKey());
            d.writeInt(8 + image.length);
            d.write(image);
        }
        try (DataOutputStream o = new DataOutputStream(Files.newOutputStream(file))) {
            o.writeBytes("icns");
            o.writeInt(8 + body.size());
            body.writeTo(o);
        }
    }
}
