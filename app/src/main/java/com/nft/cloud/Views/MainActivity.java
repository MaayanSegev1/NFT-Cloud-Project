package com.nft.cloud.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.franmontiel.localechanger.LocaleChanger;
import com.franmontiel.localechanger.utils.ActivityRecreationHelper;
import com.nft.cloud.Fragments.HomeFragment;
import com.nft.cloud.Fragments.AllChatFragment;
import com.nft.cloud.Fragments.ProfileFragment;
import com.nft.cloud.Fragments.SettingsFragment;
import com.nft.cloud.Fragments.SigninFragment;
import com.nft.cloud.R;
import com.nft.cloud.Utils.CustomTypefaceSpan;
import com.google.android.material.navigation.NavigationView;
import com.nft.cloud.Utils.HelperKeys;
import com.nft.cloud.Utils.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    NavigationView navigationView;
    DrawerLayout drawer;
    ImageView img_menu;
    TextView menuTxt;
    @Override
    protected void attachBaseContext(Context base) {
        base = LocaleChanger.configureBaseContext(base);
        super.attachBaseContext(base);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityRecreationHelper.onResume(this);

    }

    @Override
    protected void onDestroy() {
        ActivityRecreationHelper.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        clickListeners(savedInstanceState);
    }
    public void init() {


        navigationView = findViewById(R.id.nav_drawer);
        menuTxt = findViewById(R.id.menuTxt);
        img_menu = findViewById(R.id.img_menu);
        drawer = findViewById(R.id.nav_view);

    }
    public void clickListeners(Bundle savedInstanceState) {

        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        if (!SessionManager.getStringPref(HelperKeys.USER_ID,getApplicationContext()).equals("")){


            MenuItem item = menu.findItem(R.id.signin);
            item.setTitle(getString(R.string.signout));

        }else {
            MenuItem messages = menu.findItem(R.id.messages);
            MenuItem profile = menu.findItem(R.id.profile);
            MenuItem settings = menu.findItem(R.id.settings);
            messages.setVisible(false);
            profile.setVisible(false);
            settings.setVisible(false);
        }

        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            menuTxt.setText(R.string.home);
            loadFragment(new HomeFragment());
        }
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);

                } else {

                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

//for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

//the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            //Replacing the main content with ContentFragment Which is our Inbox View;

            case R.id.profile:
                menuTxt.setText(R.string.profile);
                loadFragment(new ProfileFragment());
                drawer.closeDrawer(GravityCompat.START);

                break;
            case R.id.home:
                menuTxt.setText(R.string.home);
                loadFragment(new HomeFragment());
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.messages:
                menuTxt.setText(R.string.messages);
                loadFragment(new AllChatFragment());
                drawer.closeDrawer(GravityCompat.START);

                break;
            case R.id.settings:
                menuTxt.setText(R.string.settings);
                loadFragment(new SettingsFragment());
                drawer.closeDrawer(GravityCompat.START);

                break;

            case R.id.signin:

                if (!SessionManager.getStringPref(HelperKeys.USER_ID,getApplicationContext()).equals("")) {

                drawer.closeDrawer(GravityCompat.START);
                SessionManager.clearsession(getApplicationContext());
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                }else{
                    menuTxt.setText(R.string.signin);
                    loadFragment(new SigninFragment());
                    drawer.closeDrawer(GravityCompat.START);
                }



                break;



        }
        if (menuItem.isChecked()) {
            menuItem.setChecked(false);
        } else {
            menuItem.setChecked(true);
        }
        menuItem.setChecked(true);
        return false;
    }

    private void loadFragment(Fragment fragment) {

        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        drawer.closeDrawers();
    }
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = ResourcesCompat.getFont(getApplicationContext(), R.font.poppins_regular);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
    public void onBackPressed() {
        DrawerLayout layout = (DrawerLayout)findViewById(R.id.nav_view);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1){

                finish();
            }else {
                super.onBackPressed();
            }

        }
    }
}