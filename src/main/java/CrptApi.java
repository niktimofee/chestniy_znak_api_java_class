import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CrptApi {
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService scheduler;
    private final Semaphore semaphore;
    private final int requestLimit;
    private final Duration timeUnit;

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.semaphore = new Semaphore(requestLimit);
        this.requestLimit = requestLimit;
        this.timeUnit = Duration.ofMillis(timeUnit.toMillis(1));
        resetSemaphorePeriodically();
    }

    private void resetSemaphorePeriodically() {
        scheduler.scheduleAtFixedRate(() -> {
            semaphore.drainPermits();
            semaphore.release(requestLimit);
        }, 0, timeUnit.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void createDocument(Document document, String signature) throws InterruptedException, IOException {
        semaphore.acquire();
        try {
            RequestBody body = RequestBody.create(
                    objectMapper.writeValueAsString(document),
                    MediaType.get("application/json")
            );
            Request request = new Request.Builder()
                    .url("https://ismp.crpt.ru/api/v3/lk/documents/create")
                    .post(body)
                    .addHeader("Signature", signature)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                // Обработка ответа по необходимости
            }
        } finally {
            semaphore.release();
        }
    }

    public static class Document {
        // Поля, соответствующие структуре JSON
        public Description description;
        public String doc_id;
        public String doc_status;
        public String doc_type;
        public boolean importRequest;
        public String owner_inn;
        public String participant_inn;
        public String producer_inn;
        public String production_date;
        public String production_type;
        public Product[] products;
        public String reg_date;
        public String reg_number;

        public static class Description {
            public String participantInn;
        }

        public static class Product {
            public String certificate_document;
            public String certificate_document_date;
            public String certificate_document_number;
            public String owner_inn;
            public String producer_inn;
            public String production_date;
            public String tnved_code;
            public String uit_code;
            public String uitu_code;
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        CrptApi api = new CrptApi(TimeUnit.MINUTES, 10);
        Document document = new Document();
        // Заполнение объекта document данными
        api.createDocument(document, "your-signature");
    }
}