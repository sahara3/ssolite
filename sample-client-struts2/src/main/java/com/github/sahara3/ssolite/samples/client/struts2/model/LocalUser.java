package com.github.sahara3.ssolite.samples.client.struts2.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocalUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String password;

    public static LocalUser copy(@NonNull LocalUser from) {
        return new LocalUser(from.name, from.password);
    }
}
