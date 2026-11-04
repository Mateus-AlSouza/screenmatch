package br.com.admcode.screenmatch.repository;

import br.com.admcode.screenmatch.model.Categoria;
import br.com.admcode.screenmatch.model.Episodio;
import br.com.admcode.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    boolean existsByTitulo(String titulo);
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);
    List<Serie> findByAtoresContainingIgnoreCase(String nomeAtores);

    List<Serie> findTop5ByOrderByVotacaoDesc();
    List<Serie> findByGenero(Categoria categoria);
    List<Serie> findByTotalTemporadasLessThanEqualAndVotacaoGreaterThanEqual(int totalTemporadas, double votacao);

    @Query("select s from Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.votacao >= :votacao")
    List<Serie> seriesPorTemporadaEAValiacao(int totalTemporadas, double votacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trechoEpisodio%")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);
}


