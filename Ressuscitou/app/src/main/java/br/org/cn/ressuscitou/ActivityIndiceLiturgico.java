package br.org.cn.ressuscitou;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

public class ActivityIndiceLiturgico extends Activity {
	LinkedHashMap<String, List<String>> indice_liturgico;
	List<String> cantos_list;
	ExpandableListView exp_list;
	AdapterIndiceLiturgico adapter;
	static ArrayList<Canto> data;

	private ImageButton backButton;
	private ImageButton backButton2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final Context context = this;
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();
		setContentView(R.layout.activity_indice_liturgico);
		exp_list = (ExpandableListView) findViewById(R.id.expandableListView);

		CantosClass cantosClass = ((CantosClass) getApplicationContext());
		data = cantosClass.listCantos;

		indice_liturgico = getList();
		cantos_list = new ArrayList<String>(indice_liturgico.keySet());
		adapter = new AdapterIndiceLiturgico(this, indice_liturgico, cantos_list);
		exp_list.setAdapter(adapter);

		exp_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			@SuppressLint("DefaultLocale")
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
					long id) {
				String item = (String) adapter.getChild(groupPosition, childPosition);
				String html = null;
				String url = null;
				int cat = 0;
				for (int i = 0; i < data.size(); i++) {
					if (data.get(i).getTitulo() == item) {
						html = data.get(i).getHtml();
						url = data.get(i).getUrl();
						cat = data.get(i).getCategoria();
					}
				}
				Intent intent = new Intent(context, ActivityWebView.class);
				intent.putExtra("html", html);
				intent.putExtra("url", url);
				intent.putExtra("cat", Integer.toString(cat));
				startActivity(intent);
				return true;
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

	public static LinkedHashMap<String, List<String>> getList() {
		LinkedHashMap<String, List<String>> hash_temp = new LinkedHashMap<String, List<String>>();

		List<String> ladve = new ArrayList<String>();
		List<String> llaud = new ArrayList<String>();
		List<String> lnata = new ArrayList<String>();
		List<String> lentr = new ArrayList<String>();
		List<String> lpasc = new ArrayList<String>();
		List<String> lquar = new ArrayList<String>();
		List<String> lpent = new ArrayList<String>();
		List<String> lvirg = new ArrayList<String>();
		List<String> lcria = new ArrayList<String>();
		List<String> lcpaz = new ArrayList<String>();
		List<String> lfpao = new ArrayList<String>();
		List<String> lcomu = new ArrayList<String>();
		List<String> lcfin = new ArrayList<String>();
		String sadve = "ADVENTO";
		String snata = "NATAL";		
		String squar = "QUARESMA";
		String spasc = "PÁSCOA";
		String spent = "PENTECOSTES";
		String svirg = "CANTOS À VIRGEM";
		String scria = "CANTOS PARA AS CRIANÇAS";
		String slaud = "LAUDES - VÉSPERAS";
		String sentr = "CANTOS DE ENTRADA";
		String scpaz = "PAZ - APRESENTAÇÃO DAS OFERENDAS";
		String sfpao = "FRAÇÃO DO PÃO";
		String scomu = "COMUNHÃO";
		String scfin = "CANTO FINAL";

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isAdve())
				ladve.add(data.get(i).getTitulo());
			if (data.get(i).isLaud())
				llaud.add(data.get(i).getTitulo());
			if (data.get(i).isEntr())
				lentr.add(data.get(i).getTitulo());
			if (data.get(i).isNata())
				lnata.add(data.get(i).getTitulo());
			if (data.get(i).isQuar())
				lquar.add(data.get(i).getTitulo());
			if (data.get(i).isPasc())
				lpasc.add(data.get(i).getTitulo());
			if (data.get(i).isPent())
				lpent.add(data.get(i).getTitulo());
			if (data.get(i).isVirg())
				lvirg.add(data.get(i).getTitulo());
			if (data.get(i).isCria())
				lcria.add(data.get(i).getTitulo());
			if (data.get(i).isCpaz())
				lcpaz.add(data.get(i).getTitulo());
			if (data.get(i).isFpao())
				lfpao.add(data.get(i).getTitulo());
			if (data.get(i).isComu())
				lcomu.add(data.get(i).getTitulo());
			if (data.get(i).isCfin())
				lcfin.add(data.get(i).getTitulo());
		}

		hash_temp.put(sadve, ladve);
		hash_temp.put(snata, lnata);
		hash_temp.put(squar, lquar);
		hash_temp.put(spasc, lpasc);
		hash_temp.put(spent, lpent);
		hash_temp.put(svirg, lvirg);
		hash_temp.put(scria, lcria);
		hash_temp.put(slaud, llaud);
		hash_temp.put(sentr, lentr);
		hash_temp.put(scpaz, lcpaz);
		hash_temp.put(sfpao, lfpao);
		hash_temp.put(scomu, lcomu);
		hash_temp.put(scfin, lcfin);
		return hash_temp;
	}

}