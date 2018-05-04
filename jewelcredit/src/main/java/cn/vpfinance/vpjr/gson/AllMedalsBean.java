package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 */
public class AllMedalsBean {


    /**
     * id : 15
     * logo : http://192.168.1.193/resources/imgs/medal/medal7_yes_
     * condition : 用APP投资理财产品
     * description : 即时更新，有效期永久(并列)
     * name : 掌控全局
     * medalCategoryId : 2
     * number : 1
     */

    private List<HadMedalsBean> hadMedals;
    /**
     * id : 12
     * logo : http://192.168.1.193/resources/imgs/medal/medal1_no_
     * condition : 参与了玩转旅行活动
     * description : 即时更新，有效期永久(并列)
     * name : 旅行达人
     * medalCategoryId : 2
     */

    private List<NoMedalsBean> noMedals;

    public List<HadMedalsBean> getHadMedals() {
        return hadMedals;
    }

    public void setHadMedals(List<HadMedalsBean> hadMedals) {
        this.hadMedals = hadMedals;
    }

    public List<NoMedalsBean> getNoMedals() {
        return noMedals;
    }

    public void setNoMedals(List<NoMedalsBean> noMedals) {
        this.noMedals = noMedals;
    }

    public static class HadMedalsBean {
        private int id;
        private String logo;
        private String condition;
        private String description;
        private String name;
        private String medalCategoryId;
        private int number;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMedalCategoryId() {
            return medalCategoryId;
        }

        public void setMedalCategoryId(String medalCategoryId) {
            this.medalCategoryId = medalCategoryId;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    public static class NoMedalsBean {
        private int id;
        private String logo;
        private String condition;
        private String description;
        private String name;
        private String medalCategoryId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMedalCategoryId() {
            return medalCategoryId;
        }

        public void setMedalCategoryId(String medalCategoryId) {
            this.medalCategoryId = medalCategoryId;
        }
    }
}
