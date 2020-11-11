package com.zhihuta.xiaota.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.zhihuta.xiaota.R;

public class DxImportHistoryActivity extends AppCompatActivity {

    private RecyclerView mDxHistoryRV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dx_import_history);
    }
}