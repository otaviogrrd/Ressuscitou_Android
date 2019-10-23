package br.org.cn.ressuscitou;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ActivityListaPersonal extends Activity {

    public static final String PREFS_NAME = "ArqConfiguracao";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private AdapterListaPersonal adapter = null;
    private LinearLayout listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_lista_personal);
        settings = getSharedPreferences(PREFS_NAME, 0);
        buscaListas();

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

    public void buscaListas() {

        settings = getSharedPreferences(PREFS_NAME, 0);
        String listas = settings.getString("Listas", "" );
        Gson gson = new Gson();
        ArrayList<CantoList> data = gson.fromJson(listas, new TypeToken<ArrayList<CantoList>>() {}.getType());
        ArrayList<CantoList> data2 = new ArrayList<CantoList>();
        data2.clear();

        if (data != null) {
            data2.addAll(data);
            if (adapter != null) {
                adapter.setListData(data2);
                adapter.notifyDataSetChanged();
                listview.removeViews(0,listview.getChildCount());
            } else {
                adapter = new AdapterListaPersonal(this, data2);
            }
            listview = findViewById(R.id.listPersonalizada);
            for (int i = 0; i < adapter.getCount(); i++) {
                View view = adapter.getView(i, null, listview);
                listview.addView(view);
            }
        }
    }

    public void makeToast(CharSequence charSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
    }

    public void criarLista(final View view) {
        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.nome_lista));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String titulo = input.getText().toString();
                if (!titulo.equals("")){
                    settings = getSharedPreferences(PREFS_NAME, 0);
                    String listas = settings.getString("Listas", "");
                    Gson gson = new Gson();
                    ArrayList<CantoList> data = new ArrayList<CantoList>();
                    ArrayList<CantoList> data2 = gson.fromJson(listas, new TypeToken<ArrayList<CantoList>>() {
                    }.getType());

                    if ( data2 != null)
                        data = data2;

                    CantoList lista = new CantoList();
                    lista.setTitulo(titulo);

                    for (int i = 0; i < data.size(); i++) {
                        if(data.get(i).getTitulo().equals(titulo)){
                            Toast.makeText(context, context.getString(R.string.ja_existe), Toast.LENGTH_LONG).show();
                            criarLista(view);
                            return;
                        }
                    }
                    data.add(lista);
                    listas = gson.toJson(data);
                    editor = settings.edit();
                    editor.putString("Listas",listas);
                    editor.apply();
                    buscaListas();
                }
            }
        });

        builder.show();
    }
}