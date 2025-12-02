package TtattaBackend.ttatta.security;

import lombok.Value;

@Value
public class DataKeyPair {
    byte[] dekPlain;   // AES-256 평문 DEK (즉시 사용 후 파기 예정)
    byte[] dekWrapped; // KMS가 래핑한 DEK(CiphertextBlob)
}
