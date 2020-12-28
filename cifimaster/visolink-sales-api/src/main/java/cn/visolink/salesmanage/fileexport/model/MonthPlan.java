package cn.visolink.salesmanage.fileexport.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "月计划对象")
public class MonthPlan implements Serializable {

    private  String father_id;
    private  String projectCode;
    private  String business_name;
    private  String business_id;
    private  Integer type;
    private  String guid;
    private  String basisguid;
    private  Integer reserve_can_sell_set;
    private  Double reserve_can_sell_funds;
    private  Integer   new_reserve_set;
    private  Double   new_reserve_funds;
    private  Integer   total_reserve_set;
    private  Double   total_reserve_funds;
    private  Double   year_plan_sign;
    private  Double   year_grand_total_sign;
    private  Integer   top_three_month_average_sign_set;
    private  Integer   upper_moon_sign_set;
    private  Double   upper_moon_sign_funds;
    private  Double   top_three_month_average_sign_funds;
    private  Double   last_month_turnover_rate;
    private  Double   reserve_sign_funds;
    private  Double   new_sign_funds;
    private  Double   total_sign_funds;
    private  Double   marketing_promotion_cost;
    private  String  months;
    private  Integer  prepared_by_unit_type;
    private  Integer  plan_subscription_set;
    private  Double  plan_subscription_funds;
    private  Double  plan_turnover_rate;
    private  Double  top_three_month_average_turnover_rate;
    private  Integer  row;
    private List preparedByLevels;
    private  String flag;
    private Integer Reserve_sign_set;
    private Integer new_sign_set;
    private Double year_check_funds;
    private Double months_check_funds;
    private Double year_check_funds_per;
    private Double months_check_funds_per;
}
