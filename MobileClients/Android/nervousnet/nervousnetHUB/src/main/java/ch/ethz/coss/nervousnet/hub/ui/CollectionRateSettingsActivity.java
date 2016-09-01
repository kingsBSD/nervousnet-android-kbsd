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

package ch.ethz.coss.nervousnet.hub.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.Constants;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.CollectionRateSettingItemAdapter;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;

/**
 * @author prasad
 */

public class CollectionRateSettingsActivity extends BaseActivity {

    ImageButton globalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_rate);
        final ListView listview = (ListView) findViewById(R.id.coll_rate_listview);
        final CollectionRateSettingItemAdapter adapter = new CollectionRateSettingItemAdapter(
                CollectionRateSettingsActivity.this, NervousnetVMConstants.sensor_labels, Constants.icon_array_sensors);
        listview.setAdapter(adapter);

        globalButton = (ImageButton) findViewById(R.id.globalOptionsButton);
        globalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NNLog.d("CollectionRateSettingsActivity", "Global collection rate Options button clicked");
                createDialog().show();
            }
        });

        if (((Application) getApplication()).nn_VM.getState() == NervousnetVMConstants.STATE_PAUSED) {
            globalButton.setAlpha(.4f);
            globalButton.setClickable(false);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public Dialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.collection_rate_global_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int itemClicked) {
                String[] option_array = getResources().getStringArray(R.array.collection_rate_global_options);
                String optionSelected = option_array[itemClicked];

                EventBus.getDefault().post(new NNEvent((byte) itemClicked, NervousnetVMConstants.EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST));

//                ((Application) getApplication()).nn_VM.updateAllSensorConfig(
//                        (byte) itemClicked);


            }
        });
        return builder.create();
    }


    public void showInfo(View view) {
        String title = "Sensor Collection Rate Settings:";

        // Includes the updates as well so users know what changed.
        String message = "\n\n- Settings to control the frequency of Sensors " +
                "\n- Various levels of frequency can be selected" +
                "\n          - HIGH, MEDIUM, LOW or OFF" +
                "\n";


        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new Dialog.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });
        builder.setCancelable(false);

        AlertDialog alert = builder.create();
        alert.show();

        alert.getWindow().getAttributes();

        TextView textView = (TextView) alert.findViewById(android.R.id.message);
        textView.setTextSize(12);
    }

    @Subscribe
    public void onNNEvent(NNEvent event) {
        NNLog.d("CollectionRateSettingsActivity", "onSensorStateEvent called ");

        if (event.eventType == NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED) {
            finish();
            startActivity(getIntent());
        } else if (event.eventType == NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED) {

            if (((Application) getApplication()).nn_VM.getState() == NervousnetVMConstants.STATE_PAUSED) {
                finish();
                startActivity(getIntent());
            } else {
                finish();
                startActivity(getIntent());
            }
        }

    }
}


