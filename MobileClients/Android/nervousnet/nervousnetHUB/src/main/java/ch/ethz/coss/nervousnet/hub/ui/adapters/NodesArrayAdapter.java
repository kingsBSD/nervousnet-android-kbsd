package ch.ethz.coss.nervousnet.hub.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.R;

/**
 * Created by Patrick on 02/08/2016.
 */
public class NodesArrayAdapter extends ArrayAdapter<NervousnetNode> {

    Context context;
    int layoutResourceID;
    NervousnetNode data[];

    public NodesArrayAdapter(Context context, int resource, NervousnetNode[] data) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceID = resource;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.node_list_item, null);
        }

        TextView txtNodeName = (TextView) convertView.findViewById(R.id.txt_node_name);
        txtNodeName.setText(data[position].nodeName);

        return convertView;

    }

}

