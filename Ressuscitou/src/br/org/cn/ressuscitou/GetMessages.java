package br.org.cn.ressuscitou;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.text.Html;
import android.view.Gravity;
import android.widget.TextView;

@SuppressLint("Wakelock")
public class GetMessages extends AsyncTask<String, Integer, String> {

	private Context context;
	private String incMsg;
	private PowerManager.WakeLock mWakeLock;

	Builder mAlertDialog;
	String resultado;

	public GetMessages(Context context, String incMsg) {
		this.context = context;
		this.incMsg = incMsg;
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
		if (resultado != null) {
			incMsg = incMsg + "\n\n" + "Mensagens:" + "\n" + Html.fromHtml(resultado);
		}
		if (incMsg != null) {		
			mAlertDialog = new AlertDialog.Builder(context);
			TextView msg = new TextView(context);
			msg.setText(incMsg);
			msg.setPadding(10, 20, 10, 20);
			msg.setGravity(Gravity.CENTER);
			mAlertDialog.setView(msg);
			mAlertDialog.show();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			URL url = new URL(context.getString(R.string.msg_url));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(false);
			connection.connect();

			InputStream in = new BufferedInputStream(connection.getInputStream());

			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = reader.readLine()) != null) {
				if (resultado == null)
					resultado = line;
				else
					resultado = resultado + line;
			}

			connection.disconnect();
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
