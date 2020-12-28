package cn.visolink.common.security.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Date;

/**
 * @author WCL
 * @date 2018-11-23
 * 返回token
 */
@Getter
@AllArgsConstructor
public class AuthenticationInfo implements Serializable {

    private final String token;

    private final String refreshToken;

    private final JwtUser user;

    private  final Date expiration;
}
