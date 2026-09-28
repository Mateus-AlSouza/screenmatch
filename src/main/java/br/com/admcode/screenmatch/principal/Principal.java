package br.com.admcode.screenmatch.principal;

import br.com.admcode.screenmatch.model.DadosEpisodios;
import br.com.admcode.screenmatch.model.DadosSerie;
import br.com.admcode.screenmatch.model.DadosTemporadas;
import br.com.admcode.screenmatch.service.ConsumoApi;
import br.com.admcode.screenmatch.service.ConverterDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=5635d169";
    private ConverterDados conversor = new ConverterDados();

    private ConsumoApi consumo = new ConsumoApi();

    public void exibeMenu(){
        System.out.println("Digite o nome da Série ou filme para buscar");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ","+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);


        List<DadosTemporadas> temporadas = new ArrayList<>();
		for(int i = 1; i <= dados.totalTemporadas(); i++){
			json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ","+") +"&season="+i+ API_KEY);
			DadosTemporadas dadosTemporada = conversor.obterDados(json, DadosTemporadas.class);
			temporadas.add(dadosTemporada);
		}
		temporadas.forEach(System.out::println);

//        for(int i = 0; i < dados.totalTemporadas(); i++){
//            List<DadosEpisodios> episodiosTemporada = temporadas.get(i).episodios();
//            for(int j = 0; j < episodiosTemporada.size(); j++) {
//                System.out.println("------- Season "+i+" -------");
//                System.out.println("       Episodio "+j);
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println("------- Season "+t.numero()+" -------"+"\n"+"      episódio "+e.numero()+"\n"+e.titulo())));


    }
}
