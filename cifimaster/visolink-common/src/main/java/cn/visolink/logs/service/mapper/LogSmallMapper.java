package cn.visolink.logs.service.mapper;

import cn.visolink.logs.domain.Log;
import cn.visolink.logs.service.dto.LogSmallDTO;
import cn.visolink.mapper.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author WCL
 * @date 2019-5-22
 */
@Mapper(componentModel = "spring",uses = {},unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LogSmallMapper extends EntityMapper<LogSmallDTO, Log> {

}