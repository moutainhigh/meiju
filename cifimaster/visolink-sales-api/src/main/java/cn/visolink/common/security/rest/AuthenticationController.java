package cn.visolink.common.security.rest;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import cn.visolink.common.redis.RedisUtil;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.common.security.domain.JsonRootBean;
import cn.visolink.common.security.security.AuthenticationInfo;
import cn.visolink.common.security.security.AuthorizationUser;
import cn.visolink.common.security.security.ImgResult;
import cn.visolink.common.security.security.JwtUser;
import cn.visolink.common.security.service.JwtUserDetailsServiceImpl;
import cn.visolink.common.security.utils.HttpClient;
import cn.visolink.common.security.utils.JwtTokenUtil;
import cn.visolink.common.security.utils.VerifyCodeUtils;
import cn.visolink.constant.BizConstant;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.utils.HttpClientUtil;
import cn.visolink.utils.SecurityUtils;
import com.alibaba.fastjson.JSONObject;
import io.cess.core.spring.CessBody;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author WCL
 * @date 2018-11-23
 * ???????????????token????????????????????????
 */
@Slf4j
@Controller
@RequestMapping("auth")
@Api(tags = "????????????")
public class AuthenticationController {

    @Value("${jwt.header}")
    private String tokenHeader;
    /**
     * app??????
     */
    @Value("${sso.appUser}")
    private String appUser;
    /**
     * SSO?????? ??????
     */
    @Value("${sso.privateKey}")
    private String privateKey;
    /**
     * SSO????????????
     */
    @Value("${sso.url}")
    private String url;

    @Value("${sso.tempTokenUrl}")
    private String tempTokenUrl;

    @Value("${sso.accessUrl}")
    private String accessUrl;

    @Value("${DingDing.appkey}")
    private String appkey;

    @Value("${DingDing.appsecret}")
    private String appsecret;

    @Value("${DingDing.accessTokenIp}")
    private String accessTokenIp;

    @Value("${DingDing.userIdIp}")
    private String userIdIp;

    @Value("${DingDing.userInfoIp}")
    private String userInfoIp;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisService redisService;

    @Autowired
    @Qualifier("jwtUserDetailsServiceImpl")
    private JwtUserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private RedisUtil redisUtil;



    /**
     * ????????????
     *
     * @param authorizationUser
     * @return
     */
    @CessBody
    @PostMapping(value = "${jwt.auth.path}")
    @ApiOperation(value = "??????", httpMethod = "POST")
    public AuthenticationInfo login(@RequestBody AuthorizationUser authorizationUser) {
        Map<String, String> stringMap = new HashMap<>(1);
        Map accountTypeInfo = authMapper.mGetAccountType(authorizationUser.getUsername());
        if (MapUtil.isEmpty(accountTypeInfo)) {
            throw new BadRequestException(-10_0002, "???????????????,?????????????????????!");
        }
        //??????????????????
        Integer accountType = Integer.valueOf(accountTypeInfo.get("AccountType").toString());
        //????????????ID
        String userId = String.valueOf(accountTypeInfo.get("ID"));
        //??????????????????
        String orgCate = String.valueOf(accountTypeInfo.get("OrgCategory"));
        String inputPassword = authorizationUser.getPassword();
        if (!StringUtils.isEmpty(accountTypeInfo.get("AuditID")) && "0".equals(accountTypeInfo.get("IsAudit"))) {
            throw new BadRequestException(-10_0006, "?????????????????????????????????????????????!");
        }

        String password = String.valueOf(accountTypeInfo.get("Password"));
        //sso??????
        if(accountType==1) {
            JsonRootBean ssoLogin = isSSOLogin(authorizationUser.getUsername(), inputPassword);
            if (!ssoLogin.getSuccess()) {
                throw new BadRequestException(-10_0014, "???????????????,??????????????????!");
            }
        }else{
            if (!DigestUtils.md5Hex(inputPassword).equalsIgnoreCase(password)) {
                throw new BadRequestException(-10_0007, "???????????????,???????????????!");
            }
        }
        if (BizConstant.status_disable.equals(accountTypeInfo.get("Status"))||Integer.parseInt(accountTypeInfo.get("Status")+"")==0) {
            throw new BadRequestException(-10_0008, "????????????????????????????????????????????????!");

        }


        if (BizConstant.deleted.equals(accountTypeInfo.get("IsDel"))||Integer.parseInt(accountTypeInfo.get("IsDel")+"")==1) {
            throw new BadRequestException(-10_0009, "????????????????????????????????????????????????!");
        }
        stringMap.put("UserId", userId);

        redisService.delete(VisolinkConstant.REDIS_KEY+".User"+".info"+"."+authorizationUser.getUsername());//??????????????????redis?????????????????????
        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(authorizationUser.getUsername());

//        if(!jwtUser.getPassword().equals(EncryptUtils.encryptPassword(authorizationUser.getPassword()))){
//            throw new AccountExpiredException("????????????");
//        }

//        if(!jwtUser.isEnabled()){
//            throw new AccountExpiredException("????????????????????????????????????");
//        }
        // ????????????
        final String token = jwtTokenUtil.generateToken(jwtUser);
        //???token??????redis
        redisService.saveObject(VisolinkConstant.TOKEN_KEY + "." + authorizationUser.getUsername(), authorizationUser.getUsername());
        String refreshToken = jwtTokenUtil.doGenerateRefreshToken(token);
//        redisService.saveString(jwtUser.getUsername(), token);
        // ?????? token
        Date expiration=jwtTokenUtil.getExpirationDateFromToken(token);

        return new AuthenticationInfo(token,refreshToken, jwtUser,expiration);
    }


