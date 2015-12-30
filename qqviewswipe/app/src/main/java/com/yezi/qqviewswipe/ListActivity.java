package com.yezi.qqviewswipe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<String> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        setData();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new adapter());
    }

    private void setData() {
        if (list == null) {
            list = new ArrayList<>();
        }
        for (int i = 0; i < 100; i++) {
            list.add(i + "-->test");
        }
    }

    class adapter extends RecyclerView.Adapter<myHolder> {

        @Override
        public myHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ListActivity.this).inflate(R.layout.list_item, parent, false);
            return new myHolder(view);
        }

        @Override
        public void onBindViewHolder(myHolder holder, int position) {
            holder.bind(list.get(position));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    static class myHolder extends RecyclerView.ViewHolder {
        private TextView mTextView;

        public myHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.main);
        }

        public void bind(String s) {
            mTextView.setText(s);
        }
    }
}
