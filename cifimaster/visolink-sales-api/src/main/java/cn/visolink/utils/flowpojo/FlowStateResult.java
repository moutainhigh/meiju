package cn.visolink.utils.flowpojo;

import lombok.Data;

/**
 * 流程状态更新结果
 */
@Data
public class FlowStateResult {

    private boolean state;

    private String message;

    private String instId;

    public FlowStateResult() {
    }

    public FlowStateResult(boolean state, String message) {
        this.state = state;
        this.message = message;
    }
}
