package br.com.admcode.screenmatch.principal;

import br.com.admcode.screenmatch.model.DadosSerie;
import br.com.admcode.screenmatch.model.DadosTemporadas;
import br.com.admcode.screenmatch.model.Episodio;
import br.com.admcode.screenmatch.model.Serie;
import br.com.admcode.screenmatch.repository.SerieRepository;
import br.com.admcode.screenmatch.service.ConsumoApi;
import br.com.admcode.screenmatch.service.ConverterDados;

import java.util.*;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverterDados conversor = new ConverterDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> historicoSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;

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
            case 5:
                buscarSeriePorAtores();
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
        Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("Dados da série: " + serieBuscada.get());

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


}

