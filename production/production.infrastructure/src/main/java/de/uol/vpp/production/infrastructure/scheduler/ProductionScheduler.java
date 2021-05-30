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

/**
 * Diese Klasse ist für die Erzeugung der Erzeugungsprognose für eine Maßnahmenabfrage zuständig
 */
@Component
@Log4j2
public class ProductionScheduler {

    /**
     * Periode der Prognose im Viertelstundentakt
     */
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


    /**
     * Erstellt die Erzeugungswerte der betroffenen Erzeugungsanlagen für eine Maßnahmenabfrage
     *
     * @param message Maßnahmenabfragen Nachricht aus der RabbitMQ
     */
    public void createProduction(ActionRequestMessage message) {
        String vppId = message.getVppId();
        String actionRequestId = message.getActionRequestId();
        try {
            //Erstellung des aktuellen Zeitstempels
            ZonedDateTime currentZDT = ZonedDateTime.now(ZoneId.of("GMT+2"));
            ZonedDateTime currentWithoutSeconds = ZonedDateTime.of(
                    currentZDT.getYear(), currentZDT.getMonthValue(), currentZDT.getDayOfMonth(), currentZDT.getHour(),
                    currentZDT.getMinute() - (currentZDT.getMinute() % 15),
                    0, 0, ZoneId.of("GMT+2")
            );
            // Prüfung, ob VK veröffentlicht ist
            if (masterdataRestClient.isActiveVpp(vppId)) {
                Map<String, List<WeatherDTO>> windWeatherMap = new HashMap<>();
                Map<String, List<SolarForecastDTO>> solarForecastMap = new HashMap<>();

                Map<StorageManipulationMessage, Pair<Integer, Integer>> storageManipulationToPeriodMap = new HashMap<>();

                // Hole alle Erzeugungsanlagen
                List<String> householdIds = masterdataRestClient.getAllHouseholdsByVppId(vppId);
                List<String> dppIds = masterdataRestClient.getAllDppsByVppId(vppId);
                List<WindEnergyDTO> winds = this.getAllWinds(householdIds, dppIds);
                List<WaterEnergyDTO> waters = this.getAllWaters(householdIds, dppIds);
                List<SolarEnergyDTO> solars = this.getAllSolars(householdIds, dppIds);
                List<OtherEnergyDTO> others = this.getAllOthers(householdIds, dppIds);

                // Iteriere Prognosenperiode
                for (int forecastIndex = 0; forecastIndex <= FORECAST_PERIODS; forecastIndex++) {
                    // Erstelle Erzeugungsaggregat
                    ProductionAggregate productionAggregate = new ProductionAggregate();
                    productionAggregate.setProductionActionRequestId(
                            new ProductionActionRequestIdVO(actionRequestId)
                    );
                    productionAggregate.setProductionVirtualPowerPlantId(new ProductionVirtualPowerPlantIdVO(vppId));
                    productionAggregate.setProductionStartTimestamp(
                            new ProductionStartTimestampVO(currentWithoutSeconds.toEpochSecond())
                    );
                    productionRepository.saveProduction(productionAggregate);

                    // Erstelle Prognose für den aktuellen Zeitstempel
                    this.processWinds(currentWithoutSeconds, windWeatherMap, productionAggregate, winds, actionRequestId, message.getProducerManipulations());
                    this.processWaters(currentWithoutSeconds, productionAggregate, waters, message.getProducerManipulations());
                    this.processSolars(currentZDT, currentWithoutSeconds, solarForecastMap, forecastIndex, productionAggregate, solars, message.getProducerManipulations());
                    this.processOthers(currentWithoutSeconds, productionAggregate, others, message.getProducerManipulations());

                    // Berücksichtige Speichermanipulation aus Maßnahmenabfrage
                    this.storageManipulation(message, currentWithoutSeconds, storageManipulationToPeriodMap, productionAggregate);

                    // Berücksichtige Stromnetzmanipulation aus Maßnahmenabfrage
                    this.gridManipulation(message, currentWithoutSeconds, productionAggregate);

                    // Erstellung nächster Zeitstempel
                    currentWithoutSeconds = currentWithoutSeconds.plusMinutes(15);
                }

                // Sende Nachricht an Maßnahmen-Service, dass Erzeugungsprognose erfolgreich beendet ist
                rabbitMQSender.send(actionRequestId, currentWithoutSeconds.toEpochSecond());
            } else {
                log.error("Die Erstellung der Erzeugungsprognose ist fehlgeschlagen, da das {} VK nicht veröffentlicht ist", vppId);
                rabbitMQSender.sendFailed(actionRequestId);
            }
        } catch (Exception e) {
            log.error("Die Erstellung der Erzeugungsprognose ist fehlgeschlagen.", e);
            rabbitMQSender.sendFailed(actionRequestId);
        }

    }

