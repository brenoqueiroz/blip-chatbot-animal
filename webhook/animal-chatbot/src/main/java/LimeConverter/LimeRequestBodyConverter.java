package LimeConverter;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.ByteString;
import org.limeprotocol.Envelope;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import retrofit2.Converter;

import java.io.IOException;

final class LimeRequestBodyConverter<T> implements Converter<T, RequestBody> {
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    private final JacksonEnvelopeSerializer jacksonEnvelopeSerializer;

    LimeRequestBodyConverter(JacksonEnvelopeSerializer jacksonEnvelopeSerializer) {
        this.jacksonEnvelopeSerializer = jacksonEnvelopeSerializer;
    }

    @Override
    public RequestBody convert(T value) throws IOException {

        return RequestBody.create(MEDIA_TYPE, ByteString.encodeUtf8(jacksonEnvelopeSerializer.serialize((Envelope) value)));
    }
}