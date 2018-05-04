package cn.vpfinance.vpjr.gson;

import java.util.List;

/**
 * Created by zzlz13 on 2017/12/19.
 */

public class LoanSignTypeBean {

    public List<TypesBean> types;

    public static class TypesBean {
        /**
         * name : 存管专区
         * value : 4
         */

        public String name;
        public int value;
    }
}
