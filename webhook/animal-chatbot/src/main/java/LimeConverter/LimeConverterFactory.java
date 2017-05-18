package LimeConverter;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.limeprotocol.Command;
import org.limeprotocol.Envelope;
import org.limeprotocol.Message;
import org.limeprotocol.Notification;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class LimeConverterFactory extends Converter.Factory {
    /**
     * Create an instance using a default {@link JacksonEnvelopeSerializer} instance for lime conversion.
     *  Encoding to JSON and decoding from JSON using UTF-8 chartset.
     */
    public static LimeConverterFactory create() {
        return create(new JacksonEnvelopeSerializer());
    }

    @SuppressWarnings("ConstantConditions")
    public static LimeConverterFactory create(JacksonEnvelopeSerializer gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new LimeConverterFactory(gson);
    }

    private final JacksonEnvelopeSerializer jacksonEnvelopeSerializer;

    private LimeConverterFactory(JacksonEnvelopeSerializer jacksonEnvelopeSerializer) {
        this.jacksonEnvelopeSerializer = jacksonEnvelopeSerializer;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if(!(type == Envelope.class) &&
                !(type == Message.class) &&
                !(type == Notification.class) &&
                !(type == Command.class)) return null;
        return new LimeResponseBodyConverter<>(jacksonEnvelopeSerializer);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {

        if(!(type == Envelope.class) &&
                !(type == Message.class) &&
                !(type == Notification.class) &&
                !(type == Command.class)) return null;
        return new LimeRequestBodyConverter<>(jacksonEnvelopeSerializer);
    }
}
