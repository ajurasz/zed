package zed.boot.rpi.benchmark.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import zed.boot.rpi.benchmark.statistic.Details;
import zed.boot.rpi.benchmark.statistic.Statistic;

import java.util.List;

@RestController
@RequestMapping("statistic")
public class StatisticController {

    @Autowired
    private Statistic statistic;

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public Details details() {
        return statistic.details();
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Details> list() {
        return statistic.list();
    }
}