    /**
     * Hole alle Windkraftanlagen durch Daten-Service REST-Client
     *
     * @param householdIds Liste der Haushalt Ids eines VK
     * @param dppIds       Liste der DK Ids eines VK
     * @return Liste der Windkraftanlagen
     * @throws MasterdataRestClientException e
     */
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

    /**
     * Hole alle Wasserkraftanlagen durch Daten-Service REST-Client
     *
     * @param householdIds Liste der Haushalt Ids eines VK
     * @param dppIds       Liste der DK Ids eines VK
     * @return Liste der Wasserkraftanlagen
     * @throws MasterdataRestClientException e
     */
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

    /**
     * Hole alle Solaranlagen durch Daten-Service REST-Client
     *
     * @param householdIds Liste der Haushalt Ids eines VK
     * @param dppIds       Liste der DK Ids eines VK
     * @return Liste der Solaranlagen
     * @throws MasterdataRestClientException e
     */
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

    /**
     * Hole alle alternativen Erzeugungsanlagen durch Daten-Service REST-Client
     *
     * @param householdIds Liste der Haushalt Ids eines VK
     * @param dppIds       Liste der DK Ids eines VK
     * @return Liste der alternativen Erzeugungsanlagen
     * @throws MasterdataRestClientException e
     */
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

