package com.yuyuereading.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.ycl.tabview.library.TabView;
import com.ycl.tabview.library.TabViewChild;
import com.yuyuereading.Model.bean.BookInfo;
import com.yuyuereading.Model.bean._User;
import com.yuyuereading.Model.database.OperationBookInfo;
import com.yuyuereading.fragment.HomeFragment;
import com.yuyuereading.fragment.ReadingFragment;
import com.yuyuereading.fragment.SeenFragment;
import com.yuyuereading.fragment.UserFragment;
import com.yuyuereading.fragment.WantFragment;
import com.yuyuereading.utils.BookInfoGetFromDouban;
import com.yuyuereading.utils.HttpUtils;
import com.yuyuereading.utils.SearchFromDouban;
import com.yuyuereading.utils.ShakeListener;
import com.yuyuereading.R;
import com.yuyuereading.View.CircleImageView;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, WantFragment.OnFragmentInteractionListener
        , ReadingFragment.OnFragmentInteractionListener, SeenFragment.OnFragmentInteractionListener {

    private Context mContext = MainActivity.this;
    private long exitTime = 0;
    private Boolean bmob_if_hava_book_info = false;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private View headerLayout;
    private TabView tabView;
    private MaterialSearchView searchView;
    private CircleImageView favicon;
    private TextView nickname;
    private int REQUEST_CODE = 5;
    private ShakeListener mShakeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*沉浸式标题栏*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        initView();
        onClick();
        displayList();
        display();
        initShake();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);

        nickname = headerLayout.findViewById(R.id.nickname);
        _User bmobUser = BmobUser.getCurrentUser(_User.class);
        nickname.setText(bmobUser.getUsername());
        favicon = headerLayout.findViewById(R.id.favicon);
    }

    private void initShake() {
        mShakeListener=new ShakeListener(this);
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListenerCallBack() {
            @Override
            public void onShake() {
                Intent intent = new Intent(mContext, ShakeActivity.class);
                startActivity(intent);
            }
        });
    }

    //显示其他信息
    private void display() {

    }

    //显示列表
    private void displayList() {

    }

    //点击事件
    private void onClick() {
        //对搜索的文字监听
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                HttpUtils.doGetAsy("http://139.196.36.97:8080/sbDemo/Book/search?keyword=" + query, new HttpUtils.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        try {
                            JSONArray jsonArray=JSONArray.parseArray(result);
                            //JSONObject jsonObject1 = JSONObject.parseObject(result);
                            List<BookInfo> bookInfos = SearchFromDouban.parsingBookInfos(jsonArray);
                            Intent intent = new Intent();
                            intent.setClass(mContext, BookListActivity.class);
                            intent.putExtra("type","search");
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("bookInfos", (Serializable)bookInfos);
                            intent.putExtras(bundle);
                            startActivity(intent);
                           // progress.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        //
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
            //Do some magic
            }
        });

        //点击事件
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(mContext,"第"+i+"行",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initView() {
        //执行二维码扫描的初始化操作
        ZXingLibrary.initDisplayOpinion(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        //为底部导航栏添加数据源
        List<TabViewChild> tabViewChildList=new ArrayList<>();
        TabViewChild tabViewChild00=new TabViewChild(R.drawable.home,R.drawable.home,"首页", HomeFragment.newInstance("首页","1"));
        TabViewChild tabViewChild01=new TabViewChild(R.drawable.wanting,R.drawable.wanting,"想读", WantFragment.newInstance("想读","1"));
        TabViewChild tabViewChild02=new TabViewChild(R.drawable.reading,R.drawable.reading,"在读",  ReadingFragment.newInstance("在读","1"));
        TabViewChild tabViewChild03=new TabViewChild(R.drawable.seen,R.drawable.seen,"已读",  SeenFragment.newInstance("已读","1"));
        TabViewChild tabViewChild04=new TabViewChild(R.drawable.mine,R.drawable.mine,"我的", UserFragment.newInstance("我的","1"));
        tabViewChildList.add(tabViewChild00);
        tabViewChildList.add(tabViewChild01);
        tabViewChildList.add(tabViewChild02);
        tabViewChildList.add(tabViewChild03);
        tabViewChildList.add(tabViewChild04);
        tabView = findViewById(R.id.tabView);
        tabView.setTabViewChild(tabViewChildList,getSupportFragmentManager());
        searchView = findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        //Add suggestions
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));

    }

    //返回键监听事件
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }//点击两次返回键退出程序
        else if(System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    //toolbar右面按钮
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    //toolbar右面按钮的事件监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        int menuItemId = item.getItemId();
        if (menuItemId == R.id.action_scan) {
            //如果有权限
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent it = new Intent(mContext, CaptureActivity.class);
                startActivityForResult(it, REQUEST_CODE);
            } else {//没有权限
                int REQUEST_CAMERA_PERMISSION = 0;
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            }

        }
        return true;
    }

    //侧滑菜单中的点击事件
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_statistic) {
            Intent intent=new Intent(mContext,StatisticActivity.class);
            startActivity(intent);
            // Handle the camera action
        }else if (id == R.id.nav_update) {

        }else if (id == R.id.nav_quit) {
            AlertDialog dlg = new AlertDialog.Builder(mContext)
                    .setTitle("退出登录？")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            BmobUser.logOut();
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            //清空源来栈中的Activity，新建栈打开相应的Activity
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_left_in,R.anim.slide_right_out);

                        }
                    })
                    .create();
            dlg.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //处理二维码扫描结果
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    final String isbn = bundle.getString(CodeUtils.RESULT_STRING);
                    final ProgressDialog progress = new ProgressDialog(mContext);
                    progress.setMessage("正在查询...");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();


                    HttpUtils.doGetAsy("https://api.douban.com/v2/book/isbn/" + isbn, new HttpUtils.CallBack() {
                        @Override
                        public void onRequestComplete(String result) {
                            try {
                                //把豆瓣返回的数据解析成BookInfo类
                                final BookInfo bookInfo = BookInfoGetFromDouban.parsingBookInfo(result);
                                Looper.prepare();
                                //定义传给数据库操作类的handler
                                @SuppressLint("HandlerLeak")
                                Handler handler = new Handler(){
                                    @Override
                                    public void handleMessage(Message msg) {
                                        if (msg.what == 0) {
                                            List<BookInfo> list = (List<BookInfo>) msg.obj;
                                            if (list.size() != 0) {
                                                Log.i("bmob", "handler传送成功:" + list.get(0).getObjectId());
                                                bmob_if_hava_book_info = true;
                                                Log.i("bmob", "BookInfo存在状态:" + bmob_if_hava_book_info);
                                                //如果存在的话就更新
                                                OperationBookInfo.updateBookInfo(bookInfo);
                                            } else {
                                                //若数据库中没有这本书的信息，就添加
                                                OperationBookInfo.addBookInfo(bookInfo);
                                            }
                                        }
                                    }
                                };

                                OperationBookInfo.queryBookInfo(bookInfo.getBook_isbn13(),handler);

                                Intent intent = new Intent();
                                intent.setClass(mContext, BookInfoActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("bookInfo", bookInfo);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                progress.dismiss();
                                Looper.loop();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}