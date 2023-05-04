/**
 * @author Dusker
 * @date 2023/03/09
 * @version 1.0
 */
package cn.edu.sdu.db.instamesg.tools;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AvatarResize {
    /**
     * Resize the avatar
     * @param avatar avatar passed by the client, it can't be null
     * @return {@code MultipartFile} the resized avatar, where size is 512 * 512
     * @throws IOException if the default avatar is not found
     * @since 1.0
     */
    public synchronized MultipartFile resize(MultipartFile avatar) throws IOException {
        byte[] bytes = avatar.getBytes();
        InputStream in = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(in);
        BufferedImage resizedImage = ImageUtils.resizeImage(image, 512, 512);
        return new MockMultipartFile("avatar", ImageUtils.imageToBytes(resizedImage));
    }
}