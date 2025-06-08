package org.example.projectjava.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {
    final String SECRET_KEY = "d8fc877cb52bfe727e8c93940947cd853f628d3e5a204648953b3535bc547c430dfdb7ee2e8bb8449eb033f0cb77fb51633c089edc3a7f3569eaf5449dc5c2a1595185167d84c8b711defb70672d8f857f000bedfb5b8cf120db65b55a1c2ff2781ea0ddc06600c77548755e96deb6826cf6608bcfed96a0af56639bef3bdd5bfe347345c824ff224733f0cb7c83af34ed353e0fdf4dec5174e554a696bff7434b5ebe236a1f4f967230dd0b4889ef4da951fb08c3e322b03e42076a4eb17d39877d6d4c97a04bfed72e040684ecbfdd8d1a35a8022ab1489ba1fbf9f63f108ebb18e6510295f717f2a199a362378388dcaecfa7d71dc53862abf784b65970c39a6d02d6b0a26c7f89a02826382de9a6d14d67c977f7d6fe68233dd86514718bcdd12f8693f87fbb401965c067fd23ebe174ff37e75deaa1ce7334cd2922de2c0c933c9251ead59479aa6ef9bbc6a642fca3ca9f1e9cdd4e45cc3a143bba6e34a6966727a59758d589a895be7c0a15139ae0ded8dc14e1819325649384627ee62b5fcb2451b3ba0402b42ac1b87000ebc7514c1cb364ebe391c0e6e42ec41cd1963b2ff0c66c6b4494e7011c809830e281f19e0b1328b8f0f21a3f930749c811a4810a54ad182187cd75ae5c4094909941d4b55be2ca7dd0fc2c3b3bae94c79bb5eb396f32d64313696952d7afee3f203f6de31592c4a8d03c8ab08aa1c5ce9e";

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        try {
            extractAllClaims(token);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
