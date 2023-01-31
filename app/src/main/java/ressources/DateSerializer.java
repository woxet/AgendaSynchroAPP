package ressources;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

public class DateSerializer implements Converter<Date> {
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void serialize(Date object, ObjectWriter writer, Context ctx) throws Exception {
        writer.writeString(format.format(object));
    }

    @Override
    public Date deserialize(ObjectReader reader, Context ctx) throws Exception {
        try {
            return format.parse(reader.valueAsString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}

