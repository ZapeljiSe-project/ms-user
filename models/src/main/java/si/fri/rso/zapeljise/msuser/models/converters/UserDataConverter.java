package si.fri.rso.zapeljise.msuser.models.converters;

import si.fri.rso.zapeljise.msuser.lib.UserData;
import si.fri.rso.zapeljise.msuser.models.entities.UserDataEntity;

public class UserDataConverter {
    public static UserData toDto(UserDataEntity entity) {
        UserData dto = new UserData();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());
        return dto;
    }

    public static UserDataEntity toEntity(UserData dto) {
        UserDataEntity entity = new UserDataEntity();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        return entity;
    }
}