    /**
     * Prognostiziert alle Windkraftanlagen für aktuellen Zeitstempel
     *
     * @param currentWithoutSeconds aktueller Zeitstempel
     * @param windWeatherMap        Wetterdaten aus Wetterschnittstelle
     * @param productionAggregate   aktuelles Erzeugungsaggregat
     * @param windEnergyDTOS        Liste der Windkraftanlagen
     * @param actionRequestId       Id der aktuellen Maßnahmenabfrage
     * @param producerManipulations Zu berücksichtigende Erzeugungsmanipulationen aus der Maßnahmenabfrage
     * @throws ProductionException                   e
     * @throws ProductionProducerRepositoryException e
     * @throws WeatherRestClientException            e
     */
    private void processWinds(ZonedDateTime currentWithoutSeconds, Map<String, List<WeatherDTO>> windWeatherMap, ProductionAggregate productionAggregate, List<WindEnergyDTO> windEnergyDTOS, String actionRequestId, List<ProducerManipulationMessage> producerManipulations) throws ProductionException, ProductionProducerRepositoryException, WeatherRestClientException {
        // Iteriere Windkraftanlagen
        for (WindEnergyDTO windEnergyDTO : windEnergyDTOS) {
            ProducerManipulationMessage producerManipulationMessage = null;

            // Prüfe, im aktuellen aktuellen Zeitstempel eine Manipulation für die aktuelle Windkraftanlage existiert
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
            // Hole korrekte Wetterdaten mithilfe des Zeitstempels
            WeatherDTO weatherDTO = this.getCorrectDTO(windWeatherMap.get(windEnergyDTO.getWindEnergyId()), currentWithoutSeconds);
            if (weatherDTO != null) {
                // Berechne Erzeugungswert mittels Wetterdaten und Berechnungsformel mit 100% Kapazität
                Double possibleValue = ProductionsUtils.calculateWind(windEnergyDTO.getRadius(), weatherDTO.getWindSpeed(),
                        windEnergyDTO.getEfficiency());

                // Berechne Erzeugungswert mittels Wetterdaten und Berechnungsformel mit aktueller Kapazität (inkl. Manipulationen)
                double currentValue = this.producerManipulation(producerManipulationMessage, possibleValue, windEnergyDTO.getCapacity());

                // Erstelle Erzeugungswert-Entität und füge es dem Erzeugungsaggregat hinzu
                this.createAndAssignProductionProducer(windEnergyDTO.getWindEnergyId(), "WIND",
                        currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
            } else {
                log.error("Die Erstellung eines Erzeugungswert ist fehlgeschlagen, da die Wetterdaten fehlerhaft sind.");
                rabbitMQSender.sendFailed(actionRequestId);
            }
        }
    }

    /**
     * Prognostiziert alle Wasserkraftwerke für aktuellen Zeitstempel
     *
     * @param currentWithoutSeconds aktueller Zeitstempel
     * @param productionAggregate   aktuelles Erzeugungsaggregat
     * @param waterEnergyDTOS       Liste der Wasserkraftwerke
     * @param producerManipulations Zu berücksichtigende Erzeugungsmanipulationen aus der Maßnahmenabfrage
     * @throws ProductionException                   e
     * @throws ProductionProducerRepositoryException e
     */
    private void processWaters(ZonedDateTime currentWithoutSeconds, ProductionAggregate productionAggregate, List<WaterEnergyDTO> waterEnergyDTOS, List<ProducerManipulationMessage> producerManipulations) throws ProductionException, ProductionProducerRepositoryException {
        for (WaterEnergyDTO waterEnergyDTO : waterEnergyDTOS) {
            ProducerManipulationMessage producerManipulationMessage = null;

            // Prüfe, ob es eine Manipulation für das aktuelle Wasserkraftwerk existiert
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

            // Berechne Erzeugung des Wasserkraftwerks mit 100% Kapazität
            Double possibleValue = ProductionsUtils.calculateWater(
                    waterEnergyDTO.getHeight(), waterEnergyDTO.getGravity(), waterEnergyDTO.getDensity(),
                    waterEnergyDTO.getEfficiency(), waterEnergyDTO.getVolumeFlow()
            );

            // Berechne Erzeugung des Wasserkraftwerks mit tatsächlicher Kapazität (inkl. Manipulation)
            double currentValue = this.producerManipulation(producerManipulationMessage, possibleValue, waterEnergyDTO.getCapacity());

            // Speichere Erzeugungswert und weise es dem Aggregat zu
            this.createAndAssignProductionProducer(waterEnergyDTO.getWaterEnergyId(), "WATER",
                    currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    /**
     * Prognostiziert alle Solaranlagen für aktuellen Zeitstempel
     *
     * @param currentZDT            aktueller Zeitstempel für Solar-REST-Client
     * @param currentWithoutSeconds aktueller Zeitstempel ohne Sekunden
     * @param solarForecastMap      Leere Liste oder Liste mit Erzeugungsprognose der Solaranlagen
     * @param forecastIndex         aktueller Index der Prognosenperiode
     * @param productionAggregate   Erzeugungsaggregat
     * @param solarEnergyDTOS       Liste der Solaranlagen
     * @param producerManipulations Liste der Manipulationen
     * @throws ProductionException                   e
     * @throws ProductionProducerRepositoryException e
     * @throws SolarRestClientException              e
     */
    private void processSolars(ZonedDateTime currentZDT, ZonedDateTime currentWithoutSeconds, Map<String, List<SolarForecastDTO>> solarForecastMap, int forecastIndex, ProductionAggregate productionAggregate, List<SolarEnergyDTO> solarEnergyDTOS, List<ProducerManipulationMessage> producerManipulations) throws ProductionException, ProductionProducerRepositoryException, SolarRestClientException {
        // Iteriere Solaranlagen
        for (SolarEnergyDTO solarEnergyDTO : solarEnergyDTOS) {
            ProducerManipulationMessage producerManipulationMessage = null;

            // Prüfe, ob es eine Manipulation für aktuelle Solaranlage im aktuellen Zeitstempel existiert
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

            // Hole Prognose der aktuellen Solaranlage für aktuellen Zeitstempel, falls es noch nicht existiert
            if (!solarForecastMap.containsKey(solarEnergyDTO.getSolarEnergyId())) {
                solarForecastMap.put(solarEnergyDTO.getSolarEnergyId(), solarRestClient.getSolarForecast(currentZDT, solarEnergyDTO));
            }

            // Hole korrekte Prognose mittels forecastIndex aus der Tagesprognose (100% Kapazität)
            Double possibleValue = solarForecastMap.get(solarEnergyDTO.getSolarEnergyId()).get(forecastIndex).getValue();

            // Erstelle tatsächlichen Erzeugungswert und berücksichtige Manipulation
            double currentValue = this.producerManipulation(producerManipulationMessage, possibleValue, solarEnergyDTO.getCapacity());

            // Speichere Erzeugungswert und weise es dem Aggregat zu
            this.createAndAssignProductionProducer(solarEnergyDTO.getSolarEnergyId(), "SOLAR",
                    currentValue, possibleValue, currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    /**
     * Erstellt Prognose von alternativen Erzeugungsanlagen. Hier finden keine Berechnungen statt, da diese
     * Form einen festen kW-Nennleistung besitzt, die konstant läuft.
     *
     * @param currentWithoutSeconds Zeitstempel ohne Sekunden
     * @param productionAggregate   Erzeugungsaggregat
     * @param otherEnergyDTOS       Menge der alternativen Erzeugungsanlagen
     * @param producerManipulations Menge der Manipulationen
     * @throws ProductionException                   e
     * @throws ProductionProducerRepositoryException e
     */
    private void processOthers(ZonedDateTime currentWithoutSeconds, ProductionAggregate productionAggregate, List<OtherEnergyDTO> otherEnergyDTOS, List<ProducerManipulationMessage> producerManipulations) throws ProductionException, ProductionProducerRepositoryException {
        for (OtherEnergyDTO otherEnergyDTO : otherEnergyDTOS) {
            ProducerManipulationMessage producerManipulationMessage = null;
            // Prüfe, ob eine Manipulation für aktuellen Zeitstempel und Erzeugungsanlage existiert
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

            // Erstelle Erzeugungswert mit Berücksichtigung der Kapazität (inkl. Manipulation)
            double currentValue = this.producerManipulation(producerManipulationMessage, otherEnergyDTO.getRatedCapacity(), otherEnergyDTO.getCapacity());
            // Erstellung der Erzeugungs-Entität und Zuweisung an Erzeugungsaggregat
            this.createAndAssignProductionProducer(otherEnergyDTO.getOtherEnergyId(), "OTHER",
                    currentValue, otherEnergyDTO.getRatedCapacity(), currentWithoutSeconds.toEpochSecond(), productionAggregate);
        }
    }

    /**
     * Diese Methode berücksichtigt die Speichermanipulationen der Maßnahmenabfrage und erstellt zusätzliche Erzeugungswerte
     * um die gesamte Erzeugung zu manipulieren
     *
     * @param message                        Maßnahmenabfrage
     * @param currentWithoutSeconds          Zeitstempel
     * @param storageManipulationToPeriodMap Manipulationen
     * @param productionAggregate            Erzeugungsaggregat
     * @throws ProductionException                   e
     * @throws ProductionProducerRepositoryException e
     */
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

    /**
     * Diese Methode berücksichtigt die Stromnetzmanipulation der Maßnahmenabfrage und erstellt zusätzliche Erzeugungswerte
     * um die gesamte Erzeugung zu manipulieren
     *
     * @param message               Maßnahmenabfrage
     * @param currentWithoutSeconds Zeitstempel
     * @param productionAggregate   Erzeugungsaggregat
     * @throws ProductionException                   e
     * @throws ProductionProducerRepositoryException e
     */
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

    /**
     * Diese Methode holt die korrekten Wetterdaten mithilfe
     *
     * @param weatherDTOS Liste der Wetterdaten
     * @param currentZDT  aktueller Zeitstempel
     * @return korrekte Wetterdaten für Zeitstempel
     */
    private WeatherDTO getCorrectDTO(List<WeatherDTO> weatherDTOS, ZonedDateTime currentZDT) {

        if (!weatherDTOS.isEmpty()) {
            for (WeatherDTO dto : weatherDTOS) {
                if (dto.getTimestamp().getDayOfWeek() == currentZDT.getDayOfWeek() &&
                        dto.getTimestamp().getMonthValue() == currentZDT.getMonthValue() &&
                        dto.getTimestamp().getYear() == currentZDT.getYear() &&
                        dto.getTimestamp().getHour() == currentZDT.getHour()) {
                    return dto;
                }
            }
            return weatherDTOS.get(0);
        } else {
            return null;
        }

    }

    /**
     * Diese Methode manipuliert die tatsächliche Kapazität einer Erzeugungsanlage
     *
     * @param producerManipulationMessage Manipulation
     * @param possibleValue               höchstmögliche Erzeugung der Erzeugungsanlage
     * @param capacity                    tatsächliche Kapazität der Erzeugungsanlage
     * @return (manipulierter) Erzeugungswert
     */
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

    /**
     * Diese Methode erstellt eine Erzeugungswert-Entität und weist es dem Erzeugungsaggregat zu
     *
     * @param producerId          Id der Erzeugungsanlage
     * @param type                Art der Erzeugungsanlage
     * @param currentValue        tatsächlicher Erzeugungswert
     * @param possibleValue       höchstmöglicher Erzeugungswert
     * @param timestamp           aktueller Zeitstempel
     * @param productionAggregate Erzeugungsaggregat
     * @throws ProductionException                   e
     * @throws ProductionProducerRepositoryException e
     */
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

    /**
     * Diese Methode manipuliert die Aggregation, indem es eine Erzeugungswert-Entität mit der Nennleistung aus der
     * Speichermanipulation erstellt. Wenn ein ein Speicher z.B. beladen wird, wird ein Erzeugungswert mit einer
     * negativen Zahl erzeugt
     *
     * @param currentWithoutSeconds aktueller Zeitstempel
     * @param productionAggregate   Erzeugungsaggregat
     * @param storageManipulation   Speichermanipulation
     * @throws ProductionException                   e
     * @throws ProductionProducerRepositoryException e
     */
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

