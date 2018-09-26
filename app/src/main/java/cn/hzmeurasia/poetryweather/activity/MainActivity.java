package cn.hzmeurasia.poetryweather.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.hzmeurasia.poetryweather.MyApplication;
import cn.hzmeurasia.poetryweather.R;
import cn.hzmeurasia.poetryweather.adapter.CardAdapter;
import cn.hzmeurasia.poetryweather.entity.CardEntity;
import cn.hzmeurasia.poetryweather.entity.SearchCityEntity;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    /**
     * 模拟数据
     */
    private CardEntity[] cardEntities = {new CardEntity("京兆府", "大雨", "16°", R.drawable.bg),
            new CardEntity("应天府", "多云", "26°", R.drawable.bg)};

    private List<CardEntity> cardEntityList = new ArrayList<>();
    private CardAdapter cardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        //注册和风天气
        HeConfig.init("HE1808181021011344","c6a58c3230694b64b78facdebd7720fb");
        HeConfig.switchToFreeServerNode();

        //启用toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //注册EventBus事件
        EventBus.getDefault().register(this);

        //载入左滑提示按钮
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //显示出按钮
            actionBar.setDisplayHomeAsUpEnabled(true);
            //载入按钮图片
            actionBar.setHomeAsUpIndicator(R.drawable.ic_person1);
        }
        //悬浮按钮点击事件
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyApplication.getContext(), SearchCityActivity.class);
                startActivity(intent);
            }
        });
        //初始化Card布局
        initCard();
        RecyclerView recyclerView = findViewById(R.id.rv_main);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        cardAdapter = new CardAdapter(cardEntityList);
        recyclerView.setAdapter(cardAdapter);
    }

    private void initCard() {
        cardEntityList.clear();
        for (int i = 0; i < cardEntities.length; i++) {
            cardEntityList.add(cardEntities[i]);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(SearchCityEntity searchCityEntity) {
        Log.d(TAG, "Event: "+searchCityEntity.getCityCode());
        HeWeather.getWeatherNow(this, searchCityEntity.getCityCode(), new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i(TAG, "onError: ",throwable);
                Toast.makeText(MainActivity.this,"天气获取失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<Now> list) {
                Log.i(TAG, "onSuccess: "+ new Gson().toJson(list));
                for (Now now : list) {
                    cardEntityList.add(new CardEntity(now.getBasic().getLocation(),now.getNow().getCond_txt(),now.getNow().getFl(),R.drawable.bg));
                }
                //刷新Card视图
                cardAdapter.notifyDataSetChanged();

            }
        });
    }
}
