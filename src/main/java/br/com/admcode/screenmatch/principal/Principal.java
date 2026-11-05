package br.com.admcode.screenmatch.principal;

import br.com.admcode.screenmatch.model.*;
import br.com.admcode.screenmatch.repository.SerieRepository;
import br.com.admcode.screenmatch.service.ConsumoApi;
import br.com.admcode.screenmatch.service.ConverterDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.awt.SystemColor.text;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverterDados conversor = new ConverterDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> historicoSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0){
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Historico de busca
                4 - Buscar por titulo
                5 - Buscar por atores
                6 - Ranking de Series
                7 - Buscar por gênero
                8 - Buscar por Data de Lançamento
                9 - Buscar por episódio
                10 - Melhores episódios por série
                11 - Buscar episódios a partir de uma data
                
                0 - Sair
                """;

        System.out.println(menu);
        opcao = leitura.nextInt();
        leitura.nextLine();

        switch (opcao) {
            case 1:
                buscarSerieWeb();
                break;
            case 2:
                buscarEpisodioPorSerie();
                break;
            case 3:
                buscarHistorico();
                break;
            case 4:
                buscarSeriePorTitulo();
                break;
            case 5:
                buscarSeriePorAtores();
                break;
            case 6:
                rankingSeries();
                break;
            case 7:
                buscarSeriesPorCategoria();
                break;
            case 8:
                filtrarSeriesPorTemporadaEAvaliacao();
                break;
            case 9:
                buscarEpisodiosPorTrecho();
                break;
            case 10:
                topEpisodiosPorSerie();
                break;
            case 11:
                buscarEpisodiosDepoisDeUmaData();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }}

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        if (repositorio.existsByTitulo(dados.titulo())){
            System.out.println("Serie já salva no banco de dados!");
        } else {
            Serie serie = new Serie(dados);
            repositorio.save(serie);
            System.out.println("Série salva com sucesso!");
        }
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie(){
        buscarHistorico();
        System.out.println("Digite uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if(serie.isPresent()){

            var serieEncontrada = serie.get();
            List<DadosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporadas dadosTemporada = conversor.obterDados(json, DadosTemporadas.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .toList();

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }   else {
            System.out.println("Serie não encontrada!");
        }

    }

    private void buscarHistorico(){
          series = repositorio.findAll();
          series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha um série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());

        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void buscarSeriePorAtores(){
        System.out.println("Escolha um série pelo nome: ");
        var nomeAtores = leitura.nextLine();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCase(nomeAtores);
            System.out.println("Dados das séries: ");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + ", Avaliação: " + s.getVotacao() ));
    }

    private void rankingSeries(){
        List<Serie> seriesTop = repositorio.findTop5ByOrderByVotacaoDesc();
        seriesTop.forEach(s -> System.out.println(s.getTitulo() + ", Avaliação: " + s.getVotacao() ));
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("Qual o gênero que gostaria de filtrar ?");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series da categoria " + nomeGenero);
        seriesPorCategoria.forEach(c -> System.out.println(c.getTitulo() + ", gênero: " + c.getGenero()));
    }

    private void filtrarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var votacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaEAValiacao(totalTemporadas, votacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getVotacao()));
    }

    private void buscarEpisodiosPorTrecho(){
        System.out.println("Qual o nome do episódio para busca?");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Série: %s Temporada %s - Episódio %s - %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(),
                        e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void topEpisodiosPorSerie(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Série: %s Temporada %s - Episódio %s - %s Avaliação %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(),
                            e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao() ));
        }
    }

    private void buscarEpisodiosDepoisDeUmaData(){
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano de lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine();
            List<Episodio> episodiosLancamento = repositorio.episodiosPorSerieEPorAno(serie, anoLancamento);
            episodiosLancamento.forEach(System.out::println);
        }
    }

}

