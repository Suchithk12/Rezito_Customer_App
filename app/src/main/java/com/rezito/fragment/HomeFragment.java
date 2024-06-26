package com.rezito.fragment;

import static com.rezito.activity.HomeActivity.homeActivity;
import static com.rezito.activity.HomeActivity.txtNoti;
import static com.rezito.utils.SessionManager.aboutUs;
import static com.rezito.utils.SessionManager.contactUs;
import static com.rezito.utils.SessionManager.currncy;
import static com.rezito.utils.SessionManager.iscart;
import static com.rezito.utils.SessionManager.oMin;
import static com.rezito.utils.SessionManager.privacy;
import static com.rezito.utils.SessionManager.razKey;
import static com.rezito.utils.SessionManager.tax;
import static com.rezito.utils.SessionManager.tremcodition;
import static com.rezito.utils.Utiles.productItems;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.rezito.R;
import com.rezito.activity.HomeActivity;
import com.rezito.activity.ItemDetailsActivity;
import com.rezito.adepter.CategoryAdp;
import com.rezito.adepter.ReletedItemAdp;
import com.rezito.adepter.ReletedItemDaynamicAdp;
import com.rezito.database.DatabaseHelper;
import com.rezito.model.BannerItem;
import com.rezito.model.CatItem;
import com.rezito.model.DynamicData;
import com.rezito.model.Flashsalelist;
import com.rezito.model.Home;
import com.rezito.model.ProductItem;
import com.rezito.model.User;
import com.rezito.retrofit.APIClient;
import com.rezito.retrofit.GetResult;
import com.rezito.utils.AutoScrollViewPager;
import com.rezito.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;

public class HomeFragment extends Fragment implements CategoryAdp.RecyclerTouchListener, ReletedItemAdp.ItemClickListener, GetResult.MyListener, ReletedItemDaynamicAdp.ItemClickListener {
    @BindView(R.id.viewPager)
    AutoScrollViewPager viewPager;

