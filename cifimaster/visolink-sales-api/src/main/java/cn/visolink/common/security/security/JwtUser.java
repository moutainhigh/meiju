package cn.visolink.common.security.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author WCL
 * @date 2018-11-23
 */
@Getter
@AllArgsConstructor
public class JwtUser implements UserDetails {

    private final String id;

    private final String username;

    @JsonIgnore
    private final String password;


    private final String accountType;

    private final String employeeCode;

    private final String employeeName;

    private final int gender;

    private final String mobile;

    private final String address;

    private final String projectId;

    private final String projectName;

    private final String authCompanyId;

    private final String productId;

    private final String creator;

    private final String createTime;

    private final String status;

    private final String isDel;

    private final String JobID;

    private final Map job;

    private final Map orgInfo;

    private final Map menus;


    @JsonIgnore
    private final Collection<GrantedAuthority> authorities;

    private final boolean enabled;


    @JsonIgnore
    private final Date lastPasswordResetDate;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public Collection getRoles() {
        return null;
//        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }
}
