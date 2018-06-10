package ru.slisenko.activemq.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    private final static String ACTIVEMQ_URL = "tcp://localhost:61616";
    private final static String ACTIVEMQ_QUEUE_NAME = "testQueue";

    public static void main(String[] args) {
        try (JmsProducer producer = new JmsProducer(ACTIVEMQ_URL);
             JmsConsumer consumer = new JmsConsumer(ACTIVEMQ_URL, ACTIVEMQ_QUEUE_NAME)) {
            BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (!(line = rdr.readLine()).equalsIgnoreCase("stop")) {
                producer.send(line);
            }
            System.out.println("Bye!");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
