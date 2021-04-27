package de.uol.vpp.action.infrastructure.algorithm;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.entities.ActionCatalogEntity;
import de.uol.vpp.action.domain.entities.ActionEntity;
import de.uol.vpp.action.domain.enums.ActionTypeEnum;
import de.uol.vpp.action.domain.enums.ProblemTypeEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.repositories.IActionRequestRepository;
import de.uol.vpp.action.domain.valueobjects.*;
import de.uol.vpp.action.infrastructure.rest.LoadRestClient;
import de.uol.vpp.action.infrastructure.rest.MasterdataRestClient;
import de.uol.vpp.action.infrastructure.rest.ProductionRestClient;
import de.uol.vpp.action.infrastructure.rest.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ActionAlgorithmService {

    private final IActionRequestRepository actionRequestRepository;
    private final MasterdataRestClient masterdataRestClient;
    private final LoadRestClient loadRestClient;
    private final ProductionRestClient productionRestClient;

    public void actionAlgorithm(String actionRequestId, Long timestamp) throws ActionException, ActionRepositoryException {
        log.info("crazy algorithm mit {}, {}", actionRequestId, timestamp);
        //Hole alle Loads und Productions by actionRequestId
        Optional<ActionRequestAggregate> actionRequest = actionRequestRepository.getActionRequest(new ActionRequestIdVO(actionRequestId));

        if (actionRequest.isPresent()) {
            VirtualPowerPlantDTO virtualPowerPlant = masterdataRestClient
                    .getVirtualPowerPlantById(actionRequest.get().getVirtualPowerPlantId().getValue());
            if (virtualPowerPlant != null) {
                List<LoadDTO> loads = loadRestClient.getAllLoadsByActionRequestId(actionRequestId);
                List<ProductionDTO> productions = productionRestClient.getAllProductionsByActionRequestId(actionRequestId);

                Map<Long, Pair<Double, Double>> comparisonMap = this.createComparisonMap(loads, productions);
//                List<Long> timestampWithLocalMaxima = this.checkLocalMaxima(comparisonMap);
                List<ActionTimeseries> actionTimeseriesList =
                        createActionTimeseriesList(actionRequestId, virtualPowerPlant, comparisonMap);

                List<HouseholdDTO> households = masterdataRestClient.getAllHouseholdsByVppId(virtualPowerPlant.getVirtualPowerPlantId());
                List<DecentralizedPowerPlantDTO> dpps = masterdataRestClient.getAllDppsByVppId(virtualPowerPlant.getVirtualPowerPlantId());

                List<StorageDTO> storages = new ArrayList<>();
                dpps.forEach(dpp -> storages.addAll(dpp.getStorages()));
                households.forEach(household -> storages.addAll(household.getStorages()));

                List<WaterEnergyDTO> waters = new ArrayList<>();
                dpps.forEach(dpp -> waters.addAll(dpp.getWaters()));
                households.forEach(household -> waters.addAll(household.getWaters()));

                List<WindEnergyDTO> winds = new ArrayList<>();
                dpps.forEach(dpp -> winds.addAll(dpp.getWinds()));
                households.forEach(household -> winds.addAll(household.getWinds()));

                List<SolarEnergyDTO> solars = new ArrayList<>();
                dpps.forEach(dpp -> solars.addAll(dpp.getSolars()));
                households.forEach(household -> solars.addAll(household.getSolars()));

                List<OtherEnergyDTO> others = new ArrayList<>();
                dpps.forEach(dpp -> others.addAll(dpp.getOthers()));
                households.forEach(household -> others.addAll(household.getOthers()));

                List<ActionCatalogEntity> catalogs = new ArrayList<>();
                for (ActionTimeseries actionTimeseries : actionTimeseriesList) {
                    //Hat Extremum?
                    //List<Long> maxima = actionTimeseries.getTimestamps().stream().filter(timestampWithLocalMaxima::contains).collect(Collectors.toList());
                    //actionTimeseries.setMaxima(maxima.size() > 0);

                    ActionCatalogEntity catalog = new ActionCatalogEntity();
                    catalog.setActionRequestIdVO(new ActionRequestIdVO(actionRequestId));
                    catalog.setStartTimestamp(new ActionCatalogStartTimestampVO(actionTimeseries.getTimestamps().first()));
                    catalog.setEndTimestamp(new ActionCatalogEndTimestampVO(actionTimeseries.getTimestamps().last()));
                    catalog.setProblemType(new ActionCatalogProblemTypeVO(actionTimeseries.getProblemType()));
                    catalog.setCumulativeGap(new ActionCatalogCumulativeGapVO(actionTimeseries.getAverageGap()));
                    List<ActionEntity> actionsInCatalog = new ArrayList<>();
                    if (actionTimeseries.getProblemType().equals(ProblemTypeEnum.OVERFLOW)) {
                        //Welche Speicher haben Kapazität zum speichern?
                        for (StorageDTO storage : storages) {
                            if (storage.getCapacity() < 100.) {
                                actionsInCatalog.add(
                                        this.createAction(catalog, storage.getStorageId(),
                                                true, storage.getRatedPower(), storage.getLoadTimeHour(),
                                                storage.getCapacity(), ActionTypeEnum.STORAGE_LOAD)
                                );
                            }
                        }

                        //Welche Erzeugungsanlagen können abgeregelt werden?
                        for (WaterEnergyDTO water : waters) {
                            if (water.getCapacity() > 0.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, actionTimeseries, water.getWaterEnergyId());
                                actionsInCatalog.add(
                                        this.createAction(catalog, water.getWaterEnergyId(),
                                                false, averageProduction, 0.,
                                                water.getCapacity(), ActionTypeEnum.PRODUCER_DOWN)
                                );
                            }
                        }

                        for (WindEnergyDTO wind : winds) {
                            if (wind.getCapacity() > 0.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, actionTimeseries, wind.getWindEnergyId());
                                actionsInCatalog.add(
                                        this.createAction(catalog, wind.getWindEnergyId(),
                                                false, averageProduction, 0.,
                                                wind.getCapacity(), ActionTypeEnum.PRODUCER_DOWN)
                                );
                            }
                        }

                        for (SolarEnergyDTO solar : solars) {
                            if (solar.getCapacity() > 0.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, actionTimeseries, solar.getSolarEnergyId());
                                actionsInCatalog.add(
                                        this.createAction(catalog, solar.getSolarEnergyId(),
                                                false, averageProduction, 0.,
                                                solar.getCapacity(), ActionTypeEnum.PRODUCER_DOWN)
                                );
                            }
                        }

                        for (OtherEnergyDTO other : others) {
                            if (other.getCapacity() > 0.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, actionTimeseries, other.getOtherEnergyId());
                                actionsInCatalog.add(
                                        this.createAction(catalog, other.getOtherEnergyId(),
                                                false, averageProduction, 0.,
                                                other.getCapacity(), ActionTypeEnum.PRODUCER_DOWN)
                                );
                            }
                        }

                    } else if (actionTimeseries.getProblemType().equals(ProblemTypeEnum.SHORTAGE)) {
                        //Welche Erzeugungsanlagen können hochgefahren werden?
                        for (WaterEnergyDTO water : waters) {
                            if (water.getCapacity() < 100.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, actionTimeseries, water.getWaterEnergyId());
                                Double averagePossibleProduction = this.getAveragePossibleProductionByProducerInTs(productions, actionTimeseries, water.getWaterEnergyId());
                                Double possibleProductionIncrease = averagePossibleProduction - averageProduction;
                                actionsInCatalog.add(
                                        this.createAction(catalog, water.getWaterEnergyId(),//10/100*((20-100)*-1)
                                                false, possibleProductionIncrease, 0.,
                                                water.getCapacity(), ActionTypeEnum.PRODUCER_UP)
                                );
                            }
                        }
                        for (WindEnergyDTO wind : winds) {
                            if (wind.getCapacity() < 100.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, actionTimeseries, wind.getWindEnergyId());
                                Double averagePossibleProduction = this.getAveragePossibleProductionByProducerInTs(productions, actionTimeseries, wind.getWindEnergyId());
                                Double possibleProductionIncrease = averagePossibleProduction - averageProduction;
                                actionsInCatalog.add(
                                        this.createAction(catalog, wind.getWindEnergyId(),
                                                false, possibleProductionIncrease, 0.,
                                                wind.getCapacity(), ActionTypeEnum.PRODUCER_UP)
                                );
                            }
                        }
                        for (SolarEnergyDTO solar : solars) {
                            if (solar.getCapacity() < 100.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, actionTimeseries, solar.getSolarEnergyId());
                                Double averagePossibleProduction = this.getAveragePossibleProductionByProducerInTs(productions, actionTimeseries, solar.getSolarEnergyId());
                                Double possibleProductionIncrease = averagePossibleProduction - averageProduction;
                                actionsInCatalog.add(
                                        this.createAction(catalog, solar.getSolarEnergyId(),
                                                false, possibleProductionIncrease, 0.,
                                                solar.getCapacity(), ActionTypeEnum.PRODUCER_UP)
                                );
                            }
                        }
                        for (OtherEnergyDTO other : others) {
                            if (other.getCapacity() < 100.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, actionTimeseries, other.getOtherEnergyId());
                                Double averagePossibleProduction = this.getAveragePossibleProductionByProducerInTs(productions, actionTimeseries, other.getOtherEnergyId());
                                Double possibleProductionIncrease = averagePossibleProduction - averageProduction;
                                actionsInCatalog.add(
                                        this.createAction(catalog, other.getOtherEnergyId(),
                                                false, possibleProductionIncrease, 0.,
                                                other.getCapacity(), ActionTypeEnum.PRODUCER_UP)
                                );
                            }
                        }


                        //Welche Speicher haben Kapazität zum entladen?
                        for (StorageDTO storage : storages) {
                            if (storage.getCapacity() > 0.) {
                                actionsInCatalog.add(
                                        this.createAction(catalog, storage.getStorageId(),
                                                true, storage.getRatedPower(), storage.getLoadTimeHour(),
                                                100 - storage.getCapacity(), ActionTypeEnum.STORAGE_UNLOAD)
                                );
                            }
                        }

                    }
                    catalog.setActions(actionsInCatalog);
                    catalogs.add(catalog);

                }

                //@todo persist catalogs
                log.info("creating action catalogs finished. persisting...");
                actionRequest.get().setCatalogs(catalogs);
                actionRequest.get().setFinished(new ActionFinishedVO(true));
                actionRequestRepository.saveActionRequest(actionRequest.get(), false);
            }
        }

    }

    private Map<Long, Pair<Double, Double>> createComparisonMap(List<LoadDTO> loads, List<ProductionDTO> productions) {
        TreeMap<Long, Pair<Double, Double>> result = new TreeMap<>();
        TreeSet<Long> timestamps = new TreeSet<>();

        loads.forEach((loadDTO) -> {
            timestamps.add(loadDTO.getStartTimestamp());
        });
        productions.forEach((productionDTO) -> {
            timestamps.add(productionDTO.getStartTimestamp());
        });
        for (Long timestamp : timestamps) {
            ProductionDTO productionDTO = this.getProductionByTimestamp(productions, timestamp);
            LoadDTO loadDTO = this.getLoadByTimestamp(loads, timestamp);

            if (loadDTO != null && productionDTO != null) {
                Double loadSum = 0.;
                Double productionSum = 0.;

                for (ProductionProducerDTO productionProducerDTO : productionDTO.getProducers()) {
                    productionSum = productionSum + productionProducerDTO.getCurrentValue();
                }
                for (LoadHouseholdDTO loadHouseholdDTO : loadDTO.getHouseholds()) {
                    loadSum = loadSum + loadHouseholdDTO.getLoadValue();
                }

                result.put(timestamp, Pair.of(loadSum, productionSum));
            }
        }
        return result;
    }
