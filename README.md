# rabbitmq-spring-boot
This spring boot complete project is about communication between producer and consumer, using four kinds of exchange ( topic exchange, direct exchange, fanout exchange and header exchange ).

Scenario: The system has lots of students and few instructors. Each student can subscribe to multiple instructors. Instructors can send
notice to his students or a particular student or even all students.

Design: I've tried to use four kinds of exchange here.
Direct Exchange: I used direct exchange while sending notices to individual students.
Topic Exchange: While sending a notice to his subscribers.
Fanout Exchange: To send notices to all students.
Headers Exchange: While sending notifications to students having the same headers key value, I named it sending notices to secure channel.

Steps to run this:
1. First Download and Install Erlang to run RabbitMq.
2. Download and install the RabbitMq.
3. Clone or Download this project.
4. Start RabbitMq service.
5. Run Producer and Consumer app.
6. Test the app using Postman. I've added the Postman collection.
