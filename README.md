## Задача для Java кандидата

### Описание
Необходимо используя Spring boot разработать приложение, которое умеет в фоне выполнять продолжительные задачи.
Пользователь может инициировать такую задачу и получить в ответ ее ид.
Далее пользователь может попытаться получить результат по заданному ид.
Две одинаковых задачи не могут быть запущенны одновременно, при этом может быть несколько инстансов приложения.
Приложение должно уметь восстанавливать работу если было перезапущенно с того же места где последний этап задачи был выполнен.
Нужны тесты на основные сценарии

Для примера задачи можно использовать следуюющее:
```
class TestJob {
    Stream<Integer> run(int min, int max, int count) {
        AtomicInteger counter = new AtomicInteger(0);
        return Stream
            .generate(() -> {
            counter.incrementAndGet();
            int random = (int)(Math.random() * max + min);
            return random;
            })
            .takeWhile(n -> counter.get() < count);
            }
}
```

Стартовать приложение можно инстанс в профиле inst1, inst2. Тесты запускаются в профиле test.
 
### Стек:
- JDK 17
- Spring Framework (Web, Batch, JPA)
- Spring Boot
- Maven
- Docker
- Postgres
- JPA/Hibernate
- JDBC
- Lombok
- Flyway
- JUnit
- Testcontainers
- LockRegistry

### Сборка и запуск приложения

Запустите postgres контейнер файлом docker-compose в папке \docker

Команда Windows для сборки: 
- mvn clean package

Команда Windows для запуска jar файла:
- java -jar app.jar --spring.profiles.active=inst1
- java -jar app.jar --spring.profiles.active=inst2

примеры для вызова API endpoint:
- curl --location --request POST 'localhost:8080/task?min=1&max=100&count=10'
- curl --location 'localhost:8080/task?requestId=3803bcfa-8e89-4317-9357-04b7246bb387'

порт 8080 для профиля inst1, 8081 для inst2

Также вы можете открыть коллекцию Postman из папки /postman чтобы проверить работу приложения.

Тестировать также можно посредством JMeter, Postman.

### Дата разработки
22.02.2025
