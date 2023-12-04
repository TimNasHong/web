package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
//import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Override
    public TurnoverReportVO turnoverReport(LocalDate begin, LocalDate end) {
        List<LocalDate> localDates=new ArrayList<>();
        localDates.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            localDates.add(begin);
        }

        List<Double> turnover=new ArrayList<>();
        for (LocalDate localDate : localDates) {
            LocalDateTime beginTime= LocalDateTime.of(localDate,LocalTime.MIN);
            LocalDateTime endTime=LocalDateTime.of(localDate,LocalTime.MAX);
            Map map=new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status",5);
            Double amount=orderMapper.getTurnoverByDate(map);
            amount=amount==null?0.0:amount;
            turnover.add(amount);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(localDates, ","))
                .turnoverList(StringUtils.join(turnover,","))
        .build();
    }
}
