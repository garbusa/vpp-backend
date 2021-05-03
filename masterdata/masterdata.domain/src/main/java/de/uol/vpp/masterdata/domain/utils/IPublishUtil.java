package de.uol.vpp.masterdata.domain.utils;

import de.uol.vpp.masterdata.domain.valueobjects.*;

public interface IPublishUtil {

    boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, DecentralizedPowerPlantIdVO decentralizedPowerPlantId) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, HouseholdIdVO householdId) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, WindEnergyIdVO windEnergyId) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, WaterEnergyIdVO waterEnergyId) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, SolarEnergyIdVO solarEnergyId) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, OtherEnergyIdVO otherEnergyId) throws PublishException;

    boolean isEditable(VirtualPowerPlantIdVO virtualPowerPlantId, StorageIdVO storageId) throws PublishException;

}
