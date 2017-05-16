import okhttp3.ResponseBody;
import org.limeprotocol.Command;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface BlipService {
    @Headers({"Key: dGVzdGV3ZWJob29ramF2YTpwZG1GSmZqNnd4UFY4T0xDR3l6Qw==",
    "Content-Type: application/json"})
    @POST("messages")
    Call<ResponseBody> sendMessage(@Body Message message);

    @Headers("Key: dGVzdGV3ZWJob29ramF2YTpwZG1GSmZqNnd4UFY4T0xDR3l6Qw==")
    @POST("notifications")
    Call sendNotifications(@Body Notification notification);

    @Headers("Key: dGVzdGV3ZWJob29ramF2YTpwZG1GSmZqNnd4UFY4T0xDR3l6Qw==")
    @POST("commands")
    Call<Envelope> sendCommands(@Body Command command);
}