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

import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import ch.ethz.coss.nervousnet.hub.Constants;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.ImageAdapter;
import ch.ethz.coss.nervousnet.vm.NNLog;

public class MainActivity extends BaseActivity {
    ServiceConnection mServiceConnection;
    EditText counter;

    View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentView = findViewById(R.id.main_grid);
        GridView gridview = (GridView) parentView.findViewById(R.id.main_grid);

        gridview.setAdapter(new ImageAdapter(MainActivity.this, getResources().getStringArray(R.array.main_grid),
                Constants.icons_main_screen));
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                showNextActivity(position);
            }
        });

    }

    protected void showNextActivity(int position) {

        switch (position) {
            case 0:
                startNextActivity(new Intent(MainActivity.this, SensorDisplayActivity.class));
                break;
            case 1:
                startNextActivity(new Intent(MainActivity.this,
                        AnalyticsActivity.class));
                break;
            case 2:
                startNextActivity(new Intent(MainActivity.this, SpaceActivity.class));
                break;
            case 3:
                startNextActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case 4:
                startNextActivity(new Intent(MainActivity.this,
                        HelpActivity.class));
                break;
            case 5:
                startNextActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
//		case 6:
//			startNextActivity(new Intent(MainActivity.this, ShowSensorListActivity.class));
//			break;
        }
    }

    public void toastToScreen(String msg, boolean lengthLong) {
        int toastLength = lengthLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT;
        Toast.makeText(getApplicationContext(), msg, toastLength).show();
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        NNLog.d("MainActivity", "onRestoreInstanceState");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        NNLog.d("MainActivity", "onSaveInstanceState");
    }

}
