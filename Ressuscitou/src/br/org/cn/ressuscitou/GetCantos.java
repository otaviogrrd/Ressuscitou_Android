package br.org.cn.ressuscitou;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.PowerManager;

@SuppressLint("Wakelock")
public class GetCantos extends AsyncTask<String, Integer, String> {

	private Context context;
	private 	CantosClass cantosClass;
	private int cantosVersao;
	private PowerManager.WakeLock mWakeLock;
	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	public GetCantos(Context context, int cantosVersao, CantosClass cantosClass) {
		this.context = context;
		this.cantosVersao = cantosVersao;
		this.cantosClass = cantosClass;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
		mWakeLock.acquire();
	}

	@Override
	protected void onPostExecute(String result) {
		mWakeLock.release();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			URL url = new URL(context.getString(R.string.cantos_versao));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(false);
			connection.connect();

			InputStream in = new BufferedInputStream(connection.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			String resultado = "0";
			while ((line = reader.readLine()) != null) {
				resultado = line;
			}
			connection.disconnect();

			if (cantosVersao < Integer.parseInt(resultado)) {
				url = new URL(context.getString(R.string.cantos_json));
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(false);
				connection.connect();
				in = new BufferedInputStream(connection.getInputStream());

				FileOutputStream output = context.openFileOutput("cantos.json", Context.MODE_PRIVATE);
				byte data[] = new byte[4096];
				int count;
				while ((count = in.read(data)) != -1) {
					output.write(data, 0, count);
				}
				in.close();
				output.close();
				connection.disconnect();

				settings = context.getSharedPreferences(PREFS_NAME, 0);
				editor = settings.edit();
				editor.putInt("cantosVersao", Integer.parseInt(resultado));
				editor.commit();

				try {
					cantosClass.popular(context);
				} catch (Exception e) { // ClassCastException
					// falha no android 7.0 em um motorola Moto G(4) Plus (athene_f) no dia
					// 10/02/2018 - 5 falhas seguidas sem explicacao
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}