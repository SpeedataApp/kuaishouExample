package com.kuaishouexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.kuaishouexample.db.KuaiShouDatas;
import com.kuaishouexample.util.DBUitl;
import com.kuaishouexample.util.ExcelUtil;
import com.kuaishouexample.util.SharedPreferencesUitl;
import com.kuaishouexample.view.CustomToolBar;
import com.speedata.ui.adapter.CommonAdapter;
import com.speedata.ui.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("Registered")
public class DbShowAct extends Activity implements View.OnClickListener, CustomToolBar.BtnClickListener {
    private ListView listView;
    private DBUitl dbUitl;
    private List<KuaiShouDatas> datas = new ArrayList<>();
    private SharedPreferencesUitl preferencesUitl;
    private CustomToolBar customToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_layout);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initView() {
        customToolBar = findViewById(R.id.title_bar_layout);
        customToolBar.setTitleBarListener(this);
        customToolBar.setTvExportVisable(true);
        customToolBar.setSetingBackground(R.drawable.back);
        dbUitl = new DBUitl();
        listView = findViewById(R.id.db_list);
        datas = dbUitl.queryAll();
        Button btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);
        initItem();
        customToolBar.setCount("已保存：" + datas.size());
        preferencesUitl = SharedPreferencesUitl.getInstance(this, "decoeBar");
    }

    public void initItem() {
        CommonAdapter<KuaiShouDatas> adapter = new CommonAdapter<KuaiShouDatas>(this, datas, R.layout.db_list_item) {
            @Override
            public void convert(ViewHolder helper, KuaiShouDatas item) {
                helper.setText(R.id.barcode_item, item.getBarCode());
                helper.setText(R.id.weight_item, item.getWeight());
                helper.setText(R.id.volume_item, item.getVolume());
                helper.setText(R.id.time_item, item.getTimes());
            }
        };
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void exectExcle() {
        Map<String, String> titleMap = new LinkedHashMap<String, String>();
        titleMap.put("barCode", "条码");
        titleMap.put("weight", "重量");
        titleMap.put("volume", "体积");
        titleMap.put("times", "扫描时间");
        ExcelUtil.excelExport(DbShowAct.this, datas, titleMap, "快手扫描数据");

    }

    @Override
    public void onClick(View view) {
        preferencesUitl.removeQueueList("queue");
        dbUitl.deleAll();
        datas = dbUitl.queryAll();
        initItem();
        customToolBar.setCount("已保存：" + 0);

    }

    @Override
    public void exportClick() {
        exectExcle();
    }

    @Override
    public void settingClick() {
        this.finish();
    }
}
