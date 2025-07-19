package TtattaBackend.ttatta.aws.s3;

import TtattaBackend.ttatta.config.AmazonConfig;
import TtattaBackend.ttatta.repository.DiaryPhotosRepository;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final DiaryPhotosRepository diaryPhotosRepository;

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

    // 객체 키 생성
    public String generateDiaryKeyName(String uuid, Long userId) {
        return amazonConfig.getDiaryPath() + '/' + userId + '/' + uuid;
    }

    // presigned url 생성
    private String generatePresignedUrl(String keyName, String imageType) {
        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(amazonConfig.getBucket(), keyName)
                            .withMethod(HttpMethod.PUT)
                            .withContentType(imageType)
                            .withExpiration(getExpirationTime(5));

            generatePresignedUrlRequest.addRequestParameter("Content-Type", imageType);

            generatePresignedUrlRequest.addRequestParameter(
                    Headers.S3_CANNED_ACL,
                    CannedAccessControlList.Private.toString()
            );

            return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 업로드 presinged Url
    public List<String> getPresignedUrlAndKey(String imageType, Long userId) {
        List<String> urlList = new ArrayList<>();
        String savedUuid = createUuid();

        String keyName = generateDiaryKeyName(savedUuid,userId);
        String presignedUrl = generatePresignedUrl(keyName, imageType);

        urlList.add(presignedUrl);
        urlList.add(keyName);
        return urlList;
    }

    // 수정용 presigned url
    public String getPresignedUrl(Long diaryId, String imageType) {
        String keyName = diaryPhotosRepository.findByDiaries_Id(diaryId).getImageUrl();
        return generatePresignedUrl(keyName, imageType);
    }

    // 만료 시간 설정
    public Date getExpirationTime(int minutes) {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + (1000L * 60 * minutes));
        return expiration;
    }

    // Unique id 생성
    public String createUuid() {
        return UUID.randomUUID().toString();
    }

    // 조회 용 presigned url
    public String generatePresignedUrlForView(String keyName) {
        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(amazonConfig.getBucket(), keyName)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(getExpirationTime(10));

            generatePresignedUrlRequest.addRequestParameter(
                    Headers.S3_CANNED_ACL,
                    CannedAccessControlList.Private.toString()
            );

            return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}