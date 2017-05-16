import okhttp3.ResponseBody;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import spark.Response;
import spark.ResponseTransformer;

import javax.servlet.http.HttpServletResponse;

import java.util.UUID;

import static spark.Spark.*;

public class Main {
    private static JacksonEnvelopeSerializer target;

    public static void main(String[] args) {

        target = new JacksonEnvelopeSerializer();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://msging.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BlipService service = retrofit.create(BlipService.class);

        get("/hello", (req, res) -> "Hello World");

        post("/messages", (request, response) -> {

            String body = request.body();

            Message message = (Message) target.deserialize(body);

            message.setId(UUID.randomUUID().toString());
            message.setTo(message.getFrom());
            message.setFrom(null);

            try {
                Call<ResponseBody> r = service.sendMessage(message);
                r.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        System.out.println(response.body());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        System.out.println(t);
                    }
                });
            }catch (Exception e){
                System.out.println(e);
            }
            response.status(HttpServletResponse.SC_OK);
            return response;
        });

        post("/notifications", (request, response) -> {

            String body = request.body();

            Notification notification = (Notification) target.deserialize(body);

            response.status(HttpServletResponse.SC_OK);
            return response;
        });
    }
}