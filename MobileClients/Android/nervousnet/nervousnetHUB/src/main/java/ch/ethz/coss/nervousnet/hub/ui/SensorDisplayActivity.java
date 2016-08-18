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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import java.lang.reflect.Field;
import java.util.ArrayList;

import ch.ethz.coss.nervousnet.hub.Application;
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
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.SensorReading;
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

        initServiceConnection();

    }

    private void initServiceConnection() {
        nervousnetServiceController = new NervousnetServiceController(SensorDisplayActivity.this, this);
        nervousnetServiceController.connect();
    }

    protected boolean updateStatus(SensorReading reading, int index) {

        BaseFragment fragment = (BaseFragment) sapAdapter.getFragment(index);
        NNLog.d("SensorDisplayActivity", "Inside updateStatus, index =  " + index);

        if (reading != null) {
            if (reading instanceof ErrorReading) {
                ErrorReading error = (ErrorReading) reading;

                fragment.handleError((ErrorReading) reading);
                if ((error.getErrorCode() == 103)) {
                    return true;
                }
                return false;
            } else {
                fragment.updateReadings(reading);
                return false;
            }

        }
        return false;
        // else
        // fragment.handleError(new ErrorReading(new String[]{"100", "Reading is
        // null", ""}));
    }

    @Override
    public void onBackPressed() {
        stopRepeatingTask();
        finish();
    }

    void startRepeatingTask() {

        m_statusChecker = new Runnable() {
            @Override
            public void run() {
                boolean errorFlag = false;
                NNLog.d("SensorDisplayActivity", "before updating");
//                if (mService != null) {
                errorFlag = update(); // this function can change value of m_interval.

//					if(errorFlag) {
//						stopRepeatingTask();
//					}
//                } else {
//                    NNLog.d("SensorDisplayActivity", "mService is null");
//
//                    Utils.displayAlert(SensorDisplayActivity.this, "Alert",
//                            "Please switch on the data collection option to access this feature.", "Switch On",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//                                    startStopSensorService(true);
//                                }
//                            }, "Back", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//
//                                    finish();
//                                }
//                            });
//
//
//                }

                m_handler.postDelayed(m_statusChecker, m_interval);
            }
        };

        m_statusChecker.run();
    }

    void stopRepeatingTask() {
        m_handler.removeCallbacks(m_statusChecker);
        m_statusChecker = null;

    }

    protected boolean update() {
        try {
            int index = viewPager.getCurrentItem();
            NNLog.d("SensorDisplayActivity", "Inside update : index  = " + index);
            boolean errorFlag;
            switch (index) {
                case 0:
                    errorFlag = updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_ACCELEROMETER), index);
                    break;
                case 1:
                    errorFlag = updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_BATTERY), index);
                    break;
                case 2:
                    errorFlag = updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_GYROSCOPE), index);
                    break;
                case 3:
                    errorFlag = updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_LOCATION), index);
                    break;
                case 4:
                    errorFlag = updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_LIGHT), index);
                    break;
                case 5:
                    errorFlag = updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_NOISE), index);
                    break;
                case 6:
                    errorFlag = updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_NOTIFICATION), index);
                    break;
                case 7:
                    errorFlag = updateStatus(nervousnetServiceController.getLatestReading(LibConstants.SENSOR_PROXIMITY), index);
                    break;

                case 11:
                    errorFlag = false;
                    break;

                default:
                    return false;
            }

            viewPager.getAdapter().notifyDataSetChanged();
            return errorFlag;
            // }
            // catch (RemoteException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return true;
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

}
