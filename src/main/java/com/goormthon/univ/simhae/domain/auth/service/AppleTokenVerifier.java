package com.goormthon.univ.simhae.domain.auth.service;


import com.goormthon.univ.simhae.global.config.AppleProps;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;

import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.stereotype.Component;

@Component
public class AppleTokenVerifier {
    private final AppleProps props;
    private final JWSKeySelector<SecurityContext> keySelector;

    public AppleTokenVerifier(AppleProps props) {
        this.props = props;
        try {
            this.keySelector = new JWSVerificationKeySelector<>(
                    JWSAlgorithm.RS256,
                    new RemoteJWKSet<>(new URL(props.jwkSetUri())) // ← 여기서 URL 생성 + try/catch
            );
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid JWK Set URI: " + props.jwkSetUri(), e);
        }
    }

    public AppleClaims verify(String idToken, String noncePlainOrSha256) {
        try {
            SignedJWT jwt = SignedJWT.parse(idToken);
            DefaultJWTProcessor<SecurityContext> proc = new DefaultJWTProcessor<>();
            proc.setJWSKeySelector(keySelector);
            JWTClaimsSet claims = proc.process(jwt, null);

            if (!props.issuer().equals(claims.getIssuer())) throw new IllegalArgumentException("bad iss");
            if (claims.getAudience() == null || !claims.getAudience().contains(props.clientId()))
                throw new IllegalArgumentException("bad aud");
            if (new Date().after(claims.getExpirationTime())) throw new IllegalArgumentException("expired");

            String expected = hashIfNeeded(noncePlainOrSha256);
            String tokenNonce = claims.getStringClaim("nonce");
            if (expected != null && (tokenNonce == null || !tokenNonce.equalsIgnoreCase(expected)))
                throw new IllegalArgumentException("bad nonce");

            return new AppleClaims(claims.getStringClaim("sub"), claims.getStringClaim("email"));
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid id_token: " + e.getMessage(), e);
        }
    }

    private static String hashIfNeeded(String nonce) {
        if (nonce == null || nonce.isBlank()) return null;
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] d = md.digest(nonce.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : d) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public record AppleClaims(String sub, String email) {}
}
