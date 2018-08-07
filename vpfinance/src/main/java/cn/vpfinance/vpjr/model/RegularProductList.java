package cn.vpfinance.vpjr.model;

import java.util.ArrayList;

import cn.vpfinance.vpjr.gson.FinanceProduct;

/**
 * 定期列表
 */
public class RegularProductList {
    public boolean success;
    public boolean isLastPage = false;
    public int page;

    /*
     * 列表顶部预留一个固定的产品位，产品位跳转到H5详情页。
     */
    public boolean hasTopProduct;
    public String  topProductUrl;

    public ArrayList<FinanceProduct> list;
}
