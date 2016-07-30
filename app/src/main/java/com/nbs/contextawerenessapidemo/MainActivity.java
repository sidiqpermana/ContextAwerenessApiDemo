package com.nbs.contextawerenessapidemo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends BaseActivity
    implements AdapterView.OnItemClickListener{
    private ListView lvItem;
    private String[] options = new String[]{
            "Snapshot Api",
            "Fence Api"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItem = (ListView)findViewById(R.id.lv_items);
        lvItem.setOnItemClickListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                options);
        lvItem.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:
                SnapshotApiActivity.start(this);
                break;

            case 1:
                break;
        }
    }
}
