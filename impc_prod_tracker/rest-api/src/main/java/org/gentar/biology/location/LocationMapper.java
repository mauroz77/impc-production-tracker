package org.gentar.biology.location;

import org.gentar.EntityMapper;
import org.gentar.Mapper;
import org.gentar.biology.strain.StrainMapper;
import org.gentar.biology.species.SpeciesMapper;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper implements Mapper<Location, LocationDTO>
{
    private EntityMapper entityMapper;
    private StrainMapper strainMapper;
    private SpeciesMapper speciesMapper;

    public LocationMapper(
        EntityMapper entityMapper, StrainMapper strainMapper, SpeciesMapper speciesMapper)
    {
        this.entityMapper = entityMapper;
        this.strainMapper = strainMapper;
        this.speciesMapper = speciesMapper;
    }

    public LocationDTO toDto(Location location)
    {
        return entityMapper.toTarget(location, LocationDTO.class);
    }

    public Location toEntity(LocationDTO locationDTO)
    {
        Location location = entityMapper.toTarget(locationDTO, Location.class);
        location.setStrain(strainMapper.toEntity(locationDTO.getStrainName()));
        location.setSpecies(speciesMapper.toEntity(locationDTO.getSpeciesName()));
        return location;
    }
}
