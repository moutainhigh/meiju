package cn.visolink.salesmanage.idmAll.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IdmMapper {
    /**
     *
     *
     * @return
     */
    public List<Map> selectIdm(Map<String, Object> map);

    /**
     *添加岗位
     * @return
     */
    public void addIdmCommonjobs();

    public void addIUserProject();

    /**
     *删除岗位
     * @return
     */
    public void insertIdmCommonjobs(List<String> list);


}
