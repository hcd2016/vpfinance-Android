package cn.vpfinance.vpjr.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jewelcredit.util.Utils;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.download.SpUtils;
import cn.vpfinance.vpjr.module.dialog.CommonDialogFragment;
import cn.vpfinance.vpjr.module.dialog.RechargeCloseDialog;
import cn.vpfinance.vpjr.module.setting.RealnameAuthActivity;
import cn.vpfinance.vpjr.module.trade.RechargeActivity;


/**
 */
public class AlertDialogUtils {

    public static Dialog showInvestLoadingDialog(Activity activity){
        Dialog dialog = new Dialog(activity, R.style.MyTransparent);
        dialog.setCancelable(false);
        dialog.show();
        LayoutInflater inflater = LayoutInflater.from(activity);
        View viewDialog = inflater.inflate(R.layout.dialog_invest_loading, null);
        Display display = activity.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        //设置dialog的宽高为屏幕的宽高
        ViewGroup.LayoutParams layoutParams = new  ViewGroup.LayoutParams(width, height);
        dialog.setContentView(viewDialog, layoutParams);
        return dialog;
    }

    public static void showAlertDialog(final Activity context, String title, String strCancel, String strOk, final Class clazz){

        final CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(title, "", strOk, strCancel);
        dialogFragment.setOnAllLinstener(new CommonDialogFragment.onAllListener() {
            @Override
            public void clickOk() {
                if (clazz != null){
                    context.startActivity(new Intent(context, clazz));
                }
            }

            @Override
            public void clickCancel() {

            }
        });
        dialogFragment.show(context.getFragmentManager(),"CommonDialogFragment");
    }

    public static void showAlertDialog(final Activity context, String title,String messags, String strCancel, String strOk, final Class clazz){

        final CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(title, messags, strOk, strCancel);
        dialogFragment.setOnAllLinstener(new CommonDialogFragment.onAllListener() {
            @Override
            public void clickOk() {
                if (clazz != null){
                    context.startActivity(new Intent(context, clazz));
                }
            }

            @Override
            public void clickCancel() {

            }
        });
        dialogFragment.show(context.getFragmentManager(),"CommonDialogFragment");
    }

    public static void showRechargCloseDialog(final Activity context, String title,String messags, String strCancel, String strOk){

        final CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(title, messags, strOk, strCancel);
        dialogFragment.setOnAllLinstener(new CommonDialogFragment.onAllListener() {
            @Override
            public void clickOk() {
                boolean isAllowRecharge = SharedPreferencesHelper.getInstance(context).getBooleanValue(SharedPreferencesHelper.KEY_ALLOW_RECHARGE);
                if (isAllowRecharge){
                    confirmGoRecharg(context);
                }else{
                    new RechargeCloseDialog().show(context.getFragmentManager(),"RechargeCloseDialog");
                }
            }

            @Override
            public void clickCancel() {

            }
        });
        dialogFragment.show(context.getFragmentManager(),"CommonDialogFragment");
    }

    public static void showAlertDialog(final Activity context,String title,String strCancel,String strOk, final Intent intent){

        final CommonDialogFragment dialogFragment = CommonDialogFragment.newInstance(title, "", strOk, strCancel);
        dialogFragment.setOnAllLinstener(new CommonDialogFragment.onAllListener() {
            @Override
            public void clickOk() {
                if (intent != null){
                    context.startActivity(intent);
                }
            }

            @Override
            public void clickCancel() {

            }
        });
        dialogFragment.show(context.getFragmentManager(),"CommonDialogFragment");
    }

    public static void confirmGoRecharg(final Context context){
        new AlertDialog.Builder(context).setMessage("由于监管要求，平台发布产品将以银行存管为主，确定要将资金充值到连连账户吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(context, RechargeActivity.class));
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }

    /**
     * 更新dialog
     * @param context
     * @param message
     */
    public static void showUpdateDialog(final Activity context, String message,DialogInterface.OnClickListener positive,int status){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("发现新版本")
                .setMessage(Html.fromHtml(message))
                .setPositiveButton("更新", positive)
                .setCancelable(false);
        if (status != 3){
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //取消三次后，这个版本将不再提示
                    SpUtils spUtils = SpUtils.getInstance(context);
                    int num = spUtils.getInt(SpUtils.UPDATE_ALERT_NUM, 0);
                    spUtils.putInt(SpUtils.UPDATE_ALERT_NUM,++num);
                }
            });
        }
        builder.create().show();
    }

    public static void openBankAccount(final Context context, final boolean isRealName,final String userId){
        new AlertDialog.Builder(context)
                .setMessage("请先开通存管账户")
                .setPositiveButton("去开通", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!isRealName){
                            Utils.Toast(context, "开通存管账户前请先进行实名认证");
                            context.startActivity(new Intent(context, RealnameAuthActivity.class));
                        }else{
                            Utils.goToWeb(context, "/hx/account/create?userId=" + userId,"");
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .create()
                .show();
    }
}
