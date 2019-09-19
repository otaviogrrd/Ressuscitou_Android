package br.org.cn.ressuscitou;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CantoList {

    @SerializedName("titulo")
    @Expose
    private String titulo;
    @SerializedName("cantos")
    @Expose
    private ArrayList<Integer> cantos = null;

    public CantoList() {
    }

    public CantoList(String titulo, ArrayList<Integer> cantos) {
        super();
        this.titulo = titulo;
        this.cantos = cantos;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public ArrayList<Integer> getCantos() {
        return cantos;
    }

    public void setCantos(ArrayList<Integer> cantos) {
        this.cantos = cantos;
    }

}