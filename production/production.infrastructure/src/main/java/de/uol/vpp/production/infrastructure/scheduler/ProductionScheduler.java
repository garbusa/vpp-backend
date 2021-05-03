package de.uol.vpp.production.infrastructure.scheduler;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.exceptions.ProductionProducerRepositoryException;
import de.uol.vpp.production.domain.repositories.IProductionProducerRepository;
import de.uol.vpp.production.domain.repositories.IProductionRepository;
import de.uol.vpp.production.domain.valueobjects.*;
import de.uol.vpp.production.infrastructure.rabbitmq.RabbitMQSender;
import de.uol.vpp.production.infrastructure.rest.MasterdataRestClient;
import de.uol.vpp.production.infrastructure.rest.SolarRestClient;
import de.uol.vpp.production.infrastructure.rest.WeatherRestClient;
import de.uol.vpp.production.infrastructure.rest.dto.*;
import de.uol.vpp.production.infrastructure.rest.exceptions.SolarRestClientException;
import de.uol.vpp.production.infrastructure.rest.exceptions.WeatherRestClientException;
import de.uol.vpp.production.infrastructure.utils.ProductionsUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class ProductionScheduler {

    private static final int FORECAST_PERIODS = 24 * 4; //24h in 15minutes;

    private final MasterdataRestClient masterdataRestClient;
    private final WeatherRestClient weatherRestClient;
    private final SolarRestClient solarRestClient;

    private final IProductionRepository productionRepository;
    private final IProductionProducerRepository productionProducerRepository;
    private final RabbitMQSender rabbitMQSender;

    public ProductionScheduler(MasterdataRestClient masterdataRestClient,
                               WeatherRestClient weatherRestClient, SolarRestClient solarRestClient,
                               IProductionRepository productionRepository, IProductionProducerRepository productionProducerRepository, RabbitMQSender rabbitMQSender) {
        this.masterdataRestClient = masterdataRestClient;
        this.weatherRestClient = weatherRestClient;
        this.solarRestClient = solarRestClient;
        this.productionRepository = productionRepository;
        this.productionProducerRepository = productionProducerRepository;
        this.rabbitMQSender = rabbitMQSender;
    }


    public void createProduction(String actionRequestId, String vppId) {
        try {
            ZonedDateTime currentZDT = ZonedDateTime.now(ZoneId.of("GMT+2"));
            ZonedDateTime currentWithoutSeconds = ZonedDateTime.of(
                    currentZDT.getYear(), currentZDT.getMonthValue(), currentZDT.getDayOfMonth(), currentZDT.getHour(),
                    currentZDT.getMinute() - (currentZDT.getMinute() % 15),
                    0, 0, ZoneId.of("GMT+2")
            );
            if (masterdataRestClient.isActiveVpp(vppId)) {
                Map<String, List<WeatherDTO>> windWeatherMap = new HashMap<>();
                Map<String, List<SolarForecastDTO>> solarForecastMap = new HashMap<>();

                for (int forecastIndex = 0; forecastIndex <= FORECAST_PERIODS; forecastIndex++) {
                    //Save Production
                    ProductionAggregate productionAggregate = new ProductionAggregate();
                    productionAggregate.setProductionActionRequestId(
                            new ProductionActionRequestIdVO(actionRequestId)
                    );
                    productionAggregate.setProductionVirtualPowerPlantId(new ProductionVirtualPowerPlantIdVO(vppId));
                    productionAggregate.setProductionStartTimestamp(
                            new ProductionStartTimestampVO(currentWithoutSeconds.toEpochSecond())
                    );
                    productionRepository.saveProduction(productionAggregate);

                    //Get all households
                    List<String> householdIds = masterdataRestClient.getAllHouseholdsByVppId(vppId);
                    for (String householdId : householdIds) {
                        //Get all winds
                        List<WindEnergyDTO> windEnergyDTOS = masterdataRestClient.getAllWindsByHouseholdId(householdId);
                        this.processWinds(currentWithoutSeconds, windWeatherMap, productionAggregate, windEnergyDTOS, actionRequestId);

                        //Get all waters
                        List<WaterEnergyDTO> waterEnergyDTOS = masterdataRestClient.getAllWatersByHouseholdId(householdId);
                        this.processWaters(currentWithoutSeconds, productionAggregate, waterEnergyDTOS);

                        //Get all waters
                        List<SolarEnergyDTO> solarEnergyDTOS = masterdataRestClient.getAllSolarsByHouseholdId(householdId);
                        this.processSolars(currentZDT, currentWithoutSeconds, solarForecastMap, forecastIndex, productionAggregate, solarEnergyDTOS);

                        //Get others
                        List<OtherEnergyDTO> otherEnergyDTOS = masterdataRestClient.getAllOthersByHousehold(householdId);
                        this.processOthers(currentWithoutSeconds, productionAggregate, otherEnergyDTOS);
                    }

                    //Get all households
                    List<String> dppIds = masterdataRestClient.getAllDppsByVppId(vppId);
                    for (String dppId : dppIds) {

                        //Get all winds
                        List<WindEnergyDTO> windEnergyDTOS = masterdataRestClient.getAllWindsByDppId(dppId);
                        this.processWinds(currentWithoutSeconds, windWeatherMap, productionAggregate, windEnergyDTOS, actionRequestId);


                        //Get all waters
                        List<WaterEnergyDTO> waterEnergyDTOS = masterdataRestClient.getAllWatersByDppId(dppId);
                        this.processWaters(currentWithoutSeconds, productionAggregate, waterEnergyDTOS);


                        //Get all solars
                        List<SolarEnergyDTO> solarEnergyDTOS = masterdataRestClient.getAllSolarsByDppId(dppId);
                        this.processSolars(currentZDT, currentWithoutSeconds, solarForecastMap, forecastIndex, productionAggregate, solarEnergyDTOS);


                        //Get others
                        List<OtherEnergyDTO> otherEnergyDTOS = masterdataRestClient.getAllOthersByDppId(dppId);
                        this.processOthers(currentWithoutSeconds, productionAggregate, otherEnergyDTOS);
                    }
                    currentWithoutSeconds = currentWithoutSeconds.plusMinutes(15);
                }
                rabbitMQSender.send(actionRequestId, currentWithoutSeconds.toEpochSecond());
            } else {
                log.error("Production failed, vpp status is published");
                rabbitMQSender.sendFailed(actionRequestId);
            }
        } catch (Exception e) {
            log.error("production failed", e);
            rabbitMQSender.sendFailed(actionRequestId);
        }


    }

    private void processWinds(ZonedDateTime currentWithoutSeconds, Map<String, List<WeatherDTO>> windWeatherMap, ProductionAggregate productionAggregate, List<WindEnergyDTO> windEnergyDTOS, String actionRequestId) throws ProductionException, ProductionProducerRepositoryException, WeatherRestClientException {
        for (WindEnergyDTO windEnergyDTO : windEnergyDTOS) {
            if (!windWeatherMap.containsKey(windEnergyDTO.getWindEnergyId())) {
                windWeatherMap.put(windEnergyDTO.getWindEnergyId(), weatherRestClient.getWeather(
                        windEnergyDTO.getLatitude(), windEnergyDTO.getLongitude()
                ));
            }
            WeatherDTO weatherDTO = this.getCorrectDTO(windWeatherMap.get(windEnergyDTO.getWindEnergyId()), currentWithoutSeconds);
            if (weatherDTO != null) {
                Double density = ProductionsUtils.calculateAirDensity(weatherDTO.getAirPressure(), weatherDTO.getAirHumidity(),
                        weatherDTO.getTemperatureCelsius());
                Double possibleValue = ProductionsUtils.calculateWind(windEnergyDTO.getRadius(), weatherDTO.getWindSpeed(), density,
                        windEnergyDTO.getEfficiency());
                Double currentValue = possibleValue / 100 * windEnergyDTO.getCapacity();
                this.createAndAssignProductionProducer(windEnergyDTO.getWindEnergyId(), "WIND",
                        currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
            } else {
                log.error("Production failed, weather api error");
                rabbitMQSender.sendFailed(actionRequestId);
            }
        }
    }

    private void processWaters(ZonedDateTime currentWithoutSeconds, ProductionAggregate productionAggregate, List<WaterEnergyDTO> waterEnergyDTOS) throws ProductionException, ProductionProducerRepositoryException {
        for (WaterEnergyDTO waterEnergyDTO : waterEnergyDTOS) {
            // Calculate production + forecast for each
            Double possibleValue = ProductionsUtils.calculateWater(
                    waterEnergyDTO.getHeight(), waterEnergyDTO.getGravity(), waterEnergyDTO.getDensity(),
                    waterEnergyDTO.getEfficiency(), waterEnergyDTO.getVolumeFlow()
            );
            Double currentValue = possibleValue / 100 * waterEnergyDTO.getCapacity();

            // Save current and forecast production
            this.createAndAssignProductionProducer(waterEnergyDTO.getWaterEnergyId(), "WATER",
                    currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    private void processSolars(ZonedDateTime currentZDT, ZonedDateTime currentWithoutSeconds, Map<String, List<SolarForecastDTO>> solarForecastMap, int forecastIndex, ProductionAggregate productionAggregate, List<SolarEnergyDTO> solarEnergyDTOS) throws ProductionException, ProductionProducerRepositoryException, SolarRestClientException {
        for (SolarEnergyDTO solarEnergyDTO : solarEnergyDTOS) {
            // Calculate production + forecast for each
            if (!solarForecastMap.containsKey(solarEnergyDTO.getSolarEnergyId())) {
                solarForecastMap.put(solarEnergyDTO.getSolarEnergyId(), solarRestClient.getSolarForecast(currentZDT, solarEnergyDTO));
            }
            Double possibleValue = solarForecastMap.get(solarEnergyDTO.getSolarEnergyId()).get(forecastIndex).getValue();
            Double currentValue = possibleValue / 100 * solarEnergyDTO.getCapacity();
            // Save current and forecast production
            this.createAndAssignProductionProducer(solarEnergyDTO.getSolarEnergyId(), "SOLAR",
                    currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    private void processOthers(ZonedDateTime currentWithoutSeconds, ProductionAggregate productionAggregate, List<OtherEnergyDTO> otherEnergyDTOS) throws ProductionException, ProductionProducerRepositoryException {
        for (OtherEnergyDTO otherEnergyDTO : otherEnergyDTOS) {
            Double possibleValue = otherEnergyDTO.getRatedCapacity();
            Double currentValue = possibleValue / 100 * otherEnergyDTO.getCapacity();
            this.createAndAssignProductionProducer(otherEnergyDTO.getOtherEnergyId(), "OTHER",
                    currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    private WeatherDTO getCorrectDTO(List<WeatherDTO> weatherDTOS, ZonedDateTime threadCurrentZDT) {
        if (!weatherDTOS.isEmpty()) {
            for (WeatherDTO dto : weatherDTOS) {
                if (dto.getTimestamp().getDayOfWeek() == threadCurrentZDT.getDayOfWeek() &&
                        dto.getTimestamp().getMonthValue() == threadCurrentZDT.getMonthValue() &&
                        dto.getTimestamp().getYear() == threadCurrentZDT.getYear() &&
                        dto.getTimestamp().getHour() == threadCurrentZDT.getHour()) {
                    return dto;
                }
            }
            return weatherDTOS.get(0);
        } else {
            return null;
        }

    }

    private void createAndAssignProductionProducer(String producerId, String type, Double currentValue, Double possibleValue, long timestamp, ProductionAggregate productionAggregate) throws ProductionException, ProductionProducerRepositoryException {
        ProductionProducerEntity productionProducerEntity = new ProductionProducerEntity();
        productionProducerEntity.setProducerId(
                new ProductionProducerIdVO(producerId)
        );
        productionProducerEntity.setProductionType(
                new ProductionProducerTypeVO(type)
        );
        productionProducerEntity.setCurrentValue(
                new ProductionProducerCurrentValueVO(currentValue)
        );
        productionProducerEntity.setPossibleValue(new ProductionProducerPossibleValueVO(possibleValue));
        productionProducerEntity.setStartTimestamp(
                new ProductionProducerStartTimestampVO(timestamp)
        );
        Long internalId = productionProducerRepository.saveProductionProducerInternal(productionProducerEntity);
        productionProducerRepository.assignToInternal(internalId, productionAggregate);
    }

}

