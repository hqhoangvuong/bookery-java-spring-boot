package com.bookeryapi.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {
    private final String secretKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCrF52MaXlscHEI\n" + //
            "+ZfQAMgbA2D+4zKCN8SU+IrBHJXyKC89wderlhQGmA9zBazw4hRcy1OX+MaVSeY1\n" + //
            "XrH05iqYTpqtKH7unG19rAAJxxwjH22dTX0D+WKm7z7NAZECw+OSWzKkqtTR3fHR\n" + //
            "nK5uu4pj5XJfULCRqDONKdrjtTONxUGpkcctJAP5f2rVqWZ+LmcZk/H+cCRm43ci\n" + //
            "GC81kPmR7h/yvKftRYNdGUPRnyfx7P2/fwfW9mWmeLGS84/tD9fIPaYdTLY2nGaq\n" + //
            "BPaw2Xjekyi8txycWSXBpF6wcAynslB5VxxkcaN9Rj+BN4MZus/nvezb1j4kZ0Sk\n" + //
            "xhTRLY8bAgMBAAECggEAZRH3TSpFw6yM6ctgRk0f6O1PEGOww8IkDAjjFv9HCR16\n" + //
            "AtaPDFsFC3wqrD0vE8HKW6L1h/I7hZd5Og/tuol5oi3VK/OfQGACQlooT55i3dDE\n" + //
            "FUfgUUeyYoE0rTB+tI550kyzlxkfzbZ6EuSnGt0I/MhfjzUAiZs+kOan2xqBEP79\n" + //
            "WV9jW/Md6AdPl+cQiPYfzgsShzJT4t8zWmHrDAcnh53isjKvQ6c6GoQGfhNaFl+M\n" + //
            "76S9WnDpxlUfjclcqmqSY7yYPv6ADrnK/8kENWbX1rdY/xSV5RjPrPyOJR0dMuJo\n" + //
            "IRKCbXujwwEwq4+UCiClRdAMmR/qmuxH/MrYrJTmoQKBgQDgILyrNEh9+mMCvYdp\n" + //
            "Xqn0+s7tTrIWg1mp6F+Jl/lAGburCZNOZfqAXiddJmb+o9ByB6B+3Dj5lpIqmux3\n" + //
            "/NPmW7pMdlbahVdPjgoxsyPgthjKDWOATm3AvuVvrS1szbGJUg4hk9TK7F8DTcdu\n" + //
            "ER03TjsR3sRrRB3C8Ozm+B4JCwKBgQDDbCSUQ1mVaLyEB8TvkvdHV+N2+U57dkR0\n" + //
            "p6GWKafcmlPjFG/0QcldDw1L2Ei5cc7JyawMmuyUm51175Pcx006rk6LIrvBJF+T\n" + //
            "KUHYsy4tTDJ0fWRweQO+NrK4OXDUeSdJ4nBcK28SuYKuox65vORn6Mq/5XQIgrPG\n" + //
            "h+B4MfX8MQKBgQDcrH73FohVuYVo+lFsVu+CLLw2/SftcLCE0S3X3NFl4rBbrBbk\n" + //
            "txXY9FtPpPL9vy8KRyYoIg7a9/y8hENVtJKymhgpFs9XYrhftRumaZpmswRfVKVm\n" + //
            "fFIl2A8xUNKL8U7fRPJy7woyLl8yco9seN9KR/VzF+JPNwcSnIK0uxFflwKBgG9e\n" + //
            "oRYRr+sTDmRcHSK/accx0kMKX2zDAM5OfisawDxbgzUlf4v/Neef3V78+q0cq9GZ\n" + //
            "6kAgxnUGnl0I1QZ8Won3rnFisQeoWAOA/rKlospT42k50NS8UqLDf2S375JXHUnh\n" + //
            "D1Gnuxw7trsZ4TfO64hARdPIyzDSc+vPoeA98MbxAoGAYL6oshET6lT41g9awfKg\n" + //
            "q+6FoWXyliga5yEWpqg5N46bhUDCem1pDsN4utvP1Blx5kHkSXBMTcFwsw/WQf5k\n" + //
            "BBVx320+QNTEosEZbcrJ5moH6r7r79YDHKKQJBaau+cdkatbdsSvoKI0LwV64wKV\n" + //
            "Zu2IEMo1LKXqDwPlqx2c230=";
    private final long expiration = 3600000; // 1 hour

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Instant getExpiration() {
        return Instant.now().plus(expiration, ChronoUnit.MILLIS);
    }
}