    /**
     * ??????????????????????????????token
     */
    @PostMapping(value = "/deleteAllRedisToken")
    @ApiOperation(value = "??????", httpMethod = "POST")
    @ResponseBody
    public ResultBody delRedisToken() {
        redisService.deleteByKeyLike(VisolinkConstant.TOKEN_KEY);
        return ResultBody.success("???????????????");
    }
    /**
     * ????????????????????????
     * */

    @CessBody
    @PostMapping(value = "${jwt.dingLogin.path}")
    @ApiOperation(value = "??????????????????")
    public AuthenticationInfo dingLogin(@RequestBody Map parmas,HttpServletRequest request) {
        String code;
        if(parmas!=null && parmas.get("code")!=null){
            code=parmas.get("code").toString();
        }else{
            code = request.getParameter("code");
            if("".equals(code)||"null".equals(code)){
             return null;
            }
        }
        System.out.print(code);
        Map dingToken=new HashMap();
        dingToken.put("appkey",appkey);
        dingToken.put("appsecret",appsecret);
        String accessToken= HttpClientUtil.doGet(accessTokenIp,dingToken);
        if(accessToken==null || accessToken==""){
            return null;
        }
        JSONObject jsonToken = JSONObject.parseObject(accessToken);
        if(jsonToken==null || jsonToken.get("access_token")==null || jsonToken.get("access_token")==""){
            return null;
        }
        String token=jsonToken.get("access_token")+"";
        Map userParmas=new HashMap();
        userParmas.put("access_token",token);
        userParmas.put("code",code);
        String userId=HttpClientUtil.doGet(userIdIp,userParmas);
        JSONObject jsonUserId = JSONObject.parseObject(userId);
        if(jsonUserId.get("errcode").equals(0)|| jsonUserId.get("errcode").equals("0")){

        }else{
            return null;
        }
        String userid=jsonUserId.get("userid").toString();//????????????ID
        Map userIdParams=new HashMap();
        userIdParams.put("access_token",token);
        userIdParams.put("userid",userid);
        String userInfo=HttpClientUtil.doGet(userInfoIp,userIdParams);
        if(userInfo==null || userInfo==""){
            return null;
        }
        //????????????????????????????????????
        JSONObject userInfos = JSONObject.parseObject(userInfo);
        Map userMobile=new HashMap();
        userMobile.put("mobile",userInfos.get("mobile"));
        userMobile.put("email",userInfos.get("email"));
        Map sysUserInfo=authMapper.getUserInfoByMoblie(userMobile);
        if(sysUserInfo==null || sysUserInfo.size()==0){
            return null;
        }
        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(sysUserInfo.get("UserName")+"");
        // ??????Token??????
        final String myToken = jwtTokenUtil.generateToken(jwtUser);
        String refreshToken = jwtTokenUtil.doGenerateRefreshToken(myToken);
        Date expiration=jwtTokenUtil.getExpirationDateFromToken(myToken);
        return new AuthenticationInfo(myToken,refreshToken, jwtUser,expiration);
    }


