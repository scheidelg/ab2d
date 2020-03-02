package gov.cms.ab2d.common.service;

import com.google.common.reflect.TypeToken;
import gov.cms.ab2d.common.config.Mapping;
import gov.cms.ab2d.common.dto.PropertiesDTO;
import gov.cms.ab2d.common.model.Properties;
import gov.cms.ab2d.common.repository.PropertiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static gov.cms.ab2d.common.util.Constants.*;

@Service
@Transactional
@Slf4j
public class PropertiesServiceImpl implements PropertiesService {

    @Autowired
    private Mapping mapping;

    @Autowired
    private PropertiesRepository propertiesRepository;

    private final Type propertiesListType = new TypeToken<List<PropertiesDTO>>() { } .getType();

    @Override
    public List<Properties> getAllProperties() {
        return propertiesRepository.findAll();
    }

    @Override
    public List<PropertiesDTO> getAllPropertiesDTO() {
        List<Properties> properties = getAllProperties();
        return mapping.getModelMapper().map(properties, propertiesListType);
    }

    @Override
    public Properties getPropertiesByKey(String key) {
        return propertiesRepository.findByKey(key).orElseThrow(() -> {
            log.error("No entry was found for key {}", key);
            return new ResourceNotFoundException("No entry was found for key " + key);
        });
    }

    @Override
    public List<PropertiesDTO> updateProperties(List<PropertiesDTO> propertiesDTOs) {
        List<PropertiesDTO> propertiesDTOsReturn = new ArrayList<>();
        for (PropertiesDTO propertiesDTO : propertiesDTOs) {
            checkNameOfPropertyKey(propertiesDTO);

            // If this becomes more extensive, consider having a table that contains a mapping of keys to validation expressions
            if (propertiesDTO.getKey().equals(PCP_CORE_POOL_SIZE)) {
                int value = Integer.valueOf(propertiesDTO.getValue());
                if (value > 100 || value < 1) {
                    logErrorAndThrowException(PCP_CORE_POOL_SIZE, value);
                }

                addUpdatedPropertiesToList(propertiesDTOsReturn, propertiesDTO);
            } else if (propertiesDTO.getKey().equals(PCP_MAX_POOL_SIZE)) {
                int value = Integer.valueOf(propertiesDTO.getValue());
                if (value > 500 || value < 1) {
                    logErrorAndThrowException(PCP_MAX_POOL_SIZE, value);
                }

                addUpdatedPropertiesToList(propertiesDTOsReturn, propertiesDTO);
            } else if (propertiesDTO.getKey().equals(PCP_SCALE_TO_MAX_TIME)) {
                int value = Integer.valueOf(propertiesDTO.getValue());
                if (value > 3600 || value < 1) {
                    logErrorAndThrowException(PCP_SCALE_TO_MAX_TIME, value);
                }

                addUpdatedPropertiesToList(propertiesDTOsReturn, propertiesDTO);
            }
        }

        return propertiesDTOsReturn;
    }

    private void checkNameOfPropertyKey(PropertiesDTO properties) {
        if (!ALLOWED_PROPERTY_NAMES.contains(properties.getKey())) {
            log.error("Properties must contain a valid key name, received {}", properties.getKey());
            throw new InvalidPropertiesException("Properties must contain a valid key name, received " + properties.getKey());
        }
    }

    private void addUpdatedPropertiesToList(List<PropertiesDTO> propertiesDTOsReturn, PropertiesDTO propertiesDTO) {
        Properties properties = getPropertiesByKey(propertiesDTO.getKey());
        Properties mappedProperties = mapping.getModelMapper().map(propertiesDTO, Properties.class);
        mappedProperties.setId(properties.getId());
        Properties updatedProperties = propertiesRepository.save(mappedProperties);
        propertiesDTOsReturn.add(mapping.getModelMapper().map(updatedProperties, PropertiesDTO.class));
    }

    private void logErrorAndThrowException(String propertyKey, Object propertyValue) {
        log.error("Incorrect value for {} of {}", propertyKey, propertyValue);
        throw new InvalidPropertiesException("Incorrect value for " + propertyKey + " of " + propertyValue);
    }
}