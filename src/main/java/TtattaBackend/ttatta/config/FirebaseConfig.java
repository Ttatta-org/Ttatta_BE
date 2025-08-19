package TtattaBackend.ttatta.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    // 비밀키 경로 환경 변수 ( 필수 )
    @Value("${fcm.service-account-file}")
    private String serviceAccountFilePath;

    // 프로젝트 아이디 환경 변수 ( 필수 )
    @Value("${fcm.project-id}")
    private String projectId;

    @PostConstruct
    public void init() {
        try{
            InputStream serviceAccount = new ClassPathResource(serviceAccountFilePath).getInputStream();
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectId)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { // FirebaseApp이 이미 초기화되어 있지 않은 경우에만 초기화 실행
                FirebaseApp.initializeApp(options);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
