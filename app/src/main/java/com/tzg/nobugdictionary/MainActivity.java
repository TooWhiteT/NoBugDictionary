package com.tzg.nobugdictionary;

import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.tts.auth.AuthInfo;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.tzg.nobugdictionary.Adapter.ViewPagerAdapter;
import com.tzg.nobugdictionary.Bean.BsBean;
import com.tzg.nobugdictionary.Bean.CyBean;
import com.tzg.nobugdictionary.Bean.PyBean;
import com.tzg.nobugdictionary.Bean.ZiBean;
import com.tzg.nobugdictionary.Events.BsEvent;
import com.tzg.nobugdictionary.Events.CyEvent;
import com.tzg.nobugdictionary.Events.PyEvent;
import com.tzg.nobugdictionary.Events.ZiEvent;
import com.tzg.nobugdictionary.Fragment.BsFragment;
import com.tzg.nobugdictionary.Fragment.CyFragment;
import com.tzg.nobugdictionary.Fragment.PyFragment;
import com.tzg.nobugdictionary.Fragment.ZiFragment;
import com.tzg.nobugdictionary.control.InitConfig;
import com.tzg.nobugdictionary.listener.MessageListener;
import com.tzg.nobugdictionary.listener.UiMessageListener;
import com.tzg.nobugdictionary.util.AutoCheck;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    // ================== 初始化参数设置开始 ==========================
    protected String appId = "16569109";

    protected String appKey = "Pm4x4G88sjoEqzzSxcDlUSQ2";

    protected String secretKey = "Og8bt4Oq0FG9fbgNpR2VyWyRXjg8TKnK";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.ONLINE;

    // ================选择TtsMode.ONLINE  不需要设置以下参数; 选择TtsMode.MIX 需要设置下面2个离线资源文件的路径
    private static final String TEMP_DIR = "/sdcard/baiduTTS"; // 重要！请手动将assets目录下的3个dat 文件复制到该目录

    // 请确保该PATH下有这个文件
    private static final String TEXT_FILENAME = TEMP_DIR + "/" + "bd_etts_text.dat";

    // 请确保该PATH下有这个文件 ，m15是离线男声
    private static final String MODEL_FILENAME =
            TEMP_DIR + "/" + "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    public SpeechSynthesizer mSpeechSynthesizer;
    // =========== 以下为UI部分 ==================================================


    public Retrofit retrofit;
    public NetWork netWork;

    private Toolbar mytoolbar;
    private DrawerLayout mDrawerLayout;
    private EditText toolbar_edit;
    private ViewPager mPager;
    private RadioGroup mGroup;
    private RadioButton mZi,mBs,mPy,mCy;
    private NavigationView mNavigationView;

    private FragmentManager fragmentManager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private ZiFragment ziFragment;
    private PyFragment pyFragment;
    private BsFragment bsFragment;
    private CyFragment cyFragment;
    private ViewPagerAdapter viewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTTs();
        addFragmentData();
        init();

        mZi.setChecked(true);

    }

    public void addFragmentData(){
        ziFragment = new ZiFragment();
        pyFragment = new PyFragment();
        bsFragment = new BsFragment();
        cyFragment = new CyFragment();
        fragmentList.add(ziFragment);
        fragmentList.add(bsFragment);
        fragmentList.add(pyFragment);
        fragmentList.add(cyFragment);
    }

    public void init(){
        mytoolbar = (Toolbar)findViewById(R.id.view_toolbar);
        mytoolbar.setTitle("");
        setSupportActionBar(mytoolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.myDrawer);
        mNavigationView = (NavigationView)findViewById(R.id.MyNavigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.user_exit:
                        Log.i("MENUITEM","退出");
                        break;
                    case R.id.user_updata:
                        Log.i("MENUITEM","修改");
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //设置侧滑菜单彩色图标
        mNavigationView.setItemIconTintList(null);


        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.toolbar_home);
        }


        toolbar_edit = (EditText)findViewById(R.id.toolbar_edit);

        mPager = (ViewPager)findViewById(R.id.view_vPager);
        mGroup = (RadioGroup)findViewById(R.id.view_radioGroup);
        mZi = (RadioButton)findViewById(R.id.main_zi);
        mBs = (RadioButton)findViewById(R.id.main_bs);
        mPy = (RadioButton)findViewById(R.id.main_py);
        mCy = (RadioButton)findViewById(R.id.main_cy);

        fragmentManager = this.getSupportFragmentManager();
        viewPagerAdapter = new ViewPagerAdapter(fragmentManager,fragmentList);
        mPager.setAdapter(viewPagerAdapter);
        //设置viewpager默认页
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if(i == 2){
                    switch (mPager.getCurrentItem()){
                        case 0:
                            mZi.setChecked(true);
                            break;
                        case 1:
                            mBs.setChecked(true);
                            break;
                        case 2:
                            mPy.setChecked(true);
                            break;
                        case 3:
                            mCy.setChecked(true);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
        mGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.main_zi:
                        mPager.setCurrentItem(0);
                        break;
                    case R.id.main_bs:
                        mPager.setCurrentItem(1);
                        break;
                    case R.id.main_py:
                        mPager.setCurrentItem(2);
                        break;
                    case R.id.main_cy:
                        mPager.setCurrentItem(3);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(mNavigationView);
                break;
            case R.id.toolbar_ser:
                //搜索
                if(mZi.isChecked()){
                    Log.i("MENUITEM","字搜索");
                    initRetorfit("http://v.juhe.cn/xhzd/");
                    String serStr = toolbar_edit.getText().toString().trim();
                    getZiNetWork(serStr);
                }

                if(mPy.isChecked()){
                    Log.i("MENUITEM","拼音搜索");
                    initRetorfit("http://v.juhe.cn/xhzd/");
                    String serStr = toolbar_edit.getText().toString().trim();
                    getPyNetWork(serStr);
                }
                if(mBs.isChecked()){
                    Log.i("MENUITEM","部首搜索");
                    initRetorfit("http://v.juhe.cn/xhzd/");
                    String serStr = toolbar_edit.getText().toString().trim();
                    getBsNetWork(serStr);
                }
                if(mCy.isChecked()){
                    Log.i("MENUITEM","成语搜索");
                    initRetorfit("http://v.juhe.cn/chengyu/");
                    String serStr = toolbar_edit.getText().toString().trim();
                    getCyNetWork(serStr);
                }
                break;
        }
        return true;
    }

    public void initRetorfit(String url) {
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(initOkhttp())
                .build();
        netWork = retrofit.create(NetWork.class);
    }

    public OkHttpClient initOkhttp() {
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .connectTimeout(5,TimeUnit.SECONDS)
                .build();
    }

    public void getZiNetWork(String serStr){
        netWork.getZi("bf1f9352094074c284aa3a2caad67b73",serStr)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .map(new Func1<ZiBean,List<ZiBean.ResultBean>>() {
                    @Override
                    public List<ZiBean.ResultBean> call(ZiBean ziBean) {
                        return Collections.singletonList(ziBean.getResult());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ZiBean.ResultBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,"查无此字",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<ZiBean.ResultBean> resultBeans) {

                        EventBus.getDefault().postSticky(new ZiEvent(resultBeans));
                    }
                });
    }

    public void getCyNetWork(final String serStr){
        netWork.getCy("6315e706bd9057138b369af2f887b6a7",serStr)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .map(new Func1<CyBean, List<CyBean.ResultBean>>() {
                    @Override
                    public List<CyBean.ResultBean> call(CyBean cyBean) {
                        return Collections.singletonList(cyBean.getResult());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CyBean.ResultBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,"查无此成语",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<CyBean.ResultBean> resultBeans) {
                        EventBus.getDefault().postSticky(new CyEvent(resultBeans,serStr));
                    }
                });
    }

    public void getPyNetWork(final String serStr){
        netWork.getPy("bf1f9352094074c284aa3a2caad67b73",serStr,1,1,1)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .map(new Func1<PyBean, List<PyBean.ResultBean.ListBean>>() {
                    @Override
                    public List<PyBean.ResultBean.ListBean> call(PyBean pyBean) {
                        return pyBean.getResult().getList();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<PyBean.ResultBean.ListBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,"查无此拼音",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<PyBean.ResultBean.ListBean> listBeans) {
                        EventBus.getDefault().postSticky(new PyEvent(listBeans));
                        Bundle bundle = new Bundle();
                        bundle.putString("serStr",serStr);
                        pyFragment.setArguments(bundle);
                    }
                });
    }

    public void getBsNetWork(final String serStr){
        netWork.getBs("bf1f9352094074c284aa3a2caad67b73",serStr,1,1,1)
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .map(new Func1<BsBean, List<BsBean.ResultBean.ListBean>>() {
                    @Override
                    public List<BsBean.ResultBean.ListBean> call(BsBean bsBean) {
                        return bsBean.getResult().getList();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BsBean.ResultBean.ListBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this,"查无此部首",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(List<BsBean.ResultBean.ListBean> listBeans) {
                        EventBus.getDefault().postSticky(new BsEvent(listBeans));
                        Bundle bundle = new Bundle();
                        bundle.putString("serStr",serStr);
                        bsFragment.setArguments(bundle);
                    }
                });
    }

    //语音合成初始化
    private void initTTs() {
        LoggerProxy.printable(true); // 日志打印在logcat中
        boolean isMix = ttsMode.equals(TtsMode.MIX);
        boolean isSuccess;
        if (isMix) {
            // 检查2个离线资源是否可读
            isSuccess = checkOfflineResources();
            if (!isSuccess) {
                return;
            } else {
                print("离线资源存在并且可读, 目录：" + TEMP_DIR);
            }
        }
        // 日志更新在UI中，可以换成MessageListener，在logcat中查看日志
        SpeechSynthesizerListener listener = new MessageListener();

        // 1. 获取实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(getApplicationContext());

        // 2. 设置listener
        mSpeechSynthesizer.setSpeechSynthesizerListener(listener);

        // 3. 设置appId，appKey.secretKey
        int result = mSpeechSynthesizer.setAppId(appId);
        checkResult(result, "setAppId");
        result = mSpeechSynthesizer.setApiKey(appKey, secretKey);
        checkResult(result, "setApiKey");

        // 4. 支持离线的话，需要设置离线模型
        if (isMix) {
            // 检查离线授权文件是否下载成功，离线授权文件联网时SDK自动下载管理，有效期3年，3年后的最后一个月自动更新。
            isSuccess = checkAuth();
            if (!isSuccess) {
                return;
            }
            // 文本模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            // 声学模型文件路径 (离线引擎使用)， 注意TEXT_FILENAME必须存在并且可读
            mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        }

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL);

        // x. 额外 ： 自动so文件是否复制正确及上面设置的参数
        Map<String, String> params = new HashMap<>();
        // 复制下上面的 mSpeechSynthesizer.setParam参数
        // 上线时请删除AutoCheck的调用
        if (isMix) {
            params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, TEXT_FILENAME);
            params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, MODEL_FILENAME);
        }
        InitConfig initConfig =  new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
            @Override
            /**
             * 开新线程检查，成功后回调
             */
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainDebugMessage();
                        print(message); // 可以用下面一行替代，在logcat中查看代码
                        // Log.w("AutoCheckMessage", message);
                    }
                }
            }

        });

        // 6. 初始化
        result = mSpeechSynthesizer.initTts(ttsMode);
        checkResult(result, "initTts");

    }
    /**
     * 检查appId ak sk 是否填写正确，另外检查官网应用内设置的包名是否与运行时的包名一致。本demo的包名定义在build.gradle文件中
     *
     * @return
     */
    private boolean checkAuth() {
        AuthInfo authInfo = mSpeechSynthesizer.auth(ttsMode);
        if (!authInfo.isSuccess()) {
            // 离线授权需要网站上的应用填写包名。本demo的包名是com.baidu.tts.sample，定义在build.gradle中
            String errorMsg = authInfo.getTtsError().getDetailMessage();
            print("【error】鉴权失败 errorMsg=" + errorMsg);
            return false;
        } else {
            print("验证通过，离线正式授权文件存在。");
            return true;
        }
    }

    /**
     * 检查 TEXT_FILENAME, MODEL_FILENAME 这2个文件是否存在，不存在请自行从assets目录里手动复制
     *
     * @return
     */
    private boolean checkOfflineResources() {
        String[] filenames = {TEXT_FILENAME, MODEL_FILENAME};
        for (String path : filenames) {
            File f = new File(path);
            if (!f.canRead()) {
                print("[ERROR] 文件不存在或者不可读取，请从assets目录复制同名文件到：" + path);
                print("[ERROR] 初始化失败！！！");
                return false;
            }
        }
        return true;
    }

    private void print(String message) {
        Log.i("TSSLOG", message);
    }

    @Override
    public void onDestroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer.stop();
            mSpeechSynthesizer.release();
            mSpeechSynthesizer = null;
            print("释放资源成功");
        }
        super.onDestroy();
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
            print("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

}
