package eu.xenit.alfresco.webscripts.client.spring;

import lombok.Data;

@Data
public class WebscriptHttpErrorResponse {

    private Status status;
    private String message;
    private String exception;
    private String[] callstack;
    private String server;
    private String time;

    @Data
    class Status {
        private int code;
        private String name;
        private String description;
    }



}
