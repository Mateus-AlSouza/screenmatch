package br.com.admcode.screenmatch;

import br.com.admcode.screenmatch.model.obterDados;
import br.com.admcode.screenmatch.service.ConverterDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import br.com.admcode.screenmatch.service.ConsumoApi;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var consumoApi = new ConsumoApi();
		var json = consumoApi.obterDados("https://www.omdbapi.com/?i=tt3896198&apikey=5635d169");
		System.out.println(json);
		ConverterDados conversor = new ConverterDados();
		obterDados dados = conversor.obterDados(json, obterDados.class);
		System.out.println(dados);
	}
}

