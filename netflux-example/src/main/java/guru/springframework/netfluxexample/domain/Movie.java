package guru.springframework.netfluxexample.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Document
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Movie {

	@Id
	private String id;
	
	@NonNull // Esta anotación de Lombok añade al método setter la comprobación de si esta propiedad es nula, y en caso afirmativo, lanza una excepción NullPointerException con un mensaje de error de validación 
	private String title;
}