    /**
     * ????????????????????????
     * */
    @CessBody
    @CrossOrigin
    @PostMapping(value = "${jwt.backLogin.path}")
    @ApiOperation(value = "??????????????????", httpMethod = "GET")
    public AuthenticationInfo backLogin(@RequestBody Map map, HttpServletRequest request) {
        String code;
        if(map!=null){
            if(map.get("code")!=null){
                code=map.get("code").toString();
            }else{
                code="";
            }

        }else{
            code="";
        }
        System.out.println("??????Map"+map);
        System.out.println("??????code"+code);
        System.out.println("?????????url"+tempTokenUrl);
        System.out.println("?????????url"+accessUrl);
        System.out.println(tempTokenUrl+code);
        String accessToken= HttpClient.sendPostRequest(tempTokenUrl+code,"");
        if(accessToken==null || accessToken==""){
            return null;
        }
        JSONObject jsonToken = JSONObject.parseObject(accessToken);
        System.out.println(jsonToken);
        if(jsonToken==null || jsonToken.get("access_token")==null ||
                jsonToken.get("access_token")=="" || jsonToken.get("access_token").toString().length()<13){
            return null;
        }
        String token=jsonToken.get("access_token").toString().substring(13);
        String userInfo= HttpClient.sendPostRequest(accessUrl+token,"");
        JSONObject jsonUser = JSONObject.parseObject(userInfo);
        String attributes=jsonUser.get("attributes").toString();
        JSONObject jsonAttr = JSONObject.parseObject(attributes);
        String userName=jsonAttr.get("smart-alias").toString();
        //https://sso-uat.cifi.com.cn/siam/oauth2.0/profileByJson?access_token=TGT-19633-vIo0StkcaBadby2KWckeWMc5fUPSidGsBvW0cu6vft9OxndkS1-SIAM
        Map<String, String> stringMap = new HashMap<>(1);
        //Map accountTypeInfo = authMapper.mGetAccountType(userName);
        if(userName==null || userName==""){
            return null;
        }
        redisService.delete(VisolinkConstant.REDIS_KEY+".User"+".info"+"."+userName);//??????????????????redis???????????????????????????
        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(userName);
        request.getSession().setAttribute("jobCode",jwtUser.getJob().get("JobCode"));
        // ??????Token??????
        final String myToken = jwtTokenUtil.generateToken(jwtUser);
        //???token??????redis
        redisService.saveObject(VisolinkConstant.TOKEN_KEY + "." + userName, userName);
       // redisService.setRedisObject(VisolinkConstant.TOKEN_KEY + "." + userName, myToken);
        String refreshToken = jwtTokenUtil.doGenerateRefreshToken(myToken);
        Date expiration=jwtTokenUtil.getExpirationDateFromToken(myToken);
//        redisService.saveString(jwtUser.getUsername(), token);
        // ?????? token
        return new AuthenticationInfo(myToken,refreshToken, jwtUser,expiration);
        //return null;
    }

    /**
     * ????????????????????????
     * */
    @CessBody
    @CrossOrigin
    @PostMapping(value = "/foreignLogin")
    @ApiOperation(value = "??????????????????")
    public AuthenticationInfo foreignLogin(@RequestBody Map map, HttpServletRequest request) {
        String accessToken;
        if(map!=null){
            if(map.get("accessToken")!=null){
                accessToken=map.get("accessToken").toString();
            }else{
                accessToken="";
            }

        }else{
            accessToken="";
        }

      /*  String accessToken= HttpClient.sendPostRequest(tempTokenUrl+code,"");
        if(accessToken==null || accessToken==""){
            return null;
        }
        JSONObject jsonToken = JSONObject.parseObject(accessToken);
        System.out.println(jsonToken);
        if(jsonToken==null || jsonToken.get("access_token")==null ||
                jsonToken.get("access_token")=="" || jsonToken.get("access_token").toString().length()<13){
            return null;
        }
        String token=jsonToken.get("access_token").toString().substring(13);*/
        String userInfo= HttpClient.sendPostRequest(accessUrl+accessToken,"");
        JSONObject jsonUser = JSONObject.parseObject(userInfo);
        String attributes=jsonUser.get("attributes").toString();
        JSONObject jsonAttr = JSONObject.parseObject(attributes);
        String userName=jsonAttr.get("smart-alias").toString();
        //https://sso-uat.cifi.com.cn/siam/oauth2.0/profileByJson?access_token=TGT-19633-vIo0StkcaBadby2KWckeWMc5fUPSidGsBvW0cu6vft9OxndkS1-SIAM
        Map<String, String> stringMap = new HashMap<>(1);
        //Map accountTypeInfo = authMapper.mGetAccountType(userName);
        if(userName==null || userName==""){
            return null;
        }
        redisService.delete(VisolinkConstant.REDIS_KEY+".User"+".info"+"."+userName);//??????????????????redis???????????????????????????
        final JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(userName);
        request.getSession().setAttribute("jobCode",jwtUser.getJob().get("JobCode"));
        // ??????Token??????
        final String myToken = jwtTokenUtil.generateToken(jwtUser);
        String refreshToken = jwtTokenUtil.doGenerateRefreshToken(myToken);
        Date expiration=jwtTokenUtil.getExpirationDateFromToken(myToken);
//        redisService.saveString(jwtUser.getUsername(), token);
        // ?????? token
        return new AuthenticationInfo(myToken,refreshToken, jwtUser,expiration);
        //return null;
    }

