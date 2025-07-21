package TtattaBackend.ttatta.service.AlarmService;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class FcmPushSender {
    // FCM 푸시 알림을 보내는 메서드
    public void sendPushNotification(String token, AlaramType alaramType) {
        try {
            // FCM 서버에 요청을 보내는 로직을 구현합니다.
            // 받은 token을 이용하여 fcm를 보내는 메서드
            String title = alaramType.getTitle();
            String body = alaramType.getBody();
            FirebaseMessaging.getInstance().send(Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .setToken(token)
                    .putData("type", alaramType.getType())
                    .build());
            System.out.println("Sending push notification to token: " + token);
        } catch (FirebaseMessagingException e) { // FCM 전송 실패 시 예외 처리 : 어떻게 동작하는지 확인하기 상위 메서드의 runnable에서 예외처리 안해주고 있는데 그러면 에러나면 해당 스레드 죽는것이지, 죽는다면 runnalbe에서 예외처리해주어야됨.
            log.error("FCM 전송 실패: {}", e.getMessage());
//            throw new ExceptionHandler(ErrorStatus.ALARM_FCM_SEND_FAIL);
            throw new RuntimeException("FCM 전송 실패: " + e.getMessage(), e);
        }
    }
}