    @BindView(R.id.tabview)
    TabLayout tabview;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.recycler_releted)
    RecyclerView recyclerReleted;
    @BindView(R.id.lvl_selected)
    LinearLayout lvlSelected;
    @BindView(R.id.lvl_flassel)
    LinearLayout lvlFlassel;
    Unbinder unbinder;
    @BindView(R.id.scl_main)
    ScrollView sclMain;
    @BindView(R.id.lvl_mantanasmode)
    LinearLayout lvlMantanasmode;
    private Context mContext;
    CategoryAdp adapter;
    ReletedItemAdp adapterReletedi;
    List<CatItem> categoryList;
    List<BannerItem> bannerDatumList;
    public HomeFragment homeListFragment;
    SessionManager sessionManager;
    User user;
    List<DynamicData> dynamicDataList = new ArrayList<>();
    ReletedItemAdp reletedItemAdp;
    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        bannerDatumList = new ArrayList<>();
        sessionManager = new SessionManager(mContext);
        databaseHelper = new DatabaseHelper(getActivity());

        homeListFragment = this;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(mContext);
        mLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerReleted.setLayoutManager(mLayoutManager1);
        categoryList = new ArrayList<>();
        adapter = new CategoryAdp(mContext, categoryList, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapterReletedi = new ReletedItemAdp(mContext, productItems, this);
        recyclerReleted.setItemAnimator(new DefaultItemAnimator());
        recyclerReleted.setAdapter(adapterReletedi);
        user = sessionManager.getUserDetails();

        getHome();

        return view;
    }

    private void setJoinPlayrList(LinearLayout lnrView, List<DynamicData> dataList) {
        lnrView.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_home_item, null);
            TextView itemTitle = view.findViewById(R.id.itemTitle);
            RecyclerView recyclerViewList = view.findViewById(R.id.recycler_view_list);
            itemTitle.setText(dataList.get(i).getTitle());
            ReletedItemDaynamicAdp itemAdp = new ReletedItemDaynamicAdp(mContext, dataList.get(i).getDynamicItems(), this);
            recyclerViewList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
            recyclerViewList.setAdapter(itemAdp);
            lnrView.addView(view);
        }
    }

    public void setFlaseList(LinearLayout lnrView, List<Flashsalelist> dataList) {

        lnrView.removeAllViews();
        for (int i = 0; i < dataList.size(); i++) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_home_flasitem, null);
            TextView itemTitle = view.findViewById(R.id.itemTitle);
            TextView day = view.findViewById(R.id.txt_day);
            TextView hours = view.findViewById(R.id.txt_hours);
            TextView mits = view.findViewById(R.id.txt_mits);
            TextView secound = view.findViewById(R.id.txt_secound);

            TextView txtViewllproduct = view.findViewById(R.id.txt_viewllproduct);
            RecyclerView recycler_view_list = view.findViewById(R.id.recycler_view_list);
            if (dataList.size() != 0) {
                countDownStart(day, hours, mits, secound, dataList.get(i).getEnddatetime());
                itemTitle.setText(dataList.get(i).getName());
                ReletedItemDaynamicAdp itemAdp = new ReletedItemDaynamicAdp(mContext, dataList.get(i).getProductData(), this);
                recycler_view_list.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                recycler_view_list.setAdapter(itemAdp);
                lnrView.addView(view);
            }

        }
    }

    private Runnable runnable;
    private Handler handler = new Handler();

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    private void countDownStart(TextView d, TextView h, TextView m, TextView s, String EVENT_DATE_TIME) {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(HomeFragment.this.dateFormat);
                    Date event_date = dateFormat.parse(EVENT_DATE_TIME);
                    Date current_date = new Date();
                    if (!current_date.after(event_date)) {
                        long diff = event_date.getTime() - current_date.getTime();
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        //

                        d.setText(String.format("%02d", Days));
                        h.setText(String.format("%02d", Hours));
                        m.setText(String.format("%02d", Minutes));
                        s.setText(String.format("%02d", Seconds));

                    } else {

                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick() {

    }

    @Override
    public void onClickItem(String titel, int position) {
        homeActivity.showMenu();
        Bundle args = new Bundle();
        args.putInt("id", position);
        args.putString("titel", titel);
        Fragment fragment = new SubCategoryFragment();
        fragment.setArguments(args);
        HomeActivity.getInstance().callFragment(fragment);
    }

    @Override
    public void onLongClickItem(View v, int position) {
        Log.e("posiotn", "" + position);
    }

    @Override
    public void onItemClick(ProductItem productItem, int position) {
        mContext.startActivity(new Intent(mContext, ItemDetailsActivity.class).putExtra("MyClass", productItem).putParcelableArrayListExtra("MyList", productItem.getPrice()));
    }

    @OnClick({R.id.txt_viewll, R.id.txt_viewllproduct})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_viewll:
                CategoryFragment fragment = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("arraylist", (Serializable) categoryList);
                fragment.setArguments(bundle);
                HomeActivity.getInstance().callFragment(fragment);
                break;
            case R.id.txt_viewllproduct:
                PopularFragment fragmentp = new PopularFragment();
                HomeActivity.getInstance().callFragment(fragmentp);
                break;
            default:
                break;
        }
    }


    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        List<BannerItem> bannerDatumList;
        LayoutInflater layoutInflater;

        public MyCustomPagerAdapter(Context context, List<BannerItem> bannerDatumList) {
            this.context = context;
            this.bannerDatumList = bannerDatumList;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public int getCount() {
            return bannerDatumList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.item_banner, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            Glide.with(mContext).load(APIClient.baseUrl + "/" + bannerDatumList.get(position).getBimg()).placeholder(R.drawable.empty).into(imageView);
            container.addView(itemView);
            imageView.setOnClickListener(v -> {
                if (!bannerDatumList.get(position).getCid().equalsIgnoreCase("0")) {
                    homeActivity.showMenu();
                    Bundle args = new Bundle();
                    args.putInt("id", Integer.parseInt(bannerDatumList.get(position).getCid()));
                    Fragment fragment = new SubCategoryFragment();
                    fragment.setArguments(args);
                    HomeActivity.getInstance().callFragment(fragment);
                }
            });
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private void getHome() {
        HomeActivity.custPrograssbar.prograssCreate(getActivity());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", user.getId());
            JsonParser jsonParser = new JsonParser();
            Call<JsonObject> call = APIClient.getInterface().getHome((JsonObject) jsonParser.parse(jsonObject.toString()));
            GetResult getResult = new GetResult();
            getResult.setMyListener(this);
            getResult.callForLogin(call, "homepage");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.getInstance().setdata();
        HomeActivity.getInstance().setFrameMargin(60);
        HomeActivity.getInstance().serchviewShow();
        if (user != null)
            HomeActivity.getInstance().titleChange("Hello " + user.getName());

        if (dynamicDataList != null) {
            setJoinPlayrList(lvlSelected, dynamicDataList);
        }
        if (reletedItemAdp != null) {
            reletedItemAdp.notifyDataSetChanged();
        }
        if (iscart) {
            iscart = false;
            CardFragment fragment = new CardFragment();
            HomeActivity.getInstance().callFragment(fragment);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void callback(JsonObject result, String callNo) {
        try {
            if (callNo.equalsIgnoreCase("homepage")) {
                HomeActivity.custPrograssbar.closePrograssBar();
                bannerDatumList = new ArrayList<>();
                categoryList = new ArrayList<>();
                Gson gson = new Gson();
                Home home = gson.fromJson(result.toString(), Home.class);
                if (home.getResultHome().getMainData().getMaintaince() == 1) {
                    sclMain.setVisibility(View.GONE);
                    lvlMantanasmode.setVisibility(View.VISIBLE);
                    databaseHelper.deleteCard();
                    HomeActivity.getInstance().hideActionbar();
                    return;
                }
                setFlaseList(lvlFlassel, home.getResultHome().getFlashsalelist());
                categoryList.addAll(home.getResultHome().getCatItems());
                adapter = new CategoryAdp(mContext, categoryList, this);
                recyclerView.setAdapter(adapter);

                bannerDatumList.addAll(home.getResultHome().getBannerItems());
                MyCustomPagerAdapter myCustomPagerAdapter = new MyCustomPagerAdapter(mContext, bannerDatumList);
                viewPager.setAdapter(myCustomPagerAdapter);
                viewPager.startAutoScroll();
                viewPager.setInterval(3000);
                viewPager.setCycle(true);
                viewPager.setStopScrollWhenTouch(true);
                tabview.setupWithViewPager(viewPager, true);

                reletedItemAdp = new ReletedItemAdp(mContext, home.getResultHome().getProductItems(), this);
                recyclerReleted.setAdapter(reletedItemAdp);
                if (home.getResultHome().getRemainNotification() <= 0) {
                    txtNoti.setVisibility(View.GONE);
                } else {
                    txtNoti.setVisibility(View.VISIBLE);
                    txtNoti.setText("" + home.getResultHome().getRemainNotification());
                }
                sessionManager.setStringData(currncy, home.getResultHome().getMainData().getCurrency());
                sessionManager.setStringData(privacy, home.getResultHome().getMainData().getPrivacyPolicy());
                sessionManager.setStringData(aboutUs, home.getResultHome().getMainData().getAboutUs());
                sessionManager.setStringData(contactUs, home.getResultHome().getMainData().getContactUs());
                sessionManager.setStringData(tremcodition, home.getResultHome().getMainData().getTerms());
                sessionManager.setIntData(oMin, home.getResultHome().getMainData().getoMin());
                sessionManager.setStringData(razKey, home.getResultHome().getMainData().getRazKey());
                sessionManager.setStringData(tax, home.getResultHome().getMainData().getTax());
                HomeActivity.getInstance().setTxtWallet(home.getResultHome().getWallet() + "");
                productItems = home.getResultHome().getProductItems();
                dynamicDataList = home.getResultHome().getDynamicData();
                setJoinPlayrList(lvlSelected, dynamicDataList);
            }

        } catch (Exception e) {
            Log.e("error", "==>" + e.getMessage());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
