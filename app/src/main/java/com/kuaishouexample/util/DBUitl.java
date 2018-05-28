package com.kuaishouexample.util;


import com.kuaishouexample.base.MyAplicatin;
import com.kuaishouexample.db.KuaiShouDatas;
import com.kuaishouexample.db.KuaiShouDatasDao;

import java.util.List;

/**
 * ----------Dragon be here!----------/
 * 　　　┏┓　　　┏┓
 * 　　┏┛┻━━━┛┻┓
 * 　　┃　　　　　　　┃
 * 　　┃　　　━　　　┃
 * 　　┃　┳┛　┗┳　┃
 * 　　┃　　　　　　　┃
 * 　　┃　　　┻　　　┃
 * 　　┃　　　　　　　┃
 * 　　┗━┓　　　┏━┛
 * 　　　　┃　　　┃神兽保佑
 * 　　　　┃　　　┃代码无BUG！
 * 　　　　┃　　　┗━━━┓
 * 　　　　┃　　　　　　　┣┓
 * 　　　　┃　　　　　　　┏┛
 * 　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　┃┫┫　┃┫┫
 * 　　　　　┗┻┛　┗┻┛
 * ━━━━━━神兽出没━━━━━━
 *
 * @author :孙天伟 in  2017/9/27   13:32.
 *         联系方式:QQ:420401567
 *         功能描述:  数据库增删改查
 */
public class DBUitl {
    public DBUitl() {
    }

    KuaiShouDatasDao mDao = MyAplicatin.getsInstance().getDaoSession().getKuaiShouDatasDao();

    /**
     * 添加一条数据
     *
     * @param body
     */
    public void insertDtata(KuaiShouDatas body) {
        mDao.insertOrReplace(body);
    }

    public void insertDtatas(KuaiShouDatas body) {
        MyAplicatin.getsInstance().getDaoSession().getKuaiShouDatasDao().insertOrReplace(body);
    }

    /**
     * 查数据
     *
     * @param Num 单号
     * @return
     */
    public boolean queryRunNum(String Num) {
        KuaiShouDatas user = mDao.queryBuilder().where(KuaiShouDatasDao.Properties.BarCode.eq(Num)).build().unique();
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查找所有数据
     *
     * @return
     */
    public List<KuaiShouDatas> queryAll() {
        List<KuaiShouDatas> kuaiShouDatas = mDao.loadAll();
        if (kuaiShouDatas != null && kuaiShouDatas.size() > 0)
            return kuaiShouDatas;
        return kuaiShouDatas;
    }

   public void deleAll(){
        mDao.deleteAll();
   }
}
