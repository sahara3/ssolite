package com.github.sahara3.ssolite.samples.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SuppressWarnings({ "javadoc", "static-method" })
public class WebMvcController {

	@GetMapping(path = "/")
	public String index() {
		return "index";
	}
}
