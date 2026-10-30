package br.com.admcode.screenmatch.model;

import br.com.admcode.screenmatch.service.ConsultaChatGPT;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name = "Series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    private String lancamento;
    private Integer totalTemporadas;
    private Double votacao;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String duracao;
    private String atores;
    private String poster;
    private String sinopse;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios = new ArrayList<>();

    public Serie() {}

    public Serie(DadosSerie dadosSerie){
        this.titulo = dadosSerie.titulo();
        this.lancamento = dadosSerie.lancamento();
        this.totalTemporadas = dadosSerie.totalTemporadas();
        this.votacao = OptionalDouble.of(Double.valueOf(dadosSerie.votacao())).orElse(0);
        this.genero = Categoria.fromString(dadosSerie.genero().split(",")[0].trim());
        this.duracao = dadosSerie.duracao();
        this.atores = dadosSerie.atores();
        this.poster = dadosSerie.poster();
        this.sinopse = traduzirOuManterOriginal(dadosSerie.sinopse());
    }

    private String traduzirOuManterOriginal(String sinopseOriginal) {
        if (sinopseOriginal == null || sinopseOriginal.isBlank()) {
            return sinopseOriginal;
        }

        try {
            String traducao = ConsultaChatGPT.obterTraducao(sinopseOriginal);

            if (traducao != null && !traducao.isBlank()) {
                return traducao.trim();
            }
        } catch (Exception e) {
            System.out.println(
                    "Não foi possível traduzir a sinopse. "
                            + "Salvando o texto original."
            );
        }

        return sinopseOriginal;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e -> e.setSerie(this));
        this.episodios = episodios;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getLancamento() {
        return lancamento;
    }

    public void setLancamento(String lancamento) {
        this.lancamento = lancamento;
    }

    public Integer getTotalTemporadas() {
        return totalTemporadas;
    }

    public void setTotalTemporadas(Integer totalTemporadas) {
        this.totalTemporadas = totalTemporadas;
    }

    public Double getVotacao() {
        return votacao;
    }

    public void setVotacao(Double votacao) {
        this.votacao = votacao;
    }

    public Categoria getGenero() {
        return genero;
    }

    public void setGenero(Categoria genero) {
        this.genero = genero;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }

    public String getAtores() {
        return atores;
    }

    public void setAtores(String atores) {
        this.atores = atores;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    @Override
    public String toString() {
        return  "titulo='" + titulo + '\'' +
                ", lancamento='" + lancamento + '\'' +
                ", totalTemporadas=" + totalTemporadas +
                ", votacao=" + votacao +
                ", genero=" + genero +
                ", duracao='" + duracao + '\'' +
                ", atores='" + atores + '\'' +
                ", poster='" + poster + '\'' +
                ", sinopse='" + sinopse + '\'' +
                ", episodios='" + episodios + '\'';
    }

}
