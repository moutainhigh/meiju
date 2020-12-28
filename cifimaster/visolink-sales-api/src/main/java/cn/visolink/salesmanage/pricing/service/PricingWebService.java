package cn.visolink.salesmanage.pricing.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author sjl
 * @Created date 2019/11/11 11:31 上午
 */
@WebService(name="PricingWebService",targetNamespace="http://service.PricingWebService.com")
public interface PricingWebService {
    @WebMethod
    public String test(@WebParam(name="param") String param);
}
