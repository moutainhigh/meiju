package cn.visolink.system.usermanager.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.usermanager.service.UserManagerService;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author wjc
 * @date 2019/09/11
 */
@RestController
@RequestMapping("/userManager")
public class UserManagerController {
    @Autowired
    private UserManagerService userMessageService;
    /**
     * 查询子集参数（树形）
     *
     * @return
     */
    @Log("获取用户信息")
    @CessBody
    @ApiOperation(value = "获取用户信息")
    @PostMapping("getUserMessage.action")
    public Map getUserMessage(@RequestBody Map map) {
        return userMessageService.findMessage(map);
    }

    /**
     * 启用/禁用参数
     *
     * @return
     */
    @Log("启用/禁用参数")
    @CessBody
    @ApiOperation(value = "启用/禁用参数")
    @PostMapping("updateUserStatus.action")
    public int updateUserStatus(@RequestBody Map map) {
        int i = userMessageService.updateUserStatus(map);
        return i;
    }

    /**
     * 删除用户
     *
     * @return
     */
    @Log("删除用户")
    @CessBody
    @ApiOperation(value = "删除用户")
    @PostMapping("deleteUsers.action")
    public int deleteUsers(@RequestBody Map map) {

        return userMessageService.deleteUser(map);
    }


    /**
     * 编辑用户（树形）
     *
     * @param
     * @return
     */
    @Log("编辑用户")
    @CessBody
    @ApiOperation(value = "编辑用户")
    @PostMapping("updateUsers.action")
    public int execute(@RequestBody Map map){
        int s = userMessageService.systemUserUpdate(map);
        return s;

    }
}
