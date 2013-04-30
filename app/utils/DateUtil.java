package utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtil {
	public static DateTimeFormatter fmtDatumTid = DateTimeFormat
			.forPattern("yyyy-MM-dd HH.mm");
	public static DateTimeFormatter fmtDatum = DateTimeFormat
			.forPattern("yyyy-MM-dd");

	public static DateTime createDateFromString(String datum, boolean time) {
		if (time) {
			return fmtDatumTid.parseDateTime(datum);
		} else {
			return fmtDatum.parseDateTime(datum);
		}
	}
	
	public static String getDateAsString(DateTime datum, boolean time) {
		if (datum != null) {
			if (time) {
				return fmtDatumTid.print(datum);
			} else {
				return fmtDatum.print(datum);
			}
		}
		return null;
	}

}
