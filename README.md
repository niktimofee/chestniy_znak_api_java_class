# CrptApi

Этот проект представляет собой реализацию класса `CrptApi` для работы с API Честного знака. Класс `CrptApi` является потокобезопасным и поддерживает ограничение на количество запросов к API в заданный промежуток времени.

## Объяснение кода:

**1. HTTP-клиент и JSON-сериализация:**

- <u>*OkHttpClient*</u> используется для выполнения HTTP-запросов.
- <u>*ObjectMapper*</u> из Jackson используется для сериализации объектов в JSON.

**2. Ограничение запросов:**

- Используем <u>*Semaphore*</u> для ограничения количества запросов. <u>*ScheduledExecutorService*</u> периодически освобождает семафор в соответствии с заданным интервалом времени.
- Метод <u>*resetSemaphorePeriodically*</u> сбрасывает семафор, чтобы разрешить новые запросы в новом временном интервале.

**3. Метод <u>*createDocument*</u>:**

- Этот метод отправляет POST-запрос с документом и подписью.
- Он блокируется, если лимит запросов превышен, и разблокируется, когда запрос выполнен.

**4. Внутренние классы:**

- Классы <u>*Document*</u>, <u>*Description*</u> и <u>*Product*</u> описывают структуру JSON-документа.


## Зависимости:

**Проект использует следующие библиотеки:**
- `OkHttp` для выполнения HTTP-запросов
- `Jackson` для сериализации объектов в JSON

### `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://www.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>crpt-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>CrptApi</name>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.squareup.okhttp3</groupId>
            <artifactId>okhttp</artifactId>
            <version>4.9.3</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.12.5</version>
        </dependency>
    </dependencies>
</project>
