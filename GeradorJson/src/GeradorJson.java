import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;

public class GeradorJson {

	public static void main(String[] args) throws IOException {
		Gson gson = new Gson();

		CantosClass cantosClass = new CantosClass();
		cantosClass.popular();

		for (int i = 0; i < cantosClass.listCantos.size(); i++) {

			String path = "./../html/" + cantosClass.listCantos.get(i).getHtml() + ".HTML";
			String base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(path)));
			cantosClass.listCantos.get(i).setHtml_base64(base64);

			path = "./../html/ext/" + cantosClass.listCantos.get(i).getHtml() + ".HTML";
			base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(path)));
			cantosClass.listCantos.get(i).setExt_base64(base64);

		}

		PrintStream out = new PrintStream(new FileOutputStream("./../cantos.json"));
		out.println(gson.toJson(cantosClass.listCantos));
		out.close();

		PrintStream out2 = new PrintStream(new FileOutputStream("./../Ressuscitou/app/src/main/assets/cantos.json"));
		out2.println(gson.toJson(cantosClass.listCantos));
		out2.close();
		
		System.out.println("Finalizado");;
	}

}
