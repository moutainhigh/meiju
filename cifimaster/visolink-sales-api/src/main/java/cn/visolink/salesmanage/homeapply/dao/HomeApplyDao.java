package cn.visolink.salesmanage.homeapply.dao;


import cn.visolink.salesmanage.homeapply.entity.HomeApply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 夏威审批流Mapper
 * </p>
 *
 * @author bql
 * @since 2020-09-16
 */
@Mapper
@Repository
public interface HomeApplyDao extends BaseMapper<HomeApply> {

    /**
     * 查询审批审请
     *
     * @param map map
     * @return list
     * */
    List<Map<String,Object>> selectHomeApply(Map<String,Object> map);


    /**
     * 查询审批审请单条
     *
     * @param id id
     * @return list
     * */
    Map<String,Object> selectHomeApplyById(@Param("id")String id);


    /**
     * 查询单个审请条数
     *
     * @param id id
     * @return list
     * */
    int selectFlowInfoById(@Param("id")String id);

    /**
     * 查询审批审请
     *
     * @param fileList map
     * @param uuid id
     * @return list
     * */
    int updateFileList(@Param("fileList")List<String> fileList,@Param("id")String uuid);


    /**
     * 查询当前登录人所在部门
     *
     * @param username username
     * @return list
     * */
    Map<String,String> getBelongDepartment(@Param("username")String username);



    /**
     * 获取文件数据
     *
     * @param id id
     * @return list
     */
    List<Map> getFileLists(@Param("id") String id);

    /**
     * 添加审批审请
     *
     * @param map map
     * @return row
     * */
    int initHomeApply(Map<String,Object> map);


    /**
     * 添加审批审请
     *
     * @param map map
     * @return row
     * */
    int updateHomeApply(Map<String,Object> map);

    /**
     * 添加审批审请
     *
     * @param map map
     * @return row
     * */
    int deleteHomeApply(Map<String,Object> map);

    /**
     * 添加审批流
     *
     * @param map map
     * @return row
     * */
    int saveBrokerPolicyFlow(Map<String,Object> map);

    /**
     * 审批流修改
     *
     * @param map map
     * @return row
     * */
    int updateBrokerPolicyFlow(Map<String,Object> map);

}
