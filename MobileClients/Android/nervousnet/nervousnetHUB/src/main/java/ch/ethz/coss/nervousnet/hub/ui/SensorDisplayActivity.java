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
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
//import android.util.Log;
import android.view.View;

import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ch.ethz.coss.nervousnet.hub.Constants;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.fragments.AccelFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.BaseFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.BatteryFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.DummyFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.GyroFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.LightFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.LocationFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.NoiseFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.NotificationFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.ProximityFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.TrafficFragment;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

public class SensorDisplayActivity extends BaseActivity implements ActionBarImplementation, NervousnetServiceConnectionListener {
    private static BaseFragment fragment;
    int m_interval = 100; // 100 milliseconds by default, can be changed later
    Handler m_handler = new Handler();
    Runnable m_statusChecker;
    NervousnetServiceController nervousnetServiceController;
    private Boolean bindFlag;
    private SensorDisplayPagerAdapter sapAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_display);

        sapAdapter = new SensorDisplayPagerAdapter(getApplicationContext(), this.getFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sapAdapter);
//        if (savedInstanceState == null) {
            initServiceConnection();
//        }
    }

    private void initServiceConnection() {
        nervousnetServiceController = new NervousnetServiceController(SensorDisplayActivity.this, this);
        nervousnetServiceController.connect();
    }

    protected void updateStatus(SensorReading reading, int index) {

        //BaseFragment fragment = (BaseFragment) sapAdapter.getItem(index);
        BaseFragment fragment = (BaseFragment) sapAdapter.getFragment(index);
        NNLog.d("SensorDisplayActivity", "Inside updateStatus, index =  " + index);

        if (reading != null) {
            if (reading instanceof ErrorReading) {
                fragment.handleError((ErrorReading) reading);

            } else {
                fragment.updateReadings(reading);
            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        nervousnetServiceController.disconnect();

    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        nervousnetServiceController.connect();

    }

    @Override
    public void onBackPressed() {

        nervousnetServiceController.disconnect();
        stopRepeatingTask();
        finish();
    }

    void startRepeatingTask() {

        m_statusChecker = new Runnable() {
            @Override
            public void run() {
                boolean errorFlag;
                NNLog.d("SensorDisplayActivity", "before updating");

                update(); // this function can change value of m_interval.


                m_handler.postDelayed(m_statusChecker, m_interval);
            }
        };

        m_statusChecker.run();
    }

    void stopRepeatingTask() {
        m_handler.removeCallbacks(m_statusChecker);
        m_statusChecker = null;

    }

    protected void update() {

        try {
            int index = viewPager.getCurrentItem();
            NNLog.d("SensorDisplayActivity", "Inside update : index  = " + index);
            boolean errorFlag;
            switch (index) {
                case 0:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_ACCELEROMETER), index);
                    break;
                case 1:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_BATTERY), index);
                    break;
                case 2:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_GYROSCOPE), index);
                    break;
                case 3:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_LOCATION), index);
                    break;
                case 4:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_LIGHT), index);
                    break;
                case 5:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_NOISE), index);
                    break;
                case 6:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_NOTIFICATION), index);
                    break;
                case 7:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_PROXIMITY), index);
                    break;
                case 8:
                    updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_TRAFFIC), index);
                    break;
                default:
                    break;
            }

            viewPager.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnected() {

        startRepeatingTask();

    }

    @Override
    public void onServiceDisconnected() {
        stopRepeatingTask();
    }

    @Override
    public void onServiceConnectionFailed(ErrorReading errorReading) {

    }

    public static class SensorDisplayPagerAdapter extends FragmentStatePagerAdapter {
        Context context;

        public SensorDisplayPagerAdapter(Context context, FragmentManager fm) {
            super(fm);

            this.context = context;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    fragment = new AccelFragment();
                    break;
                case 1:
                    fragment = new BatteryFragment();
                    break;
                case 2:
                    fragment = new GyroFragment();
                    break;
                case 3:
                    fragment = new LocationFragment();
                    break;
                case 4:
                    fragment = new LightFragment();
                    break;
                case 5:
                    fragment = new NoiseFragment();
                    break;
                case 6:
                    fragment = new NotificationFragment();
                    break;
                case 7:
                    fragment = new ProximityFragment();
                    break;
                case 8:
                    fragment = new TrafficFragment();
                    break;
                default:
                    fragment = new DummyFragment();
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return NervousnetVMConstants.sensor_labels.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable;
            ImageSpan span;
            SpannableStringBuilder sb;
            sb = new SpannableStringBuilder("  " + NervousnetVMConstants.sensor_labels[position]);

            drawable = context.getResources().getDrawable(Constants.icon_array_sensors[position]);
            drawable.setBounds(0, 0, 40, 40);
            span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // sb.setSpan(new ClickableSpan() {
            // @Override
            // public void onClick(View widget) {
            // Toast.makeText(context, "Clicked Span",
            // Toast.LENGTH_LONG).show();
            // }
            // }, 0, sb.length(),
            // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

        @SuppressWarnings("unchecked")
        public Fragment getFragment(int position) {
            try {

                Field f = FragmentStatePagerAdapter.class.getDeclaredField("mFragments");
                f.setAccessible(true);

                ArrayList<Fragment> fragments = (ArrayList<Fragment>) f.get(this);
                if (fragments.size() > position) {
                    return fragments.get(position);
                }
                return null;
            } catch (Exception e) {

                throw new RuntimeException(e);
            }
        }
    }


    public void showInfo(View view) {
        String title = "Sensor Frequency:";

        // Includes the updates as well so users know what changed.
        String message = "\n\n- Settings to control the frequency of Sensors." +
                "\nClick on the options to switch off or change the frequency." +
                "\n- Various levels of frequency can be selected" +
                "\n          - HIGH, MEDIUM, LOW or OFF" +
                "\n Please note if the Nervousnet Service is Paused, this control is disabled.";


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
