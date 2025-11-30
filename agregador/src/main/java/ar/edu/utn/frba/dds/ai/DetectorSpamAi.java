package ar.edu.utn.frba.dds.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface DetectorSpamAi {

  @SystemMessage("""
        Eres un moderador de contenido responsable de analizar posibles mensajes spam.
        Analiza el texto proporcionado.
        Si detectas: estafas, publicidad no solicitada, phishing o lenguaje de odio, responde 'true'.
        Si el contenido es legítimo, responde 'false'.
    """)
  boolean esSpam(@UserMessage String contenido);
}
