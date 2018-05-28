package com.kuaishouexample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kuaishouexample.R;


public class CustomToolBar extends RelativeLayout implements View.OnClickListener {

    private TextView tvCamera;
    private TextView tvWeight;
    private TextView tvCount;
    private TextView tvExport;
    private ImageView imageSetting;
    private BtnClickListener listener;

    public CustomToolBar(Context context) {
        super(context);
    }

    public CustomToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化组件
     *
     * @param context
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.titlebar, this);
        tvCamera = findViewById(R.id.title_camera_state);
        tvWeight = findViewById(R.id.title_weight_state);
        tvCount = findViewById(R.id.title_save_count);
        tvExport = findViewById(R.id.title_export);
        imageSetting = findViewById(R.id.title_seting);
        tvCamera.setOnClickListener(this);
        tvWeight.setOnClickListener(this);
        tvCount.setOnClickListener(this);
        tvExport.setOnClickListener(this);
        imageSetting.setOnClickListener(this);
    }

    public void setTitleBarListener(BtnClickListener listener) {
        this.listener = listener;
    }

    /**
     * 按钮点击接口
     */
    public interface BtnClickListener {
//        void cameraClick();
//
//        void weightClick();
//
//        void countClick();

        void exportClick();

        void settingClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.title_camera_state:
//                listener.cameraClick();
//                break;
//            case R.id.title_weight_state:
//                listener.weightClick();
//                break;
//            case R.id.title_save_count:
//                listener.countClick();
//                break;
            case R.id.title_export:
                listener.exportClick();
                break;
            case R.id.title_seting:
                listener.settingClick();
                break;
            default:
                break;
        }
    }

    public void setCameraState(String cameraState) {
        tvCamera.setText(cameraState);
    }

    public void setWeightState(String weightState) {
        tvWeight.setText(weightState);
    }

    public void setCount(String count) {
        tvCount.setText(count);
    }

    public void setTvExport(String count) {
        tvCount.setText(count);
    }

    public void setSetingBackground(int drawable) {

        imageSetting.setImageResource(drawable);
    }

    /**
     * 设置中间标题是否可见
     *
     * @param flag 是否可见
     */
    public void setTvExportVisable(boolean flag) {
        if (flag) {
            tvExport.setVisibility(VISIBLE);
        } else {
            tvExport.setVisibility(INVISIBLE);
        }
    }

}