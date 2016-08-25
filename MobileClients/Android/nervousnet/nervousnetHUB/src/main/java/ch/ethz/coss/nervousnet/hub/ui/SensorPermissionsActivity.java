package ch.ethz.coss.nervousnet.hub.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.AxonsArrayAdapter;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NervousnetAxon;

public class SensorPermissionsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_permissions);

        ListView listViewAxons = (ListView) findViewById(R.id.lst_axonsPermissions);
        final NervousnetAxon[] axonArray = getAxonList();
        AxonsArrayAdapter adapter = new AxonsArrayAdapter(this, R.layout.axon_list_item, axonArray);
        listViewAxons.setAdapter(adapter);
        listViewAxons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SensorPermissionsActivity.this, AxonSensorsActivity.class);
                intent.putExtra("AxonName", axonArray[position].axonName);

                startNextActivity(intent);
            }
        });
    }

    private NervousnetAxon[] getAxonList() {
        //// TODO: 02/08/2016 Get real axon list
        NervousnetAxon data[] = new NervousnetAxon[]
                {
                        new NervousnetAxon("Axon 1", R.drawable.ic_analytics),
                        new NervousnetAxon("Axon 2", R.drawable.ic_apps),
                        new NervousnetAxon("Axon 3", R.drawable.ic_analytics),
                };

        return data;
    }
}
