package br.org.cn.ressuscitou;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Canto {

	@SerializedName("titulo")
	@Expose
	private String titulo;
	@SerializedName("html")
	@Expose
	private String html;
	@SerializedName("url")
	@Expose
	private String url;
	@SerializedName("categoria")
	@Expose
	private int categoria;
	@SerializedName("numero")
	@Expose
	private String numero;
	@SerializedName("nr_2019")
	@Expose
	private String nr2019;
	@SerializedName("adve")
	@Expose
	private Boolean adve;
	@SerializedName("laud")
	@Expose
	private Boolean laud;
	@SerializedName("entr")
	@Expose
	private Boolean entr;
	@SerializedName("nata")
	@Expose
	private Boolean nata;
	@SerializedName("quar")
	@Expose
	private Boolean quar;
	@SerializedName("pasc")
	@Expose
	private Boolean pasc;
	@SerializedName("pent")
	@Expose
	private Boolean pent;
	@SerializedName("virg")
	@Expose
	private Boolean virg;
	@SerializedName("cria")
	@Expose
	private Boolean cria;
	@SerializedName("cpaz")
	@Expose
	private Boolean cpaz;
	@SerializedName("fpao")
	@Expose
	private Boolean fpao;
	@SerializedName("comu")
	@Expose
	private Boolean comu;
	@SerializedName("cfin")
	@Expose
	private Boolean cfin;
	@SerializedName("conteudo")
	@Expose
	private String conteudo;
	@SerializedName("html_base64")
	@Expose
	private String htmlBase64;
	@SerializedName("ext_base64")
	@Expose
	private String extBase64;

	public Canto() {
	}

	public Canto(String titulo, String html, String url, Integer categoria, String numero, String nr2019, Boolean adve, Boolean laud, Boolean entr, Boolean nata, Boolean quar, Boolean pasc, Boolean pent, Boolean virg, Boolean cria, Boolean cpaz, Boolean fpao, Boolean comu, Boolean cfin, String conteudo, String htmlBase64, String extBase64) {
		super();
		this.titulo = titulo;
		this.html = html;
		this.url = url;
		this.categoria = categoria;
		this.numero = numero;
		this.nr2019 = nr2019;
		this.adve = adve;
		this.laud = laud;
		this.entr = entr;
		this.nata = nata;
		this.quar = quar;
		this.pasc = pasc;
		this.pent = pent;
		this.virg = virg;
		this.cria = cria;
		this.cpaz = cpaz;
		this.fpao = fpao;
		this.comu = comu;
		this.cfin = cfin;
		this.conteudo = conteudo;
		this.htmlBase64 = htmlBase64;
		this.extBase64 = extBase64;
	}


	public Canto(String titulo, String html, String url, Integer categoria, String numero, String nr2019, Boolean adve, Boolean laud, Boolean entr, Boolean nata, Boolean quar, Boolean pasc, Boolean pent, Boolean virg, Boolean cria, Boolean cpaz, Boolean fpao, Boolean comu, Boolean cfin, String conteudo) {
		super();
		this.titulo = titulo;
		this.html = html;
		this.url = url;
		this.categoria = categoria;
		this.numero = numero;
		this.adve = adve;
		this.laud = laud;
		this.entr = entr;
		this.nata = nata;
		this.quar = quar;
		this.pasc = pasc;
		this.pent = pent;
		this.virg = virg;
		this.cria = cria;
		this.cpaz = cpaz;
		this.fpao = fpao;
		this.comu = comu;
		this.cfin = cfin;
		this.conteudo = conteudo;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getCategoria() {
		return categoria;
	}

	public void setCategoria(Integer categoria) {
		this.categoria = categoria;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNr2019() {
		return nr2019;
	}

	public void setNr2019(String nr2019) {
		this.nr2019 = nr2019;
	}

	public Boolean getAdve() {
		return adve;
	}

	public void setAdve(Boolean adve) {
		this.adve = adve;
	}

	public Boolean getLaud() {
		return laud;
	}

	public void setLaud(Boolean laud) {
		this.laud = laud;
	}

	public Boolean getEntr() {
		return entr;
	}

	public void setEntr(Boolean entr) {
		this.entr = entr;
	}

	public Boolean getNata() {
		return nata;
	}

	public void setNata(Boolean nata) {
		this.nata = nata;
	}

	public Boolean getQuar() {
		return quar;
	}

	public void setQuar(Boolean quar) {
		this.quar = quar;
	}

	public Boolean getPasc() {
		return pasc;
	}

	public void setPasc(Boolean pasc) {
		this.pasc = pasc;
	}

	public Boolean getPent() {
		return pent;
	}

	public void setPent(Boolean pent) {
		this.pent = pent;
	}

	public Boolean getVirg() {
		return virg;
	}

	public void setVirg(Boolean virg) {
		this.virg = virg;
	}

	public Boolean getCria() {
		return cria;
	}

	public void setCria(Boolean cria) {
		this.cria = cria;
	}

	public Boolean getCpaz() {
		return cpaz;
	}

	public void setCpaz(Boolean cpaz) {
		this.cpaz = cpaz;
	}

	public Boolean getFpao() {
		return fpao;
	}

	public void setFpao(Boolean fpao) {
		this.fpao = fpao;
	}

	public Boolean getComu() {
		return comu;
	}

	public void setComu(Boolean comu) {
		this.comu = comu;
	}

	public Boolean getCfin() {
		return cfin;
	}

	public void setCfin(Boolean cfin) {
		this.cfin = cfin;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getHtmlbase64() {
		return htmlBase64;
	}

	public void setHtmlBase64(String htmlBase64) {
		this.htmlBase64 = htmlBase64;
	}

	public String getExt_base64() {
		return extBase64;
	}

	public void setExtBase64(String extBase64) {
		this.extBase64 = extBase64;
	}

}