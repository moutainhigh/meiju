package cn.visolink.salesmanage.pricing.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 定调价主表附属表
 * </p>
 *
 * @author yangjie
 * @since 2020-08-07
 */
@Data
@TableName("mm_ap_set_pricing_attached")
public class PricingAttached implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;
    /**
     * 投资版-货值
     */
    @TableField("tzb_hz")
    private String tzbHz;
    /**
     * 年初版-货值
     */
    @TableField("ncb_hz")
    private String ncbHz;
    /**
     * 定调价前-货值
     */
    @TableField("dtjq_hz")
    private String dtjqHz;
    /**
     * 定调价后-货值
     */
    @TableField("dtjh_hz")
    private String dtjhHz;
    /**
     * 定调价后减投资版-货值
     */
    @TableField("dtjh_tzb_hz")
    private String dtjhTzbHz;
    /**
     * 定调价后减年初版-货值
     */
    @TableField("dtjh_ncb_hz")
    private String dtjhNcbHz;
    /**
     * 定调价后减定调价前-货值
     */
    @TableField("dtjh_dtjq_hz")
    private String dtjhDtjqHz;
    /**
     * 投资版-权益前-利润额
     */
    @TableField("tzb_qyq_lre")
    private String tzbQyqLre;
    /**
     * 年初版-权益前-利润额
     */
    @TableField("ncb_qyq_lre")
    private String ncbQyqLre;
    /**
     * 定调价前-权益前-利润额
     */
    @TableField("dtjq_qyq_lre")
    private String dtjqQyqLre;
    /**
     * 定调价后-权益前-利润额
     */
    @TableField("dtjh_qyq_lre")
    private String dtjhQyqLre;
    /**
     * 定调价后减投资版-权益前-利润额
     */
    @TableField("dtjh_tzb_qyq_lre")
    private String dtjhTzbQyqLre;
    /**
     * 定调价后减年初版-权益前-利润额
     */
    @TableField("dtjh_ncb_qyq_lre")
    private String dtjhNcbQyqLre;
    /**
     * 定调价后减定调价前-权益前-利润额
     */
    @TableField("dtjh_dtjq_qyq_lre")
    private String dtjhDtjqQyqLre;
    /**
     * 投资版-权益后-利润额
     */
    @TableField("tzb_qyh_lre")
    private String tzbQyhLre;
    /**
     * 年初版-权益后-利润额
     */
    @TableField("ncb_qyh_lre")
    private String ncbQyhLre;
    /**
     * 定调价前-权益后-利润额
     */
    @TableField("dtjq_qyh_lre")
    private String dtjqQyhLre;
    /**
     * 定调价后-权益后-利润额
     */
    @TableField("dtjh_qyh_lre")
    private String dtjhQyhLre;
    /**
     * 定调价后减投资版-权益后-利润额
     */
    @TableField("dtjh_tzb_qyh_lre")
    private String dtjhTzbQyhLre;
    /**
     * 定调价后减年初版-权益后-利润额
     */
    @TableField("dtjh_ncb_qyh_lre")
    private String dtjhNcbQyhLre;
    /**
     * 定调价后减定调价前-权益后-利润额
     */
    @TableField("dtjh_dtjq_qyh_lre")
    private String dtjhDtjqQyhLre;
    /**
     * 投资版-利润率
     */
    @TableField("tzb_lrl")
    private String tzbLrl;
    /**
     * 年初版-利润率
     */
    @TableField("ncb_lrl")
    private String ncbLrl;
    /**
     * 定调价前-利润率
     */
    @TableField("dtjq_lrl")
    private String dtjqLrl;
    /**
     * 定调价后-利润率
     */
    @TableField("dtjh_lrl")
    private String dtjhLrl;
    /**
     * 定调价后减投资版-利润率
     */
    @TableField("dtjh_tzb_lrl")
    private String dtjhTzbLrl;
    /**
     * 定调价后减年初版-利润率
     */
    @TableField("dtjh_ncb_lrl")
    private String dtjhNcbLrl;
    /**
     * 定调价后减定调价前-利润率
     */
    @TableField("dtjh_dtjq_lrl")
    private String dtjhDtjqLrl;
    /**
     * 投资版-IRR
     */
    @TableField("tzb_irr")
    private String tzbIrr;
    /**
     * 年初版-IRR
     */
    @TableField("ncb_irr")
    private String ncbIrr;
    /**
     * 定调价前-IRR
     */
    @TableField("dtjq_irr")
    private String dtjqIrr;
    /**
     * 定调价后-IRR
     */
    @TableField("dtjh_irr")
    private String dtjhIrr;
    /**
     * 定调价后减投资版-IRR
     */
    @TableField("dtjh_tzb_irr")
    private String dtjhTzbIrr;
    /**
     * 定调价后减年初版-IRR
     */
    @TableField("dtjh_ncb_irr")
    private String dtjhNcbIrr;
    /**
     * 定调价后减定调价前-IRR
     */
    @TableField("dtjh_dtjq_irr")
    private String dtjhDtjqIrr;
    /**
     * 投资版-回收期
     */
    @TableField("tzb_hsq")
    private String tzbHsq;
    /**
     * 年初版-回收期
     */
    @TableField("ncb_hsq")
    private String ncbHsq;
    /**
     * 定调价前-回收期
     */
    @TableField("dtjq_hsq")
    private String dtjqHsq;
    /**
     * 定调价后-回收期
     */
    @TableField("dtjh_hsq")
    private String dtjhHsq;
    /**
     * 定调价后减投资版-回收期
     */
    @TableField("dtjh_tzb_hsq")
    private String dtjhTzbHsq;
    /**
     * 定调价后减年初版-回收期
     */
    @TableField("dtjh_ncb_hsq")
    private String dtjhNcbHsq;
    /**
     * 定调价后减定调价前-回收期
     */
    @TableField("dtjh_dtjq_hsq")
    private String dtjhDtjqHsq;
    /**
     * 已实现-定调价前-利润额
     */
    @TableField("ysx_dtjq_lre")
    private String ysxDtjqLre;
    /**
     * 已实现-定调价前-利润率
     */
    @TableField("ysx_dtjq_lrl")
    private String ysxDtjqLrl;
    /**
     * 已实现-定调价后-利润额
     */
    @TableField("ysx_dtjh_lre")
    private String ysxDtjhLre;
    /**
     * 已实现-定调价后-利润率
     */
    @TableField("ysx_dtjh_lrl")
    private String ysxDtjhLrl;
    /**
     * 已实现-投资版-利润额
     */
    @TableField("ysx_tzb_lre")
    private String ysxTzbLre;
    /**
     * 已实现-投资版-利润率
     */
    @TableField("ysx_tzb_lrl")
    private String ysxTzbLrl;
    /**
     * 已实现-金额变动
     */
    @TableField("ysx_jebd")
    private String ysxJebd;
    /**
     * 已实现-利润率变动
     */
    @TableField("ysx_lrlbd")
    private String ysxLrlbd;
    /**
     * 当年预计-定调价前-利润额
     */
    @TableField("dnyj_dtjq_lre")
    private String dnyjDtjqLre;
    /**
     * 当年预计-定调价前-利润率
     */
    @TableField("dnyj_dtjq_lrl")
    private String dnyjDtjqLrl;
    /**
     * 当年预计-定调价后-利润额
     */
    @TableField("dnyj_dtjh_lre")
    private String dnyjDtjhLre;
    /**
     * 当年预计-定调价后-利润率
     */
    @TableField("dnyj_dtjh_lrl")
    private String dnyjDtjhLrl;
    /**
     * 当年预计-投资版-利润额
     */
    @TableField("dnyj_tzb_lre")
    private String dnyjTzbLre;
    /**
     * 当年预计-投资版-利润率
     */
    @TableField("dnyj_tzb_lrl")
    private String dnyjTzbLrl;
    /**
     * 当年预计-金额变动
     */
    @TableField("dnyj_jebd")
    private String dnyjJebd;
    /**
     * 当年预计-利润率变动
     */
    @TableField("dnyj_lrlbd")
    private String dnyjLrlbd;
    /**
     * 待实现-定调价前-利润额
     */
    @TableField("dsx_dtjq_lre")
    private String dsxDtjqLre;
    /**
     * 待实现-定调价前-利润率
     */
    @TableField("dsx_dtjq_lrl")
    private String dsxDtjqLrl;
    /**
     * 待实现-定调价后-利润额
     */
    @TableField("dsx_dtjh_lre")
    private String dsxDtjhLre;
    /**
     * 待实现-定调价后-利润率
     */
    @TableField("dsx_dtjh_lrl")
    private String dsxDtjhLrl;
    /**
     * 待实现-投资版-利润额
     */
    @TableField("dsx_tzb_lre")
    private String dsxTzbLre;
    /**
     * 待实现-投资版-利润率
     */
    @TableField("dsx_tzb_lrl")
    private String dsxTzbLrl;
    /**
     * 待实现-金额变动
     */
    @TableField("dsx_jebd")
    private String dsxJebd;
    /**
     * 待实现-利润率变动
     */
    @TableField("dsx_lrlbd")
    private String dsxLrlbd;
    /**
     * 当批次调价前-权益前-利润额
     */
    @TableField("dpctjq_qyq_lre")
    private String dpctjqQyqLre;
    /**
     * 当批次调价后-权益前-利润额
     */
    @TableField("dpctjh_qyq_lre")
    private String dpctjhQyqLre;
    /**
     * 差异-权益前-利润额
     */
    @TableField("cy_qyq_lre")
    private String cyQyqLre;
    /**
     * 当批次调价前-权益后-利润额
     */
    @TableField("dpctjq_qyh_lre")
    private String dpctjqQyhLre;
    /**
     * 当批次调价后-权益后-利润额
     */
    @TableField("dpctjh_qyh_lre")
    private String dpctjhQyhLre;
    /**
     * 差异-权益后-利润额
     */
    @TableField("cy_qyh_lre")
    private String cyQyhLre;
    /**
     * 当批次调价前-利润率
     */
    @TableField("dpctjq_lrl")
    private String dpctjqLrl;
    /**
     * 当批次调价后-利润率
     */
    @TableField("dpctjh_lrl")
    private String dpctjhLrl;
    /**
     * 差异-利润率
     */
    @TableField("cy_lrl")
    private String cyLrl;


}
