package TtattaBackend.ttatta.aws.s3;

import TtattaBackend.ttatta.config.AmazonConfig;
import TtattaBackend.ttatta.domain.Uuid;
import TtattaBackend.ttatta.repository.UuidRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String keyName, MultipartFile file){
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());

        metadata.setContentDisposition("inline");
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
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

    public String getPresignedUrl(String fileName) {
        String presignedUrl = "";

        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = Uuid.builder()
                .uuid(uuid).build();
        String urlName = generateDiaryKeyName(savedUuid);

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        try{
            // PUT
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(amazonConfig.getBucket(), urlName)
                            .withMethod(HttpMethod.PUT)
                            .withExpiration(expiration);

            // 액세스 권한
            generatePresignedUrlRequest.addRequestParameter(
                    Headers.S3_CANNED_ACL,
                    CannedAccessControlList.PublicRead.toString()
            );

            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            presignedUrl = url.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return presignedUrl;
    }
}