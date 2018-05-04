package cn.vpfinance.vpjr.module.product.record;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import cn.vpfinance.vpjr.module.common.FragmentContainerActivity;
import cn.vpfinance.vpjr.module.product.fragment.ProductInvestListFragment;

/**
 * Created by zhangqingtian on 16/2/2.
 */
public class ProductInvestListActivity extends FragmentContainerActivity {

    public static final String KEY_PID = "pid";
    public static final String KEY_TYPE = "type";
    public static final String KEY_IS_DEPOSIT = "isDeposit";
    public static final String KEY_TOTAL_COUNT = "frequency";
    public static final String SERVICE_TIME = "serverTime";
    private int totalCount = 0;
    private long serverTime;

    public static void goProductInvestListActivity(Context context,long pid,int type, int frequency, boolean is) {
        if (context != null) {
            Intent intent = new Intent(context, ProductInvestListActivity.class);
            intent.putExtra(KEY_PID, pid);
            intent.putExtra(KEY_TYPE, type);
            intent.putExtra(KEY_IS_DEPOSIT, is);
            intent.putExtra(KEY_TOTAL_COUNT, frequency);
            context.startActivity(intent);
        }
    }

    public static void goProductInvestListActivity(Context context,long pid,int type, int frequency, boolean is,long serviceTime) {
        if (context != null) {
            Intent intent = new Intent(context, ProductInvestListActivity.class);
            intent.putExtra(KEY_PID, pid);
            intent.putExtra(KEY_TYPE, type);
            intent.putExtra(KEY_IS_DEPOSIT, is);
            intent.putExtra(KEY_TOTAL_COUNT, frequency);
            intent.putExtra(SERVICE_TIME, serviceTime);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        long pid = 0;
        int type = -1;
        boolean isDeposit = false;
        if (intent != null) {
            pid = intent.getLongExtra(KEY_PID, 0);
            type = intent.getIntExtra(KEY_TYPE, 0);
            totalCount = intent.getIntExtra(KEY_TOTAL_COUNT, 0);
            isDeposit = intent.getBooleanExtra(KEY_IS_DEPOSIT, false);
            serverTime = intent.getLongExtra(SERVICE_TIME, System.currentTimeMillis());
        }

        Fragment fragment = ProductInvestListFragment.newInstance(pid, type,totalCount,isDeposit,serverTime);
        setFragment(fragment);
        setTitle("投资记录");

    }

}
