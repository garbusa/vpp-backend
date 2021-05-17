package de.uol.vpp.action.service.services;

import de.uol.vpp.action.domain.aggregates.ActionRequestAggregate;
import de.uol.vpp.action.domain.entities.ProducerManipulationEntity;
import de.uol.vpp.action.domain.entities.StorageManipulationEntity;
import de.uol.vpp.action.domain.enums.StatusEnum;
import de.uol.vpp.action.domain.exceptions.ActionException;
import de.uol.vpp.action.domain.exceptions.ActionRepositoryException;
import de.uol.vpp.action.domain.exceptions.ActionServiceException;
import de.uol.vpp.action.domain.exceptions.ManipulationException;
import de.uol.vpp.action.domain.repositories.IActionRequestRepository;
import de.uol.vpp.action.domain.services.IActionRequestService;
import de.uol.vpp.action.domain.utils.MathUtils;
import de.uol.vpp.action.domain.valueobjects.*;
import de.uol.vpp.action.infrastructure.rest.MasterdataRestClient;
import de.uol.vpp.action.infrastructure.rest.dto.*;
import de.uol.vpp.action.infrastructure.rest.dto.abstracts.DtoHasProducersAndStorages;
import de.uol.vpp.action.infrastructure.rest.exceptions.MasterdataRestClientException;
import lombok.RequiredArgsConstructor;
import org.joda.time.Period;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ActionRequestServiceImpl implements IActionRequestService {

    private final IActionRequestRepository actionRequestRepository;
    private final MasterdataRestClient masterdataRestClient;

    @Override
    public List<ActionRequestAggregate> getAllActionRequestByVppId(String virtualPowerPlantId) throws ActionServiceException {
        try {
            return actionRequestRepository.getAllActionRequestsByVppId(new ActionRequestVirtualPowerPlantIdVO(virtualPowerPlantId));
        } catch (ActionRepositoryException | ActionException e) {
            throw new ActionServiceException(e.getMessage(), e);
        }
    }

    @Override
    public ActionRequestAggregate get(String actionRequestId) throws ActionServiceException {
        try {
            Optional<ActionRequestAggregate> actionRequest = actionRequestRepository.getActionRequest(
                    new ActionRequestIdVO(actionRequestId)
            );
            if (actionRequest.isPresent()) {
                return actionRequest.get();
            } else {
                throw new ActionServiceException(
                        String.format("ActionRequest mit der ID %s konnte nicht gefunden werden", actionRequestId)
                );
            }
        } catch (ActionRepositoryException | ActionException e) {
            throw new ActionServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void save(ActionRequestAggregate domainEntity) throws ActionServiceException {
        try {
            if (actionRequestRepository.getActionRequest(domainEntity.getActionRequestId()).isPresent()) {
                throw new ActionServiceException(String.format("ActionRequest %s existiert bereits", domainEntity.getActionRequestId().getValue()));
            }
            if (masterdataRestClient.isHealthy()) {
                VirtualPowerPlantDTO vppDTO = masterdataRestClient.getVirtualPowerPlantById(domainEntity.getVirtualPowerPlantId().getValue());
                if (vppDTO != null) {
                    List<DecentralizedPowerPlantDTO> dppDTOs = masterdataRestClient.getAllDppsByVppId(vppDTO.getVirtualPowerPlantId());
                    List<HouseholdDTO> householdDTOs = masterdataRestClient.getAllHouseholdsByVppId(vppDTO.getVirtualPowerPlantId());
                    this.checkExistence(domainEntity, dppDTOs, householdDTOs); //Prüfe Existenz der betroffenen Anlagen

                    //Hole alle Anlagen des VKs
                    List<WaterEnergyDTO> waters = this.getAllWaters(dppDTOs, householdDTOs);
                    List<WindEnergyDTO> winds = this.getAllWinds(dppDTOs, householdDTOs);
                    List<SolarEnergyDTO> solars = this.getAllSolars(dppDTOs, householdDTOs);
                    List<OtherEnergyDTO> others = this.getAllOthers(dppDTOs, householdDTOs);
                    List<StorageDTO> storages = this.getAllStorages(dppDTOs, householdDTOs);

                    this.sortManipulations(domainEntity); //Sortiere die Manipulation nach Zeiträumen

                    //Prüfe Überlappungen von Zeiträumen von Manipulationen für gleich betroffenen Anlagen
                    this.checkProducerOverlap(domainEntity);
                    this.checkStorageOverlap(domainEntity);

                    //Prüfe, ob die Anlagen genügend Kapazitäten für die Manipulationen besitzen
                    this.checkProducerCapacity(domainEntity, waters, winds, solars, others);
                    this.checkStorageCapacity(domainEntity, storages);
                } else {
                    throw new ActionServiceException(String.format("ActionRequest %s konnte nicht erstellt werden, da VK %s nicht gefunden wurde",
                            domainEntity.getActionRequestId().getValue(), domainEntity.getVirtualPowerPlantId().getValue()));
                }
            } else {
                throw new ActionServiceException(String.format("ActionRequest %s konnte nicht erstellt werden, da Daten-Service nicht erreichbar ist",
                        domainEntity.getActionRequestId().getValue()));
            }
            domainEntity.setStatus(new ActionRequestStatusVO(StatusEnum.STARTED)); //Setze Maßnahmenabfrage Status auf "gestartet"
            actionRequestRepository.saveActionRequest(domainEntity, true); //Rufe Speicherfunktionalität der Infrastrukturschicht
        } catch (ActionRepositoryException | ActionException | MasterdataRestClientException | ManipulationException e) {
            throw new ActionServiceException(e.getMessage(), e);
        }
    }

    private void checkExistence(ActionRequestAggregate domainEntity, List<DecentralizedPowerPlantDTO> dppDTOs, List<HouseholdDTO> householdDTOs) throws ActionServiceException {
        for (ProducerManipulationEntity producerManipulationEntity : domainEntity.getProducerManipulations()) {
            boolean found = false;
            for (DecentralizedPowerPlantDTO dpp : dppDTOs) {
                found = findProducer(producerManipulationEntity, found, dpp);
            }

            for (HouseholdDTO household : householdDTOs) {
                found = findProducer(producerManipulationEntity, found, household);
            }

            if (!found) {
                throw new ActionServiceException(
                        String.format("ActionRequest konnte nicht erstellt, werden da der Produzent %s nicht gefunden werden konnte", producerManipulationEntity.getProducerId().getValue())
                );
            }
        }

        for (StorageManipulationEntity storageManipulationEntity : domainEntity.getStorageManipulations()) {
            boolean found = false;
            for (DecentralizedPowerPlantDTO dpp : dppDTOs) {
                found = findStorage(storageManipulationEntity, found, dpp);
            }

            for (HouseholdDTO household : householdDTOs) {
                found = findStorage(storageManipulationEntity, found, household);
            }

            if (!found) {
                throw new ActionServiceException(
                        String.format("ActionRequest konnte nicht erstellt, werden da der Speicher %s nicht gefunden werden konnte", storageManipulationEntity.getStorageId().getValue())
                );
            }
        }
    }

    private List<WaterEnergyDTO> getAllWaters(List<DecentralizedPowerPlantDTO> dppDTOs, List<HouseholdDTO> householdDTOs) {
        List<WaterEnergyDTO> waters = new ArrayList<>();
        dppDTOs.forEach(dpp -> waters.addAll(dpp.getWaters()));
        householdDTOs.forEach(household -> waters.addAll(household.getWaters()));
        return waters;
    }

    private List<WindEnergyDTO> getAllWinds(List<DecentralizedPowerPlantDTO> dppDTOs, List<HouseholdDTO> householdDTOs) {
        List<WindEnergyDTO> winds = new ArrayList<>();
        dppDTOs.forEach(dpp -> winds.addAll(dpp.getWinds()));
        householdDTOs.forEach(household -> winds.addAll(household.getWinds()));
        return winds;
    }

    private List<SolarEnergyDTO> getAllSolars(List<DecentralizedPowerPlantDTO> dppDTOs, List<HouseholdDTO> householdDTOs) {
        List<SolarEnergyDTO> solars = new ArrayList<>();
        dppDTOs.forEach(dpp -> solars.addAll(dpp.getSolars()));
        householdDTOs.forEach(household -> solars.addAll(household.getSolars()));
        return solars;
    }

    private List<OtherEnergyDTO> getAllOthers(List<DecentralizedPowerPlantDTO> dppDTOs, List<HouseholdDTO> householdDTOs) {
        List<OtherEnergyDTO> others = new ArrayList<>();
        dppDTOs.forEach(dpp -> others.addAll(dpp.getOthers()));
        householdDTOs.forEach(household -> others.addAll(household.getOthers()));
        return others;
    }

    private List<StorageDTO> getAllStorages(List<DecentralizedPowerPlantDTO> dppDTOs, List<HouseholdDTO> householdDTOs) {
        List<StorageDTO> storages = new ArrayList<>();
        dppDTOs.forEach(dpp -> storages.addAll(dpp.getStorages()));
        householdDTOs.forEach(household -> storages.addAll(household.getStorages()));
        return storages;
    }

    private void sortManipulations(ActionRequestAggregate domainEntity) {
        domainEntity.getProducerManipulations().sort(
                Comparator.comparing(o -> o.getStartEndTimestamp().getStart()));
        domainEntity.getStorageManipulations().sort(
                Comparator.comparing(o -> o.getStartEndTimestamp().getStart()));
        domainEntity.getGridManipulations().sort(
                Comparator.comparing(o -> o.getStartEndTimestamp().getStart()));
    }

    private void checkProducerOverlap(ActionRequestAggregate domainEntity) throws ActionServiceException {
        Map<String, List<ProducerManipulationEntity>> producerIdToManipulationEntity = new HashMap<>();
        domainEntity.getProducerManipulations().forEach((entity) ->
                producerIdToManipulationEntity.put(entity.getProducerId().getValue(), new ArrayList<>()));

        for (String producerId : producerIdToManipulationEntity.keySet()) {
            for (ProducerManipulationEntity producerManipulation : domainEntity.getProducerManipulations()) {
                if (producerId.equals(producerManipulation.getProducerId().getValue())) {
                    producerIdToManipulationEntity.get(producerId).add(producerManipulation);
                }
            }
        }

        for (String producerId : producerIdToManipulationEntity.keySet()) {
            List<ProducerManipulationEntity> entities = producerIdToManipulationEntity.get(producerId);
            if (entities.size() > 1) {
                for (int i = 1; i < entities.size(); i++) {
                    if (this.isOverlapping(entities.get(i - 1).getStartEndTimestamp().getStart(),
                            entities.get(i - 1).getStartEndTimestamp().getEnd(),
                            entities.get(i).getStartEndTimestamp().getStart(),
                            entities.get(i).getStartEndTimestamp().getEnd()
                    )) {
                        throw new ActionServiceException(
                                String.format("ActionRequest %s konnte nicht erstellt werden, " +
                                        "da gleiche Erzeugungsanlagen nicht innerhalb einer Periode " +
                                        "manipuliert werden dürfen", domainEntity.getActionRequestId().getValue())
                        );
                    }
                }
            }
        }
    }

    private void checkStorageOverlap(ActionRequestAggregate domainEntity) throws ActionServiceException {
        Map<String, List<StorageManipulationEntity>> storageIdToManipulationEntity = new HashMap<>();
        domainEntity.getStorageManipulations().forEach((entity) ->
                storageIdToManipulationEntity.put(entity.getStorageId().getValue(), new ArrayList<>()));

        for (String storageId : storageIdToManipulationEntity.keySet()) {
            for (StorageManipulationEntity storageManipulation : domainEntity.getStorageManipulations()) {
                if (storageId.equals(storageManipulation.getStorageId().getValue())) {
                    storageIdToManipulationEntity.get(storageId).add(storageManipulation);
                }
            }
        }

        for (String storageId : storageIdToManipulationEntity.keySet()) {
            List<StorageManipulationEntity> entities = storageIdToManipulationEntity.get(storageId);
            if (entities.size() > 1) {
                for (int i = 1; i < entities.size(); i++) {
                    if (this.isOverlapping(entities.get(i - 1).getStartEndTimestamp().getStart(),
                            entities.get(i - 1).getStartEndTimestamp().getEnd(),
                            entities.get(i).getStartEndTimestamp().getStart(),
                            entities.get(i).getStartEndTimestamp().getEnd()
                    )) {
                        throw new ActionServiceException(
                                String.format("ActionRequest %s konnte nicht erstellt werden, " +
                                        "da gleiche Speicheranlagen nicht innerhalb einer Periode " +
                                        "manipuliert werden dürfen", domainEntity.getActionRequestId().getValue())
                        );
                    }
                }
            }
        }
    }

    private void checkProducerCapacity(ActionRequestAggregate domainEntity, List<WaterEnergyDTO> waters, List<WindEnergyDTO> winds, List<SolarEnergyDTO> solars, List<OtherEnergyDTO> others) throws ActionServiceException {
        for (ProducerManipulationEntity manipulation : domainEntity.getProducerManipulations()) {
            for (WaterEnergyDTO water : waters) {
                if (water.getWaterEnergyId().equals(manipulation.getProducerId().getValue())) {
                    this.checkProducerPossibility(domainEntity, manipulation, water.getCapacity());
                }
            }
            for (WindEnergyDTO wind : winds) {
                if (wind.getWindEnergyId().equals(manipulation.getProducerId().getValue())) {
                    this.checkProducerPossibility(domainEntity, manipulation, wind.getCapacity());
                }
            }
            for (SolarEnergyDTO solar : solars) {
                if (solar.getSolarEnergyId().equals(manipulation.getProducerId().getValue())) {
                    this.checkProducerPossibility(domainEntity, manipulation, solar.getCapacity());
                }
            }
            for (OtherEnergyDTO other : others) {
                if (other.getOtherEnergyId().equals(manipulation.getProducerId().getValue())) {
                    this.checkProducerPossibility(domainEntity, manipulation, other.getCapacity());
                }
            }
        }
    }

    private void checkStorageCapacity(ActionRequestAggregate domainEntity, List<StorageDTO> storages) throws ManipulationException, ActionServiceException {
        for (StorageManipulationEntity manipulation : domainEntity.getStorageManipulations()) {
            for (StorageDTO storage : storages) {
                if (storage.getStorageId().equals(manipulation.getStorageId().getValue())) {
                    manipulation.setRatedPower(new StorageManipulationRatedPowerVO(storage.getRatedPower()));
                    this.checkStoragePossibility(domainEntity, manipulation, storage);
                }
            }
        }
    }

    private boolean findProducer(ProducerManipulationEntity producerManipulationEntity, boolean found, DtoHasProducersAndStorages dpp) {
        for (WaterEnergyDTO waterEnergyDTO : dpp.getWaters()) {
            if (waterEnergyDTO.getWaterEnergyId().equals(producerManipulationEntity.getProducerId().getValue())) {
                found = true;
            }
        }
        for (WindEnergyDTO windEnergyDTO : dpp.getWinds()) {
            if (windEnergyDTO.getWindEnergyId().equals(producerManipulationEntity.getProducerId().getValue())) {
                found = true;
            }
        }
        for (SolarEnergyDTO solarEnergyDTO : dpp.getSolars()) {
            if (solarEnergyDTO.getSolarEnergyId().equals(producerManipulationEntity.getProducerId().getValue())) {
                found = true;
            }
        }
        for (OtherEnergyDTO otherEnergyDTO : dpp.getOthers()) {
            if (otherEnergyDTO.getOtherEnergyId().equals(producerManipulationEntity.getProducerId().getValue())) {
                found = true;
            }
        }
        return found;
    }

    private boolean findStorage(StorageManipulationEntity storageManipulationEntity, boolean found, DtoHasProducersAndStorages dppOrHousehold) {
        for (StorageDTO storageDTO : dppOrHousehold.getStorages()) {
            if (storageDTO.getStorageId().equals(storageManipulationEntity.getStorageId().getValue())) {
                found = true;
            }
        }
        return found;
    }

    private boolean isOverlapping(ZonedDateTime start1, ZonedDateTime end1, ZonedDateTime start2, ZonedDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private void checkProducerPossibility(ActionRequestAggregate domainEntity, ProducerManipulationEntity manipulation, Double capacity) throws ActionServiceException {
        switch (manipulation.getType().getValue()) {
            case PRODUCER_UP:
                if (100. - capacity < manipulation.getCapacity().getValue()) {
                    throw new ActionServiceException(
                            String.format("ActionRequest %s konnte nicht erstellt werden, da Erzeugungsanlage %s nicht genügend Kapazitäten hat",
                                    domainEntity.getActionRequestId().getValue(), manipulation.getProducerId().getValue())
                    );
                }
                break;
            case PRODUCER_DOWN:
                if (capacity < manipulation.getCapacity().getValue()) {
                    throw new ActionServiceException(
                            String.format("ActionRequest %s konnte nicht erstellt werden, da Erzeugungsanlage %s nicht genügend Kapazitäten hat",
                                    domainEntity.getActionRequestId().getValue(), manipulation.getProducerId().getValue())
                    );
                }
                break;
            default:
                throw new ActionServiceException(
                        String.format("ActionRequest %s konnte nicht erstellt werden, da ProducerManipulationType nicht existiert",
                                domainEntity.getActionRequestId().getValue())
                );
        }
    }

    private void checkStoragePossibility(ActionRequestAggregate domainEntity, StorageManipulationEntity manipulation, StorageDTO storage) throws ActionServiceException, ManipulationException {
        switch (manipulation.getType().getValue()) {
            case STORAGE_LOAD:
                //Wenn Speicher bereits voll ist
                if (storage.getCapacity() == 100.) {
                    throw new ActionServiceException(
                            String.format("ActionRequest %s konnte nicht erstellt werden, da Speicher %s bereits voll ist",
                                    domainEntity.getActionRequestId().getValue(), manipulation.getStorageId().getValue())
                    );
                } else {
                    Period p = this.getManipulationPeriod(manipulation); //Manipulationszeitraum
                    //Berechnung der Periode von Sekunden in Stunden
                    double manipulationPeriodInHour = MathUtils.round(Integer.valueOf(p.toStandardSeconds().getSeconds()).doubleValue() / 3600.);
                    //Berechnung der möglichen Ladezeit von Speicher C-Rate in Stunden
                    double capacityHours = (1 / storage.getLoadTimeHour()) / 100. * (100. - storage.getCapacity());
                    //Wenn Manipulationszeitraum größer als mögliche Ladezeit, dann ist der Speicher voll
                    if (manipulationPeriodInHour >= capacityHours) {
                        manipulation.setHours(new StorageManipulationHoursVO(capacityHours));
                        storage.setCapacity(100.);
                    } else {
                        //Ansonsten wird die neue Kapazität für weitere Speichermanipulationen berechnet
                        double factor = MathUtils.round(manipulationPeriodInHour / capacityHours);
                        double additionCapacity = MathUtils.round(capacityHours / storage.getLoadTimeHour() * 100. * factor);
                        double newCapacity = storage.getCapacity() + additionCapacity;
                        if (newCapacity >= 100.) {
                            storage.setCapacity(100.);
                        } else {
                            storage.setCapacity(newCapacity);
                        }
                        manipulation.setHours(new StorageManipulationHoursVO(manipulationPeriodInHour));
                    }
                }
                break;
            case STORAGE_UNLOAD:
                if (storage.getCapacity() == 0.) {
                    throw new ActionServiceException(
                            String.format("ActionRequest %s konnte nicht erstellt werden, da Speicher %s leer ist",
                                    domainEntity.getActionRequestId().getValue(), manipulation.getStorageId().getValue())
                    );
                } else {
                    Period p = this.getManipulationPeriod(manipulation);
                    double manipulationPeriodInHour = (Integer.valueOf(p.toStandardSeconds().getSeconds()).doubleValue() / 3600.);
                    double capacityHours = (1 / storage.getLoadTimeHour()) / 100. * (100. - ((100. - storage.getCapacity())));
                    if (manipulationPeriodInHour >= capacityHours) {
                        manipulation.setHours(new StorageManipulationHoursVO(manipulationPeriodInHour));
                        storage.setCapacity(0.);
                    } else {
                        double factor = manipulationPeriodInHour / capacityHours;
                        double substractionCapacity = (Math.round(1000.
                                * ((capacityHours / storage.getLoadTimeHour() * 100.) * factor))
                                / 1000.);
                        double newCapacity = storage.getCapacity() - substractionCapacity;
                        if (newCapacity > 0.) {
                            storage.setCapacity(newCapacity);
                        } else {
                            storage.setCapacity(0.);
                        }
                        manipulation.setHours(new StorageManipulationHoursVO(capacityHours));
                    }
                }
                break;
            default:
                throw new ActionServiceException(
                        String.format("ActionRequest %s konnte nicht erstellt werden, da Speicher %s nicht genügend Kapazitäten hat",
                                domainEntity.getActionRequestId().getValue(), manipulation.getStorageId().getValue())
                );
        }
    }

    private Period getManipulationPeriod(StorageManipulationEntity manipulation) {
        ZonedDateTime startTs = manipulation.getStartEndTimestamp().getStart();
        ZonedDateTime endTs = manipulation.getStartEndTimestamp().getEnd();
        return new Period(startTs.toEpochSecond() * 1000, endTs.toEpochSecond() * 1000);
    }
}
