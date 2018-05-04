package cn.vpfinance.vpjr.module.user.personal;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.jewelcredit.ui.widget.ActionBarLayout;
import com.jewelcredit.util.AppState;
import com.jewelcredit.util.HttpService;
import com.jewelcredit.util.ServiceCmd;
import com.jewelcredit.util.Utils;
import com.tdk.control.ActionSheet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.base.BaseActivity;
import cn.vpfinance.vpjr.adapter.BankListViewAdapter;
import cn.vpfinance.vpjr.greendao.BankCard;
import cn.vpfinance.vpjr.greendao.BankCardDao;
import cn.vpfinance.vpjr.greendao.DaoMaster;
import cn.vpfinance.vpjr.greendao.DaoSession;
import cn.vpfinance.vpjr.model.Config;
import cn.vpfinance.vpjr.module.setting.BankAddActivity2;

/**
 * 银行卡绑定
 *
 */
public class BankManageActivity extends BaseActivity implements OnItemClickListener{

	private HttpService mHttpService = null;

	private List<BankCard> bankList;

	private ListView mListView = null;
	private BankListViewAdapter mListAdapter = null;

	private ActionBarLayout titleBar;
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_bank_manage);
        
        DaoMaster.DevOpenHelper dbHelper;
        SQLiteDatabase db;
        DaoMaster daoMaster;
        DaoSession daoSession;
        BankCardDao dao;

        dbHelper = new DaoMaster.DevOpenHelper(this, Config.DB_NAME , null);
        db = dbHelper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        dao = daoSession.getBankCardDao();

//		if (dao != null) {
//			QueryBuilder<BankCard> qb = dao.queryBuilder();
//			bankList = qb.list();
//		}
		
		initView();

//		updateNodataTips(bankList == null || bankList.isEmpty());
        mHttpService = new HttpService(this, this);
	}
	
	
	public void onResume() {
		super.onResume();
		refresh();
	}

	protected void initView() {
		titleBar = (ActionBarLayout)findViewById(R.id.titleBar);
		titleBar.setTitle("银行卡绑定").setHeadBackVisible(View.VISIBLE);

		mListView = (ListView)findViewById(R.id.bankmanage_listview);
		mListAdapter = new BankListViewAdapter(this, bankList);
		mListView.setAdapter(mListAdapter);
		mListView.setOnItemClickListener(this);
		
		findViewById(R.id.nodata_tips).setVisibility(View.GONE);
		setTheme(R.style.ActionSheetStyleIOS7);
	}

	@Override
	public void onHttpCache(int reqId) {
		
	}
	
	@Override
	public void onHttpSuccess(int reqId, JSONObject json)
	{
		if (!isHttpHandle(json)) return;
		
//		if(reqId == ServiceCmd.CmdId.CMD_removeBankCard.ordinal())
//		{
//			String msg = mHttpService.onRemoveBankCard(json);
//			if(mHttpService.checkError(msg))
//			{
//				return;
//			}
//		}

		if (reqId == ServiceCmd.CmdId.CMD_getBankCard.ordinal()) {
			BankCard card = mHttpService.onGetBankCard(this, json);
			if (card != null) {
				if (bankList == null) {
					bankList = new ArrayList<BankCard>();
				}
				else
				{
					bankList.clear();
				}
				bankList.add(card);
			}
			mListAdapter.setModel(bankList);
			if(card!=null) {
				((ActionBarLayout) findViewById(R.id.titleBar)).setActionRightVisible(View.GONE);
			} else {
				((ActionBarLayout) findViewById(R.id.titleBar)).setActionRightVisible(View.VISIBLE);
			}
			updateNodataTips(card==null);
		}

		if (reqId == ServiceCmd.CmdId.CMD_delBankCard.ordinal()) {
			String msg = mHttpService.onDelBankCard(json);
			if (msg.contains("成功")) {
				Utils.Toast(this, "操作成功");
				refresh();
			} else {
				Utils.Toast(this, msg);
			}
		}
	}

	@Override
	public void onHttpError(int reqId, String errmsg) {
		
	}

	public void showActionSheet() {
		ActionSheet menuView = new ActionSheet(this);
		menuView.setCancelButtonTitle("取消");// before add items
		menuView.addItems("删除");
		menuView.setItemClickListener(new ActionSheet.MenuItemClickListener() {
			
			@Override
			public void onItemClick(int itemPosition) {
				// TODO Auto-generated method stub
				if(itemPosition == 0) {
//					BankManageActivity.this.delCard();
					mHttpService.delBankCard();
				}
			}
		});
		
		menuView.setCancelableOnTouchMenuOutside(true);
		menuView.showMenu();
	}
	
	
	public void onItemClick(AdapterView parent, View view, int position, long id) {
//		showActionSheet();
	}

	/*
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        // TODO Auto-generated method stub  
        super.onActivityResult(requestCode, resultCode, data);  
        if(requestCode == 1 && resultCode == 1){
        	doGetBankList();
        }
    }
    */
	

	private void updateNodataTips(boolean empty) {
		if(empty) {
			findViewById(R.id.nodata_tips).setVisibility(View.VISIBLE);
			findViewById(R.id.bankmanage_listview).setVisibility(View.GONE);
			titleBar.setActionRight("添加", new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(BankManageActivity.this, BankAddActivity2.class));
				}
			});
		} else {
			findViewById(R.id.nodata_tips).setVisibility(View.GONE);
			findViewById(R.id.bankmanage_listview).setVisibility(View.VISIBLE);
			titleBar.setActionRightVisible(View.GONE);
		}
	}

	private void refresh() {
		mHttpService.getBankCard(AppState.instance().getSessionCode());
	}
}
