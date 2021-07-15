package com.wilson.controller;

import com.wilson.model.Dog;
//import io.swagger.annotations.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//@Api(value = "test API")
@Controller
public class HelloController {

    @RequestMapping(method = RequestMethod.GET, value = "/hello")
    public String hello(){
        return "Hello, Spring Boot 2!";
    }

//    @ApiOperation(value = "test dog API", notes = "test dog API notes")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "Success / Acknowledge request"),
//            @ApiResponse(code = 400, message = "Bad request"),
//            @ApiResponse(code = 500, message = "Server error"),
//    })

    @RequestMapping(method = RequestMethod.POST, value = "/test-swagger")
    public Dog helloSwagger(/*@ApiParam(required = true)*/ @RequestBody Dog dog){
        System.out.println(dog);
        return dog;
    }
}
