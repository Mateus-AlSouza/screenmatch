package br.com.admcode.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record obterDados(@JsonAlias("Title") String titulo,
                         @JsonAlias("Released") String lancamento,
                         @JsonAlias("Runtime") String duracao,
                         @JsonAlias("imdbRating") double votacao) {
}
