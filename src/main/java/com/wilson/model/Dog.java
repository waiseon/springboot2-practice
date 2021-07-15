package com.wilson.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
//@ApiModel(description = "dog")
public class Dog {
//    @ApiModelProperty(value = "name",dataType = "java.lang.String")
    private String name;
//    @ApiModelProperty(value = "age",dataType = "java.lang.Integer")
    private Integer age;
//    @ApiModelProperty(value = "weight",dataType = "java.lang.Integer")
    private Integer weight;

    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                '}';
    }
}
