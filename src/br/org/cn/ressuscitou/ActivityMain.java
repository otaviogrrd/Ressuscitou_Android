package br.org.cn.ressuscitou;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import org.jsoup.Jsoup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivityMain extends Activity {

	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private ImageButton optButton;
	private ImageButton infButton;
	private String versao = null;
	Builder mAlertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_main);
		CantosClass cantosClass = ((CantosClass) getApplicationContext());
		cantosClass.popular();
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		if (!settings.getBoolean("TermosIniciaisLidos", false)) {
			mAlertDialog = new AlertDialog.Builder(this);
			mAlertDialog.setCancelable(false);

			TextView msg = new TextView(this);
			msg.setText(this.getString(R.string.terms));
			msg.setPadding(10, 20, 10, 20);
			msg.setGravity(Gravity.CENTER);
			msg.setTextSize(20);
			mAlertDialog.setView(msg);

			mAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					editor.putBoolean("TermosIniciaisLidos", true);
					editor.commit();
				}
			});
			mAlertDialog.show();
		}
		if (settings.getInt("remindDate", 0) <= new Assist().returnDate()) {
			try {
				versao = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			new AppVersion().execute(versao);
		}

		optButton = (ImageButton) findViewById(R.id.option);
		optButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				configuracoes();
			}
		});

		infButton = (ImageButton) findViewById(R.id.info);
		infButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				info();
			}
		});
	}

	private class Assist {
		public int returnDate() {
			Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			String data = "" + currentYear;

			if (currentMonth > 9)
				data = data + currentMonth;
			else
				data = data + "0" + currentMonth;

			if (currentDay > 9)
				data = data + currentDay;
			else
				data = data + "0" + currentDay;

			return Integer.parseInt(data);

		}
	}

	private class AppVersion extends AsyncTask<String, Void, Void> {
		String appVersion;
		String curVersion;
		Context context = ActivityMain.this;

		public static final String PREFS_NAME = "ArqConfiguracao";
		private SharedPreferences settings;
		private SharedPreferences.Editor editor;

		@Override
		protected Void doInBackground(String... params) {
			curVersion = params[0];// = versao;
			try {
				appVersion = Jsoup
						.connect(context.getString(R.string.app_url) + "&hl=" + context.getString(R.string.app_lang))
						.get().select("div[itemprop=softwareVersion]").first().ownText();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (appVersion != null) {
				appVersion = appVersion.replaceAll("\\.", "");
				curVersion = curVersion.replaceAll("\\.", "");
				while (curVersion.length() < 5)
					curVersion = curVersion + "0";
				while (appVersion.length() < 5)
					appVersion = appVersion + "0";
				if (Integer.parseInt(appVersion) > Integer.parseInt(curVersion)) {
					mAlertDialog = new AlertDialog.Builder(context);
					mAlertDialog.setCancelable(false);

					mAlertDialog.setMessage(context.getString(R.string.version));
					mAlertDialog.setNegativeButton(context.getString(R.string.updateLater),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									settings = getSharedPreferences(PREFS_NAME, 0);
									editor = settings.edit();
									editor.putInt("remindDate", new Assist().returnDate() + 1);
									editor.commit();
								}
							});
					mAlertDialog.setPositiveButton(context.getString(R.string.updateNow),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(Intent.ACTION_VIEW,
											Uri.parse(context.getString(R.string.app_url) + "&hl="
													+ context.getString(R.string.app_lang)));
									startActivity(intent);
								}
							});
					mAlertDialog.show();
				}
			}
		}

	}

	public void info() {
		mAlertDialog = new AlertDialog.Builder(this);
		TextView msg = new TextView(this);
		msg.setText(this.getString(R.string.app_name) + "\n" + this.getString(R.string.subtitle) + "\n\n" + "Vers�o: "
				+ versao + "\n\n" + this.getString(R.string.terms));
		msg.setPadding(10, 20, 10, 20);
		msg.setGravity(Gravity.CENTER);
		mAlertDialog.setView(msg);
		mAlertDialog.show();
	}

	public void configuracoes() {
		Intent intent = new Intent(this, ActivityConfiguracoes.class);
		startActivity(intent);
	}

	public void buttonIndiceAlf(View v) {
		Intent intent = new Intent(this, ActivityIndiceAlfabetico.class);
		intent.putExtra("categoria", "0");
		startActivity(intent);
	}

	public void buttonIndiceLit(View v) {
		Intent intent = new Intent(this, ActivityIndiceLiturgico.class);
		startActivity(intent);
	}

	public void buttonPrecat(View v) {
		Intent intent = new Intent(this, ActivityIndiceAlfabetico.class);
		intent.putExtra("categoria", "1");
		startActivity(intent);
	}

	public void buttonCat(View v) {
		Intent intent = new Intent(this, ActivityIndiceAlfabetico.class);
		intent.putExtra("categoria", "2");
		startActivity(intent);
	}

	public void buttonElec(View v) {
		Intent intent = new Intent(this, ActivityIndiceAlfabetico.class);
		intent.putExtra("categoria", "3");
		startActivity(intent);
	}

	public void buttonLit(View v) {
		Intent intent = new Intent(this, ActivityIndiceAlfabetico.class);
		intent.putExtra("categoria", "4");
		startActivity(intent);
	}

	public void buttonAcord(View v) {
		Intent intent = new Intent(this, ActivityImageView.class);
		intent.putExtra("imagem", "acordes");
		startActivity(intent);
		this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	public void buttonArpejos(View v) {
		Intent intent = new Intent(this, ActivityImageView.class);
		intent.putExtra("imagem", "arpejos");
		startActivity(intent);
		this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

}
