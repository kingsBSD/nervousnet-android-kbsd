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
package ch.ethz.coss.nervousnet.hub.ui.fragments;

import android.app.Fragment;

import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;

public abstract class BaseFragment extends Fragment {

    public int type = 0;

    public BaseFragment() {

    }

    public BaseFragment(int type) {
        // TODO Auto-generated constructor stub

        this.type = type;
    }

    public abstract void updateReadings(SensorReading reading);

    public abstract void handleError(ErrorReading reading);

    @Override
    public void onResume() {
        NNLog.d("BaseFragment", "onResume of BaseFragment");
        super.onResume();
    }

    @Override
    public void onPause() {
        NNLog.d("BaseFragment", "OnPause of BaseFragment");
        super.onPause();
    }

}
