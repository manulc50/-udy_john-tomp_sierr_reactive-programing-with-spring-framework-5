package guru.springframework.reactivebeerclient.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8656486442576180892L;

	@Null
	private UUID id;
	
	@NotBlank
	private String beerName;
	
	@NotNull
	private BeerStyleEnum beerStyle;
	
	private String upc;
	
	private BigDecimal price;
	
	private Integer quantityOnHand;
	
	private OffsetDateTime createdDate;
	private OffsetDateTime lastModifiedDate;
	
}
