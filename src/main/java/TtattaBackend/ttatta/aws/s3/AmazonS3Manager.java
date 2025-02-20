package TtattaBackend.ttatta.aws.s3;

import TtattaBackend.ttatta.config.AmazonConfig;
import TtattaBackend.ttatta.domain.Uuid;
import TtattaBackend.ttatta.repository.UuidRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;

import com.drew.metadata.*;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.imaging.ImageMetadataReader;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String fileFormatName = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);

        MultipartFile resizedFile = resizeImageByMarvin(keyName, originalFilename, fileFormatName, file, 1000);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(resizedFile.getSize());
        metadata.setContentDisposition("inline");
        metadata.setContentType(resizedFile.getContentType());

        try(InputStream inputStream = resizedFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e){
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String generateDiaryKeyName(Uuid uuid) {
        return amazonConfig.getDiaryPath() + '/' + uuid.getUuid();
    }

    public String getUuidByUrl(String pictureUrl) {
        return pictureUrl.substring(pictureUrl.lastIndexOf("/") + 1);
    }

    public void deleteFile(String keyName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(amazonConfig.getBucket(), keyName));
        } catch (Exception e) {
            log.error("error at AmazonS3Manager deleteFile : {}", (Object) e.getStackTrace());
        }
    }

    MultipartFile resizeImageByMarvin(String fileName, String originalFilename, String fileFormatName, MultipartFile originalImage, int targetWidth) {
        try {
            byte[] imageBytes = originalImage.getBytes();
            InputStream imageStream = new ByteArrayInputStream(imageBytes);

            BufferedImage image = ImageIO.read(imageStream);

            image = correctImageOrientation(image, new ByteArrayInputStream(imageBytes));

            int originWidth = image.getWidth();
            int originHeight = image.getHeight();

            // origin 이미지가 resizing될 사이즈보다 작을 경우 작업 안함
            if(originWidth < targetWidth)
                return originalImage;
            MarvinImage imageMarvin = new MarvinImage(image);
            Scale scale = new Scale();
            scale.load();
            scale.setAttribute("newWidth", targetWidth);
            scale.setAttribute("newHeight", targetWidth * originHeight / originWidth);
            scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

            BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
            File tempFile = File.createTempFile("resized_", "." + fileFormatName);
            ImageIO.write(imageNoAlpha, fileFormatName, tempFile);

            return new CustomMultipartFile(
                    fileName,
                    originalFilename,
                    fileFormatName,
                    Files.readAllBytes(tempFile.toPath())
            );

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 리사이즈에 실패했습니다.");
        }
    }

    private BufferedImage correctImageOrientation(BufferedImage image, InputStream inputStream) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);
            ExifIFD0Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (directory != null && directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
                int orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
                return rotateImage(image, orientation);
            }
        } catch (Exception e) {
            log.warn("EXIF 데이터를 읽을 수 없습니다. 기본 방향으로 저장합니다.");
        }
        return image;
    }


    private BufferedImage rotateImage(BufferedImage image, int orientation) {
        AffineTransform transform = new AffineTransform();
        switch (orientation) {
            case 6: // 90도 회전
                transform.rotate(Math.toRadians(90), image.getWidth() / 2.0, image.getHeight() / 2.0);
                break;
            case 3: // 180도 회전
                transform.rotate(Math.toRadians(180), image.getWidth() / 2.0, image.getHeight() / 2.0);
                break;
            case 8: // 270도 회전
                transform.rotate(Math.toRadians(270), image.getWidth() / 2.0, image.getHeight() / 2.0);
                break;
            default:
                return image; // 회전 필요 없음
        }
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, null);
    }
}