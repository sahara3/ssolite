package com.github.sahara3.ssolite.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SsoAccessToken {

	private String id;

	private String sessionId;

	private OffsetDateTime expired;

}
