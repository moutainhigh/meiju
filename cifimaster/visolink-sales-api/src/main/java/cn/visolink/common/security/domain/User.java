package cn.visolink.common.security.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author WCL
 * @date 2018-11-22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_student")
public class User implements Serializable {

    @ApiModelProperty("用户Id")
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "用户名称",dataType = "String")
    private String username;

    private String password;

    private String avatar;

    @NotBlank
    @Pattern(regexp = "([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}",message = "格式错误")
    private String email;

    @NotBlank
    private String phone;

    @NotNull
    private Boolean enabled;


    @CreationTimestamp
    private Timestamp createTime;

    private Date lastPasswordResetTime;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                ", enabled=" + enabled +
                ", password='" + password + '\'' +
                ", createTime=" + createTime +
                ", lastPasswordResetTime=" + lastPasswordResetTime +
                '}';
    }

    public @interface Update {}
}