package com.accordhk.SnapNEat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.accordhk.SnapNEat.fragments.AboutFragment;
import com.accordhk.SnapNEat.fragments.DisclaimerFragment;
import com.accordhk.SnapNEat.fragments.FollowingsFollowersFragment;
import com.accordhk.SnapNEat.fragments.GMapFragment;
import com.accordhk.SnapNEat.fragments.HotSearchListFragment;
import com.accordhk.SnapNEat.fragments.HotSearchMoreListFragment;
import com.accordhk.SnapNEat.fragments.LoginFragment;
import com.accordhk.SnapNEat.fragments.LoginRegisterFragment;
import com.accordhk.SnapNEat.fragments.MainFragment;
import com.accordhk.SnapNEat.fragments.NearbyRestaurantsMapFragment;
import com.accordhk.SnapNEat.fragments.PostSnapFragment;
import com.accordhk.SnapNEat.fragments.ProfileFavouritesFragment;
import com.accordhk.SnapNEat.fragments.ProfileFollowingsFragment;
import com.accordhk.SnapNEat.fragments.ProfileFootprintsFragment;
import com.accordhk.SnapNEat.fragments.ProfileFragment;
import com.accordhk.SnapNEat.fragments.ProfileGalleryFragment;
import com.accordhk.SnapNEat.fragments.RegisterFragment;
import com.accordhk.SnapNEat.fragments.ReportInappropriateFragment;
import com.accordhk.SnapNEat.fragments.ResetPasswordFragment;
import com.accordhk.SnapNEat.fragments.RestaurantDetailsFragment;
import com.accordhk.SnapNEat.fragments.RestaurantListFragment;
import com.accordhk.SnapNEat.fragments.RestaurantsMapFragment;
import com.accordhk.SnapNEat.fragments.SearchResultsByHashTagsFragment;
import com.accordhk.SnapNEat.fragments.SearchResultsByUsersFragment;
import com.accordhk.SnapNEat.fragments.SettingsAccountFragment;
import com.accordhk.SnapNEat.fragments.SettingsChangePasswordFragment;
import com.accordhk.SnapNEat.fragments.SettingsConServicesFragment;
import com.accordhk.SnapNEat.fragments.SettingsFragment;
import com.accordhk.SnapNEat.fragments.SettingsLanguageFragment;
import com.accordhk.SnapNEat.fragments.SnapDetailsFragment;
import com.accordhk.SnapNEat.fragments.StartingFragment;
import com.accordhk.SnapNEat.models.HotSearch;
import com.accordhk.SnapNEat.models.Restaurant;
import com.accordhk.SnapNEat.models.User;
import com.accordhk.SnapNEat.services.ApiWebServices;
import com.accordhk.SnapNEat.services.GCMRegistrationIntentService;
import com.accordhk.SnapNEat.utils.SharedPref;
import com.accordhk.SnapNEat.utils.Utils;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements
        AboutFragment.OnFragmentInteractionListener,
        DisclaimerFragment.OnFragmentInteractionListener,
        FollowingsFollowersFragment.OnFragmentInteractionListener,
        GMapFragment.OnFragmentInteractionListener,
        HotSearchListFragment.OnFragmentInteractionListener,
        HotSearchMoreListFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        LoginRegisterFragment.OnFragmentInteractionListener,
        MainFragment.OnFragmentInteractionListener,
        PostSnapFragment.OnFragmentInteractionListener,
        ProfileFavouritesFragment.OnFragmentInteractionListener,
        ProfileFollowingsFragment.OnFragmentInteractionListener,
        ProfileFootprintsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        ProfileGalleryFragment.OnFragmentInteractionListener,
        RegisterFragment.OnFragmentInteractionListener,
        ReportInappropriateFragment.OnFragmentInteractionListener,
        ResetPasswordFragment.OnFragmentInteractionListener,
        RestaurantDetailsFragment.OnFragmentInteractionListener,
        RestaurantListFragment.OnFragmentInteractionListener,
        RestaurantsMapFragment.OnFragmentInteractionListener,
        SearchResultsByHashTagsFragment.OnFragmentInteractionListener,
        SearchResultsByUsersFragment.OnFragmentInteractionListener,
        SettingsAccountFragment.OnFragmentInteractionListener,
        SettingsChangePasswordFragment.OnFragmentInteractionListener,
        SettingsConServicesFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        SettingsLanguageFragment.OnFragmentInteractionListener,
        SnapDetailsFragment.OnFragmentInteractionListener,
        StartingFragment.OnFragmentInteractionListener,
        NavigationView.OnNavigationItemSelectedListener,
        NearbyRestaurantsMapFragment.OnFragmentInteractionListener {

    Fragment mFragment;
    FragmentTransaction mTransaction;

    String fragmentTag = "SNAPNEATFRAG";

    Locale currentLocale;

    private static String LOGGER_TAG = "MainActivity";

    private static String POSTSNAP_TAG = "POSTSNAP";
    private static String SETTINGSACCOUNT_TAG = "SETTINGSACCOUNT";

    public static final String SHOW_PROFILE_FROM_PUSH = "SHOW_PROFILE_FROM_PUSH";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean isReceiverRegistered;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private boolean showProfileFromPush;

    public ProgressDialog mProgressDialog;

    private NavigationView navigationView;

    private FragmentManager mManager;

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "updated...123", Toast.LENGTH_SHORT).show();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        Intent intent = getIntent();
        String intentAction = intent.getAction();
        Uri intentData = intent.getData();
        Bundle extras = intent.getExtras();

