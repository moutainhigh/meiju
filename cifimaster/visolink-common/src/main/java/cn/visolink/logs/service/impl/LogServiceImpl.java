package cn.visolink.logs.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.visolink.logs.aop.log.Log;
import cn.visolink.logs.service.mapper.LogErrorMapper;
import cn.visolink.logs.repository.LogRepository;
import cn.visolink.logs.service.LogService;
import cn.visolink.logs.service.dto.LogQueryCriteria;
import cn.visolink.logs.service.mapper.LogSmallMapper;
import cn.visolink.utils.PageUtil;
import cn.visolink.utils.QueryHelp;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author WCL
 * @date 2018-11-24
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private LogErrorMapper logErrorMapper;

    @Autowired
    private LogSmallMapper logSmallMapper;

    private final String LOGINPATH = "login";

    @Override
    public Object queryAll(LogQueryCriteria criteria, Pageable pageable){
        Page<cn.visolink.logs.domain.Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)),pageable);
        if ("ERROR".equals(criteria.getLogType())) {
            return PageUtil.toPage(page.map(logErrorMapper::toDto));
        }
        return page;
    }

    @Override
    public Object queryAllByUser(LogQueryCriteria criteria, Pageable pageable) {
        Page<cn.visolink.logs.domain.Log> page = logRepository.findAll(((root, criteriaQuery, cb) -> QueryHelp.getPredicate(root, criteria, cb)),pageable);
        return PageUtil.toPage(page.map(logSmallMapper::toDto));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String ip, ProceedingJoinPoint joinPoint, cn.visolink.logs.domain.Log log){

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Log aopLog = method.getAnnotation(Log.class);

        // 描述
        if (log != null) {
            log.setDescription(aopLog.value());
            log.setId(RandomUtil.randomUUID());
            // 获取IP地址
            log.setRequestIp(ip);
        }else{
            log=new cn.visolink.logs.domain.Log();
        }
        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName()+"."+signature.getName()+"()";

        String params = "{";
        //参数值
        Object[] argValues = joinPoint.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
        if(argValues != null){
            for (int i = 0; i < argValues.length; i++) {
                params += " " + argNames[i] + ": " + argValues[i];
            }
        }
        if(LOGINPATH.equals(signature.getName())){
            try {
                if(argValues!=null){
                    JSONObject jsonObject = new JSONObject(argValues[0]);
                    username = jsonObject.get("username").toString();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        log.setMethod(methodName);
        log.setUsername(username);
        log.setParams(params + " }");
        logRepository.save(log);
    }

    @Override
    public Object findByErrDetail(Long id) {
        return Dict.create().set("exception",logRepository.findExceptionById(id));
    }
}
