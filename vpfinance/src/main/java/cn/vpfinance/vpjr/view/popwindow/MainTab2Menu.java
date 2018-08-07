package cn.vpfinance.vpjr.view.popwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import cn.vpfinance.android.R;

/**
 */
public class MainTab2Menu extends MenuPopWindow{

    public MainTab2Menu(Context context) {
        super(context);
    }

    @Override
    protected ArrayAdapter<Item> onCreateAdapter(Context context, ArrayList<Item> mItemList) {
        return new ArrayAdapter<Item>(context, R.layout.item_pop_menu, mItemList);
    }

    @Override
    protected ListView findListView(View view) {
        return (ListView)view.findViewById(R.id.menu_listview);
    }

    @Override
    protected View onCreateView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.popwindow_main_tab_plan, null);
    }
}
