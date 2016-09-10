package ch.ethz.coss.nervousnet.hub.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import ch.ethz.coss.nervousnet.hub.R;

/**
 * Created by grg on 10/09/16.
 */
public class ToggleSwitch extends LinearLayout {


    public ToggleSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_toggle_switch, this);
    }


}