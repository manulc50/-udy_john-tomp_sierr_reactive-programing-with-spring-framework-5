package guru.springframework.sfgrestbrewery.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import guru.springframework.sfgrestbrewery.web.models.BeerStyleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Entity
public class Beer {
	
	@Id
	private Integer id;
	
	private String beerName;
	private BeerStyleEnum beerStyle;
	private BigDecimal price;
	private Integer quantityOnHand;
	
	private Long version;
	
	private String upc;
	
	private LocalDateTime createdDate;
	
	private LocalDateTime lastModifiedDate;

}
