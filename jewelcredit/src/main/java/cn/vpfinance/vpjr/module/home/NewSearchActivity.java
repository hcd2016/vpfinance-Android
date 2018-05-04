package cn.vpfinance.vpjr.module.home;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.Constant;
import cn.vpfinance.vpjr.FinanceApplication;
import cn.vpfinance.vpjr.adapter.LoanSignDepositListAdapter;
import cn.vpfinance.vpjr.adapter.LoanSignListNewAdapter;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.gson.LoanSignDepositBean;
import cn.vpfinance.vpjr.gson.LoanSignListNewBean;
import cn.vpfinance.vpjr.module.list.RegularProductListNewFragment;
import cn.vpfinance.vpjr.module.product.NewRegularProductActivity;
import cn.vpfinance.vpjr.module.product.shenyang.PresellProductActivity;
import cn.vpfinance.vpjr.module.product.transfer.NewTransferProductActivity;
import cn.vpfinance.vpjr.util.Common;
import cn.vpfinance.vpjr.util.StatusBarCompat1;
import cn.vpfinance.vpjr.view.EditTextWithDel;

/**
 */
public class NewSearchActivity extends BaseActivity {

    @Bind(R.id.search_text)
    EditTextWithDel mSearchText;
    @Bind(R.id.close)
    TextView mClose;
    @Bind(R.id.listView)
    ListView mListView;

    private HttpService mHttpService;
    private final static int PAGE_SIZE = 10;
    private int page;

    private Context mContext;
    private boolean isLastPage = false;
    private LoanSignListNewAdapter mListAdapter;
    private List<LoanSignListNewBean.LoansignsBean> totalData = new ArrayList<>();
    private List<LoanSignDepositBean.LoansignpoolBean> mDepositData = new ArrayList<>();
    private LoanSignDepositListAdapter mDepositAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        View fakeStatusBar = findViewById(R.id.fake_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            fakeStatusBar.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = fakeStatusBar.getLayoutParams();
            layoutParams.height = StatusBarCompat1.getStatusBarHeight(this);
            fakeStatusBar.setLayoutParams(layoutParams);
        } else {
            fakeStatusBar.setVisibility(View.GONE);
        }

        mContext = this;
        mHttpService = new HttpService(this, this);

        int currentListTabType = ((FinanceApplication) getApplication()).currentListTabType;
//
        if (currentListTabType == Constant.TYPE_POOL) {
            mDepositAdapter = new LoanSignDepositListAdapter(mContext);
            mListView.setAdapter(mDepositAdapter);
        } else {
            mListAdapter = new LoanSignListNewAdapter(this);
            mListView.setAdapter(mListAdapter);
        }

