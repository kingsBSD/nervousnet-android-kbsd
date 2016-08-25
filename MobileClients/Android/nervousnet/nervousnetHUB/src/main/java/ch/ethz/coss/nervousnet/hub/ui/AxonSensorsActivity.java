package ch.ethz.coss.nervousnet.hub.ui;

import android.os.Bundle;
import android.widget.Toast;

import ch.ethz.coss.nervousnet.hub.R;

public class AxonSensorsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_axon_sensors);

        String axonName = getIntent().getStringExtra("AxonName");
        Toast.makeText(AxonSensorsActivity.this, axonName, Toast.LENGTH_SHORT).show();

    }
}
