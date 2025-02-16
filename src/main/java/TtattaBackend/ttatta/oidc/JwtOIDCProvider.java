package TtattaBackend.ttatta.oidc;

import TtattaBackend.ttatta.apiPayload.code.status.ErrorStatus;
import TtattaBackend.ttatta.apiPayload.exception.handler.ExceptionHandler;
import io.jsonwebtoken.*;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
public class JwtOIDCProvider {

    // Header, body, VerifyingSignature 중에서 Header와 body만 가져옴
    private String getUnsignedToken(String token) {
        String[] splitToken = token.split("\\.");
        if (splitToken.length == 3) throw new ExceptionHandler(ErrorStatus.INVALID_TOKEN);
        return splitToken[0] + "." + splitToken[1] + ".";
    }

    // 페이로드 검증
    // iss, aud, exp 값 확인
    private Jwt<Header, Claims> getUnsignedTokenClaims(String token, String iss, String aud) {
        try {
            return Jwts.parserBuilder()
                    .requireAudience(aud)
                    .requireIssuer(iss)
                    .build()
                    .parseClaimsJwt(getUnsignedToken(token));
        } catch (ExpiredJwtException e) {
            throw new ExceptionHandler(ErrorStatus.TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new ExceptionHandler(ErrorStatus.INVALID_TOKEN);
        }
    }


    // 공개키 목록에서 쓸 Kid 가져오기
    private final String KID = "kid";

    public String getKidFromUnsignedTokenHeader(String token, String iss, String aud) {
        return (String) getUnsignedTokenClaims(token, iss, aud).getHeader().get(KID);
    }

    // 제일 핵심이 되는 소스이다. n ,e 값으로 Rsa 공개키를 연산 할 수 있다.
    private Key getRSAPublicKey(String modulus, String exponent)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        // RSA KeyFactory 생성
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        // Base64 URL 디코더를 사요앟여 modulus와 exponent를 디코딩한다.
        byte[] decodeN = Base64.getUrlDecoder().decode(modulus);
        byte[] decodeE = Base64.getUrlDecoder().decode(exponent);

        // 디코딩된 byte 배열을 BigInteger로 변환한다.
        BigInteger n = new BigInteger(1, decodeN);
        BigInteger e = new BigInteger(1, decodeE);

        // RSAPublicKeySpec을 생성한다.
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);

        // KeyFactory를 사용하여 공개키를 생성한다.
        return keyFactory.generatePublic(keySpec);
    }

    // 공개키로 토큰 검증을 시도한다.
    public Jws<Claims> getOIDCTokenJws(String token, String modulus, String exponent) {
        try {
            // getRSAPublicKey에서 생성한 공개키를 통해 Jwts parser를 이용하여 서명을 검증한다.
            // Claim을 반환한다.
            return Jwts.parserBuilder()
                    .setSigningKey(getRSAPublicKey(modulus, exponent))
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ExceptionHandler(ErrorStatus.TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new ExceptionHandler(ErrorStatus.INVALID_TOKEN);
        }
    }

    // OIDCDecodePayload 를 가져온다. 스펙이라 공통으로 사용할 수 있다.
    // 반환한 Claim에서 필요한 정보를 추출한다.
    public OIDCDecodePayload getOIDCTokenBody(String token, String modulus, String exponent) {
        Claims body = getOIDCTokenJws(token, modulus, exponent).getBody();
        return new OIDCDecodePayload(
                body.getIssuer(),
                body.getAudience(),
                body.getSubject());
    }
}
