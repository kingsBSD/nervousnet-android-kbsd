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
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;

import ch.ethz.coss.nervousnet.hub.R;

/**
 * @author prasad
 */
public class AnalyticsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);


        ListView sensList = (ListView) findViewById(R.id.sensors_list_SensStatChart);

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, NervousnetVMConstants.sensor_labels);
        sensList.setAdapter(modeAdapter);
    }


    public void onButtonTimeRangePlotClick(View v) {

        /* Show a dialog to make user select from/to dates and times */
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.time_range_plots_input, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(AnalyticsActivity.this, "Clicked OK", Toast.LENGTH_SHORT).show();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing
                    }
                });

        TimePicker fromTimePicker = ((TimePicker) dialogLayout.findViewById(R.id.fromTimePicker));
        TimePicker toTimePicker = ((TimePicker) dialogLayout.findViewById(R.id.toTimePicker));
        DatePicker fromDatePicker = ((DatePicker) dialogLayout.findViewById(R.id.fromDatePicker));
        DatePicker toDatePicker = ((DatePicker) dialogLayout.findViewById(R.id.toDatePicker));

        fromTimePicker.setIs24HourView(true);
        toTimePicker.setIs24HourView(true);

        TabHost tabHost = (TabHost) dialogLayout.findViewById(R.id.tabHost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("From");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("To");

        tab1.setIndicator("From");
        tab1.setContent(R.id.From);
        tab2.setIndicator("To");
        tab2.setContent(R.id.To);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);

        builder.show();
    }
}
