package tw.com.ispan.eeit.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.io.WKTReader;

@Converter(autoApply = false) //建議先不要autoApply，有多個Point欄位時容易衝突
public class PointToGeographyConverter implements AttributeConverter<Point, String> {

    @Override
    public String convertToDatabaseColumn(Point point) {
        if (point == null) return null;
        return new WKTWriter().write(point);
    }

    @Override
    public Point convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return (Point) new WKTReader().read(dbData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}