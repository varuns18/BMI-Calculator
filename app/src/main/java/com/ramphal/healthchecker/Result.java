package com.ramphal.healthchecker;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.transition.platform.MaterialSharedAxis;

import java.util.Locale;

public class Result extends AppCompatActivity {
    TextView answer_in_num, answer_in_text;
    Button about_weight;
    String weightStore, heightStore, ageStore, genderStore, bmi;
    int weightData, ageData;
    float heightData, bmiData, healthyWeight;
    MaterialToolbar materialToolbar2;
    MaterialCardView maintain_weight, lose_weight, gain_weight;
    Button more;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialSharedAxis sharedAxisEnter = new MaterialSharedAxis(MaterialSharedAxis.X,true);
        MaterialSharedAxis sharedAxisExit = new MaterialSharedAxis(MaterialSharedAxis.X,false);

        getWindow().setEnterTransition(sharedAxisEnter);
        getWindow().setExitTransition(sharedAxisExit);


        AdsServices.showAd(Result.this);
        AdsServices.loadInterstitialAd(Result.this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        answer_in_num = findViewById(R.id.answer_in_num);
        answer_in_text = findViewById(R.id.answer_in_text);
        about_weight = findViewById(R.id.about_weight);
        materialToolbar2 = findViewById(R.id.materialToolbar2);
        maintain_weight = findViewById(R.id.maintain_weight);
        lose_weight = findViewById(R.id.lose_weight);
        gain_weight = findViewById(R.id.gain_weight);
        more = findViewById(R.id.more);

        SharedPreferences pref = getSharedPreferences("healthData", MODE_PRIVATE);
        genderStore = pref.getString("gender", "Male");
        heightStore = pref.getString("height", "3.33");
        weightStore = pref.getString("weight", "50");
        ageStore = pref.getString("age", "20");
        SharedPreferences.Editor editor = pref.edit();

        heightData = Float.parseFloat(heightStore);
        weightData = Integer.parseInt(weightStore);
        ageData = Integer.parseInt(ageStore);

        materialToolbar2.setNavigationOnClickListener(v -> onBackPressed());

        bmiData = weightData / ((heightData*0.3048f) * (heightData*0.3048f));
        bmi = String.format(Locale.US,"%.1f", bmiData);
        answer_in_num.setText(bmi);

        healthyWeight = 21*((heightData*0.3048f) * (heightData*0.3048f));
        if (healthyWeight > weightData){
            healthyWeight = healthyWeight - weightData;
        }
        else {
            healthyWeight = weightData - healthyWeight;
        }
        String strHealthyWeight = String.format(Locale.US,"%.1f", healthyWeight);

        if (bmiData < 16.0) {
            answer_in_text.setText(getString(R.string.you_are_very_severely_underweight));
            about_weight.setText("Gain "+strHealthyWeight+"kg to exit very severe underweight category (BMI: "+bmi+").");
        } else if (15.9 < bmiData && bmiData < 17.0) {
            answer_in_text.setText(getString(R.string.you_are_severely_underweight));
            about_weight.setText("Gain "+strHealthyWeight+"kg to exit severe underweight category (BMI: "+bmi+").");
        } else if (16.9 < bmiData && bmiData < 18.5) {
            answer_in_text.setText(getString(R.string.you_are_underweight));
            about_weight.setText("Gain "+strHealthyWeight+"kg to exit underweight category (BMI: "+bmi+").");
        } else if (18.4 < bmiData && bmiData < 25.0) {
            answer_in_text.setText(getString(R.string.you_are_normal));
            about_weight.setText(getString(R.string.healthy_weight));
        } else if (24.9 < bmiData && bmiData < 30.0) {
            answer_in_text.setText(getString(R.string.you_are_overweight));
            about_weight.setText("Lose "+strHealthyWeight+"kg to exit overweight category (BMI: "+bmi+").");
        } else if (29.9 < bmiData && bmiData < 35.0) {
            answer_in_text.setText(getString(R.string.you_are_obese_class_1));
            about_weight.setText("Lose "+strHealthyWeight+"kg to exit obese class I category (BMI: "+bmi+").");
        } else if (34.9 < bmiData && bmiData < 40.0) {
            answer_in_text.setText(getString(R.string.you_are_obese_class_2));
            about_weight.setText("Lose "+strHealthyWeight+"kg to exit obese class II category (BMI: "+bmi+").");
        } else if (39.9 < bmiData) {
            answer_in_text.setText(getString(R.string.you_are_obese_class_3));
            about_weight.setText("Lose "+strHealthyWeight+"kg to exit obese class III category (BMI: "+bmi+").");
        }

        maintain_weight.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fitness-gauge.blogspot.com/2024/06/maintaining-healthy-weight..html"));
            startActivity(browserIntent);
        });
        lose_weight.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fitness-gauge.blogspot.com/2024/06/healthy-ways-to-lose-weight.html"));
            startActivity(browserIntent);
        });
        gain_weight.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fitness-gauge.blogspot.com/2024/05/gaining-weight-healthy-way-guide.html"));
            startActivity(browserIntent);
        });
        more.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fitness-gauge.blogspot.com/"));
            startActivity(browserIntent);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}