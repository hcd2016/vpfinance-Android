package cn.vpfinance.vpjr.model;

import java.util.List;

/**
 * Created by Administrator on 2015/11/9.
 * 资金流水（充值提现）bean
 */
public class FundFlowInfo {
    /*流水类型*/
    public String type;
    /*成功失败*/
    public String success;
    /*总页数*/
    public String totalPage;

    public List<FlowList> list;

    public class FlowList{
        /*类型名称*/
        private String explan;
        /*流水金额*/
        private String flowMoney;
        /*操作时间*/
        private String time;
        /*可用余额*/
        private String money;

        /*只有在提现未审核时候有，用来做取消提现的请求参数*/
        private String applyId;

        /*提现状态 ： 0未审核  1已结审核 2已取消*/
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getExplan() {
            return explan;
        }

        public void setExplan(String explan) {
            this.explan = explan;
        }

        public String getFlowMoney() {
            return flowMoney;
        }

        public void setFlowMoney(String flowMoney) {
            this.flowMoney = flowMoney;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }
    }

}
