package com.geekplay.contentservice.service;

import com.geekplay.contentservice.model.Post;
import com.geekplay.contentservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PostRepository postRepository;

    @Override
    public void run(String... args) throws Exception {
        // Solo cargamos datos si la base de datos está vacía
        if (postRepository.count() == 0) {
            loadInitialPosts();
        }
    }

    private void loadInitialPosts() {
        long currentMillis = System.currentTimeMillis();
        long authorId = 1L; // Asumimos que el Admin es ID 1

        // Textos de contenido
        String valorantContent = "Hola, gente. ¡Se viene una versión cargadita! El equipo de VALORANT al completo se ha dejado la piel para que nuestro juego esté más alineado con la visión que tenemos para él a largo plazo, preservando en todo momento nuestra identidad como shooter táctico, pero evolucionando para alcanzar un equilibrio más saludable entre armas y habilidades. Estos ajustes abarcan a los agentes, las armas y los mapas, y todos tienen por objetivo recompensar la toma de decisiones estratégica en VALORANT.";
        String knyContent = "Kimetsu no Yaiba: Mugen-jō-hen, también conocida como Demon Slayer: Kimetsu no Yaiba – The Movie: Infinity Castle, es una película de anime japonesa de fantasía oscura y acción basada en el arco «Castillo Infinito» del manga Kimetsu no Yaiba de Koyoharu Gotouge";
        String twdContent = "The Walking Dead trata sobre un grupo de sobrevivientes en un mundo postapocalíptico invadido por zombis, llamados \"caminantes\". La serie se enfoca en la lucha por la supervivencia, los conflictos interpersonales y las decisiones morales que los humanos deben tomar cuando la civilización se ha derrumbado. El personaje principal es el ayudante del sheriff Rick Grimes, quien despierta de un coma para encontrar este nuevo mundo.";
        String spidermanContent = "La peor pesadilla de Spiderman se ha hecho realidad. Uno de sus enemigos ha descubierto su identidad secreta y está usando esa información para golpear a la familia de Peter Parker. Ahora, Tía May ha sido secuestrada, y Mary Jane puede ser la siguiente.";

        List<Post> initialPosts = List.of(
            // ✅ CAMBIOS APLICADOS: Usamos "res:..." para las imágenes
            new Post(null, "Valorant: Gran Actualización", "El shooter táctico de Riot Games se renueva.", valorantContent, "VIDEOJUEGOS", authorId, currentMillis - 500000, "res:valorant"),
            new Post(null, "Kimetsu no Yaiba: El Castillo Infinito", "La nueva película de Demon Slayer ya tiene fecha.", knyContent, "PELICULAS", authorId, currentMillis - 400000, "res:kny"),
            new Post(null, "The Walking Dead: El Final", "Análisis de la última temporada de la serie de zombies.", twdContent, "SERIES", authorId, currentMillis - 300000, "res:twd"),
            new Post(null, "Spider-Man: Entre los Muertos", "Reseña del cómic que traumatizó a Peter Parker.", spidermanContent, "COMICS", authorId, currentMillis - 200000, "res:spiderman")
        );

        postRepository.saveAll(initialPosts);
        System.out.println("✅ " + initialPosts.size() + " publicaciones iniciales cargadas.");
    }
}