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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;

/**
 * @author prasad
 */

public class GenericSettingsActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_settings);

        final TextView uuid_tv = (TextView) findViewById(R.id.uuid_TV);
        uuid_tv.setText(((Application) getApplication()).nn_VM.getUUID().toString());

        findViewById(R.id.uuid_gen_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Application) getApplication()).nn_VM.newUUID();
                uuid_tv.setText(((Application) getApplication()).nn_VM.getUUID().toString());
            }
        });
    }


    public void showInfo(View view) {
        String title = "UUID:";

        // Includes the updates as well so users know what changed.
        String message = "\n\n- is the Unique ID associated with data stored and shared from a specific installation on your device. " +
                "\n- It can be used to view data shared from your device." +
                "\n- Use the 'Generate new UUID' button to generate a new UUID for your installation." +
                "\n- Every time you uninstall and reinstall the Nervousnet App, a new UUID is generated." +
                "\n- Every time a new UUID is generated, any link to you old UUID and data is lost.";


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
}
