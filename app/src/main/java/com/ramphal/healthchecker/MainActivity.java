package com.ramphal.healthchecker;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.transition.platform.MaterialSharedAxis;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView heightShow, weightShow, ageShow;
    Button weightPlus, weightMinus, agePlus, ageMinus, btnCalculate;
    MaterialToolbar topAppBar;
    Slider sliderHeight;
    MaterialButtonToggleGroup toggleButtonGroup;
    int weight, age;
    String weightData, ageData, heightData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        MaterialSharedAxis sharedAxisExit = new MaterialSharedAxis(MaterialSharedAxis.X,true);
        // Slide right
        MaterialSharedAxis sharedAxisEnter = new MaterialSharedAxis(MaterialSharedAxis.X,false);

        getWindow().setEnterTransition(sharedAxisEnter);
        getWindow().setExitTransition(sharedAxisExit);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Window window = getWindow();
        WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, window.getDecorView());
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.surface));

        heightShow = findViewById(R.id.height_data);
        weightShow = findViewById(R.id.weight_data);
        ageShow = findViewById(R.id.age_data);
        weightPlus = findViewById(R.id.weight_add_button);
        weightMinus = findViewById(R.id.weight_remove_button);
        agePlus = findViewById(R.id.age_add_button);
        ageMinus = findViewById(R.id.age_remove_button);
        btnCalculate = findViewById(R.id.btnCalculate);
        sliderHeight = findViewById(R.id.seekHeight);
        toggleButtonGroup = findViewById(R.id.toggleButton);
        topAppBar = findViewById(R.id.topAppBar);

        Intent nextActivity = new Intent(MainActivity.this, Result.class);
        toggleButtonGroup.setSingleSelection(true);
        AdsServices.loadInterstitialAd(MainActivity.this);

        SharedPreferences pref = getSharedPreferences("healthData", MODE_PRIVATE);
        String genderStore = pref.getString("gender", "Male");
        String heightStore = pref.getString("height", "3.33");
        String weightStore = pref.getString("weight", "50");
        String ageStore = pref.getString("age", "20");
        SharedPreferences.Editor editor = pref.edit();

        topAppBar.setOnMenuItemClickListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.mode_apps) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Varun.S"));
                startActivity(browserIntent);
            } else if (itemId == R.id.about) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.blogger.com/profile/00873435353667994979"));
                startActivity(browserIntent);
            } else if (itemId == R.id.share) {
                shareAppLink();
            }
            return false;
        });



        switch (genderStore) {
            case "Male":
                toggleButtonGroup.check(R.id.button1);
                break;
            case "Female":
                toggleButtonGroup.check(R.id.button2);
                break;
            case "Other":
                toggleButtonGroup.check(R.id.button3);
                break;
        }

        // Set up a listener for the toggle button group
        toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {

            if (checkedId == R.id.button1){
                editor.putString("gender", "Male");
                editor.apply();
            } else if (checkedId == R.id.button2) {
                editor.putString("gender", "Female");
                editor.apply();
            } else if (checkedId == R.id.button3) {
                editor.putString("gender", "Other");
                editor.apply();
            }
        });
        editor.apply();

        heightShow.setText(heightStore);
        sliderHeight.setValue(Float.parseFloat(heightStore));
        sliderHeight.addOnChangeListener((slider, v, b) -> {
            heightData = String.format(Locale.US, "%.2f", v);
            heightShow.setText(heightData);
            editor.putString("height", heightData);
            editor.apply();
        });

        weightShow.setText(weightStore);
        weight = Integer.parseInt(weightStore);
        weightPlus.setOnClickListener(v -> {
            weight++;
            weightData = String.valueOf(weight);
            weightShow.setText(weightData);
            editor.putString("weight", weightData);
            editor.apply();
        });

        weightMinus.setOnClickListener(v -> {
            if (weight > 0) {
                weight--;
                weightData = String.valueOf(weight);
                weightShow.setText(weightData);
            } else {
                Snackbar.make(findViewById(R.id.root_layout), "Weight cannot be negative", Snackbar.LENGTH_SHORT).show();
            }
            editor.putString("weight", weightData);
            editor.apply();
        });


        ageShow.setText(ageStore);
        age = Integer.parseInt(ageStore);
        agePlus.setOnClickListener(v -> {
            age++;
            ageData = String.valueOf(age);
            ageShow.setText(ageData);
            editor.putString("age", ageData);
            editor.apply();
        });

        ageMinus.setOnClickListener(v -> {
            if (age > 0) {
                age--;
                ageData = String.valueOf(age);
                ageShow.setText(ageData);
            } else {
                Snackbar.make(findViewById(R.id.root_layout), "Age cannot be negative", Snackbar.LENGTH_SHORT).show();
            }
            editor.putString("age", ageData);
            editor.apply();
        });


        btnCalculate.setOnClickListener(v -> {

            if (!isOnline()){
                Snackbar.make(findViewById(R.id.root_layout), "No Internet Connection", Snackbar.LENGTH_SHORT).show();
            }else {
                if (toggleButtonGroup.getCheckedButtonId() == View.NO_ID){
                    Snackbar.make(findViewById(R.id.root_layout), "Please select your gender", Snackbar.LENGTH_SHORT).show();
                } else if (heightShow.getText().toString().equals("0.00")) {
                    Snackbar.make(findViewById(R.id.root_layout), "Please enter your height", Snackbar.LENGTH_SHORT).show();
                } else if (ageShow.getText().toString().equals("0")) {
                    Snackbar.make(findViewById(R.id.root_layout), "Please enter your age", Snackbar.LENGTH_SHORT).show();
                } else if (weightShow.getText().toString().equals("0")) {
                    Snackbar.make(findViewById(R.id.root_layout), "Please enter your weight", Snackbar.LENGTH_SHORT).show();
                } else {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this);
                    startActivity(nextActivity, activityOptions.toBundle());
                }
            }

        });

    }

    private void shareAppLink() {
        // Define the Play Store URL for your app
        String appPackageName = getPackageName(); // Your app's package name
        String appLink = "https://play.google.com/store/apps/details?id=" + appPackageName;

        // Create an intent to share the app link
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Health Checker - Calculate BMI, get daily health and fitness tips with Health Checker.");
        String shareMessage = "Track your health effortlessly with Health Checker! Calculate your BMI and get personalized tips for a healthier lifestyle. Download now! " + appLink;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

        // Launch the share intent
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()){
            return false;
        }
        return true;
    }

}