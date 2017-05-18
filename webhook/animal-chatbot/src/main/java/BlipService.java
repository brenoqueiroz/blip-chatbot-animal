import okhttp3.ResponseBody;
import org.limeprotocol.Command;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface BlipService {
    @Headers({"Authorization: Key dGVzdGV3ZWJob29ramF2YTpwZG1GSmZqNnd4UFY4T0xDR3l6Qw==",
            "Content-Type: application/json"})
    @POST("messages")
    Call<ResponseBody> sendMessage(@Body Message message);

    @Headers({"Authorization: Key dGVzdGV3ZWJob29ramF2YTpwZG1GSmZqNnd4UFY4T0xDR3l6Qw==",
            "Content-Type: application/json"})
    @POST("notifications")
    Call<ResponseBody> sendNotifications(@Body Notification notification);

    @Headers({"Authorization: Key dGVzdGV3ZWJob29ramF2YTpwZG1GSmZqNnd4UFY4T0xDR3l6Qw==",
            "Content-Type: application/json"})
    @POST("commands")
    Call<ResponseBody> sendCommands(@Body Command command);
}