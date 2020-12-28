package cn.visolink.salesmanage.homenotice.dao;


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
public interface HomeNoticeDao extends BaseMapper<HomeApply> {



    /**
     * 提示首页公告
     *
     * @param map map
     * @return list
     * */
    Map<String,Object> getHomeNotice(Map<String, Object> map);



    /**
     * 提示首页公告
     *
     * @param map map
     * @return list
     * */
    int intoHomeNoticeRead(Map<String, Object> map);


    /**
     * 查询公告
     *
     * @param map map
     * @return list
     * */
    List<Map<String,Object>> selectHomeNotice(Map<String, Object> map);


    /**
     * 查询审批审请单条
     *
     * @param id id
     * @return list
     * */
    Map<String,Object> selectHomeNoticeById(@Param("id") String id);

    /**
     * 逻辑删除文件
     *
     * @param id id
     * @return int
     * */
    int isDelFile(@Param("id") String id);


    /**
     * 查询审批审请
     *
     * @param fileList map
     * @param uuid id
     * @return list
     * */
    int updateFileList(@Param("fileList") List<String> fileList, @Param("id") String uuid);


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
    int initHomeNotice(Map<String, Object> map);


    /**
     * 添加审批审请
     *
     * @param map map
     * @return row
     * */
    int updateHomeNotice(Map<String, Object> map);

    /**
     * 添加审批审请
     *
     * @param map map
     * @return row
     * */
    int deleteHomeNotice(Map<String, Object> map);

}
