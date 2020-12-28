package cn.visolink.salesmanage.flieUtils.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository
public interface FileDao {
    public Map getPath(Map params);

    public int insertFile(Map params);

    public int delFile(String id);

    public List getFileLists(String id);

    public int updateFileById(Map params);

    public int delFileByBizId(String id);

    public int updateFileBizID(Map map);
}
