package cn.visolink.salesmanage.pricing.mapper;

import cn.visolink.salesmanage.pricing.entity.PricingAttached;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 定调价主表附属表 Mapper 接口
 * </p>
 *
 * @author yangjie
 * @since 2020-08-07
 */
@Mapper
@Repository
public interface PricingAttachedDao extends BaseMapper<PricingAttached> {

}
