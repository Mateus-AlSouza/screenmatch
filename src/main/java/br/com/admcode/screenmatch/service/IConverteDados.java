package br.com.admcode.screenmatch.service;

public interface IConverteDados {
    <T> T obterDados (String Json, Class<T> classe);

}
