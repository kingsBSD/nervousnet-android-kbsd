/**
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
 */
/**
 *
 */
package ch.ethz.coss.nervousnet.hub.ui;

import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.DbDumpTask;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.vm.NNLog;

/**
 * @author prasad
 */
public abstract class BaseActivity extends Activity implements ActionBarImplementation {

    protected View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateActionBar();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActionBar();
    }

    @Override
    public void updateActionBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.ab_nn, null);

        ActionBar actionBar = null;
        Switch mainSwitch;
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
        mainSwitch = (Switch) findViewById(R.id.mainSwitch);

        byte state = ((Application) getApplication()).getState();
        NNLog.d("BaseActivity", "state = " + state);
        mainSwitch.setChecked(state == 0 ? false : true);

        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                startStopSensorService(isChecked);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.dump_db:
                dumpDb();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startStopSensorService(boolean on) {
        if (on) {
            ((Application) getApplication()).startService(this);
            if (!accessibilityEnabled()) {
                accessibilityPrompt();
            }
        } else {
            ((Application) getApplication()).stopService(this);
        }
        ((Application) getApplication()).setState(this, on ? (byte) 1 : (byte) 0);
//		finish();
//		startActivity(getIntent());
//		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    protected void startNextActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        updateActionBar();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private boolean accessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager) this.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> runningServices = am.getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            //Log.i("accessibility",service.getId());
            if ("ch.ethz.coss.nervousnet.hub/.NotificationService".equals(service.getId())) return true;
        }
        return false;
    }

    private void accessibilityPrompt() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle(this.getString(R.string.accessibiliity_dialog_title));
        myAlertDialog.setMessage(this.getString(R.string.accessibiliity_dialog_prompt));
        myAlertDialog.setPositiveButton(this.getString(R.string.accessibiliity_dialog_confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }});

        myAlertDialog.setNegativeButton(this.getString(R.string.accessibiliity_dialog_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // do something when the Cancel button is clicked
            }});

        myAlertDialog.show();

    }

    private void dumpDb() {
        new DbDumpTask(BaseActivity.this).execute(0);
    }


}
