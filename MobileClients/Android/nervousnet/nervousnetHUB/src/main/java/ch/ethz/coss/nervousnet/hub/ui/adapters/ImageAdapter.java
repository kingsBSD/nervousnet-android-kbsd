/*******************************************************************************
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *      It is responsible for collecting and managing data in a fully de-centralised fashion
 * *
 * *     Copyright (C) 2016 ETH Zürich, COSS
 * *
 * *     This file is part of Nervousnet Framework
 * *
 * *     Nervousnet is free software: you can redistribute it and/or modify
 * *     it under the terms of the GNU General Public License as published by
 * *     the Free Software Foundation, either version 3 of the License, or
 * *     (at your option) any later version.
 * *
 * *     Nervousnet is distributed in the hope that it will be useful,
 * *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * *     GNU General Public License for more details.
 * *
 * *     You should have received a copy of the GNU General Public License
 * *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.hub.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    // references to our images
    private Integer[] mThumbIds = {R.drawable.ic_missing, R.drawable.ic_missing, R.drawable.ic_missing,
            R.drawable.ic_missing, R.drawable.ic_missing, R.drawable.ic_missing};
    // references to our labels
    private String[] mLabels;

    public ImageAdapter(Context c, String[] labels, Integer[] icons) {
        mContext = c;
        mLabels = labels;// mContext.getResources().getStringArray(R.array.main_grid);
        mThumbIds = icons;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View MyView = convertView;

        if (convertView == null) {
            /* we define the view that will display on the grid */

            // Inflate the layout_rate_switch
            LayoutInflater li = ((Activity) mContext).getLayoutInflater();
            MyView = li.inflate(R.layout.category_grid_item, null);
        }
        // Add The Text!!!
        TextView tv = (TextView) MyView.findViewById(R.id.grid_item_text);
        tv.setText(mLabels[position]);
        // Add The Image!!!
        ImageView iv = (ImageView) MyView.findViewById(R.id.grid_item_image);
        iv.setImageResource(mThumbIds[position]);
        iv.setScaleType(ScaleType.FIT_XY);

        return MyView;
    }

}