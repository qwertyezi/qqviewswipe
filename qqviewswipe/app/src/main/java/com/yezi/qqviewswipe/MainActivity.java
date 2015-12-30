package com.yezi.qqviewswipe;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    QqViewSwipeLayout mQqViewSwipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQqViewSwipeLayout = (QqViewSwipeLayout) findViewById(R.id.swipe_view);
        mQqViewSwipeLayout.setOnItemViewClickListener(new QqViewSwipeLayout.onItemViewClickListener() {
            @Override
            public void onItemViewClick(View view) {
                switch (view.getId()) {
                    case R.id.main:
                        MainActivity.this.startActivity(new Intent(MainActivity.this, ListActivity.class));
                        break;
                    case R.id.menu1:
                        Toast.makeText(MainActivity.this, "menu1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu2:
                        Toast.makeText(MainActivity.this, "menu2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu3:
                        Toast.makeText(MainActivity.this, "menu3", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
