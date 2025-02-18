package TtattaBackend.ttatta.oidc;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OIDCPublicKeyResponse {
    List<OIDCPublicKeyDto> keys;
}
