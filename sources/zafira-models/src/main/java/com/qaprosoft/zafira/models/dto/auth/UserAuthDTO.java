package com.qaprosoft.zafira.models.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserAuthDTO {

    private String username;

    private String password;

    private List<Long> groupIds = new ArrayList<>();
}
