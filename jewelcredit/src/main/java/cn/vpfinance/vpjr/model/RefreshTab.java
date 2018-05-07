package cn.vpfinance.vpjr.model;

/**
 */
public class RefreshTab {

    public static final int TAB_HOME = 100;
    public static final int TAB_LIST = 101;
    public static final int TAB_MINE = 102;

    public static final int LIST_NONE = 0;
    public static final int LIST_REGULAR_PRODUCT = 1;
    public static final int LIST_TRANSFER_PRODUCT = 2;
    public static final int LIST_DEPOSIT_PRODUCT = 3;

    public int tabType;
    public int listType;

    /**
     * 刷新tab
     * @param tabType 从0开始
     * @param //listType 如果tab是产品列表的时候,1定期,2债权转让,3定存宝(其他tab传0)
     */
    public RefreshTab(int tabType) {
        this.tabType = tabType;
    }
//    public RefreshTab(int tabType,int listType) {
//        this.tabType = tabType;
//        this.listType = listType;
//    }
}
