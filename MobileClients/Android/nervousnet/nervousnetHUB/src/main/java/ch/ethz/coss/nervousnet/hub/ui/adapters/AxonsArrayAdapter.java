package ch.ethz.coss.nervousnet.hub.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.R;

/**
 * Created by Irene on 02/08/2016.
 */
public class AxonsArrayAdapter extends ArrayAdapter<NervousnetAxon> {
    Context context;
    int layoutResourceId;
    NervousnetAxon data[];

    public AxonsArrayAdapter(Context context, int resource, NervousnetAxon[] objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.axon_list_item, null);
        }

        TextView txtAxonName = (TextView) convertView.findViewById(R.id.txt_axon_name);
        ImageView imgAxon = (ImageView) convertView.findViewById(R.id.img_axon);
        txtAxonName.setText(data[position].axonName);
        imgAxon.setImageResource(data[position].axonImgResource);
        return convertView;
    }

}

