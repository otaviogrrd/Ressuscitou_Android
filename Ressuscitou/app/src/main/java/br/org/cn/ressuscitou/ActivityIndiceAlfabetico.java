package br.org.cn.ressuscitou;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ActivityIndiceAlfabetico extends Activity {

	public static final String PREFS_NAME = "ArqConfiguracao";
	String shareBody = "";
	AdapterIndiceAlf adapter2 = new AdapterIndiceAlf();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Context context = this;
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_indice_alfabetico);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		CantosClass cantosClass = ((CantosClass) getApplicationContext());
		ArrayList<Canto> data = new ArrayList<Canto>();
		ArrayList<Canto> data2 = cantosClass.listCantos;
		String categoria = getIntent().getStringExtra("categoria");
		String listaPersonalizada = getIntent().getStringExtra("listaPersonalizada");

		if (listaPersonalizada != null) {
			shareBody = getResources().getString(R.string.compartilhar_msg);
			shareBody = shareBody + "*" + listaPersonalizada + "*";
			int number = 1;

			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			String listaJson = settings.getString(listaPersonalizada, "" );
			Gson gson = new Gson();
			CantoList listCantos = gson.fromJson(listaJson, new TypeToken<CantoList>() {}.getType());
			if (listCantos != null) {
				ArrayList<Integer> cantos = listCantos.getCantos();
				for (int i = 0; i < data2.size(); i++) {
					for (int j = 0; j < cantos.size(); j++) {
						if (data2.get(i).getId() == cantos.get(j)) {
							data.add(data2.get(i));
							shareBody = shareBody+ "\n"+ number + " - " + data2.get(i).getTitulo();
							number++;
						}
					}
				}
			}else{
				Toast.makeText(context, context.getString(R.string.lista_vazia), Toast.LENGTH_LONG).show();
				onBackPressed();
			}

			ImageButton shareBttn = findViewById(R.id.sharebttn);
			shareBttn.setVisibility(View.VISIBLE);
		}else {
			for (int i = 0; i < data2.size(); i++) {
				if (categoria.equals("0")) {
					data.add(data2.get(i));
				} else if (data2.get(i).getCategoria() == Integer.parseInt(categoria)) {
					data.add(data2.get(i));
				}
			}
		}
		ListView listview = findViewById(R.id.listview);
		listview.removeAllViewsInLayout();
		AdapterIndiceAlf adapter = new AdapterIndiceAlf(this, data);
		listview.setAdapter(adapter);
		adapter2 = adapter;

		EditText inputSearch;
		inputSearch = findViewById(R.id.inputSearch);
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// When user changed the Text
				ActivityIndiceAlfabetico.this.adapter2.getFilter().filter(cs.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence cs, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable cs) {
			}
		});

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
				Object item = parent.getItemAtPosition(position);
				Canto canto = (Canto) item;
				Intent intent = new Intent(context, ActivityWebView.class);
				intent.putExtra("html", canto.getHtml());
				intent.putExtra("url", canto.getUrl());
				intent.putExtra("cat", Integer.toString(canto.getCategoria()));
				startActivity(intent);
			}
		});
		ImageButton backButton = findViewById(R.id.voltar);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		ImageButton backButton2 = findViewById(R.id.voltar2);
		backButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	public void share(View view) {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.compartilhar_lista));
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.compartilhar_lista)));
	}
}