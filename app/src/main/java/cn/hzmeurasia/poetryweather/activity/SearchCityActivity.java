package cn.hzmeurasia.poetryweather.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.zaaach.citypicker.CityPicker;
import com.zaaach.citypicker.adapter.OnPickListener;
import com.zaaach.citypicker.model.City;
import com.zaaach.citypicker.model.HotCity;
import com.zaaach.citypicker.model.LocateState;
import com.zaaach.citypicker.model.LocatedCity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.hzmeurasia.poetryweather.MyApplication;
import cn.hzmeurasia.poetryweather.entity.SearchCityEntity;


/**
 * 类名: SearchCityActivity<br>
 * 功能:(城市搜索)<br>
 * 作者:黄振敏 <br>
 * 日期:2018/9/24 13:54
 */
public class SearchCityActivity extends AppCompatActivity {
    private static final String TAG = "SearchCityActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 添加热门城市
         */
        List<HotCity> hotCities = new ArrayList<>();
        hotCities.add(new HotCity("北京", "北京", "101010100"));
        hotCities.add(new HotCity("上海", "上海", "101020100"));
        hotCities.add(new HotCity("广州", "广东", "101280101"));
        hotCities.add(new HotCity("深圳", "广东", "101280601"));
        hotCities.add(new HotCity("杭州", "浙江", "101210101"));

        CityPicker.getInstance()
                .setFragmentManager(getSupportFragmentManager())
                .enableAnimation(false)//是否启用动画效果
//                .setAnimationStyle(anim)
                .setLocatedCity(new LocatedCity("西安","陕西","10086"))
                .setHotCities(hotCities)
                .setOnPickListener(new OnPickListener() {
                    @Override
                    public void onPick(int position, City data) {
                        Toast.makeText(MyApplication.getContext(),data.getName(),Toast.LENGTH_SHORT).show();
                        //组合cityCode
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("CN").append(data.getCode());

                        //发送EventBus事件
                        EventBus.getDefault().post(new SearchCityEntity(stringBuilder.toString()));
                        finish();
                        Log.d(TAG, "onPick: "+data.getCode());

                    }

                    @Override
                    public void onLocate() {
                        //开始点位
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //更新数据
                                CityPicker.getInstance()
                                        .locateComplete(new LocatedCity("温州","浙江","10010"), LocateState.SUCCESS);
                            }
                        },2000);
                    }
                })
                .show();

    }


}
