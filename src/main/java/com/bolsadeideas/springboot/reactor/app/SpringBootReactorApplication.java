package com.bolsadeideas.springboot.reactor.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Usuario;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@SpringBootApplication
@Slf4j
public class SpringBootReactorApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/**
		 * Con just se crea el flujo.
		 * 
		 */
//		Flux<String> nombresFlux = Flux.just("Saul", "Isis", "Avila", "Tolentino", "Vidal", "Oliver", "Nadyeli");

		List<String> nombresList = Arrays.asList("Saul", "Isis", "Avila", "Tolentino", "Vidal", "Oliver", "Nadyeli");

		Flux<String> nombresFlux = Flux.fromIterable(nombresList);

		Flux<Usuario> usuarios = nombresFlux
				/**
				 * El .map basicamente sirve para hacer transformaciones entre objetos, en este
				 * caso de un string a un tipo usuario siempre retorna algo y ese algo será el
				 * nuevo flujo del tipo que se haya definido como retorno
				 */
				.map(nombre -> new Usuario(nombre.toUpperCase(), null))
				/**
				 * El metodo filter evalúa si la condicion dentro de los parentesis es
				 * verdadera, si lo es entonces incluye el elemento en el nuevo arreglo (flujo)
				 */
				.filter(usuario -> usuario.getNombre().contains("I"))
				/**
				 * Con el metodo doOnNext se realizará una acción cada que se emita un nuevo
				 * valor.
				 */
				.doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("Nombre no pueden ser vacios");
					} else {
						System.out.println("IMPRIMO EN EL doOnNext " + usuario.getNombre());
					}
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		nombresFlux.subscribe(
				/**
				 * El primer parametro es la acción a realizar con los datos
				 */
				e -> log.info(e.toString()),
				/**
				 * Como segundo parametro se manda el error en caso de que el
				 * Emisor/Observable/Publisher falle
				 */
				error -> log.error(error.getMessage()),
				/**
				 * Como tercer parametro se puede mandar una instancia de Runnable este se
				 * ejecutara cuando finalice el envio de datos
				 */
				new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecucion del flujo");
					}
				});

		usuarios.subscribe(usuario -> System.out.println(usuario.toString()));
	}

}
