/**
 * @author Dusker
 * @date 2023/03/09
 * @version 1.0
 */
package cn.edu.sdu.db.instamesg.tools;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    /**
     * Resize the image
     * @param originalImage original image
     * @param width width of the resized image
     * @param height height of the resized image
     * @return {@code BufferedImage} the resized image
     * @since 1.0
     */
    public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image resultImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultImage, 0, 0, null);
        return outputImage;
    }

    /**
     * Convert the image to bytes
     * @param image image
     * @return {@code byte[]} the bytes of the image
     * @throws IOException if the avatar is not found
     * @since 1.0
     */
    public static byte[] imageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    /**
     * Convert the bytes to image
     * @param bytes bytes
     * @return {@code BufferedImage} the image
     * @throws IOException if the avatar is not found
     * @since 1.0
     */
    private static BufferedImage bytesToImage(byte[] bytes) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }
}
