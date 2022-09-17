package dev.fullstackcode.sb.rabbitmq.producer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fullstackcode.sb.rabbitmq.producer.model.Event;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping(value ="rabbitmq/event")
public class RabbitMQProducerController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private DirectExchange directExchange;


    @PostMapping
    public String  send(@RequestBody Event event) {

        if( event.getName().equalsIgnoreCase("Event A")) {

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("text/plain");
            Message message = new Message(event.getName().getBytes(), messageProperties);

            rabbitTemplate.send(directExchange.getName(), "event_message", message);


        } else if( event.getName().equalsIgnoreCase("Event B")) {

            ObjectMapper mapper = new ObjectMapper();

            String json = null;
            try {
                json = mapper.writeValueAsString(event);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            MessageProperties messageProperties = new MessageProperties();
            //Note that you must change the contentType to application/json
            messageProperties.setContentType("application/json");
            Message message1 = new Message(json.getBytes(), messageProperties);


            rabbitTemplate.convertAndSend(directExchange.getName(),"event_jsonMessage",  message1);



        }  else if (event.getName().equalsIgnoreCase("Event C")) {

           Resource resource=resourceLoader.getResource("classpath:images/A.png");

            byte[] body ;
            try {
                File file = resource.getFile();

                body = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("image/png");
            messageProperties.getHeaders().put("extension", "png");
            messageProperties.getHeaders().put("name", "A");
            Message message = new Message(body, messageProperties);

            rabbitTemplate.convertAndSend(directExchange.getName(),"event_image",  message);

        } else if (event.getName().equalsIgnoreCase("Event D")) {

            Resource resource=resourceLoader.getResource("classpath:docs/sample.docx");

            byte[] body = new byte[0];
            try {
                File file = resource.getFile();

                body = Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            messageProperties.getHeaders().put("extension", "docx");
            messageProperties.getHeaders().put("name", "sample");
            Message message = new Message(body, messageProperties);

           rabbitTemplate.convertAndSend(directExchange.getName(),"event_doc",  message);


        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"unknown event");
        }
        return "message sent successfully";
    }

}
