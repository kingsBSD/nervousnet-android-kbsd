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
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import ch.ethz.coss.nervousnet.hub.Constants;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.ImageAdapter;
import ch.ethz.coss.nervousnet.vm.NNLog;

/**
 * @author prasad
 */
public class ShowcaseActivity extends BaseActivity {

    private static final String LOG_TAG = ShowcaseActivity.class.getSimpleName();

    public static String parseJSONFile(String res, Context context) throws IOException {
        AssetManager manager = context.getAssets();
        InputStream file = manager.open(res);
        byte[] formArray = new byte[file.available()];
        file.read(formArray);
        file.close();

        return new String(formArray);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase);

        GridView gridview = (GridView) findViewById(R.id.space_grid);

        ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
        try {
            String spaceJson = parseJSONFile("res/raw/space.json", getApplicationContext());
            JSONObject formArray = (new JSONObject(spaceJson)).getJSONObject("apps");
            String app = formArray.getString("app");
            String packageName = formArray.getString("package");
            NNLog.d(LOG_TAG, "App - " + app + ", Package - " + packageName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        gridview.setAdapter(new ImageAdapter(ShowcaseActivity.this, getResources().getStringArray(R.array.main_grid),
                Constants.icons_main_screen));
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            }
        });
    }

}
