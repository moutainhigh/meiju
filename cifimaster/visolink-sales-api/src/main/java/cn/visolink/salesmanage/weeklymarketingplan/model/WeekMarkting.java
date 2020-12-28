package cn.visolink.salesmanage.weeklymarketingplan.model;

import lombok.Data;

@Data
public class WeekMarkting {

    private String start_time;
    private String end_time;
    private Integer how_week;
    private String guid;
    private String father_id;
    private String project_id;
    private String business_name;
    private String project_name;
    private String project_code;
    private Integer type;
    private String trader_name;
    private String islast;
    private String issales_trade;
    private Double subscribe_price;
    private Integer target_month_bearer;
    private Double target_month_sign;
    private Integer target_week_bearer;
    private Double target_week_sign;


    private Double target_week_bearer_per;
    private Double target_week_sign_per;
    private Integer fact_month_bearer_total;
    private Double fact_month_bearer_per;
    private Integer fact_week_bearer;
    private Double fact_week_bearer_per;
    private Double fact_month_sign;
    private Double fact_month_sign_per;
    private Double fact_week_sign;
    private Double fact_week_sign_per;
    private Double fact_signed;
    private Double plan_reserve;
    private Double plan_signed;
    private Double plan_month_lock_price;
    private Double plan_month_lock_per;
    private Double plan_month_newsign;
    private Double plan_month_sign;
    private Double plan_week_bearer_gap;
    private Double plan_week_sign_gap;
    private Double plan_month_sign_gap;
    private String gap_cause;

    private Integer checkeds;
    private String cause_details;
    private String minor_details;
    private String detailed_description;
    private String policy_for_target;
    private Integer is_self_write;
    private String plan_status;
    private String remarks;
    private String region_sort;
    private String targetwmp_month_sign;

    private String targetwmp_week_sign;
    private Integer row;
    private String Boss;
    private String areaReport;
    private String this_time;
    private Integer weeklyType;
    private String report_time;

}
