package dev.fullstackcode.sb.rabbitmq.producer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {


    @Bean
    Queue queueImages() {
        return new Queue("queue.images", false);
    }

    @Bean
    Queue queueDocs() {
        return new Queue("queue.docs", false);
    }

    @Bean
    Queue queueMessages() {
        return new Queue("queue.messages", false);
    }

    @Bean
    Queue queueJsonMessages() {
        return new Queue("queue.jsonmessages", false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("exchange.direct");
    }

    @Bean
    Binding bindingImageQueue(Queue queueImages, DirectExchange exchange) {
        return BindingBuilder.bind(queueImages).to(exchange).with("event_image");
    }

    @Bean
    Binding bindingDocQueue(Queue queueDocs, DirectExchange exchange) {
        return BindingBuilder.bind(queueDocs).to(exchange).with("event_doc");
    }

    @Bean
    Binding bindingMessageQueue(Queue queueMessages, DirectExchange exchange) {
        return BindingBuilder.bind(queueMessages).to(exchange).with("event_message");
    }

    @Bean
    Binding bindingJsonMessageQueue(Queue queueJsonMessages, DirectExchange exchange) {
        return BindingBuilder.bind(queueJsonMessages).to(exchange).with("event_jsonMessage");
    }

    @Bean
    ApplicationRunner runner(ConnectionFactory cf) {
        return args -> cf.createConnection().close();
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
    /*
     @Autowired
	private RabbitTemplate rabbitTemplate;


	@Test
	public void testSendMessage() throws Exception {
		//1 create message
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.getHeaders().put("desc", "Information description..");
		messageProperties.getHeaders().put("type", "Custom message type..");
		Message message = new Message("Hello RabbitMQ".getBytes(), messageProperties);

		rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				System.err.println("------Add additional settings---------");
				message.getMessageProperties().getHeaders().put("desc", "Information description of additional modifications");
				message.getMessageProperties().getHeaders().put("attr", "Additional new properties");
				return message;
			}
		});
	}

	@Test
	public void testSendMessage2() throws Exception {
		//1 create message
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		Message message = new Message("mq Message 1234".getBytes(), messageProperties);

		rabbitTemplate.send("topic001", "spring.abc", message);

		rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send!");
		rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send!");
	}

	@Test
	public void testSendMessage4Text() throws Exception {
		//1 create message
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		Message message = new Message("mq Message 1234".getBytes(), messageProperties);

		rabbitTemplate.send("topic001", "spring.abc", message);
		rabbitTemplate.send("topic002", "rabbit.abc", message);
	}


	@Test
	public void testSendJsonMessage() throws Exception {

		Order order = new Order();
		order.setId("001");
		order.setName("Message order");
		order.setContent("Description information");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json);

		MessageProperties messageProperties = new MessageProperties();
		//Note that you must change the contentType to application/json
		messageProperties.setContentType("application/json");
		Message message = new Message(json.getBytes(), messageProperties);

		rabbitTemplate.send("topic001", "spring.order", message);
	}

	@Test
	public void testSendJavaMessage() throws Exception {

		Order order = new Order();
		order.setId("001");
		order.setName("Order message");
		order.setContent("Order description");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json);

		MessageProperties messageProperties = new MessageProperties();
		//Note that you must change the contentType to application/json
		messageProperties.setContentType("application/json");
		messageProperties.getHeaders().put("__TypeId__", "com.bfxy.spring.entity.Order");
		Message message = new Message(json.getBytes(), messageProperties);

		rabbitTemplate.send("topic001", "spring.order", message);
	}

	@Test
	public void testSendMappingMessage() throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		Order order = new Order();
		order.setId("001");
		order.setName("Order message");
		order.setContent("Order description");

		String json1 = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json1);

		MessageProperties messageProperties1 = new MessageProperties();
		//Note that you must change the contentType to application/json
		messageProperties1.setContentType("application/json");
		messageProperties1.getHeaders().put("__TypeId__", "order");
		Message message1 = new Message(json1.getBytes(), messageProperties1);
		rabbitTemplate.send("topic001", "spring.order", message1);

		Packaged pack = new Packaged();
		pack.setId("002");
		pack.setName("Package message");
		pack.setDescription("Package description");

		String json2 = mapper.writeValueAsString(pack);
		System.err.println("pack 4 json: " + json2);

		MessageProperties messageProperties2 = new MessageProperties();
		//Note that you must change the contentType to application/json
		messageProperties2.setContentType("application/json");
		messageProperties2.getHeaders().put("__TypeId__", "packaged");
		Message message2 = new Message(json2.getBytes(), messageProperties2);
		rabbitTemplate.send("topic001", "spring.pack", message2);
	}

	@Test
	public void testSendExtConverterMessage() throws Exception {
//			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "picture.png"));
//			MessageProperties messageProperties = new MessageProperties();
//			messageProperties.setContentType("image/png");
//			messageProperties.getHeaders().put("extName", "png");
//			Message message = new Message(body, messageProperties);
//			rabbitTemplate.send("", "image_queue", message);

			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "mysql.pdf"));
			MessageProperties messageProperties = new MessageProperties();
			messageProperties.setContentType("application/pdf");
			Message message = new Message(body, messageProperties);
			rabbitTemplate.send("", "pdf_queue", message);
	}
     */

}
