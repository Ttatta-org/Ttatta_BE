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

    public void deleteFile(String keyName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(amazonConfig.getBucket(), keyName));
        } catch (Exception e) {
            log.error("error at AmazonS3Manager deleteFile : {}", (Object) e.getStackTrace());
        }
    }

    // 객체 url 생성
    public String generateDiaryKeyName(Uuid uuid) {
        return amazonConfig.getDiaryPath() + '/' + uuid.getUuid();
    }

    // 객체 키 생성(파일 이름 추가)
    // userName 해시값으로 추가하는 코드 필요
    public String generateDiaryKeyName(Uuid uuid, String fileName, String userName) {
        return amazonConfig.getDiaryPath() + '/' + uuid.getUuid() + '_' + fileName;
    }

    /*
    public String getUuidByUrl(String pictureUrl) {
        return pictureUrl.substring(pictureUrl.lastIndexOf("/") + 1);
    }
    */

    // 객체 url을 이용하여 Unique id 반환
    public String getUuidByUrl(String pictureUrl) {
        String keyName = pictureUrl.substring(pictureUrl.lastIndexOf("/") + 1);
        String withoutExtension = keyName.substring(0, keyName.lastIndexOf("."));
        return withoutExtension.substring(withoutExtension.lastIndexOf(".") + 1);
    }


    // 업로드 Presinged Url
    public String getPresignedUrlForPost(String fileName, String userName) {
        String presignedUrl = "";

        Uuid savedUuid = createAndSaveUuid();

        String keyName = generateDiaryKeyName(savedUuid, fileName, userName);

        try{
            // PUT
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(amazonConfig.getBucket(), keyName)
                            .withMethod(HttpMethod.PUT)
                            .withExpiration(getExpirationTime(5));

            // 액세스 권한
            generatePresignedUrlRequest.addRequestParameter(
                    Headers.S3_CANNED_ACL,
                    CannedAccessControlList.Private.toString()
            );

            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            presignedUrl = url.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return presignedUrl;
    }

    // 만료 시간 설정
    public Date getExpirationTime(int minutes) {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + (1000L * 60 * minutes));
        return expiration;
    }

    // Unique id 생성 및 저장
    public Uuid createAndSaveUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuidRepository.save(Uuid.builder().uuid(uuid).build());
    }
}