package cn.visolink.salesmanage.checklist.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.firstplan.commission.service.CommissionService;
import cn.visolink.firstplan.plannode.service.TopSettingTwoExcelService;
import cn.visolink.salesmanage.checklist.entity.Checklist;
import cn.visolink.salesmanage.checklist.entity.ChecklistDetail;
import cn.visolink.salesmanage.checklist.entity.ChecklistPolicy;
import cn.visolink.salesmanage.checklist.mapper.ChecklistMapper;
import cn.visolink.salesmanage.checklist.service.ChecklistDetailService;
import cn.visolink.salesmanage.checklist.service.ChecklistPolicyService;
import cn.visolink.salesmanage.checklist.service.ChecklistService;
import cn.visolink.system.timelogs.bean.SysLog;
import cn.visolink.system.timelogs.dao.TimeLogsDao;
import cn.visolink.utils.PagingParamUtil;
import cn.visolink.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 佣金核算单 服务实现类
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@Service
public class ChecklistServiceImpl extends ServiceImpl<ChecklistMapper, Checklist> implements ChecklistService {

    private final ChecklistMapper checklistMapper;
    private final ChecklistDetailService checklistDetailService;
    private final ChecklistPolicyService checklistPolicyService;
    private final TopSettingTwoExcelService topSettingTwoExcelService;
    private final CommissionService commissionService;
    private final TimeLogsDao timeLogsDao;

    @Resource(name = "jdbcTemplatemy")
    private JdbcTemplate jdbcTemplatemy;

    private static final String ZJCJ = "中介成交";
    private static final String QMJJR = "全民经纪人";
    private static final String FKHTF = "付款后退房";

    @Autowired
    public ChecklistServiceImpl(ChecklistMapper checklistMapper, ChecklistDetailService checklistDetailService, ChecklistPolicyService checklistPolicyService, TopSettingTwoExcelService topSettingTwoExcelService, CommissionService commissionService, TimeLogsDao timeLogsDao) {
        this.checklistMapper = checklistMapper;
        this.checklistDetailService = checklistDetailService;
        this.checklistPolicyService = checklistPolicyService;
        this.topSettingTwoExcelService = topSettingTwoExcelService;
        this.commissionService = commissionService;
        this.timeLogsDao = timeLogsDao;
    }

    /**
     * 新增佣金核算单
     *
     * @param checklist checklist
     * @return return
     */
    @Override
    public ResultBody insertChecklist(Checklist checklist) {
        boolean insert = this.save(checklist);
        if (!insert) {
            return ResultUtil.error(500, "新增失败");
        }
        return ResultUtil.success(checklist.getId());
    }

    /**
     * 根据id，修改佣金核算单
     *
     * @param checklist checklist
     * @return return
     */
    @Override
    public ResultBody updateChecklistById(Checklist checklist) {
        boolean update = this.updateById(checklist);
        if (!update) {
            return ResultUtil.error(500, "修改失败");
        }
        return ResultUtil.success("");
    }

    /**
     * 删除佣金核算单
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody deleteChecklist(Map<String, Object> map, HttpServletRequest request) {
        // 0 处理数据
        packageUserId(map, request);

        // 1 获取核算单
        Checklist checklist = this.getById(map.get("ids").toString());
        if (checklist == null) {
            return ResultUtil.success("删除核算单成功");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus() && 6 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "立项状态为：草稿、立项驳回、立项撤销时，才可删除");
        }

        // 2 判断 正负核算单
        List<String> ids = StrUtil.split(map.get("ids").toString(), ',');
        map.put("ids", ids);
        if (checklist.getIsFather() == 1) {
            // 正核算单
            // 2 修改 待结佣状态：在核算单外
            // 2.1 获取 待结佣ids
            List<String> commissionIds = checklistMapper.getCommissionIdsByChecklistIds(map);
            if (CollUtil.isNotEmpty(commissionIds)) {
                Integer updateCommission = checklistMapper.updateCommissionByIds2(commissionIds);
                if (updateCommission < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.error(500, "删除失败，请稍后重试");
                }
            }

            // 3 根据核算单id，删除核算单明细和负核算单明细
            Integer delete2 = checklistMapper.removeChecklistDetailByChecklistIds2(map);
            if (delete2 < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(500, "删除失败，请稍后重试");
            }

            // 4 删除 核算单
            // 4.1 获取 负核算单id
            List<String> negativeChecklistIds = checklistMapper.getNegativeChecklistIdsByIds(ids);
            ids.addAll(negativeChecklistIds);
            Integer delete = checklistMapper.removeChecklistByIds(map);
            if (delete < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(500, "删除失败，请稍后重试");
            }
        } else {
            // 2 修改 正核算单明细（未关联负核算单）
            Integer updateCommission = checklistMapper.updateIsNegativeToZero(checklist);
            if (updateCommission < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(500, "删除失败，请稍后重试");
            }

            // 3 根据核算单id，删除核算单明细
            Integer delete2 = checklistMapper.removeChecklistDetailByChecklistIds(map);
            if (delete2 < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(500, "删除失败，请稍后重试");
            }

            // 4 删除 核算单
            Integer delete = checklistMapper.removeChecklistByIds(map);
            if (delete < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(500, "删除失败，请稍后重试");
            }
        }

        return ResultUtil.success("删除核算单成功");
    }

    /**
     * 封装 用户id、当前时间
     *
     * @param map     map
     * @param request request
     */
    private void packageUserId(Map<String, Object> map, HttpServletRequest request) {
        String userId = request.getHeader("userid");
        String username = getUserName(request);
        Date date = new Date();
        map.put("userId", userId);
        map.put("username", username);
        map.put("date", date);
    }

