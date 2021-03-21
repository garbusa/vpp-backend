package de.uol.vpp.masterdata.domain.utils;

import de.uol.vpp.masterdata.domain.valueobjects.*;

public interface IPublishUtil {

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, DecentralizedPowerPlantIdVO dppBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, HouseholdIdVO householdBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, ProducerIdVO producerBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, StorageIdVO storageBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, ConsumerIdVO consumerBusinessKey) throws PublishException;
}
