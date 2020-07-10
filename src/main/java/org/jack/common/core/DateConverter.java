package org.jack.common.core;

import java.text.ParseException;
import java.util.Date;

import org.jack.common.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class DateConverter implements Converter<String, Date> {
    private static final Logger logger=LoggerFactory.getLogger(DateConverter.class);

    @Override
    public Date convert(String source) {
        if (StringUtils.hasText(source)) {
            String format = DateUtils.DATE_FORMAT_DATE;
            if (source.length() == DateUtils.DATE_FORMAT_DATETIME.length()) {
                format = DateUtils.DATE_FORMAT_DATETIME;
            }
            try {
                return DateUtils.parseDate(source, format);
            } catch (ParseException e) {
                logger.error("convert失败 source:{}",source);
            }
        }
        return null;
    }
    
}