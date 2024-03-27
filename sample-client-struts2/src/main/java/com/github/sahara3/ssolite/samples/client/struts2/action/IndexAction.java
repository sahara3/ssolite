package com.github.sahara3.ssolite.samples.client.struts2.action;

import java.io.Serial;

import com.opensymphony.xwork2.ActionSupport;

public class IndexAction extends ActionSupport {

    @Serial
    private static final long serialVersionUID = 1L;

    public String show() {
        return SUCCESS;
    }
}
