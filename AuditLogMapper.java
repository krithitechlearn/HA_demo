package com.shoreexcursions.app.audit.mapper;

import com.shoreexcursions.app.audit.AuditLogEntity;
import com.shoreexcursions.app.audit.dto.AuditLogDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for AuditLogEntity.
 *
 * @see US-045
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AuditLogMapper {

    AuditLogDto toDto(AuditLogEntity entity);
}
