package guru.springframework.sfgrestbrewery.web.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

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
	private static final long serialVersionUID = 7639064805642543701L;

	@Null
	private Integer id;
	
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
