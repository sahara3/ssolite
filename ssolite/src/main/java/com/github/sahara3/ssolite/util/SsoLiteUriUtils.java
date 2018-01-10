package com.github.sahara3.ssolite.util;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.NonNull;

public class SsoLiteUriUtils {

	public static URI getDomainUri(@NonNull URI uri) throws URISyntaxException {
		return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null);
	}
}
