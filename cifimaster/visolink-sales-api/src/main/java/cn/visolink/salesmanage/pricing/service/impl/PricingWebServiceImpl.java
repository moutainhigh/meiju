package cn.visolink.salesmanage.pricing.service.impl;

import cn.visolink.salesmanage.pricing.service.PricingWebService;

import javax.jws.WebService;

/**
 * @author sjl
 */
@WebService(name="PricingWebService",targetNamespace="http://service.PricingWebService.com",endpointInterface="cn.visolink.salesmanage.pricing.service.PricingWebService")
public class PricingWebServiceImpl implements PricingWebService {
    @Override
    public String test(String param) {
        return "WebService Interface";
    }
}
