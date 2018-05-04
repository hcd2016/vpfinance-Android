
package cn.vpfinance.vpjr.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;

import cn.vpfinance.android.R;
import cn.vpfinance.vpjr.util.FormatUtils;

/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private TextView tvContent;
    private ArrayList<Entry> yValues;
    private ArrayList<String> xValues;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
    }
    public void setXValues(ArrayList<String> xValues,ArrayList<Entry> yValues){
        this.xValues = xValues;
        this.yValues = yValues;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText("" + FormatUtils.formatNumberUnit(ce.getHigh()));
        } else {
            String time = "";
            int index = e.getXIndex();
            if (xValues != null && xValues.size() > index){
                time = xValues.get(index);
            }
            tvContent.setGravity(Gravity.CENTER);
            tvContent.setText(time + "\n¥" +FormatUtils.formatNumberUnit(e.getVal()));
        }
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
