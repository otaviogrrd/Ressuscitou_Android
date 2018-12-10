package br.org.cn.gerador;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

public class GeradorJson {

	public static void main(String[] args) throws IOException {
		PrintStream out = new PrintStream(new FileOutputStream("cantos.json"));
		System.setOut(out);

		Gson gson = new Gson();

		CantosClass cantosClass = new CantosClass();
		cantosClass.popular();

		for (int i = 0; i < cantosClass.listCantos.size(); i++) {

			String path = "D:/Ressuscitou_Android/html/" + cantosClass.listCantos.get(i).getHtml() + ".HTML";
			String base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(path)));
			cantosClass.listCantos.get(i).setHtml_base64(base64);

			path = "D:/Ressuscitou_Android/html/ext/" + cantosClass.listCantos.get(i).getHtml() + ".HTML";
			base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(path)));
			cantosClass.listCantos.get(i).setExt_base64(base64);

		}
		
		System.out.println(gson.toJson(cantosClass.listCantos));
	}

}
