package de.uol.vpp.masterdata.domain.utils;

import de.uol.vpp.masterdata.domain.valueobjects.*;

public interface IPublishUtil {

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, DecentralizedPowerPlantIdVO dppBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, HouseholdIdVO householdBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, WindEnergyIdVO windEnergyBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, WaterEnergyIdVO waterEnergyBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, SolarEnergyIdVO solarEnergyBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, OtherEnergyIdVO otherEnergyBusinessKey) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO vppBusinessKey, StorageIdVO storageBusinessKey) throws PublishException;

}
