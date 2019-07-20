package cn.rongcapital.caas.test.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.util.StringUtils;

public class DateUtil {
	public static Date now(String pattern) {
		SimpleDateFormat fo = new SimpleDateFormat();
		Date date = new java.util.Date(System.currentTimeMillis());
		if (StringUtils.isEmpty(pattern)) {
			pattern = "yyyy-MM-dd HH:mm";
		}
		fo.applyPattern(pattern);

		try {
			date = fo.parse(DateFormatUtils.format(date, pattern));
		} catch (Exception e) {
		}

		return date;
	}

	public static void main(String[] args) {
		System.out.println(now(null));
	}
}