//
//    private List<Long> checkLocalMaxima(Map<Long, Pair<Double, Double>> comparisonMap) {
//        // Empty vector to store points of
//        // local maxima and minima
//        List<Long> maxima = new ArrayList<>();
//
//        Long[] timestamps = (Long[]) comparisonMap.keySet().toArray();
//        Double[] arr = (Double[]) comparisonMap.values().stream().map(Pair::getFirst).toArray();
//        int n = arr.length;
//
//        // Checking whether the first point is
//        // local maxima or minima or none
//        if (arr[0] > arr[1])
//            maxima.add(timestamps[0]);
//
//        // Iterating over all points to check
//        // local maxima and local minima
//        for (int i = 1; i < n - 1; i++) {
//            // Condition for local maxima
//            if ((arr[i - 1] < arr[i]) &&
//                    (arr[i] > arr[i + 1])) {
//                maxima.add(timestamps[i]);
//            }
//
//        }
//
//        // Checking whether the last point is
//        // local maxima or minima or none
//        if (arr[n - 1] > arr[n - 2]) {
//            maxima.add(timestamps[n - 1]);
//        }
//
//        return maxima;
//    }

    private List<ActionTimeseries> createActionTimeseriesList(String actionRequestId, VirtualPowerPlantDTO virtualPowerPlant, Map<Long, Pair<Double, Double>> comparisonMap) {
        List<ActionTimeseries> actionTimeseries = new ArrayList<>();
        boolean isShortage = false;
        boolean isOverflow = false;
        for (Map.Entry<Long, Pair<Double, Double>> timestampLoadProduction : comparisonMap.entrySet()) {
            Double load = timestampLoadProduction.getValue().getFirst() / 1000; //convert W into kW
            Double production = timestampLoadProduction.getValue().getSecond();
            if (!isShortage && aboveShortageThreshold(virtualPowerPlant, load, production)) {
                isOverflow = false;
                isShortage = true;
                ActionTimeseries pTs = createActionTimeseries(
                        timestampLoadProduction, load, production, ProblemTypeEnum.SHORTAGE, actionRequestId,
                        virtualPowerPlant.getVirtualPowerPlantId());
                actionTimeseries.add(pTs);
            } else if (!isOverflow && aboveOverflowThreshold(virtualPowerPlant, load, production)) {
                isShortage = false;
                isOverflow = true;
                ActionTimeseries pTs = createActionTimeseries(timestampLoadProduction, load, production,
                        ProblemTypeEnum.OVERFLOW, actionRequestId, virtualPowerPlant.getVirtualPowerPlantId());
                actionTimeseries.add(pTs);
            } else if ((isShortage && aboveShortageThreshold(virtualPowerPlant, load, production)) ||
                    (isOverflow && aboveOverflowThreshold(virtualPowerPlant, load, production))) {
                //Add to existing Timeseries
                if (actionTimeseries.size() > 0) {
                    ActionTimeseries last = actionTimeseries.get(actionTimeseries.size() - 1);
                    Double gap = 0.;
                    if (last.getProblemType().equals(ProblemTypeEnum.OVERFLOW)) {
                        gap = production - load;
                    } else if (last.getProblemType().equals(ProblemTypeEnum.SHORTAGE)) {
                        gap = load - production;
                    }
                    last.getTimestamps().add(timestampLoadProduction.getKey());
                    Double newGap = last.getAverageGap() + gap;
                    last.setAverageGap(newGap);
                }
            } else if (isShortage || isOverflow) {
                // remove timeseries with only 1 or less timestamp
                if (actionTimeseries.size() > 0 && actionTimeseries.get(actionTimeseries.size() - 1).getAverageGap() < 0.) {
                    ActionTimeseries last = actionTimeseries.get(actionTimeseries.size() - 1);
                    actionTimeseries.remove(last);
                } else if (actionTimeseries.size() > 0 && actionTimeseries.get(actionTimeseries.size() - 1).getAverageGap() > 0.) {
                    ActionTimeseries last = actionTimeseries.get(actionTimeseries.size() - 1);
                    Double oldGap = last.getAverageGap();
                    Double length = Integer.valueOf(last.getTimestamps().size()).doubleValue();
                    if (oldGap > 0. && length > 0. && oldGap / length > 0.) {
                        Double newGap = oldGap / length;
                        last.setAverageGap(newGap);
                    } else {
                        actionTimeseries.remove(last);
                    }
                }
                isShortage = false;
                isOverflow = false;
            }
        }

        return actionTimeseries;
    }

    private ActionEntity createAction(ActionCatalogEntity catalog, String producerOrStorageId, boolean isStorage, Double ratedPower, Double loadTimeHour, Double capacity, ActionTypeEnum actionType) throws ActionException {
        ActionEntity actionEntity = new ActionEntity();
        actionEntity.setActionRequestId(catalog.getActionRequestIdVO());
        actionEntity.setStartTimestamp(catalog.getStartTimestamp());
        actionEntity.setEndTimestamp(catalog.getEndTimestamp());
        actionEntity.setHours(
                new ActionHoursVO(
                        (isStorage) ? (1 / loadTimeHour) / 100 * (100 - capacity) : 0
                )
        );
        actionEntity.setActionValue(new ActionValueVO(ratedPower));
        actionEntity.setIsStorage(new ActionIsStorageVO(isStorage));
        actionEntity.setProducerOrStorageId(new ActionProducerOrStorageIdVO(
                producerOrStorageId
        ));
        actionEntity.setActionType(new ActionTypeVO(actionType));
        return actionEntity;
    }

    private Double getAverageCurrentProductionByProducerInTs(List<ProductionDTO> productions, ActionTimeseries ts, String producerId) {
        Double value = 0.;
        double counter = 0.;
        for (ProductionDTO production : productions) {
            if (ts.getTimestamps().contains(production.getStartTimestamp())) {
                for (ProductionProducerDTO productionProducer : production.getProducers()) {
                    if (productionProducer.getProducerId().equals(producerId)) {
                        value = value + productionProducer.getCurrentValue();
                        counter = counter + 1.;
                    }
                }
            }
        }
        return value / counter;
    }

    private Double getAveragePossibleProductionByProducerInTs(List<ProductionDTO> productions, ActionTimeseries ts, String producerId) {
        Double value = 0.;
        double counter = 0.;
        for (ProductionDTO production : productions) {
            if (ts.getTimestamps().contains(production.getStartTimestamp())) {
                for (ProductionProducerDTO productionProducer : production.getProducers()) {
                    if (productionProducer.getProducerId().equals(producerId)) {
                        value = value + productionProducer.getPossibleValue();
                        counter = counter + 1.;
                    }
                }
            }
        }
        return value / counter;
    }

    private ProductionDTO getProductionByTimestamp(List<ProductionDTO> productions, Long timestamp) {
        return productions.stream().filter((production) -> production.getStartTimestamp().equals(timestamp)).findFirst().orElse(null);
    }

    private LoadDTO getLoadByTimestamp(List<LoadDTO> loads, Long timestamp) {
        return loads.stream().filter((load) -> load.getStartTimestamp().equals(timestamp)).findFirst().orElse(null);
    }

    private boolean aboveShortageThreshold(VirtualPowerPlantDTO virtualPowerPlant, Double load, Double production) {
        return (load / production * 100) - 100 > virtualPowerPlant.getShortageThreshold();
    }

    private ActionTimeseries createActionTimeseries(Map.Entry<Long, Pair<Double, Double>> timestampLoadProduction, Double load, Double production, ProblemTypeEnum problemType, String actionRequestId, String virtualPowerPlantId) {
        ActionTimeseries pTs = new ActionTimeseries();
        pTs.setActionRequestId(actionRequestId);
        pTs.setVirtualPowerPlantId(virtualPowerPlantId);
        pTs.setProblemType(problemType);
        pTs.setTimestamps(new TreeSet<>());
        pTs.setActiontype(null);
        pTs.getTimestamps().add(timestampLoadProduction.getKey());
        if (pTs.getProblemType().equals(ProblemTypeEnum.OVERFLOW)) {
            pTs.setAverageGap(production - load);
        } else if (pTs.getProblemType().equals(ProblemTypeEnum.SHORTAGE)) {
            pTs.setAverageGap(load - production);
        }

        return pTs;
    }

    private boolean aboveOverflowThreshold(VirtualPowerPlantDTO virtualPowerPlant, Double load, Double production) {
        return (production / load * 100) - 100 > virtualPowerPlant.getOverflowThreshold();
    }

    
        /*
        List<String> vppIds = masterdataRestClient.getAllActiveVppIds();

        for (String vppId : vppIds) {
            List<LoadDTO> loads = loadRestClient.getAllLoadsByActionRequestId(vppId, timestamp);
            List<ProductionDTO> productions = productionRestClient.getAllProductionsByActionRequestId(vppId, timestamp);

            List<WindEnergyDTO> winds = this.getAllWinds(vppId);
            List<SolarEnergyDTO> solars = this.getAllSolars(vppId);
            List<WaterEnergyDTO> waters = this.getAllWaters(vppId);
            List<StorageDTO> storages = this.getAllStorages(vppId);

            //create affected timeseries in forecast
            boolean isShortage = false;
            boolean isOverflow = false;
            Map<Long, Pair<Double, Double>> timestampToLoadAndProduction = this.createComparisonMap(loads, productions);
            List<ActionTimeseries> actionTimeseries = new ArrayList<>();
            for (Map.Entry<Long, Pair<Double, Double>> timestampLoadProduction : timestampToLoadAndProduction.entrySet()) {
                Double load = timestampLoadProduction.getCurrentValue().getFirst() / 1000; //convert W into kW
                Double production = timestampLoadProduction.getCurrentValue().getSecond();

                if (!isShortage && load > production)  {
                    isOverflow = false;
                    isShortage = true;
                    ActionTimeseries pTs = new ActionTimeseries();
                    pTs.setTimestamps(new TreeSet<>());
                    pTs.setActiontype(null);
                    pTs.setActionRequestId(vppId);
                    pTs.setProblemType(ProblemTypeEnum.SHORTAGE);
                    pTs.getTimestamps().add(timestampLoadProduction.getKey());
                    pTs.setCumulativeGap(load - production);
                    actionTimeseries.add(pTs);
                } else if (!isOverflow && production > load) {
                    isShortage = false;
                    isOverflow = true;
                    ActionTimeseries pTs = new ActionTimeseries();
                    pTs.setTimestamps(new TreeSet<>());
                    pTs.setActiontype(null);
                    pTs.setActionRequestId(vppId);
                    pTs.setProblemType(ProblemTypeEnum.OVERFLOW);
                    pTs.getTimestamps().add(timestampLoadProduction.getKey());
                    pTs.setCumulativeGap(production - load);
                    actionTimeseries.add(pTs);
                } else if ((isShortage && load > production) ||
                        (isOverflow && production > load)) {
                    //Add to existing Timeseries
                    if (actionTimeseries.size() > 0) {
                        Double cumulativeGap = (load > production) ? load - production : (production > load) ? production - load : 0;
                        actionTimeseries.get(actionTimeseries.size() - 1).getTimestamps().add(timestampLoadProduction.getKey());
                        actionTimeseries.get(actionTimeseries.size() - 1).setCumulativeGap(
                                actionTimeseries.get(actionTimeseries.size() - 1).getCumulativeGap() + cumulativeGap
                        );
                    }
                } else {
                    actionTimeseries.get(actionTimeseries.size() - 1).setCumulativeGap(
                            actionTimeseries.get(actionTimeseries.size() - 1).getCumulativeGap() / actionTimeseries.get(actionTimeseries.size() - 1).getTimestamps().size()
                    );
                    isShortage = false;
                    isOverflow = false;
                }
            }

            //check for possible solutions
            for (ActionTimeseries ts : actionTimeseries) {
                if (ts.getProblemType().equals(ProblemTypeEnum.OVERFLOW)) {
                    ActionCatalogEntity actionAggregate = new ActionCatalogEntity();
                    actionAggregate.setActionVirtualPowerPlantId(
                            new ActionVirtualPowerPlantIdVO(vppId)
                    );
                    actionAggregate.setActionStorageId(new ActionStorageIdVO(actionStorage.getStorageId()));
                    actionAggregate.setActionProducerId(new ActionProducerIdVO(actionStorage.getStorageId()));
                    actionAggregate.setActionStartTimestamp(new ActionStartTimestampVO(ts.getTimestamps().first()));
                    actionAggregate.setActionEndTimestamp(new ActionEndTimestampVO(ts.getTimestamps().last()));
                    actionAggregate.setProblemType(new ActionTypeVO(ActionTypeEnum.INCREASE_STORAGE));
                    actionRepository.saveAction(actionAggregate);
                    //Manipulate Producers with worker task based on when and how long the producer loads a storage
                } else if (ts.getProblemType().equals(ProblemTypeEnum.SHORTAGE)) {

                } else {
                    //error
                }

            }

            //run best possible action, else create a warning

        }

    }

    private Double getAverageProductionInTimeseries(List<ProductionDTO> productions, ActionTimeseries ts) {
        Double result = 0.;
        for(ProductionDTO production : productions) {
            if(production.getStartTimestamp() >= ts.getTimestamps().first() ||
            production.getStartTimestamp() <= ts.getTimestamps().last()) {
                Double productionAverage = 0.;
                for(ProductionProducerDTO producer : production.getProducers()) {
                    productionAverage += producer.getCurrentValue();
                }
                productionAverage = productionAverage / production.getProducers().size();
                result += productionAverage;
            }
        }
        return result / ts.getTimestamps().size();
    }


    private List<StorageDTO> getStorageOverflowCandidates(List<StorageDTO> storages) {
        return storages.stream().filter((storageDTO -> storageDTO.getCapacity() > 0)).collect(Collectors.toList());
    }

    private List<WindEnergyDTO> getAllWinds(String vppId) {
        List<String> dppIds = this.masterdataRestClient.getAllDppsByVppId(vppId);
        List<String> householdIds = this.masterdataRestClient.getAllHouseholdsByVppId(vppId);

        List<WindEnergyDTO> winds = new ArrayList<>();

        for (String dppId : dppIds) {
            winds.addAll(masterdataRestClient.getAllWindsByDppId(dppId));
        }
        for (String householdId : householdIds) {
            winds.addAll(masterdataRestClient.getAllWindsByHouseholdId(householdId));
        }
        return winds;
    }

    private List<SolarEnergyDTO> getAllSolars(String vppId) {
        List<String> dppIds = this.masterdataRestClient.getAllDppsByVppId(vppId);
        List<String> householdIds = this.masterdataRestClient.getAllHouseholdsByVppId(vppId);

        List<SolarEnergyDTO> solars = new ArrayList<>();

        for (String dppId : dppIds) {
            solars.addAll(masterdataRestClient.getAllSolarsByDppId(dppId));
        }
        for (String householdId : householdIds) {
            solars.addAll(masterdataRestClient.getAllSolarsByHouseholdId(householdId));
        }
        return solars;
    }

    private List<WaterEnergyDTO> getAllWaters(String vppId) {
        List<String> dppIds = this.masterdataRestClient.getAllDppsByVppId(vppId);
        List<String> householdIds = this.masterdataRestClient.getAllHouseholdsByVppId(vppId);

        List<WaterEnergyDTO> waters = new ArrayList<>();

        for (String dppId : dppIds) {
            waters.addAll(masterdataRestClient.getAllWatersByDppId(dppId));
        }
        for (String householdId : householdIds) {
            waters.addAll(masterdataRestClient.getAllWatersByHouseholdId(householdId));
        }
        return waters;
    }

    private List<StorageDTO> getAllStorages(String vppId) {
        List<String> dppIds = this.masterdataRestClient.getAllDppsByVppId(vppId);
        List<String> householdIds = this.masterdataRestClient.getAllHouseholdsByVppId(vppId);

        List<StorageDTO> storages = new ArrayList<>();

        for (String dppId : dppIds) {
            storages.addAll(masterdataRestClient.getAllStoragesByDppId(dppId));
        }
        for (String householdId : householdIds) {
            storages.addAll(masterdataRestClient.getAllStoragesByHouseholdId(householdId));
        }
        return storages;
    }

    private Double getAvarageProductionById(String producerId, List<ProductionDTO> productions) {
        Double result = 0.;

        for (ProductionDTO dto : productions) {
            for (ProductionProducerDTO producer : dto.getProducers()) {
                if (producer.getProducerId().equals(producerId)) {
                    result = result + producer.getCurrentValue();
                }
            }
        }
        return (productions.size() > 0) ? result / productions.size() : 0;
    }

*/
}
