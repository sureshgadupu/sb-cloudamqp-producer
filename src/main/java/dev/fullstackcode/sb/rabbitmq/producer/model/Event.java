package dev.fullstackcode.sb.rabbitmq.producer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Event implements Serializable {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;

}
