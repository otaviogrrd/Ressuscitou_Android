package br.org.cn.ressuscitou;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ActivityWebView extends Activity {

	final Context context = this;
	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	private WebView webView;
	private MediaPlayer mPlayer = new MediaPlayer();
	private ImageButton transButton;
	private ImageButton musicButton;
	private ImageButton backButton;
	private ImageButton backButton2;
	private ImageButton scrollButton;
	private LinearLayout controles;
	private ProgressBar progressBar;
	private ProgressBar downBar;
	private LinearLayout downloader;
	private Thread thread;
	private Handler handler = new Handler();
	private boolean threadRunning = false;
	private int hasTransp = 0;
	private Dialog dialog;
	private String html;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();

		Intent intent = getIntent();
		html = intent.getStringExtra("html");
		iniciaCanto();

		downBar = (ProgressBar) findViewById(R.id.downloadBar);
		downloader = (LinearLayout) findViewById(R.id.downloader);
		downloader.setVisibility(View.GONE);

		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		musicButton = (ImageButton) findViewById(R.id.music);
		controles = (LinearLayout) findViewById(R.id.controlador);
		controles.setVisibility(View.GONE);

		if (getIntent().getStringExtra("url").isEmpty()) {
			musicButton.setVisibility(View.GONE);
		} else {
			criaBotoes();
		}

		String path = getFilesDir().getAbsolutePath();
		File file = new File(path, html + ".mp3");
		if (file.exists()) {
			musicButton.setVisibility(View.VISIBLE);
			criaBotoes();
		}

		musicButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				play();
			}
		});

		backButton = (ImageButton) findViewById(R.id.voltar);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		backButton2 = (ImageButton) findViewById(R.id.voltar2);
		backButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		scrollButton = (ImageButton) findViewById(R.id.scrolld);
		scrollButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				webView.post(new Runnable() {
					@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
					@Override
					public void run() {
						if (webView.canScrollVertically(+1)) {
							webView.scrollBy(0, 1);
							handler.postDelayed(this, 200);
						}
					}
				});
			}
		});

		transButton = (ImageButton) findViewById(R.id.transp);
		transButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				dialog = new Dialog(context, R.style.FullHeightDialog);
				dialog.setContentView(R.layout.custom);
				dialog.show();

				criar_botao(dialog, R.id.b01, 01);
				criar_botao(dialog, R.id.b03, 03);
				criar_botao(dialog, R.id.b05, 05);
				criar_botao(dialog, R.id.b07, 07);
				criar_botao(dialog, R.id.b09, 9);
				criar_botao(dialog, R.id.b11, 11);
				criar_botao(dialog, R.id.b02, 02);
				criar_botao(dialog, R.id.b04, 04);
				criar_botao(dialog, R.id.b06, 06);
				criar_botao(dialog, R.id.b08, 8);
				criar_botao(dialog, R.id.b10, 10);
				criar_botao(dialog, R.id.b12, 12);

				Button dialogButton = (Button) dialog.findViewById(R.id.b13);
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						salvar();
						dialog.dismiss();
					}
				});
				Button dialogButton2 = (Button) dialog.findViewById(R.id.b14);
				dialogButton2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						hasTransp = 0;
						salvar();
						iniciaCanto();
						dialog.dismiss();
					}
				});
			}
		});

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (mPlayer != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (threadRunning) {
								if (mPlayer.isPlaying()) {
									progressBar.setProgress(mPlayer.getCurrentPosition());
								}
							}
						}
					});
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void criar_botao(final Dialog dialog, int id, final int tran) {
		Button dialogButton = (Button) dialog.findViewById(id);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				transpor(tran);
				dialog.dismiss();
			}
		});

	}

	private void iniciaCanto() {
		String path = "file://" + getApplicationContext().getFilesDir().getAbsolutePath();
		if (settings.getBoolean("estendido", true)) {
			path = path + "/EXT_";
		} else {
			path = path + "/";
		}

		montaWeb(path + html + ".HTML");

		int transSalv = settings.getInt("TRANSP_" + html, 0);
		if (transSalv != 0) {
			transpor(transSalv);
		}
	}

	private void salvar() {
		editor.putInt("TRANSP_" + html, hasTransp);
		editor.commit();
	}

	private void play() {
		String path = getFilesDir().getAbsolutePath();
		File file = new File(path, html + ".mp3");
		if (file.exists()) {
			play2();
		} else {

			if (!haveWifiConnection() && !haveDataConnection()) {

				Toast.makeText(this, getString(R.string.noConection), Toast.LENGTH_SHORT).show();
			}

			if (haveWifiConnection()) {
				baixarAudio();
			} else if (!haveWifiConnection() && haveDataConnection()) {
				if (settings.getBoolean("wfOnly", false)) {
					Toast.makeText(this, getString(R.string.noWifi), Toast.LENGTH_SHORT).show();
				} else {
					baixarAudio();
					Toast.makeText(this, getString(R.string.usingData), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void baixarAudio() {
		if (downloader.getVisibility() == View.GONE) {
			downloader.setVisibility(View.VISIBLE);
			final DownloadTask downloadTask = new DownloadTask(this, downBar, downloader, musicButton);
			downloadTask.execute(html);
		}
	}

	private void play2() {
		String path = getFilesDir().getAbsolutePath();
		File file = new File(path, html + ".mp3");
		if (file.exists()) {
			try {
				mPlayer.setDataSource(file.getAbsolutePath());
			} catch (IllegalArgumentException e) {
			} catch (SecurityException e) {
			} catch (IllegalStateException e) {
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				mPlayer.prepare();
			} catch (IllegalStateException e) {
			} catch (IOException e) {
			}
			mPlayer.start();
			progressBar.setMax(mPlayer.getDuration());
			if (!threadRunning) {
				thread.start();
				threadRunning = true;
			}
			controles.setVisibility(View.VISIBLE);
			musicButton.setVisibility(View.GONE);
		}
	}

	public void criaBotoes() {
		String path = getFilesDir().getAbsolutePath();
		File file = new File(path, html + ".mp3");
		if (!file.exists()) {
			musicButton.setImageResource(R.drawable.bttndown);
		}

		((ImageButton) findViewById(R.id.ctrlrwnd))
				.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mPlayer.isPlaying())
							voltar();
					}
				}));
		((ImageButton) findViewById(R.id.ctrlpause)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mPlayer.isPlaying()) {
					pause();
					ImageButton btn = (ImageButton) findViewById(R.id.ctrlpause);
					btn.setImageResource(R.drawable.ctrlplay);
				} else {
					play2();
					ImageButton btn = (ImageButton) findViewById(R.id.ctrlpause);
					btn.setImageResource(R.drawable.ctrlpause);
				}
			}
		});
		((ImageButton) findViewById(R.id.ctrlstop)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mPlayer.isPlaying()) {
					stop();
					ImageButton btn = (ImageButton) findViewById(R.id.ctrlpause);
					btn.setImageResource(R.drawable.ctrlplay);
				}
			}
		});
		((ImageButton) findViewById(R.id.ctrlfwrd))
				.setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
					@Override
					public void onClick(View view) {
						if (mPlayer.isPlaying())
							avancar();
					}
				}));
	}

	private void pause() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.pause();
		}
	}

	private void stop() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			progressBar.setProgress(0);
			mPlayer.stop();
			mPlayer.reset();
		}
	}

	public void voltar() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.seekTo(mPlayer.getCurrentPosition() - 1000);
			progressBar.setProgress(mPlayer.getCurrentPosition() - 1000);
		}
	}

	public void avancar() {
		if (mPlayer != null && mPlayer.isPlaying()) {
			mPlayer.seekTo(mPlayer.getCurrentPosition() + 1000);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mPlayer != null && mPlayer.isPlaying()) {
			threadRunning = false;
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.release();
		}
	}

	public void montaWeb(String link) {
		webView = (WebView) findViewById(R.id.webView1);
		webView.setWebChromeClient(new WebChromeClient());
		webView.getSettings().setAppCacheEnabled(false);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setDisplayZoomControls(false);
		webView.clearCache(true);
		webView.loadUrl(link);
	}

	@SuppressWarnings("deprecation")
	private boolean haveWifiConnection() {
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
	private boolean haveDataConnection() {
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

	public void transpor(int numero) {
		hasTransp = numero;
		String[] escalaTmp = (new String[] { "zerofiller", "@01", "@02", "@03", "@04", "@05", "@06", "@07", "@08",
				"@09", "@10", "@11", "@12" });
		String[] escala = (new String[] { "zerofiller", "Do", "Do#", "Re", "Mib", "Mi", "Fa", "Fa#", "Sol", "Sol#",
				"La", "Sib", "Si", "Do", "Do#", "Re", "Mib", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "Sib", "Si" });

		String receiveString = "";
		StringBuilder stringBuilder = new StringBuilder();
		try {
			String file = html + ".HTML";
			if (settings.getBoolean("estendido", true)) {
				file = "EXT_" + file;
			}
			InputStream in = this.openFileInput(file);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			// Declaracao de variaveis para logica
			int pri = 99;
			// a primeira nota a aparecer no canto (99 = nao existe ainda)
			int dif = 0;

			while ((receiveString = bufferedReader.readLine()) != null) {
				// Loop apenas nas linhas vermelhas
				if (!receiveString.contains("FF0000")) {
					stringBuilder.append(receiveString).append("\n");
					continue;
				}
				// Se for vermelho e possui o <H2> encontramos o titulo, ignore o titulo
				if (receiveString.contains("<H2>")) {
					stringBuilder.append(receiveString).append("\n");
					continue;
				}
				// Para nao baguncar a logica do replace precisamos
				// substituir todas as notas por valores
				// temporarios(@xx)
				// primeiro os especiais(#) depois o resto
				receiveString = receiveString.replace("Do#", escalaTmp[2]).replace("Fa#", escalaTmp[7]).replace("Sol#",
						escalaTmp[9]);
				for (int i = 0; i < escalaTmp.length; i++) {
					receiveString = receiveString.replace(escala[i], escalaTmp[i]);
				}
				// Logica para descobrir a primeira nota:
				if (pri == 99) {
					String x = "@";
					for (int i = 0; i < receiveString.length(); i++) {
						if (receiveString.charAt(i) == x.charAt(0)) {
							pri = Integer.parseInt(receiveString.substring(i + 1, i + 3));
							// dif = quantas casas vai subir ou descer
							dif = Math.abs(numero - pri);
							break;
						}
					}
				}
				if ((pri > numero) && !(dif == 0)) {
					for (int i = 12; i > 0; i--) {
						receiveString = receiveString.replace(escalaTmp[i], escala[i + 12 - dif]);
					}
				}
				if ((pri < numero) && !(dif == 0)) {
					for (int i = 0; i < escalaTmp.length; i++) {
						receiveString = receiveString.replace(escalaTmp[i], escala[i + dif]);
					}
				}
				if (pri - numero == 0) {
					for (int i = 0; i < escalaTmp.length; i++) {
						receiveString = receiveString.replace(escalaTmp[i], escala[i]);
					}
				}

				stringBuilder.append(receiveString).append("\n");
			}

			FileOutputStream fos = openFileOutput("temp.html", Context.MODE_PRIVATE);
			fos.write(stringBuilder.toString().getBytes());
			fos.close();

			String path = getFilesDir().getAbsolutePath();
			montaWeb("file://" + path + "/temp.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}