package de.uol.vpp.production.infrastructure.scheduler;

import de.uol.vpp.production.domain.aggregates.ProductionAggregate;
import de.uol.vpp.production.domain.entities.ProductionProducerEntity;
import de.uol.vpp.production.domain.exceptions.ProductionException;
import de.uol.vpp.production.domain.exceptions.ProductionProducerRepositoryException;
import de.uol.vpp.production.domain.repositories.IProductionProducerRepository;
import de.uol.vpp.production.domain.repositories.IProductionRepository;
import de.uol.vpp.production.domain.utils.TimestampUtils;
import de.uol.vpp.production.domain.valueobjects.*;
import de.uol.vpp.production.infrastructure.rabbitmq.RabbitMQSender;
import de.uol.vpp.production.infrastructure.rabbitmq.messages.ActionRequestMessage;
import de.uol.vpp.production.infrastructure.rabbitmq.messages.GridManipulationMessage;
import de.uol.vpp.production.infrastructure.rabbitmq.messages.ProducerManipulationMessage;
import de.uol.vpp.production.infrastructure.rabbitmq.messages.StorageManipulationMessage;
import de.uol.vpp.production.infrastructure.rest.MasterdataRestClient;
import de.uol.vpp.production.infrastructure.rest.SolarRestClient;
import de.uol.vpp.production.infrastructure.rest.WeatherRestClient;
import de.uol.vpp.production.infrastructure.rest.dto.*;
import de.uol.vpp.production.infrastructure.rest.exceptions.MasterdataRestClientException;
import de.uol.vpp.production.infrastructure.rest.exceptions.SolarRestClientException;
import de.uol.vpp.production.infrastructure.rest.exceptions.WeatherRestClientException;
import de.uol.vpp.production.infrastructure.utils.ProductionsUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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


    public void createProduction(ActionRequestMessage message) {
        String vppId = message.getVppId();
        String actionRequestId = message.getActionRequestId();
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

                Map<StorageManipulationMessage, Pair<Integer, Integer>> storageManipulationToPeriodMap = new HashMap<>();

                List<String> householdIds = masterdataRestClient.getAllHouseholdsByVppId(vppId);
                List<String> dppIds = masterdataRestClient.getAllDppsByVppId(vppId);
                List<WindEnergyDTO> winds = this.getAllWinds(householdIds, dppIds);
                List<WaterEnergyDTO> waters = this.getAllWaters(householdIds, dppIds);
                List<SolarEnergyDTO> solars = this.getAllSolars(householdIds, dppIds);
                List<OtherEnergyDTO> others = this.getAllOthers(householdIds, dppIds);

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

                    this.processWinds(currentWithoutSeconds, windWeatherMap, productionAggregate, winds, actionRequestId, message.getProducerManipulations());
                    this.processWaters(currentWithoutSeconds, productionAggregate, waters, message.getProducerManipulations());
                    this.processSolars(currentZDT, currentWithoutSeconds, solarForecastMap, forecastIndex, productionAggregate, solars, message.getProducerManipulations());
                    this.processOthers(currentWithoutSeconds, productionAggregate, others, message.getProducerManipulations());

                    //Storage Manipulation
                    this.storageManipulation(message, currentWithoutSeconds, storageManipulationToPeriodMap, productionAggregate);

                    //Grid Manipulation
                    this.gridManipulation(message, currentWithoutSeconds, productionAggregate);
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

    private List<WindEnergyDTO> getAllWinds(List<String> householdIds, List<String> dppIds) throws MasterdataRestClientException {
        List<WindEnergyDTO> winds = new ArrayList<>();
        for (String householdId : householdIds) {
            winds.addAll(masterdataRestClient.getAllWindsByHouseholdId(householdId));
        }
        for (String dppId : dppIds) {
            winds.addAll(masterdataRestClient.getAllWindsByDppId(dppId));
        }
        return winds;
    }

    private List<WaterEnergyDTO> getAllWaters(List<String> householdIds, List<String> dppIds) throws MasterdataRestClientException {
        List<WaterEnergyDTO> waters = new ArrayList<>();
        for (String householdId : householdIds) {
            waters.addAll(masterdataRestClient.getAllWatersByHouseholdId(householdId));
        }
        for (String dppId : dppIds) {
            waters.addAll(masterdataRestClient.getAllWatersByDppId(dppId));
        }
        return waters;
    }

    private List<SolarEnergyDTO> getAllSolars(List<String> householdIds, List<String> dppIds) throws MasterdataRestClientException {
        List<SolarEnergyDTO> solars = new ArrayList<>();
        for (String householdId : householdIds) {
            solars.addAll(masterdataRestClient.getAllSolarsByHouseholdId(householdId));
        }
        for (String dppId : dppIds) {
            solars.addAll(masterdataRestClient.getAllSolarsByDppId(dppId));
        }
        return solars;
    }


    private List<OtherEnergyDTO> getAllOthers(List<String> householdIds, List<String> dppIds) throws MasterdataRestClientException {
        List<OtherEnergyDTO> others = new ArrayList<>();
        for (String householdId : householdIds) {
            others.addAll(masterdataRestClient.getAllOthersByHousehold(householdId));
        }
        for (String dppId : dppIds) {
            others.addAll(masterdataRestClient.getAllOthersByDppId(dppId));
        }
        return others;
    }

    private void processWinds(ZonedDateTime currentWithoutSeconds, Map<String, List<WeatherDTO>> windWeatherMap, ProductionAggregate productionAggregate, List<WindEnergyDTO> windEnergyDTOS, String actionRequestId, List<ProducerManipulationMessage> producerManipulations) throws ProductionException, ProductionProducerRepositoryException, WeatherRestClientException {
        for (WindEnergyDTO windEnergyDTO : windEnergyDTOS) {
            ProducerManipulationMessage producerManipulationMessage = null;

            for (ProducerManipulationMessage producerManipulation : producerManipulations) {
                ZonedDateTime start = TimestampUtils.toBerlinTimestamp(producerManipulation.getStartTimestamp(), false);
                ZonedDateTime end = TimestampUtils.toBerlinTimestamp(producerManipulation.getEndTimestamp(), false);
                if (producerManipulation.getProducerId().equals(windEnergyDTO.getWindEnergyId())
                        && (currentWithoutSeconds.isAfter(start)
                        || currentWithoutSeconds.isEqual(start)) && (currentWithoutSeconds.isBefore(end) ||
                        currentWithoutSeconds.isEqual(end))
                ) {
                    producerManipulationMessage = producerManipulation;
                }
            }

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

                double currentValue = this.producerManipulation(producerManipulationMessage, possibleValue, windEnergyDTO.getCapacity());

                this.createAndAssignProductionProducer(windEnergyDTO.getWindEnergyId(), "WIND",
                        currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
            } else {
                log.error("Production failed, weather api error");
                rabbitMQSender.sendFailed(actionRequestId);
            }
        }
    }

    private void processWaters(ZonedDateTime currentWithoutSeconds, ProductionAggregate productionAggregate, List<WaterEnergyDTO> waterEnergyDTOS, List<ProducerManipulationMessage> producerManipulations) throws ProductionException, ProductionProducerRepositoryException {
        for (WaterEnergyDTO waterEnergyDTO : waterEnergyDTOS) {
            ProducerManipulationMessage producerManipulationMessage = null;
            for (ProducerManipulationMessage producerManipulation : producerManipulations) {
                ZonedDateTime start = TimestampUtils.toBerlinTimestamp(producerManipulation.getStartTimestamp(), false);
                ZonedDateTime end = TimestampUtils.toBerlinTimestamp(producerManipulation.getEndTimestamp(), false);
                if (producerManipulation.getProducerId().equals(waterEnergyDTO.getWaterEnergyId())
                        && (currentWithoutSeconds.isAfter(start)
                        || currentWithoutSeconds.isEqual(start)) && (currentWithoutSeconds.isBefore(end) ||
                        currentWithoutSeconds.isEqual(end))
                ) {
                    producerManipulationMessage = producerManipulation;
                }
            }

            // Calculate production + forecast for each
            Double possibleValue = ProductionsUtils.calculateWater(
                    waterEnergyDTO.getHeight(), waterEnergyDTO.getGravity(), waterEnergyDTO.getDensity(),
                    waterEnergyDTO.getEfficiency(), waterEnergyDTO.getVolumeFlow()
            );

            double currentValue = this.producerManipulation(producerManipulationMessage, possibleValue, waterEnergyDTO.getCapacity());

            // Save current and forecast production
            this.createAndAssignProductionProducer(waterEnergyDTO.getWaterEnergyId(), "WATER",
                    currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    private void processSolars(ZonedDateTime currentZDT, ZonedDateTime currentWithoutSeconds, Map<String, List<SolarForecastDTO>> solarForecastMap, int forecastIndex, ProductionAggregate productionAggregate, List<SolarEnergyDTO> solarEnergyDTOS, List<ProducerManipulationMessage> producerManipulations) throws ProductionException, ProductionProducerRepositoryException, SolarRestClientException {
        for (SolarEnergyDTO solarEnergyDTO : solarEnergyDTOS) {
            ProducerManipulationMessage producerManipulationMessage = null;
            for (ProducerManipulationMessage producerManipulation : producerManipulations) {
                ZonedDateTime start = TimestampUtils.toBerlinTimestamp(producerManipulation.getStartTimestamp(), false);
                ZonedDateTime end = TimestampUtils.toBerlinTimestamp(producerManipulation.getEndTimestamp(), false);
                if (producerManipulation.getProducerId().equals(solarEnergyDTO.getSolarEnergyId())
                        && (currentWithoutSeconds.isAfter(start)
                        || currentWithoutSeconds.isEqual(start)) && (currentWithoutSeconds.isBefore(end) ||
                        currentWithoutSeconds.isEqual(end))
                ) {
                    producerManipulationMessage = producerManipulation;
                }
            }

            // Calculate production + forecast for each
            if (!solarForecastMap.containsKey(solarEnergyDTO.getSolarEnergyId())) {
                solarForecastMap.put(solarEnergyDTO.getSolarEnergyId(), solarRestClient.getSolarForecast(currentZDT, solarEnergyDTO));
            }
            Double possibleValue = solarForecastMap.get(solarEnergyDTO.getSolarEnergyId()).get(forecastIndex).getValue();

            double currentValue = this.producerManipulation(producerManipulationMessage, possibleValue, solarEnergyDTO.getCapacity());

            // Save current and forecast production
            this.createAndAssignProductionProducer(solarEnergyDTO.getSolarEnergyId(), "SOLAR",
                    currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    private void processOthers(ZonedDateTime currentWithoutSeconds, ProductionAggregate productionAggregate, List<OtherEnergyDTO> otherEnergyDTOS, List<ProducerManipulationMessage> producerManipulations) throws ProductionException, ProductionProducerRepositoryException {
        for (OtherEnergyDTO otherEnergyDTO : otherEnergyDTOS) {
            ProducerManipulationMessage producerManipulationMessage = null;
            for (ProducerManipulationMessage producerManipulation : producerManipulations) {
                ZonedDateTime start = TimestampUtils.toBerlinTimestamp(producerManipulation.getStartTimestamp(), false);
                ZonedDateTime end = TimestampUtils.toBerlinTimestamp(producerManipulation.getEndTimestamp(), false);
                if (producerManipulation.getProducerId().equals(otherEnergyDTO.getOtherEnergyId())
                        && (currentWithoutSeconds.isAfter(start)
                        || currentWithoutSeconds.isEqual(start)) && (currentWithoutSeconds.isBefore(end) ||
                        currentWithoutSeconds.isEqual(end))
                ) {
                    producerManipulationMessage = producerManipulation;
                }
            }
            double currentValue = this.producerManipulation(producerManipulationMessage, otherEnergyDTO.getRatedCapacity(), otherEnergyDTO.getCapacity());
            this.createAndAssignProductionProducer(otherEnergyDTO.getOtherEnergyId(), "OTHER",
                    currentValue, otherEnergyDTO.getRatedCapacity(), currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    private void storageManipulation(ActionRequestMessage message, ZonedDateTime currentWithoutSeconds, Map<StorageManipulationMessage, Pair<Integer, Integer>> storageManipulationToPeriodMap, ProductionAggregate productionAggregate) throws ProductionException, ProductionProducerRepositoryException {
        for (StorageManipulationMessage storageManipulation : message.getStorageManipulations()) {
            ZonedDateTime start = TimestampUtils.toBerlinTimestamp(storageManipulation.getStartTimestamp(), false);
            if (currentWithoutSeconds.isEqual(start)) {
                int quarterPeriods = Double.valueOf(Math.floor(storageManipulation.getHours() * 4.)).intValue();
                storageManipulationToPeriodMap.put(storageManipulation, Pair.of(1, quarterPeriods));
                this.storageManipulation(currentWithoutSeconds, productionAggregate, storageManipulation);
            }
            if (storageManipulationToPeriodMap.get(storageManipulation) != null) {
                Integer current = storageManipulationToPeriodMap.get(storageManipulation).getFirst();
                Integer max = storageManipulationToPeriodMap.get(storageManipulation).getSecond();
                if (currentWithoutSeconds.isAfter(start) && current > 0 &&
                        current <= max) {
                    storageManipulationToPeriodMap.put(storageManipulation, Pair.of(current + 1, max));
                    this.storageManipulation(currentWithoutSeconds, productionAggregate, storageManipulation);
                }
            }
        }
    }

    private void gridManipulation(ActionRequestMessage message, ZonedDateTime currentWithoutSeconds, ProductionAggregate productionAggregate) throws ProductionException, ProductionProducerRepositoryException {
        for (GridManipulationMessage gridManipulation : message.getGridManipulations()) {
            ZonedDateTime start = TimestampUtils.toBerlinTimestamp(gridManipulation.getStartTimestamp(), false);
            ZonedDateTime end = TimestampUtils.toBerlinTimestamp(gridManipulation.getEndTimestamp(), false);
            if ((currentWithoutSeconds.isEqual(start) || currentWithoutSeconds.isAfter(start)) &&
                    (currentWithoutSeconds.isEqual(end) || currentWithoutSeconds.isBefore(end))) {
                if (gridManipulation.getType().equals("GRID_LOAD")) {
                    this.createAndAssignProductionProducer("GRID", "GRID",
                            gridManipulation.getRatedCapacity() * -1, gridManipulation.getRatedCapacity() * -1, currentWithoutSeconds.toEpochSecond(),
                            productionAggregate);
                } else if (gridManipulation.getType().equals("GRID_UNLOAD")) {
                    this.createAndAssignProductionProducer("GRID", "GRID",
                            gridManipulation.getRatedCapacity(), gridManipulation.getRatedCapacity(), currentWithoutSeconds.toEpochSecond(),
                            productionAggregate);
                }
            }
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

    private double producerManipulation(ProducerManipulationMessage producerManipulationMessage, Double possibleValue, Double capacity) {
        if (producerManipulationMessage != null) {
            if (producerManipulationMessage.getType().equals("PRODUCER_UP")) {
                return possibleValue / 100 * (capacity + producerManipulationMessage.getCapacity());
            } else if (producerManipulationMessage.getType().equals("PRODUCER_DOWN")) {
                return possibleValue / 100 * (capacity - producerManipulationMessage.getCapacity());
            }
        }
        return possibleValue / 100 * capacity;
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

    private void storageManipulation(ZonedDateTime currentWithoutSeconds, ProductionAggregate productionAggregate, StorageManipulationMessage storageManipulation) throws ProductionException, ProductionProducerRepositoryException {
        if (storageManipulation.getType().equals("STORAGE_LOAD")) {
            this.createAndAssignProductionProducer(storageManipulation.getStorageId(), "STORAGE",
                    storageManipulation.getRatedPower() * -1., storageManipulation.getRatedPower() * -1., currentWithoutSeconds.toEpochSecond(),
                    productionAggregate);
        } else if (storageManipulation.getType().equals("STORAGE_UNLOAD")) {
            this.createAndAssignProductionProducer(storageManipulation.getStorageId(), "STORAGE",
                    storageManipulation.getRatedPower(), storageManipulation.getRatedPower(), currentWithoutSeconds.toEpochSecond(),
                    productionAggregate);
        }
    }

}

