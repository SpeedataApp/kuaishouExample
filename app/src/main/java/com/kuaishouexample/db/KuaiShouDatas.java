package com.kuaishouexample.db;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class KuaiShouDatas {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String barCode;
    private String weight;
    private String volume;
    private String times;

    public KuaiShouDatas(String barCode, String weight, String volume, String times) {
        this.barCode = barCode;
        this.weight = weight;
        this.volume = volume;
        this.times = times;
    }

    @Generated(hash = 1997579501)
    public KuaiShouDatas(Long id, String barCode, String weight, String volume,
            String times) {
        this.id = id;
        this.barCode = barCode;
        this.weight = weight;
        this.volume = volume;
        this.times = times;
    }

    @Generated(hash = 78718797)
    public KuaiShouDatas() {
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
