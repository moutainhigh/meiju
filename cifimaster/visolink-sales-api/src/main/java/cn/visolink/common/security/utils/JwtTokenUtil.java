package cn.visolink.common.security.utils;

import cn.visolink.common.security.security.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author WCL
 */
@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -3301605591108950415L;
    private Clock clock = DefaultClock.INSTANCE;

    @Value("${jwt.secret}")
    private String secret;


    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh.expiration}")
    private Long refreshExpiration;

    @Value("${jwt.header}")
    private String tokenHeader;
    /**
     * 获取用户名从token中
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    /**
     * 获取jwt发布时间
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }
    /**
     * 获取jwt失效时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    /**
     * 获取jwt的payload部分
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
    /**
     * <pre>
     *  验证token是否失效
     *  true:过期   false:没过期
     * </pre>
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private Boolean ignoreTokenExpiration(String token) {
        // here you specify tokens, for that the expiration is ignored
        return false;
    }
    /**
     * 生成token(通过用户名和签名时候用的随机数)
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<String, Object>();
        return doGenerateToken(claims, userDetails.getUsername());
    }
    /**
     * 生成token
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getIssuedAtDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token) || ignoreTokenExpiration(token));
    }

    /**
     * 生成刷新Token
     * @param token
     * @return
     */
    public String doGenerateRefreshToken(String token) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, refreshSecret)
                .compact();
    }

    /**
     * 校验token
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        JwtUser user = (JwtUser) userDetails;
        final Date created = getIssuedAtDateFromToken(token);
//        final Date expiration = getExpirationDateFromToken(token);
//        如果token存在，且token创建日期 > 最后修改密码的日期 则代表token有效
        return (!isTokenExpired(token)
                && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate())
        );
    }
    /**
     * 校验token
     * @param token
     * @return
     */
    public Boolean validateToken(String token ) {
        return isTokenExpired(token);
    }
    /**
     * <pre>
     *  验证RefreshToken是否失效
     *  true:过期   false:没过期
     * </pre>
     */
    public Boolean isRefreshTokenExpired(String token) {

        Claims claim = Jwts.parser()
                .setSigningKey(refreshSecret)
                .parseClaimsJws(token)
                .getBody();
        com.google.common.base.Function<Claims, Date> getExpiration = Claims::getExpiration;
        Date apply = getExpiration.apply(claim);
        return apply.after(clock.now());
    }

    /**
     * 刷新Token
     * @param token
     * @return
     */
    public String refreshToken(String token) {
        final Date createdDate = clock.now();
        final Date expirationDate = calculateExpirationDate(createdDate);

        final Claims claims = Jwts.parser()
                .setSigningKey(refreshSecret)
                .parseClaimsJws(token)
                .getBody();
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.getSubject())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    /**
     * 计算token失效时间
     * @param createdDate
     * @return
     */
    private Date calculateExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration*1000);
    }
    /**
     * 计算刷新token失效时间
     * @param createdDate
     * @return
     */
    private Date calculateRefreshTokenExpirationDate(Date createdDate) {
        return new Date(createdDate.getTime() + expiration*1000*60*60);
    }
}
