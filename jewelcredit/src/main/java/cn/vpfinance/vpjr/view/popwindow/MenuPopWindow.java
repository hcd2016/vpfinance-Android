package cn.vpfinance.vpjr.view.popwindow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

/**
 */
public abstract class MenuPopWindow{

    private final ArrayList<Item> mItemList;
    private final PopupWindow mPopupWindow;
    private Context context;
    private ListView mListView;
    private ArrayAdapter<Item> mAdapter;
    private onItemSelectedListener mListener;
    private View view;

    public MenuPopWindow(Context context){
        this.context = context;

        mItemList = new ArrayList<Item>(2);
        view = onCreateView(context);

        view.setFocusableInTouchMode(true);

        mAdapter = onCreateAdapter(context, mItemList);
        mListView = findListView(view);
        mListView.setAdapter(mAdapter);

        mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = mAdapter.getItem(position);
                if (mListener != null) {
                    mListener.selected(view, item, position);
                }
                dismiss();
            }
        });

    }

    public PopupWindow getPopupWindow(){
        if (mPopupWindow != null){
            return mPopupWindow;
        }else{
            return null;
        }
    }

    /** add item*/
    public void addItem(String text,int id){
        mItemList.add(new Item(text, id));
        mAdapter.notifyDataSetChanged();
    }

    public void addItem(int resId,int id){
        addItem(context.getString(resId), id);
    }

    /** show position*/
    public void showAsDropDown(View parent){
        mPopupWindow.showAsDropDown(parent);
    }


    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity){
        //TODO 第一次没数据，测量有问题,lv写死宽度
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int viewWidth = view.getMeasuredWidth();
        WindowManager systemService = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = systemService.getDefaultDisplay().getWidth();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mPopupWindow.showAsDropDown(anchor, width / 2 - viewWidth/2, yoff, gravity);
        }else{
            mPopupWindow.showAsDropDown(anchor, width / 2 - viewWidth/2,yoff);
        }
    }


    /** set listener for click */
    public void setOnItemSelectedListener(onItemSelectedListener listener){
        mListener = listener;
    }

    public static interface onItemSelectedListener{
        public void selected(View view,Item item,int position);
    }

    /** is showing */
    public boolean isShowing(){
        return mPopupWindow.isShowing();
    }

    public void dismiss(){
        mPopupWindow.dismiss();
    }

    /** 实现设置一个adapter*/
    protected abstract ArrayAdapter<Item> onCreateAdapter(Context context, ArrayList<Item> mItemList);

    /** 实现找到lv的id*/
    protected abstract ListView findListView(View view);

    /** 实现并返回我们的弹出菜单的这个view*/
    protected abstract View onCreateView(Context context);

    public static class Item{
        public String text;
        public int id;

        public Item(String text, int id) {
            this.text = text;
            this.id = id;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
