package br.com.admcode.screenmatch.service;

import com.openai.client.OpenAIClient; import com.openai.client.okhttp.OpenAIOkHttpClient; import com.openai.models.ChatModel; import com.openai.models.chat.completions.ChatCompletion; import com.openai.models.chat.completions.ChatCompletionCreateParams;

public class ConsultaChatGPT {

    private static final OpenAIClient CLIENT =
            OpenAIOkHttpClient.fromEnv();

    public static String obterTraducao(String texto) {

        if (texto == null || texto.isBlank()) {
            return "";
        }

        ChatCompletionCreateParams requisicao =
                ChatCompletionCreateParams.builder()
                        .model(ChatModel.GPT_4O_MINI)
                        .addUserMessage(
                                "Traduza para o português brasileiro o texto abaixo. "
                                        + "Retorne somente a tradução:\n\n"
                                        + texto
                        )
                        .build();

        ChatCompletion resposta =
                CLIENT.chat().completions().create(requisicao);

        return resposta.choices()
                .get(0)
                .message()
                .content()
                .orElse("")
                .trim();
    }


}