    public JsonRootBean isSSOLogin(String UserName, String Password){
        //?????????
        String randomCode= RandomUtil.randomString(8);
        //?????????
        String timestamp = DateUtil.format(new Date(), "yyyyMMddHHmmss'Z'");
        //??????
        String body="{\"userId\": \""+UserName+"\",\"password\":\""+Password+"\"}";
        //??????
        String encodeKey = DigestUtils.sha256Hex(cn.visolink.utils.StringUtils.join(appUser, randomCode, timestamp, "{" + privateKey + "}"));
        //??????
        String sign = DigestUtils.md5Hex(cn.visolink.utils.StringUtils.join(url, "&", body, "&", privateKey));
        String body1 = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .header("appuser", appUser)
                .header("randomcode", randomCode)
                .header("timestamp", timestamp)
                .header("encodekey", encodeKey)
                .header("sign", sign)
                .body(body).execute().body();
        JsonRootBean jsonObject = JSONUtil.toBean(body1, JsonRootBean.class);

        return jsonObject;
    };

    /**
     * ??????????????????
     *
     * @return
     */
    @GetMapping(value = "${jwt.auth.account}")
    public ResponseEntity getUserInfo() {
        JwtUser jwtUser = (JwtUser) userDetailsService.loadUserByUsername(SecurityUtils.getUsername());
        return ResponseEntity.ok(jwtUser);
    }


    /**
     * ????????????
     *
     * @return
     */
    @GetMapping(value = "loginOut")
    @ResponseBody
    @ApiOperation(value = "loginOut", notes = "????????????")
    public ResultBody loginOut() {
        String username = SecurityUtils.getUsername();
        redisService.delete(VisolinkConstant.REDIS_KEY+".User"+".info"+"."+username);
        redisUtil.del("orgmenu"+username);
        redisUtil.del("orgfpmenu"+username);
        return new ResultBody();
    }


    /**
     * ????????????
     *
     * @return
     */
    @GetMapping(value = "loginOutUserName")
    @ResponseBody
    @ApiOperation(value = "????????????", httpMethod = "GET")
    public ResultBody loginOutUserName(String userName) {
        redisService.delete(VisolinkConstant.REDIS_KEY+".User"+".info"+"."+userName);
        redisUtil.del("orgmenu"+userName);
        redisUtil.del("orgfpmenu"+userName);
        return new ResultBody();
    }
    /**
     * ??????Token
     *
     * @return
     */
    @PostMapping(value = "refreshToken")
    @CessBody
    @ApiOperation(value = "??????token", notes = "??????token")
    public String refreshToken(String  token) {
        try {
            //??????token??????????????????????????????
            Boolean issExpired = jwtTokenUtil.isRefreshTokenExpired(token);
            return  jwtTokenUtil.refreshToken(token);
        }catch (ExpiredJwtException e){
            throw new BadRequestException(-10_0025,"??????token?????????,?????????????????????");
        }catch (Exception e){
            throw new BadRequestException(-10_0024,"??????token??????,?????????????????????");

        }

    }

    /**
     * ???????????????
     */
    @GetMapping(value = "vCode")
    @ApiOperation(value = "???????????????", notes = "?????????????????????")
    public ImgResult getCode(String code) throws IOException {

        //??????????????????
        String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
        String uuid = RandomUtil.simpleUUID();
        redisService.saveString(uuid, verifyCode);
        System.out.println(verifyCode);
        // ????????????
        int w = 111, h = 36;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        VerifyCodeUtils.outputImage(w, h, stream, verifyCode);
        try {
            return new ImgResult(Base64.encode(stream.toByteArray()), uuid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            stream.close();
        }
    }
}
