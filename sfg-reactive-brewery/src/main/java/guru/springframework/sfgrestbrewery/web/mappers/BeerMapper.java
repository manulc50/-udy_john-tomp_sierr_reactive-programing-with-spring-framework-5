package guru.springframework.sfgrestbrewery.web.mappers;

import org.mapstruct.Mapper;

import guru.springframework.sfgrestbrewery.domain.Beer;
import guru.springframework.sfgrestbrewery.web.models.BeerDto;


@Mapper(uses = {DateMapper.class})
public interface BeerMapper {

	BeerDto beerToBeerDto(Beer beer);
	BeerDto beerToBeerDtoWithInventory(Beer beer);
	Beer beerDtoToBeer(BeerDto beerDto);
}