//        Log.d(LOGGER_TAG, "Deeplink data: "+intentData.getPath());
//        Log.d(LOGGER_TAG, "Deeplink action: "+action);
//        Log.d(LOGGER_TAG, "Deeplink data: "+data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        currentLocale = (new Utils(this)).getLocale(new SharedPref(this).getSelectedLanguage());

        // Check if user is currently logged in
        mCurrentUser = new SharedPref(getApplicationContext()).getLoggedInUser();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(LOGGER_TAG, "YOU ARE HERE: " + intent.getAction());
            }
        };

        if (mCurrentUser != null) {
            // Registering BroadcastReceiver
            registerReceiver();
        }

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

//            mManager = getSupportFragmentManager();
//            mManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//                @Override
//                public void onBackStackChanged() {
//                    FragmentManager manager1 = getSupportFragmentManager();
//                    if (manager1 != null) {
//                        int backStackEntryCount = manager1.getBackStackEntryCount();
//                        if (backStackEntryCount == 0) {
//                            finish();
//                        }
//                        Fragment fragment = manager1.getFragments().get(backStackEntryCount - 1);
//                        fragment.onResume();
//                    }
//                }
//            });

            mTransaction = getSupportFragmentManager().beginTransaction();

                if (mCurrentUser == null) { // No logged in user; show login page
                    mFragment = new StartingFragment();
                } else { // User still logged in; show homepage
                    mFragment = new MainFragment();
                }

            updateNavigationDrawerItems();

            if (intentData != null) {
                String intentPath = intentData.toString();

                Log.d(LOGGER_TAG, "intentPath: " + intentPath);

                if (intentPath.contains("sneapp://?snapId=")) {
                    try {
                        int snapId = Integer.parseInt(intentPath.substring(intentPath.lastIndexOf("=") + 1));
                        Log.d("snapId: ", String.valueOf(snapId));

                        mFragment = new SnapDetailsFragment();
                        Bundle args = new Bundle();
                        args.putInt(SnapDetailsFragment.SNAP_ID, snapId);
                        mFragment.setArguments(args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (intentPath.contains("sneapp://?restaurantId=")) {
                    try {
                        int restaurantId = Integer.parseInt(intentPath.substring(intentPath.lastIndexOf("=") + 1));
                        Log.d("restaurantId: ", String.valueOf(restaurantId));

                        mFragment = new RestaurantDetailsFragment();
                        Bundle args = new Bundle();
                        args.putInt(RestaurantDetailsFragment.RESTAURANT_ID, restaurantId);
                        mFragment.setArguments(args);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (extras != null) {
                showProfileFromPush = extras.getBoolean(SHOW_PROFILE_FROM_PUSH);

                Log.d("showProfileFromPush ?: ", "" + (showProfileFromPush == true));

                if (showProfileFromPush == true) { // from push notif
                    // Check if user is currently logged in
//                    User user = new SharedPref(getApplicationContext()).getLoggedInUser();

                    if (mCurrentUser == null) { // No logged in user; show login page
                        mFragment = new StartingFragment();
                    } else { // User still logged in; show homepage

                        Log.d("USER ID: ", mCurrentUser.getId() + "");
                        Log.d("USER nID: ", mCurrentUser.getnId() + "");

                        if (mCurrentUser.getId() < 1) { // No logged in user
                            mFragment = new StartingFragment();
                        } else {
                            Bundle args = new Bundle();
                            args.putInt(ProfileFragment.USER_ID, mCurrentUser.getId());
                            mFragment = new ProfileFragment();
                            mFragment.setArguments(args);
                        }
                    }

                }
            }

            mTransaction.replace(R.id.fragment_container, mFragment);
            mTransaction.commit();

            navigationView.setCheckedItem(R.id.nav_homepage);
        }

        // call this when the language setting is updated from Settings-Language page
//        onConfigurationChanged(config);
    }

    @Override
    public void onBackPressed() {
        Log.d(LOGGER_TAG, "onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null) {
                if (fragment instanceof MainFragment) {
                    if ((((MainFragment) fragment).getmPopupBackStack()).size() > 0) {
                        ((MainFragment) fragment).slideDown(((MainFragment) fragment).getmPopupBackStack().pop());
                    } else
                        super.onBackPressed();
                } else if (fragment instanceof NearbyRestaurantsMapFragment) {
                    if ((((NearbyRestaurantsMapFragment) fragment).getmPopupBackStack()).size() > 0) {
                        ((NearbyRestaurantsMapFragment) fragment).slideDown(((NearbyRestaurantsMapFragment) fragment).getmPopupBackStack().pop());
                    } else
                        super.onBackPressed();
                } else if (fragment instanceof SnapDetailsFragment) {
                    if ((((SnapDetailsFragment) fragment).getmPopupBackStack()).size() > 0) {
                        ((SnapDetailsFragment) fragment).slideDown(((SnapDetailsFragment) fragment).getmPopupBackStack().pop());
                    } else
                        super.onBackPressed();
                } else if (fragment instanceof SettingsAccountFragment) {
                    if ((((SettingsAccountFragment) fragment).getmPopupBackStack()).size() > 0) {
                        ((SettingsAccountFragment) fragment).slideDown(((SettingsAccountFragment) fragment).getmPopupBackStack().pop());
                    } else
                        super.onBackPressed();
                } else if (fragment instanceof RestaurantsMapFragment) {
                    if ((((RestaurantsMapFragment) fragment).getmPopupBackStack()).size() > 0) {
                        ((RestaurantsMapFragment) fragment).slideDown(((RestaurantsMapFragment) fragment).getmPopupBackStack().pop());
                    } else {
                        super.onBackPressed();

                        try {
                            Log.d(LOGGER_TAG, "trying to call onResume");

                            fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                            if (fragment instanceof RestaurantListFragment) { // refresh RestaurantListFragment
                                Log.d(LOGGER_TAG, "instance of RestaurantListFragment");
                                ((RestaurantListFragment) fragment).generateView();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (fragment instanceof RestaurantDetailsFragment) {
                    if ((((RestaurantDetailsFragment) fragment).getmPopupBackStack()).size() > 0) {
                        ((RestaurantDetailsFragment) fragment).slideDown(((RestaurantDetailsFragment) fragment).getmPopupBackStack().pop());
                    } else
                        super.onBackPressed();
                } else if (fragment instanceof ProfileFragment) {
                    if ((((ProfileFragment) fragment).getmPopupBackStack()).size() > 0) {
                        ((ProfileFragment) fragment).slideDown(((ProfileFragment) fragment).getmPopupBackStack().pop());
                    } else {
                        super.onBackPressed();

                        try {
                            Log.d(LOGGER_TAG, "trying to call onResume");

                            fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            if (fragment instanceof ProfileFragment) {
                                Log.d(LOGGER_TAG, "instance of ProfileFragment");
                                ((ProfileFragment) fragment).generateView();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (fragment instanceof PostSnapFragment) {
                    if ((((PostSnapFragment) fragment).getmPopupBackStack()).size() > 0) {
                        ((PostSnapFragment) fragment).slideDown(((PostSnapFragment) fragment).getmPopupBackStack().pop());
                    } else {
                        final Utils mUtils = new Utils(getApplicationContext());
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(mUtils.getStringResource(R.string.leave_page));

                        builder.setPositiveButton(getResources().getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                mUtils.resetSearchSettings();
                                MainActivity.super.onBackPressed();
                            }
                        });

                        builder.setNegativeButton(getResources().getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
//                else if (fragment instanceof SnapDetailsFragment) {
//                    if(!((SnapDetailsFragment) fragment).disableBack)
//                        super.onBackPressed();
//                }
                else if (fragment instanceof RestaurantListFragment) {
                    Log.d("goback: ", "RestaurantListFragment");
                    goBackResto(((RestaurantListFragment) fragment).restaurantFilters);
                } else if (fragment instanceof HotSearchMoreListFragment) {
                    goBackHotSearchMore(((HotSearchMoreListFragment) fragment).searchMoreType, ((HotSearchMoreListFragment) fragment).categoryFilters);
                } else if (fragment instanceof FollowingsFollowersFragment) {
                    super.onBackPressed();

                    try {
                        Log.d(LOGGER_TAG, "trying to call onResume");

                        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                        if (fragment instanceof ProfileFragment) { // refresh ProfileFragment
                            Log.d(LOGGER_TAG, "instance of ProfileFragment");
                            ((ProfileFragment) fragment).generateView();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    // left navigation boor
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager manager = this.getSupportFragmentManager();
        manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mTransaction = manager.beginTransaction();

        int id = item.getItemId();

        if (id == R.id.nav_homepage) {

            mFragment = new MainFragment();
//            User user = new SharedPref(getApplicationContext()).getLoggedInUser();
//            if(user != null)
//                mFragment = new MainFragment();
//            else {
//                Bundle args = new Bundle();
//                args.putBoolean(LoginRegisterFragment.SHOW_LOGIN_FLAG, true);
//                mFragment = new LoginRegisterFragment();
//                mFragment.setArguments(args);
//            }
        } else if (id == R.id.nav_profile_followings) {

            User user = new SharedPref(getApplicationContext()).getLoggedInUser();
            if (user != null) {
                Log.d(LOGGER_TAG, "Login USER ID: " + String.valueOf(user.getId()));

                Bundle args = new Bundle();
                args.putInt(ProfileFragment.USER_ID, user.getId());
                mFragment = new ProfileFragment();
                mFragment.setArguments(args);
            } else {
                Bundle args = new Bundle();
                args.putBoolean(LoginRegisterFragment.SHOW_LOGIN_FLAG, true);
                mFragment = new LoginRegisterFragment();
                mFragment.setArguments(args);
            }

        } else if (id == R.id.nav_settings) {
            mFragment = new SettingsFragment();
        } else if (id == R.id.nav_starting) {
            mFragment = new StartingFragment();
        }

        mTransaction.add(R.id.fragment_container, mFragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the locale has changed
        if (!currentLocale.equals(newConfig.locale)) {
            currentLocale = newConfig.locale;

            this.setContentView(R.layout.activity_main);
            navigationView = (NavigationView) this.findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(MainActivity.this);

            updateNavigationDrawerItems();

            showSettingsFragmentFromLanguageChange();

        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("URI: ", uri.toString());
    }

    @Override
    public void updateNavigationDrawerItems() {
        // Check if user is currently logged in
        User user = new SharedPref(getApplicationContext()).getLoggedInUser();
        if (user != null) {
            navigationView.getMenu().findItem(R.id.nav_starting).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_profile_followings).setVisible(true);

            registerReceiver(); // register gcm here as well
        } else {
            navigationView.getMenu().findItem(R.id.nav_starting).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_profile_followings).setVisible(false);
            unregisterGcmReceiver(); // unregister gcm here as well
        }
        invalidateOptionsMenu();
    }

    @Override
    public void showDisclaimerFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new DisclaimerFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showSettingsAccountFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new SettingsAccountFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showSettingsConnectedServicesFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new SettingsConServicesFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showSettingsFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new SettingsFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
        updateNavigationDrawerItems();
    }

    private void showSettingsFragmentFromLanguageChange() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.fragment_container, new SettingsFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showLanguageFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new SettingsLanguageFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showAboutFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new AboutFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showSettingsChangePasswordFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new SettingsChangePasswordFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void addSnapPost() {

        // get Hot search filters
        final Utils mUtils = new Utils(getApplicationContext());
        ApiWebServices mApi = new ApiWebServices().getInstance(getApplicationContext());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(mUtils.getStringResource(R.string.common_loading));

        try {
            Map<String, String> params = mUtils.getBaseRequestMap();

            mProgressDialog.show();
            mApi.getHotSearchFilters(params, new ApiWebServices.ApiListener() {
                @Override
                public void onResponse(Object object) {
                    mUtils.dismissDialog(mProgressDialog);
                    mTransaction = getSupportFragmentManager().beginTransaction();
                    mTransaction.add(R.id.fragment_container, new PostSnapFragment());
                    mTransaction.addToBackStack(POSTSNAP_TAG);
                    mTransaction.commit();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    mUtils.dismissDialog(mProgressDialog);
                }
            }, false);
        } catch (Exception e) {
            e.printStackTrace();
            mUtils.dismissDialog(mProgressDialog);
        }
    }

    @Override
    public void showUserFollowings(int userId) {
        mTransaction = getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putInt(FollowingsFollowersFragment.USER_ID, userId);
        args.putInt(FollowingsFollowersFragment.TYPE, FollowingsFollowersFragment.TYPE_FOLLOWING);
        FollowingsFollowersFragment fragment = new FollowingsFollowersFragment();
        fragment.setArguments(args);

        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showUserFollowers(int userId) {
        mTransaction = getSupportFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        args.putInt(FollowingsFollowersFragment.USER_ID, userId);
        args.putInt(FollowingsFollowersFragment.TYPE, FollowingsFollowersFragment.TYPE_FOLLOWER);
        FollowingsFollowersFragment fragment = new FollowingsFollowersFragment();
        fragment.setArguments(args);

        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showHotSearchMore(List<String> defaultSelected, int type, boolean isSingleSelect, boolean isSelectedValueValue) {
        this.showHotSearchMoreShowAll(defaultSelected, type, isSingleSelect, isSelectedValueValue, true);
    }

    @Override
    public void showHotSearchMoreShowAll(List<String> defaultSelected, int type, boolean isSingleSelect, boolean isSelectedValueValue, boolean showAllOption) {
        Bundle args = new Bundle();

        args.putString(HotSearchMoreListFragment.DEFAULT_SELECTED_VALUES, (new Utils(this)).listToString(defaultSelected));
        args.putInt(HotSearchMoreListFragment.SEARCH_MORE_TYPE, type);
        args.putBoolean(HotSearchMoreListFragment.SEARCH_IS_SINGLE_SELECT, isSingleSelect);
        args.putBoolean(HotSearchMoreListFragment.SEARCH_IS_SELECTED_VALUE_VALUE, isSelectedValueValue);
        args.putBoolean(HotSearchMoreListFragment.SHOW_ALL_OPTION, showAllOption);
        HotSearchMoreListFragment fragment = new HotSearchMoreListFragment();
        fragment.setArguments(args);

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showRestaurantList(Restaurant restaurant) {
        Bundle args = new Bundle();
        args.putString(RestaurantListFragment.RESTAURANT, new Gson().toJson(restaurant));
        RestaurantListFragment fragment = new RestaurantListFragment();
        fragment.setArguments(args);

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showHomeFragmentNoBackStack() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.fragment_container, new MainFragment(), fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showHomeFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new MainFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showHomeFragmentFromLogout() {
        doLogoutUser();

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.fragment_container, new MainFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showStartingFragmentFromLogout() {
        doLogoutUser();

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.replace(R.id.fragment_container, new StartingFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showUserProfile(int userId) {
        Bundle args = new Bundle();
        args.putInt(ProfileFragment.USER_ID, userId);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    // Show the LoginRegisterFragment
    @Override
    public void showLoginRegistrationFragment(boolean showLoginFlag) {
        Bundle args = new Bundle();
        args.putBoolean(LoginRegisterFragment.SHOW_LOGIN_FLAG, showLoginFlag);
        LoginRegisterFragment fragment = new LoginRegisterFragment();
        fragment.setArguments(args);

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    // Select which fragment (login/register) to show in LoginRegisterFragment
    @Override
    public void selectLoginRegisterFragment(boolean showLoginFlag) {
        mTransaction = getSupportFragmentManager().beginTransaction();
        if (showLoginFlag) {
            mFragment = new LoginFragment();
        } else {
            mFragment = new RegisterFragment();
        }

        mTransaction.replace(R.id.login_register_frame_content, mFragment);
        mTransaction.commit();
    }

    // Show the reset password fragment in LoginRegisterFragment
    @Override
    public void showResetPasswordFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mFragment = new ResetPasswordFragment();
        mTransaction.replace(R.id.login_register_frame_content, mFragment);
        mTransaction.commit();
    }

    @Override
    public void toggleSideBarNav() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void showHotSearchListFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new HotSearchListFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showGMapFragment(int id, float latitude, float longitude, String title, String name) {
        GMapFragment fragment = new GMapFragment();

        Bundle args = new Bundle();
        args.putFloat(GMapFragment.LATITUDE, latitude);
        args.putFloat(GMapFragment.LONGITUDE, longitude);
        args.putString(GMapFragment.TITLE, title);
        args.putString(GMapFragment.NAME, name);
        args.putInt(GMapFragment.RESTAURANT_ID, id);
        fragment.setArguments(args);

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showSnapDetails(int id) {
        viewSnapDetails(id, false);
    }

    private void viewSnapDetails(int id, boolean disableBackFlag) {
        SnapDetailsFragment fragment = new SnapDetailsFragment();

        Bundle args = new Bundle();
        args.putBoolean(SnapDetailsFragment.DISABLE_BACK, disableBackFlag);
        args.putInt(SnapDetailsFragment.SNAP_ID, id);
        fragment.setArguments(args);

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, fragment);
        mTransaction.addToBackStack(null);
        mTransaction.commit();
    }

    @Override
    public void showRestaurantDetails(int restaurantId) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();

        Bundle args = new Bundle();
        args.putInt(RestaurantDetailsFragment.RESTAURANT_ID, restaurantId);
        fragment.setArguments(args);

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showNearbyRestaurantsMapFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new NearbyRestaurantsMapFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void goBack() {
//        super.onBackPressed();
//
//        try {
//            Log.d(LOGGER_TAG, "trying to call onResume");
//
//            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//            if(fragment instanceof ProfileFragment) { // refresh ProfileFragment
//                Log.d(LOGGER_TAG, "instance of ProfileFragment");
//                ((ProfileFragment) fragment).generateView();
//            } else if (fragment instanceof RestaurantListFragment) { // refresh RestaurantListFragment
//                Log.d(LOGGER_TAG, "instance of RestaurantListFragment");
//                ((RestaurantListFragment) fragment).generateView();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        Toast.makeText(MainActivity.this, "back!", Toast.LENGTH_SHORT).show();
        this.onBackPressed();

    }

    @Override
    public void goBackResto(Restaurant restaurant) {
        super.onBackPressed();
        try {
            Log.d(LOGGER_TAG, "trying to call onResume");

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof PostSnapFragment) {
                Log.d(LOGGER_TAG, "instance of PostSnapFragment");
                ((PostSnapFragment) fragment).customResumeFromBack(HotSearch.Category.DISTRICT.getKey(), restaurant);
            } else if (fragment instanceof RestaurantListFragment) { // refresh RestaurantListFragment
                Log.d(LOGGER_TAG, "instance of RestaurantListFragment");
                ((RestaurantListFragment) fragment).generateView();
            }

//            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//            fragment.onResume();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goBackHotSearchMore(int type, List<String> selectedValues) {
        super.onBackPressed();
        try {
            Log.d(LOGGER_TAG, "trying to call onResume");

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof PostSnapFragment) {
                Log.d(LOGGER_TAG, "instance of PostSnapFragment");
                ((PostSnapFragment) fragment).customResumeFromBack(type, selectedValues);
            } else if (fragment instanceof HotSearchListFragment) {
                Log.d(LOGGER_TAG, "instance of PostSnapFragment");
                ((HotSearchListFragment) fragment).customResumeFromBack(type, selectedValues);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showReportInappropriate(int userId, int snapId) {
        ReportInappropriateFragment fragment = new ReportInappropriateFragment();

        Bundle args = new Bundle();
        args.putInt(ReportInappropriateFragment.USER_ID, userId);
        args.putInt(ReportInappropriateFragment.SNAP_ID, snapId);
        fragment.setArguments(args);

        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showSearchResults(int type, String searchString, HashMap<Integer, List<String>> selectedValuesMap) {
        Bundle args = new Bundle();
        args.putInt(SearchResultsByUsersFragment.SEARCH_TYPE, type);
        args.putString(SearchResultsByUsersFragment.SEARCH_STRING, searchString);

        mTransaction = getSupportFragmentManager().beginTransaction();

        if (type == HotSearch.SearchResultType.USER.getKey()) {
            Log.d(LOGGER_TAG, "showSearchResults by USER");
            SearchResultsByUsersFragment fragment = new SearchResultsByUsersFragment();
            fragment.setArguments(args);
            mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        } else {
            Log.d(LOGGER_TAG, "showSearchResults by HASH");

            args.putSerializable(SearchResultsByHashTagsFragment.SEARCH_CATEGORIES, selectedValuesMap);
            SearchResultsByHashTagsFragment fragment = new SearchResultsByHashTagsFragment();
            fragment.setArguments(args);
            mTransaction.add(R.id.fragment_container, fragment, fragmentTag);
        }

        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    public void showRestaurantsMapFragment() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.fragment_container, new RestaurantsMapFragment(), fragmentTag);
        mTransaction.addToBackStack(fragmentTag);
        mTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = null;

        Log.d(LOGGER_TAG, "requestCode: " + String.valueOf(requestCode) + " resultcode: " + resultCode);

        if (requestCode == MainFragment.REQUEST_CHECK_SETTINGS) {
//            final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
            switch (requestCode) {
                case MainFragment.REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            break;
                        default:
                            break;
                    }
                    break;
            }
        } else {
            fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }

        if (fragment != null) {
            Log.d("FRAGMENT NOT NULL", fragment.toString());
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(GCMRegistrationIntentService.REGISTRATION_COMPLETE));

            isReceiverRegistered = true;
        }

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent gcmIntent = new Intent(this, GCMRegistrationIntentService.class);
            startService(gcmIntent);
        }
    }

    private void unregisterGcmReceiver() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isReceiverRegistered = false;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOGGER_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentUser != null) {
            registerReceiver();
        }
    }

    @Override
    protected void onPause() {
        unregisterGcmReceiver();
        super.onPause();
    }

    private void doLogoutUser() {
        new Utils(this).resetPreference();
        updateNavigationDrawerItems();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        MainFragment.onRequestPermissionsResult()
//    }

}
