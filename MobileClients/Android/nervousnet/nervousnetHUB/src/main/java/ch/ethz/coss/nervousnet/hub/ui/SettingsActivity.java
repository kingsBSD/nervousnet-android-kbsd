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
import android.os.Bundle;
import android.view.View;

import ch.ethz.coss.nervousnet.hub.R;

/**
 * @author prasad
 */

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.gen_settings_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(new Intent(SettingsActivity.this, GenericSettingsActivity.class));
            }
        });

        findViewById(R.id.coll_rate_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(new Intent(SettingsActivity.this, CollectionRateSettingsActivity.class));
            }
        });

        findViewById(R.id.sharing_nodes_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startNextActivity(new Intent(SettingsActivity.this, SharingNodeActivity.class));
            }
        });

        findViewById(R.id.sensor_perm_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startNextActivity(new Intent(SettingsActivity.this, SensorPermissionsActivity.class));
            }
        });

    }

}
