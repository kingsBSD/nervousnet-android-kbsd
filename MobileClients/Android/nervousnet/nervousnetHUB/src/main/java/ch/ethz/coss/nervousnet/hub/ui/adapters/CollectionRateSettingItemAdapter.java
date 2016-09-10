package ch.ethz.coss.nervousnet.hub.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;

public class CollectionRateSettingItemAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] labels;
    private final Integer[] icons;
    private Button button;

    public CollectionRateSettingItemAdapter(Context context, String[] sensor_labels, Integer[] icons_arr) {
        super(context, R.layout.collection_rate_item, sensor_labels);
        this.context = context;
        this.labels = sensor_labels;
        this.icons = icons_arr;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.collection_rate_item, null);
        }
        final int position = pos;
        TextView textView = (TextView) convertView.findViewById(R.id.sensor_name);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.sensor_icon);
        textView.setText(labels[position]);
        imageView.setImageResource(icons[position]);

        button = (Button) convertView.findViewById(R.id.sensor_level_button);
        int sensorState = ((Application) context.getApplicationContext()).nn_VM.getSensorState(position);
        if (pos != LibConstants.SENSOR_NOTIFICATION) { // doesn't make sense to specify a frequency for Notifications and other events.
            button.setText(NervousnetVMConstants.sensor_freq_labels[sensorState]);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NNLog.d("CollectionRateSettingItemAdapter", "button on click");
                    createDialog(position).show();
                }
            });
        }
        else { // event-based sensors should just toggle on-off.
            if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
                button.setText(NervousnetVMConstants.sensor_freq_labels[0]);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new NNEvent((long) position, NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW, NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST));
                    }
                });
            }
            else {
                button.setText(NervousnetVMConstants.sensor_on);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new NNEvent((long) position, NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF, NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST));
                    }
                });
            }
        }

        if (((Application) context.getApplicationContext()).nn_VM.getState() == NervousnetVMConstants.STATE_PAUSED
                || ((Application) context.getApplicationContext()).nn_VM
                .getSensorState(position) == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE
                || ((Application) context.getApplicationContext()).nn_VM
                .getSensorState(position) == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
            button.setAlpha(.4f);
            button.setClickable(false);
        }

        return convertView;
    }

    public Dialog createDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.choose_sensor_rate_label);
        builder.setItems(NervousnetVMConstants.sensor_freq_labels, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int itemClicked) {
                String[] option_array = NervousnetVMConstants.sensor_freq_labels;
                String optionSelected = option_array[itemClicked];

                EventBus.getDefault().post(new NNEvent((long) position, (byte) itemClicked, NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST));
//                ((Application) context.getApplicationContext()).nn_VM.updateSensorConfig((long) position,
//                        (byte) itemClicked);
//                ((Activity) context).finish();
//                context.startActivity(((Activity) context).getIntent());
            }
        });
        return builder.create();
    }

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

}