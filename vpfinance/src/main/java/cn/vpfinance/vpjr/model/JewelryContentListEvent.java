package cn.vpfinance.vpjr.model;

import java.util.List;

import cn.vpfinance.vpjr.gson.JewelryBean;

/**
 */
public class JewelryContentListEvent {

    public List<JewelryBean.ContentListEntity> list;

    public JewelryContentListEvent(List<JewelryBean.ContentListEntity> list) {
        this.list = list;
    }
}
