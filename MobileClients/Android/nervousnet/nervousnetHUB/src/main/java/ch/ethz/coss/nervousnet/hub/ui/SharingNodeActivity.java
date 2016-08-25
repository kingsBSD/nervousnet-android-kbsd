package ch.ethz.coss.nervousnet.hub.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NervousnetNode;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NodesArrayAdapter;

public class SharingNodeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_node);

        ListView list = (ListView) findViewById(R.id.lst_Nodes);

        final NervousnetNode[] nodesList = getNodeList();
        NodesArrayAdapter adapter = new NodesArrayAdapter(this, R.layout.node_list_item, nodesList);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vibrate();
                Toast.makeText(SharingNodeActivity.this, "Do you want to remove " + nodesList[position].nodeName + "?", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    private void vibrate() {
        Vibrator v = (Vibrator) SharingNodeActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(50);
    }

    public void clickedAdd(View v) {
        vibrate();
        Toast.makeText(SharingNodeActivity.this, "Opens splash to add a new node", Toast.LENGTH_SHORT).show();
    }

    private NervousnetNode[] getNodeList() {
        //// TODO: 02/08/2016 get real node names
        NervousnetNode[] data = new NervousnetNode[]{
                new NervousnetNode("ETH Main Node"),
                new NervousnetNode("Alice's Node"),
                new NervousnetNode("Switzerland"),
                new NervousnetNode("nervousnet internal"),
        };

        return data;
    }
}