    private String getUserName(HttpServletRequest request) {
        String username = null;
        try {
            if (StrUtil.isNotBlank(request.getHeader("employeeName"))) {
                username = URLDecoder.decode(request.getHeader("employeeName"), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return username;
    }

    /**
     * 根据id，查询佣金核算单详情
     *
     * @param id id
     * @return return
     */
    @Override
    public ResultBody getChecklistById(String id) {
        Checklist checklist = this.getById(id);
        return ResultUtil.success(checklist);
    }

    /**
     * 创建正核算单
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody createChecklist(Map<String, String> map, HttpServletRequest request) {
        // 0 判断名称是否重复
        map.put("checklistId", "");
        Map duplicateNameCheck = checklistMapper.duplicateNameCheck(map);
        if (duplicateNameCheck != null) {
            return ResultUtil.error(500, "名称已存在");
        }

        // 1 准备数据
        String commissionIds = map.get("commissionIds");
        String policyIds = map.get("policyIds");

        // 请求头数据
        String userId = request.getHeader("userid");
        String jobId = request.getHeader("jobid");
        String jobOrgId = request.getHeader("joborgid");
        /*String orgId = request.getHeader("orgid");*/
        String orgId = map.get("org_id")+"";

        String userName = getUserName(request);
        // 其它
        Date date = new Date();
        String id = UUID.randomUUID().toString().replace("-", "");
        // 封装 核算单
        Checklist checklist = packageChecklist(map, id, userId, userName, date, jobId, jobOrgId, orgId);

        // 2 获取 未在核算单内的待结佣
        List<String> commissionIdsList = StrUtil.split(commissionIds, ',');
        List<Map<String, Object>> commissions = checklistMapper.getNotCommissionByIds(commissionIdsList);
        if (CollUtil.isEmpty(commissions)) {
            return ResultUtil.error(500, "未找到待结佣数据，请稍后再试");
        }

        // 3 校验 业务归属
        String channelName = null;
        String businessAttributionCode = null;
        if (StrUtil.equals(ZJCJ, checklist.getDealType())) {
            // 渠道名称
            channelName = StringUtils.toString(commissions.get(0).get("gain_by"));
            businessAttributionCode = StringUtils.toString(commissions.get(0).get("gain_num"));

            if (StrUtil.isBlank(businessAttributionCode)) {
                return ResultUtil.error(500, "中介成交，业务归属不能为空");
            }
            for (Map<String, Object> commission : commissions) {
                if (!StrUtil.equals(businessAttributionCode, StringUtils.toString(commission.get("gain_num")))) {
                    return ResultUtil.error(500, "中介成交，不同业绩归属不可同时创建核算单");
                }
            }
        }

        // 4 新增 核算单
        checklist.setChannelName(channelName);
        checklist.setBusinessAttributionCode(businessAttributionCode);
        boolean saveChecklist = this.save(checklist);
        if (!saveChecklist) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "创建核算单失败，请稍后重试");
        }

        // 5 新增 核算单明细
        List<ChecklistDetail> checklistDetailList = packageChecklistDetails(userId, date, commissions, id, jobId, jobOrgId, orgId);
        boolean saveBatchChecklistDetail = checklistDetailService.saveBatch(checklistDetailList);
        if (!saveBatchChecklistDetail) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "创建核算单失败，请稍后重试");
        }

        // 6 新增核算单-政策中间表
        if (StrUtil.isNotBlank(policyIds)) {
            ArrayList<ChecklistPolicy> checklistPolicyList = getChecklistPolicies(policyIds, id, userId, date, jobId, jobOrgId, orgId);
            boolean saveBatchChecklistPolicy = checklistPolicyService.saveBatch(checklistPolicyList);
            if (!saveBatchChecklistPolicy) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(500, "创建核算单失败，请稍后重试");
            }
        }

        // 7 修改待结佣状态 在核算单内
        Integer updateCommission = checklistMapper.updateCommissionByIds(commissionIdsList);
        if (updateCommission < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "创建核算单失败，请稍后重试");
        }

        return ResultUtil.success(id);
    }

    /**
     * 封装 正核算单
     *
     * @param map      map
     * @param id       id
     * @param userId   userId
     * @param userName userName
     * @param date     date
     * @param jobId    jobId
     * @param jobOrgId jobOrgId
     * @param orgId    orgId
     * @return return
     */
    private Checklist packageChecklist(Map<String, String> map, String id, String userId, String userName, Date date, String jobId, String jobOrgId, String orgId) {
        Checklist checklist = new Checklist();
        checklist.setMainDataProjectId(map.get("mainDataProjectId"));
        checklist.setDivision(map.get("division"));
        checklist.setCityCompany(map.get("cityCompany"));
        checklist.setProjectName(map.get("projectName"));
        checklist.setDealType(map.get("dealType"));
        checklist.setChecklistName(map.get("checklistName"));
        checklist.setId(id);
        checklist.setProjectStatus(1);
        checklist.setPaymentClosed(BigDecimal.ZERO);
        checklist.setIsFather(1);
        checklist.setCreatorName(userName);
        checklist.setIsAbnormal(0);
        checklist.setCreator(userId);
        checklist.setCreateTime(date);
        checklist.setJobId(jobId);
        checklist.setJobOrgId(jobOrgId);
        checklist.setOrgId(orgId);
        // 核算单编号
        checklist.setChecklistCode(getChecklistCode(date));
        return checklist;
    }

    /**
     * 生成核算单-政策
     *
     * @param policyIds   policyIds
     * @param checklistId checklistId
     * @param userId      userId
     * @param date        date
     * @param jobId       jobId
     * @param jobOrgId    jobOrgId
     * @param orgId       orgId
     * @return return
     */
    private ArrayList<ChecklistPolicy> getChecklistPolicies(String policyIds, String checklistId, String userId, Date date, String jobId, String jobOrgId, String orgId) {
        ArrayList<ChecklistPolicy> checklistPolicyList = new ArrayList<>();
        List<String> policyIdList = StrUtil.split(policyIds, ',');
        for (String policyId : policyIdList) {
            ChecklistPolicy checklistPolicy = new ChecklistPolicy();
            checklistPolicy.setId(UUID.randomUUID().toString().replace("-", ""));
            checklistPolicy.setChecklistId(checklistId);
            checklistPolicy.setPolicyId(policyId);
            checklistPolicy.setCreator(userId);
            checklistPolicy.setCreateTime(date);
            checklistPolicy.setJobId(jobId);
            checklistPolicy.setJobOrgId(jobOrgId);
            checklistPolicy.setOrgId(orgId);
            checklistPolicyList.add(checklistPolicy);
        }
        return checklistPolicyList;
    }

    /**
     * 封装 正核算单明细
     *
     * @param userId      userId
     * @param date        date
     * @param commissions commissions
     * @param checklistId checklistId
     * @param jobId       jobId
     * @param jobOrgId    jobOrgId
     * @param orgId       orgId
     * @return return
     */
    private List<ChecklistDetail> packageChecklistDetails(String userId, Date date, List<Map<String, Object>> commissions, String checklistId, String jobId, String jobOrgId, String orgId) {
        List<ChecklistDetail> checklistDetailList = new ArrayList<>();
        for (Map<String, Object> commission : commissions) {
            ChecklistDetail checklistDetail = new ChecklistDetail();
            checklistDetail.setId(UUID.randomUUID().toString().replace("-", ""));
            checklistDetail.setFid(checklistId);
            checklistDetail.setChecklistId(checklistId);
            checklistDetail.setCommissionId(StringUtils.toString(commission.get("id")));
            checklistDetail.setRoomId(StringUtils.toString(commission.get("room_id")));
            checklistDetail.setBusinessAttributionCode(StringUtils.toString(commission.get("gain_num")));
            checklistDetail.setTransactionId(StringUtils.toString(commission.get("transaction_id")));
            checklistDetail.setOpportunityId(StringUtils.toString(commission.get("intention_id")));
            checklistDetail.setIsDeadlock(0);
            checklistDetail.setIsHide(0);
            checklistDetail.setIsNegative(0);
            checklistDetail.setCreator(userId);
            checklistDetail.setCreateTime(date);
            checklistDetail.setJobId(jobId);
            checklistDetail.setJobOrgId(jobOrgId);
            checklistDetail.setOrgId(orgId);
            checklistDetail.setAmountClosed(BigDecimal.ZERO);
            checklistDetail.setAmountPaid(BigDecimal.ZERO);
            if (StrUtil.equals(FKHTF, StringUtils.toString(commission.get("transaction_status")))) {
                checklistDetail.setChecklistDetailType(3);
                checklistDetail.setIsSettle(0);
                checklistDetail.setProjectAmount(new BigDecimal(StringUtils.toString(commission.get("commission_money"), "0")));
                checklistDetail.setOutstandingAmount(new BigDecimal(StringUtils.toString(commission.get("commission_money"), "0")));
                checklistDetail.setUnpaidAmount(new BigDecimal(StringUtils.toString(commission.get("commission_money"), "0")));
            } else {
                checklistDetail.setChecklistDetailType(1);
                checklistDetail.setComPaymentRatio(BigDecimal.ZERO);
            }
            checklistDetailList.add(checklistDetail);
        }
        return checklistDetailList;
    }

    /**
     * 生成 核算单编号
     *
     * @param date date
     * @return return
     */
    private String getChecklistCode(Date date) {
        String checklistCodePrefix = "HSD";
        String checklistCodeDate = DateUtil.format(date, "yyyyMMdd");
        String checklistCodePrefixAndDateAndStr = checklistCodePrefix + checklistCodeDate;
        // 获取 当期日期，最大的核算单编号
        String maxChecklistCode = checklistMapper.getMaxChecklistCode(checklistCodeDate);
        String checklistCode;
        if (StrUtil.isBlank(maxChecklistCode)) {
            checklistCode = checklistCodePrefixAndDateAndStr + "0001";
        } else {
            String checklistCodeNumber = String.format("%04d", Integer.parseInt(maxChecklistCode.substring(maxChecklistCode.length() - 4)) + 1);
            checklistCode = checklistCodePrefixAndDateAndStr + checklistCodeNumber;
        }
        return checklistCode;
    }

    /**
     * 分页查询，佣金核算单
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody getChecklistListPage(Map<String, String> map) {
        // 1 拼接排序条件
        if (StrUtil.isBlank(map.get("orderBy")) || StrUtil.isBlank(map.get("orderType"))) {
            map.put("orderBy", "create_time DESC");
        } else {
            map.put("orderBy", map.get("orderBy") + " " + map.get("orderType"));
        }

        Page<Map> page = PagingParamUtil.getPage(map);
        map.put("projectStatus", StrUtil.replace(map.get("projectStatus"), ",", "','"));
        List<Map> list = checklistMapper.getChecklistListPage(page, map);
        page.setRecords(list);
        return ResultUtil.success(page);
    }

    /**
     * 分页查询，佣金核算单明细
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody getChecklistDetailListPage(Map<String, String> map) {
        // 1 拼接排序条件
        if (StrUtil.isBlank(map.get("orderBy")) || StrUtil.isBlank(map.get("orderType"))) {
            map.put("orderBy", "checklist_detail_type DESC,create_time DESC,id");
        } else {
            map.put("orderBy", map.get("orderBy") + " " + map.get("orderType"));
        }

        Page<Map> page = PagingParamUtil.getPage(map);
        List<Map> list = checklistMapper.getChecklistDetailListPage(page, map);
        page.setRecords(list);
        return ResultUtil.success(page);
    }

    /**
     * 分页查询，佣金核算单-政策
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody getChecklistPolicyListPage(Map<String, String> map) {
        Page<Map> page = PagingParamUtil.getPage(map);
        List<Map> list = checklistMapper.getChecklistPolicyListPage(page, map);
        page.setRecords(list);
        return ResultUtil.success(page);
    }

    /**
     * 关联核算单
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody relatedChecklist(Map<String, String> map, HttpServletRequest request) {
        // 1 获取 核算单
        Checklist checklist = this.getById(map.get("checklistIds"));
        if (checklist == null) {
            return ResultUtil.error(500, "未找到核算单，请稍后重试");
        }
        if (checklist.getIsFather() == 0) {
            return ResultUtil.error(500, "不能关联负核算单");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "立项状态需为：草稿、立项驳回");
        }

        // 2 获取 待结佣（不在核算单内）
        List<String> commissionIdList = StrUtil.split(map.get("commissionIds"), ',');
        List<Map<String, Object>> commissions = checklistMapper.getNotCommissionByIds(commissionIdList);
        if (CollUtil.isEmpty(commissions)) {
            return ResultUtil.error(500, "未找到待结佣数据，请稍后再试");
        }

        // 3 判断 数据是否合法
        String dealType = checklist.getDealType();
        if (StrUtil.equals(ZJCJ, dealType)) {
            String businessAttributionCode = checklist.getBusinessAttributionCode();
            for (Map<String, Object> commission : commissions) {
                if (!StrUtil.equals(businessAttributionCode, StringUtils.toString(commission.get("gain_num")))) {
                    return ResultUtil.error(500, "中介成交，不同业绩归属，不可关联核算单");
                }
            }
        }

        String id = checklist.getId();
        // 4 新增核算单明细
        List<ChecklistDetail> checklistDetailList = packageChecklistDetails(request.getHeader("userid"), new Date(), commissions, id,
                request.getHeader("jobid"), request.getHeader("joborgid"), request.getHeader("orgid"));
        boolean saveBatchChecklistDetail = checklistDetailService.saveBatch(checklistDetailList);
        if (!saveBatchChecklistDetail) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "关联核算单失败，请稍后重试");
        }

        // 5 修改待结佣状态 在核算单内
        Integer updateCommission = checklistMapper.updateCommissionByIds(commissionIdList);
        if (updateCommission < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "关联核算单失败，请稍后重试");
        }

        return ResultUtil.success("关联核算单成功");
    }

    /**
     * 移除核算单明细
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody removeChecklistDetail(Map<String, Object> map, HttpServletRequest request) {
        // 0 封装数据
        packageUserId(map, request);
        List<String> checklistDetailId = StrUtil.split(map.get("checklistDetailId").toString(), ',');
        map.put("ids", checklistDetailId);

        // 1 获取 核算单明细
        ChecklistDetail checklistDetail = checklistDetailService.getById(map.get("checklistDetailId").toString());
        if (checklistDetail == null) {
            return ResultUtil.error(500, "未找到明细，请稍后重试");
        }

        // 2 获取 核算单
        Checklist checklist = this.getById(checklistDetail.getChecklistId());
        if (checklist == null) {
            return ResultUtil.error(500, "未找到核算单，请稍后重试");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus() && 6 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "立项状态为：草稿、立项驳回、立项撤销时，才可移除");
        }

        if (checklist.getIsFather() == 1) {
            // 正核算单
            // 3 修改 待结佣状态：在核算单外
            List<String> commissionIds = StrUtil.split(checklistDetail.getCommissionId(), ',');
            if (CollUtil.isNotEmpty(commissionIds)) {
                Integer updateCommission = checklistMapper.updateCommissionByIds2(commissionIds);
                if (updateCommission < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.error(500, "移除明细失败，请稍后重试");
                }
            }
        } else {
            // 负核算单
            // 3 修改 对应正核算单明细（未关联负核算单）
            Integer update = checklistMapper.updateIsNegativeToZeroByChecklistDetail(checklistDetail);
            if (update < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(500, "移除明细失败，请稍后重试");
            }
        }

        // 4 删除 核算单明细
        Integer delete2 = checklistMapper.removeChecklistDetailByDetailId(map);
        if (delete2 < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "移除明细失败，请稍后重试");
        }

        // 5 修改 核算单 佣金立项金额
        Integer update2 = checklistMapper.updateProjectAmount(checklist.getId());
        if (update2 < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "移除明细失败，请稍后重试");
        }

        return ResultUtil.success("移除明细成功");
    }

    /**
     * 修改佣金立项金额
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody updateProjectAmount(Map<String, String> map, HttpServletRequest request) {
        // 1 获取 核算单明细
        ChecklistDetail checklistDetail = checklistDetailService.getById(map.get("id"));
        if (checklistDetail == null) {
            return ResultUtil.error(500, "未找到明细，请稍后重试");
        }
        if (checklistDetail.getChecklistDetailType() != 1) {
            return ResultUtil.error(500, "正常结佣的明细，才可修改佣金立项金额及点位");
        }

        // 2 获取核算单
        Checklist checklist = this.getById(checklistDetail.getChecklistId());
        if (checklist == null) {
            return ResultUtil.error(500, "未找到核算单，请稍后重试");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "草稿、立项驳回状态可修改佣金立项金额及点位");
        }

        // 4 修改 核算单明细 佣金立项金额
        packageProjectAmount(checklistDetail, map, request);
        boolean update = checklistDetailService.updateById(checklistDetail);
        if (!update) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "修改佣金立项金额失败，请稍后重试");
        }

        // 5 修改 核算单 佣金立项金额
        Integer update2 = checklistMapper.updateProjectAmount(checklist.getId());
        if (update2 < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "修改佣金立项金额失败，请稍后重试");
        }

        return ResultUtil.success("修改佣金立项金额成功");
    }

    /**
     * 封装 佣金金额 和 佣金点位
     *
     * @param checklistDetail checklistDetail
     * @param map             map
     * @param request         request
     */
    private void packageProjectAmount(ChecklistDetail checklistDetail, Map<String, String> map, HttpServletRequest request) {
        if (map.get("projectAmount") != null && StrUtil.isNotBlank(map.get("projectAmount"))) {
            BigDecimal projectAmount = new BigDecimal(map.get("projectAmount"));
            checklistDetail.setProjectAmount(projectAmount);
            checklistDetail.setOutstandingAmount(projectAmount);
            checklistDetail.setUnpaidAmount(projectAmount);
        }
        if (map.get("commissionPoint") != null && StrUtil.isNotBlank(map.get("commissionPoint"))) {
            checklistDetail.setCommissionPoint(new BigDecimal(map.get("commissionPoint")));
        }
        checklistDetail.setEditor(request.getHeader("userid"));
        checklistDetail.setEditTime(new Date());
    }

    /**
     * 批量修改佣金立项金额
     *
     * @param list    list
     * @param request request
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody updateBatchProjectAmount(List<Map<String, String>> list, HttpServletRequest request) {
        // 0 数据处理
        List<String> checklistDetailIds = list.stream().map(map -> map.get("id")).collect(Collectors.toList());

        // 1 获取核算单明细
        List<Map> checklistDetailList = checklistMapper.getChecklistDetailListByIds(checklistDetailIds);
        if (CollUtil.isEmpty(checklistDetailList)) {
            return ResultUtil.error(500, "未找到明细，请稍后重试");
        }
        checklistDetailList.removeIf(map -> !StrUtil.equals("1", map.get("checklist_detail_type").toString()));
        if (CollUtil.isEmpty(checklistDetailList)) {
            return ResultUtil.error(500, "未找到可修改的明细，请稍后重试");
        }

        // 2 获取核算单
        Checklist checklist = this.getById(checklistDetailList.get(0).get("checklist_id").toString());
        if (checklist == null) {
            return ResultUtil.error(500, "未找到核算单，请稍后重试");
        }
        if (checklist.getIsFather() == 0) {
            return ResultUtil.error(500, "负核算单明细，不可修改佣金立项金额及点位");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "草稿、立项驳回状态可修改佣金立项金额及点位");
        }

        // 4 修改 核算单明细 佣金立项金额
        for (Map map : checklistDetailList) {
            ChecklistDetail checklistDetail = new ChecklistDetail();
            checklistDetail.setId(map.get("id").toString());
            for (Map<String, String> map2 : list) {
                if (StrUtil.equals(map.get("id").toString(), map2.get("id"))) {
                    packageProjectAmount(checklistDetail, map2, request);
                }
            }
            boolean update = checklistDetailService.updateById(checklistDetail);
            if (!update) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.error(500, "修改金额失败，请稍后重试");
            }
        }

        // 5 修改 核算单 佣金立项金额
        Integer update2 = checklistMapper.updateProjectAmount(checklist.getId());
        if (update2 < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "修改金额失败，请稍后重试");
        }

        return ResultUtil.success("修改金额成功");
    }

    /**
     * 导出核算单
     *
     * @param ids      ids
     * @param request  request
     * @param response response
     */
    @Override
    public void exportChecklist(String ids, HttpServletRequest request, HttpServletResponse response) {
        exportChecklistByChecklistId(StrUtil.split(ids, ','), request, response);
    }

    /**
     * 根据核算单id，导出核算单
     *
     * @param checklistIds checklistIds
     * @param request      request
     * @param response     response
     */
    private void exportChecklistByChecklistId(List<String> checklistIds, HttpServletRequest request, HttpServletResponse response) {
        List<String> checklistDetailIds = new ArrayList<>();
        if(checklistIds != null && checklistIds.size() > 0){
            // 1 获取 要导出的明细id
            checklistDetailIds = checklistMapper.getChecklistDetailIdsByIds(checklistIds);
            if (CollUtil.isEmpty(checklistDetailIds)) {
                throw new BadRequestException(1001, "未找到核算单明细数据!");
            }
        }
        // 2 导出
        exportChecklistUtil(request, response, checklistDetailIds, 1);
    }

    /**
     * 导出核算单公共方法
     *
     * @param request            request
     * @param response           response
     * @param checklistDetailIds checklistDetailIds
     * @param type               type
     */
    private void exportChecklistUtil(HttpServletRequest request, HttpServletResponse response, List<String> checklistDetailIds, Integer type) {
        // 2 获取 要导出的数据
        List<Map> checklistDetails = new ArrayList<>();
        if(checklistDetailIds != null && checklistDetailIds .size()>0){
            checklistDetails = checklistMapper.getChecklistDetailListByIds(checklistDetailIds);
        }
        /*if (CollUtil.isEmpty(checklistDetails)) {
            throw new BadRequestException(1001, "未找到核算单明细数据!");
        }*/

        try {
            XSSFWorkbook workbook;
            /*if (QMJJR.equals(checklistDetails.get(0).get("source_type_desc"))) { */
            if (type == 1) {
                workbook = topSettingTwoExcelService.getWorkbook(request, "checklistQm.xlsx");
            } else {
                workbook = topSettingTwoExcelService.getWorkbook(request, "checklistFqm.xlsx");
            }
            XSSFSheet sheet = workbook.getSheetAt(0);
            // 加锁（设置保护密码）
            sheet.protectSheet("100010111000001001");

            // 字体
            XSSFFont font = workbook.createFont();
            font.setFontName("Microsoft YaHei Light");
            font.setFontHeightInPoints((short) 12);

            // 加锁样式
            CellStyle rowStyle = workbook.createCellStyle();
            rowStyle.setLocked(true);
            rowStyle.setFont(font);
            short index = IndexedColors.GREY_25_PERCENT.getIndex();
            rowStyle.setFillForegroundColor(index);
            rowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // 解锁样式
            CellStyle rowStyleUnLocke = workbook.createCellStyle();
            rowStyleUnLocke.setLocked(false);
            rowStyleUnLocke.setFont(font);

            /*if (QMJJR.equals(checklistDetails.get(0).get("source_type_desc"))) { */
            if (type == 1) {
                for (int i = 0; i < checklistDetails.size(); i++) {
                    Map map = checklistDetails.get(i);
                    Row row = exportChecklistQmAndFqmUtil(sheet, rowStyle, i, 35, map);
                    row.getCell(10).setCellValue(map.get("current_role") != null ? map.get("current_role") + "" : "");
                    row.getCell(11).setCellValue(map.get("transactionStatus") != null ? map.get("transactionStatus") + "" : "");
                    row.getCell(12).setCellValue(map.get("subscription_date") != null ? map.get("subscription_date") + "" : "");
                    row.getCell(13).setCellValue(map.get("signing_date") != null ? map.get("signing_date") + "" : "");
                    row.getCell(14).setCellValue(map.get("now_price") != null ? map.get("now_price") + "" : "");
                    row.getCell(15).setCellValue(map.get("back_price") != null ? map.get("back_price") + "" : "");
                    row.getCell(16).setCellValue(map.get("collection_proportion") != null ? map.get("collection_proportion") + "" : "");
                    row.getCell(17).setCellValue(map.get("project_amount") != null ? map.get("project_amount") + "" : "");
                    //解锁
                    row.getCell(17).setCellStyle(rowStyleUnLocke);

                    row.getCell(18).setCellValue(map.get("commission_point") != null ? map.get("commission_point") + "" : "");
                    //解锁
                    row.getCell(18).setCellStyle(rowStyleUnLocke);

                    row.getCell(19).setCellValue(map.get("amount_closed") != null ? map.get("amount_closed") + "" : "");
                    row.getCell(20).setCellValue(map.get("outstanding_amount") != null ? map.get("outstanding_amount") + "" : "");
                    row.getCell(21).setCellValue(map.get("amount_paid") != null ? map.get("amount_paid") + "" : "");
                    row.getCell(22).setCellValue(map.get("unpaid_amount") != null ? map.get("unpaid_amount") + "" : "");
                    row.getCell(23).setCellValue(map.get("com_payment_ratio") != null ? map.get("com_payment_ratio") + "" : "");
                    row.getCell(24).setCellValue(map.get("employee_name") != null ? map.get("employee_name") + "" : "");
                    row.getCell(25).setCellValue(map.get("customer_mobile") != null ? map.get("customer_mobile") + "" : "");
                    row.getCell(26).setCellValue(map.get("bank_num") != null ? map.get("bank_num") + "" : "");
                    row.getCell(27).setCellValue(map.get("bank_name") != null ? map.get("bank_name") + "" : "");
                    row.getCell(28).setCellValue(map.get("reportIdCard") != null ? map.get("reportIdCard") + "" : "");
                    row.getCell(29).setCellValue(map.get("report_mobile") != null ? map.get("report_mobile") + "" : "");
                    row.getCell(30).setCellValue(map.get("creator_name") != null ? map.get("creator_name") + "" : "");
                    row.getCell(31).setCellValue(map.get("create_time") != null ? map.get("create_time") + "" : "");
                    row.getCell(32).setCellValue(map.get("commission_type") != null ? map.get("commission_type") + "" : "");
                    row.getCell(33).setCellValue(map.get("built_up_area") != null ? map.get("built_up_area") + "" : "");
                    row.getCell(34).setCellValue(map.get("id") != null ? map.get("id") + "" : "");
                }
            } else {
                for (int i = 0; i < checklistDetails.size(); i++) {
                    Map map = checklistDetails.get(i);
                    Row row = exportChecklistQmAndFqmUtil(sheet, rowStyle, i, 30, map);
                    row.getCell(10).setCellValue(map.get("transactionStatus") != null ? map.get("transactionStatus") + "" : "");
                    row.getCell(11).setCellValue(map.get("subscription_date") != null ? map.get("subscription_date") + "" : "");
                    row.getCell(12).setCellValue(map.get("signing_date") != null ? map.get("signing_date") + "" : "");
                    row.getCell(13).setCellValue(map.get("now_price") != null ? map.get("now_price") + "" : "");
                    row.getCell(14).setCellValue(map.get("back_price") != null ? map.get("back_price") + "" : "");
                    row.getCell(15).setCellValue(map.get("collection_proportion") != null ? map.get("collection_proportion") + "" : "");
                    row.getCell(16).setCellValue(map.get("project_amount") != null ? map.get("project_amount") + "" : "");
                    //解锁
                    row.getCell(16).setCellStyle(rowStyleUnLocke);

                    row.getCell(17).setCellValue(map.get("commission_point") != null ? map.get("commission_point") + "" : "");
                    //解锁
                    row.getCell(17).setCellStyle(rowStyleUnLocke);

                    row.getCell(18).setCellValue(map.get("amount_closed") != null ? map.get("amount_closed") + "" : "");
                    row.getCell(19).setCellValue(map.get("outstanding_amount") != null ? map.get("outstanding_amount") + "" : "");
                    row.getCell(20).setCellValue(map.get("amount_paid") != null ? map.get("amount_paid") + "" : "");
                    row.getCell(21).setCellValue(map.get("unpaid_amount") != null ? map.get("unpaid_amount") + "" : "");
                    row.getCell(22).setCellValue(map.get("com_payment_ratio") != null ? map.get("com_payment_ratio") + "" : "");
                    row.getCell(23).setCellValue(map.get("employee_name") != null ? map.get("employee_name") + "" : "");
                    row.getCell(24).setCellValue(map.get("customer_mobile") != null ? map.get("customer_mobile") + "" : "");
                    row.getCell(25).setCellValue(map.get("creator_name") != null ? map.get("creator_name") + "" : "");
                    row.getCell(26).setCellValue(map.get("create_time") != null ? map.get("create_time") + "" : "");
                    row.getCell(27).setCellValue(map.get("commission_type") != null ? map.get("commission_type") + "" : "");
                    row.getCell(28).setCellValue(map.get("built_up_area") != null ? map.get("built_up_area") + "" : "");
                    row.getCell(29).setCellValue(map.get("id") != null ? map.get("id") + "" : "");
                }
            }
            // 生成 文件名称
            // String projectName = checklistDetails.get(0).get("project_name") + "-";
            // String sourceTypeDesc = checklistDetails.get(0).get("source_type_desc") + "-";
            String name;

            /* if (QMJJR.equals(checklistDetails.get(0).get("source_type_desc"))) { */
            if (type == 1) {
                name = "核算单-";
            } else {
                name = "核算单明细-";
            }

            topSettingTwoExcelService.exportExcelResponse(response,name, workbook);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(1001, "读取模版文件失败!");
        }
    }

    /**
     * 抽取 导出中介和全民的相同代码部分
     *
     * @param sheet    sheet
     * @param rowStyle rowStyle
     * @param rowNum   rowNum
     * @param cellNum  cellNum
     * @param map      map
     * @return return
     */
    private Row exportChecklistQmAndFqmUtil(XSSFSheet sheet, CellStyle rowStyle, int rowNum, int cellNum, Map map) {
        Row row = sheet.createRow(rowNum + 1);
        for (int c = 0; c < cellNum; c++) {
            row.createCell(c).setCellStyle(rowStyle);
        }

        row.getCell(0).setCellValue(map.get("business_unit_name") != null ? map.get("business_unit_name") + "" : "");
        row.getCell(1).setCellValue(map.get("project_name") != null ? map.get("project_name") + "" : "");
        row.getCell(2).setCellValue(map.get("checklist_name") != null ? map.get("checklist_name") + "" : "");
        row.getCell(3).setCellValue(map.get("checklist_code") != null ? map.get("checklist_code") + "" : "");
        row.getCell(4).setCellValue(map.get("project_code") != null ? map.get("project_code") + "" : "");
        String projectStatus = map.get("project_status").toString();
        if ("1".equals(projectStatus)) {
            projectStatus = "草稿";
        } else if ("2".equals(projectStatus)) {
            projectStatus = "已审核";
        } else if ("3".equals(projectStatus)) {
            projectStatus = "已立项";
        } else if ("4".equals(projectStatus)) {
            projectStatus = "立项通过";
        } else if ("5".equals(projectStatus)) {
            projectStatus = "立项驳回";
        } else if ("6".equals(projectStatus)) {
            projectStatus = "立项撤销";
        }
        row.getCell(5).setCellValue(projectStatus != null ? projectStatus : "");
        row.getCell(6).setCellValue(map.get("room_name") != null ? map.get("room_name") + "" : "");
        row.getCell(7).setCellValue(map.get("customer_name") != null ? map.get("customer_name") + "" : "");
        row.getCell(8).setCellValue(map.get("gain_by") != null ? map.get("gain_by") + "" : "");
        row.getCell(9).setCellValue(map.get("source_type_desc") != null ? map.get("source_type_desc") + "" : "");
        return row;
    }

    /**
     * 导出核算单明细
     *
     * @param ids      ids
     * @param request  request
     * @param response response
     */
    @Override
    public void exportChecklistDetail(String ids, HttpServletRequest request, HttpServletResponse response) {
        // 1 导出
        exportChecklistUtil(request, response, StrUtil.split(ids, ','), 2);
    }

    /**
     * 导出全部
     *
     * @param map      map
     * @param request  request
     * @param response response
     */
    @Override
    public void exportAll(Map<String, String> map, HttpServletRequest request, HttpServletResponse response) {
        // 0 拼接排序条件
        if (StrUtil.isBlank(map.get("orderBy")) || StrUtil.isBlank(map.get("orderType"))) {
            map.put("orderBy", "create_time DESC");
        } else {
            map.put("orderBy", map.get("orderBy") + " " + map.get("orderType"));
        }

        // 1 获取 核算单id
        List<String> checklistIds = checklistMapper.getChecklistIds(map);

        // 2 导出 核算单
        this.exportChecklistByChecklistId(checklistIds, request, response);
    }

    /**
     * 导出全部明细
     *
     * @param map      map
     * @param request  request
     * @param response response
     */
    @Override
    public void exportAllDetail(Map<String, String> map, HttpServletRequest request, HttpServletResponse response) {
        // 0 拼接排序条件
        if (StrUtil.isBlank(map.get("orderBy")) || StrUtil.isBlank(map.get("orderType"))) {
            map.put("orderBy", "t1.checklist_detail_type DESC,t1.create_time DESC,t1.id");
        } else {
            map.put("orderBy", map.get("orderBy") + " " + map.get("orderType"));
        }

        // 1 获取 核算单明细id
        List<String> checklistDetailIds = checklistMapper.getChecklistDetailIds(map);

        // 2 导出
        exportChecklistUtil(request, response, checklistDetailIds, 2);
    }

    /**
     * 核算单审批
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Override
    public ResultBody checklistApprove(Map<String, Object> map, HttpServletRequest request) {
        // 0 准备数据
        packageUserId(map, request);
        map.put("ids", StrUtil.split(map.get("checklistId").toString(), ','));

        // 1 获取 核算单
        Checklist checklist = this.getById(map.get("checklistId").toString());
        if (checklist == null) {
            return ResultUtil.error(500, "未找到核算单，请稍后重试");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "核算单的立项状态为：草稿、立项驳回时，才可审批");
        }

        // 2 正核算单 校验佣金金额
        if (checklist.getIsFather() == 1) {
            // 正核算单
            // 2.1 校验核算单明细 是否录入 佣金金额或佣金点位
            Long isEntryProjectAmount = checklistMapper.isEntryProjectAmountAndCommissionPoint(checklist.getId());
            if (isEntryProjectAmount > 0) {
                return ResultUtil.error(500, "未录入佣金金额或佣金点位");
            }

            // 2.3 校验核算单 是否关联 政策
            Long isRelatedPolicy = checklistMapper.isRelatedPolicy(checklist.getId());
            if (isRelatedPolicy == 0) {
                return ResultUtil.error(500, "未关联政策");
            }

            // 2.4 校验 金额
            if (checklist.getProjectAmount() == null || checklist.getProjectAmount().intValue() < 0) {
                return ResultUtil.error(500, "核算单的立项金额需大于零");
            }
            if (!StrUtil.equals(ZJCJ, checklist.getDealType())) {
                // 全民经纪人 校验 结佣形式
                if (StrUtil.isBlank(checklist.getCommissionType())) {
                    return ResultUtil.error(500, "未录入结佣形式");
                }

                List<Map> list = checklistMapper.getProjectAmountByBusinessAttributionCode(checklist.getId());
                for (Map projectAmountMap : list) {
                    if (Double.parseDouble(projectAmountMap.get("project_amount").toString()) < 0) {
                        return ResultUtil.error(500, "业务归属人编号：" + projectAmountMap.get("business_attribution_code") + "，立项金额小于零");
                    }
                }
            }

            // 2.5 获取 结佣明细 交易id
            List<String> transactionIds = checklistMapper.getTransactionIdsByChecklistIds(map);

            // 2.6 校验 退房中
            String transactionIdsStr = CollUtil.join(transactionIds.iterator(), "','");
            if (StrUtil.isBlank(transactionIdsStr)) {
                return ResultUtil.error(500, "该核算单无结佣交易");
            }
            String sql = "SELECT roominfo FROM dotnet_erp60.dbo.VS_XSGL_TFDETAIL WHERE TradeGUID IN ('" + transactionIdsStr + "')";
            List<Map<String, Object>> roomInfoList = jdbcTemplatemy.queryForList(sql);
            if (CollUtil.isNotEmpty(roomInfoList)) {
                List<Object> list = roomInfoList.stream().map(obj -> obj.get("roominfo")).collect(Collectors.toList());
                String join = CollUtil.join(list.iterator(), ",");
                return ResultUtil.error(500, join + " 已发起退房流程");
            }

            // 2 更新数据
            ResultBody resultBody = commissionService.updateMyTrade(transactionIds);
            if (resultBody.getCode() != 200) {
                return ResultUtil.error(500, "核算单审批失败，请稍后重试");
            }

            // 3 获取数据
            List<String> commissionIds = checklistMapper.getCommissionIdsByChecklistIds(map);
            List<Map<String, String>> commissionList = checklistMapper.getCommissionByIds(commissionIds);

            // 4 校验 业务归属
            if (StrUtil.equals(ZJCJ, checklist.getDealType())) {
                String businessAttributionCode = checklist.getBusinessAttributionCode();
                for (Map<String, String> commission : commissionList) {
                    if (!StrUtil.equals(businessAttributionCode, commission.get("gain_num"))) {
                        return ResultUtil.error(500, "中介成交，核算单只能有一个业务归属");
                    }
                }
            }

            // 5 校验 交易状态
            for (Map<String, String> commission : commissionList) {
                String status = commission.get("my_STATUS");
                if (status == null || StrUtil.equals("关闭", status)) {
                    return ResultUtil.error(500, "核算单中存在异常交易，请移除");
                }
            }
        }

        // 6 核算单审批
        Integer update = checklistMapper.checklistApprove(map);
        if (update < 0) {
            return ResultUtil.error(500, "核算单审批失败，请稍后重试");
        }

        return ResultUtil.success("核算单审批成功");
    }

    /**
     * 核算单审批回调接口
     *
     * @param map map
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map approvalCallbackInterface(Map<String, Object> map) {
        // 日志
        try {
            SysLog sysLog1 = new SysLog();
            sysLog1.setStartTime(DateUtil.formatDateTime(new Date()));
            sysLog1.setTaskName("核算单审批回调");
            sysLog1.setNote(JSON.toJSONString(map));
            timeLogsDao.insertLogs(sysLog1);
        } catch (Exception e) {
        }

        // 响应时间
        String requstTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");

        // 1 判断 状态
        Integer update = -1;
        List<String> ids = StrUtil.split(map.get("checklistId").toString(), ',');
        map.put("ids", ids);
        Checklist checklist = this.getById(map.get("checklistId").toString());
        if (checklist == null) {
            return ResultUtil.getErrorMap(requstTime, "调用失败，未找到核算单");
        }

        if (StrUtil.equals("3", map.get("projectStatus").toString())) {
            // 已立项
            if (2 != checklist.getProjectStatus()) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，核算单的立项状态为：已审核时，才可立项");
            }

            // 1 修改 核算单明细（已立项）
            Integer update2 = checklistMapper.updateChecklistDetailProject(map);
            if (update2 < 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.getErrorMap(requstTime, "调用失败");
            }

            // 2 修改 核算单状态（已立项）
            update = checklistMapper.updateChecklistProject(map);
        } else if (StrUtil.equals("6", map.get("projectStatus").toString())) {
            // 立项撤销
            if (4 != checklist.getProjectStatus()) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，核算单的立项状态为：立项通过时，才可立项撤销");
            }

            // 0 负核算单直接失败
            if (checklist.getIsFather() == 0) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultUtil.getErrorMap(requstTime, "调用失败，负核算单不能立项撤销");
            }

            // 1 获取 欠款明细的ids
            List<String> arrearsChecklistDetailIds = checklistMapper.getArrearsChecklistDetailIds(map.get("checklistId").toString());
            if (!CollUtil.isEmpty(arrearsChecklistDetailIds)) {
                // 2 获取 关联的待结佣ids
                map.put("ids", arrearsChecklistDetailIds);
                List<String> commissionIds = checklistMapper.getCommissionIdsByChecklistDetailIds(map);
                if (!CollUtil.isEmpty(commissionIds)) {
                    // 4 修改 待结佣状态（在核算单外）
                    Integer integer2 = checklistMapper.updateCommissionByIds2(commissionIds);
                    if (integer2 < 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return ResultUtil.getErrorMap(requstTime, "调用失败");
                    }
                }

                // 3 删除 欠款明细
                Integer integer = checklistMapper.removeChecklistDetailByDetailId(map);
                if (integer < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.getErrorMap(requstTime, "调用失败");
                }
            }

            checklistMapper.updateChecklistIsAbnormalForce(map.get("checklistId").toString());
            // 5 修改 核算单状态（非已立项）
            // 5.1 获取 负核算单id
            List<String> negativeChecklistIds = checklistMapper.getNegativeChecklistIdsByIds(ids);
            ids.addAll(negativeChecklistIds);
            map.put("ids", ids);
            update = checklistMapper.updateChecklistProject2(map);
        } else if (StrUtil.equals("4", map.get("projectStatus").toString())) {
            // 立项通过
            if (3 != checklist.getProjectStatus()) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，核算单的立项状态为：已立项时，才可立项通过");
            }

            if (checklist.getIsFather() == 0) {
                // 负核算单
                // 1 修改 正核算单明细（负核算单立项通过）
                Integer updateCommission = checklistMapper.updateIsNegativeToTwo(checklist);
                if (updateCommission < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.getErrorMap(requstTime, "调用失败");
                }

                // 2 修改 核算单的异常状态
                Integer updateChecklistIsAbnormal = checklistMapper.updateChecklistIsAbnormal(checklist.getFid());
                if (updateChecklistIsAbnormal < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.getErrorMap(requstTime, "调用失败");
                }

                // 3 修改 核算单的佣金立项金额
                Integer updateProjectAmount = checklistMapper.updateProjectAmount(checklist.getFid());
                if (updateProjectAmount < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.getErrorMap(requstTime, "调用失败");
                }
            }

            // 3 修改 核算单状态（非已立项）
            update = checklistMapper.updateChecklistProject2(map);
        } else if (StrUtil.equals("5", map.get("projectStatus").toString())) {
            // 立项驳回
            if (3 != checklist.getProjectStatus()) {
                return ResultUtil.getErrorMap(requstTime, "调用失败，核算单的立项状态为：已立项时，才可立项驳回");
            }
            update = checklistMapper.updateChecklistProject2(map);
        }else if (StrUtil.equals("7", map.get("projectStatus").toString()))  {
            map.put("projectStatus","2");
            update = checklistMapper.updateChecklistProject3(map);
        }

        if (update < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.getErrorMap(requstTime, "调用失败");
        }

        return ResultUtil.getSuccessMap(requstTime);
    }

    /**
     * 关联政策
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Override
    public ResultBody relatedPolicy(Map<String, String> map, HttpServletRequest request) {
        // 1 获取 核算单
        Checklist checklist = this.getById(map.get("checklistId"));
        if (checklist == null) {
            return ResultUtil.error(500, "未找到关联的核算单，请稍后重试");
        }
        if (checklist.getIsFather() == 0) {
            return ResultUtil.error(500, "负核算单不能关联政策");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "核算单的立项状态为：草稿、立项驳回时，才可关联");
        }

        // 2 新增 核算单-政策
        ArrayList<ChecklistPolicy> checklistPolicyList = getChecklistPolicies(map.get("policyIds"), checklist.getId(),
                request.getHeader("userid"), new Date(), request.getHeader("jobid"), request.getHeader("joborgid"),
                request.getHeader("orgid"));
        boolean saveBatch = checklistPolicyService.saveBatch(checklistPolicyList);
        if (!saveBatch) {
            return ResultUtil.error(500, "关联政策，请稍后重试");
        }

        return ResultUtil.success("");
    }

    /**
     * 移除政策
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Override
    public ResultBody removePolicy(Map<String, Object> map, HttpServletRequest request) {
        // 0 准备数据
        packageUserId(map, request);
        map.put("ids", StrUtil.split(map.get("policyIds").toString(), ','));

        // 1 获取 核算单
        Checklist checklist = this.getById(map.get("checklistId").toString());
        if (checklist == null) {
            return ResultUtil.error(500, "未找到关联的核算单，请稍后重试");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "核算单的立项状态为：草稿、立项驳回时，才可移除");
        }

        // 2 移除 核算单-政策
        Integer delete = checklistMapper.removeChecklistPolicyByPolicyId(map);
        if (delete < 0) {
            return ResultUtil.error(500, "移除核算单明细失败，请稍后重试");
        }

        return ResultUtil.success("");
    }

    /**
     * 查询负核算单信息
     *
     * @param checklistId checklistId
     * @return return
     */
    @Override
    public ResultBody getNegativeChecklist(String checklistId) {
        List<Map> list = checklistMapper.getNegativeChecklist(checklistId);
        return ResultUtil.success(list);
    }

    /**
     * 创建负核算单
     *
     * @param map     map
     * @param request request
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody createNegativeChecklist(Map<String, Object> map, HttpServletRequest request) {
        // 0 判断名称是否重复
        Object checklistId = map.get("checklistId");
        map.put("checklistId", "");
        Map duplicateNameCheck = checklistMapper.duplicateNameCheck(map);
        if (duplicateNameCheck != null) {
            return ResultUtil.error(500, "名称已存在");
        }
        map.put("checklistId", checklistId);

        // 0 准备数据
        String userName = getUserName(request);
        String userId = request.getHeader("userid");
        String jobId = request.getHeader("jobid");
        String jobOrgId = request.getHeader("joborgid");
        String orgId = request.getHeader("orgid");
        Date date = new Date();
        String id = UUID.randomUUID().toString().replace("-", "");

        // 1 获取 核算单
        Checklist fChecklist = this.getById(map.get("checklistId").toString());
        if (fChecklist == null) {
            return ResultUtil.error(500, "未找到核算单，请稍后重试");
        }

        // 2 获取 未关联负核算单的明细
        List<Map> checklistDetails = checklistMapper.getNotNegativeChecklistDetailListByIds(StrUtil.split(map.get("checklistDetailIds").toString(), ','));
        if (CollUtil.isEmpty(checklistDetails)) {
            return ResultUtil.error(500, "未找到核算单明细，请稍后重试");
        }

        // 3 新增 核算单明细(数据准备工作)
        BigDecimal projectAmount = BigDecimal.ZERO;
        List<String> checklistDetailIds = new ArrayList<>();
        List<ChecklistDetail> checklistDetailList = new ArrayList<>();
        projectAmount = getNegativeChecklistDetailList(userId, date, id, fChecklist, checklistDetails, projectAmount, checklistDetailIds, checklistDetailList, jobId, jobOrgId, orgId);
        if (CollUtil.isEmpty(checklistDetailList)) {
            return ResultUtil.error(500, "未找到核算单明细，请稍后重试");
        }
        // 保存 核算单明细
        boolean saveBatchChecklistDetail = checklistDetailService.saveBatch(checklistDetailList);
        if (!saveBatchChecklistDetail) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "创建负核算单失败，请稍后重试");
        }

        // 4 新增 负核算单(数据准备工作)
        Checklist negativeChecklist = getNegativeChecklist(userName, userId, date, id, fChecklist, projectAmount, jobId, jobOrgId, orgId, map.get("checklistName").toString());
        // 保存 负核算单
        boolean saveChecklist = this.save(negativeChecklist);
        if (!saveChecklist) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "创建负核算单失败，请稍后重试");
        }

        // 5 修改 核算单明细（已关联负核算单）
        Integer updateIsNegativeByIds = checklistMapper.updateIsNegativeByIds(checklistDetailIds);
        if (updateIsNegativeByIds < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResultUtil.error(500, "创建负核算单失败，请稍后重试");
        }

        return ResultUtil.success(id);
    }

    /**
     * 封装 负核算单
     *
     * @param userName      userName
     * @param userId        userId
     * @param date          date
     * @param id            id
     * @param fChecklist    fChecklist
     * @param projectAmount projectAmount
     * @param jobId         jobId
     * @param jobOrgId      jobOrgId
     * @param orgId         orgId
     * @param checklistName checklistName
     * @return return
     */
    private Checklist getNegativeChecklist(String userName, String userId, Date date, String id, Checklist fChecklist, BigDecimal projectAmount,
                                           String jobId, String jobOrgId, String orgId, String checklistName) {
        Checklist negativeChecklist = new Checklist();
        negativeChecklist.setId(id);
        negativeChecklist.setFid(fChecklist.getId());
        negativeChecklist.setFcode(fChecklist.getChecklistCode());
        negativeChecklist.setMainDataProjectId(fChecklist.getMainDataProjectId());
        negativeChecklist.setDealType(fChecklist.getDealType());
        negativeChecklist.setChannelName(fChecklist.getChannelName());
        negativeChecklist.setProjectStatus(1);
        negativeChecklist.setIsFather(0);
        negativeChecklist.setDivision(fChecklist.getDivision());
        negativeChecklist.setCityCompany(fChecklist.getCityCompany());
        negativeChecklist.setProjectName(fChecklist.getProjectName());
        negativeChecklist.setIsAbnormal(0);
        negativeChecklist.setCreator(userId);
        negativeChecklist.setCreatorName(userName);
        negativeChecklist.setCreateTime(date);
        negativeChecklist.setJobId(jobId);
        negativeChecklist.setJobOrgId(jobOrgId);
        negativeChecklist.setOrgId(orgId);
        negativeChecklist.setChecklistCode(getChecklistCode(date));
        negativeChecklist.setProjectAmount(projectAmount);
        negativeChecklist.setChecklistName(checklistName);
        return negativeChecklist;
    }

    /**
     * 封装 负核算单明细
     *
     * @param userId             userId
     * @param date               date
     * @param id                 id
     * @param fChecklist         fChecklist
     * @param checklistDetails   checklistDetails
     * @param projectAmount      projectAmount
     * @param checklistDetailIds checklistDetailIds
     * @param jobId              jobId
     * @param jobOrgId           jobOrgId
     * @param orgId              orgId
     * @return return
     */
    private BigDecimal getNegativeChecklistDetailList(String userId, Date date, String id, Checklist fChecklist, List<Map> checklistDetails,
                                                      BigDecimal projectAmount, List<String> checklistDetailIds,
                                                      List<ChecklistDetail> checklistDetailList, String jobId, String jobOrgId, String orgId) {
        for (Map checklistDetail : checklistDetails) {
            if (StrUtil.equals("0", checklistDetail.get("is_negative").toString())) {
                ChecklistDetail checklistDetailNew = new ChecklistDetail();
                checklistDetailNew.setId(UUID.randomUUID().toString().replace("-", ""));
                checklistDetailNew.setFid(fChecklist.getId());
                checklistDetailNew.setChecklistId(id);
                checklistDetailNew.setCommissionId(checklistDetail.get("commission_id").toString());
                checklistDetailNew.setRoomId(checklistDetail.get("room_id").toString());
                checklistDetailNew.setBusinessAttributionCode(checklistDetail.get("business_attribution_code") + "");
                checklistDetailNew.setTransactionId(checklistDetail.get("transaction_id").toString());
                checklistDetailNew.setOpportunityId(checklistDetail.get("opportunity_id").toString());
                checklistDetailNew.setIsDeadlock(1);
                checklistDetailNew.setIsHide(0);
                checklistDetailNew.setChecklistDetailType(2);
                checklistDetailNew.setCreator(userId);
                checklistDetailNew.setCreateTime(date);
                checklistDetailNew.setJobId(jobId);
                checklistDetailNew.setJobOrgId(jobOrgId);
                checklistDetailNew.setOrgId(orgId);
                // 立项金额
                BigDecimal outstandingAmount = new BigDecimal(checklistDetail.get("outstanding_amount").toString()).multiply(new BigDecimal(-1));
                checklistDetailNew.setProjectAmount(outstandingAmount);
                projectAmount = projectAmount.add(outstandingAmount);

                checklistDetailIds.add(checklistDetail.get("id").toString());
                checklistDetailList.add(checklistDetailNew);
            }
        }
        return projectAmount;
    }

    /**
     * 导入核算单明细
     *
     * @param file            file
     * @param dealType        dealType
     * @param calculationType calculationType
     * @return return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultBody importChecklistDetail(MultipartFile file, String dealType, String calculationType) {
        try {
            File f = File.createTempFile("tmp", null);
            file.transferTo(f);
            f.deleteOnExit();
            FileInputStream fileInputStream = new FileInputStream(f);
            XSSFWorkbook workBook = new XSSFWorkbook(fileInputStream);
            Sheet planSheet = workBook.getSheetAt(0);
            // 导入数据总行数
            int planSheetTotalRows = planSheet.getLastRowNum();
            int startRow = 1;
            Row row;

            // 明细id 下标
            int idIndex;
            // 佣金金额 下标
            int projectAmountIndex;
            // 佣金点位 下标
            int commissionPointIndex;
            // 成交金额 下标
            int nowPriceIndex;
            // 默认值
            Object defaultValue = 0;
            // 遍历
            List<Object> checklistDetailIds = new ArrayList<>();
            for (int i = startRow; i <= planSheetTotalRows; i++) {
                row = planSheet.getRow(i);

                // 1 设置 下标
                if (StrUtil.equals(QMJJR, dealType)) {
                    idIndex = 34;
                    projectAmountIndex = 17;
                    commissionPointIndex = 18;
                    nowPriceIndex = 14;
                } else {
                    idIndex = 29;
                    projectAmountIndex = 16;
                    commissionPointIndex = 17;
                    nowPriceIndex = 13;
                }

                // 2 设置 参数
                Map<String, Object> map = new HashMap<>(8);
                Object checklistDetailId = getCellValueByCell(row.getCell(idIndex));
                if (checklistDetailId != null && StrUtil.isNotBlank(checklistDetailId.toString())) {
                    if (StrUtil.isNotBlank(calculationType) && StrUtil.equals(calculationType, "2")) {
                        // 佣金算点位
                        // 成交金额
                        BigDecimal nowPrice = new BigDecimal(getCellValueByCell(row.getCell(nowPriceIndex), defaultValue).toString());
                        // 佣金金额
                        BigDecimal projectAmount = new BigDecimal(getCellValueByCell(row.getCell(projectAmountIndex), defaultValue).toString());
                        map.put("project_amount", projectAmount);
                        // 佣金点位
                        map.put("commission_point", projectAmount.multiply(new BigDecimal("100")).divide(nowPrice, 2, RoundingMode.HALF_UP));
                    } else if (StrUtil.equals(calculationType, "3")) {
                        // 点位算佣金
                        // 成交金额
                        BigDecimal nowPrice = new BigDecimal(getCellValueByCell(row.getCell(nowPriceIndex), defaultValue).toString());
                        // 佣金点位
                        BigDecimal commissionPoint = new BigDecimal(getCellValueByCell(row.getCell(commissionPointIndex), defaultValue).toString());
                        map.put("commission_point", commissionPoint);
                        // 佣金金额
                        map.put("project_amount", nowPrice.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP).multiply(commissionPoint, MathContext.UNLIMITED));
                    } else {
                        map.put("project_amount", getCellValueByCell(row.getCell(projectAmountIndex), defaultValue));
                        map.put("commission_point", getCellValueByCell(row.getCell(commissionPointIndex), defaultValue));
                    }
                    map.put("id", checklistDetailId);
                    checklistDetailIds.add(checklistDetailId);
                }

                // 3 修改 明细 佣金金额
                if (CollUtil.isNotEmpty(map)) {
                    Integer importChecklistDetail = checklistMapper.importChecklistDetail(map);
                    if (importChecklistDetail < 0) {
                        return ResultUtil.error(500, "导入明细失败，请稍后重试");
                    }
                }
            }

            // 5 批量修改 核算单 佣金立项金额
            if (CollUtil.isNotEmpty(checklistDetailIds)) {
                Integer update2 = checklistMapper.updateProjectAmountByChecklistDetailIds(checklistDetailIds);
                if (update2 < 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResultUtil.error(500, "导入明细失败，请稍后重试");
                }
            }
        } catch (IllegalStateException e) {
            throw new BadRequestException(-15_1001, "Excel表格内容输入格式不正确，请修改后重新导入");
        } catch (Exception e) {
            throw new BadRequestException(-15_1002, "导入失败，请联系管理员");
        }
        return ResultUtil.success("导入成功");
    }

    /**
     * 获取单元格各类型值，返回字符串类型
     *
     * @param cell cell
     * @return return
     */
    private static Object getCellValueByCell(Cell cell) {
        return getCellValueByCell(cell, "");
    }

    /**
     * 获取单元格各类型值，返回字符串类型
     *
     * @param cell         cell
     * @param defaultValue defaultValue
     * @return return
     */
    private static Object getCellValueByCell(Cell cell, Object defaultValue) {
        // 判断是否为null或空串
        if (cell == null || StrUtil.isBlank(cell.toString())) {
            return defaultValue;
        }

        Object cellValue;
        int cellType = cell.getCellType();

        switch (cellType) {
            case Cell.CELL_TYPE_STRING:
                // 字符串类型
                cellValue = cell.getStringCellValue().trim();
                boolean boo = cellValue == null || StrUtil.isBlank(cellValue.toString());
                cellValue = boo ? defaultValue : cellValue;
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                // 布尔类型
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                // 数值类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // 判断日期类型
                    cellValue = DateUtil.format(cell.getDateCellValue(), "yyyy-MM-dd");
                } else {  //否
                    cellValue = new DecimalFormat("#.##").format(cell.getNumericCellValue());
                }
                break;
            default: //其它类型，取空串吧
                cellValue = defaultValue;
                break;
        }
        return cellValue;
    }

    /**
     * 获取渠道名称
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody getChannelName(Map<String, Object> map) {
        List<Map> list = checklistMapper.getChannelName(map);
        return ResultUtil.success(list);
    }

    /**
     * 核算单撤回
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody checklistWithdraw(Map<String, Object> map) {
        Integer num = checklistMapper.checklistWithdraw(map);
        if (num < 1) {
            return ResultUtil.error(500, "核算单撤回失败，请稍后重试");
        }
        return ResultUtil.success("");
    }

    /**
     * 核算单欠款校验
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody checklistArrearsCheck(Map<String, Object> map) {
        List<String> list = checklistMapper.checklistArrearsCheck(map);
        return ResultUtil.success(list);
    }

    /**
     * 修改核算单名称
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody updateChecklistName(Map<String, String> map) {
        // 1 获取 核算单
        Checklist checklist = this.getById(map.get("checklistId"));
        if (checklist == null) {
            return ResultUtil.error(500, "未找到核算单，请稍后重试");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "草稿、立项驳回，才可修改名称");
        }

        // 1.2 判断名称是否重复
        Map duplicateNameCheck = checklistMapper.duplicateNameCheck(map);
        if (duplicateNameCheck != null) {
            return ResultUtil.error(500, "名称已存在");
        }

        // 2 修改名称
        Integer update = checklistMapper.updateChecklistName(map);
        if (update < 0) {
            return ResultUtil.error(500, "名称修改失败，请稍后重试");
        }
        return ResultUtil.success("名称修改成功");
    }

    /**
     * 修改结佣形式
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody updateCommissionType(Map<String, String> map) {
        // 1 获取 核算单
        Checklist checklist = this.getById(map.get("checklistId"));
        if (checklist == null) {
            return ResultUtil.error(500, "未找到核算单，请稍后重试");
        }
        if (checklist.getIsFather() == 0) {
            return ResultUtil.error(500, "负核算单不能编辑结佣形式");
        }
        if (1 != checklist.getProjectStatus() && 5 != checklist.getProjectStatus()) {
            return ResultUtil.error(500, "草稿、立项驳回，才可编辑结佣形式");
        }

        // 2 修改名称
        Integer update = checklistMapper.updateCommissionType(map);
        if (update < 0) {
            return ResultUtil.error(500, "结佣形式编辑失败，请稍后重试");
        }
        return ResultUtil.success("结佣形式编辑成功");
    }

    /**
     * 测试
     *
     * @param map map
     * @return return
     */
    @Override
    public ResultBody test(Map<String, Object> map) {
        BigDecimal bigDecimal = new BigDecimal("");
        System.out.println(bigDecimal);
        return ResultUtil.success(bigDecimal);
    }
}
