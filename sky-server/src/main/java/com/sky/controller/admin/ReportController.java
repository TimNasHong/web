package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/admin/report")
public class ReportController {

@Autowired
private ReportService reportService;
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverReportVOResult(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                           @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        TurnoverReportVO turnoverReportVO=reportService.turnoverReport(begin,end);
         return Result.success(turnoverReportVO);
    }
}
