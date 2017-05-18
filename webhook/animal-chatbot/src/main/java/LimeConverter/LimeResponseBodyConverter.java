package LimeConverter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.Reader;

import okhttp3.ResponseBody;
import org.limeprotocol.serialization.JacksonEnvelopeSerializer;
import retrofit2.Converter;

final class LimeResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final JacksonEnvelopeSerializer jacksonEnvelopeSerializer;

    LimeResponseBodyConverter(JacksonEnvelopeSerializer jacksonEnvelopeSerializer) {
        this.jacksonEnvelopeSerializer = jacksonEnvelopeSerializer;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        Reader r = value.charStream();
        JsonReader jsonReader = null;//gson.newJsonReader(value.charStream());
        try {
            return null;
            //return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}