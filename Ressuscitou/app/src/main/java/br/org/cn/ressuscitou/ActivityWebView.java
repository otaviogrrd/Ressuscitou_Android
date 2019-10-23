package br.org.cn.ressuscitou;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ActivityWebView extends Activity {

    final Context context = this;
    public static final String PREFS_NAME = "ArqConfiguracao";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private WebView webView;
    private MediaPlayer mPlayer = new MediaPlayer();
    private ImageButton musicButton;
    private LinearLayout controles;
    private ProgressBar progressBar;
    private ProgressBar downBar;
    private LinearLayout downloader;
    private Thread thread;
    private Handler handler = new Handler();
    private boolean threadRunning = false;
    private boolean menuOptionsVisible = false;
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

        downBar = findViewById(R.id.downloadBar);
        downloader = findViewById(R.id.downloader);
        downloader.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar1);
        musicButton = findViewById(R.id.music);
        controles = findViewById(R.id.controlador);
        controles.setVisibility(View.GONE);

        if (getIntent().getStringExtra("url").isEmpty()) {
            musicButton.setVisibility(View.GONE);
        } else {
            criaBotoes();
        }

        String path = getFilesDir().getAbsolutePath();
        File file = new File(path, html + ".mp3");
        if (file.exists()) {
            //musicButton.setVisibility(View.VISIBLE);
            criaBotoes();
        }

        musicButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                play();
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
        ImageButton scrollButton = findViewById(R.id.scrolld);
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


        ImageButton capotButton = findViewById(R.id.capot);
        capotButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                dialog = new Dialog(context, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.dialog_capotraste);
                dialog.show();

                NumberPicker np = dialog.findViewById(R.id.np);
                np.setMinValue(0);
                np.setMaxValue(21);
                int capotSalv = settings.getInt("CAPOT_" + html, 0);
                np.setValue(capotSalv);
                //Gets whether the selector wheel wraps when reaching the min/max value.
                np.setWrapSelectorWheel(true);
                np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        editor.putInt("CAPOT_" + html, newVal);
                        editor.commit();

                        int transSalv = settings.getInt("TRANSP_" + html, 0);
                        if (hasTransp != 0)
                            transpor(hasTransp);
                        else
                            transpor(transSalv);
                    }
                });
            }
        });
        ImageButton transButton = findViewById(R.id.transp);
        transButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                dialog = new Dialog(context, R.style.FullHeightDialog);
                dialog.setContentView(R.layout.dialog_transposicao);
                dialog.show();

                if (settings.getBoolean("escalaAmericana", false)) {
                    criar_botao(dialog, R.id.b01, "01", context.getString(R.string.a01));
                    criar_botao(dialog, R.id.b03, "03", context.getString(R.string.a03));
                    criar_botao(dialog, R.id.b05, "05", context.getString(R.string.a05));
                    criar_botao(dialog, R.id.b07, "07", context.getString(R.string.a07));
                    criar_botao(dialog, R.id.b09, "09", context.getString(R.string.a09));
                    criar_botao(dialog, R.id.b11, "11", context.getString(R.string.a11));
                    criar_botao(dialog, R.id.b02, "02", context.getString(R.string.a02));
                    criar_botao(dialog, R.id.b04, "04", context.getString(R.string.a04));
                    criar_botao(dialog, R.id.b06, "06", context.getString(R.string.a06));
                    criar_botao(dialog, R.id.b08, "08", context.getString(R.string.a08));
                    criar_botao(dialog, R.id.b10, "10", context.getString(R.string.a10));
                    criar_botao(dialog, R.id.b12, "12", context.getString(R.string.a12));
                } else {
                    criar_botao(dialog, R.id.b01, "01", context.getString(R.string.t01));
                    criar_botao(dialog, R.id.b03, "03", context.getString(R.string.t03));
                    criar_botao(dialog, R.id.b05, "05", context.getString(R.string.t05));
                    criar_botao(dialog, R.id.b07, "07", context.getString(R.string.t07));
                    criar_botao(dialog, R.id.b09, "09", context.getString(R.string.t09));
                    criar_botao(dialog, R.id.b11, "11", context.getString(R.string.t11));
                    criar_botao(dialog, R.id.b02, "02", context.getString(R.string.t02));
                    criar_botao(dialog, R.id.b04, "04", context.getString(R.string.t04));
                    criar_botao(dialog, R.id.b06, "06", context.getString(R.string.t06));
                    criar_botao(dialog, R.id.b08, "08", context.getString(R.string.t08));
                    criar_botao(dialog, R.id.b10, "10", context.getString(R.string.t10));
                    criar_botao(dialog, R.id.b12, "12", context.getString(R.string.t12));
                }

                Button dialogButton = dialog.findViewById(R.id.b13);
                dialogButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        salvar();
                        dialog.dismiss();
                    }
                });
                Button dialogButton2 = dialog.findViewById(R.id.b14);
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

    public void criar_botao(final Dialog dialog, int id, final String tran, String text) {
        Button dialogButton = dialog.findViewById(id);
        dialogButton.setText(text);
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                transpor(Integer.parseInt(tran));
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

        // montaWeb(path + html + ".HTML");

        int transSalv = settings.getInt("TRANSP_" + html, 0);
        transpor(transSalv);

    }

    private void salvar() {
        editor.putInt("TRANSP_" + html, hasTransp);
        editor.commit();
        transpor(hasTransp);
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

        findViewById(R.id.ctrlrwnd)
                .setOnTouchListener(new RepeatListener(400, 100, new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mPlayer.isPlaying())
                            voltar();
                    }
                }));
        findViewById(R.id.ctrlpause).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mPlayer.isPlaying()) {
                    pause();
                    ImageButton btn = findViewById(R.id.ctrlpause);
                    btn.setImageResource(R.drawable.ctrlplay);
                } else {
                    play2();
                    ImageButton btn = findViewById(R.id.ctrlpause);
                    btn.setImageResource(R.drawable.ctrlpause);
                }
            }
        });
        findViewById(R.id.ctrlstop).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mPlayer.isPlaying()) {
                    stop();
                    ImageButton btn = findViewById(R.id.ctrlpause);
                    btn.setImageResource(R.drawable.ctrlplay);
                }
            }
        });
        findViewById(R.id.ctrlfwrd)
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
        webView = findViewById(R.id.webView1);
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
        String[] escalaTmp = (new String[]{"zerofiller", "@01", "@02", "@03", "@04", "@05", "@06", "@07", "@08",
                "@09", "@10", "@11", "@12"});
        String[] escalaEuropeia = (new String[]{"zerofiller", "Do", "Do#", "Re", "Mib", "Mi", "Fa", "Fa#", "Sol",
                "Sol#", "La", "Sib", "Si", "Do", "Do#", "Re", "Mib", "Mi", "Fa", "Fa#", "Sol", "Sol#", "La", "Sib",
                "Si"});
        String[] escalaAmericana = (new String[]{"zerofiller", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A",
                "Bb", "B", "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B"});

        String[] escalaMenos = (new String[]{"C-", "C#-", "D-", "Eb-", "E-", "F-", "F#-", "G-", "G#-", "A-", "Bb-",
                "B-"});
        String[] escalaMenor = (new String[]{"Cm", "C#m", "Dm", "Ebm", "Em", "Fm", "F#m", "Gm", "G#m", "Am", "Bbm",
                "Bm"});

        String[] escala = escalaEuropeia;
        if (settings.getBoolean("escalaAmericana", false)) {
            escala = escalaAmericana;
        }
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

                    if (receiveString.contains("@transp@")) {
                        int transSalv = settings.getInt("TRANSP_" + html, 0);
                        if (transSalv != 0) {
                            receiveString = "<FONT COLOR=\"#8a00e0\">  Salvo: Transposição " + escalaTmp[transSalv] + "</FONT>";
                            pri = 98;
                        } else {
                            continue;
                        }
                    }else if (receiveString.contains("@capot@")) {
                        int capotSalv = settings.getInt("CAPOT_" + html, 0);
                        if (capotSalv != 0) {
                            receiveString = "<FONT COLOR=\"#8a00e0\">  Salvo: Braçadeira " + capotSalv + "ª Traste</FONT>";
                            pri = 98;
                        } else {
                            if (receiveString.contains("Braçadeira")) {
                                stringBuilder.append(receiveString).append("\n");
                            }
                            continue;
                        }
                    }else {
                        stringBuilder.append(receiveString).append("\n");
                        continue;
                    }
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
                    receiveString = receiveString.replace(escalaEuropeia[i], escalaTmp[i]);
                }

                // Logica para descobrir a primeira nota:
                if (pri == 99) {
                    String x = "@";
                    for (int i = 0; i < receiveString.length(); i++) {
                        if (receiveString.charAt(i) == x.charAt(0)) {
                            pri = Integer.parseInt(receiveString.substring(i + 1, i + 3));
                            // dif = quantas casas vai subir ou descer
                            if (numero != 0) {
                                dif = Math.abs(numero - pri);
                            }
                            break;
                        }
                    }
                }

                if (pri == 99) {
                    continue;
                }

                if (pri == 98) {
                    pri = 99;
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
                if (pri - numero == 0 || dif == 0) {
                    for (int i = 0; i < escalaTmp.length; i++) {
                        receiveString = receiveString.replace(escalaTmp[i], escala[i]);
                    }
                }

                for (int i = 0; i < escalaMenos.length; i++) {
                    receiveString = receiveString.replace(escalaMenos[i], escalaMenor[i]);
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
    public void escolhe(View view) {
        settings = getSharedPreferences(PREFS_NAME, 0);
        String listas = settings.getString("Listas", "" );
        Gson gson = new Gson();
        ArrayList<CantoList> data = gson.fromJson(listas, new TypeToken<ArrayList<CantoList>>() {}.getType());
        if (data == null  || data.size() == 0 ){
            Toast.makeText(context, context.getString(R.string.crie_uma_lista), Toast.LENGTH_LONG).show();
            criarLista();
            return;
        }

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this, R.style.FullHeightDialog);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
        final Context context = this;

        arrayAdapter.add(context.getString(R.string.listaPersonal));
        arrayAdapter.add(context.getString(R.string.escolha_lista2));

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    criarLista( );
                else
                    escolheLista();
            }
        });
        builderSingle.show();
    }

    public void criarLista(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.FullHeightDialog);
        builder.setTitle(this.getString(R.string.nome_lista));
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
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
                            criarLista();
                            return;
                        }
                    }
                    data.add(lista);
                    listas = gson.toJson(data);
                    editor = settings.edit();

                    CantosClass cantosClass = new CantosClass();
                    cantosClass = ((CantosClass) getApplicationContext());

                    CantoList listCanto = new CantoList();
                    ArrayList<Integer> cantos = new ArrayList<>();
                    cantos.add(cantosClass.getCantoId(html));
                    listCanto.setTitulo(titulo);
                    listCanto.setCantos(cantos);

                    String listaNova = gson.toJson(listCanto);
                    editor = settings.edit();
                    editor.putString(titulo,listaNova);
                    editor.putString("Listas",listas);
                    editor.apply();
                    Toast.makeText(context, context.getString(R.string.adicionadoalista), Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }

    public void escolheLista( ) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this, R.style.FullHeightDialog);
        builderSingle.setTitle(context.getString(R.string.escolha_lista));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
        final Context context = this;
        settings = getSharedPreferences(PREFS_NAME, 0);
        String listas = settings.getString("Listas", "" );
        Gson gson = new Gson();
        ArrayList<CantoList> data = gson.fromJson(listas, new TypeToken<ArrayList<CantoList>>() {}.getType());
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                arrayAdapter.add(data.get(i).getTitulo());
            }
        }
        if (data.size() == 0 ){
            criarLista();
            return;
        }

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                String listaJson = settings.getString(strName, "" );
                Gson gson = new Gson();
                CantoList listCanto = gson.fromJson(listaJson, new TypeToken<CantoList>() {}.getType());

                CantosClass cantosClass = ((CantosClass) getApplicationContext());

                if (listCanto != null) {
                    ArrayList<Integer> cantos = listCanto.getCantos();
                    if (cantos.contains(cantosClass.getCantoId(html))) {
                        Toast.makeText(context, context.getString(R.string.ja_na_lista), Toast.LENGTH_LONG).show();
                        return;
                    }  else {
                        cantos.add(cantosClass.getCantoId(html));
                    }
                    listCanto.setCantos(cantos);
                }else{
                    listCanto = new CantoList();
                    ArrayList<Integer> cantos = new ArrayList<>();
                    cantos.add(cantosClass.getCantoId(html));
                    listCanto.setTitulo(strName);
                    listCanto.setCantos(cantos);
                }

                String listas = gson.toJson(listCanto);
                editor = settings.edit();
                editor.putString(strName,listas);
                editor.apply();
                Toast.makeText(context, context.getString(R.string.adicionadoalista), Toast.LENGTH_LONG).show();

            }
        });
        builderSingle.show();
    }
    public void  hideShowAnimate( int id, int visib, Animation anima ){
        ImageButton imageButton =  findViewById(id);
        imageButton.startAnimation(anima);
        imageButton.setVisibility(visib);
    }
    public void showMenu(View view) {
        Animation animation;
        int visibility;
        if ( menuOptionsVisible ) {
            animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_down);
            visibility = View.INVISIBLE;
            menuOptionsVisible = false;
        }else {
            animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_up);
            visibility = View.VISIBLE;
            menuOptionsVisible = true;
        }

        hideShowAnimate(R.id.scrolld,visibility,animation);
        hideShowAnimate(R.id.capot,visibility,animation);
        hideShowAnimate(R.id.transp,visibility,animation);
        hideShowAnimate(R.id.addLista,visibility,animation);
        if (!getIntent().getStringExtra("url").isEmpty())
            if (!mPlayer.isPlaying())
                hideShowAnimate(R.id.music,visibility,animation);

    }
}