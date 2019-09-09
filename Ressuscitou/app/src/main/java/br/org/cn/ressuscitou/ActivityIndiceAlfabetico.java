package br.org.cn.ressuscitou;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ActivityIndiceAlfabetico extends Activity {

	AdapterIndiceAlf adapter2 = new AdapterIndiceAlf();

	private ImageButton backButton;
	private ImageButton backButton2;

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
		String categoria = getIntent().getStringExtra("categoria");

		ArrayList<Canto> data2 = cantosClass.listCantos;
		for (int i = 0; i < data2.size(); i++) {
			if (categoria.equals("0")) {
				data.add(data2.get(i));
			} else if (data2.get(i).getCategoria() == Integer.parseInt(categoria)) {
				data.add(data2.get(i));
			}
		}
		ListView listview = findViewById(R.id.listview);
		listview.removeAllViewsInLayout();// adicionado 26/04 para ver se para o
											// bug da lista crescendo s�
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
		backButton = findViewById(R.id.voltar);
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
		backButton2 = findViewById(R.id.voltar2);
		backButton2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}
}