package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 */
public class PrivateSettingBean {

    /**
     * settingKey : showTender
     * settingValue : 1
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String settingKey;
        private String settingValue;

        public String getSettingKey() {
            return settingKey;
        }

        public void setSettingKey(String settingKey) {
            this.settingKey = settingKey;
        }

        public String getSettingValue() {
            return settingValue;
        }

        public void setSettingValue(String settingValue) {
            this.settingValue = settingValue;
        }
    }
}
