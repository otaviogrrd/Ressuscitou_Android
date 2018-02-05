package br.org.cn.ressuscitou;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
	final static ArrayList<String> links = new ArrayList<String>();
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
		if (result != null)
			Toast.makeText(context, result, Toast.LENGTH_LONG).show();
		else {
		    // mensagem de download concluido
			Toast.makeText(context, context.getString(R.string.downSuc), Toast.LENGTH_SHORT).show();
		    // muda a imagem do botao de download para de reproduzir
			musicButton.setImageResource(R.drawable.bttnmusic);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		String nomeCanto = params[0];
		try {

		    // buscar as urls de hospedagem dos audios
			if (links.size() == 0) {
				URL url = new URL(context.getString(R.string.link_url));
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(false);
				connection.connect();

				InputStream in = new BufferedInputStream(connection.getInputStream());

				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line;
				while ((line = reader.readLine()) != null) {
					links.add(line);
				}

				connection.disconnect();
			}

		    // tentar fazer o downlaod para cada link 
			for (int j = 0; j < links.size(); j++) {

				// confere se o arquivo ja existe no dispositivo
				File audio = new File(context.getFilesDir(), nomeCanto + ".mp3");
				if (audio.exists())
					audio.delete();	//break;

				String link = links.get(j);

				String urlfinal = new String(link + nomeCanto + ".mp3").replaceAll(" ","%20");//monta a url final
				URL url = new URL(urlfinal); 
								
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.connect();
				int response = connection.getResponseCode();

				if (response > 199 && response < 300) {
					int fileLength = connection.getContentLength();
					StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
					long storage = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();

					if (fileLength < storage) {
						// download do arquivo
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
						return null;//download finalizado com sucesso, finaliza a task
					} else {
						connection.disconnect();
						return context.getString(R.string.spaceErr);//download incompleto, falta armazenamento, finaliza a task
					}
				}
				connection.disconnect();
			}
			return context.getString(R.string.conErr); // n?o pegou os links? n?o conectou? n?o baixou em ennhuma tentativa? erro no servidor
		} catch (Exception e) {
			return context.getString(R.string.downErr);
		}
	}
}
