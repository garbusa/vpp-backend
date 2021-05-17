package de.uol.vpp.action.infrastructure.algorithm;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.entities.ActionCatalogEntity;
import de.uol.vpp.action.domain.entities.ActionEntity;
import de.uol.vpp.action.domain.enums.ActionTypeEnum;
import de.uol.vpp.action.domain.enums.ProblemTypeEnum;
import de.uol.vpp.action.domain.enums.StatusEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.repositories.IActionRequestRepository;
import de.uol.vpp.action.domain.valueobjects.*;
import de.uol.vpp.action.infrastructure.rest.LoadRestClient;
import de.uol.vpp.action.infrastructure.rest.MasterdataRestClient;
import de.uol.vpp.action.infrastructure.rest.ProductionRestClient;
import de.uol.vpp.action.infrastructure.rest.dto.*;
import de.uol.vpp.action.infrastructure.rest.exceptions.LoadRestClientException;
import de.uol.vpp.action.infrastructure.rest.exceptions.MasterdataRestClientException;
import de.uol.vpp.action.infrastructure.rest.exceptions.ProductionRestClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class ActionCatalogInfrastructureService {

    private final IActionRequestRepository actionRequestRepository;
    private final MasterdataRestClient masterdataRestClient;
    private final LoadRestClient loadRestClient;
    private final ProductionRestClient productionRestClient;

    public void createActionCatalogs(String actionRequestId) throws ActionException, ActionRepositoryException, MasterdataRestClientException, LoadRestClientException, ProductionRestClientException {
        //Hole alle Loads und Productions by actionRequestId
        Optional<ActionRequestAggregate> actionRequest = actionRequestRepository.getActionRequest(new ActionRequestIdVO(actionRequestId));

        if (actionRequest.isPresent()) {
            VirtualPowerPlantDTO virtualPowerPlant = masterdataRestClient
                    .getVirtualPowerPlantById(actionRequest.get().getVirtualPowerPlantId().getValue());
            if (virtualPowerPlant != null) {
                List<LoadDTO> loads = loadRestClient.getAllLoadsByActionRequestId(actionRequestId);
                List<ProductionDTO> productions = productionRestClient.getAllProductionsByActionRequestId(actionRequestId);

                Map<Long, Pair<Double, Double>> comparisonMap = this.createComparisonMap(loads, productions);
                List<DifferenceTimeseries> differenceTimeseriesList =
                        createDifferenceTimeseriesList(actionRequest.get(), virtualPowerPlant, comparisonMap);

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
                for (DifferenceTimeseries differenceTimeseries : differenceTimeseriesList) {
                    ActionCatalogEntity catalog = new ActionCatalogEntity();
                    catalog.setActionRequestIdVO(new ActionRequestIdVO(actionRequestId));
                    catalog.setStartTimestamp(new ActionCatalogStartTimestampVO(differenceTimeseries.getTimestamps().first()));
                    catalog.setEndTimestamp(new ActionCatalogEndTimestampVO(differenceTimeseries.getTimestamps().last()));
                    catalog.setProblemType(new ActionCatalogProblemTypeVO(differenceTimeseries.getProblemType()));
                    catalog.setCumulativeGap(new ActionCatalogCumulativeGapVO(differenceTimeseries.getAverageGap()));
                    List<ActionEntity> actionsInCatalog = new ArrayList<>();
                    if (differenceTimeseries.getProblemType().equals(ProblemTypeEnum.OVERFLOW)) {
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
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, differenceTimeseries, water.getWaterEnergyId());
                                actionsInCatalog.add(
                                        this.createAction(catalog, water.getWaterEnergyId(),
                                                false, averageProduction, 0.,
                                                water.getCapacity(), ActionTypeEnum.PRODUCER_DOWN)
                                );
                            }
                        }

                        for (WindEnergyDTO wind : winds) {
                            if (wind.getCapacity() > 0.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, differenceTimeseries, wind.getWindEnergyId());
                                actionsInCatalog.add(
                                        this.createAction(catalog, wind.getWindEnergyId(),
                                                false, averageProduction, 0.,
                                                wind.getCapacity(), ActionTypeEnum.PRODUCER_DOWN)
                                );
                            }
                        }

                        for (SolarEnergyDTO solar : solars) {
                            if (solar.getCapacity() > 0.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, differenceTimeseries, solar.getSolarEnergyId());
                                actionsInCatalog.add(
                                        this.createAction(catalog, solar.getSolarEnergyId(),
                                                false, averageProduction, 0.,
                                                solar.getCapacity(), ActionTypeEnum.PRODUCER_DOWN)
                                );
                            }
                        }

                        for (OtherEnergyDTO other : others) {
                            if (other.getCapacity() > 0.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, differenceTimeseries, other.getOtherEnergyId());
                                actionsInCatalog.add(
                                        this.createAction(catalog, other.getOtherEnergyId(),
                                                false, averageProduction, 0.,
                                                other.getCapacity(), ActionTypeEnum.PRODUCER_DOWN)
                                );
                            }
                        }

                    } else if (differenceTimeseries.getProblemType().equals(ProblemTypeEnum.SHORTAGE)) {
                        //Welche Erzeugungsanlagen können hochgefahren werden?
                        for (WaterEnergyDTO water : waters) {
                            if (water.getCapacity() < 100.) {
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, differenceTimeseries, water.getWaterEnergyId());
                                Double averagePossibleProduction = this.getAveragePossibleProductionByProducerInTs(productions, differenceTimeseries, water.getWaterEnergyId());
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
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, differenceTimeseries, wind.getWindEnergyId());
                                Double averagePossibleProduction = this.getAveragePossibleProductionByProducerInTs(productions, differenceTimeseries, wind.getWindEnergyId());
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
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, differenceTimeseries, solar.getSolarEnergyId());
                                Double averagePossibleProduction = this.getAveragePossibleProductionByProducerInTs(productions, differenceTimeseries, solar.getSolarEnergyId());
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
                                Double averageProduction = this.getAverageCurrentProductionByProducerInTs(productions, differenceTimeseries, other.getOtherEnergyId());
                                Double averagePossibleProduction = this.getAveragePossibleProductionByProducerInTs(productions, differenceTimeseries, other.getOtherEnergyId());
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
                log.info("creating action catalogs status. persisting...");
                actionRequest.get().setCatalogs(catalogs);
                actionRequest.get().setStatus(new ActionRequestStatusVO(StatusEnum.FINISHED));
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

    private List<DifferenceTimeseries> createDifferenceTimeseriesList(ActionRequestAggregate actionRequest, VirtualPowerPlantDTO virtualPowerPlant, Map<Long, Pair<Double, Double>> comparisonMap) {
        List<DifferenceTimeseries> differenceTimeseries = new ArrayList<>();
        boolean isShortage = false;
        boolean isOverflow = false;
        int timestampCounter = 0;
        for (Map.Entry<Long, Pair<Double, Double>> timestampLoadProduction : comparisonMap.entrySet()) {
            Double load = this.getCurrentLoad(timestampLoadProduction);
            Double production = this.getCurrentProduction(timestampLoadProduction);

            boolean isStartingShortage = !isShortage && !isOverflow && this.aboveShortageThreshold(actionRequest, load, production);
            boolean isStartingOverflow = !isShortage && !isOverflow && this.aboveOverflowThreshold(actionRequest, load, production);
            boolean isRunningDifferenceTimeseries = (isShortage && aboveShortageThreshold(actionRequest, load, production)) ||
                    (isOverflow && aboveOverflowThreshold(actionRequest, load, production));
            boolean isEndingDifferenceTimeseries = (!isRunningDifferenceTimeseries && (isShortage || isOverflow)) ||
                    timestampCounter == comparisonMap.size() - 1;
            boolean hasLessOrEqualOneElement = differenceTimeseries.size() > 0 && differenceTimeseries.get(differenceTimeseries.size() - 1).getAverageGap() < 0.;
            boolean hasMoreOrEqualOneElement = differenceTimeseries.size() > 0 && differenceTimeseries.get(differenceTimeseries.size() - 1).getAverageGap() > 0.;

            if (isStartingShortage) {
                isShortage = true;
                DifferenceTimeseries dt = this.createDifferenceTimeseries(
                        timestampLoadProduction, load, production, ProblemTypeEnum.SHORTAGE, actionRequest.getActionRequestId().getValue(),
                        virtualPowerPlant.getVirtualPowerPlantId());
                differenceTimeseries.add(dt);
            } else if (isStartingOverflow) {
                isOverflow = true;
                DifferenceTimeseries dt = this.createDifferenceTimeseries(timestampLoadProduction, load, production,
                        ProblemTypeEnum.OVERFLOW, actionRequest.getActionRequestId().getValue(), virtualPowerPlant.getVirtualPowerPlantId());
                differenceTimeseries.add(dt);
            } else if (isRunningDifferenceTimeseries) {
                if (differenceTimeseries.size() > 0) {
                    DifferenceTimeseries last = differenceTimeseries.get(differenceTimeseries.size() - 1);
                    this.addTimestampGap(timestampLoadProduction, load, production, last);
                }
            }

            if (isEndingDifferenceTimeseries) {
                if (hasLessOrEqualOneElement) {
                    this.removeLastDifferenceTimeseries(differenceTimeseries);
                } else if (hasMoreOrEqualOneElement) {
                    DifferenceTimeseries last = differenceTimeseries.get(differenceTimeseries.size() - 1);
                    Double oldGap = last.getAverageGap();
                    Double length = Integer.valueOf(last.getTimestamps().size()).doubleValue();
                    if (oldGap > 0. && length > 0. && oldGap / length > 0.) {
                        Double newGap = oldGap / length;
                        last.setAverageGap(newGap);
                    } else {
                        differenceTimeseries.remove(last);
                    }
                }
                isShortage = false;
                isOverflow = false;
            }
            timestampCounter++;
        }
        return differenceTimeseries;
    }

    private Double getAverageCurrentProductionByProducerInTs(List<ProductionDTO> productions, DifferenceTimeseries ts, String producerId) {
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

    private Double getAveragePossibleProductionByProducerInTs(List<ProductionDTO> productions, DifferenceTimeseries ts, String producerId) {
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

    private Double getCurrentLoad(Map.Entry<Long, Pair<Double, Double>> timestampLoadProduction) {
        return ((timestampLoadProduction.getValue().getFirst() / 1000) < 0.) ? 0. : timestampLoadProduction.getValue().getFirst() / 1000.; //convert W into kW
    }

    private Double getCurrentProduction(Map.Entry<Long, Pair<Double, Double>> timestampLoadProduction) {
        return (timestampLoadProduction.getValue().getSecond() < 0.) ? 0. : timestampLoadProduction.getValue().getSecond();
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

    private boolean aboveShortageThreshold(ActionRequestAggregate actionRequest, Double load, Double production) {
        return load > (production + actionRequest.getShortageThreshold().getValue());
    }

    private boolean aboveOverflowThreshold(ActionRequestAggregate actionRequest, Double load, Double production) {
        return production > (load + actionRequest.getOverflowThreshold().getValue());
    }

    private ProductionDTO getProductionByTimestamp(List<ProductionDTO> productions, Long timestamp) {
        return productions.stream().filter((production) -> production.getStartTimestamp().equals(timestamp)).findFirst().orElse(null);
    }

    private LoadDTO getLoadByTimestamp(List<LoadDTO> loads, Long timestamp) {
        return loads.stream().filter((load) -> load.getStartTimestamp().equals(timestamp)).findFirst().orElse(null);
    }

    private DifferenceTimeseries createDifferenceTimeseries(Map.Entry<Long, Pair<Double, Double>> timestampLoadProduction, Double load, Double production, ProblemTypeEnum problemType, String actionRequestId, String virtualPowerPlantId) {
        DifferenceTimeseries pTs = new DifferenceTimeseries();
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

    private void addTimestampGap(Map.Entry<Long, Pair<Double, Double>> timestampLoadProduction, Double load, Double production, DifferenceTimeseries last) {
        Double gap = 0.;
        if (last.getProblemType().equals(ProblemTypeEnum.OVERFLOW)) {
            gap = production - load;
        } else if (last.getProblemType().equals(ProblemTypeEnum.SHORTAGE)) {
            gap = load - production;
        }
        last.getTimestamps().add(timestampLoadProduction.getKey());
        //Addiere neue Energielücke aus aktuellen Zeitstempel
        Double newGap = last.getAverageGap() + gap;
        last.setAverageGap(newGap);
    }

    private void removeLastDifferenceTimeseries(List<DifferenceTimeseries> differenceTimeseries) {
        DifferenceTimeseries last = differenceTimeseries.get(differenceTimeseries.size() - 1);
        differenceTimeseries.remove(last);
    }

    public void actionFailed(String actionRequestId) {
        try {
            Optional<ActionRequestAggregate> actionRequestOptional = actionRequestRepository.getActionRequest(new ActionRequestIdVO(actionRequestId));
            if (actionRequestOptional.isPresent()) {
                ActionRequestAggregate actionRequest = actionRequestOptional.get();
                actionRequest.setStatus(new ActionRequestStatusVO(StatusEnum.FAILED));
                actionRequestRepository.saveActionRequest(actionRequest, false);
            } else {
                log.error("Set action status failed. ActionRequest does not exist?");
            }

        } catch (ActionRepositoryException | ActionException e) {
            log.error(e);
        }
    }

}
