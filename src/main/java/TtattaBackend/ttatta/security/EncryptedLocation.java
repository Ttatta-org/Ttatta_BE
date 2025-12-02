package TtattaBackend.ttatta.security;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EncryptedLocation {
    byte[] latCipher;
    byte[] lngCipher;
    byte[] ivLat;
    byte[] ivLng;
    byte[] dekWrapped;
    String kmsKeyId;
    short encVer;
}
