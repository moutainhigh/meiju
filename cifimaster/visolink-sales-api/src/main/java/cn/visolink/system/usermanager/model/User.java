package cn.visolink.system.usermanager.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wjc
 * @date 2019/09/11
 */
@Getter
@Setter
public class User {
    private String userId;
    private String userName;
    private String employeeName;
    private String jobId;
    private String jobName;
    private String orgId;
    private String orgName;
    private String authCompanyId;
    private String authCompanyName;
    private String productId;
    private String productName;
    private String projectId;
    private String projectName;
    private String jobIds;
    private String frameworkScriptUrl;
    private String orgIsLast;
}
