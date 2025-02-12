package TtattaBackend.ttatta.Oidc;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OIDCPublicKeyResponse {
    List<OIDCPublicKeyDto> keys;
}
