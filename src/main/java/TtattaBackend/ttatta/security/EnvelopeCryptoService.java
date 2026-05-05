package TtattaBackend.ttatta.security;

import TtattaBackend.ttatta.domain.Diaries;
import TtattaBackend.ttatta.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DataKeySpec;
import software.amazon.awssdk.services.kms.model.GenerateDataKeyRequest;
import software.amazon.awssdk.services.kms.model.GenerateDataKeyResponse;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnvelopeCryptoService {

    private static final SecureRandom RNG = new SecureRandom();
    private final KmsClient kmsClient;
    private final DiaryRepository diaryRepository;

    @Value("${kms.key.arn}")
    private String kmsKeyArn;

    @Value("${aes.secret-key}")
    private String aesSecret;

    // AES-256 암호화
    public EncryptedLocation aesEncryptLatLng(double latitude, double longitude, Long userId) {
        byte[] key = Base64.getDecoder().decode(aesSecret);
        byte[] ivLat = randomIV();
        byte[] ivLng = randomIV();
        byte[] aadBase = ByteBuffer.allocate(8).putLong(userId).array();
        byte[] latCt = aesGcmEncrypt(key, ivLat, doubleToBytes(latitude), mixAad("lat", aadBase));
        byte[] lngCt = aesGcmEncrypt(key, ivLng, doubleToBytes(longitude), mixAad("lng", aadBase));
        return EncryptedLocation.builder()
                .latCipher(latCt).lngCipher(lngCt)
                .ivLat(ivLat).ivLng(ivLng)
                .encVer((short) 2)
                .build();
    }

    // AES-256 복호화
    public DecryptedLocation aesDecryptLatLng(
            byte[] latCipher, byte[] ivLat,
            byte[] lngCipher, byte[] ivLng,
            Long userId) {
        byte[] key = Base64.getDecoder().decode(aesSecret);
        byte[] aadBase = ByteBuffer.allocate(8).putLong(userId).array();
        double lat = aesGcmDecryptToDouble(latCipher, ivLat, key, mixAad("lat", aadBase));
        double lng = aesGcmDecryptToDouble(lngCipher, ivLng, key, mixAad("lng", aadBase));
        return new DecryptedLocation(lat, lng);
    }

    // encVer에 따라 복호화 분기
    public DecryptedLocation smartDecrypt(Diaries diary) {
        if (diary.getEncVer() == 1) {
            return new DecryptedLocation(diary.getLatitude(), diary.getLongitude());
        }
        return aesDecryptLatLng(
                diary.getLatCipher(), diary.getIvLat(),
                diary.getLngCipher(), diary.getIvLng(),
                diary.getUsers().getId()
        );
    }


    /**
     *
     * AWS KMS 암호화
     */
    public DataKeyPair generateDataKeyPair() {
        GenerateDataKeyResponse res = kmsClient.generateDataKey(
                GenerateDataKeyRequest.builder()
                        .keyId(kmsKeyArn)
                        .keySpec(DataKeySpec.AES_256)
                        .build()
        );
        byte[] dekPlain = res.plaintext().asByteArray();
        byte[] dekWrapped = res.ciphertextBlob().asByteArray();
        return new DataKeyPair(dekPlain, dekWrapped);
    }

    /** 유틸: 민감 바이트 덮어쓰기 */
    public static void zeroize(byte[] b) {
        if (b != null) java.util.Arrays.fill(b, (byte) 0);
    }

    public EncryptedLocation encryptLatLng(double latitude, double longitude, Long aadUserId) {
        DataKeyPair dk = generateDataKeyPair();
        try {
            // 1) IV를 각 필드별로 분리(12바이트 권장)
            byte[] ivLat = randomIV();
            byte[] ivLng = randomIV();

            // 2) AAD 구성(선택): userId + 필드명 분리
            byte[] aadBase = (aadUserId == null) ? null : ByteBuffer.allocate(8).putLong(aadUserId).array();
            byte[] aadLat = mixAad("lat", aadBase);
            byte[] aadLng = mixAad("lng", aadBase);

            byte[] latCt = aesGcmEncrypt(dk.getDekPlain(), ivLat, doubleToBytes(latitude), aadLat);
            byte[] lngCt = aesGcmEncrypt(dk.getDekPlain(), ivLng, doubleToBytes(longitude), aadLng);

            return EncryptedLocation.builder()
                    .latCipher(latCt)
                    .lngCipher(lngCt)
                    .ivLat(ivLat)
                    .ivLng(ivLng)
                    .dekWrapped(dk.getDekWrapped())
                    .kmsKeyId(kmsKeyArn)
                    .encVer((short)1)
                    .build();
        } finally {
            // DEK 는 반드시 폐기
            zeroize(dk.getDekPlain());
        }
    }

    // ===== 로컬 헬퍼 =====
    private static byte[] aesGcmEncrypt(byte[] key, byte[] iv, byte[] plaintext, byte[] aad) {
        try {
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), spec);
            if (aad != null) c.updateAAD(aad);
            return c.doFinal(plaintext); // [cipher || tag]
        } catch (Exception e) {
            throw new IllegalStateException("AES-GCM encrypt failed", e);
        }
    }

    private static byte[] randomIV() {
        byte[] iv = new byte[12]; // 96-bit 권장
        RNG.nextBytes(iv);
        return iv;
    }

    private static byte[] doubleToBytes(double v) {
        return ByteBuffer.allocate(8).putDouble(v).array();
    }

    private static byte[] mixAad(String field, byte[] base) {
        byte[] f = field.getBytes(StandardCharsets.UTF_8);
        if (base == null) return f;
        byte[] out = new byte[f.length + base.length];
        System.arraycopy(f, 0, out, 0, f.length);
        System.arraycopy(base, 0, out, f.length, base.length);
        return out;
    }

    public DecryptedLocation decryptLatLng(
            byte[] latCipher, byte[] ivLat,
            byte[] lngCipher, byte[] ivLng,
            byte[] dekWrapped, String kmsKeyId,
            Long userId // AAD
    ) {
        // 1) KMS를 통해 DEK 복호화
        byte[] dek = kmsClient.decrypt(
                r -> r.ciphertextBlob(SdkBytes.fromByteArray(dekWrapped))
                .keyId(kmsKeyId))
                .plaintext().asByteArray();

        // 2) AES-GCM 복호화 수행
        byte[] aadBase = (userId == null) ? null : ByteBuffer.allocate(8).putLong(userId).array();
        byte[] aadLat = mixAad("lat", aadBase);
        byte[] aadLng = mixAad("lng", aadBase);
        double lat = aesGcmDecryptToDouble(latCipher, ivLat, dek, aadLat);
        double lng = aesGcmDecryptToDouble(lngCipher, ivLng, dek, aadLng);

        return new DecryptedLocation(lat, lng);
    }


    @Transactional
    public int migrateToAes() {
        List<Diaries> allDiaries = diaryRepository.findAll();
        int successCount = 0;
        for (Diaries diary : allDiaries) {
            try {
                if (diary.getEncVer() == 2) continue;
                Long userId = diary.getUsers().getId();
                EncryptedLocation enc = aesEncryptLatLng(diary.getLatitude(), diary.getLongitude(), userId);
                diary.updateEncryption(enc.getLatCipher(), enc.getLngCipher(), enc.getIvLat(), enc.getIvLng(), (short) 2);
                successCount++;
                log.info("마이그레이션 성공 - Diary ID: {}", diary.getId());
            } catch (Exception e) {
                log.error("마이그레이션 실패 - Diary ID: {} | 사유: {}", diary.getId(), e.getMessage());
            }
        }
        return successCount;
    }

    @Transactional
    public int decrypt() { // 파라미터 필요 없음!
        List<Diaries> allDiaries = diaryRepository.findAll();
        int successCount = 0;

        for (Diaries diary : allDiaries) {
            try {
                // 1) DB 컬럼에 저장된 해당 데이터의 KMS Key ID를 가져옵니다.
                String kmsKeyId = diary.getKmsKeyId();
                byte[] individualDekWrapped = diary.getDekWrapped();

                // 2) 해당 키 ID를 지정하여 DEK 복호화
                byte[] dek = kmsClient.decrypt(r -> r.ciphertextBlob(SdkBytes.fromByteArray(individualDekWrapped))
                                .keyId(kmsKeyId))
                        .plaintext().asByteArray();

                // 3) AES-GCM 복호화 (userId 포함)
                Long userId = (diary.getUsers() != null) ? diary.getUsers().getId() : null;
                byte[] aadBase = (userId == null) ? null : ByteBuffer.allocate(8).putLong(userId).array();
                byte[] aadLat = mixAad("lat", aadBase);
                byte[] aadLng = mixAad("lng", aadBase);

                double lat = aesGcmDecryptToDouble(diary.getLatCipher(), diary.getIvLat(), dek, aadLat);
                double lng = aesGcmDecryptToDouble(diary.getLngCipher(), diary.getIvLng(), dek, aadLng);

                // 4) 평문 업데이트 및 성공 카운트 증가
                diary.updateLocation(lat, lng);
                successCount++;

                log.info("성공 - Diary ID: {}, 사용된 Key: {}", diary.getId(), kmsKeyId);
            } catch (Exception e) {
                log.error("실패 - Diary ID: {} | 사유: {}", diary.getId(), e.getMessage());
            }
        }
        return successCount; // 처리된 총 개수 반환
    }

    private double aesGcmDecryptToDouble(byte[] cipher, byte[] iv, byte[] dek, byte[] aad) {
        try {
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(dek, "AES");
            GCMParameterSpec gcm = new GCMParameterSpec(128, iv);
            c.init(Cipher.DECRYPT_MODE, keySpec, gcm);

            // AAD = userId 고정 (저장 시와 동일해야함)
            if (aad != null) c.updateAAD(aad);
            byte[] plain = c.doFinal(cipher);

            // 평문은 8바이트 IEEE-754 double이어야 함
            if (plain.length != 8) {
                throw new IllegalStateException("Unexpected plaintext length: " + plain.length);
            }
            return ByteBuffer.wrap(plain).getDouble();
        } catch (Exception e) {
            throw new RuntimeException("AES-GCM encrypt failed", e);
        }
    }
}
