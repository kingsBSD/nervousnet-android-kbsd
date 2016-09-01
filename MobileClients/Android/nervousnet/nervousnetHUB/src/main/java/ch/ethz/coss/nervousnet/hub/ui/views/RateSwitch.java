package ch.ethz.coss.nervousnet.hub.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import ch.ethz.coss.nervousnet.hub.R;

/**
 * Created by prasad on 22/08/16.
 */
public class RateSwitch extends LinearLayout {


    public RateSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_rate_switch, this);
    }


}
