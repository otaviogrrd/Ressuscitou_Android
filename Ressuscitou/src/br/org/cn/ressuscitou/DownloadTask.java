package br.org.cn.ressuscitou;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("Wakelock")
public class DownloadTask extends AsyncTask<String, Integer, String> {

	private Context context;
	private PowerManager.WakeLock mWakeLock;
	private ProgressBar mProgressBar;
	private ImageButton musicButton;
	private LinearLayout downloader;
	int fileLength;

	public DownloadTask(Context context, ProgressBar mProgressBar, LinearLayout downloader, ImageButton musicButton) {
		this.context = context;
		this.mProgressBar = mProgressBar;
		this.downloader = downloader;
		this.musicButton = musicButton;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// take CPU lock to prevent CPU from going off if the user
		// presses the power button during download
		musicButton.setClickable(false);
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
		mWakeLock.acquire();
		// mProgressDialog.show();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		// if we get here, length is known, now set indeterminate to false
		mProgressBar.setIndeterminate(false);
		mProgressBar.setMax(100);
		mProgressBar.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		mWakeLock.release();
		downloader.setVisibility(View.GONE);
		musicButton.setClickable(true);
		if (result != null || fileLength == 0)
			Toast.makeText(context, context.getString(R.string.downErr), Toast.LENGTH_LONG).show();
		else {
			Toast.makeText(context, context.getString(R.string.downSuc), Toast.LENGTH_SHORT).show();
			musicButton.setImageResource(R.drawable.bttnmusic);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		String nomeCanto = params[0];
		try {
			URL url = new URL("http://www.cn.org.br/app_ressuscitou/" + nomeCanto + ".mp3");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/xml");
			connection.setRequestProperty("Content-Type", "audio/mpeg");
			connection.setRequestMethod("GET");
			connection.connect();
			int response = connection.getResponseCode();

			if (response < 200 || response > 299) {
				url = new URL("http://www.imaculadaconceicaodf.com.br/ressuscitou/mp3/" + nomeCanto + ".mp3");
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(false);
				connection.connect();
				response = connection.getResponseCode();
			}

			if (response > 199 && response < 300) {
				fileLength = connection.getContentLength();
				StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
				long storage = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();

				if (fileLength < storage) {
					// download the file
					InputStream input = connection.getInputStream();
					FileOutputStream output = context.openFileOutput("temp", Context.MODE_PRIVATE);

					byte data[] = new byte[4096];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1) {
						total += count;

						if (fileLength > 0)
							publishProgress((int) (total * 100 / fileLength));

						output.write(data, 0, count);
					}
					input.close();
					output.close();
					connection.disconnect();

					File from = new File(context.getFilesDir(), "temp");
					File to = new File(context.getFilesDir(), nomeCanto + ".mp3");
					from.renameTo(to);
				} else {
					connection.disconnect();
					return context.getString(R.string.spaceErr);
				}
			} else {
				connection.disconnect();
				return context.getString(R.string.conErr);
			}
		} catch (Exception e) {
			return context.getString(R.string.conErr);
		}
		return null;
	}
}