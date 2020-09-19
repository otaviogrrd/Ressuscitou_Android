package br.org.cn.ressuscitou;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	private CantosClass cantosClass;
	Builder mAlertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_main);

		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();

		cantosClass = ((CantosClass) getApplicationContext());
		cantosClass.popular();

		editor.putInt("cantosVersaoAssets", 48);
		editor.commit();

		if (haveWifiConnection() || haveDataConnection()) {
			final GetCantos cantosGetter = new GetCantos(getApplicationContext(), cantosClass);
			cantosGetter.execute();
		}

		if (settings.getInt("messageDate", 0) <= returnDate()) {
			final GetMessages messageGetter = new GetMessages( ActivityMain.this, "");
			messageGetter.execute();
			editor.putInt("messageDate", returnDate() + 1);
			editor.commit();
		}

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

		optButton = findViewById(R.id.option);
		optButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				configuracoes();
			}
		});

		infButton = findViewById(R.id.info);
		infButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				info();
			}
		});
	}

	@SuppressWarnings("deprecation")
	public boolean haveWifiConnection() {
		boolean haveConnectedWifi = false;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
		}
		return haveConnectedWifi;
	}

	@SuppressWarnings("deprecation")
	public boolean haveDataConnection() {
		boolean haveConnectedMobile = false;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedMobile;
	}

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

	public void info() {
		try {
			String versao = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			int versaoLista = settings.getInt("cantosVersaoAssets", 1);
			int versaoDown = settings.getInt("cantosVersaoDown", 0);
			if (versaoDown > versaoLista ) {
				versaoLista = versaoDown;
			}
			String msg = "";
			msg = this.getString(R.string.app_name) + "\n" // nome
					+ this.getString(R.string.subtitle) + "\n\n" // subtitulo
					+ this.getString(R.string.versao) + versao + "  (" + versaoLista + ")\n\n" // versao
					+ this.getString(R.string.terms) + "\n\n"; // termos

			final GetMessages messageGetter = new GetMessages(this, msg);
			messageGetter.execute();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

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

	public void buttonListas(View v) {
		Intent intent = new Intent(this, ActivityListaPersonal.class);
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
