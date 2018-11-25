package br.org.cn.ressuscitou;

import java.io.File;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityConfiguracoes extends Activity {

	public static final String PREFS_NAME = "ArqConfiguracao";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;

	private AdapterListaAudios adapter = null;
	private ScrollView scrollView;
	private LinearLayout listview;
	private ImageButton backButton;
	private ImageButton backButton2;
	private TextView tomTxt;
	private TextView limparTxt;
	private TextView wifiTxt;
	private TextView extTxt;
	public CheckBox wfOnly;
	public CheckBox estendido;
	private TextView downloadAll;
	private ArrayList<Canto> data = new ArrayList<Canto>();
	private ArrayList<String> multDown = new ArrayList<String>();
	public ProgressBar downBar;
	public LinearLayout downloader;
	public DownloadTaskMult downloadTaskMult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_configuracoes);
		settings = getSharedPreferences(PREFS_NAME, 0);
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		scrollView.smoothScrollTo(0, 0);

		CantosClass cantosClass = ((CantosClass) getApplicationContext());
		data = cantosClass.listCantos;
		buscaCantos();

		downBar = (ProgressBar) findViewById(R.id.downloadBar);
		downloader = (LinearLayout) findViewById(R.id.downloader);
		downloader.setVisibility(View.GONE);

		downloadAll = (TextView) findViewById(R.id.downlAll);
		downloadAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				apaga();
				downloadAll();
			}
		});

		tomTxt = (TextView) findViewById(R.id.tom);
		tomTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				descobrir_tom();
			}
		});

		limparTxt = (TextView) findViewById(R.id.limpar);
		limparTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				limparTransp();
			}
		});

		wfOnly = (CheckBox) findViewById(R.id.wifiOnly);
		if (settings.getBoolean("wfOnly", false))
			wfOnly.setChecked(true);
		wfOnly.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				marca_chckBox(wfOnly, "wfOnly");
			}
		});
		wifiTxt = (TextView) findViewById(R.id.wifiTxt);
		wifiTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				txt_marca_chckBox(wfOnly, "wfOnly");
			}
		});

		estendido = (CheckBox) findViewById(R.id.extend);
		if (settings.getBoolean("estendido", true))
			estendido.setChecked(true);
		estendido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				marca_chckBox(estendido, "estendido");
			}
		});
		extTxt = (TextView) findViewById(R.id.extTxt);
		extTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				txt_marca_chckBox(estendido, "estendido");
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
	}

	public void txt_marca_chckBox(CheckBox chckBox, String str) {
		if (chckBox.isChecked()) {
			chckBox.setChecked(false);
		} else {
			chckBox.setChecked(true);
		}
		marca_chckBox(chckBox, str);
	}

	public void marca_chckBox(CheckBox chckBox, String str) {
		editor = settings.edit();
		if (chckBox.isChecked()) {
			editor.putBoolean(str, true);
		} else {
			editor.putBoolean(str, false);
		}
		editor.commit();
	}

	public void limparTransp() {
		editor = settings.edit();
		editor.clear();
		editor.commit();
		marca_chckBox(wfOnly, "wfOnly");
		marca_chckBox(estendido, "estendido");
		makeToast(getText(R.string.eraseText));
	}

	public void apaga() {
		String path = getFilesDir().getAbsolutePath();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getUrl() != "") {
				File file = new File(path, data.get(i).getHtml() + ".mp3");
				if (file.exists() && file.length() == 0) {
					file.delete();
				}
			}
		}
		listview.removeAllViews();
		buscaCantos();
	}

	@SuppressWarnings("unchecked")
	public void downloadAll() {
		makeToast(getText(R.string.startDownAll));
		multDown.clear();
		String path = getFilesDir().getAbsolutePath();
		int count = 0;
		for (int i = 0; i < data.size(); i++) {
			// if (data.get(i).getUrl() != "") {
			File file = new File(path, data.get(i).getHtml() + ".mp3");
			if (!file.exists()) {
				// multDown.add(data.get(i).getUrl());
				multDown.add(data.get(i).getHtml());
				count++;
			}
			// }
		}
		if (count == 0 )
			makeToast(getText(R.string.todosBaixados));
			
			
		downloadTaskMult = new DownloadTaskMult(this, downBar, downloader);
		downloadTaskMult.execute(multDown);
		makeToast(getText(R.string.startDownAll2).toString() + " " + count + " "
				+ getText(R.string.startDownAll3).toString());
	}

	public void descobrir_tom() {
		Uri uri = Uri.parse("http://neo-transposer.com/");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	public void buscaCantos() {
		ArrayList<Audio> data2 = new ArrayList<Audio>();
		String path = getFilesDir().getAbsolutePath();
		data2.clear();
		for (int i = 0; i < data.size(); i++) {
			File file = new File(path, data.get(i).getHtml() + ".mp3");
			if (file.exists()) {
				String titulo = data.get(i).getTitulo();
				String html = data.get(i).getHtml();

				String value = null;
				long Filesize = file.length() / 1024;
				if (Filesize >= 1024)
					value = Filesize / 1024 + "." + Filesize % 1024 + " Mb";
				else
					value = Filesize + " Kb";
				data2.add(new Audio(titulo, value, html));
			}
		}
		adapter = new AdapterListaAudios(this, data2);

		listview = (LinearLayout) findViewById(R.id.listView1);

		for (int i = 0; i < adapter.getCount(); i++) {
			View view = adapter.getView(i, null, listview);
			listview.addView(view);
		}

		((ImageButton) findViewById(R.id.apagar)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String path = getFilesDir().getAbsolutePath();
				ArrayList<String> list = adapter.getSelected();
				int count = 0;
				for (int i = 0; i < list.size(); i++) {
					File file = new File(path, list.get(i).toString() + ".mp3");
					if (file.exists()) {
						file.delete();
						count++;
					}
				}

				if (count > 0)
					makeToast(count + " " + getText(R.string.deletedAudio).toString());

				listview.removeAllViews();
				buscaCantos();
			}
		});
	}

	public void makeToast(CharSequence charSequence) {
		Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
	}
}