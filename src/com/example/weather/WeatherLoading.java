package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class WeatherLoading extends Activity {

	String[] names = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
			"Костя", "Игорь", "Анна", "Денис", "Андрей" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_weather);
		TextView appName = (TextView) findViewById(R.id.textViewAppName);
		Animation startAnimation = AnimationUtils.loadAnimation(this,
				R.anim.start_animation);
		appName.startAnimation(startAnimation);
		startAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				startActivity(new Intent(WeatherLoading.this,
						MainActivity.class));
				WeatherLoading.this.finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
