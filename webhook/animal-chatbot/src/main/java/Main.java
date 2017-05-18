import LimeConverter.LimeConverterFactory;
import okhttp3.ResponseBody;
import org.limeprotocol.Message;
import org.limeprotocol.Node;
import org.limeprotocol.Notification;
import org.limeprotocol.messaging.contents.PlainText;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

import javax.servlet.http.HttpServletResponse;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {
    private static JacksonEnvelopeSerializer target;

    public static void main(String[] args) {

        target = new JacksonEnvelopeSerializer();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://msging.net/")
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(LimeConverterFactory.create())
                .build();

        BlipService blipService = retrofit.create(BlipService.class);

        get("/hello", (req, res) -> "Hello World");

        post("/messages", (request, response) -> {

            String body = request.body();
            Message message = (Message) target.deserialize(body);

            Node from = message.getFrom();
            message.setFrom(message.getTo());
            message.setTo(from);
            message.setContent(new PlainText("Hello from Webhook Java"));

            Call<ResponseBody> r = blipService.sendMessage(message);
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

            response.status(HttpServletResponse.SC_OK);
            return response;
        });

        post("/notifications", (request, response) -> {

            String body = request.body();
            Notification notification = (Notification) target.deserialize(body);

            System.out.println("Received notification: Event( " + notification.getEvent() + " )" + "Reason( " + notification.getReason() + " )");
            response.status(HttpServletResponse.SC_OK);
            return response;
        });
    }
}