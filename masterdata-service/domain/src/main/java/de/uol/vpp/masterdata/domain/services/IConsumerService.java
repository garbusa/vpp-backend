package de.uol.vpp.masterdata.domain.services;

import de.uol.vpp.masterdata.domain.entities.ConsumerEntity;

import java.util.List;

public interface IConsumerService {

    List<ConsumerEntity> getAllByHouseholdId(String householdBusinessKey) throws ConsumerServiceException;

    ConsumerEntity get(String businessKey) throws ConsumerServiceException;

    void save(ConsumerEntity domainEntity, String householdBusinessKey) throws ConsumerServiceException;

    void delete(String businessKey, String vppBusinessKey) throws ConsumerServiceException;

    void updateStatus(String businessKey, boolean isRunning, String vppBusinessKey) throws ConsumerServiceException;

    void update(String businessKey, ConsumerEntity toDomain, String vppBusinessKey) throws ConsumerServiceException;

}
