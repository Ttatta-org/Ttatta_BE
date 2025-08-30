package TtattaBackend.ttatta.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DataKeySpec;
import software.amazon.awssdk.services.kms.model.GenerateDataKeyRequest;
import software.amazon.awssdk.services.kms.model.GenerateDataKeyResponse;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class EnvelopeCryptoService {

    private final KmsClient kms;
    private static final SecureRandom RNG = new SecureRandom();

    @Value("${kms.key.arn}")
    private String kmsKeyArn;

    public DataKeyPair generateDataKeyPair() {
        GenerateDataKeyResponse res = kms.generateDataKey(
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
            byte[] aadLng = mixAad("lng", ivLng);

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
}