        //如果输入法在窗口上已经显示，则隐藏，反之则显示
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        //弹出键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_FORCED);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        //down事件和up事件都会执行  加个判断
                        page = 0;
                        totalData.clear();
                        mDepositData.clear();
                        requestNet();
                    }
                    hideInput();
                    return true;
                }
                return false;
            }
        });

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String search_text = editable.toString();
                if (TextUtils.isEmpty(search_text)) {
                    mClose.setText("取消");
                } else {
                    mClose.setText("搜索");
                }
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == view.getCount() - 1 && !isLastPage) {
                    page++;
                    requestNet();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int currentListTabType = ((FinanceApplication) getApplication()).currentListTabType;

                switch (currentListTabType) {
                    case Constant.TYPE_BANK:
                        if (mListAdapter != null) {
                            LoanSignListNewBean.LoansignsBean bankBean = mListAdapter.getItem(position);
                            if ((!isFinishing()) && bankBean != null && bankBean.loansign != null) {
                                if (bankBean.loansign.productType == 3) {
                                    PresellProductActivity.goPresellProductActivity(NewSearchActivity.this, "" + bankBean.loansign.id);
                                } else {
                                    NewRegularProductActivity.goNewRegularProductActivity(mContext, bankBean.loansign.id, 0, bankBean.loansignbasic.loanTitle, false, 1);
                                }
                            }
                        }
                        break;
                    case Constant.TYPE_REGULAR:
                        if (mListAdapter != null) {
                            LoanSignListNewBean.LoansignsBean regularBean = mListAdapter.getItem(position);
                            if ((!isFinishing()) && regularBean != null && regularBean.loansign != null) {
                                if (regularBean.loansign.productType == 3) {
                                    PresellProductActivity.goPresellProductActivity(NewSearchActivity.this, "" + regularBean.loansign.id);
                                } else {
                                    NewRegularProductActivity.goNewRegularProductActivity(mContext, regularBean.loansign.id, 0, regularBean.loansignbasic.loanTitle, false, 0);
                                }
                            }
                        }
                        break;
                    case Constant.TYPE_TRANSFER:
                        if (mListAdapter != null) {
                            LoanSignListNewBean.LoansignsBean tranfBean = mListAdapter.getItem(position);
                            if ((!isFinishing()) && tranfBean != null && tranfBean.loansign != null) {
                                NewTransferProductActivity.goNewTransferProductActivity(mContext, tranfBean.loansign.id);
                            }
                        }
                        break;
                    case Constant.TYPE_POOL:
                        if (mDepositAdapter != null) {
                            LoanSignDepositBean.LoansignpoolBean item = mDepositAdapter.getItem(position);
                            if (item != null && item.id != 0) {
                                int pid = item.id;
                                String loanTitle = item.loanTitle;
                                NewRegularProductActivity.goNewRegularProductActivity(mContext, pid, 0, loanTitle, true, 0);
                            }
                        }
                        break;
                }
            }
        });

    }

    public void requestNet() {
        String searchText = mSearchText.getText().toString();
        int currentListTabType = ((FinanceApplication) getApplication()).currentListTabType;

        switch (currentListTabType){
            case Constant.TYPE_BANK:
                mHttpService.getLoanSignListNew(Constant.TYPE_BANK, page * PAGE_SIZE, PAGE_SIZE, searchText);
                break;
            case Constant.TYPE_REGULAR:
                mHttpService.getLoanSignListNew(Constant.TYPE_REGULAR, page * PAGE_SIZE, PAGE_SIZE, searchText);
                break;
            case Constant.TYPE_TRANSFER:
                mHttpService.getLoanSignListNew(Constant.TYPE_TRANSFER, page * PAGE_SIZE, PAGE_SIZE, searchText);
                break;
            case Constant.TYPE_POOL:
                mHttpService.getLoanSignPool(page * PAGE_SIZE, PAGE_SIZE, searchText);
                break;
        }
    }

    @Override
    public void onHttpSuccess(int reqId, JSONObject json) {
        if (!isHttpHandle(json)) return;

        if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_List_New.ordinal()) {
            LoanSignListNewBean listNew = mHttpService.onGetLoanSignListNew(json);
            if (listNew == null) {
                Utils.Toast(mContext, "没有查找到相关产品");
            } else {
                if (listNew.loansigns != null) {
                    isLastPage = listNew.total <= (page + 1) * PAGE_SIZE;
                    if (page == 0){
                        totalData.clear();
                    }
                    totalData.addAll(listNew.loansigns);
                }
                int currentListTabType = ((FinanceApplication) getApplication()).currentListTabType;
                mListAdapter.setData(totalData, currentListTabType);
            }
        }else if (reqId == ServiceCmd.CmdId.CMD_Loan_Sign_Pool.ordinal()){
            //isLastPage
            LoanSignDepositBean loanSignDepositBean = mHttpService.onGetLoanSignPool(json);
            if (loanSignDepositBean != null && loanSignDepositBean.loansignpool != null && loanSignDepositBean.loansignpool.size() != 0){//有数据
                if (page == 0){
                    mDepositData.clear();
                }
                mDepositData.addAll(loanSignDepositBean.loansignpool);
                mDepositAdapter.setData(mDepositData);
            }
        }
    }

    @OnClick(R.id.close)
    public void onClick() {
        if ("取消".equals(mClose.getText())) {
            hideInput();
            finish();
        } else {
            //搜索
            //取消键盘
            hideInput();
            totalData.clear();
            mDepositData.clear();
            page = 0;
            requestNet();
        }
    }

    private void hideInput() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
